package com.starbaby.diyBook.helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;

public class DrawRect {
//	public static Bitmap Rect(Bitmap bit){//画边线
//		Bitmap frameRightBit  = null;
//		frameRightBit = Bitmap.createBitmap(bit.getWidth(), bit.getHeight(), Config.ARGB_8888);
//		Canvas canvas = new Canvas(frameRightBit);
//		Paint paint = new Paint();
//		paint.setColor(0xff7cfc00);// 设置灰色  
//		paint.setStyle(Style.STROKE);//设置填满  
//		paint.setStrokeWidth(2);
//		canvas.drawBitmap(bit, 0, 0, null);
//		canvas.drawRect(1, 1, bit.getWidth()-1, bit.getHeight()-1, paint);
//		return frameRightBit;
//	}
	//画阴影
	public static Bitmap Shadow(Bitmap bit){
		Bitmap frameRightBit = null;
		frameRightBit = Bitmap.createBitmap(bit.getWidth(), bit.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(frameRightBit);
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setAlpha(175);
		canvas.drawRect(0, 0, bit.getWidth(), bit.getHeight(), paint);
//		canvas.drawBitmap(bit, 0, 0, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		return frameRightBit;
		
	}
}
