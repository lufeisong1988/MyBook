package com.starbaby.diyBook.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CopyFile {
	public static void copyFile(String oldPath,String newPath){
		int bufferSum = 0;
		int bufferRead = 0;
		InputStream is = null;
		FileOutputStream fs = null;
		if(new File(oldPath).exists()){
			try {
				is = new FileInputStream(oldPath);
				fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while((bufferRead = is.read(buffer)) != -1){
					bufferSum += bufferRead;
					fs.write(buffer, 0, bufferRead);
					fs.flush();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(is != null){
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(fs != null){
					try {
						fs.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
