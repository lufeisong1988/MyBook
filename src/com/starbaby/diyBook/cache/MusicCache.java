package com.starbaby.diyBook.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.starbaby.diyBook.utils.StoreSrc;
import com.starbaby.diyBook.utils.Utils;


import android.os.Environment;
/**
 * 音乐缓存工具类
 * @author Administrator
 *
 */
public class MusicCache {
	static OutputStream output = null;
	private static String shortPath = "/starbaby_diyBook/";
	private static String shortPath2 = "myBook/";
	private static final String CACHDIR = "diyBook_MusicCache";
	@SuppressWarnings({ "unused" })
	public static String saveMp3(String url) {
		try {
			URL httpUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
			String path1 = Utils.sdPath + shortPath;
			String path1_1 = path1 + shortPath2;
			String path2 = path1_1 + StoreSrc.getTpl_name();
			String realPath = path2 +  "/" +CACHDIR+"/"+ convertUrlToFileName(url);//下载完后的文件存储路径
			String pathName = path2 +  "/" +CACHDIR+"/"+ convertUrlToFileName(url) + ".tmp";// 临时文件存储路径
			File file = new File(realPath);
			InputStream input = conn.getInputStream();
			if (file.exists()) {
				System.out.println("exits");
				return realPath;
			} else {
				if(!new File(path1).exists()){
					new File(path1).mkdir();
				}
				if(!new File(path1_1).exists()){
					new File(path1_1).mkdir();
				}
				if(!new File(path2).exists()){
					new File(path2).mkdir();
				}
				if(!new File(path2+ "/" +CACHDIR).exists()){
					new File(path2+ "/" +CACHDIR).mkdir();
				}
				File file2 = new File(pathName);
				file2.createNewFile();// 新建文件
				output = new FileOutputStream(file2);
				// 读取大文件
				byte[] buffer = new byte[1024 * 1024];
				int ch = -1;
				int count = 0;
				while((ch = input.read(buffer)) != -1){
					output.write(buffer, 0,ch );
					count += ch;
				}
				file2.renameTo(new File(realPath));
				output.flush();
				return realPath;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	/**
	 * 保存打开封面时候的一张图片
	 * @param url
	 * @param bookName
	 * @return
	 */
	public static String saveMusic(String url,String bookName) {
		try {
			URL httpUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
			String SDCard = Environment.getExternalStorageDirectory() + "";
			String path1 = SDCard + shortPath;
			String path1_1 = path1 + shortPath2;
			String path2 = path1_1 + bookName;
			String realPath = path2 +  "/" +CACHDIR+"/"+ convertUrlToFileName(url);// 文件存储路径
			String pathName = path2 +  "/" +CACHDIR+"/"+ convertUrlToFileName(url) + ".tmp";// 临时文件存储路径
			File file = new File(realPath);
			InputStream input = conn.getInputStream();
			if (file.exists()) {
				System.out.println("exits");
				return realPath;
			} else {
				if(!new File(path1).exists()){
					new File(path1).mkdir();
				}
				if(!new File(path1_1).exists()){
					new File(path1_1).mkdir();
				}
				if(!new File(path2).exists()){
					new File(path2).mkdir();
				}
				if(!new File(path2+ "/" +CACHDIR).exists()){
					new File(path2+ "/" +CACHDIR).mkdir();
				}
				File file2 = new File(pathName);
				file2.createNewFile();// 新建文件
				output = new FileOutputStream(file2);
				// 读取大文件
				byte[] buffer = new byte[1024 * 1024];
				int ch = -1;
				int count = 0;
				while((ch = input.read(buffer)) != -1){
					output.write(buffer, 0,ch );
					count += ch;
				}
				file2.renameTo(new File(realPath));
				output.flush();
				return realPath;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	/** 将url转成文件名 **/
    private static String convertUrlToFileName(String url) {
        String[] strs = url.split("/");
        return strs[strs.length - 1] ;
    }
}

