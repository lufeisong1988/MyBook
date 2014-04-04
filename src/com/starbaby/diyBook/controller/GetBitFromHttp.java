package com.starbaby.diyBook.controller;
/**
 * 做书 获取图片
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.view.ShowZuoShuTips;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class GetBitFromHttp {
	Context mContext;
	ShowZuoShuTips mShowZuoShuTips;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				mShowZuoShuTips.dismissTips();
				Toast.makeText(mContext, "下载图片资源失败", Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg);
		}
	};
	public GetBitFromHttp(Context mContext,ShowZuoShuTips mShowZuoShuTips){
		this.mContext = mContext;
		this.mShowZuoShuTips = mShowZuoShuTips;
		
	}
	/*
	 * 获取模板做图图片(PNG)
	 */
	public  void getBitmapPng(String tpl_id,String url){
		String oldPath = Utils.zuoshuCache + tpl_id  + "/" + getName(url) ;
		String newPath = Utils.path + getName(url);
		if(new File(oldPath).exists()){
			CopyFile.copyFile(oldPath,newPath);
			Log.i("copyPng","copy");
			return;
		}
		downloadFile(url);
	}
	/*
	 * 遍历图片list 如果本地存在就copy 否则download
	 */
	public  void getBitmap(String tpl_id,String url){
		String oldPath = Utils.basePath1 + tpl_id  + "/"+ Utils.imgPathName + "/" + getName(url) + ".cach";
		String newPath = Utils.path + getName(url);
		if(!new File(newPath).exists()){
			if(new File(oldPath).exists()){
				CopyFile.copyFile(oldPath,newPath);
				Log.i("copyJpg","copy");
				return;
			}
			downloadFile(url);
		}
	}
	/*
	 * 保存图片
	 */
	static void saveBitmap(Bitmap bit,String bitUrl){
		File pathFile = new File(Utils.sdPath);
		pathFile.mkdirs();
		File strFile = new File(Utils.basePath);
		strFile.mkdirs();
		File saveFile = new File(Utils.path);
		saveFile.mkdirs();
		File saveBitFile = new File(Utils.path + getName(bitUrl));
		OutputStream outStream = null;
		try {
			saveBitFile.createNewFile();
			outStream = new FileOutputStream(saveBitFile);
			bit.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			outStream.flush();
			outStream.close();
			if(bit != null || !bit.isRecycled()){
				bit.recycle();
				bit = null;
				System.gc();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(outStream != null){
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	/*
	 * 截取图片url为图片保存name
	 */
	static String getName(String url){
		String name = null;
		if(url != null){
			String str1[] = url.split("/");
			return str1[str1.length - 1];
		}
		return name;
		
	}
	//流转成字节
	static byte[] readStream(InputStream stream) throws IOException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buff = new byte[1024];
		int len = 0;
		while((len = stream.read(buff)) != -1){
			outStream.write(buff, 0, len);
		}
		outStream.close();
		stream.close();
		return outStream.toByteArray();
	}
	
	private void downloadFile(final String urlPath) {
		Log.i("download","download");
		Bitmap resultBit = null;
		InputStream inputStream = null;
		ByteArrayOutputStream byteArrayOutputStream = null;
		byte[] data = null;
		try {
			URL url = new URL(urlPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6 * 1000);
			conn.connect();
			if (conn.getResponseCode() == 200) {
				inputStream = conn.getInputStream();
				byteArrayOutputStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = inputStream.read(buffer)) != -1) {
					byteArrayOutputStream.write(buffer, 0, length);
				}
				data = byteArrayOutputStream.toByteArray();
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (byteArrayOutputStream != null) {
				try {
					byteArrayOutputStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		if (data != null) {
			// 保存操作
			resultBit = BitmapFactory.decodeByteArray(data, 0, data.length,null);
			saveBitmap(resultBit,urlPath);
		}else{
			//下载失败
			Message msg = new Message();
			msg.what = 1;
			mHandler.sendMessage(msg);
		}
				
	}
}
