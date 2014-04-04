package com.starbaby.diyBook.helper;
/**
 * 书本打开的声音工具类
 */

import com.starbaby.diyBook.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class BookMusicHelper {
	Context mContext;
	private AudioManager audioMgr = null; 
	private int CurrentVolum;
	public BookMusicHelper(Context mContext){
		this.mContext = mContext;
		audioMgr = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);  
		CurrentVolum = audioMgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	}
	/*
	 * 打开书
	 */
	public void openBook(){
//		audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, CurrentVolum,AudioManager.FLAG_PLAY_SOUND);  
		MediaPlayer mp = MediaPlayer.create(mContext, R.raw.book_open);
		if(mp != null){
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
					mp = null;
				}
			});
		}
	}
	/*
	 * 关闭书
	 */
	public void closeBook(){
//		audioMgr.setStreamVolume(AudioManager.STREAM_MUSIC, CurrentVolum,AudioManager.FLAG_PLAY_SOUND);
		MediaPlayer mp = MediaPlayer.create(mContext, R.raw.book_close);
		if(mp != null){
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
					mp = null;
				}
			});
		}
	}
	/*
	 * 打开app音效
	 */
//	public MediaPlayer mp ;
//	public void openApp(){
//		mp = MediaPlayer.create(mContext, R.raw.openapp);
//		if(mp != null){
//			mp.start();
//		}
//		
//	}
	/*
	 * 按钮点击音效
	 */
	public void pressBnt(){
		MediaPlayer mp = MediaPlayer.create(mContext, R.raw.press);
		if(mp != null){
			mp.start();
			mp.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
					mp = null;
				}
			});
		}
	}
}
