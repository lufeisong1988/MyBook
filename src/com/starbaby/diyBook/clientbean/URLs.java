package com.starbaby.diyBook.clientbean;

public class URLs {
	public final static String API_HOST = "api.starbaby.cn";
	public final static String FAMILY_API_HOST = API_HOST+"/familyHelper";
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";

	private final static String URL_SPLITTER = "/";

	private final static String URL_COMMON_API_HOST = HTTP+API_HOST+URL_SPLITTER;
	private final static String URL_FAMILY_API_HOST = HTTP+FAMILY_API_HOST+URL_SPLITTER;
    
	public final static String POST_INFO_LIST = URL_FAMILY_API_HOST+"find/info_lists";
	public final static String INFO_DETAIL = URL_FAMILY_API_HOST+"find/info_detail";
 
	
	//妈妈圈
	public final static String POST_DEFAULT_LIST = URL_FAMILY_API_HOST+"quanzi/mtopic";
	public final static String POST_TIME_LIST =    URL_FAMILY_API_HOST+"quanzi/mtopicByTime";
	public final static String COMMENT_LIST =     URL_FAMILY_API_HOST+"quanzi/marticle";
	public final static String POST_PUSH = URL_FAMILY_API_HOST+"quanzi/pushPost";
	public final static String GET_AT_LIST = URL_FAMILY_API_HOST+"quanzi/at_user_list/";
	
	//公共接口
	public final static String UPDATE_VERSION = URL_COMMON_API_HOST+"common/sys_info/1";
	public  final static String URL_EDIT_AVATAR_PATH =URL_COMMON_API_HOST+"common/editAvatar"; //编辑头像
	public  final static String URL_GET_AVATAR_PATH = URL_COMMON_API_HOST+"common/avatar_api/"; //获取头像
	public final static String LOGIN_VALIDATE_HTTP = URL_COMMON_API_HOST+"common/validateUser";
	public final static String REGISTER = URL_COMMON_API_HOST+"common/register";
	public final static String AVATAR = URL_COMMON_API_HOST+"common/imageup";
 
	//朋友圈
	public final static String FRIEND_POST = URL_FAMILY_API_HOST+"quanzi/friends_circle";
	public final static String POST_COMMENT = URL_FAMILY_API_HOST+"quanzi/mreply";
	public final static String POST_DEL_MYPOST =  URL_FAMILY_API_HOST+"ucenter/delPosts"; //删除主题接口
	//获取头像接口
	public final static String GET_AVATAR = URL_COMMON_API_HOST+"common/avatar_api/";

	//粉丝
	public final static String GET_FANS_LIST =  URL_FAMILY_API_HOST+"ucenter/user_fans/";
	//关注
	public final static String POST_FENSI_ADD = URL_FAMILY_API_HOST+"ucenter/attention";
	public final static String GET_ATTENTION_LIST = URL_FAMILY_API_HOST+"ucenter/user_notes/";
	public final static String POST_FENSI_CANCLE = URL_FAMILY_API_HOST+"ucenter/cancel_attention";															 
	//好友
	public final static String POST_FRIEND_ADD = URL_FAMILY_API_HOST+"ucenter/add_friend";
		
	//消息列表
	public final static String POST_MESSAGE_LIST=URL_FAMILY_API_HOST+"ucenter/usermsg";
	public final static String POST_MESSAGE_DEL =URL_FAMILY_API_HOST+"ucenter/delMsg";
	
	//家长圈-用户中心
	public final static String GET_MY_POST_LIST = URL_FAMILY_API_HOST+"ucenter/usertopic/"; //我/Ta 的主题列表
}
