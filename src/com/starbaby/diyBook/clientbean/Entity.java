package com.starbaby.diyBook.clientbean;

import java.text.SimpleDateFormat;

import org.json.JSONObject;

import com.starbaby.diyBook.clientapp.AppException;


/**
 * 实体类
 * @author stone(fanlei123126@163.com)
 * @version 1.0
 * @created 2013-3-29
 */
public abstract class Entity extends Base {

	public final static SimpleDateFormat SDF_IN = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final static SimpleDateFormat SDF_OUT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	protected int id;

	protected int msg;
	
	protected int newmsgsize;
	
	protected String cacheKey;
	
	public int getMsg() {
		return msg;
	}

	public void setMsg(int msg) {
		this.msg = msg;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getCacheKey() {
		return cacheKey;
	}

	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}

	public int getNewmsgsize() {
		return newmsgsize;
	}

	public void setNewmsgsize(int newmsgsize) {
		this.newmsgsize = newmsgsize;
	}
	
	public static int parseMsg(String str) throws AppException{
		//System.out.println("str=="+str);
		int result = 0;
		try{
			JSONObject object = new JSONObject(str);
			result = object.getInt("msg");
		}catch(Exception e){
			e.printStackTrace();
			throw AppException.json(e);
		}
		return result;
	}
	
}
