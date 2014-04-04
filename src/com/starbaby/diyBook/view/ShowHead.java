package com.starbaby.diyBook.view;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.controller.SavePic;
import com.starbaby.diyBook.main.MainActivity;
import com.starbaby.diyBook.main.RegisterAccount;
import com.starbaby.diyBook.net.AsyncHttpPost;
import com.starbaby.diyBook.net.DefaultThreadPool;
import com.starbaby.diyBook.net.RequestParameter;
import com.starbaby.diyBook.net.RequestResultCallback;
import com.starbaby.diyBook.utils.HttpPort;
import com.starbaby.diyBook.utils.JsonObject;
import com.starbaby.diyBook.utils.SdcardpathUtils;
import com.starbaby.diyBook.utils.UploadUtil;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ShowHead extends Activity{
	ImageView iv;
	Button bnt2;
	ProgressBar pb;
	ImageButton bnt;
	Bitmap headBit = null;
	String picPath = null;
	Bitmap photo = null;
	private String contentUrl = null;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				Toast.makeText(ShowHead.this, "上传成功", 2000).show();
				try {
					new SavePic().saveHeadImg(photo,SdcardpathUtils.headName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case 2:
				Toast.makeText(ShowHead.this, "请先上传头像", 2000).show();
				break;
			case 3:
				Toast.makeText(ShowHead.this, "上传失败", 2000).show();
				break;
			case 4:
				Toast.makeText(ShowHead.this, "连接网络失败", 2000).show();
				break;
			}
			pb.setVisibility(8);
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showhead);
		init();
		listener();
	}
	private void init() {
		iv = (ImageView) findViewById(R.id.showhead_iv);
		bnt = (ImageButton) findViewById(R.id.showhead_bnt);
		bnt2 = (Button) findViewById(R.id.showhead_bnt2);
		pb = (ProgressBar) findViewById(R.id.showhead_pb);
		bnt2.setVisibility(8);
		pb.setVisibility(8);
		headBit = BitmapFactory.decodeFile(SdcardpathUtils.headName);
		if(headBit != null){
			iv.setImageBitmap(headBit);
		}else{
			iv.setBackgroundResource(R.drawable.head);
		}
	}
	private void listener() {
		bnt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
		bnt2.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("unused")
			@Override
			public void onClick(View v) {
				pb.setVisibility(1);
				new Thread(){

					@Override
					public void run() {
						File file = new File(SdcardpathUtils.changeHeadName);
						if (file != null) {
							String request = UploadUtil.uploadFile(file,HttpPort.registerImgUrl);
							if(request != null){
								// 保存头像到本地
								try {
									JSONObject json = new JSONObject(request);
									contentUrl = json.getString("imageurl");
									LoginHeadImg(contentUrl);
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}else{
							Message msg = new Message();
							msg.what = 2;
							mHandler.sendMessage(msg);
						}
						super.run();
					}

				}.start();
			}
		});
		iv.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ShowHead.this.finish();
				return false;
			}
		});
	}
	/*
	 * 修改头像
	 */
	private void LoginHeadImg(String headImgUrl) {
		List<RequestParameter> parameterList = new ArrayList<RequestParameter>();
		SharedPreferences userInfo =  ShowHead.this.getSharedPreferences("diyBook",MODE_WORLD_READABLE);
		String MyPsw = userInfo.getString("psw", "");
		int MyUid = userInfo.getInt("uid", 0);
		parameterList.add(new RequestParameter("uid", Integer.toString(MyUid)));
		parameterList.add(new RequestParameter("pwd", MyPsw));
		parameterList.add(new RequestParameter("avatar", headImgUrl));
		AsyncHttpPost httpost = new AsyncHttpPost(null,HttpPort.changeHeadImg, parameterList,new RequestResultCallback() {

					@Override
					public void onSuccess(Object o) {
						final String result = (String) o;
						ShowHead.this.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								try {
									int msg = new JsonObject().getMSG(result);
									if(msg == 1){
										Message msg2 = new Message();
										msg2.what = 1;
										mHandler.sendMessage(msg2);
									}else{
										Message msg2 = new Message();
										msg2.what = 3;
										mHandler.sendMessage(msg2);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						});
					}

					@Override
					public void onFail(Exception e) {
						Log.i("GetBookCoverInfo",e.toString());
						Message msg = new Message();
						msg.what = 4;
						mHandler.sendMessage(msg);
					}
				});
		DefaultThreadPool.getInstance().execute(httpost);
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
					photo = extras.getParcelable("data");
					// 设置头像图片为圆角
					iv.invalidate();
					iv.setImageBitmap(photo);
					bnt2.setVisibility(1);
					// 保存头像到本地
					try {
						new SavePic().saveHeadImg(photo,SdcardpathUtils.changeHeadName);
					} catch (IOException e) {
						e.printStackTrace();
					}
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
		
}
