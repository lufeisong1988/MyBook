package com.starbaby.diyBook.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.utils.HttpPort;
import com.starbaby.diyBook.utils.SdcardpathUtils;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.commentDialogUtils;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * 版本的升级
 * @author Administrator
 *
 */
public class Update {
	Long fileSize;
	int downLoadSize ;
	private  Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 2:
				mSeekBar.setMax(fileSize.intValue());
				break;
			case 3:
				mSeekBar.setProgress(downLoadSize);
				break;
			case 4:
				mDialog.dismiss();
				break;
			case 5:
				mDialog.dismiss();
				Toast.makeText(mContext, "下载失败...", 1000).show();
				break;
			}
			
			super.handleMessage(msg);
		}
	};
	public String getUpdateResult(){//从服务器获取最新版本信息
		String result = null;
		HttpClient client = new DefaultHttpClient();
		//设置连接超时时间为40s
		HttpConnectionParams.setConnectionTimeout(client.getParams(),40000);
		//设置读取超时为20s
		HttpConnectionParams.setSoTimeout(client.getParams(),20000);
		//使用get请求
		HttpGet get = new HttpGet(HttpPort.upDateUrl);
		//执行请求，获取响应结果
		HttpResponse respone;
		try {
			respone = client.execute(get);
			//获得响应状态码
			int code = respone.getStatusLine().getStatusCode() ;
			//判断请求是否成功
			if(code == 200){
				result = EntityUtils.toString(respone.getEntity());
			}else{
				result = null;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	public  int getVerCode(Context mContext){//获取本地apk版本号
		int verCode = -1;
		try {
			verCode = mContext.getPackageManager().getPackageInfo("com.starbaby.diyBook", 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verCode;
	}
	public String getVerName(Context mContext){//获取本地apk的名称
		String verName = null;
		try {
			verName = mContext.getPackageManager().getPackageInfo("com.starbaby.diyBook", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}
	Context mContext;
	public  void downLoadApk(final Context mContext,final String apkUrl,final String minVerCode,final int verCode,final String mark){
		this.mContext = mContext;
		StringBuffer sb = new StringBuffer();
		sb.append("发现新版本, 是否更新 " + "\n" + mark);
		new AlertDialog.Builder(mContext)
				.setTitle("软件更新")
				.setMessage(sb.toString())
				.setPositiveButton("暂时不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								if(verCode < Integer.parseInt(minVerCode)){
									Toast.makeText(mContext, "由于你的版本过低，无法正常运行starbaby_diyBook，3秒后自动结束当前apk运行", 1000).show();
									Timer timer=new Timer();
									TimerTask timerTask=new TimerTask() {
										@Override
										public void run() {
											Intent startMain=new Intent(Intent.ACTION_MAIN);
											startMain.addCategory(Intent.CATEGORY_HOME);
											startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
											mContext.startActivity(startMain);
											System.exit(0);
										}
									};
									timer.schedule(timerTask, 1000*3);
								}
							}
						}).setNegativeButton("确定", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								showDialog();
								downFile(apkUrl,mContext);
							}
						}).create().show();
	}
	public void downFile(final String updata_link,final Context mContext) {
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				//设置连接超时时间为40s
				HttpConnectionParams.setConnectionTimeout(client.getParams(),40000);
				//设置读取超时为20s
				HttpConnectionParams.setSoTimeout(client.getParams(),20000);
				HttpGet get = new HttpGet(updata_link);
				HttpResponse response;
				try {
					response = client.execute(get);
					int code = response.getStatusLine().getStatusCode() ;
					//判断请求是否成功
					if(code == 200){
						
					}else{
						sendMsg(5);
					}
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					fileSize = length;
					sendMsg(2);
					InputStream is = entity.getContent();
					FileOutputStream fileoutputstream = null;
					// 下载后生成的apk.name
					File file = new File(Environment.getExternalStorageDirectory(),"diyBook");
					if (is != null) {
						fileoutputstream = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						while ((ch = is.read(buf)) != -1) {
							fileoutputstream.write(buf, 0, ch);
							count += ch;
							downLoadSize += ch;
							sendMsg(3);
						}
					}
					fileoutputstream.flush();
					sendMsg(4);
					if (fileoutputstream != null) {
						fileoutputstream.close();
						is.close();
					}
					down(mContext);
				} catch (ClientProtocolException e) {
					sendMsg(5);
					e.printStackTrace();
				} catch (IOException e) {
					sendMsg(5);
					e.printStackTrace();
				}
			}
		}.start();
	}
	protected  void down(final Context mContext) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				update(mContext);
			}
		}).start();
	}
	public  void update(Context mContext) {
		/*
		 * 删除本地所有数据
		 * 3张数据库表 1张sp，本地所有文件
		 */
		File headImgFile = new File(SdcardpathUtils.headName);
		if (headImgFile.exists()) {
			headImgFile.delete();
		}
		Utils.mDBHelper.deleteAllData();
		Utils.mDBCacheHelper.deleteAll();
		Utils.mDBUserInfoHelper.deleteAll();
		SharedPreferences userInfo = mContext.getSharedPreferences("diyBook",mContext.MODE_WORLD_READABLE);
		SharedPreferences.Editor editor = userInfo.edit();
		editor.clear();
		editor.commit();
		DeleteCache.delAllFile(Utils.basePath);
		/*
		 * 手动安装
		 */
		File apkFile = new File(Environment.getExternalStorageDirectory(),"diyBook");
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + apkFile.toString()),"application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
	private void sendMsg(int flag)
	{
	    Message msg = new Message();
	    msg.what = flag;
	    mHandler.sendMessage(msg);
	}	 
	Dialog mDialog;
	SeekBar mSeekBar;
	private void showDialog(){
		mDialog = new commentDialogUtils(mContext, LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, R.layout.download, R.style.Theme_dialog);
		mDialog.setCanceledOnTouchOutside(false);
		mSeekBar =(SeekBar)mDialog. findViewById(R.id.download_sb);
		mDialog.show();
	}
 
}
