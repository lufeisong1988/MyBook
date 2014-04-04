package com.starbaby.diyBook.main;
/**
 * 个人中心自定义分享
 */
import java.io.File;
import java.util.HashMap;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import com.starbaby.diyBook.R;
import com.starbaby.diyBook.helper.BookMusicHelper;
import com.starbaby.diyBook.utils.Utils;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "ShowToast", "HandlerLeak", "InlinedApi" })
public class Share extends BaseActivity implements OnClickListener{
	private ImageButton iBnt1,iBnt2;
	private ImageView iv;
	private TextView tv;
	private String name = null;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				Toast.makeText(Share.this, "分享成功", 1000).show();
				SharedPreferences sp = Share.this.getSharedPreferences("diyBook", MODE_WORLD_READABLE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putBoolean("bAllow", true);
				editor.commit();
				
				break;
			case 2:
				Toast.makeText(Share.this, "分享失败", 1000).show();
				
				break;
			case 3:
				Toast.makeText(Share.this, "分享取消", 1000).show();
				
				break;
			}
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sharenote);
		init();
		listener();
		name = getIntent().getExtras().getString("name");
	}
	private void init() {
		iBnt1 = (ImageButton) findViewById(R.id.sharenote_back);
		iBnt2 = (ImageButton) findViewById(R.id.sharenote_ensure);
		iv = (ImageView) findViewById(R.id.sharenote_iv);
		tv =(TextView) findViewById(R.id.sharenote_tv2);
		tv.setText("用了下“星宝宝童书”这个app,效果不错哦！");
		Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.starbaby);
		if(bit != null){
			iv.setImageBitmap(bit);
		}
	}
	private void listener() {
		iBnt1.setOnClickListener(this);
		iBnt2.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		new BookMusicHelper(this).pressBnt();
		switch(v.getId()){
		case R.id.sharenote_back:
			this.finish();
			break;
		case R.id.sharenote_ensure:
			if(name.equals("weChatFriend")){
				wechatMoments();
			}else if(name.equals("weChat")){
				weChat();
			}
			break;
		}
	}

	/*
	 * 微信朋友圈
	 */
	void wechatMoments(){
		Platform wechatmoments = ShareSDK.getPlatform(this, WechatMoments.NAME);
		wechatmoments.setPlatformActionListener(new PlatformActionListener() {
			
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onCancel(Platform arg0, int arg1) {
				Message msg = new Message();
				msg.what = 3;
				mHandler.sendMessage(msg);
			}
		});
		WechatMoments.ShareParams sp = new WechatMoments.ShareParams(); 
		sp.imagePath = Utils.shareQQ + "qqPic.jpg";
		sp.shareType = Platform.SHARE_IMAGE;
		wechatmoments.share(sp); // 执行图文分享 
	}
	/*
	 * 微信
	 */
	void weChat(){
		Platform wechat = ShareSDK.getPlatform(this, Wechat.NAME);
		wechat.setPlatformActionListener(new PlatformActionListener() {
			
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onCancel(Platform arg0, int arg1) {
				Message msg = new Message();
				msg.what = 3;
				mHandler.sendMessage(msg);
			}
		});
		Wechat.ShareParams sp = new Wechat.ShareParams(); 
		sp.imagePath = Utils.shareQQ + "qqPic.jpg";
		sp.shareType = Platform.SHARE_IMAGE;
		wechat.share(sp); // 执行图文分享 
	}
	
	File file = new File("mnt/sdcard/saber.jpg");
	private void shareToFriend(File file) {
		
		Intent intent = new Intent();
		ComponentName comp = new ComponentName("com.tencent.mm",
				"com.tencent.mm.ui.tools.ShareImgUI");
		intent.setComponent(comp);
		intent.setAction("android.intent.action.SEND");
		intent.setType("image/*");
		//intent.setFlags(0x3000001);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		startActivity(intent);
	}
	private void shareToTimeLine(File file) {
		Intent intent = new Intent();
		ComponentName comp = new ComponentName("com.tencent.mm",
				"com.tencent.mm.ui.tools.ShareToTimeLineUI");
		intent.setComponent(comp);
		intent.setAction("android.intent.action.SEND");
		intent.setType("image/*");
		//intent.setFlags(0x3000001);
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		startActivity(intent);
	}
}
