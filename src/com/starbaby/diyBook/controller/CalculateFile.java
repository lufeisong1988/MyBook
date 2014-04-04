package com.starbaby.diyBook.controller;

import android.annotation.SuppressLint;
import java.text.DecimalFormat;

@SuppressLint("UseValueOf")
public class CalculateFile {
	/** 
     * 获取文件夹大小 
     * @param file File实例 
     * @return long 单位为M 
     * @throws Exception 
     */  
    public static long getFolderSize(java.io.File file)throws Exception{  
        long size = 0;  
        java.io.File[] fileList = file.listFiles();  
        for (int i = 0; i < fileList.length; i++)  
        {  
            if (fileList[i].isDirectory())  
            {  
                size = size + getFolderSize(fileList[i]);  
            } else  
            {  
                size = size + fileList[i].length();  
            }  
        }  
        return size;  
    }  
    /** 
     * 文件大小单位转换 
     *  
     * @param size 
     * @return 
     */  
    public static String setFileSize(long size) {  
        DecimalFormat df = new DecimalFormat("###.##");  
        float f = ((float) size / (float) (1024 * 1024));  
        if (f < 1.0) {  
            float f2 = ((float) size / (float) (1024));  
            return df.format(new Float(f2).doubleValue()) + "KB";  
        } else {  
            return df.format(new Float(f).doubleValue()) + "M";  
        }  
    }
}
