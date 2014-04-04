package com.starbaby.diyBook.controller;
/**
 * 切换横竖屏，这里默认为横屏
 */
import com.starbaby.diyBook.utils.StoreSrc;
import com.starbaby.diyBook.view.CurlView;

/**
 * CurlView size changed observer.
 */
public class SizeChangedObserver implements CurlView.SizeChangedObserver {
	float H;
	float W;
	private CurlView mCurlView;
	public SizeChangedObserver(CurlView mCurlView){
		this.mCurlView = mCurlView;
	}
	public void onSizeChanged(int w, int h) {
		if (w > h) {//横屏的情况下，展示2页
			mCurlView.setViewMode(CurlView.SHOW_TWO_PAGES);
			//(w - 2xw) / (h - 2yh) = 2W / H;x:书页在x轴距离手机边框的距离   y:书页在y轴距离手机边框的距离。书的比例是3/2
			H = StoreSrc.getImage_height();
			W = StoreSrc.getImage_width();
			float scaleBit = ((2 * W) /(float) H);
			float scaleScreen = ((float)w / h);
			if(scaleBit < scaleScreen){//展开后 上下铺满
				mCurlView.setMargins((w*H - 2*W*h) / (2*H*w),0.0f,(w*H - 2*W*h) / (2*H*w),0.0f);
			}else{//展开后左右铺满
				mCurlView.setMargins(0.0f,(2 * h * W - w * H) / (4 * W * h),0.0f,(2 * h * W - w * H) / (4 * W * h));
			}
//			mCurlView.setMargins(marginX,(float) (2*W*h + 2*marginX*w*H - w*H) / (4*h*W), marginX, (float) (2*W*h + 2*marginX*w*H - w*H) / (4*h*W));
		} else {//竖屏的情况下，展示单页
			mCurlView.setViewMode(CurlView.SHOW_ONE_PAGE);
			mCurlView.setMargins(.1f, .1f, .1f, .1f);
		}
	}
}
