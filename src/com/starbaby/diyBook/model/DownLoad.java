package com.starbaby.diyBook.model;
/**
 * 下载模块
 */
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.starbaby.diyBook.cache.ImageFileCache;
import com.starbaby.diyBook.cache.ImageGetFromHttp;
import com.starbaby.diyBook.cache.MusicCache;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.utils.StoreSrc;
import com.starbaby.diyBook.utils.Utils;

public class DownLoad {
	private static Context mContext;
	private static CountDownLatch latch;
	private String bookName = "";
	String tpl_name = "";
	int image_count = 0;
	int image_width = 0;
	int image_height = 0;
	String bookResult = null;
	ArrayList<String> imagelist = new ArrayList<String>();
	ArrayList<String> audiolist = new ArrayList<String>();
	String time = "";
	SeekBar sb;
	long  count = 0;
	@SuppressWarnings("static-access")
	public DownLoad(Context mContext,String bookName,String time ,String tpl_name,int image_count,int image_width,int image_height,ArrayList<String> imagelist,ArrayList<String> audiolist,String bookResult){
		this.bookName = bookName;
		this.mContext = mContext;
		this.time = time;
		this.tpl_name = tpl_name;
		this.image_count = image_count;
		this.image_width = image_width;
		this.image_height = image_height;
		this.imagelist = imagelist;
		this.audiolist = audiolist;
		this.bookResult = bookResult;
	}
	/*
	 * 打开书本后，下载完4张图片（一个音乐）后开始下载剩余的所有内容
	 */
	public void saveOther(SeekBar sb){
		this.sb = sb;
		final Message msg = new Message();
		msg.what = 1;
		Thread thread = new Thread() {

			@SuppressWarnings("static-access")
			public void run() {
				ExecutorService exec = Executors.newCachedThreadPool();
				if (Utils.createMusic) {//有音乐
					latch = new CountDownLatch(imagelist.size() + audiolist.size() );
					count = latch.getCount();
					for (int n = 0; n < (imagelist.size() ); n++) {
						if(Utils.bOpen){
							return;
						}else{
							Bitmap bit = BitmapFactory.decodeFile(Utils.basePath1 + bookName+ Utils.basePath2 + NamePic.convertUrlToFileName(imagelist.get(n)));
							if (bit != null) {
								latch.countDown();
								refresh();
							} else {
								exec.execute(new ImgRnuable(latch, imagelist.get(n ),bookName));
							}
						}
					}
					for(int j = 0;j < (audiolist.size() );j++){
						if(Utils.bOpen){//书本关闭,终止下载
							return;
						}else{//书本打开
							new MusicCache().saveMp3(audiolist.get(j ));
							latch.countDown();
							refresh();
						}
					}
				} else{//无音乐
					latch = new CountDownLatch(imagelist.size() );
					count = latch.getCount();
					for (int n = 0; n < (imagelist.size() ); n++) {
						Bitmap bit = BitmapFactory.decodeFile(Utils.basePath1 + bookName+ Utils.basePath2 + NamePic.convertUrlToFileName(imagelist.get(n )));
						if (bit != null) {
							latch.countDown();
							refresh();
						} else {
							exec.execute(new ImgRnuable(latch, imagelist.get(n ),bookName));
						}
					}
				}
				try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				exec.shutdown();
				//记录 下载完成的书本。下次本地直接打开
				//bookName = tpl_id
				Cursor bookCursor = null;
				bookCursor = Utils.mDBHelper.getBookName(bookName);
				if(bookCursor.getCount() == 0){
					bookCursor.close();
					Utils.mDBHelper.saveINFO(bookName, image_count, image_width, image_height);
					Utils.mDBHelper.saveTIME(bookName, time);
					for(int i = 0;i < imagelist.size();i++){
						Utils.mDBHelper.saveIMG(bookName, imagelist.get(i));
					}
					if(Utils.createMusic){
						for(int j = 0;j < audiolist.size();j++){
							Utils.mDBHelper.saveMP3(bookName, audiolist.get(j));
						}
					}
					Log.i("download finish",bookName);
					//记录 个人个人信息下数据库，方便在个人中心进行删除，读取操作
					//从3张表里获取 相应的coverUrl用来记录封面
					//存储书本信息到 个人中心的本地书架
					String coverUrl = "";
					String time = "";
					Cursor cursor = null;
					Cursor cursor2 = null;
					Cursor cursor3 = null;
					cursor = Utils.mDBCacheHelper.getLocalBookInfo(bookName);
					if(cursor.getCount() > 0){
						cursor.moveToFirst();
						coverUrl = cursor.getString(cursor.getColumnIndex("COVER"));
						time = cursor.getString(cursor.getColumnIndex("TIME"));
						cursor.close();
					}
					cursor2 = Utils.mDBUserInfoHelper.getLocalBookInfo(bookName);
					if(cursor2.getCount() > 0){
						cursor2.moveToFirst();
						coverUrl = cursor2.getString(cursor2.getColumnIndex("COVERURL"));
						time = cursor2.getString(cursor2.getColumnIndex("TIME"));
						cursor2.close();
					}
					cursor3 = Utils.mDBUserInfoHelper.getLocalCoverCollect(bookName);
					if(cursor3.getCount() > 0){
						cursor3.moveToFirst();
						coverUrl = cursor3.getString(cursor3.getColumnIndex("COVERURL"));
						time = cursor3.getString(cursor3.getColumnIndex("TIME"));
						cursor3.close();
					}
					if(!Utils.mDBUserInfoHelper.bExist(tpl_name)){
						Utils.mDBUserInfoHelper.saveLocalBook(bookName,tpl_name,coverUrl,time);
					}
					Utils.mDBPlayinfoHelper.addResult(Integer.parseInt(tpl_name), bookResult);
				}
				super.run();
			}
		};
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/*
	 * 线程下载图片
	 */
	
	class ImgRnuable implements Runnable{
		CountDownLatch latch;
		String url;
		String mbookName;
		public ImgRnuable(CountDownLatch latch, String url,String mbookName){
			this.latch = latch;
			this.url = url;
			this.mbookName = mbookName;
		}

		@Override
		public void run() {
			Bitmap bit = ImageGetFromHttp.downloadBitmap(url);
			if(bit != null){
				new ImageFileCache(mbookName).saveBitmap(bit, url);
			}
			latch.countDown();
			refresh();
		}
	}
	void refresh(){
		((Activity) mContext).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				final long i = latch.getCount();
				if(sb != null){
					sb.setMax((int)count);
					sb.invalidate();
					sb.setProgress((int)(count - i));
				}
			}
		});
	}
}
