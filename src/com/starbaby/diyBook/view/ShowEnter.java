package com.starbaby.diyBook.view;
//package com.starbaby.diyBook.View;
//
//import android.app.ActionBar.LayoutParams;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.RelativeLayout;
//import android.widget.Toast;
//
//import com.starbaby.diyBook.R;
//import com.starbaby.diyBook.Controller.Login;
//import com.starbaby.diyBook.Main.RegisterAccount;
//import com.starbaby.diyBook.Utils.EncodeUtil;
//import com.starbaby.diyBook.Utils.checkNetUtils;
//import com.starbaby.diyBook.Utils.commentDialogUtils;
//
//public class ShowEnter implements OnClickListener{
//	Context mContext;
//	EditText name = null;
//	EditText psw  = null;
//	RelativeLayout enterPb;
//	commentDialogUtils diaolog;
//	SharedPreferences userInfo;
//	public ShowEnter(Context mContext,SharedPreferences userInfo){
//		((Activity) mContext).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);  
//		this.mContext = mContext;
//		this.userInfo = userInfo;
//	}
//	/*
//	 * 展示登入界面
//	 */
//	
//	public void showEnter(){
//		diaolog = new commentDialogUtils(mContext, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, R.layout.enter , R.style.Theme_dialog);
//		diaolog.show();
//		Button register = (Button)diaolog. findViewById(R.id.enter_bnt1);//注册
//		Button enter = (Button)diaolog. findViewById(R.id.enter_bnt3);//登入
//		ImageButton close = (ImageButton)diaolog. findViewById(R.id.enter_iBnt1);
//		name = (EditText)diaolog. findViewById(R.id.enter_nicknameET);
//		psw = (EditText)diaolog. findViewById(R.id.enter_psw);
//		enterPb = (RelativeLayout)diaolog. findViewById(R.id.enter_showDialog);
//		enterPb.setVisibility(View.GONE);
//		register.setOnClickListener(this);
//		enter.setOnClickListener(this);
//		close.setOnClickListener(this);
//	}
//	@Override
//	public void onClick(View v) {
//		switch(v.getId()){
//		case R.id.enter_bnt1://注册
//			mContext.startActivity(new Intent(mContext,RegisterAccount.class));
//			((Activity) mContext).overridePendingTransition(R.anim.workbook_in,R.anim.none);
//			break;
//		case R.id.enter_bnt3://登入
//			enter();
//			break;
//		case R.id.enter_iBnt1://关闭登入
//			diaolog.cancel();
//			break;
//		}
//	}
//	/*
//	 * 登入
//	 */
//	public void enter() {
//		String ename = name.getText().toString();
//		String epsw = psw.getText().toString();
//		if(ename != null && !ename.equals("")  && epsw != null && !epsw.equals("") ){
//			String psw = EncodeUtil.getMD5(epsw.getBytes());// 密码MD5
//			if(checkNetUtils.checkNet(mContext)){
//				enterPb.setVisibility(View.VISIBLE);
//				new Login(mContext,diaolog,userInfo,enterPb).login(ename, psw);
//			}else{
//				Toast.makeText(mContext, "网络连接失败", 1000).show();
//			}
//		}else{
//			Toast.makeText(mContext, "请输入完整信息", 1000).show();
//		}
//	}
//}
