package com.starbaby.diyBook.cache;
/**
 * 缓存每个分类下的书本封面
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.starbaby.diyBook.utils.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CoverCacheFromHttp {
	private String saveCoverPath = Utils.coverPath;//每个分类下第一页的封面缓存路径
	private String section;//分类名
	private Bitmap coverBit = null;
	public CoverCacheFromHttp(Context mContext,String section){
		this.section = section;
	}
	public Bitmap saveBookCache(String url){
		File file = new File(saveCoverPath + section +"/" + convertUrlToFileName(url));
		if(file.exists()){//直接从缓存读取
			coverBit = BitmapFactory.decodeFile(saveCoverPath + section +"/" + convertUrlToFileName(url));
			if(coverBit == null){
				file.delete();
			}else{
				return coverBit;
			}
		}else{//从服务器下载
			if(!new File(Utils.basePath).exists()){
				new File(Utils.basePath).mkdir();
			}
			if(!new File(saveCoverPath).exists()){
				new File(saveCoverPath).mkdir();
			}
			if(!new File(saveCoverPath + section).exists()){
				new File(saveCoverPath + section).mkdir();
			}
			File coverPath = new File(saveCoverPath + section + "/" + convertUrlToFileName(url));
			coverBit = ImageGetFromHttp.downloadBitmap(url);
			if(coverBit != null){
				try {
					coverPath.createNewFile();
					OutputStream outstream = new FileOutputStream(coverPath);
					coverBit.compress(Bitmap.CompressFormat.PNG, 100, outstream);
					outstream.flush();
					outstream.close();
					return coverBit;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	private static final String WHOLESALE_CONV = ".cach";  
	 /** 将url转成文件名 **/
    private String convertUrlToFileName(String url) {
    	 String[] strs = url.split("/");
         String[] strs2 = strs[strs.length - 1].split("\\.");
         return strs2[0] + ".jpg" +WHOLESALE_CONV ;
    }
  
}
