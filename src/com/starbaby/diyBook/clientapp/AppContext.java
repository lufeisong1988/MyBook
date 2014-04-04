package com.starbaby.diyBook.clientapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.starbaby.diyBook.clientapi.ApiClient;
import com.starbaby.diyBook.clientbean.Comment;
import com.starbaby.diyBook.clientbean.CommentList;
import com.starbaby.diyBook.clientbean.FansList;
import com.starbaby.diyBook.clientbean.FriendPost;
import com.starbaby.diyBook.clientbean.FriendPostList;
import com.starbaby.diyBook.clientbean.NoticeList;
import com.starbaby.diyBook.clientbean.Picture;
import com.starbaby.diyBook.clientbean.PostList;
import com.starbaby.diyBook.clientbean.User;
import com.starbaby.diyBook.clientcommon.MethodsCompat;
import com.starbaby.diyBook.clientcommon.StringUtils;

public class AppContext extends Application {
	
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	public  static final int POST_DEFAULT_TYPE = 1; 
	public  static final int POST_TIME_TYPE = 2;
	private static final int CACHE_TIME =8*60*60000;//缓存失效时间 8小时  
	
	public boolean isFirst = true; //第一次加载
	
	private int loginUid = 0;	//登录用户的id
	private String loginPwd = "";//登录用户的密码
	private String loginName="";
	private String faceURL = "";
	
	public static final int NET_ERROR = -14;
	public static final  int PAGE_SIZE = 10;
	public  static final String FACE_URL = "user.face"; //存储用户头像的缓存key
	
	@Override
	public void onCreate() {
		super.onCreate();
        //注册App异常崩溃处理器   
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        initImageLoader(getApplicationContext()); //初始化图片加载器
	}
	/**
	 * 更新用户头像
	 * @param portrait 新上传的头像
	 * @return
	 * @throws AppException
	 */
	public Picture updatePortrait(File portrait) throws AppException {
		return ApiClient.updatePortrait(this, portrait);
	}
	//初始化图片加载器
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				//.cacheInMemory(true)
				.resetViewBeforeLoading(true)
				.cacheOnDisc(true)
				.displayer(new RoundedBitmapDisplayer(5))
				.build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				//.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.defaultDisplayImageOptions(defaultOptions)
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	/**
	 * 评论列表
	 * @param catalog 1帖子 2回复
	 * @param id 某条新闻，帖子，动弹的id 或者某条留言的friendid
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public CommentList getCommentList( int id, int pageIndex, boolean isRefresh) throws AppException {
		CommentList list = null;
		//String key = "commentlist_"+id+"_"+pageIndex;	
		//System.out.println("pageIndex=="+pageIndex);
		if(isNetworkConnected() ) {
			try{
				list = ApiClient.getCommentList(this, id, pageIndex);
			}catch(AppException e){
					throw e;
			}
		} else {
			if(list == null)
				list = new CommentList();
			 list.setMsg(-14);
		}
		return list;
	}
	/**
	 * 登录验证
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public User loginVerify(String account,String pwd) throws AppException{
		return ApiClient.login(this,account,pwd);
	}
	
	/**
	 * 发主题帖
	 * @param uid
	 * @param pwd
	 * @param content
	 * @param imgUrl
	 * @return
	 * @throws AppException
	 */
	public FriendPost pubPost(int uid,String pwd,String content,Map<String,String> imgUrl,int typeId) throws AppException{
		return ApiClient.pubPost(this,uid,pwd,content,imgUrl,typeId);
	}
	/**
	 * 关注列表
	 * @param uid
	 * @param pageNum
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	public FansList getAttentionList(int uid,int pageNum) throws AppException{
		FansList list = null;
		 String key = "attentionList_"+uid+"_"+pageNum;
		 System.out.println("key====="+key);
		if(isNetworkConnected()){ 
			try{
				list = ApiClient.getAttentionList(this, uid, pageNum);
			}catch(AppException e){
				throw e;
			}
		}else{
			if(list == null)
				list = new FansList();
			if(!isNetworkConnected())
				list.setMsg(NET_ERROR);
		}
		return list;
	}
	
	/**
	 * 关注列表
	 * @param uid
	 * @param pageNum
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	public FansList getFansList(int uid,int pageNum) throws AppException{
		FansList list = null;
		 String key = "getFansList"+uid+"_"+pageNum;
		 System.out.println("key====="+key);
		if(isNetworkConnected()){ 
			try{
				list = ApiClient.getAttentionList(this, uid, pageNum);
			}catch(AppException e){
				throw e;
			}
		}else{
			if(list == null)
				list = new FansList();
			if(!isNetworkConnected())
				list.setMsg(NET_ERROR);
		}
		return list;
	}
	/**
	 * 获取当前网络类型
	 * @return 0：没有网络   1：WIFI网络   2：2G  3：3G
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
				int type = networkInfo.getSubtype();
				if(type==TelephonyManager.NETWORK_TYPE_CDMA ||type==TelephonyManager.NETWORK_TYPE_EDGE || type==TelephonyManager.NETWORK_TYPE_GPRS ){
					netType = NETTYPE_CMWAP;
				}else{
					netType = NETTYPE_CMNET;
				}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}
	
	/**
	 * 用户是否登录
	 * @return
	 */
	public boolean isLogin() {
		return (loginUid>0);
	}

