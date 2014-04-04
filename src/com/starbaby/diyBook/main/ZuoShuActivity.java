package com.starbaby.diyBook.main;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.controller.CopyFile;
import com.starbaby.diyBook.controller.CreatePageProvider;
import com.starbaby.diyBook.controller.CreateSizeChangedObserver;
import com.starbaby.diyBook.controller.GetArtBook;
import com.starbaby.diyBook.helper.DrawRect;
import com.starbaby.diyBook.helper.MusicHelper;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.model.CoverThreadPool3;
import com.starbaby.diyBook.net.AsyncHttpPost;
import com.starbaby.diyBook.net.DefaultThreadPool;
import com.starbaby.diyBook.net.RequestParameter;
import com.starbaby.diyBook.net.RequestResultCallback;
import com.starbaby.diyBook.utils.HttpPort;
import com.starbaby.diyBook.utils.JavaBean3;
import com.starbaby.diyBook.utils.JavaBeanZuoShu;
import com.starbaby.diyBook.utils.UploadUtil;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.checkNetUtils;
import com.starbaby.diyBook.utils.commentDialogUtils;
import com.starbaby.diyBook.view.CreateCurlView;
import com.starbaby.diyBook.view.EnterActivityView;
import com.starbaby.diyBook.view.ZuoShuEdit;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint({ "Recycle", "SdCardPath", "FloatMath", "HandlerLeak", "SimpleDateFormat", "WorldReadableFiles", "NewApi" })
public class ZuoShuActivity extends Activity implements OnTouchListener,OnClickListener{
	
	private ImageView imgPhotoLeft, imgFrameLeft,imgPhotoRight,imgFrameRight,shadowLeft,shadowRight;
	Bitmap photoLeft,frameLeft,photoRight,frameRight,resultLeft;;
	LinearLayout parent;
	LinearLayout zuoshuView;
//	ProgressBar savePb;
	
	CreateCurlView mCurlView;
	CreatePageProvider mPageProvider;
	CreateSizeChangedObserver mSizeChangedObserver;
	GetArtBook mGetArtBook;
	JavaBeanZuoShu mJavaBeanZuoShu ;
	SharedPreferences sp;
	
	private byte[] photoByte;
	private int currentId = 0;
	private int currentPage = 0;
	private boolean createCurlView = true;
	private ArrayList<String> imgUrlList;
	String tpl_id;
	String direction = "right";
	boolean bNext = true;//true 需要继续做图，false进入翻页
	public static int result_camera = 1;
	private boolean bClick = true;//防止重复点击保存键
	private boolean bToNext = true;//保存后，防止重复点击下一页
	private String product_id;//整本书提交后返回的书本product_id
	private int imageWidth;
	private int imageHeight;
	private int imageCount;
	private String tpl_name;
	
	private Matrix matrixLeft = new Matrix();
	private Matrix savedMatrixLeft = new Matrix();
	private Matrix matrixRight = new Matrix();
	private Matrix savedMatrixRight = new Matrix();
	// 进行缩放
	private PointF start = new PointF();
	private PointF mid = new PointF();
	float oldDist = 1f;
	private float newDist = 0;
	private float scale = 0;
	float oldDegrees = 0;
	float newDegress;
	int matrixScale = 0;
	//记录手指触摸状态
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;
	private boolean bLastOne = false;//判断是否是最后一页：如果是，直接发送所有url集合到服务器。如果不是，先发送图片获取url，再发送所有url集合到服务器
	private boolean bLoginCache = true;//true 第一次点击保存时 ，
	
