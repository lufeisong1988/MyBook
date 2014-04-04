/*package com.starbaby.diyBook.clientui;

import org.apache.http.message.BasicNameValuePair;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientbean.User;
import com.starbaby.diyBook.clientcommon.EncodeUtil;
import com.starbaby.diyBook.clientcommon.StringUtils;
import com.starbaby.diyBook.clientcommon.UIHelper;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

 

public class Login extends BaseActivity {
	private  AppContext ac; 
	private AutoCompleteTextView mAccount;
	private EditText  mPwd;
	private ImageView btn_login;
	private ImageView btn_reg;
	private ImageView btn_back;
	private View loginLoading;
    private ProgressDialog mProgress;
	
  	//private int curLoginType ;
	private AnimationDrawable loadingAnimation;
	private InputMethodManager imm;
	
	public final static int LOGIN_OTHER = 0x00;
	public final static int LOGIN_MAIN= 0x01;
 
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		//curLoginType = getIntent().getIntExtra("LOGINTYPE", LOGIN_OTHER);
	
 		loginLoading = (View)findViewById(R.id.login_loading);
		mAccount = (AutoCompleteTextView)findViewById(R.id.login_form_account);
		mPwd = (EditText)findViewById(R.id.login_form_pwd);
		
	    btn_login = (ImageView)findViewById(R.id.login_lgn_btn);
	        btn_login.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					//隐藏软键盘
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);  
					
					String account = mAccount.getText().toString();
					String pwd = mPwd.getText().toString();
					System.out.println("pwd==="+pwd);
				//	boolean isRememberMe = chb_rememberMe.isChecked();
					//判断输入
					if(StringUtils.isEmpty(account)){
						UIHelper.ToastMessage(v.getContext(), getString(R.string.msg_login_email_null));
						return;
					}
					if(StringUtils.isEmpty(pwd)){
						UIHelper.ToastMessage(v.getContext(), getString(R.string.msg_login_pwd_null));
						return;
					}
				 
 			        //密码加密
			        pwd = EncodeUtil.getMD5(pwd.getBytes());	
			        System.out.println("pwd加密后==="+pwd);
			        login(account, pwd);
				}
			});

	    	//是否显示登录信息
	       ac = (AppContext)getApplication();
	       ac.initLoginInfo();
	        if(ac.isLogin()){
	        	//UIHelper.showFriends(this, ac.getLoginUid(), ac.getLoginPwd());
				BasicNameValuePair pairUid = new BasicNameValuePair("uid",new Integer(ac.getLoginUid()).toString());
				BasicNameValuePair pairPwd = new BasicNameValuePair("pwd",ac.getLoginPwd());
	        	UIHelper.showActivity(this, SelectMenu.class, pairUid,pairPwd);
	        }else{
		        User user = ac.getLoginInfo();
		        if(user==null ) return;
		        if(!StringUtils.isEmpty(user.getUname())){
		        	mAccount.setText(user.getUname());
		        	mAccount.selectAll();
		        }
	        }
	        
	}
 
	 //登录验证
    private void login(final String account, final String pwd) {
    	mProgress = ProgressDialog.show(this, "正在登陆验证账号", "登陆中···",true,true); 
    	btn_login.setEnabled(false);
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				btn_login.setEnabled(true);
				if(mProgress!=null)mProgress.dismiss();
				if(msg.what == 1){
					User user = (User)msg.obj;
					if(user != null){
						//清空原先cookie
						//ApiClient.cleanCookie();
						//提示登陆成功
						UIHelper.ToastMessage(Login.this, R.string.msg_login_success);
					//	UIHelper.showFriends(Login.this, user.getUid(), user.getPasswd());
						//Base
					 
						BasicNameValuePair pairUid = new BasicNameValuePair("uid",new Integer(user.getUid()).toString());
						BasicNameValuePair pairPwd = new BasicNameValuePair("pwd", user.getPasswd());
						UIHelper.showActivity(Login.this,SelectMenu.class,pairUid,pairPwd);
						finish();
					}
				}else if(msg.what == -1){
					((AppException)msg.obj).makeToast(Login.this);
				}else if(msg.what== 0 ){
					UIHelper.ToastMessage(Login.this, getString(R.string.msg_login_fail)+","+getString(R.string.msg_login_error_five));
				} else if(msg.what==-4){
					UIHelper.ToastMessage(Login.this, R.string.network_not_connected);
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg =new Message();
				try {
					if(ac.isNetworkConnected()){
		                User user = ac.loginVerify(account, pwd);
			                if(user.getResult()==1){
			                    user.setPasswd(pwd);
			                	ac.saveLoginInfo(user);
			                	msg.what = 1;
			                	msg.obj = user;
			                }else{
			                	ac.cleanLoginInfo();//清除登陆信息
			                	msg.what = 0;
			                }
					}else {
						msg.what = -4;
					}
	            } catch (AppException e) {
	            	e.printStackTrace();
			    	msg.what = -1;
			    	msg.obj = e;
	            }
				handler.sendMessage(msg);
			}
		}.start();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		this.onDestroy();
    	}
    	return super.onKeyDown(keyCode, event);
    }
}
*/