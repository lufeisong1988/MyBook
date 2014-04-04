package com.starbaby.diyBook.model;
/**
 * 控制 下载图片和音乐的线程
 */
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.starbaby.diyBook.cache.ImageFileCache;
import com.starbaby.diyBook.cache.ImageGetFromHttp;
import com.starbaby.diyBook.cache.MusicCache;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.utils.Utils;

public class ThreadPool {
	private static CountDownLatch latch;
	private String bookName;
	ArrayList<String> imgList;
	ArrayList<String> adoList;
	int audio_available;
	public ThreadPool(String bookName,ArrayList<String> imgList,ArrayList<String> adoList,int audio_available){
		this.bookName = bookName;
		this.imgList = imgList;
		this.adoList = adoList;
		this.audio_available = audio_available;
	}
	/**
	 * 第一次打开书本下载4张图片和一个音乐
	 */
	public void begin(){
		Thread thread = new Thread(){

			@Override
			public void run() {
				latch = new CountDownLatch(5);
				ExecutorService exec = Executors.newCachedThreadPool();
				for(int n = 0 ;n < 4;n++){
					Bitmap bit = BitmapFactory.decodeFile(Utils.basePath1 + bookName + Utils.basePath2 + NamePic.convertUrlToFileName(imgList.get(n)));
					if(bit != null){
						latch.countDown();
					}else{
						exec.execute(new ImgRnuable(latch, imgList.get(n)));	
					}
				}
				if(audio_available == 1){//有音乐
					MusicCache.saveMusic(adoList.get(0),bookName);
					latch.countDown();
				}else{//没有音乐
					Utils.auto = false;//设置手动（不存在自动模式）
					latch.countDown();
				}
				try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				exec.shutdown();
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
	class ImgRnuable implements Runnable{
		CountDownLatch latch;
		String url;
		public ImgRnuable(CountDownLatch latch, String url){
			this.latch = latch;
			this.url = url;
		}

		@Override
		public void run() {
			if(url != null && !url.equals("")){
				Bitmap bit = ImageGetFromHttp.downloadBitmap(url);
				if(bit != null){
					new ImageFileCache(bookName).saveBitmap(bit, url);
				}
				latch.countDown();
			}
		}
	}
}
