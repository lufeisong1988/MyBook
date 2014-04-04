package com.starbaby.diyBook.controller;
/**
 * 版本自检
 */
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import cn.sharesdk.framework.Platform.ShareParams;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.checkNetUtils;
import com.starbaby.diyBook.utils.commentDialogUtils;

@SuppressLint({ "HandlerLeak", "ShowToast" })
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CheckUpdate implements OnClickListener{
	String result;
	Update mUpdate = new Update();
	String mVerCode;
	String mVerName;
	String mMin;
	String mApkUrl;
	String mark;
	int verCode;
	String verName;
	Context mContext;
	SharedPreferences userInfo ;
	boolean bShow;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				if(bShow){
					mDialog.dismiss();
				}
				if(Integer.parseInt(mVerCode) > verCode){
					mUpdate.downLoadApk(mContext, mApkUrl, mMin, verCode,mark);
				}else{
					if(bShow){
						showtip();
					}
				}
				break;
			case 2:
				if(bShow){
					mDialog.dismiss();
					Toast.makeText(mContext, "连接网络失败", 1000).show();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};
	public CheckUpdate(Context mContext,boolean bShow){
		this.mContext = mContext;
		this.bShow = bShow;
		userInfo = mContext.getSharedPreferences("diyBook", mContext.MODE_WORLD_READABLE);
		if(bShow){
			showDialog();
		}
	}
	public void checkUpdate(){
		if(checkNetUtils.checkNet(mContext)){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					result = mUpdate.getUpdateResult();
					if(result != null){
						try {
							JSONObject obj = new JSONObject(result);
							mVerCode = obj.getString("version_num");
							mVerName = obj.getString("version_code");
							mMin = obj.getString("allow_min");
							mApkUrl = obj.getString("update_link");
							SharedPreferences.Editor editor = userInfo.edit();
							editor.putString("bShare", obj.getString("share"));
							editor.commit();
							Utils.bShare = Integer.parseInt(obj.getString("share"));
							mark = obj.getString("mark");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						verCode = mUpdate.getVerCode(mContext);
						verName = mUpdate.getVerName(mContext);
						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);
					}else{
						if(bShow){
							Message msg = new Message();
							msg.what = 2;
							mHandler.sendMessage(msg);
						}
						Log.i("update", "连接网络失败");
					}
				}
			}).start();
		}else{
			if(bShow){
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessage(msg);
			}
			Log.i("update", "连接网络失败");
		}
	}
	Dialog mDialog;
	void showDialog(){
		mDialog = new commentDialogUtils(mContext, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, R.layout.show_dialog,  R.style.Theme_dialog);
		mDialog.show();
	}
	Dialog showTip;
	void showtip(){
		showTip = new commentDialogUtils(mContext, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, R.layout.showtip,  R.style.Theme_dialog);
		ImageButton ensure =(ImageButton)showTip. findViewById(R.id.showtip_iBnt2);
		ensure.setOnClickListener(this);
		showTip.show();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.showtip_iBnt2:
			showTip.dismiss();
			break;
		}
	}
}
