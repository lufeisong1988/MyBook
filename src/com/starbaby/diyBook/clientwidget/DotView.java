package com.starbaby.diyBook.clientwidget;

 


import com.starbaby.diyBook.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;


/**
 * 点View
 * 
 * @author stone
 * 
 */
public class DotView extends View {

	private int dotCount = 0;
	private int dotPos = 0;
	private int padding = 5;
	private Paint mPaint;
	private int dotW = 5;
	private Bitmap imgOn;
	private Bitmap imgOff;

	public DotView(Context c) {
		this(c, null);
	}

	public DotView(Context c, int count) {
		this(c, null);
		if (count >= 0) {
			dotCount = count;
		}
	}

	public DotView(Context c, AttributeSet attrs) {
		super(c, attrs);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		Resources res = c.getResources();
		imgOn = BitmapFactory.decodeResource(res, R.drawable.dark_dot);
		imgOff = BitmapFactory.decodeResource(res, R.drawable.white_dot);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = View.MeasureSpec.getSize(widthMeasureSpec);
		int height = imgOn.getHeight();
		dotW = imgOn.getWidth();
		padding = dotW / 2;
		setMeasuredDimension(width, height);
	}

	public void setCount(int count) {
		if (count >= 0) {
			dotCount = count;
			invalidate();
		}
	}

	public void setPos(int pos) {
		if (dotPos >= 0 && dotPos < dotCount) {
			dotPos = pos;
			invalidate();
		}
	}

	protected void onDraw(Canvas canvas) {
		int startX = (getWidth() - dotW * dotCount - padding * (dotCount - 1)) / 2 - padding;
		for (int i = 0; i < dotCount; i++) {
			if (dotPos == i) {
				canvas.drawBitmap(imgOn, startX + dotW / 2 + (dotW + padding) * i, 0, null);
			} else {
				canvas.drawBitmap(imgOff, startX + dotW / 2 + (dotW + padding) * i, 0, null);
			}
		}
	}
}
