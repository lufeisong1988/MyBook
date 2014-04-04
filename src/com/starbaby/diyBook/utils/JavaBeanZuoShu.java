package com.starbaby.diyBook.utils;

import java.io.Serializable;
import java.util.ArrayList;

public class JavaBeanZuoShu implements Serializable{
	public ArrayList<String> musicList = new ArrayList<String>();//做书里的音乐资源（如果存在）
//	public ArrayList<String> imageList = new ArrayList<String>();//模板图书的图片集合（别人作品）
//	public ArrayList<String> playInfo = new ArrayList<String>();//
	public ArrayList<Integer> playId = new ArrayList<Integer>();//做书图片的id位置
	public ArrayList<String> playList = new ArrayList<String>();//做书图片和原图片的集合
//	public ArrayList<String> getImageList() {
//		return imageList;
//	}
//	public void setImageList(ArrayList<String> imageList) {
//		this.imageList = imageList;
//	}
//	public ArrayList<String> getPlayInfo() {
//		return playInfo;
//	}
//	public void setPlayInfo(ArrayList<String> playInfo) {
//		this.playInfo = playInfo;
//	}
	public ArrayList<Integer> getPlayId() {
		return playId;
	}
	public void setPlayId(ArrayList<Integer> playId) {
		this.playId = playId;
	}
	public ArrayList<String> getPlayList() {
		return playList;
	}
	public void setPlayList(ArrayList<String> playList) {
		this.playList = playList;
	}
	public ArrayList<String> getMusicList() {
		return musicList;
	}
	public void setMusicList(ArrayList<String> musicList) {
		this.musicList = musicList;
	}
}
