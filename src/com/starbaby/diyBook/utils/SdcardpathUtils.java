package com.starbaby.diyBook.utils;

import android.annotation.SuppressLint;
/**
 * sdcard信息 保存头像图片
 * @author Administrator
 *
 */
@SuppressLint("SdCardPath")
public class SdcardpathUtils {
//	public static String AppPath = "/sdcard/starbaby_diyBook/myBook";
	public static String SaveHeadPath = Utils.sdPath + "/starbaby_diyBook/userInfo/";
	public static String headName=SaveHeadPath+"headImg"+".jpg";
	public static String changeHeadName=SaveHeadPath+"headImg2"+".jpg";
}
