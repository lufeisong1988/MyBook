package com.starbaby.diyBook.clientbean;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;



public class CommentList extends Entity {
	
/*	public final static int CATALOG_comment=1;
	public final static int CATALOG_MESSAGE = 2;//
*/	
	private int  pageSize;
	private int  msg;
	private  HashMap<String,Object> commentMap =new HashMap<String,Object>();//
	private  List<Comment> commentList = new ArrayList<Comment>();
 
	public HashMap<String, Object> getCommentMap() {
		return commentMap;
	}

	public void setCommentMap(HashMap<String, Object> commentMap) {
		this.commentMap = commentMap;
	}

	public   List<Comment> getCommentList() {
		return commentList;
	}

	public void setCommentList(List<Comment> commentList) {
		  commentList = commentList;
	}

	public int getMsg() {
		return msg;
	}

	public void setMsg(int msg) {
		this.msg = msg;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public static CommentList  parse(String str) throws AppException{
		CommentList commentList = new CommentList();
		JSONObject  commentListJson;
		try {
			commentListJson = new JSONObject(str);
		    commentList.msg=commentListJson.getInt("msg");
		 	if(commentList.msg==-2) return commentList; //发生错误直接返回
		 	
				commentList.pageSize=commentListJson.getInt("pagesize")/AppContext.PAGE_SIZE;
				JSONArray comments = commentListJson.getJSONArray("datalist");
				for(int i=0;i<comments.length();i++){
					Comment comment = new Comment();
					JSONObject result = comments.getJSONObject(i);
					comment.setPid(result.getInt("pid"));
					comment.setMessage(result.getString("message"));
					comment.setAuthor(result.getString("author"));
					comment.setAuthorid(result.getInt("authorid"));
					comment.setDateline(result.getString("dateline"));
					comment.setAvatarurl(result.getString("avatarurl"));
					comment.setViews(result.getInt("views"));
					comment.setReplies(result.getInt("replies"));
					if(result.getInt("first")==1){
						 commentList.getCommentMap().put("tcomment", comment);
					 }else{ 
						 commentList.getCommentList().add(comment);
					 }
				}
				 
		} catch (JSONException e) {
			throw AppException.json(e);
		}
		return  commentList;
	}
}
