package com.starbaby.diyBook.clientapi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import android.util.Log;

import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientbean.Comment;
import com.starbaby.diyBook.clientbean.CommentList;
import com.starbaby.diyBook.clientbean.Entity;
import com.starbaby.diyBook.clientbean.Fans;
import com.starbaby.diyBook.clientbean.FansList;
import com.starbaby.diyBook.clientbean.FriendPost;
import com.starbaby.diyBook.clientbean.FriendPostList;
import com.starbaby.diyBook.clientbean.Notice;
import com.starbaby.diyBook.clientbean.NoticeList;
import com.starbaby.diyBook.clientbean.Picture;
import com.starbaby.diyBook.clientbean.PostList;
import com.starbaby.diyBook.clientbean.URLs;
import com.starbaby.diyBook.clientbean.User;
import com.starbaby.diyBook.clientcommon.StringUtils;


public class ApiClient {
	public static final String UTF_8 ="UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";
	public static final String HOST = "www.starbaby.cn";
	
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 3;
	
	private final static int SYS = 2;
	private static String appUserAgent;
	private final static String IMG = "img";
	/**
	 * 用户登录
	 * @param appContext
	 * @param username
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public static User login(AppContext appContext,String username,String pwd) throws AppException{
		Map<String,Object> params = new HashMap<String,Object>() ;
		System.out.println("username==="+username+";pwd=="+pwd);
		params.put("username",username);
		params.put("pwd",pwd);
		//params.put("keep_login", 1);
 
		try {
			return User.parse(http_post(appContext, URLs.LOGIN_VALIDATE_HTTP,params,null));
		} catch (Exception e) {
			if(e instanceof AppException){
				throw (AppException)e;
			}
			throw AppException.network(e);
		}
		
	}
	/**
	 * 发帖子
	 * @param appContext
	 * @param uid
	 * @param pwd
	 * @param content
	 * @return
	 * @throws AppException
	 */
	public static FriendPost pubPost(AppContext appContext,int uid,String pwd,String content,Map<String,String> imgUrl,int typeId) throws AppException{
		Map<String,Object> params =  new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("pwd", pwd);
		params.put("title", "");
		params.put("txt", content);
		params.put("sys", SYS);
		params.put("typeid",typeId);
		if(imgUrl.size()>0){
			StringBuffer buffer = new StringBuffer();
			String split="";
			Iterator<Entry<String, String>> iterator = imgUrl.entrySet().iterator();  
			while(iterator.hasNext()){
				Entry<String, String>  entry=(Entry<String, String>)iterator.next(); 
				buffer.append(split);
				buffer.append(entry.getValue());
				if(StringUtils.isEmpty(split)){
					split=";";
				}
			}
			params.put("imgs", buffer.toString());
		}
		try{
			return FriendPost.parse(http_post(appContext,URLs.POST_PUSH,params,null));
		}catch(Exception e){
			if(e instanceof AppException){
				throw (AppException)e;
			}
			throw AppException.network(e);
		}
	}
	/**
	 * 获取评论列表
	 * @param catalog 1新闻  2帖子  3动弹  4动态
	 * @param id
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @throws AppException
	 */
	public static CommentList getCommentList(AppContext appContext,final int id, final int pageIndex) throws AppException {
	/*	String newUrl = _MakeURL(URLs.COMMENT_LIST, new HashMap<String, Object>(){{
			//put("catalog", catalog);
			put("id", id);
			put("pageIndex", pageIndex);
			//put("pageSize", pageSize);
		}});*/
		String newUrl =  URLs.COMMENT_LIST+"/"+id+"/"+pageIndex;
		//System.out.println("newUrl==="+newUrl);
		try{
			return CommentList.parse(http_get(appContext, newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	public static FansList getAttentionList(AppContext appContext,int uid,int pageNum) throws AppException{
		String url  = URLs.GET_ATTENTION_LIST+uid+"/"+pageNum;
		try {
			return FansList.parseAttention(http_get(appContext,url));
		} catch (AppException e) {
			if(e instanceof AppException){
				throw (AppException)e;
			}
			throw AppException.network(e);
		}
	}
	
	public static FansList getFansList(AppContext appContext,int uid,int pageNum) throws AppException{
		String url  = URLs.GET_FANS_LIST+uid+"/"+pageNum;
		try {
			return FansList.parse(http_get(appContext,url));
		} catch (AppException e) {
			if(e instanceof AppException){
				throw (AppException)e;
			}
			throw AppException.network(e);
		}
	}
	
	
	public static int cancleAttention(AppContext appContext,int uid,String pwd,int auid) throws AppException{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("pwd", pwd);
		params.put("auid", auid);
		//System.out.println("uid="+uid+",pwd="+pwd+",auid="+auid);
		try {
			return Fans.parseMsg(http_post(appContext, URLs.POST_FENSI_CANCLE, params, null));
		} catch (AppException e) {
			if(e instanceof AppException){
				throw (AppException)e;
			}
			throw AppException.network(e);
		}
	}
	
	public static int delNotice(AppContext appContext,final int uid,final String pwd,final int id) throws AppException{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("pwd", pwd);
		params.put("id", id);
		
		try {
			return Notice.parse(http_post(appContext,URLs.POST_MESSAGE_DEL,params,null));
		} catch (AppException e) {
			if(e instanceof AppException){
				throw (AppException)e;
			}
			throw AppException.network(e);
		}
	}

	
	
	private static String getUserAgent(AppContext appContext){
		if(appUserAgent == null || appUserAgent == ""){
			StringBuilder sb = new StringBuilder(HOST);
			sb.append("/"+appContext.getPackageInfo().versionName+"_"+appContext.getPackageInfo().versionCode);
			sb.append("/Android");
			sb.append("/"+android.os.Build.VERSION.RELEASE);
			sb.append("/"+android.os.Build.MODEL);
			sb.append("/"+appContext.getAppId());
			appUserAgent = sb.toString();
		}
		return appUserAgent;
	}
	
	private static HttpClient getHttpClient(){
		HttpClient httpClient = new HttpClient();
		
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
		//设置默认的超市重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		//设置连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		//设置读取数据超时时间
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		//设置字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}
	
	private static GetMethod getHttpGet(String url,String userAgent){//,String cookie
		GetMethod httpGet = new GetMethod(url);
		//设置请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		//httpGet.setRequestHeader("Host", URLs.HOST);
		httpGet.setRequestHeader("Connection", "Keep-Alive");
	//	httpGet.setRequestHeader("Cookie", cookie);
		httpGet.setRequestHeader("User-Agent", userAgent);
		return httpGet;
	}
	private static PostMethod getHttpPost(String url,String userAgent) { 
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", URLs.API_HOST);
		httpPost.setRequestHeader("Connection","Keep-Alive");
	    //httpPost.setRequestHeader("Cookie", cookie);
		httpPost.setRequestHeader("User-Agent", userAgent);
		return httpPost;
	}
	/**
	 * 朋友圈
	 * @param appContext
	 * @param pageIndex
	 * @param type
	 * @return
	 * @throws AppException
	 */
	public static FriendPostList getFriendList(AppContext appContext,final int pageIndex,final int uid,final int type) throws AppException {
		String newUrl =URLs.FRIEND_POST+"/"+uid+"/"+pageIndex + "/" + type;
		Log.i("newUrl",newUrl);
		//System.out.println("newUrl======"+newUrl); 
		try{
			return FriendPostList.parse(http_get(appContext,newUrl));
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	
	/**
	 * 妈妈圈
	 * @param appContext
	 * @param pageIndex
	 * @param type
	 * @return
	 * @throws AppException
	 */
	public static FriendPostList getPostList(AppContext appContext,final int catalog, final int pageIndex,int type,final int uid) throws AppException {
		String newUrl = "";
		if(type==AppContext.POST_DEFAULT_TYPE)
			newUrl = URLs.POST_DEFAULT_LIST+"/"+catalog+"/"+pageIndex+"/"+uid;
		else 
			newUrl =URLs.POST_TIME_LIST+"/"+catalog+"/"+pageIndex+"/"+uid;
		//System.out.println(newUrl);
		try{
			return FriendPostList.parse(http_get(appContext,newUrl));		
		}catch(Exception e){
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	/**
	 * 发布评论
	 * @param appContext
	 * @param uid
	 * @param pwd
	 * @param postId
	 * @param content
	 * @return
	 * @throws AppException
	 */
	public static Comment pubComment(AppContext appContext,int uid,String pwd,int tid,String content) throws AppException{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("pwd", pwd);
		params.put("id", tid);
		params.put("txt", content);
		params.put("sys", SYS);
		
		try {
			return Comment.parse(http_post(appContext,URLs.POST_COMMENT,params,null));
		} catch (AppException e) {
			if(e instanceof AppException){
				throw (AppException)e;
			}
			throw AppException.network(e);
		}
	}
	
	public static int delPost(AppContext appContext,int uid,String pwd,int tid) throws AppException{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("pwd", pwd);
		params.put("tid", tid);
		//System.out.println("uid=="+uid+";pwd=="+pwd+";tid="+tid);
		try {
			return FriendPost.parseDelPost(http_post(appContext, URLs.POST_DEL_MYPOST,params,null));
		} catch (Exception e) {
			if(e instanceof AppException){
				throw (AppException)e;
			}
			throw AppException.network(e);
		}
		
	}
	
	public static int addFenSi(AppContext appContext,int uid,String pwd,int auid) throws AppException{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("pwd", pwd);
		params.put("auid", auid);
		//System.out.println("uid="+uid+",pwd="+pwd+",auid="+auid);
		try {
			return Entity.parseMsg(http_post(appContext, URLs.POST_FENSI_ADD, params, null));
		} catch (AppException e) {
			throw (AppException) e;
		}
	}
	
	/**
	 * 我的信息实现
	 * @param appContext
	 * @param uid
	 * @return
	 * @throws AppException 
	 */
	public static NoticeList getNotices(AppContext appContext,final int catalog,final int uid,final String pwd,final int curPage) throws AppException{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("pwd", pwd);
		params.put("is_new", 2); //是否未读：0-已读，1-未读，2-全部；默认 1
		params.put("type", catalog); ////消息类型：all-全部； 默认all 1-评论主题；2-回复评论； 3-回复机构评论；     4-加关注；  5-加为好友；
		params.put("cur_page",curPage);
		 System.out.println("noticeUrlhhh=="+URLs.POST_MESSAGE_LIST+"uid="+uid+"&pwd="+pwd+"&is_new="+2+"&type="+catalog+"&cur_page="+curPage);
		try {
			return NoticeList.parseNoticeList(http_post(appContext,URLs.POST_MESSAGE_LIST,params,null));
		} catch (AppException e) {
			e.printStackTrace();
			throw e;
		}
	}
	/**
	 * 回复评论
	 * @param appContext
	 * @param uid
	 * @param pwd
	 * @param tid
	 * @param postId
	 * @param content
	 * @return
	 * @throws AppException
	 */
	public static Comment replyComment(AppContext appContext,int uid,String pwd,int tid,int postId,String content) throws AppException{
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("uid", uid);
		params.put("pwd", pwd);
		params.put("id", tid);
		params.put("cid", postId);
		params.put("txt", content);
		params.put("sys", SYS);
		
		try {
			return Comment.parse(http_post(appContext,URLs.POST_COMMENT,params,null));
		} catch (AppException e) {
			if(e instanceof AppException){
				throw (AppException)e;
			}
			throw AppException.network(e);
		}
	}
	
	/**
	 * 我/Ta 的主题列表
	 * @param appContext
	 * @param uid
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public static PostList getMyPostList(AppContext appContext,final int hisId,final int catalog,final int pageIndex,int curUid) throws AppException{
		String postUrl =  URLs.GET_MY_POST_LIST+hisId+"/"+catalog+"/"+pageIndex+"/"+curUid;
	    //System.out.println("postURL=="+postUrl);
		try {
			return PostList.parseMyPost(http_get(appContext,postUrl));
		} catch (AppException e) {
			if(e instanceof AppException)
				throw (AppException)e;
			throw AppException.network(e);
		}
	}
	/**
	 * 上传相片
	 * @param appContext
	 * @param avatar
	 * @return
	 * @throws AppException
	 */
	public static Picture updatePortrait(AppContext appContext,File avatar) throws AppException{
		Map<String,File> params = new HashMap<String,File>();
		params.put(IMG, avatar);
		//System.out.println("avatar==> "+avatar.length());
		//System.out.println("avatar==> "+avatar.getPath());
		return Picture.parseAvatar(http_post(appContext,URLs.AVATAR,null,params));
	}
	
	/**
	 * get请求URL
	 * @param url
	 * @throws AppException 
	 */
	private static String http_get(AppContext appContext, String url) throws AppException {	
		// System.out.println("get_url==> "+url);
		 //String cookie = getCookie(appContext);
		  String userAgent =  getUserAgent(appContext);
		
		HttpClient httpClient = null;
		GetMethod httpGet = null;

		String responseBody = "";
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpGet = getHttpGet(url,userAgent);	
				int statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}
				responseBody = httpGet.getResponseBodyAsString();
				break;				
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);
		
		return responseBody;
	}
	
	/**
	 * 公用post方法
	 * @param url
	 * @param params
	 * @param files
	 * @throws AppException
	 */
	private static String http_post(AppContext appContext, String url, Map<String, Object> params, Map<String,File> files) throws AppException {
	    //System.out.println("post_url==> "+url);
       // String cookie = getCookie(appContext);
	    String userAgent = getUserAgent(appContext); 
		HttpClient httpClient = null;
		PostMethod httpPost = null;
		
		//post表单参数处理
		int length = (params == null ? 0 : params.size()) + (files == null ? 0 : files.size());
		Part[] parts = new Part[length];
		int i = 0;
        if(params != null)
        for(String name : params.keySet()){
        	parts[i++] = new StringPart(name, String.valueOf(params.get(name)), UTF_8);
        	//System.out.println("parts===");
        }
        if(files != null)
        for(String file : files.keySet()){
        	try {
				parts[i++] = new FilePart(file, files.get(file));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        }
		
		String responseBody = "";
			try 
			{
				httpClient = getHttpClient();
				httpPost = getHttpPost(url,userAgent);	  
		        httpPost.setRequestEntity(new MultipartRequestEntity(parts,httpPost.getParams()));		        
		        int statusCode = httpClient.executeMethod(httpPost);
		        if(statusCode != HttpStatus.SC_OK) 
		        {
		        	throw AppException.http(statusCode);
		        }  
		     	responseBody =StringUtils.getStrFromInputSteam(httpPost.getResponseBodyAsStream()) ;
			} catch (HttpException e) {
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		    return responseBody;
  
	}
	
}
