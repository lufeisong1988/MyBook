package com.starbaby.diyBook.controller;

import com.starbaby.diyBook.R;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service{
	public static MediaPlayer mp;
	public MyBinder binder = new MyBinder();
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onDestroy() {
		Log.i("Service","ondestory");
		if(mp != null){
			mp.release();
			mp = null;
		}
		super.onDestroy();
	}

	public class MyBinder extends Binder{
		public MusicService getService(){
			return MusicService.this;
		}
	}

	public void excute() {
		mp = MediaPlayer.create(getBaseContext(), R.raw.openapp);
		mp.start();
		System.out.println("通过Binder得到Service的引用来调用Service内部的方法");
	}
	@Override
	public boolean onUnbind(Intent intent) {
		// 当调用者退出(即使没有调用unbindService)或者主动停止服务时会调用
		System.out.println("调用者退出了");
		if(mp != null){
			mp.release();
			mp = null;
		}
		return super.onUnbind(intent);
	}

}
