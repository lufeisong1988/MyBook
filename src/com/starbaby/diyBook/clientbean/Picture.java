package com.starbaby.diyBook.clientbean;

import org.json.JSONException;
import org.json.JSONObject;

import com.starbaby.diyBook.clientapp.AppException;



public class Picture extends Entity {
	
	private String small;
	private String big;
	private String imgUrl;
	private int    result;
	
	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}
	public String getSmall() {
		return small;
	}

	public void setSmall(String small) {
		this.small = small;
	}

	public String getBig() {
		return big;
	}

	public void setBig(String big) {
		this.big = big;
	}

	
	public static Picture parseAvatar(String str) throws AppException{
		if(str == null){
			return null;
		}
		Picture picture = null;
		try{
			JSONObject object = new JSONObject(str);
			int msg = object.getInt("msg");
			if(picture == null){
				picture = new Picture();
			}
			if(msg==1){
				picture.setImgUrl(object.getString("imageurl"));
			}
			picture.setResult(msg);
		} catch(JSONException e){
			e.printStackTrace();
			throw AppException.json(e);
		}
		return picture;
	}
	
	public static Picture parseUpdate(String str) throws AppException{
		if(str == null){
			return null;
		}
	 	//System.out.println("str=="+str);
		Picture picture = null;
		try{
			JSONObject object = new JSONObject(str);
			int msg = object.getInt("msg");
			if(picture == null){
				picture = new Picture();
			}
			if(msg>0){
				picture.setImgUrl(object.getString("avatar"));
			}
			picture.setResult(msg);
		} catch(JSONException e){
			e.printStackTrace();
			throw AppException.json(e);
		}
		return picture;
	}
	
}
