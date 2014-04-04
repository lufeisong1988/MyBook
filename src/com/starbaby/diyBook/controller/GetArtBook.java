package com.starbaby.diyBook.controller;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio.Playlists;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.helper.DrawRect;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.main.ZuoShuActivity;
import com.starbaby.diyBook.net.AsyncHttpGet;
import com.starbaby.diyBook.net.DefaultThreadPool;
import com.starbaby.diyBook.net.RequestResultCallback;
import com.starbaby.diyBook.utils.JavaBeanZuoShu;
import com.starbaby.diyBook.utils.StoreSrc;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.commentDialogUtils;
import com.starbaby.diyBook.view.CurlView;
import com.starbaby.diyBook.view.ShowZuoShuTips;

@SuppressLint("ResourceAsColor")
public class GetArtBook {
	Context mContext;
	String endName = ".cach";
	JavaBeanZuoShu mJavaBeanZuoShu;
	ArrayList<String> newBookList;
	ArrayList<Integer> playIdList;
	ArrayList<String> musicList;
	ShowZuoShuTips mShowZuoShuTips;
	PageProvider mPageProvider;
	CurlView mCurlView;
	String tpl_id;
	PopupWindow pop;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				mJavaBeanZuoShu.playId = playIdList;
				mJavaBeanZuoShu.playList = newBookList;
				mShowZuoShuTips.dismissTips();
				pop.dismiss();
				mPageProvider.setBToTransparent(true,newBookList.get(0));
				mCurlView.setCurrentIndex(0);
				Intent intent = new Intent(mContext,ZuoShuActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("tpl_id", tpl_id);
				bundle.putInt("width", StoreSrc.getImage_width());
				bundle.putInt("height", StoreSrc.getImage_height());
				bundle.putInt("count", StoreSrc.getImage_count());
				bundle.putSerializable("javaBean", mJavaBeanZuoShu);
				intent.putExtras(bundle);
				((Activity) mContext).startActivityForResult(intent,1);
				((Activity) mContext).overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
				break;
			case 2:
				Log.i("读取图片失败","读取图片失败");
				mShowZuoShuTips.dismissTips();
				Toast.makeText(mContext, "做书模式启动失败", Toast.LENGTH_LONG).show();
				break;
			case 3:
				showWait();
//				Toast.makeText(mContext, "该模板暂不支持做书，敬请期待或先选择其它模板!", Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	public GetArtBook(Context mContext,JavaBeanZuoShu mJavaBeanZuoShu,ShowZuoShuTips mShowZuoShuTips,PageProvider mPageProvider,CurlView mCurlView,PopupWindow pop){
		this.mContext = mContext;
		this.mJavaBeanZuoShu = mJavaBeanZuoShu;
		this.mShowZuoShuTips = mShowZuoShuTips;
		this.mPageProvider = mPageProvider;
		this.mCurlView = mCurlView;
		this.pop = pop;
	}
	public  void getBookList(String url,final String tpl_id){//下载所有信息
		this.tpl_id = tpl_id;
		AsyncHttpGet get  = new AsyncHttpGet(null, url + tpl_id , null, new RequestResultCallback() {
			
			@Override
			public void onSuccess(final Object o) {
						ArrayList<String> imageList = new ArrayList<String>();//当前书本所有图片的url集合
						ArrayList<String> playImgList = new ArrayList<String>();//需要修改的图片的URL集合
						playIdList = new ArrayList<Integer>();//需要修改图片的位置ID
						newBookList = new ArrayList<String>();//完成后的新书的所有图片的url集合
						musicList = new ArrayList<String>();//音乐集合
						String result = (String)o;
						Log.i("resutl",result);
						if(result != null){
							try {
								JSONObject object = new JSONObject(result);
								JSONArray imgList = object.getJSONArray("imagelist");
								JSONObject playList = object.getJSONObject("playinfo");
								if(playList == null || playList.equals("")){
									Message msg = new Message();
									msg.what = 3;
									mHandler.sendMessage(msg);
									return;
								}
								if(Integer.parseInt(object.getString("audio_available")) == 1){//如果为1.则代表有音乐存在
									JSONArray musList = object.getJSONArray("audiolist");//music列表
									for(int j = 0; j < musList.length();j++){
										String musicUrl = musList.getString(j);
//										GetMusFromHttp.downMusic(musicUrl);
										musicList.add(musicUrl);
									}
									mJavaBeanZuoShu.musicList = musicList;
								}
								for(int i = 0;i < imgList.length();i++){
									String imgUrl = imgList.getString(i);
									imageList.add(imgUrl);
								}
								String playId = playList.getString("pageno_str");
								String playImg = playList.getString("images_str");
								String Id[] = playId.split(",");
								for(int m = 0;m < Id.length;m++){
									playIdList.add(Integer.parseInt(Id[m]));
								}
								String Img[] = playImg.split(",");
								for(int n = 0;n < Img.length;n++){
									playImgList.add(Img[n]);
								}
								newBookList = imageList;
								for(int j = 0;j < Id.length;j++){
									newBookList.remove((int)playIdList.get(j) - 1);
									newBookList.add(playIdList.get(j) - 1, playImgList.get(j));
								}
								for(int k = 0;k < newBookList.size();k++){
//									GetBitFromHttp.getBitmap(tpl_id,newBookList.get(k).toString());
								}
								/*
								 * 添加做书图片（png）url和id到数据库
								 */
								Utils.mDBPlayinfoHelper.delete(Integer.parseInt(tpl_id));
								for(int m = 0;m < newBookList.size();m++){
									Utils.mDBPlayinfoHelper.addBitUrl(Integer.parseInt(tpl_id),newBookList.get(m));
								}
								for(int n = 0;n < playIdList.size();n++){
									Utils.mDBPlayinfoHelper.addBitId(Integer.parseInt(tpl_id),playIdList.get(n));
								}
								saveCache(playImgList);
								Message msg = new Message();
								msg.what = 1;
								mHandler.sendMessage(msg);
							} catch (JSONException e) {
								e.printStackTrace();
								Log.i("object", "null");
								Message msg = new Message();
								msg.what = 3;
								mHandler.sendMessage(msg);
								return;
							}
						}
					}
			
			@Override
			public void onFail(Exception e) {
				//下载做图资源失败
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessage(msg);
				Log.i("error Exception", e.toString());
			}
		});
		DefaultThreadPool.getInstance().execute(get);
	}
	public  void getBookList2(String url,final String tpl_id){//只下载做图的图片(其他图片 和 音乐都从原来多文件夹里复制过去)
		if(new File(Utils.zuoshuCache + tpl_id).exists()){
			Log.i("getData","local");
			ArrayList<String> bitUrl = Utils.mDBPlayinfoHelper.getBitUrl(Integer.parseInt(tpl_id));//做书模板的图片url
			ArrayList<Integer> bitId = Utils.mDBPlayinfoHelper.getBitId(Integer.parseInt(tpl_id));//做书下标id
			ArrayList<String> musicUrl = new ArrayList<String>();//音乐列表
			if(bitUrl.size() != 0 && bitId.size() != 0){//文件 与 数据库信息都存在
				File path = new File(Utils.path);
				if(!path.exists()){
					path.mkdirs();
				}
				/*
				 * copy图片
				 */
				for(int i = 0;i < bitUrl.size();i++){
					String NoChangePicOldPath = Utils.basePath1 + tpl_id + "/" + Utils.imgPathName + "/" + NamePic.mp3UrlToFileName(bitUrl.get(i).toString() + endName);
					if(new File(NoChangePicOldPath).exists()){
						String NoChangePicNewPath = Utils.path +  NamePic.mp3UrlToFileName(bitUrl.get(i).toString());
						copyFile(NoChangePicOldPath, NoChangePicNewPath);
					}
					String ChangePicOldPath = Utils.zuoshuCache + tpl_id + "/" + NamePic.mp3UrlToFileName(bitUrl.get(i).toString());
					if(new File(ChangePicOldPath).exists()){
						String ChangePicNewPath = Utils.path +  NamePic.mp3UrlToFileName(bitUrl.get(i).toString());
						copyFile(ChangePicOldPath, ChangePicNewPath);
					}
				}
				/*
				 * copy音乐
				 */
				Cursor cursor = Utils.mDBHelper.getMP3(tpl_id);
				if(cursor.getCount() > 0){
					for(int j = 0 ; j< cursor.getCount();j++){
						musicUrl.add(cursor.getString(cursor.getColumnIndex("MP3")));
						cursor.moveToNext();
					}
				}
				cursor.close();
				for(int m = 0;m < musicUrl.size();m++){
					String MusciOldPath = Utils.basePath1 + tpl_id + "/" + Utils.audPathName + "/" + NamePic.mp3UrlToFileName(musicUrl.get(m).toString());
					String MusicNewPath = Utils.path + NamePic.mp3UrlToFileName(musicUrl.get(m).toString());
					File strFile = new File(MusciOldPath);
					if(strFile.exists()){
						copyFile(MusciOldPath,MusicNewPath);
					}
				}
				playIdList = bitId;
				newBookList = bitUrl;
				mJavaBeanZuoShu.musicList = musicUrl;
				
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
				Log.i("local","1");
				return;
			}
		}
		Log.i("local","2");
		this.tpl_id = tpl_id;
		AsyncHttpGet get  = new AsyncHttpGet(null, url + tpl_id , null, new RequestResultCallback() {
			
			@Override
			public void onSuccess(final Object o) {
				Log.i("getData","net");
						ArrayList<String> musicList = new ArrayList<String>();//如果书本带音乐（音乐url集合）;
						ArrayList<String> imageList = new ArrayList<String>();//当前书本所有图片的url集合
						ArrayList<String> playImgList = new ArrayList<String>();//需要修改的图片的URL集合
						playIdList = new ArrayList<Integer>();//需要修改图片的位置ID
						newBookList = new ArrayList<String>();//完成后的新书的所有图片的url集合
						String result = (String)o;
						Log.i("result",result);
						if(result != null){
							try {
								JSONObject object = new JSONObject(result);
								JSONArray imgList = object.getJSONArray("imagelist");//模板img列表
								JSONObject playList = object.getJSONObject("playinfo");//做书信息
								if(Integer.parseInt(object.getString("audio_available")) == 1){//如果为1.则代表有音乐存在
									JSONArray musList = object.getJSONArray("audiolist");//music列表
									for(int j = 0; j < musList.length();j++){
										String musicUrl = musList.getString(j);
										musicList.add(musicUrl);
									}
									mJavaBeanZuoShu.musicList = musicList;
								}
								for(int i = 0;i < imgList.length();i++){
									String imgUrl = imgList.getString(i);
									imageList.add(imgUrl);
								}
								String playId = playList.getString("pageno_str");//做书id标签
								String playImg = playList.getString("images_str");//做书img列表
								String Id[] = playId.split(",");
								for(int m = 0;m < Id.length;m++){
									playIdList.add(Integer.parseInt(Id[m]));
								}
								String Img[] = playImg.split(",");
								for(int n = 0;n < Img.length;n++){
									playImgList.add(Img[n]);
								}
								newBookList = imageList;
								for(int j = 0;j < Id.length;j++){
									newBookList.remove((int)playIdList.get(j) - 1);
									newBookList.add(playIdList.get(j) - 1, playImgList.get(j));
								}
								for(int k = 0;k < playImgList.size();k++){
//									GetBitFromHttp.getBitmap(tpl_id,playImgList.get(k).toString());
								}
								//复制其他文件到临时文件夹(音乐和剩余图片)
								for(int i = 0;i < newBookList.size();i++){//图片
									String oldPath = Utils.basePath1 + tpl_id + "/" + Utils.imgPathName + "/" + NamePic.mp3UrlToFileName(newBookList.get(i).toString() + endName);
									String newPath = Utils.path +  NamePic.mp3UrlToFileName(newBookList.get(i).toString());
									File strFile = new File(oldPath);
									if(strFile.exists())
										copyFile(oldPath,newPath);
								}
								for(int j = 0 ; j < musicList.size();j++){
									String oldPath = Utils.basePath1 + tpl_id + "/" + Utils.audPathName + "/" + NamePic.mp3UrlToFileName(musicList.get(j).toString());
									String newPath = Utils.path + NamePic.mp3UrlToFileName(musicList.get(j).toString());
									File strFile = new File(oldPath);
									if(strFile.exists()){
										copyFile(oldPath,newPath);
									}
								}
								/*
								 * 添加做书图片（png）url和id到数据库
								 */
								Utils.mDBPlayinfoHelper.delete(Integer.parseInt(tpl_id));
								for(int m = 0;m < newBookList.size();m++){
									Utils.mDBPlayinfoHelper.addBitUrl(Integer.parseInt(tpl_id),newBookList.get(m));
								}
								for(int n = 0;n < playIdList.size();n++){
									Utils.mDBPlayinfoHelper.addBitId(Integer.parseInt(tpl_id),playIdList.get(n));
								}
								saveCache(playImgList);
								Message msg = new Message();
								msg.what = 1;
								mHandler.sendMessage(msg);
							} catch (JSONException e) {
								e.printStackTrace();
								Log.i("object", "null");
								Message msg = new Message();
								msg.what = 3;
								mHandler.sendMessage(msg);
								return;
							}
						}
					}
			
			@Override
			public void onFail(Exception e) {
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessage(msg);
				Log.i("error Exception", e.toString());
			}
		});
		DefaultThreadPool.getInstance().execute(get);
	}
	public void copyFile(String oldPath,String newPath){
		int bufferSum = 0;
		int bufferRead = 0;
		if(new File(oldPath).exists()){
			try {
				InputStream is = new FileInputStream(oldPath);
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while((bufferRead = is.read(buffer)) != -1){
					bufferSum += bufferRead;
					fs.write(buffer, 0, bufferRead);
					fs.flush();
				}
				is.close();
				fs.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	commentDialogUtils NoZuoShuTips;
	Button wait;
	void showWait(){
		NoZuoShuTips = new commentDialogUtils(mContext, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, R.layout.wait_next, R.style.Theme_dialog);
		wait = (Button) NoZuoShuTips.findViewById(R.id.wait_bnt);
		TextView tv1 = (TextView) NoZuoShuTips.findViewById(R.id.wait_tv1);
		TextView tv2 = (TextView) NoZuoShuTips.findViewById(R.id.wait_tv2);
		tv1.setText("     该模板暂不支持做书，敬请期待或先选择其它模板!");
		tv1.setTextSize(13);
		tv2.setText("");
		wait.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mShowZuoShuTips.dismissTips();
				NoZuoShuTips.dismiss();
			}
		});
		NoZuoShuTips.show();
	}
	/*
	 * 保存做书图片（png）(对图片进行转移至临时缓存文件夹)
	 */
	private void saveCache(ArrayList<String> bitUrlList){
		String path1 = Utils.zuoshuCache;
		if(!new File(path1).exists()){
			new File(path1).mkdirs();
		}
		String path2 = Utils.zuoshuCache + tpl_id + "/";
		if(!new File(path2).exists()){
			new File(path2).mkdirs();
		}
		for(int i = 0;i < bitUrlList.size();i++){
			String oldPath = Utils.path + NamePic.UrlToFileName(bitUrlList.get(i));
			String newPath = path2 + NamePic.UrlToFileName(bitUrlList.get(i));
			if(new File(oldPath).exists()){
				copyFile(oldPath, newPath);
			}
		}
	}
}
