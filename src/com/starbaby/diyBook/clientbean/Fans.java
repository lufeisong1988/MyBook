package com.starbaby.diyBook.clientbean;

import org.json.JSONObject;

import com.starbaby.diyBook.clientapp.AppException;


public class Fans extends Entity {
	private int uid;
	private String userName;
	private String detail;
	private String avatar;
	private String catalog;
	private int isNoted;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public int getIsNoted() {
		return isNoted;
	}
	public void setIsNoted(int isNoted) {
		this.isNoted = isNoted;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getCatalog() {
		return catalog;
	}
	public void setCatalog(String catalog) {
		this.catalog = catalog;
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
