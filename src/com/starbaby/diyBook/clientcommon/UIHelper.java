package com.starbaby.diyBook.clientcommon;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.message.BasicNameValuePair;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppManager;
import com.starbaby.diyBook.clientui.FinishDialog;
import com.starbaby.diyBook.clientui.FriendsActivity;
import com.starbaby.diyBook.clientui.ImageUploadPager;
import com.starbaby.diyBook.clientui.ImageViewPager;
import com.starbaby.diyBook.clientui.MessageDetail;
import com.starbaby.diyBook.clientui.MessagePub;
import com.starbaby.diyBook.clientui.MessageType;
import com.starbaby.diyBook.clientui.MyNotice;
import com.starbaby.diyBook.clientui.SnsDelDialog;
import com.starbaby.diyBook.clientwidget.LoadingDialog;
import com.starbaby.diyBook.view.EnterActivityView;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.sax.StartElementListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


/**
 * 应用程序UI工具包：封装UI相关的一些操作
 * @author stone(fanlei123126@163.com)
 * @version 1.0
 * @created 2013-3-29
 */
public class UIHelper {
	public final static int LISTVIEW_ACTION_INIT = 0x01;
	public final static int LISTVIEW_ACTION_REFRESH = 0x02;
	public final static int LISTVIEW_ACTION_SCROLL = 0x03;
	public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;
	
	public final static int LISTVIEW_DATA_MORE = 0x01;
	public final static int LISTVIEW_DATA_LOADING = 0x02;
	public final static int LISTVIEW_DATA_FULL = 0x03;
	public final static int LISTVIEW_DATA_EMPTY = 0x04;
	
	public final static int REQUEST_CODE_FOR_REPLY = 0x02;
	public final static int REQUEST_CODE_FOR_VIEW = 0x03;
	public final static int REQUEST_CODE_FOR_FINISH = 0x04;
	public final static int REQUEST_CODE_FOR_DIALOGIN = 0x05;
	public final static int REQUEST_CODE_FOR_ATME = 0x06;
	public final static int REQUEST_CODE_FOR_DEL = 0x07;
	
	public final static int LISTVIEW_DATATYPE_FRIENDS_ALL = 0x00;
	public final static int LISTVIEW_DATATYPE_FRIENDS_CHAT = 0x01;
	public final static int LISTVIEW_DATATYPE_FRIENDS_DIARY = 0x02;
	public final static int LISTVIEW_DATATYPE_FRIENDS_MOMENT = 0x03;
	
	public static void showFriends(Context context,int uid,String pwd){
		Intent intent = new Intent(context, FriendsActivity.class);
		intent.putExtra("uid", uid);
		intent.putExtra("pwd", pwd);
		context.startActivity(intent);
	}
 
	 
	/**
	 * 取消编辑提示对话框
	 * @param cont
	 */
	public static void showFinishDialog(Activity context)
	{
		 Intent intent = new Intent(context,FinishDialog.class);
		 context.startActivityForResult(intent, REQUEST_CODE_FOR_FINISH);
	}
	 /**
	 * 显示登陆界面
	 */ 
	public static void showLogin(Context context){
		Intent intent = new Intent(context,EnterActivityView.class);
	/*	 if(context instanceof Main){
			intent.putExtra("LOGINTYPE", Login.LOGIN_MAIN);
		  } */
		context.startActivity(intent);
	}
	
	/**
	 * 图片预览
	 * @param context
	 * @param pics
	 * @param position
	 */
	public static void showImageUploadPager(Activity context,ArrayList<String> pics,int position){
		Intent intent = new Intent(context,ImageUploadPager.class);
		intent.putStringArrayListExtra("list", pics);
		intent.putExtra("position", position);
		//System.out.println("pics================="+pics.size());
		context.startActivityForResult(intent,REQUEST_CODE_FOR_VIEW);
	}
	
