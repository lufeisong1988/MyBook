package com.starbaby.diyBook.main;
/**
 *github
 * 主界面
 * 第二版本
 * 2014 3 17
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.graphics.Matrix;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.renderscript.ProgramFragmentFixedFunction.Builder.Format;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;

import com.baidu.mobstat.StatService;
import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.clientui.FriendsActivity;
import com.starbaby.diyBook.controller.CheckUpdate;
import com.starbaby.diyBook.controller.CollectBook;
import com.starbaby.diyBook.controller.DeleteCache;
import com.starbaby.diyBook.controller.GetArtBook;
import com.starbaby.diyBook.controller.GetData;
import com.starbaby.diyBook.controller.GetZuoShuCache;
import com.starbaby.diyBook.controller.HeadImgThread;
import com.starbaby.diyBook.controller.MusicService;
import com.starbaby.diyBook.controller.PageProvider;
import com.starbaby.diyBook.controller.ReadSDcard;
import com.starbaby.diyBook.controller.ShowBookInfo;
import com.starbaby.diyBook.controller.SizeChangedObserver;
import com.starbaby.diyBook.fragment.FragmentS1;
import com.starbaby.diyBook.fragment.FragmentS2;
import com.starbaby.diyBook.fragment.FragmentS3;
import com.starbaby.diyBook.fragment.FragmentS4;
import com.starbaby.diyBook.helper.BookMusicHelper;
import com.starbaby.diyBook.helper.DBCacheHelper;
import com.starbaby.diyBook.helper.DBHelper;
import com.starbaby.diyBook.helper.DBPlayinfoHelper;
import com.starbaby.diyBook.helper.DBUserInfoHelper;
import com.starbaby.diyBook.helper.HttpHelper;
import com.starbaby.diyBook.helper.MusicHelper;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.helper.SaveShadow;
import com.starbaby.diyBook.helper.ShareComment;
import com.starbaby.diyBook.model.DownLoad;
import com.starbaby.diyBook.model.ThreadPool;
import com.starbaby.diyBook.open.PerspectiveView;
import com.starbaby.diyBook.utils.HttpPort;
import com.starbaby.diyBook.utils.JavaBean;
import com.starbaby.diyBook.utils.JavaBeanLocation;
import com.starbaby.diyBook.utils.JavaBeanZuoShu;
import com.starbaby.diyBook.utils.StoreSrc;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.checkNetUtils;
import com.starbaby.diyBook.utils.commentDialogUtils;
import com.starbaby.diyBook.view.CurlView;
import com.starbaby.diyBook.view.EnterActivityView;
import com.starbaby.diyBook.view.EventLinearLayout;
import com.starbaby.diyBook.view.MyFloatView;
import com.starbaby.diyBook.view.ShowZuoShuTips;
import com.starbaby.diyBook.view.mSeekBar;
import com.starbaby.diyBook.view.mSeekBar.OnSeekBarChangeListener;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
@SuppressLint({ "ShowToast", "HandlerLeak", "WorldReadableFiles", "Recycle", "NewApi" })
@SuppressWarnings("deprecation")
public class MainActivity extends BaseFActivity implements OnClickListener,OnGestureListener ,OnTouchListener, AnimatorUpdateListener{
	
	private ImageButton bookshelfiBnt,commentIBnt;
	private Button Bnt1,Bnt2,Bnt3,Bnt4,Bnt5;
	private ImageView animLeft,animRight,headIv;
	private FrameLayout container,mainactivity;
	private View view;
	private LinearLayout animParent;
	private Fragment details,saveDetails;
	private EventLinearLayout parent;
	private RelativeLayout showDialog;
	public static TextView msgTv;
	
	private CurlView mCurlView;
	private PageProvider mPageProvider;
	private SizeChangedObserver mSizeChangedObserver;
	private PerspectiveView perspectiveView;
	private SharedPreferences userInfo ;
	private AudioManager audio ;
	private Animation translate_down;
	private Animation translate_up;
	GestureDetector gesture_detector = null; 
	private DownLoad mDownLoad;
	MediaPlayer mp;
	Intent intent;
	
	private int index ;
	private float x,y,width,height;
	@SuppressWarnings("unused")
	private boolean closeActivity = true;
	private boolean mFV = false;
	private boolean mFV2 = false;
	private boolean mPop = false;
	private boolean ShowSb = false;
	private int ScreenWidth;
	private int ScreenHeight;
	private int maxVolume;
	private int curVolume;
	String mBookName;
	boolean bClose = true;
	boolean bOpen = false;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				showDialog.setVisibility(8);
				Toast.makeText(MainActivity.this, "下载完成", 1000).show();
				break;
			case 1:
				showDialog.setVisibility(8);
				break;
			case 2:
				downLoadDialog.dismiss();
				Toast.makeText(MainActivity.this, "下载完成", 1000).show();
				break;
			case 5://第一次请求下载完成，自动打开书本
				Utils.readCount++;
				parent.setbTrue(true);//打开后，书本后的所有控件都失去焦点
//				showDialog.setVisibility(8);
				iv_cover.setBackgroundDrawable(null);
				anim.stop();
				mBookName  = StoreSrc.getTpl_name();
				addPerspectiveView(1, mBookName);
				break;
			case 6:
				bOpen = true;
				audio.setStreamVolume(AudioManager.STREAM_MUSIC, Utils.bVolum(MainActivity.this), 0);
				container.setBackgroundColor(0x88888888);
				mCurlView = new CurlView(MainActivity.this,perspectiveView);
				mPageProvider = new PageProvider(MainActivity.this, mCurlView, mBookName);
				mSizeChangedObserver = new SizeChangedObserver(mCurlView);
				new ShowBookInfo(MainActivity.this, mCurlView, container,index, mPageProvider, mSizeChangedObserver).BookInfo();
				if(Utils.auto){
					Auto();
				}
				createView();
				if(Utils.readNet){
					if(Utils.bWifi(MainActivity.this)){
						if(!checkNetUtils.checkWifi(MainActivity.this)){
							showDialog();
						}else{
							downLoad();
						}
					}else{
						downLoad();
					}
				}
				break;
			case 7:
				maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				curVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
				if(curVolume == 0){
					bnt3.setBackgroundResource(R.drawable.volumep3);
					if(Utils.auto){
						Utils.auto = false;
						bnt1.setBackgroundResource(R.drawable.buttonplay);
					}
				}else{
					bnt3.setBackgroundResource(R.drawable.volumep1);
				}
				seekBar.setMax(maxVolume);
				seekBar.setProgress(curVolume);
				break;
			case 8://关闭书本
				if(bClose){
					bClose = false;
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							
							addPerspectiveView(2,mBookName);
							closeActivity = true;
							MusicHelper.stopMusci();
						}
					}, 1000);
				}
				break;
			case 10:
				if(Utils.bOpen){
					parent.setbTrue(false);
				}else{
					parent.setbTrue(true);
				}
//				showDialog.setVisibility(8);
				iv_cover.setBackgroundDrawable(null);
				anim.stop();
				break;
			case 11:
//				showDialog.setVisibility(1);
				iv_cover.setBackgroundDrawable(anim);
				anim.start();
				break;
			case 12:
				memory_show();
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	@SuppressLint("InlinedApi")
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainactivity);
		DisplayMetrics DM = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(DM);
		Utils.DMWidth = getWindowManager().getDefaultDisplay().getWidth();
		Utils.DMHeight = getWindowManager().getDefaultDisplay().getHeight();
		ScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		ScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
		ShareSDK.initSDK(this);
		//静态实例化数据库
		Utils.mDBHelper = new DBHelper(this);
		Utils.mDBUserInfoHelper = new DBUserInfoHelper(this);
		Utils.mDBCacheHelper = new DBCacheHelper(this);
		Utils.mDBPlayinfoHelper = new DBPlayinfoHelper(this);
		init();
		listener();
		Login();
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		unbindService(conn);
		super.onDestroy();
	}
	protected void onStop(){
		MusicHelper.pauseMusic();
		super.onStop();
	}
	 /*
     * 百度统计模块
     */
	@Override
	protected void onPause() {
		super.onPause();
		StatService.setAppChannel(this,null,false);
		StatService.onPause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
	}
	void init(){
		msgTv = (TextView) findViewById(R.id.mainactivity_notice);
		mainactivity = (FrameLayout) findViewById(R.id.mainactivity);
		parent = (EventLinearLayout) findViewById(R.id.main_parent);
		bookshelfiBnt = (ImageButton) findViewById(R.id.MainmyshelftiBnt);
		commentIBnt = (ImageButton) findViewById(R.id.comment_iBnt);
//		commentIBnt.setVisibility(View.INVISIBLE);
		headIv = (ImageView) findViewById(R.id.infoiBnt);
		Bnt1 = (Button) findViewById(R.id.Bnt1);
		Bnt2 = (Button) findViewById(R.id.Bnt2);
		Bnt3 = (Button) findViewById(R.id.Bnt3);
		Bnt4 = (Button) findViewById(R.id.Bnt4);
		Bnt5 = (Button) findViewById(R.id.Bnt5);
		animLeft = (ImageView) findViewById(R.id.anim_left);
		animRight = (ImageView) findViewById(R.id.anim_right);
		animParent = (LinearLayout) findViewById(R.id.anim_parent);
		container = (FrameLayout) findViewById(R.id.containers);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		details = (Fragment)getSupportFragmentManager().findFragmentById(R.id.fragment);
		showDialog = (RelativeLayout) findViewById(R.id.showDialog);
		showDialog.setVisibility(8);
		headIv.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.work_head));
		
		AppContext ac = (AppContext) getApplication();
		ac.initLoginInfo();
	}
	void listener(){
		mainactivity.setOnTouchListener(this);
		mainactivity.setLongClickable(true);
		mGestureDetector = new GestureDetector(this);  
		bookshelfiBnt.setOnClickListener(this);
		headIv.setOnClickListener(this);
		Bnt1.setOnClickListener(this);
		Bnt2.setOnClickListener(this);
		Bnt3.setOnClickListener(this);
		Bnt4.setOnClickListener(this);
		Bnt5.setOnClickListener(this);
		commentIBnt.setOnClickListener(this);
	}
	public void Login(){
		setVolumeControlStream(AudioManager.RINGER_MODE_SILENT);
		File file = new File(Utils.shareQQ);
		file.mkdirs();
		File str = new File(Utils.shareQQ + "qqPic.jpg");
		try {
			str.createNewFile();
			Bitmap share = BitmapFactory.decodeResource(getResources(), R.drawable.starbaby);
			FileOutputStream fos = new FileOutputStream(str);
			share.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		intent = new Intent(MainActivity.this,MusicService.class);
		details = new FragmentS3(this,translate_down,translate_up,animLeft,animRight,parent,intent,conn,msgTv);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fragment, details);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
		Bnt3.setBackgroundResource(R.drawable.lead_center);
		Bnt3.setTextColor(Color.WHITE);
		
		userInfo = MainActivity.this.getSharedPreferences("diyBook",MODE_WORLD_READABLE);
		String bShare = userInfo.getString("bShare", "");
		if(bShare != null && !bShare.equals("")){
			Utils.bShare = Integer.parseInt(bShare);
		}else{
			Utils.bShare = 0;
		}
		new Thread(new Runnable() {//第二天后,做书参数清零
			
			@Override
			public void run() {
				Time t = new Time();
				t.setToNow();
				int currentDay = t.monthDay;
				if(userInfo.contains("Day")){
					int day = userInfo.getInt("Day", 0);
					if(currentDay > day){
						SharedPreferences.Editor editor = userInfo.edit();
						editor.putBoolean("bAllow", false);
						editor.putInt("Day", 1);
						editor.putInt("DayAllowCount", 0);
						editor.commit();
					}
				}
			}
		}).start();
		audio.setMode(0);
        audio.setRingerMode(2);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, 8, 0);
		showOpenBook();
		if(Utils.openApp){//打开动画
			Utils.openApp = false;
			animLeft.setOnClickListener(this);
			animRight.setOnClickListener(this);
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					
					new Thread(new HeadImgThread(headIv,MainActivity.this)).start();
					new CheckUpdate(MainActivity.this,false).checkUpdate();
					getNoticeNo();
				}
			}, 8000);
		}else{
			animParent.setVisibility(8);
		}
	}
	public void showOpenBook(){
		perspectiveView = new PerspectiveView(this);
		container.addView(perspectiveView);
	}
	void animation(){
		translate_down = AnimationUtils.loadAnimation(MainActivity.this, R.anim.translate_right);
		translate_up = AnimationUtils.loadAnimation(MainActivity.this,R.anim.translate_left);
		translate_up.setFillEnabled(true);
		translate_up.setFillAfter(true);
		translate_down.setFillEnabled(true);
		translate_down.setFillAfter(true);
		translate_up.setStartOffset(10);
		translate_down.setStartOffset(10);
		animLeft.setAnimation(translate_up);
		animRight.setAnimation(translate_down);
		translate_down.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				parent.setbTrue(true);
//				startService(intent);
				bindService(intent, conn, Context.BIND_AUTO_CREATE);
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				 parent.setbTrue(false);
				 poster_show();
				 MusicService.mp.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(MediaPlayer mp) {
//							stopService(intent);
							unbindService(conn);
						}
					});
			}
		});
		animLeft.startAnimation(translate_up);
		animRight.startAnimation(translate_down);
		Utils.bOpenDoor = false;
	}
	@Override
	public void onClick(View v) {
		JavaBean.bookIdList = new ArrayList<String>();
		JavaBean.bookCoverList = new ArrayList<String>();
		JavaBean.bookNameList = new ArrayList<String>();
		JavaBean.bookUpdateTime = new ArrayList<String>();
		switch(v.getId()){
		case R.id.anim_left:
			if(Utils.bOpenDoor){
				parent.setbTrue(true);
				animation();
				animLeft.setClickable(false);
				animRight.setClickable(false);
				Utils.bOpenApp = false;
			}
			break;
		case R.id.anim_right:
			if(Utils.bOpenDoor){
				parent.setbTrue(true);
				animation();
				animLeft.setClickable(false);
				animRight.setClickable(false);
				Utils.bOpenApp = false;
			}
			break;
		case R.id.Bnt1://绘本
			new BookMusicHelper(this).pressBnt();
			if(checkNetUtils.checkNet(this)){
				Utils.s2++;
			}
			details = new FragmentS1(this,msgTv);
			Bnt1.setBackgroundResource(R.drawable.lead_left);
			Bnt2.setBackgroundResource(R.drawable.main_center);
			Bnt3.setBackgroundResource(R.drawable.main_center);
			Bnt4.setBackgroundResource(R.drawable.main_center);
			Bnt1.setTextColor(Color.WHITE);
			Bnt2.setTextColor(Color.BLACK);
			Bnt3.setTextColor(Color.BLACK);
			Bnt4.setTextColor(Color.BLACK);
			break;
		case R.id.Bnt2://益智
			new BookMusicHelper(this).pressBnt();
			if(checkNetUtils.checkNet(this)){
				Utils.s1++;
			}
			details = new FragmentS2(this,msgTv);
			Bnt1.setBackgroundResource(R.drawable.main_left);
			Bnt2.setBackgroundResource(R.drawable.lead_center);
			Bnt3.setBackgroundResource(R.drawable.main_center);
			Bnt4.setBackgroundResource(R.drawable.main_center);
			Bnt1.setTextColor(Color.BLACK);
			Bnt2.setTextColor(Color.WHITE);
			Bnt3.setTextColor(Color.BLACK);
			Bnt4.setTextColor(Color.BLACK);
			break;
		case R.id.Bnt3://新书
			new BookMusicHelper(this).pressBnt();
			if(checkNetUtils.checkNet(this)){
				Utils.sNew++;
			}
			details = new FragmentS3(this,translate_down,translate_up,animLeft,animRight,parent,msgTv);
			Bnt1.setBackgroundResource(R.drawable.main_left);
			Bnt2.setBackgroundResource(R.drawable.main_center);
			Bnt3.setBackgroundResource(R.drawable.lead_center);
			Bnt4.setBackgroundResource(R.drawable.main_center);
			Bnt1.setTextColor(Color.BLACK);
			Bnt2.setTextColor(Color.BLACK);
			Bnt3.setTextColor(Color.WHITE);
			Bnt4.setTextColor(Color.BLACK);
			break;
		case R.id.Bnt4://幼教
			new BookMusicHelper(this).pressBnt();
			if(checkNetUtils.checkNet(this)){
				Utils.s5++;
			}
			details = new FragmentS4(this,msgTv);
			Bnt1.setBackgroundResource(R.drawable.main_left);
			Bnt2.setBackgroundResource(R.drawable.main_center);
			Bnt3.setBackgroundResource(R.drawable.main_center);
			Bnt4.setBackgroundResource(R.drawable.lead_center);
			Bnt1.setTextColor(Color.BLACK);
			Bnt2.setTextColor(Color.BLACK);
			Bnt3.setTextColor(Color.BLACK);
			Bnt4.setTextColor(Color.WHITE);
			break;
		case R.id.Bnt5://更多
			new BookMusicHelper(this).pressBnt();
			startActivity(new Intent(this,MoreBook.class));
			overridePendingTransition(R.anim.sectionbook_in,R.anim.none);
			break;
		case R.id.SDbnt1://播放模式
			new BookMusicHelper(this).pressBnt();
			if(Utils.createMusic){
				if(Utils.auto){//从自动切换为手动
					Utils.auto = false;
					bnt1.setBackgroundResource(R.drawable.buttonplay);
				}else{//从手动切换为自动
					Utils.auto = true;
					if(!MusicHelper.music.isPlaying()){
						startAuto();
					}
					Auto();
					bnt1.setBackgroundResource(R.drawable.buttonpause);
				}
			}
			break;
		case R.id.SDbnt4://分享
			new BookMusicHelper(this).pressBnt();
			if(Utils.basePath1 + mBookName + Utils.basePath2+  NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(0)) != null){//书本没封面的情况下
				SaveShadow.SaveCanvas(this,BitmapFactory.decodeFile(Utils.basePath1 + mBookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(0))));
			}else{
				SaveShadow.SaveCanvas(this,BitmapFactory.decodeFile(Utils.basePath1 + mBookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(2))));
			}
			new ShareComment(this).showShare(false,null, Utils.sharePhoto + "sharePic.jpg");
			break;
		case R.id.SDbnt5://下载
			showDownLoad();
			break;
		case R.id.pop_collect://收藏
			int Uid = userInfo.getInt("uid", 0);//未登入只能查阅三本书籍
			String Psw = userInfo.getString("psw", "");
			if(Psw == null || Psw.equals("")){
				startActivity(new Intent(this,EnterActivityView.class));
			}else{
				if(Utils.mDBUserInfoHelper.bCollect(tpl_id)){
					Log.i("collect","已经收藏过了");
					showBCollect();
				}else{
					Log.i("collect","第一次收藏");
					new CollectBook(this,Uid + "",Psw,bntCollect).collectLocalBook(nameList, tpl_id, updateTime, coverList);
				}
			}
			
			break;
		case R.id.readMode://切换读书模式
			break;
		case R.id.zuoShuMode://切换做书模式
				perspectiveView.setVisibility(View.INVISIBLE);// 自动翻页的时候，打开封面的view先消失
				returnToCover();
			break;
		case R.id.wait_bnt:
			wait_next.dismiss();
			break;
		case R.id.pop_return://关闭工具栏
			new BookMusicHelper(this).pressBnt();
			pop.dismiss();
			mPop = false;
			myFV2 = new MyFloatView(getApplicationContext());
			myFV2.setBackgroundColor(Color.TRANSPARENT);
			myFV2.setImageResource(R.drawable.pop_close);
			wm.addView(myFV2, wmParams2);
			wm.addView(myFV, wmParams);
			mFV = true;
			mFV2 = true;
			myFV2.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Message msg = new Message();
					msg.what = 8;
					mHandler.sendMessage(msg);
				}
			});
			break;
		case R.id.infoiBnt://头像
			new BookMusicHelper(this).pressBnt();
			Intent intent = new Intent(MainActivity.this,UserInfo.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean("bMyshelf", true);
			bundle.putInt("uid", 0);//访问的是自己的头像，所以不传uid
			intent.putExtras(bundle);
//			startActivity(intent);
			startActivityForResult(intent, 9);
			overridePendingTransition(R.anim.sectionbook_in,R.anim.none);
			break;
		case R.id.MainmyshelftiBnt://爱做书
			new BookMusicHelper(this).pressBnt();
//			startActivity(new Intent(MainActivity.this,WorkingShow.class));
			startActivityForResult(new Intent(MainActivity.this,WorkingShow.class), 9);
			overridePendingTransition(R.anim.workbook_in,R.anim.none);
			if(checkNetUtils.checkNet(this)){
				Utils.sWork++;
			}
			break;
		case R.id.warn_bnt2:
			warn_dialog.dismiss();
			break;
		case R.id.memory_bnt2:
			memory_dialog.dismiss();
			break;
		case R.id.poster_bnt2:
			poster_dialog.dismiss();
			break;
		case R.id.wifi_bnt1:
			wifiDialog.dismiss();
			break;
		case R.id.wifi_bnt2:
			wifiDialog.dismiss();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					mDownLoad.saveOther(null);
				}
			}).start();
			break;
		case R.id.comment_iBnt:
			checkBEnter();
			break;
		}
		if(v.getId() == R.id.Bnt1 || v.getId() == R.id.Bnt2 || v.getId() == R.id.Bnt3 || v.getId() == R.id.Bnt4 ){
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.fragment, details);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}
		
	}
	/**
	 * 通过点击书架上的书本 获取具体信息 ,首先下载 第一，二，三。和一首音乐
	 * 记录该书本的素有信息：图片和音乐
	 * 然后自动打开书本动画
	 * @param tpl_id
	 * @param view
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param coverId
	 * @param innerId
	 * @param flag 1:打开书本 2：合闭书本
	 * @param bookName
	 */
	String updateTime;
	String tpl_id;
	LinearLayout linearlayout;
	boolean bLong;
	String nameList;
	String coverList;
	ImageView iv_cover;
	AnimationDrawable anim;
	LinearLayout ll_cover;
	public void loading(final LinearLayout ll_cover,final ImageView iv_cover,final String tpl_id ,final LinearLayout linearlayout,final View view,final  float x,final float y,final float width, final float height,final String updateTime,final String nameList,final String coverList,boolean bLong){//点击获取封面和第一，第二页以及第一首music
		anim = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_list);
		anim.setOneShot(false);
		
		mDownLoad = null;
		ll_cover.setVisibility(View.VISIBLE);
		this.iv_cover = iv_cover;
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "请插入SdCard卡", 1000).show();
			return;
		}
		if(android.os.Build.VERSION.RELEASE.substring(0, 1).equals("2")){
			warn_show();
			return;
		}
		String Name = userInfo.getString("name", "");//未登入只能查阅三本书籍
		String Psw = userInfo.getString("psw", "");
		if(Name == null || Psw == null || Name.equals("") || Psw.equals("")){
			if(Utils.readCount > 5){
				startActivity(new Intent(this,EnterActivityView.class));
				return;
			}
		}
		if(perspectiveView == null){
			perspectiveView = new PerspectiveView(this);
			container.addView(perspectiveView);
		}
		container.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter(){
			 @Override
               public void onAnimationEnd(Animator animation) {
				 container.setVisibility(View.VISIBLE);
               }
		});
		if(bLong){
			index = 0;
		}else{
			index = 1;
		}
		this.tpl_id = tpl_id;
		this.updateTime = updateTime;
		this.bLong = bLong;
		this.nameList = nameList;
		this.coverList = coverList;
		final Thread thread = new Thread(){

			@SuppressLint({ "SdCardPath", "ShowToast" })
			@Override
			public void run() {
				try {
					Cursor mCursor = Utils.mDBHelper.getBookName(tpl_id.toString());
					if(mCursor .getCount() != 0){
						Cursor timeCursor = Utils.mDBHelper.getTIME(tpl_id.toString());
						timeCursor.moveToFirst();
						String LastUpdateTime = timeCursor.getString(timeCursor.getColumnIndex("TIME"));
						timeCursor.close();
						mCursor.close();
						if (LastUpdateTime.equals(updateTime)) {// 判断时间戳。如果一致就读本地。否则删除本地，再在线读取
							Log.i("info", "本地");
							Utils.readNet = false;
							if(new GetData(linearlayout,view,x,y,width,height).getLocalData(tpl_id.toString())){
								Message msg = new Message();
								msg.what = 5;
								mHandler.sendMessage(msg);
							}
						} else {
							DeleteCache.delAllFile(Utils.basePath1 + tpl_id.toString());
							ReadNet(linearlayout,view,x,y,width,height);
						}
					}else{
						ReadNet(linearlayout,view,x,y,width,height);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
				super.run();
			}
		};
		thread.start();
	}
	/*
	 * 从网络读取
	 */
	String tpl_name;
	int image_count;
	int image_width;
	int image_height;
	String bookResult;
	ArrayList<String> imagelist;
	ArrayList<String> audiolist;
	void ReadNet(LinearLayout linearlayout,View view,float x,float y ,float width,float height){
		if(Utils.SDcardMemory > ReadSDcard.readSDCard()){
			if((ReadSDcard.readSDCard() + ReadSDcard.readFile()) > Utils.SDcardMemory){
				DeleteCache.checkRoom(this);
			}else{
				Message msg = new Message();
				msg.what = 12;
				mHandler.sendMessage(msg);
				return;
			}
		}
		Utils.readNet = true;
		Log.i("info","在线");
		Message msg = new Message();
		msg.what = 11;
		mHandler.sendMessage(msg);
		//若果本地有缓存先清除（防止数据混乱）
		File bookFile = new File(Utils.basePath1 + tpl_id.toString());
		if (bookFile.exists()) {
			DeleteCache.delAllFile(Utils.basePath1 + tpl_id.toString());
		}
		if (new GetData(linearlayout,view,x,y,width,height).getDate(HttpPort.bookUrl, tpl_id)) {
			tpl_name = StoreSrc.getTpl_name();
			image_count = StoreSrc.getImage_count();
			image_width = StoreSrc.getImage_width();
			image_height = StoreSrc.getImage_height();
			imagelist = StoreSrc.getImagelist();
			audiolist = StoreSrc.getAudiolist();
			bookResult = StoreSrc.getResult();
			mDownLoad = new DownLoad(MainActivity.this, tpl_name, updateTime, tpl_name, image_count, image_width, image_height, imagelist, audiolist,bookResult);
			Message msg2 = new Message();
			msg2.what = 5;
			mHandler.sendMessage(msg2);
		} else {
			Message msg2 = new Message();
			msg2.what = 10;
			mHandler.sendMessage(msg2);
		}
	}
	@SuppressLint("NewApi")

	public void addPerspectiveView( final int flag,final String bookName) {
		this.linearlayout = JavaBeanLocation.getLinearlayout();
		this.view = JavaBeanLocation.getView();
		this.x = JavaBeanLocation.getX();
		this.y = JavaBeanLocation.getY();
		this.width = JavaBeanLocation.getWidth();
		this.height = JavaBeanLocation.getHeight();
		closeActivity = false;
		Bitmap cover = null;
		Bitmap backCover = null;
		Bitmap innerCover = null;
		Matrix matrix = new Matrix();
		matrix.postScale(-1,1); 
		if(Utils.currentPage == 0 ){
			cover = BitmapFactory.decodeResource(getResources(), R.drawable.coveralpha);
			backCover = BitmapFactory.decodeResource(getResources(), R.drawable.coveralpha);
			innerCover = BitmapFactory.decodeFile(Utils.basePath1 + bookName+ Utils.basePath2 + NamePic.convertUrlToFileName(StoreSrc.getImagelist().get((Utils.currentPage - 1 ) * 2 + 2)));
		}else if(Utils.currentPage == StoreSrc.getImage_count() / 2){
			mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 1, ScreenHeight/2 , 0));
			for(int count = 1;count < 321;count++){
				mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, (int)(Utils.DMWidth / 321)*(count), ScreenHeight/2, 0));
			}
			mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, ScreenWidth, ScreenHeight/2, 0));
			cover = BitmapFactory.decodeFile(Utils.basePath1 + bookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(0)));
			Bitmap bit  = BitmapFactory.decodeFile(Utils.basePath1 + bookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get((Utils.currentPage - 2 ) * 2 + 1)));
			if(bit != null){
				backCover = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),bit.getHeight(), matrix, true);
			}
			innerCover = BitmapFactory.decodeFile(Utils.basePath1 + bookName+ Utils.basePath2 + NamePic.convertUrlToFileName(StoreSrc.getImagelist().get((Utils.currentPage - 2 ) * 2 + 2)));
		}else{
			cover = BitmapFactory.decodeFile(Utils.basePath1 + bookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(0)));
			Bitmap bit  = BitmapFactory.decodeFile(Utils.basePath1 + bookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get((Utils.currentPage - 1 ) * 2 + 1)));
			if(bit != null){
				backCover = Bitmap.createBitmap(bit, 0, 0, bit.getWidth(),bit.getHeight(), matrix, true);
			}
			innerCover = BitmapFactory.decodeFile(Utils.basePath1 + bookName+ Utils.basePath2 + NamePic.convertUrlToFileName(StoreSrc.getImagelist().get((Utils.currentPage - 1 ) * 2 + 2)));
		}
		if(cover == null){
			cover = BitmapFactory.decodeResource(getResources(),R.drawable.right_default);
		}
		if(backCover == null){
			backCover = BitmapFactory.decodeResource(getResources(),R.drawable.left_default);
		}
		if(innerCover == null){
			innerCover = BitmapFactory.decodeResource(getResources(),R.drawable.right_default);
		}
		if(perspectiveView != null){
			perspectiveView.setTextures(cover, innerCover,backCover,x - width * 1.23f ,x + width * 1.23f , y, y + height, flag ,StoreSrc.getImage_width(),StoreSrc.getImage_height(),bLong);
			if(flag == 2){
				perspectiveView.setVisibility(View.VISIBLE);
			}
			final ObjectAnimator animator = ObjectAnimator.ofFloat(container,"alpha", 0f, 0.7f);
			animator.setDuration(800);
			animator.setInterpolator(new AccelerateDecelerateInterpolator());
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					perspectiveView.startAnimation();
					animator.addListener(new AnimatorListener() {
						
						@Override
						public void onAnimationStart(Animator arg0) {
							if(flag == 1){
								new Handler().postDelayed(new Runnable() {
									
									@Override
									public void run() {
										Utils.bSlide = false;
										view.setVisibility(View.INVISIBLE);
										linearlayout.setVisibility(View.INVISIBLE);
									}
								}, 100);
								
							}else if(flag == 2){
								mCurlView.setVisibility(View.INVISIBLE);
								if(pop != null && pop.isShowing())
									pop.dismiss();
							}
						}
						
						@Override
						public void onAnimationRepeat(Animator arg0) {
							
						}
						
						@Override
						public void onAnimationEnd(Animator arg0) {
							if(flag == 1){
								Message msg = new Message();
								msg.what = 6;
								mHandler.sendMessage(msg);
							}else if(flag == 2){
								if (mFV) {
									wm.removeView(myFV);
									mFV = false;
								}
								if (mFV2) {
									wm.removeView(myFV2);
									mFV2 = false;
								}
								if (mPop) {
									pop.dismiss();
									mPop = false;
								}
								Utils.currentPage = 1;
								perspectiveView.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter(){
									 @Override
						                public void onAnimationEnd(Animator animation) {
										 parent.setbTrue(false);
										 perspectiveView.setVisibility(View.GONE);
											container.removeView(perspectiveView);
											perspectiveView = null;
											container.setVisibility(View.INVISIBLE);
						                }
								});
								view.setVisibility(View.VISIBLE);
								linearlayout.setVisibility(View.VISIBLE);
								bClose = true;
								bOpen = false;
								Utils.bOpen = true;
								container.removeView(mCurlView);
								mCurlView = null;
								Utils.bSlide = true;
							}
						}
						
						@Override
						public void onAnimationCancel(Animator arg0) {
							
						}
					});
					animator.start();
				}
			}, 100);
		}
	}
	private commentDialogUtils warn_dialog;
	TextView warn_tv;
	Button warn_bnt2;
	void warn_show(){
		warn_dialog = new commentDialogUtils(this, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, R.layout.warn_info,  R.style.Theme_dialog);
		warn_dialog.show();
		warn_tv = (TextView)warn_dialog.findViewById(R.id.warn_tv);
		warn_bnt2 =(Button)warn_dialog. findViewById(R.id.warn_bnt2);
		warn_bnt2.setOnClickListener(this);
	}
	commentDialogUtils memory_dialog;
	TextView memory_tv;
	Button memory_bnt2;
	void memory_show(){
		memory_dialog = new commentDialogUtils(this, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, R.layout.memory_dialog,  R.style.Theme_dialog);
		memory_dialog.show();
		memory_tv = (TextView)memory_dialog.findViewById(R.id.memory_tv);
		memory_bnt2 =(Button)memory_dialog. findViewById(R.id.memory_bnt2);
		memory_bnt2.setOnClickListener(this);
	}
	commentDialogUtils poster_dialog;
	TextView poster_tv;
	Button poster_bnt2;
	void poster_show(){
		poster_dialog = new commentDialogUtils(this, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, R.layout.show_poster,  R.style.Theme_dialog);
		poster_bnt2 =(Button)poster_dialog. findViewById(R.id.poster_bnt2);
		poster_bnt2.setOnClickListener(this);
		poster_dialog.show();
		Utils.bSlide = true;
	}
	commentDialogUtils wifiDialog;
	Button cancle,ensure;
	void showDialog(){
		wifiDialog = new commentDialogUtils(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, R.layout.wifi_tip, R.style.Theme_dialog);
		cancle =(Button)wifiDialog. findViewById(R.id.wifi_bnt1);
		ensure =(Button)wifiDialog. findViewById(R.id.wifi_bnt2);
		cancle.setOnClickListener(this);
		ensure.setOnClickListener(this);
		wifiDialog.show();
	}
	commentDialogUtils downLoadDialog;
	SeekBar sb;
	void showDownLoad(){
		downLoadDialog = new commentDialogUtils(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, R.layout.download_dialog, R.style.Theme_dialog);
		sb = (SeekBar) downLoadDialog.findViewById(R.id.download_seekBar);
		downLoadDialog.setCanceledOnTouchOutside(false);
		downLoadDialog.show();
		new BookMusicHelper(this).pressBnt();
		
		DeleteCache.delAllFile(Utils.basePath1 + mBookName);
		Utils.mDBHelper.deleteRefresh(mBookName);
		Thread thread3 = new Thread(){
			@SuppressWarnings("static-access")
			public void run() {
				if (new checkNetUtils().checkNet(MainActivity.this)) {
					if(mDownLoad == null){
						if(Utils.SDcardMemory > ReadSDcard.readSDCard()){
							if((ReadSDcard.readSDCard() + ReadSDcard.readFile()) > Utils.SDcardMemory){
								DeleteCache.checkRoom(MainActivity.this);
							}else{
								Message msg = new Message();
								msg.what = 12;
								mHandler.sendMessage(msg);
								return;
							}
						}
						Utils.readNet = true;
						Log.i("info","在线");
						//若果本地有缓存先清除（防止数据混乱）
						File bookFile = new File(Utils.basePath1 + tpl_id.toString());
						if (bookFile.exists()) {
							DeleteCache.delAllFile(Utils.basePath1 + tpl_id.toString());
						}
						try {
							String result = HttpHelper.GetNewBook(HttpPort.bookUrl + Integer.parseInt(tpl_id));
							if(result != null){
								final ArrayList<String> urlList = new ArrayList<String>();
								final ArrayList<String> audioList = new ArrayList<String>();
								JSONObject object = new JSONObject(result);
								final String tpl_name = tpl_id;
								int audio_available = object.getInt("audio_available");
								int image_count = object.getInt("image_count");
								int image_width = object.getInt("image_width");
								int image_height = object.getInt("image_height");
								final JSONArray imagelist = object.getJSONArray("imagelist");
								final JSONArray audiolist = object.getJSONArray("audiolist");
								for (int i = 0; i < imagelist.length(); i++) {
									String url = null;
									url = (String) imagelist.get(i);
									urlList.add(url);
								}
								if (audio_available == 1) {
									for (int i = 0; i < audiolist.length(); i++) {
										String audio = null;
										audio = (String) audiolist.get(i);
										audioList.add(audio);
									}
									Utils.createMusic = true;
								} else {
									Utils.createMusic = false;
								}
								mDownLoad = new DownLoad(MainActivity.this, tpl_name, updateTime, tpl_name, image_count, image_width, image_height, urlList, audioList,result);
								mDownLoad.saveOther(sb);
								Message msg = new Message();
								msg.what = 2;
								mHandler.sendMessage(msg);
							}else{
//								Toast.makeText(MainActivity.this, "下载失败", Toast.LENGTH_LONG).show();
							}
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (org.apache.http.ParseException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}else{
						
						mDownLoad.saveOther(sb);
						Message msg = new Message();
						msg.what = 2;
						mHandler.sendMessage(msg);
					}
				}
				super.run();
			}
		};
		thread3.start();
	}
	/**
	 * 监听返回键
	 */
	public void onBackPressed(){
//		if(closeActivity){
//			super.onBackPressed();
//		}else{
//			Message msg = new Message();
//			msg.what = 8;
//			mHandler.sendMessage(msg);
//		}
	}
	/*
	 * 点击2次退出app
	 */
	 @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "再按一次退出程序",
					Toast.LENGTH_SHORT).show();
			// 利用handler延迟发送更改状态信息
			mHandler2.sendEmptyMessageDelayed(0, 2000);
		} else {
			finish();
			System.exit(0);
		}
	}
	private static boolean isExit = false;

    Handler mHandler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };


	/*
	 * 抽屉按钮
	 */
	WindowManager wm = null;
	WindowManager.LayoutParams wmParams = null;
	WindowManager.LayoutParams wmParams2 = null;
	MyFloatView myFV = null;
	MyFloatView myFV2 = null;

	public void createView() {
		myFV = new MyFloatView(getApplicationContext());
		
		myFV.setImageResource(R.drawable.open);
		myFV.setBackgroundColor(0x00000000);
		myFV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				wm.removeView(myFV);
				mFV = false;
				if(mFV2){
					wm.removeView(myFV2);	
					mFV2 = false;
				}
				showPop();
			}
		});
		//获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		//return
		wmParams = new WindowManager.LayoutParams();
		wmParams.type = 2002;
		wmParams.format = PixelFormat.RGBA_8888;
		wmParams.flags |= 8;
		wmParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL; // 调整悬浮窗口至左上角
		wmParams.x = 0;
		wmParams.y = 0;
		wmParams.width = LayoutParams.WRAP_CONTENT;
		wmParams.height = LayoutParams.WRAP_CONTENT;
		//close
		wmParams2 = new WindowManager.LayoutParams();
		wmParams2.type = 2002;
		wmParams2.format = PixelFormat.RGBA_8888;
		wmParams2.flags |= 8;
		wmParams2.gravity = Gravity.RIGHT | Gravity.TOP; // 调整悬浮窗口至左上角
		wmParams2.x = 0;
		wmParams2.y = 0;
		wmParams2.width = LayoutParams.WRAP_CONTENT;
		wmParams2.height = LayoutParams.WRAP_CONTENT;
		// 显示myFloatView图像
		wm.addView(myFV, wmParams);
		mFV = true;
	}
	/*
	 * 展示工具栏
	 */
	Button bnt1,bnt3,bnt4,bnt5,bnt6,bntCollect;
	LinearLayout pop_ll;
	boolean open = false;
	PopupWindow pop ;
	mSeekBar seekBar;
	float startTime ;
	float endTime ;
	float totalTime ;
	Button readBnt,zuoShuBnt;
	public void showPop(){
		View view  = LayoutInflater.from(this).inflate(R.layout.pop_inflate, null);
		bnt1 = (Button)view. findViewById(R.id.SDbnt1);
		bnt3 = (Button)view. findViewById(R.id.SDbnt3);
		bnt4 = (Button)view. findViewById(R.id.SDbnt4);
		bnt5 = (Button)view. findViewById(R.id.SDbnt5);
		bnt6 = (Button)view. findViewById(R.id.pop_return);
		bntCollect = (Button) view.findViewById(R.id.pop_collect);
		readBnt = (Button) view.findViewById(R.id.readMode);
		zuoShuBnt = (Button) view.findViewById(R.id.zuoShuMode);
		readBnt.setOnClickListener(this);
		zuoShuBnt.setOnClickListener(this);
		pop_ll =(LinearLayout)view. findViewById(R.id.pop_parent);
		pop_ll.setVisibility(8);
		if(Utils.bShare == 0){
			bnt4.setVisibility(8);
		}else{
			bnt4.setVisibility(1);
		}
		seekBar = (mSeekBar)view. findViewById(R.id.seekBar);
		seekBar.setThumb(this.getResources().getDrawable(R.drawable.volum_button));
		if(Utils.createMusic){
			if(Utils.auto){
				bnt1.setBackgroundResource(R.drawable.buttonpause);
			}else{
				bnt1.setBackgroundResource(R.drawable.buttonplay);
			}
			if(Utils.volume){
				bnt3.setBackgroundResource(R.drawable.volumep1);
			}else{
				bnt3.setBackgroundResource(R.drawable.volumep3);
			}
			bnt3.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					
					switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						startTime = SystemClock.uptimeMillis();
						break;
					case MotionEvent.ACTION_UP:
						endTime = SystemClock.uptimeMillis();
						totalTime = endTime - startTime;
						if(totalTime > 1500){
							pop_ll.setVisibility(1);
							ShowSb = true;
							if(!Utils.volume){
								Utils.volume = true;
								bnt3.setBackgroundResource(R.drawable.volumep1);
							}
						}else{
							if(ShowSb){
								pop_ll.setVisibility(8);
								ShowSb = false;
							}else{
								if(Utils.volume){//从有声音切换为静音
									Utils.volume = false;
									audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
									bnt3.setBackgroundResource(R.drawable.volumep3);
								}else{//从静音切换为有声音
									Utils.volume = true;
									audio.setStreamVolume(AudioManager.STREAM_MUSIC, 8, 0);
									bnt3.setBackgroundResource(R.drawable.volumep1);
								}
							}
						}
						break;
					}
					return false;
				}
			});
		}else{
			bnt1.setBackgroundResource(R.drawable.buttonplay);
			bnt3.setBackgroundResource(R.drawable.volumep1);
		}
		pop = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		pop.setFocusable(false);// 设置PopupWindow可获得焦点
		pop.setOutsideTouchable(true);// 设置非PopupWindow区域不可触摸
		pop.setAnimationStyle(R.style.PopupAnimation);
		pop.showAtLocation(findViewById(R.id.mainActivity), Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0);
		pop.update();
		mPop = true;
		bnt1.setOnClickListener(this);
		bnt4.setOnClickListener(this);
		bnt5.setOnClickListener(this);
		bnt6.setOnClickListener(this);
		//这里需要判断。如果收藏数据库里存在，收藏按钮不可按
		if(Utils.mDBUserInfoHelper.bCollect(tpl_id)){//已经收藏了这本书，按钮不可按
			bntCollect.setBackgroundResource(R.drawable.collect2);
		}
		bntCollect.setOnClickListener(this);
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(mSeekBar VerticalSeekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(mSeekBar VerticalSeekBar) {
				
			}
			
			@Override
			public void onProgressChanged(mSeekBar VerticalSeekBar, int progress,boolean fromUser) {
				audio.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
			}
		});
		new Thread(new volumeThread()).start();
	}
	/*
	 * 线程用来实时监控音量
	 */
	class volumeThread implements Runnable {

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				Message msg = new Message();
				msg.what = 7;
				MainActivity.this.mHandler.sendMessage(msg);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	/**
	 * 切换做书模式  向前翻页（翻到首页）
	 */
	void returnToCover(){
		JavaBeanZuoShu mJavaBeanZuoShu = new JavaBeanZuoShu();
		ShowZuoShuTips mShowZuoShuTips = new ShowZuoShuTips(); 
		final GetZuoShuCache mGetZuoShuCache = new GetZuoShuCache(MainActivity.this, mJavaBeanZuoShu, mShowZuoShuTips, mPageProvider, mCurlView, pop);
		if(Utils.currentPage > 0){
			boolean bToCover = true;
			mPageProvider.setBToCover(bToCover);
			mCurlView.setCurrentIndex(1);
			mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN, 1,	ScreenHeight, 0));
			for (int count = 1; count < 321; count++) {
				mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_MOVE, (int)(Utils.DMWidth / 321) * count, ScreenHeight, 0));
			}
			mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_UP, ScreenWidth , ScreenHeight, 0));
		}
		
		mShowZuoShuTips.showTips(this);
		new Thread(new Runnable() {
			public void run() {
				downLoadZuoShu(mGetZuoShuCache);
			}
		}).start();
	}
	/**
	 * 下载做书所需要的内容
	 */
	void downLoadZuoShu(GetZuoShuCache mGetZuoShuCache){
		Cursor cursor = Utils.mDBHelper.getBookName(tpl_id);
		//先清除临时文件夹里的所有内容
		DeleteCache.delAllFile(Utils.path);
		new File(Utils.path).mkdirs();
		if(cursor.getCount() != 0 ){//在线下载完成 或者本地已经缓存了。只需再下载做书的图片
			//有个条件，如果 做书的图片已经下载完成了，直接获取。否则 向服务器请求做书的图片
			String bookResult = Utils.mDBPlayinfoHelper.getResult(Integer.parseInt(tpl_id));
			mGetZuoShuCache.getOtherBookCache(tpl_id,bookResult);
		}else{//重新完整下载 
			try {
				Log.i("MainActivity","net");
				mGetZuoShuCache.getAllBookCache(HttpPort.bookUrl, tpl_id,bookResult);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		cursor.close();
	}
	/**
	 * 自动翻页
	 * @author Administrator
	 *
	 */
	void startAuto(){
		mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN, ScreenWidth,	ScreenHeight, 0));
		for (int count = 1; count < 321; count++) {
			mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_MOVE, (int)(Utils.DMWidth / 321) * (321 - count), ScreenHeight, 0));
		}
		mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_UP, 1, ScreenHeight, 0));
	}
	void Auto(){
		MusicHelper.music.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				Log.i("MUSIC","COMPLETE");
				final int i = mCurlView.getCurrentIndex() + 2;
				if(Utils.auto){
					if (i > (StoreSrc.getAudiolist().size())) {
						Message msg = new Message();
						msg.what = 8;
						mHandler.sendMessage(msg);
					} else {
						mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN, ScreenWidth,	ScreenHeight, 0));
						for (int count = 1; count < 321; count++) {
							mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_MOVE, (int)(Utils.DMWidth / 321) * (321 - count), ScreenHeight, 0));
						}
						mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_UP, 1, ScreenHeight, 0));
						Auto();
					}
				}
			}
		});
	}
	/**
	 * 自动状态下 手指滑动后切换为手动
	 */
	long firstClick;
	long lastClick;
	// 计算点击的次数
	int count;
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		mGestureDetector.onTouchEvent(ev);//在有listview的情况下。先捕获屏幕的左右滑动事件，然后在传递给listview，防止listview覆盖掉左右滑动事件
		if(!Utils.bOpen){
			if(bOpen)
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if(Utils.currentPage * 2 == StoreSrc.getImagelist().size()){
					Message msg = new Message();
					msg.what = 8;
					mHandler.sendMessage(msg);
				}else{
					// 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
					if (firstClick != 0
							&& System.currentTimeMillis() - firstClick > 300) {
						count = 0;
					}
					count++;
					if (count == 1) {
						firstClick = System.currentTimeMillis();
					} else if (count == 2) {
						lastClick = System.currentTimeMillis();
						// 两次点击小于300ms 也就是连续点击
						if (lastClick - firstClick < 300) {// 判断是否是执行了双击事件
							System.out.println(">>>>>>>>执行了双击事件");
							Message msg = new Message();
							msg.what = 8;
							mHandler.sendMessage(msg);
						}
					}
				}
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				break;
			}
		}
		return super.dispatchTouchEvent(ev);
	}
	void downLoad(){
		if(Utils.auto){//自动,下载完所有内容再调用timer
			mDownLoad.saveOther(null);
			showDialog.setVisibility(8);
			Auto();
		}else{//手动,下载动画直到下载完成再消失。期间可以翻页
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					mDownLoad.saveOther(null);
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
				}
			});
			thread.start();
		}
	}
	GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 100;
	private static final int FLING_MIN_VELOCITY = 0;  
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (Utils.bOpen && Utils.bSlide) {
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				Toast.makeText(this, "滑动方向有误，请反方向操作！^_^", Toast.LENGTH_SHORT).show();
			} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				startActivity(new Intent(MainActivity.this, WorkingShow.class));
				overridePendingTransition(R.anim.workbook_in, R.anim.none);
				if (checkNetUtils.checkNet(this)) {
					Utils.sWork++;
				}
			}
		}
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e) {
		
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	@Override
	public void onAnimationUpdate(ValueAnimator animation) {
		// TODO Auto-generated method stub
		
	}
	commentDialogUtils wait_next;
	Button wait;
	void showWait(){
		wait_next = new commentDialogUtils(this, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, R.layout.wait_next, R.style.Theme_dialog);
		wait = (Button) wait_next.findViewById(R.id.wait_bnt);
		TextView tv1 = (TextView) wait_next.findViewById(R.id.wait_tv1);
		TextView tv2 = (TextView) wait_next.findViewById(R.id.wait_tv2);
		tv1.setText("     手机做书功能急急忙忙开发中，敬请期待新版本和关注我们的版本发布公告！ 手机版本未更新前，可在网页http://diy.starbaby.cn 做书，然后会同步在手机上显示。");
		tv1.setTextSize(13);
		tv2.setText("");
		wait.setOnClickListener(this);
		wait_next.show();
	}
	commentDialogUtils collectDialog;
	Button collect;
	void showBCollect(){
		collectDialog = new commentDialogUtils(this, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, R.layout.wait_next, R.style.Theme_dialog);
		collect = (Button) collectDialog.findViewById(R.id.wait_bnt);
		collectDialog.setCanceledOnTouchOutside(false);
		TextView tv1 = (TextView) collectDialog.findViewById(R.id.wait_tv1);
		TextView tv2 = (TextView) collectDialog.findViewById(R.id.wait_tv2);
		tv1.setText("该书已收藏至用户中心个人书架");
		tv2.setText("取消收藏请在个人中心操作");
		collect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				collectDialog.dismiss();
			}
		});
		collectDialog.show();
	}
	private MusicService myService;
	ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			myService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			myService = ((MusicService.MyBinder)service).getService();
			myService.excute();
		}
	};

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		switch (arg1) {
		case 1:
			showPop();
			break;
		case 2:
			getNoticeNo();
			break;
		}
		super.onActivityResult(arg0, arg1, arg2);
	}
	void checkBEnter(){//点击朋友圈 判断是否已经登入
		
//		if (ac.isLogin()) {
//			BasicNameValuePair pairUid = new BasicNameValuePair("uid",new Integer(ac.getLoginUid()).toString());
//			BasicNameValuePair pairPwd = new BasicNameValuePair("pwd",ac.getLoginPwd());
			UIHelper.showActivity(this,FriendsActivity.class);
			overridePendingTransition(R.anim.workbook_in,R.anim.none);
//		}else{
//			startActivity(new Intent(this,EnterActivityView.class));
//		}
	}
	int msgnum;
	void getNoticeNo(){//获取未读消息数
		if(userInfo.contains("msgnum")){
			msgnum = userInfo.getInt("msgnum", 0);
			if(msgnum == 0){
				msgTv.setVisibility(View.INVISIBLE);
			}else{
				msgTv.setVisibility(View.VISIBLE);
				msgTv.setText(msgnum + "");
			}
		}
	}
}
