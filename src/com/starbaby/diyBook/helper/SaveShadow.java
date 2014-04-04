package com.starbaby.diyBook.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.utils.Utils;

public class SaveShadow {
	public static void SaveCanvas(Context mContext,Bitmap src){
//		Bitmap resultBit = null;
//		Bitmap resultCover = null;
//		Bitmap coverBit = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.shadow);
//		
//		int srcW = src.getWidth();
//		int srcH = src.getHeight();
//		float scaleY = (float)srcH / coverBit.getHeight();
//		resultCover = Bitmap.createScaledBitmap(coverBit, srcW, srcH + (int) scaleY * 12, false);
//		resultBit = Bitmap.createBitmap(srcW, srcH + (int) scaleY * 12, Config.ARGB_8888);
//		Canvas canvas = new Canvas(resultBit);
//		Paint paint = new Paint();
//		paint.setColor(Color.WHITE);
//		canvas.drawRect(0, 0, srcW, srcH + (int) scaleY * 12, paint);//12为底部阴影高度
//		canvas.drawBitmap(src, 0, 0, null);
//		canvas.drawBitmap(resultCover, 0, 0, null);
//		canvas.save(Canvas.ALL_SAVE_FLAG);
//		File str = new File(Utils.sharePhoto);
//		str.mkdirs();
//		File file = new File(Utils.sharePhoto + "sharePic.jpg");
//		try {
//			file.createNewFile();
//			FileOutputStream fos = new FileOutputStream(file);
//			resultBit.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//			fos.flush();
//			fos.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		resultBit.recycle();
//		resultBit = null;
//		src.recycle();
//		src= null;
		int width = src.getWidth();
		int height = src.getHeight();
		Bitmap left = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.book_left);
		Bitmap right = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.book_right);
		Bitmap top = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.book_top);
		Bitmap bottom = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.book_bottom);
		int leftW = left.getWidth();
		int leftH = left.getHeight();
		int rightW = right.getWidth();
		int rightH = right.getHeight();
		int topW = top.getWidth();
		int topH = top.getHeight();
		int bottomW = bottom.getWidth();
		int bottomH = bottom.getHeight();
		
		Bitmap realBit = Bitmap.createBitmap(width + leftW + rightW, height + topH + bottomH, Config.ARGB_8888);
		Canvas canvas = new Canvas(realBit);
		
		Bitmap mLeft = Bitmap.createScaledBitmap(left, leftW, height, false);
		Bitmap mRight = Bitmap.createScaledBitmap(right, rightW, height, false);
		Bitmap mTop = Bitmap.createScaledBitmap(top, width + leftW + rightW, topH, false);
		Bitmap mBottom = Bitmap.createScaledBitmap(bottom, width + leftW + rightW, bottomH, false);
		canvas.drawBitmap(mTop, 0, 0, null);
		canvas.drawBitmap(mLeft, 0 , topH , null);
		canvas.drawBitmap(src, leftW, topH, null);
		canvas.drawBitmap(mRight, width + leftW, topH, null);
		canvas.drawBitmap(mBottom, 0, height + topH, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		
		File str = new File(Utils.sharePhoto);
		str.mkdirs();
		File file = new File(Utils.sharePhoto + "sharePic.jpg");
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			realBit.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		realBit.recycle();
		realBit = null;
		src.recycle();
		src= null;
		left.recycle();
		left = null;
		right.recycle();
		right = null;
		top.recycle();
		top = null;
		bottom.recycle();
		bottom = null;
		mLeft.recycle();
		mLeft = null;
		mRight.recycle();
		mRight = null;
		mTop.recycle();
		mTop = null;
		mBottom.recycle();
		mBottom = null;
		
		
	}
}
