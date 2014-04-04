package com.starbaby.diyBook.controller;
/**
 * 保存头像
 */
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.starbaby.diyBook.utils.SdcardpathUtils;

import android.graphics.Bitmap;

public class SavePic {
	// 保存头像
	public void saveHeadImg(Bitmap bitmap,String filePath) throws IOException {
		String saveHeadImgPath = SdcardpathUtils.SaveHeadPath;
		File folder = new File(saveHeadImgPath);
		if (!folder.exists()) // 如果文件夹不存在则创建
		{
			folder.mkdir();
		}
		File headImgFile = new File(filePath);
		if (headImgFile.exists()) {
			headImgFile.delete();
		}
		BufferedOutputStream boStream = new BufferedOutputStream(new FileOutputStream(headImgFile));
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, boStream);
		boStream.flush();
		boStream.close();
	}
}
