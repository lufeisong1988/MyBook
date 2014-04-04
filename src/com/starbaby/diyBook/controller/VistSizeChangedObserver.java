package com.starbaby.diyBook.controller;

import com.starbaby.diyBook.view.CreateCurlView;
import com.starbaby.diyBook.view.VistCurlView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
/**
 * 切换横竖屏，这里默认为横�?
 */
import android.util.Log;

/**
 * CurlView size changed observer.
 */
public class VistSizeChangedObserver implements VistCurlView.SizeChangedObserver {
	float H;
	float W;
	private VistCurlView mCurlView;
	String bitUrl;
	public VistSizeChangedObserver(VistCurlView mCurlView,String bitUrl){
		this.mCurlView = mCurlView;
		this.bitUrl = bitUrl;
	}
	public void onSizeChanged(int w, int h) {
		if (w > h) {//横屏的情况下，展双页
			Log.i("orection","H");
			mCurlView.setViewMode(CreateCurlView.SHOW_TWO_PAGES);
			Bitmap bit = BitmapFactory.decodeFile(bitUrl);
			H = bit.getHeight();
			W = bit.getWidth();
			float scaleBit = ((2 * W) /(float) H);
			float scaleScreen = ((float)w / h);
			if(scaleBit < scaleScreen){//展开后上下铺满
				mCurlView.setMargins((w*H - 2*W*h) / (2*H*w),0.0f,(w*H - 2*W*h) / (2*H*w),0.0f);
			}else{//展开后左右铺满
				mCurlView.setMargins(0.0f,(2 * h * W - w * H) / (4 * W * h),0.0f,(2 * h * W - w * H) / (4 * W * h));
			}
		} else {//竖屏的情况下，展示单页
			Log.i("orection","V");
			mCurlView.setViewMode(CreateCurlView.SHOW_ONE_PAGE);
			mCurlView.setMargins(.1f, .1f, .1f, .1f);
		}
	}
}
