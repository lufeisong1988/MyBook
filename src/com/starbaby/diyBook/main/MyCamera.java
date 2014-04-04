package com.starbaby.diyBook.main;
/**
 * 自定义相机
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.starbaby.diyBook.R;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.utils.Utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera; 
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Size;

public class MyCamera extends Activity implements SurfaceHolder.Callback,Camera.PictureCallback,OnClickListener{
	String TAG = "MyCamera";
	double scale = Double.MAX_VALUE;
	double scaleScreen = Double.MAX_VALUE;
	String direction;
	
	SurfaceView mSurfaceView;
	Button takePhoto,cancle;
	ImageView cameraBgLeft,cameraBgRight;
	ImageView coverImg;
	
	Camera camera;
	SurfaceHolder holder;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				setResult(1,null);
				MyCamera.this.finish();
				overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
				break;
			}
			super.handleMessage(msg);
		}
	};
	AutoFocusCallback mAutoFocusCallback = new AutoFocusCallback() {//自动聚焦
		
		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.camera);
		init();
		listener();
		Login();
		
	}

	private void init() {
		mSurfaceView = (SurfaceView) findViewById(R.id.camera);
		takePhoto = (Button) findViewById(R.id.camera_bnt1);
		cancle = (Button) findViewById(R.id.camera_cancle);
		cameraBgLeft = (ImageView) findViewById(R.id.camera_bg_left);
		cameraBgRight = (ImageView) findViewById(R.id.camera_bg_right);
		coverImg = (ImageView) findViewById(R.id.camera_cover);
		coverImg.setVisibility(View.INVISIBLE);
	}
	private void listener(){
		mSurfaceView.setOnClickListener(this);
		mSurfaceView.setFocusable(true);
		mSurfaceView.setFocusableInTouchMode(true);
		takePhoto.setOnClickListener(this);
		cancle.setOnClickListener(this);
	}
	float scalePic = 0;
	Bitmap scaleBitRight;
	Bitmap scaleBitLeft;
	int[] bundleLeft;
	int[] bundleRight;
	ArrayList<String> playList;
	ArrayList<Integer> playId;
	int currentPage;
	int currentId;
	@SuppressWarnings("deprecation")
	private void Login(){
		bundleLeft = getIntent().getExtras().getIntArray("locationLeft");
		bundleRight = getIntent().getExtras().getIntArray("locationRight");
		playList = getIntent().getExtras().getStringArrayList("playList");
		playId = getIntent().getExtras().getIntegerArrayList("playId");
		currentPage = getIntent().getExtras().getInt("currentPage");
		direction = getIntent().getExtras().getString("direction");
		currentId = getIntent().getExtras().getInt("currentId");
		
		holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this);
		Bitmap orgBitLeft = null;
		Bitmap orgBitRight = null;
		if(currentPage > 0 ){
			if(direction.equals("left")){
				orgBitLeft = BitmapFactory.decodeFile(Utils.path + NamePic.mp3UrlToFileName(playList.get(playId.get(currentId) - 1).toString()));
				orgBitRight = BitmapFactory.decodeFile(Utils.path + NamePic.mp3UrlToFileName(playList.get(playId.get(currentId) ).toString()));
			}else if(direction.equals("right")){
				orgBitLeft = BitmapFactory.decodeFile(Utils.path + NamePic.mp3UrlToFileName(playList.get(playId.get(currentId) - 2).toString()));
				orgBitRight = BitmapFactory.decodeFile(Utils.path + NamePic.mp3UrlToFileName(playList.get(playId.get(currentId) - 1).toString()));
			}
			float scaleW = (float)orgBitRight.getWidth() / (Utils.DMWidth / 2);
			float scaleH = (float)orgBitRight.getHeight() / Utils.DMHeight;
			if(scaleW > scaleH){
				scalePic = scaleW;
			}else{
				scalePic = scaleH;
			}
			scaleBitLeft = Bitmap.createScaledBitmap(orgBitLeft, (int)(orgBitLeft.getWidth()  / scalePic), (int)(orgBitLeft.getHeight()  / scalePic), false);
			scaleBitRight = Bitmap.createScaledBitmap(orgBitRight, (int)(orgBitRight.getWidth()  / scalePic), (int)(orgBitRight.getHeight()  / scalePic), false);
			cameraBgLeft.setLayoutParams(new LinearLayout.LayoutParams((int)(orgBitLeft.getWidth() / scalePic), (int)(orgBitLeft.getHeight() / scalePic)));
			cameraBgRight.setLayoutParams(new LinearLayout.LayoutParams((int)(orgBitRight.getWidth() / scalePic), (int)(orgBitRight.getHeight() / scalePic)));
			cameraBgLeft.setImageBitmap(scaleBitLeft);
			cameraBgRight.setImageBitmap(scaleBitRight);
			DrawShadow(cameraBgLeft,cameraBgRight,scaleBitLeft,scaleBitRight,(int)(orgBitLeft.getWidth() / scalePic), (int)(orgBitLeft.getHeight() / scalePic));
			orgBitLeft.recycle();
			orgBitLeft = null;
			orgBitRight.recycle();
			orgBitRight = null;
		}else if(currentPage == 0){
			orgBitLeft = BitmapFactory.decodeFile(Utils.path + NamePic.mp3UrlToFileName(playList.get(playId.get(currentPage) - 1).toString()));
			orgBitRight = BitmapFactory.decodeFile(Utils.path + NamePic.mp3UrlToFileName(playList.get(playId.get(currentPage) - 1).toString()));
			float scaleW = (float)orgBitRight.getWidth() / (Utils.DMWidth / 2);
			float scaleH = (float)orgBitRight.getHeight() / Utils.DMHeight;
			if(scaleW > scaleH){
				scalePic = scaleW;
			}else{
				scalePic = scaleH;
			}
			scaleBitLeft = Bitmap.createScaledBitmap(orgBitLeft, (int)(orgBitLeft.getWidth()  / scalePic), (int)(orgBitLeft.getHeight()  / scalePic), false);
			scaleBitRight = Bitmap.createScaledBitmap(orgBitRight, (int)(orgBitRight.getWidth()  / scalePic), (int)(orgBitRight.getHeight()  / scalePic), false);
			cameraBgLeft.setLayoutParams(new LinearLayout.LayoutParams((int)(orgBitLeft.getWidth() / scalePic), (int)(orgBitLeft.getHeight() / scalePic)));
			cameraBgRight.setLayoutParams(new LinearLayout.LayoutParams((int)(orgBitRight.getWidth() / scalePic), (int)(orgBitRight.getHeight() / scalePic)));
			cameraBgLeft.setImageBitmap(scaleBitLeft);
			cameraBgLeft.setVisibility(View.INVISIBLE);
			cameraBgRight.setImageBitmap(scaleBitRight);
			DrawShadow(null,cameraBgRight,null,scaleBitRight,(int)(orgBitRight.getWidth() / scalePic), (int)(orgBitRight.getHeight() / scalePic));
			orgBitLeft.recycle();
			orgBitLeft = null;
			orgBitRight.recycle();
			orgBitRight = null;
		}
		
	}
	void DrawShadow(ImageView BgLeft,ImageView BgRight,Bitmap scaleBitLeft,Bitmap scaleBitRight,int w,int h){
		Bitmap bit = Bitmap.createBitmap(Utils.DMWidth, Utils.DMHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(bit);
		Paint p = new Paint();  
        p.setAntiAlias(true);  
        p.setFilterBitmap(false);
        p.setColor(Color.TRANSPARENT);
        canvas.drawRect(0, 0, Utils.DMWidth, Utils.DMHeight, p);
        int sc = canvas.saveLayer(0, 0, Utils.DMWidth, Utils.DMHeight, null,  
                Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG  
                        | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG  
                        | Canvas.FULL_COLOR_LAYER_SAVE_FLAG  
                        | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        p.setColor(Color.BLACK);
        p.setAlpha(175);
        canvas.drawRect(0, 0, Utils.DMWidth, Utils.DMHeight, p);        //DST
        p.setXfermode(new PorterDuffXfermode(Mode.XOR));  
        p.setColor(Color.WHITE);
        if(direction.equals("right")){
        	canvas.drawRect(bundleRight[0], bundleRight[1], bundleRight[0] + w, bundleRight[1] + h, p);        //SRC
            p.setXfermode(null);  
            Paint colorPaint = new Paint();
            colorPaint.setColor(0xff7cfc00);
            colorPaint.setStyle(Style.STROKE);//设置填满  
            colorPaint.setStrokeWidth(2);
            canvas.drawRect(bundleRight[0], bundleRight[1], bundleRight[0] + w, bundleRight[1] + h, colorPaint);
        }else if(direction.equals("left")){
        	canvas.drawRect(bundleRight[0] - w, bundleRight[1], bundleRight[0] , bundleRight[1] + h, p);        //SRC
            p.setXfermode(null);  
            Paint colorPaint = new Paint();
            colorPaint.setColor(0xff7cfc00);
            colorPaint.setStyle(Style.STROKE);//设置填满  
            colorPaint.setStrokeWidth(2);
            canvas.drawRect(bundleRight[0] - w, bundleRight[1], bundleRight[0] , bundleRight[1] + h, colorPaint);
        }
        
        canvas.restoreToCount(sc);
		
		coverImg.setImageBitmap(bit);
		coverImg.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.i("lenovo","changed");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		
		double value = 1.0f;
		double preScale = 1.0f;
		double picValue = 1.0f;
		double width = 0,height = 0;
		double picWidth = 0,picHeight = 0;
		scaleScreen = (double) Utils.DMWidth / Utils.DMHeight;
		if(camera == null){
			camera = Camera.open();
			Camera.Parameters params = camera.getParameters();
			List<Size> size = params.getSupportedPreviewSizes();
			List<Size> picSize = params.getSupportedPictureSizes();
			// 获取一个适配屏幕大小的分辨率
			for (int i = 0; i < size.size(); i++) {
				Size fitsize = size.get(i);
				scale = (double) fitsize.width / fitsize.height;
				double currentValue = Math.abs(scale - scaleScreen);
				if(currentValue < value){
					width = fitsize.width;
					height = fitsize.height;
					value = currentValue;
					preScale = scale;
				}
			}
			//寻找最匹配的照片像素比列
			for(int j = 0;j < picSize.size();j++){
				Size fitPicSize = picSize.get(j);
				double picScale = (double)fitPicSize.width / fitPicSize.height;
				double currentPicValue = Math.abs(preScale - picScale);
				Log.i("currentPicValue",currentPicValue + "");
				if(currentPicValue < picValue || currentPicValue == picValue){
					picValue = currentPicValue;
					picWidth = fitPicSize.width;
					picHeight = fitPicSize.height;
					Log.i("picWidth",picWidth + "");
					Log.i("picHeight",picHeight + "");
					Log.i("picValue",picValue + "");
				}
				Log.i("pictureSize","======================");
			}
			Log.i("setPictureSize",picWidth + ":" + picHeight);
			Log.i("setPreviewSize",width + ":" + height);
			params.setPictureSize((int)picWidth,(int)picHeight);
			params.setPreviewSize((int)width,(int)height);
			camera.setParameters(params);
			try {
				camera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
			}
			camera.startPreview();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(camera != null){
			Log.i("lenovo","destory");
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}

	@Override
	public void onPictureTaken(final byte[] data, final Camera camera) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Bitmap screenBit = null;
				Bitmap scaleBit = null;
				Bitmap cutBit = null;
				Bitmap resultBit = null;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				
				screenBit = BitmapFactory.decodeByteArray(data, 0, data.length);
				scaleBit = Bitmap.createScaledBitmap(screenBit, Utils.DMWidth, Utils.DMHeight, false);
				int[] location = new int[2];
				ImageView cameraBg = null;
				Bitmap scaleBitmap = null;
				if(direction.equals("right")){
					cameraBg = cameraBgRight;
					scaleBitmap = scaleBitRight;
				}else if(direction.equals("left")){
					cameraBg = cameraBgLeft;
					scaleBitmap = scaleBitLeft;
				}
				cameraBg.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				if(x < 0 || x == 0){
					x = 0;
				}
				if(y < 0 || y == 0 ){
					y = 0;
				}
				int w = scaleBitmap.getWidth();
				int h = scaleBitmap.getHeight();
				if(w > scaleBit.getWidth()){
					w = scaleBit.getWidth();
				}
				if(h > scaleBit.getHeight()){
					h = scaleBit.getHeight();
				}
				cutBit = Bitmap.createBitmap(scaleBit, x, y, w , h );
				resultBit = Bitmap.createBitmap(w, h, Config.ARGB_8888);
				Canvas canvas = new Canvas(resultBit);
				canvas.drawBitmap(cutBit, 0, 0, null);
				canvas.drawBitmap(scaleBitmap, 0, 0, null);
				canvas.save();
				File file = new File(Utils.takePhoto);
				file.mkdirs();
				String[] str = (NamePic.mp3UrlToFileName(playList.get(playId.get(currentId) - 1).toString())).split("\\.");
				String picName = str[0] + ".jpg";
				File saveFile = new File(Utils.path +  picName);
				try {
					saveFile.createNewFile();
					FileOutputStream fos = new FileOutputStream(saveFile);
					resultBit.compress(Bitmap.CompressFormat.JPEG, 100, fos);
					fos.flush();
					fos.close();
					screenBit.recycle();
					screenBit = null;
					scaleBit.recycle();
					scaleBit = null;
					cutBit.recycle();
					cutBit = null;
					resultBit.recycle();
					resultBit = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
			}
		}).start();
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * camera监听事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.camera:
			camera.autoFocus(mAutoFocusCallback);
			break;
		case R.id.camera_bnt1:
			camera.takePicture(null, null, this);
			break;
		case R.id.camera_cancle:
			MyCamera.this.finish();
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			break;
		}
	}
	
}
