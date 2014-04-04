package com.starbaby.diyBook.clientbean;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.starbaby.diyBook.clientapp.AppException;

 

public class Notice extends Entity{
	private int sendUid;
	private String sendUname;
	private String avatar;
	private int refid;
	private String message;
	private String refmsg;
	private int    typeId;
	private String deteline;
	private int ignore;
	
	public int getSendUid() {
		return sendUid;
	}
	public void setSendUid(int sendUid) {
		this.sendUid = sendUid;
	}
	public String getSendUname() {
		return sendUname;
	}
	public void setSendUname(String sendUname) {
		this.sendUname = sendUname;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public int getRefid() {
		return refid;
	}
	public void setRefid(int refid) {
		this.refid = refid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getRefmsg() {
		return refmsg;
	}
	public void setRefmsg(String refmsg) {
		this.refmsg = refmsg;
	}
	public int getTypeId() {
		return typeId;
	}
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	public String getDeteline() {
		return deteline;
	}
	public void setDeteline(String deteline) {
		this.deteline = deteline;
	}
	public int getIgnore() {
		return ignore;
	}
	public void setIgnore(int ignore) {
		this.ignore = ignore;
	}
	
	public static int parse(String str) throws AppException{
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
