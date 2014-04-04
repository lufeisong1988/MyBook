package com.starbaby.diyBook.view;

import com.starbaby.diyBook.R;
/**
 * 个人中心的按钮
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserInfoBnt extends LinearLayout {
	private ImageView iv;
	private TextView tv;
	public UserInfoBnt(Context context){
		super(context,null);
	}
	public UserInfoBnt(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.userinfo_button, this, true);
		iv = (ImageView)findViewById(R.id.userifo_button1);
		tv = (TextView) findViewById(R.id.userinfo_tv1);
	}
	public void setBit(int bit){
		iv.setBackgroundResource(bit);
	}
	public void setText(String text){
		tv.setText(text);
	}
	public void setColor(int NO){
		tv.setTextColor(NO);
	}
}
