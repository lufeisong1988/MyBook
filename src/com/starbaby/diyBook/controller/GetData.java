package com.starbaby.diyBook.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

//import com.starbaby.diyBook.Helper.DBHelper;
import com.starbaby.diyBook.helper.HttpHelper;
import com.starbaby.diyBook.model.ThreadPool;
import com.starbaby.diyBook.utils.JavaBeanLocation;
import com.starbaby.diyBook.utils.StoreSrc;
import com.starbaby.diyBook.utils.Utils;
/**
 *
 * 把点击书本的信息保存在本地数据库。方便下次阅读直接读取缓存。
 * ture 为第一次阅读，在线下载
 * false 已经读过，直接从本地缓存读取
 * @author Administrator
 *
 */
public class GetData {
	private StoreSrc store;
	private JavaBeanLocation mJavaBeanLocation;
	private String result = null;
//	DBHelper helper;
	LinearLayout linearlayout;
	View view;
	float x;
	float y ;
	float width;
	float height;
	public GetData(LinearLayout linearlayout,View view,float x,float y ,float width,float height) {
//		this.helper = helper;
		this.linearlayout = linearlayout;
		this.view = view;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	@SuppressWarnings("static-access")
	/*
	 * 在线阅读 网络请求。
	 */
	public boolean getDate(String bookInfoUrl,String id) {
		try {
			final ArrayList<String> urlList = new ArrayList<String>();
			final ArrayList<String> audioList = new ArrayList<String>();
			
			result = HttpHelper.GetNewBook(bookInfoUrl + Integer.parseInt(id));
			if(result == null){
				return false;
			}
			Log.i("result",result);
			JSONObject object = new JSONObject(result);
			final String tpl_name = id;
			int audio_available = object.getInt("audio_available");
			int image_count = object.getInt("image_count");
			int image_width = object.getInt("image_width");
			int image_height = object.getInt("image_height");
			final JSONArray imagelist = object.getJSONArray("imagelist");
			final JSONArray audiolist = object.getJSONArray("audiolist");
			/*
			 * 进行下载 4张图片和1个音乐（或者为0）
			 */
			ArrayList<String> imgList = new ArrayList<String>();
			ArrayList<String> adoList = new ArrayList<String>();
			for(int i = 0;i < 4;i++){
				imgList.add((String) imagelist.get(i));
			}
			if (audio_available == 1) {
				adoList.add((String) audiolist.get(1));
			}
			new ThreadPool(tpl_name,imgList,adoList,audio_available).begin();
			/*
			 * 下载打开内容完成
			 */
			if(Utils.bOpen){
				mJavaBeanLocation = new JavaBeanLocation();
				store = new StoreSrc();
				store.audiolist = new ArrayList<String>();
				
				Utils.bOpen = false;
				mJavaBeanLocation.setLinearlayout(linearlayout);
				mJavaBeanLocation.setView(view);
				mJavaBeanLocation.setX(x);
				mJavaBeanLocation.setY(y);
				mJavaBeanLocation.setWidth(width);
				mJavaBeanLocation.setHeight(height);
				
				for (int i = 0; i < imagelist.length(); i++) {
					String url = null;
					url = (String) imagelist.get(i);
					urlList.add(url);
				}
				if (audio_available == 1) {
					for (int i = 0; i < audiolist.length(); i++) {
						String audio = null;
						audio = (String) audiolist.get(i);
						audioList.add(audio);
					}
					store.setAudiolist(audioList);
					Utils.createMusic = true;
				} else {
					Utils.createMusic = false;
				}
				store.setResult(result);
				store.setImagelist(urlList);
				store.setTpl_name(tpl_name);
				store.setImage_count(image_count);
				store.setImage_width(image_width);
				store.setImage_height(image_height);
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	/*
	 * 本地阅读 。已经缓存在本地了
	 */
	public boolean getLocalData(String bookName){
		if(Utils.bOpen){
			Utils.bOpen = false;
			mJavaBeanLocation = new JavaBeanLocation();
			store = new StoreSrc();
			mJavaBeanLocation.setLinearlayout(linearlayout);
			mJavaBeanLocation.setView(view);
			mJavaBeanLocation.setX(x);
			mJavaBeanLocation.setY(y);
			mJavaBeanLocation.setWidth(width);
			mJavaBeanLocation.setHeight(height);
			final ArrayList<String> urlList = new ArrayList<String>();
			final ArrayList<String> audioList = new ArrayList<String>();
			Cursor mCursor = Utils.mDBHelper.getBookName(bookName);
			mCursor.moveToFirst();
			store.setTpl_name(mCursor.getString(mCursor.getColumnIndex("NAME")));
			store.setImage_count(mCursor.getInt(mCursor.getColumnIndex("COUNT")));
			store.setImage_width(mCursor.getInt(mCursor.getColumnIndex("WIDTH")));
			store.setImage_height(mCursor.getInt(mCursor.getColumnIndex("HEIGHT")));
			Cursor imgCursor = Utils.mDBHelper.getIMG(bookName);
			if (imgCursor.getCount() != 0) {// 以前阅读过，从本地读取缓存
				for (int i = 0; i < imgCursor.getCount(); i++) {
					urlList.add(imgCursor.getString(imgCursor.getColumnIndex("IMG")));
					imgCursor.moveToNext();
				}
				Cursor imp3Cursor = Utils.mDBHelper.getMP3(bookName);
				if (imp3Cursor.getCount() != 0) {
					for (int i = 0; i < imp3Cursor.getCount(); i++) {
						audioList.add(imp3Cursor.getString(imp3Cursor
								.getColumnIndex("MP3")));
						imp3Cursor.moveToNext();
					}
					store.setAudiolist(audioList);
					Utils.createMusic = true;
				} else {
					Utils.createMusic = false;
				}
				store.setImagelist(urlList);
			}
			return true;
		}else{
			return false;
		}
	}
}
