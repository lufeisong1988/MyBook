package com.starbaby.diyBook.clientapp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Date;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientcommon.UIHelper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;


public class AppException extends Exception implements UncaughtExceptionHandler {
	private final static boolean Debug = true;
	public final static byte TYPE_NETWORK 	= 0x01;
	public final static byte TYPE_SOCKET	= 0x02;
	public final static byte TYPE_HTTP_CODE	= 0x03;
	public final static byte TYPE_HTTP_ERROR= 0x04;
	public final static byte TYPE_XML	 	= 0x05;
	public final static byte TYPE_IO	 	= 0x06;
	public final static byte TYPE_RUN	 	= 0x07;
	public final static byte TYPE_JSON 		= 0x08;

	private byte type;
	private int code;
	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	
	private AppException(){
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}
	
	private AppException(byte type,int code,Exception excp){
		super(excp);
		this.type = type;
		this.code = code;
		if(Debug){
			
		}
	}
	public int getType(){
		return this.type;
	}
	
	public int getCode(){
		return this.code;
	}
	
	/**
	 * 提示友好的错误信息
	 * @param ctx
	 */
	public void makeToast(Context ctx){
		switch(this.getType()){
		case TYPE_HTTP_CODE:
			String err = ctx.getString(R.string.http_status_code_error, this.getCode());
			Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_HTTP_ERROR:
			Toast.makeText(ctx, R.string.http_exception_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_SOCKET:
			Toast.makeText(ctx, R.string.socket_exception_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_NETWORK:
			Toast.makeText(ctx, R.string.network_not_connected, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_XML:
			Toast.makeText(ctx, R.string.xml_parser_failed, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_IO:
			Toast.makeText(ctx, R.string.io_exception_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_RUN:
			Toast.makeText(ctx, R.string.app_run_code_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_JSON:
			Toast.makeText(ctx, R.string.json_parser_failed, Toast.LENGTH_SHORT).show();
			break;
		}
	}
	/**
	 * 把错误信息写入日志文件
	 * @param excp
	 */
	public void saveErrorLog(Exception excp){
		String errorLog = "errorLog.txt";
		String savePath = "";
		String logFilePath = "";
		FileWriter fileWriter = null;
		PrintWriter printWriter = null;
		try{
			//判断是否挂载SD卡
			String storegeState = Environment.getExternalStorageState();
			if(storegeState.equals(Environment.MEDIA_MOUNTED)){
				savePath = Environment.getExternalStorageDirectory()+"/diybook2/log/";
				File file = new File(savePath);
				if(!file.exists()){
					file.mkdirs();
				}
				logFilePath = savePath+errorLog;
			}
			//没有挂载SD卡，无法写文件
			if(logFilePath==""){
				return;
			}
			
			File logFile = new File(logFilePath);
			if(!logFile.exists()){
				logFile.createNewFile();
			}
			fileWriter = new FileWriter(logFile);
			printWriter = new PrintWriter(fileWriter);
			printWriter.println("============="+new Date().toString()+"===============");
			excp.printStackTrace(printWriter);
			printWriter.close();
			fileWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(printWriter!=null){
				printWriter.close();
			}
			if(fileWriter!=null){
				try {
					fileWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static AppException http(int code){
		return new AppException(TYPE_HTTP_CODE,code,null);
	}
	
	public static AppException http(Exception e){
		return new AppException(TYPE_HTTP_ERROR,0,e);
	}
	
	public static AppException socket(Exception e) {
		return new AppException(TYPE_SOCKET, 0 ,e);
	}
	
	public static AppException io(Exception e){
		if(e instanceof UnknownHostException || e instanceof ConnectException){
			return new AppException(TYPE_NETWORK,0,e);
		} else if(e instanceof IOException){
			return new AppException(TYPE_IO, 0, e);
		}
		return run(e);
	}
	
	public static AppException json(Exception e){
		return new AppException(TYPE_JSON,0,e);
	}
	
	public static AppException run(Exception e){
		return new AppException(TYPE_RUN, 0, e);
	}
	
	public static AppException network(Exception e){
		return new AppException(TYPE_NETWORK,0,e);
	}

	/**
	 * 获取APP异常崩溃处理对象
	 * @param context
	 * @return
	 */
	public static AppException getAppExceptionHandler(){
		return new AppException();
	}
	
	public void uncaughtException(Thread thread,Throwable ex){
		if(!handleException(ex)) return;
		
	}
	
	/**
	 * 自定义异常处理:收集错误信息&发送错误报告
	 * @param ex
	 * @return true:处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex){
		if(ex==null) return false;
		final Context context = AppManager.getAppManager().currentActivity();
		if(context == null){
			return false;
		}
		
		final String crashReport = getCrashReport(context, ex);
		new Thread(){
			public void run(){
				Looper.prepare();
				UIHelper.sendAppCrashReport(context, crashReport);
				Looper.loop();
			}
		}.start();
		return true;
	}
	
	/**
	 * 获取APP崩溃异常报告
	 * @param ex
	 * @return
	 */
	private String getCrashReport(Context context,Throwable ex){
		PackageInfo pinfo = ((AppContext)context.getApplicationContext()).getPackageInfo();
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Version: "+pinfo.versionName+"("+pinfo.versionCode+")\n");
		exceptionStr.append("Android: "+android.os.Build.VERSION.RELEASE+"("+android.os.Build.MODEL+")");
		exceptionStr.append("Exception: "+ex.getMessage()+"\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for(int i=0;i<elements.length;i++){
			exceptionStr.append(elements[i].toString()+"\n");
		}
		return exceptionStr.toString();
	}
}
