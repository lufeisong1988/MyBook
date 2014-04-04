package com.starbaby.diyBook.clientbean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientcommon.StringUtils;

public class Comment extends Entity {
	private int pid;
	private int first;
	private int views;
	private int authorid;
	private int replies;
	private int picCount;
	private String message;
	private String author;
	private String dateline;
	private String avatarurl;
	private List<Picture> pics = new ArrayList<Picture>();
	private ArrayList<String> smallImg = new ArrayList<String>();
	private ArrayList<String> bigImg = new ArrayList<String>();

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public int getReplies() {
		return replies;
	}

	public void setReplies(int replies) {
		this.replies = replies;
	}

	public int getAuthorid() {
		return authorid;
	}

	public void setAuthorid(int authorid) {
		this.authorid = authorid;
	}

	public void setReplies(Integer replies) {
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

	public String getAvatarurl() {
		return avatarurl;
	}

	public void setAvatarurl(String avatarurl) {
		this.avatarurl = avatarurl;
	}

	public int getPicCount() {
		return picCount;
	}

	public void setPicCount(int picCount) {
		this.picCount = picCount;
	}

	public List<Picture> getPics() {
		return pics;
	}

	public void setPics(List<Picture> pics) {
		this.pics = pics;
	}

	public ArrayList<String> getSmallImg() {
		return smallImg;
	}

	public ArrayList<String> getBigImg() {
		return bigImg;
	}

	public static Comment parse(String str) throws AppException {
		Comment Comment = null;
		try {
			if (StringUtils.isEmpty(str))
				return null;
			JSONObject object = new JSONObject(str);
			int msg = object.getInt("msg");
			if (Comment == null) {
				Comment = new Comment();
			}
			if (msg == 1) {
				Comment.setPid(object.getInt("pid"));
			}
			Comment.setMsg(msg);
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.json(e);
		}
		return Comment;
	}
}
