package com.starbaby.diyBook.model;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;

import com.starbaby.diyBook.cache.CoverCacheFromHttp;

/**
 * 控制 每个分类下书本封面的缓存 线程1(首页各个分类  作品秀)
 * @author Administrator
 *
 */
public class CoverThreadPool {
	private static CountDownLatch latch;
	Context mContext;
	String section;
	ArrayList<String> bookCoverList;
	public CoverThreadPool(Context mContext,ArrayList<String> bookCoverList,String section){
		this.mContext = mContext;
		this.section = section;
		this.bookCoverList = bookCoverList;
	}
	public void saveCover(){
		Thread thread = new Thread(){

			@Override
			public void run() {
				latch = new CountDownLatch(bookCoverList.size());
				ExecutorService exec = Executors.newCachedThreadPool();
				for(int n = 0;n <bookCoverList.size() ;n++){
					if(bookCoverList.get(n) != null && !bookCoverList.get(n).equals("")){
						exec.execute(new ImgRnuable(latch, bookCoverList.get(n)));
					}
				}
				try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				exec.shutdown();
			}
			
		};
		thread.start();
	}
	Bitmap bit;
	class ImgRnuable implements Runnable{
		CountDownLatch latch;
		String url;
		public ImgRnuable(CountDownLatch latch, String url){
			this.latch = latch;
			this.url = url;
		}

		@Override
		public void run() {
			bit = new CoverCacheFromHttp(mContext, section).saveBookCache(url);
			if(bit != null){
				latch.countDown();
			}
		}
	}
}
