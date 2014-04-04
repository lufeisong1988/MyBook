package com.starbaby.diyBook.clientbean;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.starbaby.diyBook.clientapp.AppException;

 

public class User extends Entity {
	private int uid;
	private String uname;
	private String passwd;
	private String face;
	private String email;
	private int result;
/*	private String  birthday;*/
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getFace() {
		return face;
	}
	public void setFace(String face) {
		this.face = face;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
/*	
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	*/
	
	public static User parse(String str) throws AppException  {
		if(str == null){
			return null;
		}
	   System.out.println("parse str=="+str);
		User user = null;
		try {
			JSONTokener jsonParser = new JSONTokener(str);
			JSONObject object = (JSONObject)jsonParser.nextValue();
			int msg = object.getInt("msg");
			if(user==null){
				user = new User();
			}
			if(msg==1){
				 user.setUid(object.getInt("uid")) ;
				 user.setUname(object.getString("username"));
				 user.setEmail(object.getString("email")) ;
				 user.setFace(object.getString("avatar"));
			}
			user.setResult(msg);
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.json(e);
		}
		return user;
	}
 
	
	public static User parseRegister(String str) throws AppException  {
		if(str == null){
			return null;
		}
		User user = null;
	    //System.out.println("parseRegister str=="+str);
		try {
			JSONTokener jsonParser = new JSONTokener(str);
			JSONObject object = (JSONObject)jsonParser.nextValue();
			int msg = object.getInt("msg");
			if(user==null){
				user = new User();
			}
			if(msg==1){
				 user.setUid(object.getInt("uid")) ;
				 user.setFace(object.getString("avatar"));
			}
			user.setResult(msg);
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.json(e);
		}
		return user;
	}

	public static String parseBirthday(String str) throws AppException{
		//System.out.println("parseBirthday==="+str);
		String result ="";
		try{
			JSONTokener jsonParser = new JSONTokener(str);
			JSONObject object = (JSONObject)jsonParser.nextValue();
			result = object.getString("birthday");
		}catch (JSONException e) {
			e.printStackTrace();
			throw AppException.json(e);
		}
		return result;
	}
}
