package com.starbaby.diyBook.helper;

import java.sql.Connection;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
/**
 * 每本书的信息缓存数据库
 * @author Administrator
 *
 */
public class DBHelper {
	private static final String DATABASE_NAME = "diyBook.db";
	public static String table_info = "BOOK_info";
	public static String table_img = "IMG_info";
	public static String table_mp3 = "MP3_info";
	public static String table_updateTime = "UPDATE_info";
	SQLiteDatabase db;
	Context mContext;
	Connection conn;
	@SuppressWarnings("static-access")
	public DBHelper(Context mContext){
		this.mContext = mContext;
		db = mContext.openOrCreateDatabase(DATABASE_NAME, mContext.MODE_PRIVATE, null);
		createTable();
	}
	/**
	 * 创建表
	 */
	public void createTable(){
		if(isTableExist(table_img)){
		}else{
			db.execSQL("CREATE TABLE "+ table_img + " ("+ "_id INTEGER PRIMARY KEY autoincrement," + "NAME TEXT,"+ "IMG TEXT" + ");");
		}
		if(isTableExist(table_mp3)){
		}else{
			db.execSQL("CREATE TABLE "+ table_mp3 + " ("+ "_id INTEGER PRIMARY KEY autoincrement," + "NAME TEXT,"+ "MP3 TEXT" + ");");
		}
		if(isTableExist(table_info)){
		}else{
			db.execSQL("CREATE TABLE "+ table_info + " ("+ "_id INTEGER PRIMARY KEY autoincrement," + "NAME TEXT," +"COUNT INTEGER," +"WIDTH INTEGER,"+ "HEIGHT INTEGER" + ");");
		}
		if(isTableExist(table_updateTime)){
		}else{
			db.execSQL("CREATE TABLE "+ table_updateTime + " ("+ "_id INTEGER PRIMARY KEY autoincrement," + "NAME TEXT," + "TIME TEXT" + ");");
		}
	}
	/**
	 * 保存数据
	 * @param no
	 * @param img
	 * @param mp3
	 * @return
	 */
	public boolean saveIMG(String name,String img){
		String sql = "";
		if(img != null && !img.equals("")){
			sql = "insert into "+ table_img +" values ( null,'" + name +"','" + img  +"')";
			db.execSQL(sql);
		}
		
		return true;
	}
	public boolean saveMP3(String name,String mp3){
		String sql = "";
		if(mp3 != null && !mp3.equals("")){
			sql = "insert into "+ table_mp3 +" values ( null,'" + name +"','" + mp3  +"')";
			db.execSQL(sql);
		}
		return true;
	}
	public boolean saveINFO(String name,int count,int width,int height){
		String sql = "";
		if(name != null && !name.equals("")){
			sql = "insert into "+ table_info +" values ( null,'" + name +"','" + count +"','" + width +"','"+ height  +"')";
			db.execSQL(sql);
		}
		return true;
	}
	public boolean saveTIME(String name,String time){
		String sql = "";
		if(name != null && !name.equals("")){
			sql = "insert into "+ table_updateTime +" values ( null,'" + name +"','" + time  +"')";
			db.execSQL(sql);
		}
		return true;
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
	 * 获取有多少本书
	 */
	public long BookCount(){
		String sql = "SELECT COUNT(*) FROM " + table_info;
		SQLiteStatement statement = db.compileStatement(sql);
		long count = statement.simpleQueryForLong();
		return count;
	}
	/**
	 * 查询书籍是否存在
	 * @return
	 */
	public Cursor getBookName(String name){
		String strNAME = "NAME";
		Cursor mCursor = db.query(table_info, new String[] { "_id", "NAME","COUNT","WIDTH","HEIGHT" }, strNAME + "=" +"'"+ name+"'",null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	/**
	 *查询img
	 */
	public Cursor getIMG(String name){
		String strNAME = "NAME";
		Cursor mCursor = db.query(table_img, new String[] { "_id", "NAME","IMG" }, strNAME + "=" +"'"+ name+"'",null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	/**
	 *查询mp3
	 */
	public Cursor getMP3(String name){
		String strNAME = "NAME";
		Cursor mCursor = db.query(table_mp3, new String[] { "_id", "NAME","MP3" }, strNAME + "=" +"'"+ name+"'",null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	/**
	 *查询更新时间
	 */
	public Cursor getTIME(String name){
		String strNAME = "NAME";
		Cursor mCursor = db.query(table_updateTime, new String[] { "_id", "NAME","TIME" }, strNAME + "=" +"'"+ name+"'",null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	/*
	 * 查询本地书籍
	 */
	public Cursor getLocalBook(){
		Cursor cursor = null;
		cursor = db.query(table_info, null, null, null, null, null, null);
		return cursor;
		
	}
	/*
	 * 删除书本信息
	 * 1.书本信息
	 * 2.图片信息
	 * 3.音乐信息
	 * 4.更新时间信息
	 */
	public void deleteBOOK_info(String tpl_id){
		String sql = "";
		sql = "delete from " + table_info + " where NAME = '" + tpl_id + "'";
		db.execSQL(sql);
	}
	public void deleteIMG_info(String tpl_id){
		String sql = "";
		sql = "delete from " + table_img + " where NAME = '" + tpl_id + "'";
		db.execSQL(sql);
	}
	public void deleteMP3_info(String tpl_id){
		String sql = "";
		sql = "delete from " + table_mp3 + " where NAME = '" + tpl_id + "'";
		db.execSQL(sql);
	}
	public void deleteUPDATE_info(String tpl_id){
		String sql = "";
		sql = "delete from " + table_updateTime + " where NAME = '" + tpl_id +"'";
		db.execSQL(sql);
	}
	public void deleteRefresh(String bookName){
		String sql1 = null;
		String sql2 = null;
		String sql3 = null;
		String sql4 = null;
		Cursor mCursor = getBookName(bookName);
		if(mCursor != null){
			sql1 = "DELETE FROM " + table_info +" WHERE NAME LIKE '" + bookName +"%'";
			sql2 = "DELETE FROM " + table_img +" WHERE NAME LIKE '" + bookName +"%'";
			sql3 = "DELETE FROM " + table_mp3 +" WHERE NAME LIKE '" + bookName +"%'";
			sql4 = "DELETE FROM " + table_updateTime +" WHERE NAME LIKE '" + bookName +"%'";
			db.execSQL(sql1);
			db.execSQL(sql2);
			db.execSQL(sql3);
			db.execSQL(sql4);
		}
		mCursor.close();
	}
	/*
	 * 清空表数据
	 */
	public void deleteListData(){
		String sql1 = "";
		sql1 = "delete from " + table_img ;
		db.execSQL(sql1);
		String sql2 = "";
		sql2 = "delete from " + table_mp3 ;
		db.execSQL(sql2);
		String sql3 = "";
		sql3 = "delete from " + table_info ;
		db.execSQL(sql3);
		String sql4 = "";
		sql4 = "delete from " + table_updateTime ;
		db.execSQL(sql4);
		
 	}
	/**
	 * 清空数据库
	 */
	public void deleteAllData(){
		String sql = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		sql = "DROP TABLE " + table_img;
		sql2 = "DROP TABLE " + table_mp3;
		sql3 = "DROP TABLE " + table_info;
		sql4 = "DROP TABLE " + table_updateTime;
		db.execSQL(sql);
		db.execSQL(sql2);
		db.execSQL(sql3);
		db.execSQL(sql4);
	}
}