	/**
	 * 初始化用户登录信息
	 */
	public void initLoginInfo() {
		User loginUser = getLoginInfo();
		if(loginUser!=null && loginUser.getUid()>0){
			this.loginUid = loginUser.getUid();
			this.loginPwd = loginUser.getPasswd();
			this.loginName = loginUser.getUname();
			this.faceURL = loginUser.getFace();
		}else{
			this.Logout();
		}
	}
	

	/**
	 * 用户注销
	 */
	public void Logout() {
		this.cleanCookie();
		this.loginUid = 0;
		this.loginPwd="";
		this.loginName="";
		this.faceURL="";
	}
	/**
	 * 清除保存的缓存
	 */
	public void cleanCookie()
	{
		removeProperty(AppConfig.CONF_COOKIE);
	}
	
	public int cancleAttention(int uid,String pwd,int auid) throws AppException{
		int result = 10;
		if(isNetworkConnected()){
			result = ApiClient.cancleAttention(this,uid,pwd,auid);
		} else{
			result = NET_ERROR;
		}
		return result;
	}
	
	public PackageInfo getPackageInfo(){
		PackageInfo info = null;
		try{
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		}catch(NameNotFoundException e){
			e.printStackTrace(System.err);
		}
		if(info == null) info = new PackageInfo();
		return info;
	}
	
	/**
	 * 判断缓存是否失效
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile)
	{
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists() && ((System.currentTimeMillis() - data.lastModified()) > CACHE_TIME) && !isFirst){
			failure = true;
		} else if(!data.exists()){
			failure = true;
		}
		isFirst = false;
		return failure;
	}
	
	/**
	 * 帖子列表
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public FriendPostList getMessageList(int catalog,int pageIndex, boolean isRefresh,int type) throws AppException {
		FriendPostList list = null;
        String key = "messagelist_"+pageIndex+"_"+type+"_"+catalog;  
		if(isNetworkConnected() && ( isCacheDataFailure(key)|| isRefresh)) {   
			try{
				list = ApiClient.getPostList(this,catalog,pageIndex,type,loginUid);
				if(list != null && pageIndex == 1 ){
					int notice = list.getNewmsgsize();
					list.setNewmsgsize(0);
					list.setCacheKey(key);
					saveObject(list, key);
					list.setNewmsgsize(notice);
				}   
			}catch(AppException e){
			 	list = (FriendPostList)readObject(key);
			 	if(list == null)
					throw e;
			}
		} else {
			list = (FriendPostList)readObject(key);
			if(list == null)
				list = new FriendPostList();
			if(!isNetworkConnected())
				list.setErrorCode(NET_ERROR);
		}
		return list;
	}
	
	/**
	 * 朋友圈列表
	 * @param pageIndex
	 * @return
	 * @throws AppException
	 */
	public FriendPostList getFriendList(int pageIndex, boolean isRefresh,int uid,int type) throws AppException {
		FriendPostList list = null;
        String key = "friendList"+pageIndex+"_"+uid + "_" + type;  
		if(isNetworkConnected()&& (!isReadDataCache(key) || isRefresh)) {   
			try{
				list = ApiClient.getFriendList(this,pageIndex,uid,type);
				if(list != null && pageIndex == 1){
					list.setCacheKey(key);
					saveObject(list, key);
				}   
			}catch(AppException e){
			 	list = (FriendPostList)readObject(key);
			 	if(list == null)
					throw e;
			}
		} else {
			list = (FriendPostList)readObject(key);
			if(list == null)
				list = new FriendPostList();
			if(!isNetworkConnected())
				list.setErrorCode(NET_ERROR);
		}
		return list;
	}
	