	private Handler mHandler = new Handler(){

		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				bLastOne = false;
				updateTip.dismiss();
				Toast.makeText(ZuoShuActivity.this, "这是最后一页，即将进入预览模式。。。", Toast.LENGTH_LONG).show();
				parent.setVisibility(View.VISIBLE);
				
				mCurlView = (CreateCurlView) findViewById(R.id.zuoshu_curlView);
				mPageProvider = new CreatePageProvider(getApplicationContext(), mJavaBeanZuoShu.playList);
				mSizeChangedObserver = new CreateSizeChangedObserver(mCurlView, Utils.path + NamePic.mp3UrlToFileName(mJavaBeanZuoShu.getPlayList().get(0)));
				mCurlView.setShowMusic(true);
				mCurlView.vist(false);
				mCurlView.setPageProvider(mPageProvider);
				mCurlView.setSizeChangedObserver(mSizeChangedObserver);
				mCurlView.setCurrentIndex(Utils.currentPageCount);
				MusicHelper.init(ZuoShuActivity.this,Utils.currentPageCount - 1);
				mCurlView.setVisibility(View.VISIBLE);
				
				zuoshuView.setVisibility(View.INVISIBLE);
				operateDialog.dismiss();
				break;
			case 2:
				Toast.makeText(ZuoShuActivity.this, "保存图片失败。。。", Toast.LENGTH_LONG).show();
				updateTip.dismiss();
				break;
			case 3:
				if(currentId == mJavaBeanZuoShu.playId.size() ){
					imgFrameRight.setVisibility(View.INVISIBLE);
					imgFrameLeft.setVisibility(View.INVISIBLE);
					imgPhotoLeft.setVisibility(View.INVISIBLE);
					imgPhotoRight.setVisibility(View.INVISIBLE);
					shadowLeft.setVisibility(View.INVISIBLE);
					shadowRight.setVisibility(View.INVISIBLE);
				}
				updateTip.dismiss();
				parent.setVisibility(View.VISIBLE);
				if(createCurlView){
					createCurlView = false;
					mCurlView = (CreateCurlView) findViewById(R.id.zuoshu_curlView);
					mPageProvider = new CreatePageProvider(getApplicationContext(), mJavaBeanZuoShu.playList);
					mSizeChangedObserver = new CreateSizeChangedObserver(mCurlView, Utils.path + NamePic.mp3UrlToFileName(mJavaBeanZuoShu.getPlayList().get(0)));
					mCurlView.setShowMusic(false);
					mCurlView.setPageProvider(mPageProvider);
					mCurlView.setSizeChangedObserver(mSizeChangedObserver);
					mCurlView.setCurrentIndex(0);
				}else{
					mCurlView.setCurrentIndex(Utils.currentPageCount);
				}
				mCurlView.setVisibility(View.VISIBLE);
				ShowOperate();
				break;
			case 4:
				MusicHelper.stopMusci();//关闭做书模式下的音乐
				mCurlView.setVisibility(View.INVISIBLE);
				parent.setVisibility(View.INVISIBLE);
				zuoshuView.setVisibility(View.VISIBLE);
				ShowOperate();
				break;
			case 5:
				updateTip.dismiss();
				Toast.makeText(ZuoShuActivity.this, "你需要分享。。。", Toast.LENGTH_LONG).show();
				break;
			case 6:
				updateTip.dismiss();
				break;
			case 7:
				updateTip.show();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.zuoshu_activity);
		init();
		listener();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Login();
			}
		}, 10);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				ShowMenu();
			}
		}, 1000);
		
	}
	private void init() {
		shadowLeft = (ImageView) findViewById(R.id.shadowLeft);
		imgPhotoLeft = (ImageView) findViewById(R.id.addPhoto1);
		imgFrameLeft = (ImageView) findViewById(R.id.addFrame1);
		shadowRight = (ImageView) findViewById(R.id.shadowRight);
		imgPhotoRight = (ImageView) findViewById(R.id.addPhoto2);
		imgFrameRight = (ImageView) findViewById(R.id.addFrame2);
		parent = (LinearLayout) findViewById(R.id.parent);
		zuoshuView = (LinearLayout) findViewById(R.id.zuoshu_view);
		parent.setVisibility(View.GONE);
	}
	void listener(){
		imgPhotoLeft.setOnTouchListener(this);
		imgPhotoRight.setOnTouchListener(this);
	}
	@SuppressWarnings("deprecation")
	void Login(){
		sp = getSharedPreferences("diyBook", MODE_WORLD_READABLE);
		imgUrlList = new ArrayList<String>();
		Bundle bundle = getIntent().getExtras();
		mJavaBeanZuoShu =(JavaBeanZuoShu) bundle.getSerializable("javaBean");
		tpl_id = bundle.getString("tpl_id");
		imageCount = bundle.getInt("count");
		imageWidth = bundle.getInt("width");
		imageHeight = bundle.getInt("height");
		Utils.ScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		Utils.ScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
		
		Bitmap bitRight = Utils.getFrameRect(BitmapFactory.decodeFile(Utils.path + NamePic.mp3UrlToFileName(mJavaBeanZuoShu.playList.get(0))));
		RelativeLayout.LayoutParams paramsLeft = (RelativeLayout.LayoutParams) imgPhotoLeft.getLayoutParams();
		paramsLeft.width = bitRight.getWidth();
		paramsLeft.height = bitRight.getHeight();
		imgPhotoLeft.setLayoutParams(paramsLeft);
		RelativeLayout.LayoutParams paramsRight = (RelativeLayout.LayoutParams) imgPhotoRight.getLayoutParams();
		paramsRight.width = bitRight.getWidth();
		paramsRight.height = bitRight.getHeight();
		imgPhotoRight.setLayoutParams(paramsRight);
		imgFrameRight.setImageBitmap(bitRight);
		//添加阴影 右边的
		shadowRight.setImageBitmap(DrawRect.Shadow(bitRight));
	}
	int finalCount;
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.operate_bnt1://提前预览
			Intent intent = new Intent(this, VistBook.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("vistBook", mJavaBeanZuoShu);
			intent.putExtras(bundle);
			startActivity(intent);
			overridePendingTransition(R.anim.vist_in,R.anim.none);
			break;
		case R.id.operate_bnt2://继续下一页。如果当前页还有没做完的就跳到另一个封面，如果当前页都完成了，就自动翻页，跳转到下个做书页面
			bClick = true;
			//判断后面的资源是否已经下载完成
			if(currentId == mJavaBeanZuoShu.playId.size() ){//最后一页。不继续往下翻
				operateForEnd();
				return;
			}
			saveBnt.setVisibility(View.INVISIBLE);
			if(!bToNext){
				return;
			}
		
			bToNext = false;
			finalCount = (int)Math.ceil(((float)mJavaBeanZuoShu.playId.get(currentId) - 1) / 2 );
			if(mJavaBeanZuoShu.playId.get(currentId - 1) == (Utils.currentPageCount * 2 + 1)){//说明翻页后是在右边做书，点击下一页直接自动翻页(不需要做任何判断)
				operateForNext();
				bNext = true;
			}else if(mJavaBeanZuoShu.playId.get(currentId - 1) == (Utils.currentPageCount * 2 )){//说明在左边翻页,点击下一页要判断，右边是否还有模板需要做书(1.做书，2.不做书，直接自动翻页)
				if(mJavaBeanZuoShu.playId.contains(Utils.currentPageCount * 2 + 1)){//右边需要做图
					mCurlView.setVisibility(View.INVISIBLE);
					parent.setVisibility(View.INVISIBLE);
					operateDialog.dismiss();
					if(bNext){
						bNext = false;
						frameLeft = bExistBitmap(mJavaBeanZuoShu.playList.get(currentPage * 2 - 1));
						frameRight = bExistBitmap(mJavaBeanZuoShu.playList.get(currentPage * 2));
						imgFrameLeft.setImageBitmap(Utils.getFrameRect(frameLeft));
						imgFrameRight.setImageBitmap(Utils.getFrameRect(frameRight));
						direction = "right";
					}else{
						bNext = true;
						operateForNext();
					}
				}else{
					operateForNext();
				}
			}
			break;
		case R.id.zuoshu_menu_bnt1://看书模式
			changeMode();
			break;
		case R.id.wifi_bnt1:
			menu.dismiss();
			Utils.currentPageCount = 0;
			MusicHelper.stopMusci();//关闭做书模式下的音乐
			MusicHelper.init(getApplicationContext(), -1);// 阅读模式是当前封面，初始化封面音乐
			setResult(1, null);
			this.finish();
			overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			break;
		case R.id.wifi_bnt2:
			changeDialog.dismiss();
			break;
		case R.id.zuoshu_menu_bnt2://做书模式
			
			break;
		case R.id.zuoshu_menu_addpic://添加照片
			showDialog();
			break;
		case R.id.zuoshu_menu_save://保存图片  保存合成图片到本地，并post到服务器获取对应url
			if(!bClick){
			Log.i("bClick","false");
		}else{
			Log.i("bClick","true");
		}
			
			if(bClick){
				showTips();
				//提交到服务器前 先验证是否登入
				SharedPreferences userInfo = getSharedPreferences("diyBook", MODE_WORLD_READABLE);
				String Name = userInfo.getString("name", "");//未登入只能查阅三本书籍
				String Psw = userInfo.getString("psw", "");
				if(Name == null || Psw == null || Name.equals("") || Psw.equals("")){
					bClick = true;
					startActivity(new Intent(this, EnterActivityView.class));
					updateTip.dismiss();
					return;
				}
				//判断是否是wift状态下，如果不是拒绝做书功能
				if(!checkNetUtils.checkWifi(getApplicationContext())){
					bClick = true;
					showWifiTips();
					updateTip.dismiss();
					return;
				}
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						ZuoShuTitle();
					}
				}).start();
			}
			

