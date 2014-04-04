package com.starbaby.diyBook.view;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;


import com.starbaby.diyBook.R;
import com.starbaby.diyBook.cache.ImageGetFromHttp;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientbean.User;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.controller.SavePic;
import com.starbaby.diyBook.main.RegisterAccount;
import com.starbaby.diyBook.utils.EncodeUtil;
import com.starbaby.diyBook.utils.SdcardpathUtils;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.checkNetUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class EnterActivityView extends Activity implements OnClickListener{
	Button register,enter;
	ImageButton close;
	EditText psw,name;
	RelativeLayout enterPb,rl;
	AppContext ac;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case -1:
				break;
			case 0:
				UIHelper.ToastMessage(EnterActivityView.this, getString(R.string.msg_login_fail)+","+getString(R.string.msg_login_error_five));
				break;
			case -2:
				UIHelper.ToastMessage(EnterActivityView.this, R.string.network_not_connected);
				break;
			case 9:
				Toast.makeText(EnterActivityView.this, "登入成功", Toast.LENGTH_LONG).show();
			
				if(Utils.bReturnEnter){
					setResult(0, null);
				}
				EnterActivityView.this.finish();
				break;
			}
			enterPb.setVisibility(View.GONE);
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter);
		init();
		listener();

	}

	private void init() {
		register = (Button) findViewById(R.id.enter_bnt1);//注册
		enter = (Button) findViewById(R.id.enter_bnt3);//登入
		close = (ImageButton) findViewById(R.id.enter_iBnt1);
		name = (EditText) findViewById(R.id.enter_nicknameET);
		psw = (EditText) findViewById(R.id.enter_psw);
		enterPb = (RelativeLayout) findViewById(R.id.enter_showDialog);
		rl = (RelativeLayout) findViewById(R.id.enter_rl);
		enterPb.setVisibility(View.GONE);
		
	}
	void listener(){
		register.setOnClickListener(this);
		enter.setOnClickListener(this);
		close.setOnClickListener(this);
		rl.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.enter_rl:
			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(EnterActivityView.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
			break;
		case R.id.enter_bnt1://注册
			startActivity(new Intent(this,RegisterAccount.class));
			this.finish();
			overridePendingTransition(R.anim.workbook_in,R.anim.none);
			break;
		case R.id.enter_bnt3://登入
			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(EnterActivityView.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
			enter();
			break;
		case R.id.enter_iBnt1://关闭登入
			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(EnterActivityView.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
			this.finish();
			break;
		}
	}
	/*
	 * 登入
	 */
	String ename;
	String epsw;
	public void enter() {
		ename = name.getText().toString();
		epsw = psw.getText().toString();
		if(ename != null && !ename.equals("")  && epsw != null && !epsw.equals("") ){
			String psw = EncodeUtil.getMD5(epsw.getBytes());// 密码MD5
			if(checkNetUtils.checkNet(this)){
				enterPb.setVisibility(View.VISIBLE);
				login(ename, psw);
			}else{
				Toast.makeText(this, "网络连接失败", Toast.LENGTH_LONG).show();
			}
		}else{
			Toast.makeText(this, "请输入完整信息", Toast.LENGTH_LONG).show();
		}
	}
	 //登录验证
    private void login(final String account, final String pwd) {
		new Thread(){
			public void run() {
				//是否显示登录信息
				ac = (AppContext)getApplication();
				try {
					if (ac.isNetworkConnected()) {
						User user = ac.loginVerify(account, pwd);
						if (user.getResult() == 1) {
							user.setPasswd(pwd);
							ac.saveLoginInfo(user);

							if (user != null) {
								// 提示登陆成功
								uid = user.getUid();
								avatar = user.getFace();
								email = user.getEmail();
								SharedPreferences userInfo = EnterActivityView.this.getSharedPreferences("diyBook",MODE_WORLD_READABLE);
								SharedPreferences.Editor editor = userInfo.edit();
								editor.putString("name", account);
								editor.putString("psw", pwd);
								editor.putInt("uid", uid);
								editor.putString("avatar", avatar);
								editor.commit();
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss     ");
								Date currentTime = new Date(System.currentTimeMillis());
								String str = formatter.format(currentTime);
								Utils.mDBUserInfoHelper.deleteEnterTime(uid);
								Utils.mDBUserInfoHelper.saveEnterTime(uid, str);
								headBit = new ImageGetFromHttp().downloadBitmap(avatar);

								if (headBit != null) {
									try {
										new SavePic().saveHeadImg(headBit,SdcardpathUtils.headName);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								showMessage(9);
							}

						} else {
							ac.cleanLoginInfo();// 清除登陆信息
							showMessage(0);
						}
					} else {
						showMessage(-2);
					}
				} catch (AppException e) {
					e.printStackTrace();
					showMessage(-1);
				}
			}
		}.start();
    }
    
	Bitmap headBit = null;
	String avatar = null;
	int uid;
	String email;
	public boolean onTouchEvent(MotionEvent event) {
		try{
			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(EnterActivityView.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
		}catch(Exception e){
			e.printStackTrace();
			Log.i("EnterActivity exception",e.toString());
		}
		return super.onTouchEvent(event);
    }
	private void showMessage(int msgWhat){
		Message msg = new Message();
		msg.what = msgWhat;
		mHandler.sendMessage(msg);
	}
}
