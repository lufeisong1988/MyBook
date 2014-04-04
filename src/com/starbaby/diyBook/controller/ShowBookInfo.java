package com.starbaby.diyBook.controller;
/**
 * 自动打开封面后，再显示书本具体内容
 */
import android.app.Activity;
import android.content.Context;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.helper.MusicHelper;
import com.starbaby.diyBook.main.MainActivity;
import com.starbaby.diyBook.view.CurlView;
import com.starbaby.diyBook.view.CurlView.PageProvider;
import com.starbaby.diyBook.view.CurlView.SizeChangedObserver;

public class ShowBookInfo {
	Context mContext;
	CurlView mCurlView;
	FrameLayout container;
	int index;
	PageProvider mPageProvider;
	SizeChangedObserver mSizeChangedObserver;
	public ShowBookInfo(Context mContext,CurlView mCurlView,FrameLayout container,int index,PageProvider mPageProvider,SizeChangedObserver mSizeChangedObserver){
		this.mContext = mContext;
		this.mCurlView = mCurlView;
		this.container = container;
		this.index = index;
		this.mPageProvider = mPageProvider;
		this.mSizeChangedObserver = mSizeChangedObserver;
	}
	public void BookInfo(){
		container.addView(mCurlView);
		if (((Activity) mContext).getLastNonConfigurationInstance() != null) {
			index = (Integer) ((Activity) mContext).getLastNonConfigurationInstance();
		}
		mCurlView.setPageProvider(mPageProvider);//获取当前显示页的图片
		mCurlView.setSizeChangedObserver(mSizeChangedObserver);//获取屏幕状态（横屏：展示2页，竖屏：展示一页）
		mCurlView.setCurrentIndex(index);//设置打开app显示的第几页
		mCurlView.setBackgroundColor(0xffffffff);//设置背景色（注：demo下整个activity都是GLSurfaceView）
//		MusicHelper.init(mContext,index);
		MusicHelper.init(mContext,index - 1);
	}
}
