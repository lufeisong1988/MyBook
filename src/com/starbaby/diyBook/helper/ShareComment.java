package com.starbaby.diyBook.helper;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;




import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.main.Share;
import com.starbaby.diyBook.sharesdk.OnekeyShare;
import com.starbaby.diyBook.sharesdk.ShareContentCustomizeDemo;
import com.starbaby.diyBook.utils.Utils;

public class ShareComment {
	Context mContext;
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				Toast.makeText(mContext, "分享失败", Toast.LENGTH_LONG).show();
				break;
			case 2:
				Toast.makeText(mContext, "分享成功", Toast.LENGTH_LONG).show();
				SharedPreferences sp = mContext.getSharedPreferences("diyBook", mContext.MODE_WORLD_READABLE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("bAllow", true);
				editor.commit();
				break;
			case 3:
				Toast.makeText(mContext, "分享取消", Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg);
		}
	};
	public ShareComment(Context mContext){
		this.mContext = mContext;
	}
	public void showShare(boolean silent, String platform,String ImagePath){
		final OnekeyShare oks = new OnekeyShare();
		oks.setNotification(R.drawable.ic_launcher, mContext.getString(R.string.app_name));
		oks.setAddress("12345678901");
//		oks.setTitle("自己动手制作的个性化有声读物（“星宝宝童”书手机app）");
		oks.setText("");
		oks.setImagePath(ImagePath);
//		oks.setUrl("http://www.starbaby.cn/");
		oks.setLatitude(23.056081f);
		oks.setLongitude(113.385708f);
		oks.setSilent(silent);
		if (platform != null) {
			oks.setPlatform(platform);
		}

		oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());
		oks.show(mContext);
		oks.setCallback(new PlatformActionListener() {
			
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onCancel(Platform arg0, int arg1) {
				Message msg = new Message();
				msg.what = 3;
				mHandler.sendMessage(msg);
			}
		});
	}
}
