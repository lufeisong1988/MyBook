package com.starbaby.diyBook.clientbean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;

 

/**
 * 帖子列表实体类
 * @author stone(fanlei123126@163.com)
 * @version 1.0
 * @created 2013-3-29
 */
public class PostList extends Entity{

	public final static int POST_TYPE_ALL = 0; //全部
	public final static int POST_TYPE_CHAT = 1; //闲聊
	public final static int POST_TYPE_DIARY = 2; //日志
	public final static int POST_TYPE_MOMENT = 3;//精彩瞬间
	
	private int pageSize;
	private int isNoted;
	private List<Post> postlist = new ArrayList<Post>();
	
	public int getPageSize() {
		return pageSize;
	}
	public List<Post> getPostlist() {
		return postlist;
	}

	public int getIsNoted() {
		return isNoted;
	}
 
	
	public static PostList parse(String str,int pageIndex,int catalog) throws AppException{
	     //System.out.println("PostList str="+str);
		PostList postList = new PostList();
		JSONObject  postListJson;
		try {
			postListJson = new JSONObject(str);
			if(AppContext.PAGE_SIZE>0)
			postList.pageSize=postListJson.getInt("pagesize")/AppContext.PAGE_SIZE;
			postList.newmsgsize = postListJson.getInt("newmsgsize");
			JSONArray posts = postListJson.getJSONArray("datalist");
			for(int i=0;i<posts.length();i++){
				Post post = new Post();
				JSONObject result = posts.getJSONObject(i);
				if(pageIndex==1 && i<=2)  //不显示评论标记
				{
					post.setTag(1);
				}else {
					post.setTag(0);
				}
				post.setType(catalog);
				post.setTid(result.getInt("tid"));
				post.setPid(result.getInt("pid"));
				post.setMessage(result.getString("message"));
				post.setAuthor(result.getString("author"));
				post.setAuthorid(result.getInt("authorid"));
				post.setDateline(result.getString("dateline"));
				post.setViews(result.getString("views"));
				post.setReplies(result.getString("replies"));
				post.setDisplayorder(result.getInt("displayorder"));
				post.setAvatarurl(result.getString("avatarurl"));
				post.setPicCount(result.getInt("pic_count"));
				post.setSource(result.getString("source"));
				if(post.getPicCount()>0){
					JSONArray pics = result.getJSONArray("pics");
					if(pics.length()>0){
						JSONObject picsObject = pics.getJSONObject(0);
						post.setPicURL(picsObject.getString("image"));
					}
				/*	for(int j=0;j<pics.length();j++){
						JSONObject picsObject = pics.getJSONObject(j);
						post.getPicURL().add(picsObject.getString("image"));
					}*/
				}
				JSONArray comments = result.getJSONArray("comments");
				for(int j=0;j<comments.length();j++){
					Comment comment = new Comment();
					JSONObject comObject = comments.getJSONObject(j);
					comment.setPid(comObject.getInt("pid"));
					comment.setMessage(comObject.getString("message"));
					comment.setAuthor(comObject.getString("author") );
					comment.setAuthorid(comObject.getInt("authorid"));
					comment.setDateline(comObject.getString("dateline"));
					comment.setAvatarurl(comObject.getString("avatarurl"));
					post.getComments().add(comment);
				}
				postList.getPostlist().add(post);
			}
		} catch (JSONException e) {
			throw AppException.json(e);
		}
		return  postList;
	}
	
	
	public static PostList parseMyPost(String str) throws AppException{
		//System.out.println("parseMyPost=="+str);
		PostList postList = new PostList();
		JSONObject  postListJson;
		try {
			postListJson = new JSONObject(str);
			postList.pageSize=postListJson.getInt("pagesize")/AppContext.PAGE_SIZE;
			postList.isNoted = postListJson.getInt("is_noted");
		 
			JSONArray posts = postListJson.getJSONArray("datalist");
			for(int i=0;i<posts.length();i++){
				Post post = new Post();
				JSONObject result = posts.getJSONObject(i);
				post.setTid(result.getInt("tid"));
				post.setPid(result.getInt("pid"));
				post.setMessage(result.getString("message"));
				post.setAuthor(result.getString("author"));
				post.setAuthorid(result.getInt("authorid"));
				post.setDateline(result.getString("dateline"));
				post.setViews(result.getString("views"));
				post.setReplies(result.getString("replies"));
				post.setDisplayorder(result.getInt("displayorder"));
				/*post.setAvatarurl(avataUrl);*/
				post.setPicCount(result.getInt("pic_count"));
				post.setSource(result.getString("source"));
				if(post.getPicCount()>0){
					JSONArray pics = result.getJSONArray("pics");
					if(pics.length()>0){
						JSONObject picsObject = pics.getJSONObject(0);
						post.setPicURL(picsObject.getString("image"));
					}
					/*for(int j=0;j<pics.length();j++){
						JSONObject picsObject = pics.getJSONObject(j);
						post.getPicURL().add(picsObject.getString("image"));
					}*/
				}
				postList.getPostlist().add(post);
			}
		} catch (JSONException e) {
			throw AppException.json(e);
		}
		return  postList;
	}
}
