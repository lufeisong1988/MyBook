package com.starbaby.diyBook.helper;

import android.util.Log;

/**
 * 保存图片名称工具类
 * @author Administrator
 *
 */
public class NamePic {
	 private static final String WHOLESALE_CONV = ".cach"; 
	 public static String convertUrlToFileName(String url) {
		String[] strs = url.split("/");
		String[] strs2 = strs[strs.length - 1].split("\\.");
		return strs2[0] + ".jpg" +WHOLESALE_CONV ;
	    }
	 public static String mp3UrlToFileName(String url){
		 String[] strs = url.split("/");
	     return strs[strs.length - 1] ;
	 }
	 public static String ZuoShuUrlToFileName(String url){
		 String[] strs = url.split("/");
		 String[] strs2 = strs[strs.length - 1].split("\\.");
		 return strs2[0] + ".jpg";
	 }
	 public static String ZuoShuUrlToFileNamePNG(String url){
		 String[] strs = url.split("/");
		 String[] strs2 = strs[strs.length - 1].split("\\.");
		 return strs2[0] + ".png";
	 }
	 public static String UrlToFileName(String url){
		 String[] strs = url.split("/");
		 return strs[strs.length - 1];
	 }
}
