package com.starbaby.diyBook.controller;
/**
 * 对sdcard剩余的容量进行判断（10M为界限）
 */
import java.io.File;
import java.text.DecimalFormat;

import com.starbaby.diyBook.utils.Utils;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

public class ReadSDcard {
	static long availCount;
	static long blockCount;
	static long blockSize;
	static long fileSize;
	static int intFileSize = 0;
	/*
	 * sdcard剩余的容量
	 */
	public static int readSDCard() {  
        String state = Environment.getExternalStorageState();  
        if(Environment.MEDIA_MOUNTED.equals(state)) {  
            File sdcardDir = Environment.getExternalStorageDirectory();  
            StatFs sf = new StatFs(sdcardDir.getPath());  
            blockSize = sf.getBlockSize();  
            blockCount = sf.getBlockCount();  
            availCount = sf.getAvailableBlocks();  
            Log.d("", "block大小:"+ blockSize+",block数目:"+ blockCount+",总大小:"+blockSize*blockCount/1024+"KB");  
            Log.d("", "可用的block数目：:"+ availCount+",剩余空间:"+ availCount*blockSize/1024+"KB");  
        }
		return (int)(availCount*blockSize/1024);     
    }  
	/*
	 * 本地书本缓存占用的容量
	 */
	public static int readFile(){
		File file = new File(Utils.basePath1);//存放书本缓存的文件夹
		if(file.exists()){
			try {
				fileSize = CalculateFile.getFolderSize(file);
				DecimalFormat df = new DecimalFormat("###.##");  
				float f = ((float) fileSize / (float) 1024);
				if (f < 1.0) {
					float f2 = ((float) fileSize / (float) (1024));
					intFileSize = 0;
					return intFileSize;
				} else {
					intFileSize =(int) (new Float(f).doubleValue());
					return intFileSize;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			return 0;
		}
		return intFileSize;
	}
}