	/**
	 * 获取消息信息
	 * @param uid
	 * @return
	 * @throws AppException 
	 */
	public NoticeList getNoticeList(int catalog,int uid,String pwd,int curPage) throws AppException{
		 NoticeList noticeList = null;
			 try{
				 noticeList = ApiClient.getNotices(this, catalog,uid, pwd,curPage);
			 }catch(AppException e){
 				 throw e;
			 }
		   return noticeList;
	}
	
	/**
	 * 我的主题帖
	 * @param uid
	 * @param pageIndex
	 * @param isRefresh
	 * @return
	 * @throws AppException
	 */
	public PostList getMyPostList(int hisId,int curCatalog,int pageIndex,int curUid,boolean isRefresh) throws AppException{
		PostList list = null;
		
		//String key = "mypostlist_"+hisId+"_"+pageIndex+"_"+curCatalog;
		if(isNetworkConnected()){  //&& (!isReadDataCache(key)|| isRefresh) 
			try {
				 list= ApiClient.getMyPostList(this,hisId,curCatalog,pageIndex,curUid);
			/*	 if(list!=null && pageIndex==1 && hisId==curUid ){
					list.setCacheKey(key);
					saveObject(list, key);
				 }*/
			} catch (AppException e) {
				//list = (PostList)readObject(key);
			 	//if(list == null)
					throw e;
			}
		} else {
		     //  list = (PostList)readObject(key);
			if(list==null){
				list = new PostList();
			}
			if(!isNetworkConnected()){
				list.setErrorCode(NET_ERROR);
			}
		}
		
		return list;
	}
	public Comment pubComment(int uid,String pwd,int tId,String content) throws AppException{
		return ApiClient.pubComment(this, uid, pwd, tId,content);
	}
	
	public Comment repComment(int uid,String pwd,int tId,int postId,String content) throws AppException  {
		return ApiClient.replyComment(this, uid, pwd, tId, postId, content);
	}
	/**
	 * 删除主题
	 * @param uid
	 * @param pwd
	 * @param tid
	 * @return
	 * @throws AppException
	 */
	public int delPost(int uid,String pwd,int tid) throws AppException{
		int result = 0;
		if(isNetworkConnected()){
			result = ApiClient.delPost(this,uid,pwd,tid);
		}else {
			 result = -14;
		}
		return result;
	}
	
	/**
	 * 删除消息
	 * @param uid
	 * @param pwd
	 * @param id
	 * @return
	 * @throws AppException
	 */
	public int delNotice(int uid,String pwd,int id) throws AppException{
		int result = 0;
		if(isNetworkConnected()){
			try {
				result = ApiClient.delNotice(this, uid, pwd, id);
			} catch (AppException e) {
				throw e;
			}
		} else {
			result = -14;
		}
		return result;
	}
	
	/**
	 * 添加关注
	 * @param uid
	 * @param pwd
	 * @param auid
	 * @return
	 * @throws AppException
	 */
	public int addFenSi(int uid,String pwd,int auid) throws AppException{
		int result = -10;
		if(isNetworkConnected()){
			result = ApiClient.addFenSi(this, uid, pwd, auid);
		} else {
			result = -14;
		}
		return result;
	}
	
	
	/**
	 * 检测网络是否可用
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}
	
	/**
	 * 保存对象
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try{
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			try {
				oos.close();
			} catch (Exception e) {}
			try {
				fos.close();
			} catch (Exception e) {}
		}
	}
	
	/**
	 * 保存登录信息
	 * @param username
	 * @param pwd
	 */
	public void saveLoginInfo(final User user) {
		this.loginUid = user.getUid();
		this.loginPwd = user.getPasswd();
		this.loginName= user.getUname();
		this.faceURL  = user.getFace();
		setProperties(new Properties(){{
			setProperty("user.uid", String.valueOf(loginUid));
			setProperty("user.name", loginName);
		    setProperty(FACE_URL,faceURL);//用户头像 url
			setProperty("user.email", user.getEmail());
			setProperty("user.pwd",loginPwd);
			}});		
	}
	/**
	 * 清除登录信息
	 */
	public void cleanLoginInfo() {
		Log.i("cleanLoginInfo","did");
		this.loginUid = 0;
		this.loginPwd = "";
		this.loginName="";
		this.faceURL = "";
		removeProperty("user.uid","user.name",FACE_URL,"user.email","user.pwd","user.login");
	}
	
	public void removeProperty(String...key){
		AppConfig.getAppConfig(this).remove(key);
	}	
	
