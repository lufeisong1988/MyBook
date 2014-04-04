package com.starbaby.diyBook.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.starbaby.diyBook.utils.StoreSrc;
import com.starbaby.diyBook.utils.Utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

public class MusicHelper {
	/**
	 * 声音控制类
	 * 
	 * @author 
	 * 
	 */
	public static MediaPlayer music;
	private static boolean musicSt = true; // 音乐开关
	private static Context context;
	private static int msec;
	private static int currentindex;
	private static Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
			case 1:
				startMusic();
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	/**
	 * 初始化方法
	 * 
	 * @param c
	 */
	public static void init(Context c,int index) {
		context = c;
		currentindex = index;
		music = new MediaPlayer();
		if(Utils.createMusic){
			initMusic(currentindex + 1);
		}
	}


	// 初始化音乐播放器
	@SuppressWarnings("static-access")
	private static void initMusic(final int index) {
		if (index < StoreSrc.getAudiolist().size()) {
			if ((index) < (StoreSrc.getAudiolist().size() - 1)
					|| (index) == (StoreSrc.getAudiolist().size() - 1)) {
				String realPath = Utils.basePath1 + StoreSrc.getTpl_name()
						+ "/diyBook_MusicCache/" + NamePic.mp3UrlToFileName(StoreSrc.getAudiolist().get(index));// mp3文件存储路径
				if (new File(realPath).exists()) {
					try {
						File file = new File(realPath);
						FileInputStream fis = new FileInputStream(file);
						if (music != null) {
							music.setDataSource(fis.getFD());
							music.prepare();
							Utils.currentMusicTime = music.getDuration();
							music.setLooping(true);
							Message msg = new Message();
							msg.what = 1;
							mHandler.sendMessage(msg);
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 暂停音乐
	 */
	public static void pauseMusic() {
		if (music != null && music.isPlaying()){
			music.pause();
			msec = music.getCurrentPosition();
		}
	}

	/**
	 * 播放音乐
	 */
	public static void startMusic() {
		if (musicSt){
			if(music != null){
				music.start();
				music.setLooping(false);
			}
		}
	}
	/**
	 * 暂停后继续播放
	 */
	public static void keepMusic(){
		if (musicSt && music != null){
			music.seekTo(msec);
			music.start();
			music.setLooping(false);
		}
	}
	/**
	 * 切换一首音乐并播放
	 */
	public static void changeAndPlayMusic(int index) {
		if (music != null){
			music.reset();
			initMusic(index);
		}
	}
	/**
	 * 停止播放
	 */
	public static void stopMusci(){
		if(music != null ){
			if(music.isPlaying()){
				music.stop();
				music.release();
			}
		}
		music = null;
	}
	/**
	 * 获得音乐开关状态
	 * 
	 * @return
	 */
	public static boolean isMusicSt() {
		return musicSt;
	}

	/**
	 * 设置音乐开关
	 * 
	 * @param musicSt
	 */
	public static void setMusicSt(boolean musicSt) {
		if(music == null){
			music = new MediaPlayer();
		}
		MusicHelper.musicSt = musicSt;
		if (musicSt)
			music.start();
		else
			music.stop();
	}
}
