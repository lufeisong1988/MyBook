package com.starbaby.diyBook.main;
/**
 * 用户注册
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.controller.SavePic;
import com.starbaby.diyBook.helper.BookMusicHelper;
import com.starbaby.diyBook.net.AsyncHttpPost;
import com.starbaby.diyBook.net.DefaultThreadPool;
import com.starbaby.diyBook.net.RequestParameter;
import com.starbaby.diyBook.net.RequestResultCallback;
import com.starbaby.diyBook.utils.EncodeUtil;
import com.starbaby.diyBook.utils.HttpPort;
import com.starbaby.diyBook.utils.JsonObject;
import com.starbaby.diyBook.utils.SdcardpathUtils;
import com.starbaby.diyBook.utils.UploadUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class RegisterAccount extends BaseActivity implements OnClickListener{
	private ImageButton man,woman;
	private LinearLayout headImg;
	private EditText nickName, name,psw;
	private ImageView headIV;
	private Button registerBnt,back;
	private boolean addHead = false;
	private boolean sex = true;//选择 男
	private String sexNo = "1";
	private String picPath = null;
	private String contentUrl = null;
	private BookMusicHelper mBookMusicHelper;
	private ProgressBar pb;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				Toast.makeText(RegisterAccount.this, "注册失败", 1000).show();
				pb.setVisibility(8);
				break;
			case 2:
				pb.setVisibility(8);
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userregister);
		mBookMusicHelper = new BookMusicHelper(this);
		init();
		listener();
	}

	private void init() {
		back = (Button) findViewById(R.id.register_back);
		man = (ImageButton) findViewById(R.id.iBnt1);
		woman = (ImageButton) findViewById(R.id.iBnt2);
		nickName = (EditText) findViewById(R.id.nicknameET);
		name = (EditText) findViewById(R.id.nameET);
		psw = (EditText) findViewById(R.id.pswET);
		headIV = (ImageView) findViewById(R.id.iv1);
		registerBnt = (Button) findViewById(R.id.registerBnt);
		pb = (ProgressBar) findViewById(R.id.register_pb);
		headImg = (LinearLayout) findViewById(R.id.headImg);
		pb.setVisibility(8);
	}

	private void listener() {
		back.setOnClickListener(this);
		man.setOnClickListener(this);
		woman.setOnClickListener(this);
		headIV.setOnClickListener(this);
		registerBnt.setOnClickListener(this);
		headImg.setOnClickListener(this);
		}

	@Override
	public void onClick(View v) {
		new BookMusicHelper(this).pressBnt();
		switch(v.getId()){
		case R.id.register_back://返回
			this.finish();
			overridePendingTransition(R.anim.none,R.anim.workbook_out);
			break;
		case R.id.iBnt1:
			if(sex){
				sexNo = "1";
			}else{
				man.setBackgroundResource(R.drawable.choose_sex_in);
				woman.setBackgroundResource(R.drawable.choose_sex_over);
				sex = true;
				sexNo = "2";
			}
			break;
		case R.id.iBnt2:
			if (sex) {
				man.setBackgroundResource(R.drawable.choose_sex_over);
				woman.setBackgroundResource(R.drawable.choose_sex_in);
				sex = false;
				sexNo = "1";
			} else {
				sexNo = "2";
			}
			break;
		case R.id.headImg://添加头像
			showDialog();
			break;
		case R.id.registerBnt://注册提交
			pb.setVisibility(1);
			if(nickName.getText().toString() == null || nickName.getText().toString().equals("")){
				Toast.makeText(this, "请输入昵称", 1000).show();
				pb.setVisibility(8);
			}else if(name.getText().toString() == null || name.getText().toString().equals("")){
				Toast.makeText(this, "请输入用户名", 1000).show();
				pb.setVisibility(8);
			}else if(psw.getText().toString() == null || psw.getText().toString().equals("")){
				Toast.makeText(this, "请输入密码", 1000).show();
				pb.setVisibility(8);
			}else if(!addHead){
				Toast.makeText(this, "请选择头像", 1000).show();
				pb.setVisibility(8);
			}else{
				// 前提：先发送头像给服务器
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {
						Message message = new Message();
						String userNick = nickName.getText().toString();// 昵称
						String userEmial = name.getText().toString();// 邮箱
						String userPwd = psw.getText().toString();// 用户输入的密码
						String userSex = sexNo;// 性别
						Integer userSys = 2;// android版本
						// 密码进行MD5加密
						String enPwd = EncodeUtil.getMD5(userPwd.getBytes());
						File file = new File(picPath);
						if (file != null) {
							String request = UploadUtil.uploadFile(file,HttpPort.registerImgUrl);
							if(request != null){
								// json解析获取的url
								try {
									JSONObject json = new JSONObject(request);
									contentUrl = json.getString("imageurl");
								} catch (JSONException e) {
									e.printStackTrace();
								}
								login(userNick, userEmial, enPwd, userSex, userSys,contentUrl);
							}else{
								Message msg = new Message();
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
						}
					}
				});
				thread.start();
			}
			break;
			
		}
		
	}
	public void showDialog(){
		new AlertDialog.Builder(this).setMessage("请选择头像").setNegativeButton("相册", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT,null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
				startActivityForResult(intent, 2);
			}
		}).setPositiveButton("拍照", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"temp.jpg")));
				startActivityForResult(intent, 1);
			}
		}).show();
	}
	// 个操作的相对相应事件
	@SuppressWarnings({ "deprecation", "unused", "static-access" })
	protected void onActivityResult(int requestCode, int resultCode,final Intent data) {
		if (resultCode == 0)
			return;
		// 拍照
		if (requestCode == 1) {
			// 设置文件保存路径这里放在跟目录下
			File picture = new File(Environment.getExternalStorageDirectory()+ "/temp.jpg");
			startPhotoZoom(Uri.fromFile(picture));
		}
		if (data == null)
			return;
		// 读取相册缩放图片
		if (requestCode == 2) {
			startPhotoZoom(data.getData());
		}
		// 处理结果
		if (requestCode == 3) {
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				// 保存头像到本地
				try {
					new SavePic().saveHeadImg(photo,SdcardpathUtils.headName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				picPath = SdcardpathUtils.headName;
				File tempFile = new File(picPath);
				Bitmap headImgBitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
				// 设置头像图片为圆角
				headIV.invalidate();
				headIV.setBackgroundDrawable(getWallpaper().createFromPath(picPath));
				addHead = true;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	// 头像剪裁操作
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}
	
	// 注册操作,首先获取传头像图片后获取的url
		private void login(final String userNick, String userEmial, final String userPwd,final String userSex, Integer userSys, final String userAvatar) {
			List<RequestParameter> parameterList = new ArrayList<RequestParameter>();
			parameterList.add(new RequestParameter("nick", userNick));
			parameterList.add(new RequestParameter("email", userEmial));
			parameterList.add(new RequestParameter("pwd", userPwd));
			parameterList.add(new RequestParameter("sex", userSex));
			parameterList.add(new RequestParameter("sys", userSys));
			parameterList.add(new RequestParameter("avatar", userAvatar));
			AsyncHttpPost httpost = new AsyncHttpPost(null,HttpPort.registerUrl, parameterList,new RequestResultCallback() {

						@Override
						public void onSuccess(Object o) {
							final String result = (String) o;
							RegisterAccount.this.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									try {
										int msg = new JsonObject().getMSG(result);
										if(msg==1){
											Toast.makeText(RegisterAccount.this, "注册成功", 1000).show();
											SharedPreferences.Editor editor = RegisterAccount.this.getSharedPreferences("diyBook",MODE_WORLD_READABLE).edit();
											editor.putString("name", userNick);
											editor.putString("psw", userPwd);
											editor.putInt("uid", new JsonObject().getUID(result));
											editor.putString("avatar", userAvatar);
											editor.commit();
											RegisterAccount.this.finish();
											overridePendingTransition(R.anim.none,R.anim.workbook_out);
										}else if(msg==-1){
											Toast.makeText(RegisterAccount.this, "用户名为空", 1000).show();
										}else if(msg==-2){
											Toast.makeText(RegisterAccount.this, "用户密码为空", 1000).show();
										}else if(msg==-6){
											Toast.makeText(RegisterAccount.this, "邮箱为空", 1000).show();
										}else if(msg==-7){
											Toast.makeText(RegisterAccount.this, "邮箱格式错误", 1000).show();
										}else if(msg==-8){
											Toast.makeText(RegisterAccount.this, "用户名应为0-15个字", 1000).show();
										}else if(msg==-9){
											Toast.makeText(RegisterAccount.this, "用户名已存在", 1000).show();
										}else if(msg==-10){
											Toast.makeText(RegisterAccount.this, "邮箱已存在", 1000).show();
										}else if(msg==-11){
											Toast.makeText(RegisterAccount.this, "注册失败", 1000).show();
										}else if(msg==-12){
											Toast.makeText(RegisterAccount.this, "未知错误", 1000).show();
										}else if(msg==-13){
											Toast.makeText(RegisterAccount.this, "请先上传头像", 1000).show();
										}
										Message msg2 = new Message();
										msg2.what = 2;
										mHandler.sendMessage(msg2);
									} catch (JSONException e) {
										e.printStackTrace();
									}
									
								}
							});
						}

						@Override
						public void onFail(Exception e) {
							Message msg = new Message();
							msg.what = 1;
							mHandler.sendMessage(msg);
						}
					});
			DefaultThreadPool.getInstance().execute(httpost);
		}

	/**
	 * (non-Javadoc)点击空白处 关闭软件盘
	 * 
	 * @see android.app.Activity#onTouchEvent(android.view.MotionEvent)
	 */
	// 软键盘消失方法
	public void CloseKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(nickName.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(psw.getWindowToken(), 0);

	}

	// 监听点击屏幕上任何位置软键盘消失
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		CloseKeyBoard();
		return super.dispatchTouchEvent(ev);
	}
}
