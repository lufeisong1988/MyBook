package com.starbaby.diyBook.utils;

import java.util.ArrayList;
/**
 * 每本书的具体信息 图片url 音乐url 书名 书页张数 音乐数目 书页宽高
 * @author Administrator
 *
 */
public class StoreSrc {
	public static String result = null;
	public static boolean currentPage = false;
	public static String tpl_name;
	public static int image_count;
	public static int image_width;
	public static int image_height;
	public static ArrayList<String> imagelist;
	public static ArrayList<String> audiolist;
	public static String getTpl_name() {
		return tpl_name;
	}
	public static void setTpl_name(String tpl_name) {
		StoreSrc.tpl_name = tpl_name;
	}
	public static int getImage_count() {
		return image_count;
	}
	public static void setImage_count(int image_count) {
		StoreSrc.image_count = image_count;
	}
	public static int getImage_width() {
		return image_width;
	}
	public static void setImage_width(int image_width) {
		StoreSrc.image_width = image_width;
	}
	public static int getImage_height() {
		return image_height;
	}
	public static void setImage_height(int image_height) {
		StoreSrc.image_height = image_height;
	}
	public static ArrayList<String> getImagelist() {
		return imagelist;
	}
	public static void setImagelist(ArrayList<String> imagelist) {
		StoreSrc.imagelist = imagelist;
	}
	public static ArrayList<String> getAudiolist() {
		return audiolist;
	}
	public static void setAudiolist(ArrayList<String> audiolist) {
		StoreSrc.audiolist = audiolist;
	}
	public static String getResult() {
		return result;
	}
	public static void setResult(String result) {
		StoreSrc.result = result;
	}
	
}
