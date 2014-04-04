package com.starbaby.diyBook.utils;

import android.view.View;
import android.widget.LinearLayout;

public class JavaBeanLocation {
	public static LinearLayout linearlayout;
	public static View view;
	public static float x;
	public static float y;
	public static float width;
	public static float height;
	public static LinearLayout getLinearlayout() {
		return linearlayout;
	}
	public static void setLinearlayout(LinearLayout linearlayout) {
		JavaBeanLocation.linearlayout = linearlayout;
	}
	public static View getView() {
		return view;
	}
	public static void setView(View view) {
		JavaBeanLocation.view = view;
	}
	public static float getX() {
		return x;
	}
	public static void setX(float x) {
		JavaBeanLocation.x = x;
	}
	public static float getY() {
		return y;
	}
	public static void setY(float y) {
		JavaBeanLocation.y = y;
	}
	public static float getWidth() {
		return width;
	}
	public static void setWidth(float width) {
		JavaBeanLocation.width = width;
	}
	public static float getHeight() {
		return height;
	}
	public static void setHeight(float height) {
		JavaBeanLocation.height = height;
	}
	
}