//			if(!bClick){
//				updateTip.dismiss();
//				return;
//			}
//			
			break;
		case R.id.memory_bnt2:
			dialog.dismiss();
			break;
		case R.id.zuoshu_menu_finish://提交所有url到服务器
			startActivityForResult(new Intent(ZuoShuActivity.this,ZuoShuEdit.class), 3);
			break;
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView)v;
		view.setScaleType(ImageView.ScaleType.MATRIX);
		if(v.getId() == R.id.addPhoto1){
			switch(event.getAction() & MotionEvent.ACTION_MASK){
			case MotionEvent.ACTION_DOWN://第一个手指按下
				savedMatrixLeft.set(matrixLeft);
				start.set(event.getX(), event.getY());
				mode = DRAG;
				break;
			case MotionEvent.ACTION_POINTER_DOWN://第二个手指按下
				oldDist = spaceing(event);
				if(oldDist > 5f){
					savedMatrixLeft.set(matrixLeft);
					midPoint(mid,event);
					oldDegrees = rotation(event);
					mode = ZOOM;
				}
				break;
			case MotionEvent.ACTION_MOVE://
				if(mode == DRAG){//平移
					matrixLeft.set(savedMatrixLeft);
					matrixLeft.postTranslate(event.getX() - start.x, event.getY() - start.y);
				}else if(mode == ZOOM){//缩放
					newDist = spaceing(event);
					if(newDist > 5){
						matrixLeft.set(savedMatrixLeft);
						newDegress = rotation(event);
						scale = newDist / oldDist;
						matrixLeft.postScale(scale, scale, mid.x, mid.y);
						matrixLeft.postRotate(newDegress - oldDegrees, mid.x, mid.y);
					}
				}
				break;
			case MotionEvent.ACTION_UP://第一个手指抬起
			case MotionEvent.ACTION_POINTER_UP://第二个手指抬起 
				mode = NONE;
				break;
			}
			view.setImageMatrix(matrixLeft);
		}else if (v.getId() == R.id.addPhoto2){
			switch(event.getAction() & MotionEvent.ACTION_MASK){
			case MotionEvent.ACTION_DOWN://第一个手指按下
				savedMatrixRight.set(matrixRight);
				start.set(event.getX(), event.getY());
				mode = DRAG;
				break;
			case MotionEvent.ACTION_POINTER_DOWN://第二个手指按下
				oldDist = spaceing(event);
				if(oldDist > 5f){
					savedMatrixRight.set(matrixRight);
					midPoint(mid,event);
					oldDegrees = rotation(event);
					mode = ZOOM;
				}
				break;
			case MotionEvent.ACTION_MOVE://
				if(mode == DRAG){//平移
					matrixRight.set(savedMatrixRight);
					matrixRight.postTranslate(event.getX() - start.x, event.getY() - start.y);
				}else if(mode == ZOOM){//缩放
					newDist = spaceing(event);
					if(newDist > 5){
						matrixRight.set(savedMatrixRight);
						newDegress = rotation(event);
						scale = newDist / oldDist;
						matrixRight.postScale(scale, scale, mid.x, mid.y);
						matrixRight.postRotate(newDegress - oldDegrees, mid.x, mid.y);
					}
				}
				break;
			case MotionEvent.ACTION_UP://第一个手指抬起
			case MotionEvent.ACTION_POINTER_UP://第二个手指抬起 
				mode = NONE;
				break;
			}
			view.setImageMatrix(matrixRight);
		}
		return true;
	}
	//图片平移的距离
	private float spaceing(MotionEvent event){
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	//取得图片中点，进行图片操作
	private void midPoint(PointF point,MotionEvent event){
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
	 // 取旋转角度  
    private float rotation(MotionEvent event) {  
        double delta_x = (event.getX(0) - event.getX(1));  
        double delta_y = (event.getY(0) - event.getY(1));  
        double radians = Math.atan2(delta_y, delta_x);  
        return (float) Math.toDegrees(radians);  
    } 
   //photo和frame进行整合
    Bitmap saveWithFrame(Bitmap photo,Bitmap frame,Matrix matrix,int width,int height){
    	Bitmap bitSrc = frame;
    	Paint paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);
    	resultLeft = Bitmap.createBitmap(bitSrc.getWidth(),bitSrc.getHeight(),Bitmap.Config.ARGB_8888);
    	// 自定义边界
		Canvas canvas = new Canvas(resultLeft);
		canvas.drawRect(0, 0, bitSrc.getWidth(), bitSrc.getHeight(), paint);
		if(photo != null){
			canvas.drawBitmap(photo, matrix, paint);
		}
		canvas.drawBitmap(bitSrc, 0, 0, paint);
		float scale = (float)width / (float)bitSrc.getWidth();
		Bitmap realBit = Bitmap.createScaledBitmap(resultLeft, (int)(bitSrc.getWidth() * scale), (int)(bitSrc.getHeight() * scale), false);
		return realBit;
    }
    //保存完成的图片到本地
	void saveBit(String frameName,Bitmap photo,Bitmap frame,Matrix matrix,int width,int height,final int AllowCount,final int DayAllowCount,final boolean bAllow){
    	Bitmap bit = saveWithFrame(photo,frame,matrix,width,height);
    	OutputStream fOut = null;
    	String[] str = frameName.split("\\.");
    	String pathUrl = str[0];
		final File file = new File(Utils.path + pathUrl + ".jpg");
		try {
			file.createNewFile();
			fOut = new FileOutputStream(file);
			bit.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();
			MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(), file.getName(), file.getName());
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					Message msg = new Message();
					if(!bLastOne){
						String result = UploadUtil.uploadFile(file, HttpPort.registerImgUrl);
						if(result == null || result.equals("")){
							msg.what = 2;
							mHandler.sendMessage(msg);
							bClick = true;
							return;
						}
						currentId++;
						JSONObject json;
						try {
							json = new JSONObject(result);
							String imgUrl = json.getString("imageurl");
							Log.i("imageurl",imgUrl);
							imgUrlList.add(imgUrl);
							bClick = true;
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
					}else{
						currentId++;
					}
//					if(currentId == mJavaBeanZuoShu.playId.size() ){//最后一页。不继续往下翻
//						bClick = false;
//						msg.what = 6;
//						mHandler.sendMessage(msg);
//						startActivityForResult(new Intent(ZuoShuActivity.this,ZuoShuEdit.class), 3);
//					}else{
						msg.what = 3;
						mHandler.sendMessage(msg);
//					}
				}
			}).start();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	//添加图片
	public void showDialog(){
		new AlertDialog.Builder(this).setMessage("选择照片").setNegativeButton("相册", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT,null);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
				startActivityForResult(intent, 2);
			}
		}).setPositiveButton("拍照", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(ZuoShuActivity.this,MyCamera.class);
				Bundle bundle = new Bundle();
				bundle.putIntArray("locationLeft", getLocation(imgFrameLeft));
				bundle.putIntArray("locationRight", getLocation(imgFrameRight));
				bundle.putStringArrayList("playList", mJavaBeanZuoShu.playList);
				bundle.putInt("currentId", currentId);
				bundle.putString("direction", direction);
				bundle.putInt("currentPage",currentPage);
				bundle.putIntegerArrayList("playId", mJavaBeanZuoShu.playId);
				intent.putExtras(bundle);
				startActivityForResult(intent,result_camera);
				overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
			}
		}).show();
	}
	// 个操作的相对相应事件
	protected void onActivityResult(int requestCode, int resultCode,final Intent data) {
		//从相机获取照片
		if(resultCode == 1){
			switch(requestCode){
			case 1:
				Bitmap left = null;
				Bitmap right = null;
				if(currentId > 0){
					if(direction.equals("left")){
						left = bExistBitmap(mJavaBeanZuoShu.playList.get(mJavaBeanZuoShu.playId.get(currentId) -1).toString());
						right = bExistBitmap(mJavaBeanZuoShu.playList.get(mJavaBeanZuoShu.playId.get(currentId)).toString());
					}else if(direction.equals("right")){
						left = bExistBitmap(mJavaBeanZuoShu.playList.get(mJavaBeanZuoShu.playId.get(currentId) - 2).toString());
						right = bExistBitmap(mJavaBeanZuoShu.playList.get(mJavaBeanZuoShu.playId.get(currentId) - 1).toString());
					}
					
					imgFrameLeft.invalidate();
					imgFrameRight.invalidate();
					imgFrameLeft.setImageBitmap(Utils.getFrameRect(left));
					imgFrameRight.setImageBitmap(Utils.getFrameRect(right));
					photoRight = null;
					frameRight = right;
					photoLeft = null;
					frameLeft = left;
				}else if(currentId == 0){
					right = bExistBitmap(mJavaBeanZuoShu.playList.get(currentId * 2));
					imgFrameRight.invalidate();
					imgFrameRight.setImageBitmap(Utils.getFrameRect(right));
					photoRight = null;
					frameRight = right;
				}
				saveBnt.setVisibility(View.VISIBLE);
				break;
			}
		}
		//做书完成 
		if(requestCode == 3){
			switch(resultCode){
			case 3:
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						Message msg = new Message();
						msg.what = 7;
						mHandler.sendMessage(msg);
						String name = data.getExtras().getString("name");
						String info = data.getExtras().getString("info");
						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < imgUrlList.size(); i++) {
							if (i == imgUrlList.size() - 1) {
								sb.append(imgUrlList.get(i));
							} else {
								sb.append(imgUrlList.get(i) + ",");
							}
						}
						int mUid = sp.getInt("uid", 0);
						String mPsw = sp.getString("psw", "");
						String mTpl_id = tpl_id;
						tpl_name = name;
						String mMark = info;
						String mImgstr = sb.toString();
						Log.i("mImgstr","mImgstr" + mImgstr);
						int mSys = 2;
						postImgUrlList(mUid, mPsw, mTpl_id, tpl_name, mMark, mImgstr,mSys, AllowCount, DayAllowCount, bAllow);
					}
				}).start();
				break;
			case 4:
				bLastOne = true;
				currentId--;
				bClick = true;
				break;
			}
		}
		switch(requestCode){
		case 2:
			ContentResolver resolver = getContentResolver();
			try {
				// 获得图片的uri
				Uri originalUri = data.getData();
				// 将图片内容解析成字节数组
				photoByte = readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));
				// 将字节数组转换为ImageView可调用的Bitmap对象
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length, options);
				if(direction.equals("left")){
					photoLeft = getPicFromBytes(photoByte,imgFrameLeft, options);
					imgPhotoLeft.setImageBitmap(photoLeft);
				}else if(direction.equals("right")){
					photoRight = getPicFromBytes(photoByte,imgFrameRight, options);
					imgPhotoRight.setImageBitmap(photoRight);
				}
				saveBnt.setVisibility(View.VISIBLE);
			} catch (Exception e) {
//				System.out.println(e.getMessage());
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	 // 把本地图片转换为字节 
	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}
	
	 //从本地相册获取
	public static Bitmap getPicFromBytes(byte[] bytes,ImageView frame,BitmapFactory.Options opts) {
		if (bytes != null){
			int width = frame.getWidth();
			int height = frame.getHeight();
			int parentWidth = opts.outWidth;
			int parentHeight = opts.outHeight;
			if(parentWidth > width || parentHeight > height){
				int widthRatio = (int) Math.ceil((double)parentWidth / width);
				int heightRatio = (int) Math.ceil((double)parentHeight / height);
				opts.inSampleSize = widthRatio < heightRatio ? heightRatio : widthRatio;
			}
			opts.inJustDecodeBounds = false;
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,opts);
		}
		return null;
	}
	 // 从相机获取
	public Bitmap scaleBit(Bitmap photo,ImageView frame){
		if(photo != null && frame != null){
			int photoWidth = photo.getWidth();
			int photoHeight = photo.getHeight();
			int frameWidth = frame.getWidth();
			int frameHeight = frame.getHeight();
			float scale = 1;
			if(photoWidth > frameWidth || photoHeight > frameHeight){
				float widthRatio = (float)photoWidth / frameWidth;
				float heightRatio = (float)photoHeight / frameHeight;
				scale = widthRatio < heightRatio ? heightRatio : widthRatio;
			}
			return Bitmap.createScaledBitmap(photo, (int)(photoWidth / scale),  (int)(photoHeight / scale), false);
		}
		return null;
	}
	//自动翻页
	void AutoPage(){
		int[] location = new int[2];
		imgFrameRight.getLocationOnScreen(location);
		int startW = location[0] + imgFrameRight.getWidth();
		int startH = location[1] + imgFrameRight.getHeight();
		int endW = location[0] - imgFrameRight.getWidth();
		mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN, startW,	startH, 0));
		for (int count = 1; count < 400; count++) {
			mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_MOVE, (int) (startW / 400) * (400 - count), startH, 0));
		}
		mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_UP, endW, startH, 0));
	}
	
	/*
	 * 自动翻页
	 */
	void operateForNext(){
		for(int i = Utils.currentPageCount;i < finalCount;i++){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			};
			AutoPage();
		}
		frameLeft = BitmapFactory.decodeFile(Utils.path + NamePic.mp3UrlToFileName(mJavaBeanZuoShu.playList.get(finalCount * 2 - 1)));
		frameRight = BitmapFactory.decodeFile(Utils.path + NamePic.mp3UrlToFileName(mJavaBeanZuoShu.playList.get(finalCount * 2  )));
		operateDialog.dismiss();

		if(mJavaBeanZuoShu.playId.contains((Utils.currentPageCount + 1) * 2)){//左页
			direction = "left";
			imgFrameLeft.setImageBitmap(Utils.getFrameRect(frameLeft));
			imgFrameRight.setImageBitmap(Utils.getFrameRect(frameRight));
		}else if(mJavaBeanZuoShu.playId.contains((Utils.currentPageCount + 1)* 2 + 1)){//右页
			direction = "right";
			imgFrameLeft.setImageBitmap(Utils.getFrameRect(frameLeft));
			imgFrameRight.setImageBitmap(Utils.getFrameRect(frameRight));
		} 
		//给左边添加阴影
		shadowLeft.setImageBitmap(DrawRect.Shadow(Utils.getFrameRect(frameLeft)));
		imgPhotoLeft.setImageBitmap(null);
		imgPhotoRight.setImageBitmap(null);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mCurlView.setVisibility(View.INVISIBLE);
				parent.setVisibility(View.INVISIBLE);
			}
		}, 1000);
		
		currentPage++;
		bToNext = true;
	}
	/*
	 * 做完最后一页。自动翻到末尾
	 */
	void operateForEnd(){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				operateDialog.dismiss();
				finishBnt.setVisibility(View.VISIBLE);
			}
		}, (imageCount / 2 - currentPage ) * 1000);
		
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				for(int i = currentPage;i < (imageCount / 2 ) ;i++){
					try {
						
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					};
					AutoPage();
						
				}
			}
		}).start();
		
	}
	//操作提示
	Dialog operateDialog;
	TextView tip;
	Button nextBnt,vistBnt;
	void ShowOperate(){
		operateDialog = new commentDialogUtils(this, LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT, R.layout.operate, R.style.Theme_dialog);
		tip = (TextView) operateDialog.findViewById(R.id.operate_tv1);
		nextBnt = (Button) operateDialog.findViewById(R.id.operate_bnt2);
		vistBnt = (Button) operateDialog.findViewById(R.id.operate_bnt1);
		nextBnt.setOnClickListener(this);
		vistBnt.setOnClickListener(this);
		operateDialog.setCanceledOnTouchOutside(false);
		operateDialog.show();
	}
	//最后一页 ,提交整本爱做书到服务器，
	void postImgUrlList(final int mUid,final String mPsw,String mTpl_id,String mTitle,String mMark,String mImgstr,int mSys,final int AllowCount,final int DayAllowCount,final boolean bAllow){
		List<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("uid", mUid + ""));
		parameter.add(new RequestParameter("pwd", mPsw));
		parameter.add(new RequestParameter("tpl_id", mTpl_id));
		parameter.add(new RequestParameter("title", mTitle));
		parameter.add(new RequestParameter("mark", mMark));
		parameter.add(new RequestParameter("imgstr", mImgstr));
		parameter.add(new RequestParameter("sys", mSys + ""));
		parameter.add(new RequestParameter("test", 0 + ""));
		AsyncHttpPost post = new AsyncHttpPost(null, HttpPort.zuoShu, parameter, new RequestResultCallback() {
			
			@Override
			public void onSuccess(Object o) {
				String result = (String)o;
				String mMsg;
				if(result != null && !result.equals("")){
					Log.i("result",result);
					
					try {
						JSONObject obj = new JSONObject(result);
						mMsg = obj.getString("msg");
						if(Integer.parseInt(mMsg) != 1 ){
							Message msg = new Message();
							msg.what = 2;
							mHandler.sendMessage(msg);
							bLastOne = true;
							bClick = true;
							return;
						}
						product_id = obj.getString("product_id");
						bClick = true;
						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);
						SharedPreferences.Editor editor = sp.edit();
						int mAllowCount;
						int mDayAllowCount;
						if(AllowCount < 5){
							mAllowCount = AllowCount + 1;
							mDayAllowCount = DayAllowCount;
						}else{
							mAllowCount = AllowCount;
							mDayAllowCount = DayAllowCount + 1;
						}
						Time t = new Time();
						t.setToNow();
						int day = t.monthDay;
						editor.putBoolean("bAllow", bAllow);
						editor.putInt("AllowCount", mAllowCount);
						editor.putInt("DayAllowCount", mDayAllowCount);
						editor.putInt("Day", day);
						editor.commit();
						postData(1,mUid,mPsw);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onFail(Exception e) {
				currentId--;
				bClick = true;
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessage(msg);
				Log.i("Exception",e.toString());
			}
		});
		DefaultThreadPool.getInstance().execute(post);
	}
	PopupWindow menu;
	Button zuoshuBnt,kanshuBnt,addPicBnt,saveBnt,finishBnt;
	void ShowMenu(){
		View view = LayoutInflater.from(this).inflate(R.layout.zuoshu_menu, null);
		menu = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		menu.setAnimationStyle(R.style.PopupAnimation);
		menu.setBackgroundDrawable(new BitmapDrawable());
		menu.showAtLocation(findViewById(R.id.zuoshu_main), Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0);
		zuoshuBnt = (Button) view.findViewById(R.id.zuoshu_menu_bnt2);
		kanshuBnt = (Button) view.findViewById(R.id.zuoshu_menu_bnt1);
		addPicBnt = (Button) view.findViewById(R.id.zuoshu_menu_addpic);
		saveBnt = (Button) view.findViewById(R.id.zuoshu_menu_save);
		finishBnt = (Button) view.findViewById(R.id.zuoshu_menu_finish);
		zuoshuBnt.setOnClickListener(this);
		kanshuBnt.setOnClickListener(this);
		addPicBnt.setOnClickListener(this);
		finishBnt.setOnClickListener(this);
		saveBnt.setOnClickListener(this);
		saveBnt.setVisibility(View.INVISIBLE);
		finishBnt.setVisibility(View.INVISIBLE);
		menu.update();
	}
	//获取imageView的坐标
	int[] getLocation(ImageView iv){
		int[] location = new int[2];
		iv.getLocationOnScreen(location);
		return location;
	}
	Bitmap bExistBitmap(String url){
		Bitmap bit = null;
		String[] str = (NamePic.mp3UrlToFileName(url)).split("\\.");
		String picName = str[0] + ".jpg";
		File existFile = new File(Utils.path + picName);
		if(existFile.exists()){
			bit = BitmapFactory.decodeFile(existFile.toString());
		}else{
			bit = Utils.getFrameRect(BitmapFactory.decodeFile(Utils.path + NamePic.mp3UrlToFileName(url)));
		}
		
		return bit;
	}
	//整本作品提交到服务器后刷新 个人中心的 我的作品
	private void postData(final int mPage,final int uid ,final String psw){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				DefaultHttpClient client = new DefaultHttpClient();
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				NameValuePair pair1 = new BasicNameValuePair("uid", uid + "");  
				NameValuePair pair2 = new BasicNameValuePair("guest_id", uid + "");  
				NameValuePair pair3 = new BasicNameValuePair("page", mPage + "");  
				list.add(pair1);
				list.add(pair2);
				list.add(pair3);
				UrlEncodedFormEntity entity;
				try {
					entity = new UrlEncodedFormEntity(list,"UTF-8");
					HttpPost post = new HttpPost(HttpPort.myWorkShowUrl);  
	                post.setEntity(entity); 
	                HttpResponse response;  
	                //设置连接超时时间为40s
	        		HttpConnectionParams.setConnectionTimeout(client.getParams(),40000);
	        		//设置读取超时为20s
	        		HttpConnectionParams.setSoTimeout(client.getParams(),20000);
	                response=client.execute(post); 
	                if (response.getStatusLine().getStatusCode() == 200) {
						String result = EntityUtils.toString(response.getEntity());
						if(result != null && !result.equals("")){
							JSONObject object;
							JSONArray array;
							object = new JSONObject(result);
							int total_size = object.getInt("msg");
							array = object.getJSONArray("data");
							ArrayList<String> bookCoverList = new ArrayList<String>();
							ArrayList<String> bookNameList = new ArrayList<String>();
							ArrayList<String> bookIdList = new ArrayList<String>();
							ArrayList<String> bookUpdateTime = new ArrayList<String>();
							JavaBean3.bookCoverList = new ArrayList<String>();
							JavaBean3.bookNameList = new ArrayList<String>();
							JavaBean3.bookIdList = new ArrayList<String>();
							JavaBean3.bookUpdateTime = new ArrayList<String>();
							for (int i = 0; i < array.length(); i++) {
								JSONObject list2 = array.getJSONObject(i);
								String bCompleted = list2.getString("is_completed");
								String templates_id = list2.getString("product_id");
								String templates_name = list2.getString("product_name");
								String thumb_img = list2.getString("thumb_img");
								String update_time = list2.getString("update_time");
								JavaBean3.bookCoverList.add(thumb_img);
								JavaBean3.bookNameList.add(templates_name);
								JavaBean3.bookIdList.add(templates_id);
								JavaBean3.bookUpdateTime.add(update_time);
								bookCoverList.add(thumb_img);
								bookNameList.add(templates_name);
								bookIdList.add(templates_id);
								bookUpdateTime.add(update_time);
							}
							new CoverThreadPool3(ZuoShuActivity.this,bookCoverList, uid + "").saveCover();
							Utils.mDBUserInfoHelper.deleteUserInfo(uid);
							for (int j = 0; j < bookCoverList.size(); j++) {
								Utils.mDBUserInfoHelper.saveCoverUrl(uid,bookNameList.get(j), bookIdList.get(j),bookUpdateTime.get(j),bookCoverList.get(j));
							}
							SharedPreferences.Editor editor = sp.edit();
							editor.putLong("updateTime",System.currentTimeMillis() / 1000);
							editor.commit();
							moveFile();
						}
	                }
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}  
			}
		}).start();
	}
	//把临时文件夹里的东西移动到指定目录下
	void moveFile(){
		for(int i = 0;i < mJavaBeanZuoShu.playList.size();i++){//copy图片
			String oldFileName = NamePic.ZuoShuUrlToFileName(mJavaBeanZuoShu.playList.get(i));
			String newFileName = NamePic.convertUrlToFileName(mJavaBeanZuoShu.playList.get(i));
			File oldFile = new File(Utils.path + oldFileName);
			File newFile = new File(Utils.basePath1 + product_id + "/" + Utils.imgPathName + "/" + newFileName);
			if(oldFile.exists()){
				new File(Utils.basePath1 + product_id ).mkdirs();
				new File(Utils.basePath1 + product_id + "/" + Utils.imgPathName).mkdirs();
				CopyFile.copyFile(oldFile.toString(),newFile.toString());
			}
		}
		if(Utils.createMusic){
			for(int j = 0;j < mJavaBeanZuoShu.musicList.size();j++){
				String musicName = NamePic.mp3UrlToFileName(mJavaBeanZuoShu.musicList.get(j));
				File oldFile = new File(Utils.path + musicName);
				File newFile = new File(Utils.basePath1 + product_id + "/" + Utils.audPathName + "/" + musicName);
				if(oldFile.exists()){
					new File(Utils.basePath1 + product_id).mkdirs();
					new File(Utils.basePath1 + product_id + "/" + Utils.audPathName ).mkdirs();
					CopyFile.copyFile(oldFile.toString(),newFile.toString());
				}
			}
		}
		addData();
	}
	//对数据库就行添加
	void addData(){
		Cursor bookCursor = null;
		bookCursor = Utils.mDBHelper.getBookName(product_id);
		if(bookCursor.getCount() == 0){
			
			//记录 个人个人信息下数据库，方便在个人中心进行删除，读取操作
			//从3张表里获取 相应的coverUrl用来记录封面
			//存储书本信息到 个人中心的本地书架
			String coverUrl = "";
			String time = "";
			Cursor cursor = null;
			Cursor cursor2 = null;
			Cursor cursor3 = null;
			cursor = Utils.mDBCacheHelper.getLocalBookInfo(product_id);
			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				coverUrl = cursor.getString(cursor.getColumnIndex("COVER"));
				time = cursor.getString(cursor.getColumnIndex("TIME"));
				cursor.close();
			}
			cursor2 = Utils.mDBUserInfoHelper.getLocalBookInfo(product_id);
			if(cursor2.getCount() > 0){
				cursor2.moveToFirst();
				coverUrl = cursor2.getString(cursor2.getColumnIndex("COVERURL"));
				time = cursor2.getString(cursor2.getColumnIndex("TIME"));
				cursor2.close();
			}
			cursor3 = Utils.mDBUserInfoHelper.getLocalCoverCollect(product_id);
			if(cursor3.getCount() > 0){
				cursor3.moveToFirst();
				coverUrl = cursor3.getString(cursor3.getColumnIndex("COVERURL"));
				time = cursor3.getString(cursor3.getColumnIndex("TIME"));
				cursor3.close();
			}
			Utils.mDBUserInfoHelper.saveLocalBook(product_id,tpl_name,coverUrl,time);
			
			bookCursor.close();
			Utils.mDBHelper.saveINFO(product_id, imageCount, imageWidth, imageHeight);
			Utils.mDBHelper.saveTIME(product_id, time);
			for(int i = 0;i < mJavaBeanZuoShu.playList.size();i++){
				Utils.mDBHelper.saveIMG(product_id, mJavaBeanZuoShu.playList.get(i));
			}
			if(Utils.createMusic){
				for(int j = 0;j < mJavaBeanZuoShu.musicList.size();j++){
					Utils.mDBHelper.saveMP3(product_id, mJavaBeanZuoShu.musicList.get(j));
				}
			}
		}
	}
	commentDialogUtils dialog;
	TextView tv1,tv2;
	Button iKnow;
	void showWifiTips(){
		dialog = new commentDialogUtils(this, LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT, R.layout.memory_dialog, R.style.Theme_dialog);
		tv1 = (TextView) dialog.findViewById(R.id.memory_tv);//提示信息
		tv2 = (TextView) dialog.findViewById(R.id.memory_tv2);//通知
		iKnow = (Button) dialog.findViewById(R.id.memory_bnt2);//知道了
		iKnow.setOnClickListener(this);
		tv2.setText("当前wifi不可用");
		tv1.setText("为节省您的流量数据，暂时仅允许在WIFI下使用做书功能。后期请关注新版本更新^_^");
		tv1.setTextSize(14);
		dialog.show();
	}
	/**
	 * 做书条件
	 * 1.最外层条件AllowCount(int)：如果小于5本，可以做书。大于5本，每天最多只能再做2本
	 * 2.每天做书2本DayAllowCount(int)：如果小于2本，可以做书。如果大于2本，要去分享，然后当天无限期做书
	 * 3.分享bAllow（boolean）：分享成功，可以在当天不限数做书（超过夜晚12点，DayAllowCount和bAllow清零,重新计数）
	 */
	int AllowCount;
	int DayAllowCount;
	boolean bAllow;
	void ZuoShuTitle(){
		if(!sp.contains("AllowCount")){//本地数据库已经开始记载
			SharedPreferences.Editor editor = sp.edit();
			editor.putInt("AllowCount", 0);
			editor.putInt("DayAllowCount", 0);
			editor.putBoolean("bAllow", false);
			editor.putInt("Day", 1);
			editor.commit();
		}
		AllowCount = sp.getInt("AllowCount", 0);//条件1
		if(AllowCount < 5){
			//做书
			saveOperate(AllowCount,0,false);
		}else{
			DayAllowCount = sp.getInt("DayAllowCount", 0);//条件2
			if(DayAllowCount < 2){
				//做书
				saveOperate(AllowCount,DayAllowCount,false);
			}else{
				bAllow = sp.getBoolean("bAllow", false);//条件3
				if(bAllow){
					//做书
					saveOperate(AllowCount,DayAllowCount,bAllow);
				}else{
					//提示 你需要去分享
					Message msg = new Message();
					msg.what = 5;
					mHandler.sendMessage(msg);
				}
			}
		}
	}
	//点击 保存图片 按钮的操作过程
	void saveOperate(int AllowCount,int DayAllowCount,boolean bAllow){
		bClick = false;
		String frameName = NamePic.mp3UrlToFileName(mJavaBeanZuoShu.playList.get(mJavaBeanZuoShu.playId.get(currentId) - 1));
		if(direction.equals("left")){
			Bitmap leftBit = BitmapFactory.decodeFile(Utils.path + NamePic.UrlToFileName(mJavaBeanZuoShu.playList.get(mJavaBeanZuoShu.playId.get(currentId) - 1)));
			frameLeft = bExistBitmap(mJavaBeanZuoShu.playList.get(mJavaBeanZuoShu.playId.get(currentId) - 1));
			saveBit(frameName,photoLeft,frameLeft,matrixLeft,leftBit.getWidth(),leftBit.getHeight(),AllowCount,DayAllowCount,bAllow);
		}else if(direction.equals("right")){
			Bitmap rightBit = BitmapFactory.decodeFile(Utils.path + NamePic.UrlToFileName(mJavaBeanZuoShu.playList.get(mJavaBeanZuoShu.playId.get(currentId) - 1)));
			frameRight = bExistBitmap(mJavaBeanZuoShu.playList.get(mJavaBeanZuoShu.playId.get(currentId) - 1));
			saveBit(frameName,photoRight,frameRight,matrixRight,rightBit.getWidth(),rightBit.getHeight(),AllowCount,DayAllowCount,bAllow);
		}
	}
	commentDialogUtils changeDialog;
	TextView mTv1,mTv2,mTv3;
	Button bnt1,bnt2;
	void changeMode(){
		changeDialog = new commentDialogUtils(this, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, R.layout.wifi_tip, R.style.Theme_dialog);
		mTv1 = (TextView) changeDialog.findViewById(R.id.wifi_tv1);
		mTv2 = (TextView) changeDialog.findViewById(R.id.wifi_tv2);
		mTv3 = (TextView) changeDialog.findViewById(R.id.wifi_tv3);
		bnt1 = (Button) changeDialog.findViewById(R.id.wifi_bnt1);
		bnt2 = (Button) changeDialog.findViewById(R.id.wifi_bnt2);
		bnt1.setOnClickListener(this);
		bnt2.setOnClickListener(this);
		changeDialog.setCanceledOnTouchOutside(false);
		mTv1.setText("当前书本未保存");
		mTv2.setText("确定放弃吗？");
		mTv3.setText("");
		bnt1.setText("放弃");
		bnt2.setText("继续完成");
		changeDialog.show();
	}
	commentDialogUtils updateTip;
	TextView tv;
	void showTips(){
		updateTip = new commentDialogUtils(this, LinearLayout.LayoutParams.WRAP_CONTENT,  LinearLayout.LayoutParams.WRAP_CONTENT, R.layout.zuoshu_title, R.style.Theme_dialog);
		updateTip.setCanceledOnTouchOutside(false);
		tv = (TextView) updateTip.findViewById(R.id.zuoshu_textview);
		tv.setText("正在处理中.....");
		updateTip.show();
	}
	boolean ActionFlag = true;
	@Override
	public void onBackPressed() {
		changeMode();
	}
//	/*
//	 * 判断 点击下一页时，其后面需要加载的资源是否已经存在，如果不存在先进行下载，再进行自动翻页
//	 */
//	void bExistCache(){
//		for(int i = Utils.currentPageCount;i < finalCount;i++){
//			
//		}
//	}
}
