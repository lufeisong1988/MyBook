package com.starbaby.diyBook.clientbean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientcommon.StringUtils;



public class Post extends Entity{
	private int tid;
	private int pid;
	private int authorid;
	private int picCount;
	private int tag;
	private int type;
	private String views;
	private String replies;
	private String message;
	private String author;
	private String dateline;
	private String avatarurl;
	private String picURL;
	private String source;
	
	private List<Comment> comments = new ArrayList<Comment>();
	
	private File   imgFile;
	
	private int displayorder;
	
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public int getAuthorid() {
		return authorid;
	}
	public void setAuthorid(int authorid) {
		this.authorid = authorid;
	}
	public String getViews() {
		return views;
	}
	public void setViews(String views) {
		this.views = views;
	}
	public String getReplies() {
		return replies;
	}
	public void setReplies(String replies) {
		this.replies = replies;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getDateline() {
		return dateline;
	}
	public void setDateline(String dateline) {
		this.dateline = dateline;
	}
	public int getDisplayorder() {
		return displayorder;
	}
	public void setDisplayorder(int displayorder) {
		this.displayorder = displayorder;
	}
	public String getAvatarurl() {
		return avatarurl;
	}
	public void setAvatarurl(String avatarurl) {
		this.avatarurl = avatarurl;
	}
	public File getImgFile() {
		return imgFile;
	}
	public void setImgFile(File imgFile) {
		this.imgFile = imgFile;
	}
	public int getPicCount() {
		return picCount;
	}
	public void setPicCount(int picCount) {
		this.picCount = picCount;
	}
	public String getPicURL() {
		return picURL;
	}
	public void setPicURL(String picURL) {
		this.picURL = picURL;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public static Post parse(String str) throws AppException  { //,String picUrl
 		Post post =  new Post();
		try {
			if(StringUtils.isEmpty(str)) return post;
			//JSONTokener jsonParser = new JSONTokener(str);
			JSONObject object = new JSONObject(str);//(JSONObject)jsonParser.nextValue();
			int msg = object.getInt("msg");
			if(msg==1){
				post.setTid(object.getInt("tid"));
				post.setPid(object.getInt("pid"));
			/*	if(!StringUtils.isEmpty(picUrl)){
					post.setPicCount(1);
					post.setPicURL(picUrl);
				}*/
			}
			post.setMsg(msg);
		}  catch (JSONException e) {
			e.printStackTrace();
			throw AppException.json(e);
		}
		return post;
	}
	
	
	public static int parseDelPost(String str) throws AppException  {
		int result = 0;
		try {
			if(StringUtils.isEmpty(str)) return result;
			/*JSONTokener jsonParser = new JSONTokener(str);
			JSONObject object = (JSONObject)jsonParser.nextValue();*/
			JSONObject object = new JSONObject(str);
			result = object.getInt("msg");
		}  catch (JSONException e) {
			e.printStackTrace();
			throw AppException.json(e);
		}
		return result;
	}
	
	
}
