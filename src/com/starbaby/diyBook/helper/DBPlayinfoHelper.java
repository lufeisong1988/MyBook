package com.starbaby.diyBook.helper;
/**
 * 缓存 做书所需的图片资源信息
 */
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBPlayinfoHelper {
	private static final String playinfo = "playinfo.db";
	public static String playinfo_result = "playinfo_result";
	public static String pageno_str = "pageno_str";
	public static String images_str = "images_str";
	public static Context mContext;
	public SQLiteDatabase db;
	public DBPlayinfoHelper(Context mContext){
		DBPlayinfoHelper.mContext = mContext;
		db = mContext.openOrCreateDatabase(playinfo, Context.MODE_PRIVATE, null);
		createTable();
	}
	/*
	 * 创建表
	 */
	private void createTable(){
		if(!bExistTable(pageno_str)){
			db.execSQL("create table " + pageno_str + " ( _id INTEGER PRIMARY KEY autoincrement," + "tpl_id integer," + "pageno_str text );");
		}
		if(!bExistTable(images_str)){
			db.execSQL("create table " + images_str + " ( _id INTEGER PRIMARY KEY autoincrement," + "tpl_id integer," + "images_str integer );");
		}
		if(!bExistTable(playinfo_result)){
			db.execSQL("create table " + playinfo_result + " ( _id INTEGER PRIMARY KEY autoincrement," + "tpl_id integer," + "playinfo_result text );");
		}
	}
	/*
	 * 判断表是否存在
	 */
	private boolean bExistTable(String tableName){
		if(tableName == null || tableName.equals("")){
			return false;
		}
		Cursor cursor = null;
		String sql = "select count(1) as c from sqlite_master where type = 'table' and name = '" + tableName + "'";
		cursor = db.rawQuery(sql, null);
		if(cursor.moveToFirst()){
			int count = cursor.getInt(0);
			if(count > 0){
				return true;
			}
		}
		cursor.close();
		return false;
	}
	/*
	 * 添加模板的所有信息
	 */
	public void addResult(int tpl_id,String result){
		String sql = null;
		sql = "insert into " + playinfo_result + " values ( null,'" + tpl_id + "','" + result +"')";
		db.execSQL(sql);
	}
	/*
	 * 添加做书图片（png）
	 */
	public void addBitUrl(int tpl_id,String bitUrl){
		String sql = null;
		sql = "insert into " + pageno_str +" values ( null,'" + tpl_id + "','" + bitUrl  +"')";
		db.execSQL(sql);
	}
	/*
	 * 添加做书图片对应的下标Id
	 */
	public void addBitId(int tpl_id,int bitId){
		String sql = null;
		sql = "insert into " + images_str +" values ( null,'" + tpl_id + "','" + bitId  +"')";
		db.execSQL(sql);
	}
	/*
	 * 获取模板所有信息
	 */
	public String getResult(int tpl_id){
		String result = null;
		Cursor cursor;
		cursor = db.query(playinfo_result, new String[]{"_id","tpl_id","playinfo_result"}, "tpl_id = '" + tpl_id + "'", null, null, null, null);
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			for(int i = 0;i < cursor.getCount();i++){
				result = cursor.getString(cursor.getColumnIndex("playinfo_result"));
			}
		}
		cursor.close();
		return result;
	}
	/*
	 * 获取做书图片（png）
	 */
	public ArrayList<String> getBitUrl(int tpl_id){
		ArrayList<String> bitUrl = new ArrayList<String>();
		Cursor cursor;
		cursor = db.query(pageno_str, new String[]{"_id","tpl_id","pageno_str"}, "tpl_id = '" + tpl_id + "'", null, null, null, null);
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			for(int i = 0;i < cursor.getCount();i++){
				bitUrl.add(cursor.getString(cursor.getColumnIndex("pageno_str")));
				cursor.moveToNext();
			}
			return bitUrl;
		}
		cursor.close();
		return bitUrl;
	}
	/*
	 * 获取做书图片的下标id
	 */
	public ArrayList<Integer> getBitId(int tpl_id){
		ArrayList<Integer> bitId = new ArrayList<Integer>();
		Cursor cursor;
		cursor = db.query(images_str, new String[]{"_id","tpl_id","images_str"}, "tpl_id = '" + tpl_id + "'", null, null, null, null);
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			for(int i = 0;i < cursor.getCount();i++){
				bitId.add(cursor.getInt(cursor.getColumnIndex("images_str")));
				cursor.moveToNext();
			}
			return bitId;
		}
		cursor.close();
		return bitId;
	}
	/*
	 * 在个人中心删除模板时，删除模板所有信息
	 */
	public void deleteAll(int tpl_id){
		Cursor cursor;
		cursor = db.query(playinfo_result, new String[]{"_id","tpl_id","playinfo_result"}, "tpl_id = '" + tpl_id + "'", null, null, null, null);
		if(cursor.getCount() > 0){
			String sql = null;
			sql = "delete from " + playinfo_result + " where tpl_id = '" + tpl_id + "'";
			db.execSQL(sql);
		}
		cursor.close();
	}
	/*
	 * 删除对应tpl_id下的图片信息和图片下标id信息
	 */
	public void delete(int tpl_id){
		Cursor cursor ;
		cursor = db.query(pageno_str, new String[]{"_id","tpl_id","pageno_str"}, "tpl_id = '" + tpl_id + "'", null, null, null, null);
		if(cursor.getCount() > 0){
			String sql = null;
			sql = "delete from " + pageno_str + " where tpl_id = '" + tpl_id + "'";
			db.execSQL(sql);
		}
		cursor = db.query(images_str, new String[]{"_id","tpl_id","images_str"}, "tpl_id = '" + tpl_id + "'", null, null, null, null, null);
		if(cursor.getCount() > 0){
			String sql2 = null;
			sql2 = "delete from " + images_str + " where tpl_id = '" + tpl_id + "'";
			db.execSQL(sql2);
		}
		cursor.close();
	}
	/*
	 * 删除表下的所有信息
	 */
	public void deleteAllInfo(){
		String sql = null;
		String sql2 = null;
		String sql3 = null;
		sql = "delete from " + pageno_str;
		sql2 = "delete from " + images_str;
		sql3 = "delete from " + playinfo_result;
		db.execSQL(sql);
		db.execSQL(sql2);
		db.execSQL(sql3);
	}
}
