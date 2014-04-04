package com.starbaby.diyBook.controller;

import java.io.File;
import java.util.ArrayList;

import com.starbaby.diyBook.utils.Utils;

import android.content.Context;
import android.database.Cursor;

/**
 * 
 * @author Administrator
 * 删除书本缓存（图片 ，音乐）
 */
public class DeleteCache {
	public static void delAllFile(String path){
		File file = new File(path);
		if(!file.exists()){
			return;
		}
		if(!file.isDirectory()){
			return;
		}
		String[] tempList = file.list();
		File temp = null;
		for(int i = 0;i < tempList.length;i++){
			if(path.endsWith(File.separator)){
				temp = new File(path + tempList[i]);
			}else{
				temp = new File(path + File.separator +tempList[i]);
			}
			if(temp.isFile()){
				final File copyTemp = new File(temp + ".cache");
				temp.renameTo(copyTemp);
				copyTemp.delete();
			}
			if(temp.isDirectory()){
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" +tempList[i]);//  再删除空文件夹
			}
			delFolder(path);
		}
	}
	
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			final File copyMyFilePath = new File(myFilePath + ".cache");
			myFilePath.renameTo(copyMyFilePath);
			copyMyFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();
		}
	}
	/*
	 * 智能删除
	 */
	public static void checkRoom(Context mContext){
		smartDeleteCache(mContext);
		if(Utils.SDcardMemory > ReadSDcard.readSDCard()){
			checkRoom(mContext);
		}
	}
	public static void smartDeleteCache(Context mContext){
		ArrayList<String> tpl_id = new ArrayList<String>();
//		DBHelper mDBHelper = null;
		Cursor cursor = null;
//		mDBHelper = new DBHelper(mContext);
		cursor = Utils.mDBHelper.getLocalBook();
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			for(int i = 0;i < cursor.getCount();i++){
				tpl_id.add(cursor.getString(cursor.getColumnIndex("NAME")));
			}
			delAllFile(Utils.basePath1 + tpl_id.get(0).toString());
			Utils.mDBUserInfoHelper.deleteLocalBook(tpl_id.get(0).toString());
			Utils.mDBHelper.deleteBOOK_info(tpl_id.get(0).toString());
			Utils.mDBHelper.deleteIMG_info(tpl_id.get(0).toString());
			Utils.mDBHelper.deleteMP3_info(tpl_id.get(0).toString());
			Utils.mDBHelper.deleteUPDATE_info(tpl_id.get(0).toString());
		}
		cursor.close();
	}
}
