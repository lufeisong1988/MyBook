package com.starbaby.diyBook.utils;
/**
 * 接口类
 * @author Administrator
 *
 */
public class HttpPort {
	public static String testUrl = "http://api.starbaby.cn/diybook/mtpl_data";//测试接口
	public static String baseUrl = "http://api.starbaby.cn/diybook/";//diybook项目接口的base_url 
	public static String NewBookUrl = baseUrl+"new_books/";//获取新书推荐接口
	public static String topcateBookUrl = baseUrl + "topcate_books/";//某个顶级分类下的模版列表接口
	public static String bookUrl = baseUrl + "mtpl_data/";//获取某个模版的数据信息  
	public static String workShowUrl = baseUrl + "zuopin/";//爱做书接口
	public static String myWorkShowUrl = baseUrl + "user_works";//个人作品秀
	public static String upDateUrl ="http://api.starbaby.cn/common/sys_info/5";//版本更新接口
	public static String sync_collect = baseUrl + "sync_collection";//同步线上collect数据接口
	public static String zuoShu = baseUrl + "make_book";//做书接口
	public static String deleteBook = baseUrl + "del_zuopin";//删除书本接口
	
	public static String addAttention = "http://appApi/attention";//加关注接口
	
	public static String topcate_id_1 = "12";//宝宝益智
	public static String topcate_id_2 = "10";//精品绘本
	public static String topcate_id_3 = "9";//早教特色
	public static String topcate_id_4 = "7";//宝宝成长
	public static String topcate_id_5 = "1";//胎教故事
	public static String topcate_id_6 = "11";//幼儿教案
	public static String topcate_id_7 = "3";//幼儿学习
	public static String topcate_id_8 = "2";//儿童故事
	public static String topcate_id_9 = "28";//品牌故事
	public static String topcate_id_10 = "13";//品质出版
	
	public static String sendPicUrl = "";// 测试上传图片接口
	
	public static String registerImgUrl="http://api.starbaby.cn/common/imageup";//本地上传图片 反馈的url
	public static String registerUrl="http://www.starbaby.cn/appApi/register";//注册接口
	public static String enterUrl="http://api.starbaby.cn/common/validateUser";//登入接口
	public static String changeHeadImg = "http://api.starbaby.cn/common/editAvatar";//修改头像接口
}
