package com.starbaby.diyBook.clientbean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientcommon.StringUtils;

 


public class FriendPost extends Entity {
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
	private String source;
	private boolean showView = false;
	
/*	private List<Picture> pics = new ArrayList<Picture>();*/
	private List<Comment> comments = new ArrayList<Comment>();
	private ArrayList<String>  smallPics = new ArrayList<String>();
	private ArrayList<String>  bigPics = new ArrayList<String>();
	
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
/*	public File getImgFile() {
		return imgFile;
	}
	public void setImgFile(File imgFile) {
		this.imgFile = imgFile;
	}*/
	public int getPicCount() {
		return picCount;
	}
	public void setPicCount(int picCount) {
		this.picCount = picCount;
	}
 
	/*public List<Picture> getPics() {
		return pics;
	}
	public void setPics(List<Picture> pics) {
		this.pics = pics;
	}*/
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
	public ArrayList<String> getSmallPics() {
		return smallPics;
	}
	public void setSmallPics(ArrayList<String> smallPics) {
		this.smallPics = smallPics;
	}
	public ArrayList<String> getBigPics() {
		return bigPics;
	}
	public void setBigPics(ArrayList<String> bigPics) {
		this.bigPics = bigPics;
	}
	public boolean isShowView() {
		return showView;
	}
	public void setShowView(boolean showView) {
		this.showView = showView;
	}
	
	public static FriendPost parse(String str) throws AppException  {
 		FriendPost post =  new FriendPost();
		try {
			if(StringUtils.isEmpty(str)) return post;
			//JSONTokener jsonParser = new JSONTokener(str);
			JSONObject object = new JSONObject(str);//(JSONObject)jsonParser.nextValue();
			int msg = object.getInt("msg");
			if(msg==1){
				post.setTid(object.getInt("tid"));
				post.setPid(object.getInt("pid"));
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
