package com.starbaby.diyBook.controller;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.main.ZuoShuActivity;
import com.starbaby.diyBook.utils.JavaBeanZuoShu;
import com.starbaby.diyBook.utils.StoreSrc;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.commentDialogUtils;
import com.starbaby.diyBook.view.CurlView;
import com.starbaby.diyBook.view.ShowZuoShuTips;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

public class GetZuoShuCache {
	private Context mContext;
	
	private String tpl_id;
	
	private ArrayList<Integer> playIdList = new ArrayList<Integer>();//做书模板做图对应的下标id
	private ArrayList<String> newBookList = new ArrayList<String>();//做书模板图片集合
	private ArrayList<String> musicList = new ArrayList<String>();//做书模板里的音频集合
	private ArrayList<String> imageList = new ArrayList<String>();//当前书本所有图片的url集合(jpg)
	private ArrayList<String> playImgList = new ArrayList<String>();//需要修改的图片的URL集合(png)
	
	private JavaBeanZuoShu mJavaBeanZuoShu;
	
	private PageProvider mPageProvider;
	private CurlView mCurlView;
	private GetBitFromHttp mGetBitFromHttp;
	
	private ShowZuoShuTips mShowZuoShuTips;
	private PopupWindow pop;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				Utils.bChangeMode = true;
				showWait();
				break;
			case 2:
				mJavaBeanZuoShu.playId = playIdList;
				mJavaBeanZuoShu.playList = newBookList;
				mJavaBeanZuoShu.musicList = musicList;
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
				Utils.bChangeMode = true;
				((Activity) mContext).startActivityForResult(intent,1);
				((Activity) mContext).overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
				break;
			}
			super.handleMessage(msg);
		}
	};
	public GetZuoShuCache(Context mContext,JavaBeanZuoShu mJavaBeanZuoShu,ShowZuoShuTips mShowZuoShuTips,PageProvider mPageProvider,CurlView mCurlView,PopupWindow pop){
		this.mContext = mContext;
		this.mJavaBeanZuoShu = mJavaBeanZuoShu;
		this.mShowZuoShuTips = mShowZuoShuTips;
		this.mPageProvider = mPageProvider;
		this.mCurlView = mCurlView;
		this.pop = pop;
		mGetBitFromHttp = new GetBitFromHttp(mContext,mShowZuoShuTips);
	}
	/*
	 * 本地没有该书缓存，重新下载所有book cache
	 */
	public void getAllBookCache(String url,final String tpl_id,final String bookResult) throws JSONException{
		this.tpl_id = tpl_id;
		JSONObject object = null;
		try {
			object = new JSONObject(bookResult);
			getBookExampleImg(tpl_id, object);
			getBookAudio(tpl_id, object);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	/*
	 * 本地有缓存
	 * 只下载模板(png),若果有模板(PNG)缓存，就不需要下载，直接进入做书模式
	 * 
	 */
	public void getOtherBookCache(final String tpl_id,final String bookResult){
		this.tpl_id = tpl_id;
		JSONObject object = null;
		try {
			object = new JSONObject(bookResult);
			bExistAll(object);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/*
	 * handler
	 */
	void sendHandlerMsg(int msgNo){
		Message msg = new Message();
		msg.what = msgNo;
		mHandler.sendMessage(msg);
	}
	/*
	 * 做书模板不存在tips
	 */
	private commentDialogUtils NoZuoShuTips;
	private Button wait;
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
	 * 获取做书模板所有图片信息(jpg and png)
	 * 先下载第一张png图片。然后进入做书模式，其他的在线程里继续下载
	 */
	void getBookExampleImg(String tpl_id,JSONObject object){
		getBookInofList(object,true);
		// 添加做书图片（png）url和id到数据库
		Utils.mDBPlayinfoHelper.delete(Integer.parseInt(tpl_id));
		for (int m = 0; m < newBookList.size(); m++) {
			Utils.mDBPlayinfoHelper.addBitUrl(Integer.parseInt(tpl_id),newBookList.get(m));
		}
		for (int n = 0; n < playIdList.size(); n++) {
			Utils.mDBPlayinfoHelper.addBitId(Integer.parseInt(tpl_id),playIdList.get(n));
		}
		getBookOtherImg(tpl_id,newBookList);
	}
	/*
	 * 获取音频文件
	 */
	void getBookAudio(String tpl_id,JSONObject object){
		JSONArray musList = null;
		String audio_available = null;
		try {
			audio_available = object.getString("audio_available");
			if(audio_available.equals("1")){
				musList = object.getJSONArray("audiolist");
				for(int j = 0; j < musList.length();j++){
					String musicUrl = musList.getString(j);
					GetMusFromHttp.downMusic(tpl_id,musicUrl);
					musicList.add(musicUrl);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/*
	 * 获取其他图片jpg
	 * 如果该书目录下有，就不要下载，直接copy。否则下载
	 */
	void getBookOtherImg(String tpl_id,ArrayList<String> otherImgList){
		for(int i = 0;i < otherImgList.size();i++){
			mGetBitFromHttp.getBitmap(tpl_id,otherImgList.get(i).toString());
		}
	}
	/*
	 * 判断模板所需图片(PNG)缓存 文件与数据信息是够同时存在
	 * 
	 */
	void bExistAll(JSONObject object){
		if(new File(Utils.zuoshuCache + tpl_id).exists()){
			getBookInofList(object,false);
		}else{
			getBookInofList(object,true);
		}
		getBookOtherImg(tpl_id,newBookList);
		getBookAudio(tpl_id,object);
	}
	/*
	 * 获取模板的信息playIdList newBookList
	 */
	void getBookInofList(JSONObject object,boolean saveCache){
		JSONObject playinfoObject = null;
		JSONArray imageArray = null;
		try {
			imageArray = object.getJSONArray("imagelist");
			playinfoObject = object.getJSONObject("playinfo");
			if(playinfoObject == null || playinfoObject.equals("")){//做书模板不存在
				sendHandlerMsg(1);
				return;
			}
			for(int i = 0;i < imageArray.length();i++){
				String imgUrl = imageArray.getString(i);
				imageList.add(imgUrl);
			}
			String playId = playinfoObject.getString("pageno_str");
			String playImg = playinfoObject.getString("images_str");
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
				int count = (int)playIdList.get(j) - 1;
				newBookList.remove(count);
				newBookList.add(count, playImgList.get(j).toString());
			}
			for (int k = 0; k < playImgList.size(); k++) {
				mGetBitFromHttp.getBitmapPng(tpl_id, playImgList.get(k).toString());
				if (k == 0) {
					sendHandlerMsg(2);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
	}
}
