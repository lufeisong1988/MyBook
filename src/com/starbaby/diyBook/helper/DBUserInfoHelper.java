package com.starbaby.diyBook.helper;
/**
 * 记录登入时间。用来判断8小时后退出登入
 */
import com.starbaby.diyBook.utils.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBUserInfoHelper {
	private static String DATABASE_NAME = "diyBook_UserInfo.db";
	private static String table_enterTime = "EnterTime_info";
	private static String table_coverUrl = "CoverUrl_info";
	private static String table_local = "LocalBook_info";
	private static String table_collect = "CollectBook_info";
	Context mContext;
	SQLiteDatabase db;
	public DBUserInfoHelper(Context mContext){
		this.mContext = mContext;
		db = mContext.openOrCreateDatabase(DATABASE_NAME, mContext.MODE_PRIVATE, null);
		createTable();
	}
	/**
	 * 创建表
	 */
	public void createTable(){
		if(isTableExist(table_coverUrl)){
		}else{
			db.execSQL("CREATE TABLE "+ table_coverUrl + " ("+ "_id INTEGER PRIMARY KEY autoincrement," + "UID INTEGER," + "NAME TEXT,"+ "ID TEXT,"+ "TIME TEXT,"+ "COVERURL TEXT" + ");");
		}
		if(isTableExist(table_enterTime)){
		}else{
			db.execSQL("CREATE TABLE "+ table_enterTime + " ("+ "_id INTEGER PRIMARY KEY autoincrement," + "UID INTEGER,"+ "TIME TEXT" + ");");
		}
		if(isTableExist(table_local)){
			
		}else{
			db.execSQL("CREATE TABLE "+ table_local + " ("+ "_id INTEGER PRIMARY KEY autoincrement," + "TPL_ID TEXT ," + "NAME TEXT," + "COVERURL TEXT," + "TIME TEXT" + ");");
		}
		if(isTableExist(table_collect)){
			
		}else{
			db.execSQL("CREATE TABLE "+ table_collect + " ("+ "_id INTEGER PRIMARY KEY autoincrement," + "UID INTEGER," + "NAME TEXT,"+ "ID TEXT,"+ "TIME TEXT,"+ "COVERURL TEXT" + ");");
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
	/*
	 * 存入登入时间
	 */
	public void saveEnterTime(int uid,String time){
		String sql = null;
		if(time != null && !time.equals("")){
			sql = "insert into "+ table_enterTime +" values ( null,'" + uid +"','" + time  +"')";
			db.execSQL(sql);
		}
	}
	/*
	 * 获取时间
	 */
	public Cursor getEnterTime(int uid){
		String name = "UID";
		Cursor mCursor = db.query(table_enterTime, new String[]{"_id","UID","TIME"}, name + "='" + uid +"'", null, null, null, null) ;
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	/*
	 * 清理 当前user的登入时间
	 */
	public void deleteEnterTime(int uid){
		String sql = "";
		sql = "delete from " + table_enterTime + " where UID =" + "'" +uid +"'";
		db.execSQL(sql);
	}
	/*
	 * 存入个人账号下作品封面
	 */
	public void saveCoverUrl(int uid,String name,String id,String time,String coverUrl ){
		String sql ="";
		sql = "insert into "+ table_coverUrl +" values ( null,'" + uid +"','" + name +"','" + id +"','" + time +"','"+ coverUrl  +"')";
		db.execSQL(sql);
	}
	/*
	 *获取个人账号下作品封面的url
	 */
	public Cursor getCoverUrl(int uid){
		String name = "UID";
		Cursor mCursor = db.query(table_coverUrl, new String[]{"_id","UID","NAME","ID","TIME","COVERURL"}, name + "='" + uid +"'", null, null, null, null) ;
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	/*
	 * 删除我的作品 下的书本信息
	 */
	public void deleteMyBook(String tpl_id){
		String sql = "";
		sql = "delete from " + table_coverUrl + " where ID = '" + tpl_id + "'";
		db.execSQL(sql);
	}
	/*
	 *  清空个人账号下的封面数据
	 */
	public void deleteUserInfo(int uid){
		String sql = "";
		sql = "delete from " + table_coverUrl + " where UID = '" + uid + "'";
		db.execSQL(sql);
	}
	
	/*
	 * 1.存入个人中心 本地书本信息
	 * 2.获取个人中心 本地书本信息
	 * 3.删除个人中心 本地书本信息(直接删除 ，转存进收藏)
	 * 4.判断是否已经存在本地
	 */
	public void saveLocalBook(String tpl_id,String bookName ,String coverUrl,String time){
		String sql = "";
		sql = "insert into " + table_local + " values ( null,'" + tpl_id + "','" + bookName + "','" + coverUrl + "','" + time +"')";
		db.execSQL(sql);
	}
	public Cursor getLocalBook(){
		Cursor cursor = null;
		cursor = db.query(table_local, null, null, null, null, null,null);
		return cursor;
	}
	public void deleteLocalBook(String tpl_id){
		String sql = "";
		sql = "delete from " + table_local + " where TPL_ID = '" + tpl_id + "'";
		db.execSQL(sql);
	}
	public boolean bExist(String tpl_id){
		Cursor mCursor;
		mCursor = db.query(table_local, new String[]{"_id","TPL_ID","NAME","COVERURL","TIME"}, "TPL_ID = '" + tpl_id + "'" , null, null, null, null);
		if(mCursor.getCount() > 0){
			mCursor.close();
			return true;
		}else{
			mCursor.close();
			return false;
		}
	}
	/*
	 * collect数据库
	 * 1.存入collect书本数据
	 * 2.读取collect书本数据
	 * 3.删除collect数据（更新操作）
	 * 4.判断要收藏的书本已经存在
	 */
	public void saveCollectBook(int uid,String name,String id,String time,String coverUrl){
		String sql = "";
		sql = "insert into " + table_collect + " values ( null ,'" + uid + "','" +  name + "','" + id + "','" + time + "','" + coverUrl +  "')";
		db.execSQL(sql);
	}
	public Cursor getCollectBook(){
		Cursor cursor = null;
		cursor = db.query(table_collect, null, null, null, null, null, null);
		return cursor;
	}
	public void deleteCollectBook(){
		String sql = "";
		sql = "delete from " + table_collect + " where UID = '" + Utils.collect_uid +"'";  
		db.execSQL(sql);
	}
	public boolean bCollect(String tpl_id){
		Cursor cursor = null;
		cursor = db.query(table_collect, new String[]{"_id","UID","NAME","ID","TIME","COVERURL"}, "ID = '" + tpl_id + "'", null, null, null, null);
		if(cursor.getCount() > 0){
			return true;
		}
		return false;
	}
	/*
	 * 查询个人中心下阅读过的书本信息（个人中心与其他书籍分在2个数据库中）
	 * 
	 */
	public Cursor getLocalBookInfo(String tpl_id){
		Cursor cursor = null;
		cursor = db.query(table_coverUrl, new String[]{"_id","UID","NAME","ID","TIME","COVERURL"}, "ID = '" + tpl_id + "'", null, null, null, null);
		return cursor;
	}
//	/*
//	 * 1.通过个人作品表 tpl_id 来获取保存本地的coverUrl
//	 * 2.通过收藏表tpl_id 来获取保存本地的coverUrl
//	 */
//	public Cursor getLocalCoverCenter(String tpl_id){
//		Cursor cursor = null;
//		cursor = db.query(table_coverUrl, new String[]{"_id","UID","NAME","ID","TIME","COVERURL"}, "ID = '" + tpl_id + "'", null, null, null, null);
//		return cursor;
//	}
	public Cursor getLocalCoverCollect(String tpl_id){
		Cursor cursor = null;
		cursor = db.query(table_collect, new String[]{"_id","UID","NAME","ID","TIME","COVERURL"}, "ID = '" + tpl_id + "'", null, null, null, null);
		return cursor;
	}
	/*
	 * 清空缓存时删除 本地和collect信息
	 */
	public void deleteLocalAndCollect(){
		String sql3 = "";
		sql3 = "delete from " + table_local;
		db.execSQL(sql3);
	}
	/*
	 * 清空所有数据
	 */
	public void deleteAll(){
		String sql1 = "";
		String sql2 = "";
		String sql3 = "";
		String sql4 = "";
		sql1 = "DROP TABLE " + table_enterTime;
		sql2 = "DROP TABLE " + table_coverUrl;
		sql3 = "DROP TABLE " + table_local;
		sql4 = "DROP TABLE " + table_collect;
		db.execSQL(sql1);
		db.execSQL(sql2);
		db.execSQL(sql3);
		db.execSQL(sql4);
	}
}
