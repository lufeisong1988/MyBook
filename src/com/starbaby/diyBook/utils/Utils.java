package com.starbaby.diyBook.utils;
/**
 * app全局变量
 */
import java.io.InputStream;
import java.io.OutputStream;

import com.starbaby.diyBook.controller.MusicService;
import com.starbaby.diyBook.helper.DBCacheHelper;
import com.starbaby.diyBook.helper.DBHelper;
import com.starbaby.diyBook.helper.DBPlayinfoHelper;
import com.starbaby.diyBook.helper.DBUserInfoHelper;
import com.starbaby.diyBook.main.MainActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Environment;

public class Utils {
	public static long currentMusicTime = 1000;
	public static SQLiteDatabase db;
	
	public static boolean bChangeMode = true;//判断是否可以点击进行做书模式（单例）
	
	public static int flag;
	public static Bitmap coverTexture;
	public static Bitmap contentTexture;
	public static float origin_left;
	public static float origin_right;
	public static float origin_top;
	public static float origin_bottom;
	public static float left;
	public static float right;
	public static float bottom;
	public static float top;
	//静态实例化数据库
	public static DBHelper mDBHelper;
	public static DBUserInfoHelper mDBUserInfoHelper;
	public static DBCacheHelper mDBCacheHelper;
	public static DBPlayinfoHelper mDBPlayinfoHelper;
	
	public static int coverHeight;
	public static int coverWidth;
	public static boolean bOpenApp = true;//true是自动打开门。false是手动打开门
	
	public static int s1 = 0;//宝宝益智
	public static int s2 = 0;//精品绘本
	public static int s3 = 0;//早教特色
	public static int s4 = 0;//	宝宝成长
	public static int s5 = 0;//	胎教故事
	public static int s6 = 0;//	幼儿教案
	public static int s7 = 0;//	幼儿学习
	public static int s8 = 0;//儿童故事
	public static int s9 = 0;//	品牌故事
	public static int s10 = 0;	//专题产品
	public static int sNew = 0;//新书
	public static int sWork = 0;
	public static int sInfo = 0;//自增。如果是3的倍数就刷新
	
	public static int readCount = 0;//未登入 最多读三本书 自增
	
	public static boolean readNet = true;//网络读取 true 本地读取false
	
	public static String sdPath = Environment.getExternalStorageDirectory().getPath();// /mnt/sdcard
	public static String basePath =sdPath +  "/starbaby_diyBook/";
	public static String basePath1 =sdPath +  "/starbaby_diyBook/myBook/";
	public static String basePath2 = "/DiyBook_ImgCache/";
	public static String coverPath = sdPath + "/starbaby_diyBook/coverCache/";//封面路径
	public static String path = basePath + "myPlay/";//爱做书的临时文件
	public static String takePhoto = basePath + "/takePhoto/";//拍照照片路径
	public static String sharePhoto = basePath + "/share/";//分享照片的操作
	public static String shareQQ = basePath + "/wenxin/";//分享到微信
	public static String renameFile = sdPath + "reName";//临时文件件。用来删除文件夹
	public static String zuoshuCache = basePath + "/zuoshuCache/";//做书图片（png）的临时缓存文件夹
	
	public static boolean createMusic = false;//判断书本中是否含有音乐 true:有 false:没有
	public static boolean openApp = true;//判断是否是第一次打开app加载打开动画
	public static boolean auto = false;//判断是否自动播放 true——自动  false——手动。默认手动
	public static boolean volume = true;//判断是否静音。true -有声音 false- 静音
	public static boolean bEnter = false;//判断是否登入
	
	public static String imgPathName = "DiyBook_ImgCache";//保存本地书本的图片文件夹name
	public static String audPathName = "diyBook_MusicCache";//保存本地书本的音乐文件夹name
	
	public static int DMWidth;//屏幕的宽
	public static int DMHeight;//屏幕的高
	
	public static int bShare;//0为不分享
	public static boolean bOpen = true;//如果为true 准许打开 ； false已经打开一本，其他不准许打开           (true书本关闭        false书本打开)
	public static boolean bSlide = false;//false 不允许滑动 true 允许滑动
	
	
	public static int currentPage = 1;//(0,1,2)记录打开的第几页。关闭时候调用第几页封面关闭
	public static String sharePicPath = null;
	public static int SDcardMemory =10 * 1024;//sdcard最小剩余容量
	public static boolean bOpenDoor = true;//true 可以自动打开。false已经手动打开，自动取消
	static SharedPreferences sp;
	public static boolean bReturnEnter = false;
	
	public static String collect_uid = "1234567890";//用户收藏书籍 书本封面缓存的文件别名
//	public static String collect_nickUid = "1234567890";
	
	public static boolean newBookCache = true;
	public static boolean huibenBookCache = true;
	public static boolean yizhiBookCache = true;
	public static boolean youjiaoBookCache = true;
	public static boolean userBookCache = true;
	public static boolean moreBookCache = true;
	public static boolean workBookCache = true;
	
	public static int currentPageCount;
//	public static String path = "mnt/sdcard/myPlay/";
	public static int ScreenWidth;
	public static int ScreenHeight;
	public static int ZuoShuCount;//最多做5本 ，如果超出则要分享后才能做 
	public static boolean bWifi(Context mContext){
		boolean bwifi = true;//提示在wifi条件下下载
		sp = mContext.getSharedPreferences("diyBook",mContext.MODE_WORLD_READABLE);
		if(sp.getInt("wifi", 0) == 1){
			bwifi = true;
		}else if(sp.getInt("wifi", 0) == 2){
			bwifi = false;
		}
		return bwifi;
	}
	public static int bVolum(Context mContext){
		AudioManager audio;
		sp = mContext.getSharedPreferences("diyBook",mContext.MODE_WORLD_READABLE);
		audio = (AudioManager) mContext. getSystemService(Context.AUDIO_SERVICE);
		int curVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		if(sp.getInt("volum", 0) == 1){//不静音
			
		}else if(sp.getInt("volum", 0) == 2){//静音
			curVolume = 0;
		}
		return curVolume;
	}
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
	public static Bitmap getFrameRect(Bitmap frameBit){
		int frameWidth = frameBit.getWidth();
		int frameHeight = frameBit.getHeight();
		int rectWidth = Utils.ScreenWidth / 2;
		int rectHeight = Utils.ScreenHeight;
		float widthRatio = (float)rectWidth / frameWidth;
		float heightRatio = (float)rectHeight / frameHeight;
		float scale = widthRatio < heightRatio ? widthRatio : heightRatio;
		return Bitmap.createScaledBitmap(frameBit, (int)(frameWidth * scale), (int)(frameHeight * scale), false);
	}
}