	public void setProperties(Properties ps){
		AppConfig.getAppConfig(this).set(ps);
	}
	/**
	 * 判断缓存数据是否可读
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile)
	{
		return readObject(cachefile) != null;
	}
	
	/**
	 * 判断缓存是否存在
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile)
	{
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists())
			exist = true;
		return exist;
	}
	
	/**
	 * 读取对象
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file){
		if(!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try{
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable)ois.readObject();
		}catch(FileNotFoundException e){
		}catch(Exception e){
			e.printStackTrace();
			//反序列化失败 - 删除缓存文件
			if(e instanceof InvalidClassException){
				File data = getFileStreamPath(file);
				data.delete();
			}
		}finally{
			try {
				ois.close();
			} catch (Exception e) {}
			try {
				fis.close();
			} catch (Exception e) {}
		}
		return null;
	}
	
	/**
	 * 清除缓存目录
	 * @param dir 目录
	 * @param numDays 当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {          
	    int deletedFiles = 0;         
	    if (dir!= null && dir.isDirectory()) {             
	        try {                
	            for (File child:dir.listFiles()) {    
	                if (child.isDirectory()) {              
	                    deletedFiles += clearCacheFolder(child, curTime);          
	                }  
	                if (child.lastModified() < curTime) {     
	                    if (child.delete()) {                   
	                        deletedFiles++;           
	                    }    
	                }    
	            }             
	        } catch(Exception e) {       
	            e.printStackTrace();    
	        }     
	    }       
	    return deletedFiles;     
	}
	/**
	 * 判断当前版本是否兼容目标版本的方法
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}
		/**
		 * 清除app缓存
		 */
		public void clearAppCache()
		{
		 
			//清除数据缓存
			clearCacheFolder(getFilesDir(),System.currentTimeMillis());
			clearCacheFolder(getCacheDir(),System.currentTimeMillis());
			//2.2版本才有将应用缓存转移到sd卡的功能
			if(isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)){
				clearCacheFolder(MethodsCompat.getExternalCacheDir(this),System.currentTimeMillis());
			}
		}	
	/**
	 * 获取App唯一标识
	 * @return
	 */
	public String getAppId() {
		String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
		if(StringUtils.isEmpty(uniqueID)){
			uniqueID = UUID.randomUUID().toString();
			setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}
	public void setProperty(String key,String value){
		AppConfig.getAppConfig(this).set(key, value);
	}
	
	public String getProperty(String key){
		return AppConfig.getAppConfig(this).get(key);
	}
	
	
	/**
	 * 获取登录信息
	 * @return
	 */
	public  User getLoginInfo() {		
		User lu = new User();		
		lu.setUid(StringUtils.toInt(getProperty("user.uid"), 0));
		lu.setUname(getProperty("user.name"));
		lu.setFace(getProperty(FACE_URL));
		lu.setEmail(getProperty("user.email"));
		lu.setPasswd(getProperty("user.pwd"));
//		SharedPreferences sp = getSharedPreferences("diyBook", MODE_WORLD_READABLE);
//		lu.setUid(sp.getInt("uid", 0));
//		lu.setUname(sp.getString("name", ""));
//		lu.setFace(sp.getString("avatar", ""));
//		lu.setPasswd(sp.getString("psw", ""));
		return lu;
	}
	

	/**
	 * 获取登录用户id
	 * @return
	 */
	public int getLoginUid() {
		return this.loginUid;
	}
 
	/**
	 * 获取登录用户id
	 * @return
	 */
	public String getLoginPwd() {
		return this.loginPwd;
	}
	
	/**
	 * 获取登录用户名
	 * @return
	 */
	public String getLoginName(){
		return this.loginName;
	}
	
	/*
	 * 获取头像地址
	 */
	public String getFaceUrl(){
		return this.faceURL;
	}
	
	public void setFaceUrl(String url){
		this.faceURL = url;
	}
	
	/**
	 * 应用程序是否发出提示音
	 * @return
	 */
	public boolean isAppSound() {
		return isAudioNormal() && isVoice();
	}
	
	/**
	 * 检测当前系统声音是否为正常模式
	 * @return
	 */
	public boolean isAudioNormal() {
		AudioManager mAudioManager = (AudioManager)getSystemService(AUDIO_SERVICE); 
		return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}
	
	/**
	 * 是否发出提示音
	 * @return
	 */
	public boolean isVoice()
	{
		String perf_voice = getProperty(AppConfig.CONF_VOICE);
		//默认是开启提示声音
		if(StringUtils.isEmpty(perf_voice))
			return true;
		else
			return StringUtils.toBool(perf_voice);
	}
	
}
