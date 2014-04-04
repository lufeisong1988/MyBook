package com.starbaby.diyBook.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.utils.Utils;

/**
 * 做书 获取音乐
 */
public class GetMusFromHttp {
	public static void downMusic(String tpl_id,String url){
		File basePath = new File(Utils.sdPath);
		basePath.mkdirs();
		File basePath2 = new File(Utils.basePath);
		basePath2.mkdirs();
		File basePath3 = new File(Utils.path);
		basePath3.mkdirs();
		String realPath = basePath3 + "/" + NamePic.mp3UrlToFileName(url);// 下载完后的文件存储路径
		String pathName = basePath3 + "/" + NamePic.mp3UrlToFileName(url) + ".tmp";// 临时文件存储路径
		String oldPath = Utils.basePath1 + tpl_id + "/" + Utils.audPathName + "/" + NamePic.mp3UrlToFileName(url);
		String newPath = Utils.path + NamePic.mp3UrlToFileName(url);
		if (new File(oldPath).exists()) {
			CopyFile.copyFile(oldPath, newPath);
			return;
		}
		File storePath = new File(pathName);
		try {
			storePath.createNewFile();
			downloadFile(url, storePath, realPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
			
	}
	private static void downloadFile(final String urlPath,final File storePath,final String realPath) {
		InputStream inputStream = null;
		FileOutputStream output = null;
		try {
			URL url = new URL(urlPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6 * 1000);
			conn.connect();
			if (conn.getResponseCode() == 200) {
				inputStream = conn.getInputStream();
				output = new FileOutputStream(storePath);
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = inputStream.read(buffer)) != -1) {
					output.write(buffer, 0, length);
				}
				storePath.renameTo(new File(realPath));
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
			if(output != null){
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
