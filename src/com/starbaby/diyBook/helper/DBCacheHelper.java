package com.starbaby.diyBook.helper;

import java.sql.Connection;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
/**
 * 书本封面缓存数据库
 * @author Administrator
 *
 */
public class DBCacheHelper {
	private static final String DATABASE_NAME = "starbaby_diyBook_cache.db";
	public static String table_BookCache = "book_cache";
	SQLiteDatabase db;
	Connection conn;
	Context mContext;
	String section;
	public DBCacheHelper(Context mContext){
		this.mContext = mContext;
		db = mContext.openOrCreateDatabase(DATABASE_NAME, mContext.MODE_PRIVATE, null);
		createTable();
	}
	void createTable(){
		if(isTableExist(table_BookCache)){
			
		}else{
			db.execSQL("CREATE TABLE "+ table_BookCache + " ("+ "_id INTEGER PRIMARY KEY autoincrement," + "SECTION TEXT,"+ "COVER TEXT," + "NAME TEXT," + "ID TEXT," +"TIME TEXT"+ ");");
		}
	}
	/**
	 * 判断表是否存在
	 * @param tableName
	 * @return
	 */
	public boolean isTableExist(String tableName){
		boolean result = false;
		if(tableName == null){
			return false;
		}
		try {
			Cursor cursor = null;
			String sql = "select count(1) as c from sqlite_master where type ='table' and name ='" + tableName + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}else{
					
				}
			}
			cursor.close();
		} catch (Exception e) {

		}         
		return result;
	}
	/**
	 * 保存cache
	 * @return
	 */
	public boolean saveCache(String section,String cover,String name,String id,String time){
		String sql = "";
		sql = "insert into "+ table_BookCache +" values ( null,'" + section +"','" + cover + "','" + name +"','" + id +"','" + time +"')";
		db.execSQL(sql);
		return true;
	}
	/**
	 * 获取cache
	 */
	public Cursor getbookCache(String section){
		String strNAME = "SECTION";
		Cursor mCursor = null;
		if(section != null && !section.equals("")){
			mCursor = db.query(table_BookCache, new String[] { "_id", "SECTION","COVER","NAME","ID","TIME" }, strNAME + "=" +"'"+ section+"'",null, null, null, null, null);
			if (mCursor != null) {
				mCursor.moveToFirst();
			}
		}
		return mCursor;
	}
	/*
	 * 根据tpl_id获取cache信息(本地书架)
	 */
	public Cursor getLocalBookInfo(String tpl_id){
		Cursor cursor = null;
		cursor = db.query(table_BookCache,  new String[] { "_id", "SECTION","COVER","NAME","ID","TIME" }, "ID = '" + tpl_id + "'", null, null, null, null);
		if(cursor.getCount() > 0){
			cursor.moveToFirst();
		}
		return cursor;
	}
//	/*
//	 * 根据tpl_id获coverUrl用来保存本地书本的封面
//	 */
//	public Cursor getLocalCover(String tpl_id){
//		Cursor cursor = null;
//		cursor = db.query(table_BookCache, new String[] { "_id", "SECTION","COVER","NAME","ID","TIME" }, "ID = '" + tpl_id +"'", null, null, null, null);
//		return cursor;
//	}
	/**
	 * 清空表每个分类下的数据
	 * 
	 */
	public void deleteSection(String section){
		String sql = "";
		if(section != null && !section.equals("")){
			sql = "delete from " + table_BookCache + " where SECTION =" + "'" +section +"'";
			db.execSQL(sql);
		}
	}
	/*
	 * 清空所有数据
	 */
	public void deleteAll(){
		String sql = "";
		sql = "DROP TABLE " + table_BookCache;
		db.execSQL(sql);
	}
}
