package com.starbaby.diyBook.clientbean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientcommon.StringUtils;

 
public class FansList extends Entity {
	private List<Fans> fansList = new ArrayList<Fans>();
	private int msg;
	private int fansSize;
	private int noteSize;
	
	public List<Fans> getFansList() {
		return fansList;
	}

	public int getMsg() {
		return msg;
	}

	public int getFansSize() {
		return fansSize;
	}
	
	public int getNoteSize() {
		return noteSize;
	}

	public static FansList parseAttention(String str) throws AppException{
		System.out.println("AttentionList String =="+str);
		FansList  attentionList = new FansList();
		JSONObject  attentionListJson;
		try {
			attentionListJson = new JSONObject(str);
			if(AppContext.PAGE_SIZE>0)
			attentionList.noteSize=attentionListJson.getInt("notesize")/AppContext.PAGE_SIZE;
 
			attentionList.msg = attentionListJson.getInt("msg");
			JSONArray fans = attentionListJson.getJSONArray("notelist");
			for(int i=0;i<fans.length();i++){
				Fans fan = new Fans();
				JSONObject result = fans.getJSONObject(i);
				fan.setUid(result.getInt("uid"));
				fan.setUserName(result.getString("username"));
				fan.setAvatar(result.getString("avatar"));
				fan.setIsNoted(1);
				attentionList.getFansList().add(fan);
			}
		}catch(Exception e){
			throw AppException.json(e);
		}
		return attentionList;
	}
	
	public static FansList parse(String str) throws AppException{
	    System.out.println("FansList parse==="+str);
		FansList fansList = new FansList();
		JSONObject  fansListJson;
		try {
			fansListJson = new JSONObject(str);
			fansList.fansSize=fansListJson.getInt("fanssize")/AppContext.PAGE_SIZE;
			 
			fansList.msg = fansListJson.getInt("msg");
			if(fansList.msg!=0){
				JSONArray fans = fansListJson.getJSONArray("fanslist");
				for(int i=0;i<fans.length();i++){
					Fans fan = new Fans();
					JSONObject result = fans.getJSONObject(i);
					fan.setUid(result.getInt("uid"));
					fan.setUserName(result.getString("username"));
					fan.setAvatar(result.getString("avatar"));
					fan.setIsNoted(StringUtils.toInt(result.getString("is_noted")));
					fansList.getFansList().add(fan);
				}
			}
		}catch(Exception e){
			throw AppException.json(e);
		}
		return fansList;
	}
}
