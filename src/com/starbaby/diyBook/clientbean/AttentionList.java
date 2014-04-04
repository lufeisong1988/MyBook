package com.starbaby.diyBook.clientbean;
/*package com.starbaby.friendsbook.bean;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
 

public class AttentionList extends Entity {
	private int noteSize;
	//private String userName;
	private ArrayList<Fans> fansList = new ArrayList<Fans>();
	private PersonalTotal total;
	
	public PersonalTotal getTotal() {
		return total;
	}

	public int getNoteSize() {
		return noteSize;
	}

	public String getUserName() {
		return userName;
	}

	public ArrayList<Fans> getFansList() {
		return fansList;
	}
	

	public static AttentionList parseAttention(String str) throws AppException{
		//System.out.println("AttentionList String =="+str);
		AttentionList  attentionList = new AttentionList();
		PinYin py = new PinYin();
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
				fan.setDetail(py.getPinyin(fan.getUserName()));
				System.out.println("fan.getDetail()====="+fan.getDetail());
				fan.setAvatar(result.getString("avatar"));
				fan.setIsNoted(1);
				attentionList.getFansList().add(fan);
			}
		}catch(Exception e){
			throw AppException.json(e);
		}
		return attentionList;
	}
	
	public static AttentionList parseContact(String str) throws AppException{
		//System.out.println("AttentionList String =="+str);
		AttentionList  attentionList = new AttentionList();
		PinYin py = new PinYin();
		JSONObject  attentionListJson;
		try {
			attentionListJson = new JSONObject(str);
			attentionList.msg = attentionListJson.getInt("msg");
			if(attentionList.msg>0){
				JSONArray fans = attentionListJson.getJSONArray("atuserlist");
				for(int i=0;i<fans.length();i++){
					Fans fan = new Fans();
					JSONObject result = fans.getJSONObject(i);
					fan.setUid(result.getInt("uid"));
					fan.setUserName(result.getString("username"));
					fan.setDetail(py.getPinyin(fan.getUserName()));
					fan.setCatalog(py.converterToFirstSpell(fan.getUserName()));
					fan.setAvatar(result.getString("avatar"));
					attentionList.getFansList().add(fan);
				}
			}
		}catch(Exception e){
			throw AppException.json(e);
		}
		return attentionList;
	}
}
*/