	public static void sendBroadPubMsg(Context context,int what,int catalog){
		Intent intent = new Intent("com.starbaby.friendbook.app.action.APP_POSTPUB"); 
		intent.putExtra("MSG_WAHT", what);
		intent.putExtra("MSG_CATALOG", catalog);
		context.sendBroadcast(intent);
	}
	
	/**
	 * 显示发帖页面
	 * @param context
	 */
	public static void showMessagePub(Activity context,int catalog)
	{
		Intent intent = new Intent();
 		intent.setClass(context, MessagePub.class);
 		intent.putExtra("catalog", catalog);
		context.startActivity(intent);
		context.overridePendingTransition(R.anim.workbook_in,R.anim.none);
	}
	
	public static void showActivity(Activity context,Class<?> cls,BasicNameValuePair...body){
		Intent intent = new Intent();
		intent.setClass(context, cls);
		for(int i=0;i<body.length;i++){
			intent.putExtra(body[i].getName(), body[i].getValue());
		}
		context.startActivity(intent);
	}
	//登入自己或者他人的个人中心
	public static void showUserActivity(int userUid,Activity context,Class<?> cls,BasicNameValuePair...body){
		Intent intent = new Intent();
		intent.setClass(context, cls);
		for(int i=0;i<body.length;i++){
			intent.putExtra(body[i].getName(), body[i].getValue());
		}
		intent.putExtra("userUid", userUid);
		if(cls.equals(MyNotice.class)){
			context.startActivityForResult(intent, 1);
		}else{
			context.startActivity(intent);
		}
	}
	
	/**
	 * 显示发帖选择界面
	 */
	public static void showMessageType(Activity context){
		Intent intent = new Intent();
 		intent.setClass(context, MessageType.class);
		context.startActivity(intent);
	}
	
	/**
	 * 组合消息文本
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableStringBuilder parseFriendPostpan(String name,SpannableStringBuilder body){
		SpannableStringBuilder sp = null;
		int start = 0;
		int end = 0;
		String mName = name + "：";
		sp = body.insert(0, mName);
		//sp = new SpannableString(name + "：" + body);
		end = name.length();
		
		//设置用户名字体加粗、高亮 
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#2f548f")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#ff000000")), end,sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
	}
	
	public static void showImageViewPager(Context context,ArrayList<String> smallList,ArrayList<String> bigList,int position){
		Intent intent = new Intent(context,ImageViewPager.class);
		intent.putStringArrayListExtra("imgs", smallList);
		intent.putStringArrayListExtra("bigImgs", bigList);
		intent.putExtra("index", position);
		context.startActivity(intent);
	}
	/**
	 * 删除自己的话题
	 * @param cont
	 */
	public static void showSnsDelDialog(Activity context,int tid,int position)
	{
		 Intent intent = new Intent(context,SnsDelDialog.class);
		 intent.putExtra("tid", tid);
		 intent.putExtra("position",position);
		 context.startActivityForResult(intent, REQUEST_CODE_FOR_DEL);
	}
   
	/**
	 * 弹出Toast消息
	 * @param msg
	 */
	public static void ToastMessage(Context cont,String msg)
	{
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}
	public static void ToastMessage(Context cont,int msg)
	{
		Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
	}
	public static void ToastMessage(Context cont,String msg,int time)
	{
		Toast.makeText(cont, msg, time).show();
	}
	
	/***
	 * 点击返回监听事件
	 * @param activity
	 * @return
	 **/
	public static View.OnClickListener finish(final Activity activity)
	{
		return new View.OnClickListener() {
			public void onClick(View v) {
				activity.finish();
				activity.overridePendingTransition(R.anim.none,R.anim.workbook_out);
			}
		};
	}	
	
