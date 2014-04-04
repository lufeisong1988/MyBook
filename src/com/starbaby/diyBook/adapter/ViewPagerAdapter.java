package com.starbaby.diyBook.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPagerAdapter extends PagerAdapter {

	//界面列表
	private ArrayList<View> views;
	public ViewPagerAdapter(ArrayList<View> views){
		this.views = views;
	}
	
	//销毁position位置的界面
	@Override
	public void destroyItem(View parent,int position,Object arg2){
		((ViewPager)parent).removeView(views.get(position));
	}
	
	@Override
	public int getCount() {
		if(views != null){
			return views.size();
		}
		return 0;
	}

	//初始化position位置的界面
	@Override
	public Object instantiateItem(View parent,int position){
		((ViewPager)parent).addView(views.get(position),0);
		return views.get(position);
	}
	
	//判断是否由对象生成界面
	@Override
	public boolean isViewFromObject(View parent, Object object) {
		return (parent==object);
	}
}
