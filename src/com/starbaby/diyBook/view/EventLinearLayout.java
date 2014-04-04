package com.starbaby.diyBook.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class EventLinearLayout extends RelativeLayout {
	private boolean bTrue = false;//false 获得焦点 true：失去焦点
	public boolean isbTrue() {
		return bTrue;
	}

	public void setbTrue(boolean bTrue) {
		this.bTrue = bTrue;
	}

	public EventLinearLayout(Context context) {
		super(context);
	}

	public EventLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		return super.onInterceptTouchEvent(ev);
		return bTrue;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
//		return super.onTouchEvent(event);
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		return super.onKeyDown(keyCode, event);
		return true;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
//		return super.onKeyUp(keyCode, event);
		return true;
	}
}