	/**
	 * 组合消息文本
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableString parsePostpan(String name,String body,String action){
		SpannableString sp = null;
		int start = 0;
		int end = 0;
		if(StringUtils.isEmpty(action)){
			sp = new SpannableString(name + "：" + body);
			end = name.length();
		}else{
			sp = new SpannableString(action + name + "：" + body);
			start = action.length();
			end = start + name.length();
		}
		//设置用户名字体加粗、高亮 
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
	}
	/**
	 * 组合消息文本
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableStringBuilder parsePostpan(String name,SpannableStringBuilder body){
		SpannableStringBuilder sp = null;
		int start = 0;
		int end = 0;
		String mName = name + "：";
		sp = body.insert(0, mName);
		end = name.length();
		//设置用户名字体加粗、高亮 
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		sp.setSpan(new ForegroundColorSpan(Color.parseColor("#2f548f")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
	}
	
	/**
	 * 组合回复引用文本
	 * @param name
	 * @param body
	 * @return
	 */
	public static SpannableStringBuilder parseQuoteSpan(String name,SpannableStringBuilder body){
		String nName = "回复："+name+"\n";
		SpannableStringBuilder sp = body.insert(0, nName);
		//设置用户名字体加粗、高亮 
		sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 3,nName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#0e5986")), 3,nName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
	}
	public static void showPostDetail(Context context,int tid,int authorid){
		//System.out.println("tid=========="+tid);
		Intent intent  = new Intent(context,MessageDetail.class);
		intent.putExtra("tid", tid);
		intent.putExtra("authorid", authorid);
/*		intent.putExtra("authorname",authorname);*/
		context.startActivity(intent);
	}
 
	/**
	 * 清除app缓存
	 * @param activity
	 */
	public static void clearAppCache(Activity activity)
	{
		final AppContext ac = (AppContext)activity.getApplication();
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(msg.what==1){
					ToastMessage(ac, "缓存清除成功");
				}else{
					ToastMessage(ac, "缓存清除失败");
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg = new Message();
				try {				
					ac.clearAppCache();
					msg.what = 1;
				} catch (Exception e) {
					e.printStackTrace();
	            	msg.what = -1;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	/**
	 * 发送App异常崩溃报告
	 * @param cont
	 * @param crashReport
	 */
	public static void sendAppCrashReport(final Context cont, final String crashReport)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_error);
		builder.setMessage(R.string.app_error_message);
		builder.setPositiveButton(R.string.submit_report, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//发送异常报告
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain"); //模拟器
				//i.setType("message/rfc822") ; //真机
				i.putExtra(Intent.EXTRA_EMAIL, new String[]{"fanlei123126@163.com"});
				i.putExtra(Intent.EXTRA_SUBJECT,"星宝宝Android客户端 - 错误报告");
				i.putExtra(Intent.EXTRA_TEXT,crashReport);
				cont.startActivity(Intent.createChooser(i, "发送错误报告"));
				Log.i("Exception Log",crashReport);
				//退出
				AppManager.getAppManager().AppExit(cont);
			}
		});
		builder.setNegativeButton(R.string.sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//退出
				AppManager.getAppManager().AppExit(cont);
			}
		});
		builder.show();
	}
	
	/**
	 * 删除对话框
	 * @param cont
	 */
	public static void showDelOptionDialog(final Context context,final String title,final LoadingDialog loading,final Thread thread)
	{
		  CharSequence[] items = {
				"删除"
			};
			AlertDialog imageDialog = new AlertDialog.Builder(context).setTitle(title).setItems(items,
				new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface dialog, int item)
					{
						//删除操作
						if( item == 0 )
						{
							if(loading != null){
								loading.setLoadText("正在删除数据···");
								loading.show();	
							}
							 thread.start();
						} 
					}}).create();
			    imageDialog.show();
			    
			    
	}

 
	
	
	/**
	 * 退出程序
	 * @param cont
	 */
	public static void Exit(final Context cont)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_menu_surelogout);
		builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//退出
				AppManager.getAppManager().AppExit(cont);
			}
		});
		builder.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
	
	/**
	 * 退出程序
	 * @param cont
	 */
	public static void ExitByMin(final Context cont)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(cont);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle(R.string.app_menu_surelogout);
		builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				//退出
				AppManager.getAppManager().AppExit(cont);
			}
		});
		builder.show();
	}
}
