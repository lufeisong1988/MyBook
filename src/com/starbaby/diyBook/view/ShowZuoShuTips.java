package com.starbaby.diyBook.view;

import android.content.Context;
import android.widget.LinearLayout;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.utils.commentDialogUtils;

/**
 * 显示切换做书模式提示
 * @author Administrator
 *
 */
public class ShowZuoShuTips {
	commentDialogUtils mShowZuoShuTips;
	public void showTips(Context mContext){
		mShowZuoShuTips = new commentDialogUtils(mContext, LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT, R.layout.zuoshu_title, R.style.Theme_dialog);
		mShowZuoShuTips.setCanceledOnTouchOutside(false);
		mShowZuoShuTips.show();
	}
	public void dismissTips(){
		if(mShowZuoShuTips != null)
			mShowZuoShuTips.dismiss();
	}
}
