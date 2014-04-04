package com.starbaby.diyBook.main;
/**
 * 个人信息
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import com.starbaby.diyBook.R;
import com.starbaby.diyBook.cache.ImageGetFromHttp;
import com.starbaby.diyBook.clientbean.URLs;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.clientui.MyFans;
import com.starbaby.diyBook.clientui.MyFollow;
import com.starbaby.diyBook.clientui.MyNotice;
import com.starbaby.diyBook.clientui.MyPost;
import com.starbaby.diyBook.controller.CollectBook;
import com.starbaby.diyBook.controller.DeleteCache;
import com.starbaby.diyBook.controller.GetArtBook;
import com.starbaby.diyBook.controller.GetData;
import com.starbaby.diyBook.controller.GetZuoShuCache;
import com.starbaby.diyBook.controller.HeadImgThread2;
import com.starbaby.diyBook.controller.PageProvider;
import com.starbaby.diyBook.controller.ReadSDcard;
import com.starbaby.diyBook.controller.ShowBookInfo;
import com.starbaby.diyBook.controller.SizeChangedObserver;
import com.starbaby.diyBook.fragment.Fragment1;
import com.starbaby.diyBook.fragment.Fragment2;
import com.starbaby.diyBook.fragment.Fragment4;
import com.starbaby.diyBook.fragment.Fragment6;
import com.starbaby.diyBook.helper.BookMusicHelper;
import com.starbaby.diyBook.helper.HttpHelper;
import com.starbaby.diyBook.helper.MusicHelper;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.helper.SaveShadow;
import com.starbaby.diyBook.helper.ShareComment;
import com.starbaby.diyBook.model.DownLoad;
import com.starbaby.diyBook.net.AsyncHttpPost;
import com.starbaby.diyBook.net.DefaultThreadPool;
import com.starbaby.diyBook.net.RequestParameter;
import com.starbaby.diyBook.net.RequestResultCallback;
import com.starbaby.diyBook.open.PerspectiveView;
import com.starbaby.diyBook.utils.HttpPort;
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
import com.starbaby.diyBook.view.ShowHead;
import com.starbaby.diyBook.view.ShowZuoShuTips;
import com.starbaby.diyBook.view.UserInfoBnt;
import com.starbaby.diyBook.view.mSeekBar;
import com.starbaby.diyBook.view.mSeekBar.OnSeekBarChangeListener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ParseException;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
@SuppressLint({ "ShowToast", "HandlerLeak", "WorldReadableFiles", "SimpleDateFormat", "Recycle" })
public class UserInfo extends FragmentActivity implements OnClickListener{
	private ImageView head;
	private ImageButton book,information,back;
	public static Button bnt1;
	public static Button theme;
	public static UserInfoBnt bnt2;
	public static UserInfoBnt bnt6;
	public static Button bnt8;
	private Fragment details;
	private FrameLayout contains;
	private View view;
	private EventLinearLayout parent;
	private RelativeLayout showDialog;
	public static TextView name_tv;
	private ImageButton attention;
	private LinearLayout attentionLL,fansLL;
	public static TextView attentionTv;
	public static TextView fansTv;
	public static TextView msgTv;
	
	private PerspectiveView mPerspectiveView;
	private AudioManager audio ;
	private CurlView mCurlView;
	private PageProvider mPageProvider;
	private SizeChangedObserver mSizeChangedObserver;
	private SharedPreferences sp;
	private DownLoad mDownLoad;
	
	private Boolean bMyshelf;//判断是否是从书架登入用户中心，还是在家长圈登入用户中心
	private int userUid;//这是用来查看当前用户信息的uid
	private int maxVolume;
	private int curVolume;
	private int index ;
	private int ScreenWidth,ScreenHeight;
	private int uid ;
	private String avatar;
	private String psw;
	private String name;
	private Bitmap userHeadBit = null;//他人的头像
	private int attentiontotal;//关注数
	private int fanstotal;//粉丝数
	private int note_state;//关注状态：0-自己/未登录；1-未关注；2-已关注；       3-相互关注
	private int msgnum;//未读消息数
	private float x,y,width,height;
	private String mBookName;
	private boolean mFV = false;
	private boolean mFV2 = false;
	private boolean mPop = false;
	private boolean ShowSb = false;
	private boolean bClose = true;
	private boolean bOpen = false;//打开动画结束前不允许双击关闭
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				parent.setbTrue(true);//打开后，书本后的所有控件都失去焦点
				showDialog.setVisibility(View.GONE);
				mBookName  = StoreSrc.getTpl_name();
				addPerspectiveView(1, mBookName);
				break;
			case 2:
				downLoadDialog.dismiss();
				Toast.makeText(UserInfo.this, "下载完成", 1000).show();
				break;
			case 3:
				bOpen = true;
				audio.setStreamVolume(AudioManager.STREAM_MUSIC, Utils.bVolum(UserInfo.this), 0);
				contains.setBackgroundColor(0x88888888);
				mCurlView = new CurlView(UserInfo.this,mPerspectiveView);
				mPageProvider = new PageProvider(UserInfo.this, mCurlView, mBookName);
				mSizeChangedObserver = new SizeChangedObserver(mCurlView);
				new ShowBookInfo(UserInfo.this, mCurlView, contains, index, mPageProvider, mSizeChangedObserver).BookInfo();
				if(Utils.auto){
					Auto();
				}
				createView();
				if(Utils.readNet){
					if(Utils.bWifi(UserInfo.this)){
						if(!checkNetUtils.checkWifi(UserInfo.this)){
							showDialog();
						}else{
							downLoad();
						}
					}else{
						downLoad();
					}
				}
				break;
			case 4:
				showDialog.setVisibility(8);
				Toast.makeText(UserInfo.this, "下载成功", 1000).show();
				break;
			case 7:
				maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				curVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
				if(curVolume == 0){
					Bnt3.setBackgroundResource(R.drawable.volumep3);
					if(Utils.auto){
						Utils.auto = false;
						bnt1.setBackgroundResource(R.drawable.buttonplay);
					}
				}else{
					Bnt3.setBackgroundResource(R.drawable.volumep1);
				}
				seekBar.setMax(maxVolume);
				seekBar.setProgress(curVolume);
				break;
			case 8:
				Utils.bOpen = true;
				if(bClose){
					bClose = false;
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							Log.i("last","2");
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
				showDialog.setVisibility(8);
				break;
			case 11:
				showDialog.setVisibility(View.VISIBLE);
				break;
			case 12:
				memory_show();
				break;
			case 13:
				showDialog.setVisibility(8);
				break;
			case 14:
				head.setImageBitmap(userHeadBit);
				break;
			case 15://添加关注成功
				Toast.makeText(UserInfo.this, "关注成功", Toast.LENGTH_LONG).show();
				attention.setVisibility(View.INVISIBLE);
				break;
			case 16://添加关注失败
				Toast.makeText(UserInfo.this, "关注失败", Toast.LENGTH_LONG).show();
				break;
			case 17://已经关注过
				Toast.makeText(UserInfo.this, "已经关注过", Toast.LENGTH_LONG).show();
				attention.setVisibility(View.INVISIBLE);
				break;
			case 18://不能对自己关注
				Toast.makeText(UserInfo.this, "不能对自己关注", Toast.LENGTH_LONG).show();
				attention.setVisibility(View.INVISIBLE);
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo);
		sp = UserInfo.this.getSharedPreferences("diyBook",MODE_WORLD_READABLE);
		uid = sp.getInt("uid",0);
		avatar = sp.getString("avatar", "");
		psw = sp.getString("psw", "");
		name = sp.getString("name", "");
		ScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		ScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
		Utils.bReturnEnter = false;
		bMyshelf = getIntent().getExtras().getBoolean("bMyshelf");
		int mUid = getIntent().getExtras().getInt("uid");//访问当前头像用户的uid，如果为0：则是访问自己。如果不为0：则是访问他人
		if(bMyshelf){
			userUid = uid;
			init();
			listener();
			new Thread(new HeadImgThread2(head,this)).start();
		}else{
			final String avaterUrl = getIntent().getExtras().getString("avater");
			String name = getIntent().getExtras().getString("name");
			userUid = mUid;
			init2();
			listener();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					userHeadBit = new ImageGetFromHttp().downloadBitmap(avaterUrl);
					if(userHeadBit != null){
						Message msg = new Message();
						msg.what = 14;
						mHandler.sendMessage(msg);
					}
				}
			}).start();
			name_tv.setText(name);
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	protected void onStop(){
		MusicHelper.pauseMusic();
		super.onStop();
	}
	@Override
	protected void onPause() {
		super.onPause();
		
	}
	@Override
	protected void onResume() {
		MusicHelper.keepMusic();
		super.onResume();
	}
	private void init() {
		msgTv = (TextView) findViewById(R.id.userinfo_msg);
		back = (ImageButton) findViewById(R.id.userinfo_back);
		information = (ImageButton) findViewById(R.id.userinfo_information);
		parent = (EventLinearLayout) findViewById(R.id.user_parent);
		head = (ImageView) findViewById(R.id.userinfo_iv1);
		bnt1 = (Button) findViewById(R.id.userinfo_bnt1);
		bnt8 = (Button) findViewById(R.id.userinfo_bnt8);
		name_tv = (TextView) findViewById(R.id.user_name);
		attention = (ImageButton) findViewById(R.id.add_attention);
		attentionLL = (LinearLayout) findViewById(R.id.attention);
		attentionTv = (TextView) findViewById(R.id.attention_tv1);
		fansLL = (LinearLayout) findViewById(R.id.fans);
		fansTv = (TextView) findViewById(R.id.fans_tv1);
		
//		name_tv.setVisibility(View.INVISIBLE);
//		attention.setVisibility(View.INVISIBLE);
//		attentionLL.setVisibility(View.INVISIBLE);
//		attentionTv.setVisibility(View.INVISIBLE);
//		fansLL.setVisibility(View.INVISIBLE);
//		fansTv.setVisibility(View.INVISIBLE);
		
		bnt8.setTextColor(0xFFFF0000);
		theme = (Button) findViewById(R.id.userinfo_theme);
		theme.setTextColor(0xFFFF0000);
		bnt2 = (UserInfoBnt) findViewById(R.id.userinfo_bnt2);
		bnt2.setBit(R.drawable.userinfo_bnt2);
		bnt2.setText("关于我们");
		bnt6 = (UserInfoBnt) findViewById(R.id.userinfo_bnt6);
		bnt6.setBit(R.drawable.userinfo_bnt6);
		bnt6.setText("分享好友");
		bnt6.setColor(0xFFFFFFFF);
		if(Utils.bShare == 0){
			bnt6.setVisibility(4);
		}else{
			bnt6.setVisibility(1);
		}
		book = (ImageButton) findViewById(R.id.userinfo_iBnt1);
		if(uid != 0 && psw != null && !psw.equals("")){//已经登入
			name_tv.setVisibility(View.VISIBLE);
			details = new Fragment1(this,uid,uid + "",psw,attentionTv,fansTv,true,attention,msgTv);
			bnt2.setColor(0xFFFFFFFF);
			bnt1.setTextColor(0xFFFFFFFF);
			Utils.sInfo++;
			attentiontotal = sp.getInt("attentiontotal", 0);
			fanstotal = sp.getInt("fanstotal", 0);
			msgnum = sp.getInt("msgnum", 0);
			attentionTv.setText(attentiontotal + "");
			fansTv.setText(fanstotal + "");
			if(msgnum == 0){
				msgTv.setVisibility(View.INVISIBLE);
			}else{
				msgTv.setVisibility(View.VISIBLE);
				msgTv.setText(msgnum + "");
			}
		}else{//没有登入
			name_tv.setVisibility(View.INVISIBLE);
			details = new Fragment2(this);
			bnt2.setColor(0xFF000000);
			bnt1.setTextColor(0xFFFF0000);
			bnt1.setBackgroundResource(R.drawable.userinfo_left_off);
			bnt2.setBackgroundResource(R.drawable.userinfo_choose_in);
		}
		this.getSupportFragmentManager().beginTransaction().replace(R.id.userinfo_contains, details).commit();
		
		back.setVisibility(View.INVISIBLE);
		name_tv.setText(name);
		attention.setVisibility(View.INVISIBLE);
		contains = (FrameLayout) findViewById(R.id.userinfo_containers);
		showDialog = (RelativeLayout) findViewById(R.id.userinfo_showDialog);
		showDialog.setVisibility(View.GONE);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.setMode(0);
        audio.setRingerMode(2);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, 8, 0);
		showOpenBook();
	}
	void init2(){
		msgTv = (TextView) findViewById(R.id.userinfo_msg);
		back = (ImageButton) findViewById(R.id.userinfo_back);
		information = (ImageButton) findViewById(R.id.userinfo_information);//通知
		parent = (EventLinearLayout) findViewById(R.id.user_parent);
		head = (ImageView) findViewById(R.id.userinfo_iv1);
		bnt1 = (Button) findViewById(R.id.userinfo_bnt1);
		bnt1.setText("他的书架");
		bnt8 = (Button) findViewById(R.id.userinfo_bnt8);//系统设置
		name_tv = (TextView) findViewById(R.id.user_name);
		attention = (ImageButton) findViewById(R.id.add_attention);
		attentionLL = (LinearLayout) findViewById(R.id.attention);
		attentionTv = (TextView) findViewById(R.id.attention_tv1);
		fansLL = (LinearLayout) findViewById(R.id.fans);
		fansTv = (TextView) findViewById(R.id.fans_tv1);
		bnt8.setTextColor(0xFFFF0000);
		theme = (Button) findViewById(R.id.userinfo_theme);
		theme.setTextColor(0xFFFF0000);
		theme.setBackgroundResource(R.drawable.userinfo_right_off);
		bnt2 = (UserInfoBnt) findViewById(R.id.userinfo_bnt2);
		bnt2.setBit(R.drawable.userinfo_bnt2);
		bnt2.setText("关于我们");
		bnt6 = (UserInfoBnt) findViewById(R.id.userinfo_bnt6);
		bnt6.setBit(R.drawable.userinfo_bnt6);
		bnt6.setText("分享好友");
		bnt6.setVisibility(View.INVISIBLE);
		bnt6.setColor(0xFFFFFFFF);
		if(Utils.bShare == 0){
			bnt6.setVisibility(4);
		}else{
			bnt6.setVisibility(1);
		}
		book = (ImageButton) findViewById(R.id.userinfo_iBnt1);
		details = new Fragment1(this, userUid, uid + "", psw, attentionTv,fansTv, false, attention, msgTv);
		bnt2.setColor(0xFFFFFFFF);
		bnt1.setTextColor(0xFFFFFFFF);
		Utils.sInfo++;
		this.getSupportFragmentManager().beginTransaction().replace(R.id.userinfo_contains, details).commit();
		
		back.setVisibility(View.VISIBLE);
		bnt2.setVisibility(View.INVISIBLE);
		book.setVisibility(View.INVISIBLE);
		information.setVisibility(View.INVISIBLE);
		attention.setVisibility(View.INVISIBLE);
		bnt8.setVisibility(View.GONE);
		name_tv.setText(name);
		contains = (FrameLayout) findViewById(R.id.userinfo_containers);
		showDialog = (RelativeLayout) findViewById(R.id.userinfo_showDialog);
		showDialog.setVisibility(View.GONE);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.setMode(0);
        audio.setRingerMode(2);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, 8, 0);
		showOpenBook();
	}
	void showOpenBook(){
		mPerspectiveView = new PerspectiveView(this);
		contains.addView(mPerspectiveView);
	}

	private void listener() {
		bnt2.setOnClickListener(this);
		bnt6.setOnClickListener(this);
		bnt8.setOnClickListener(this);
		book.setOnClickListener(this);
		theme.setOnClickListener(this);
		information.setOnClickListener(this);
		attention.setOnClickListener(this);
		attentionLL.setOnClickListener(this);
		fansLL.setOnClickListener(this);
		back.setOnClickListener(this);
		attention.setOnClickListener(this);
		if(bMyshelf){
			bnt1.setOnClickListener(this);
			head.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		new BookMusicHelper(this).pressBnt();
		switch(v.getId()){
		case R.id.userinfo_bnt1://我的作品
			if(bMyshelf){
				if(!Utils.isFastDoubleClick()){
					uid = sp.getInt("uid",0);
					avatar = sp.getString("avatar", "");
					psw = sp.getString("psw", "");
					if(uid != 0 && psw != null && !psw.equals("")){
						details = new Fragment1(this,userUid,uid + "",psw,attentionTv,fansTv,true,attention,msgTv);
					}else{
						Utils.bReturnEnter = true;
						startActivityForResult(new Intent(this,EnterActivityView.class), 0);
					}
					bnt2.setColor(0xFFFFFFFF);
					bnt6.setColor(0xFFFFFFFF);
					bnt1.setTextColor(0xFFFFFFFF);
					bnt8.setTextColor(0xFFFF0000);
					theme.setTextColor(0xFFFF0000);
					bnt1.setBackgroundResource(R.drawable.userinfo_left_in);
					bnt2.setBackgroundResource(R.drawable.userinfo_choose_off);
					bnt6.setBackgroundResource(R.drawable.userinfo_choose_off);
					bnt8.setBackgroundResource(R.drawable.userinfo_right_off);
					theme.setBackgroundResource(R.drawable.userinfo_mid_off);
				}
			}else{
				details = new Fragment1(this,userUid,uid + "",psw,attentionTv,fansTv,false,attention,msgTv);
			}
			break;
		case R.id.userinfo_bnt2://关于我们
			details = new Fragment2(UserInfo.this);
			bnt2.setColor(0xFF000000);
			bnt6.setColor(0xFFFFFFFF);
			bnt1.setTextColor(0xFFFF0000);
			bnt8.setTextColor(0xFFFF0000);
			theme.setTextColor(0xFFFF0000);
			bnt1.setBackgroundResource(R.drawable.userinfo_left_off);
			bnt2.setBackgroundResource(R.drawable.userinfo_choose_in);
			bnt6.setBackgroundResource(R.drawable.userinfo_choose_off);
			bnt8.setBackgroundResource(R.drawable.userinfo_right_off);
			theme.setBackgroundResource(R.drawable.userinfo_mid_off);
			break;
		case R.id.userinfo_bnt6://分享好友
			details = new Fragment6();
			bnt2.setColor(0xFFFFFFFF);
			bnt6.setColor(0xFF000000);
			bnt1.setTextColor(0xFFFF0000);
			bnt8.setTextColor(0xFFFF0000);
			theme.setTextColor(0xFFFF0000);
			bnt1.setBackgroundResource(R.drawable.userinfo_left_off);
			bnt2.setBackgroundResource(R.drawable.userinfo_choose_off);
			bnt6.setBackgroundResource(R.drawable.userinfo_choose_in);
			bnt8.setBackgroundResource(R.drawable.userinfo_right_off);
			theme.setBackgroundResource(R.drawable.userinfo_mid_off);
			break;
		case R.id.userinfo_bnt8://系统设置
			details = new Fragment4(this);
			bnt2.setColor(0xFFFFFFFF);
			bnt6.setColor(0xFFFFFFFF);
			bnt1.setTextColor(0xFFFF0000);
			bnt8.setTextColor(0xFFFFFFFF);
			theme.setTextColor(0xFFFF0000);
			bnt1.setBackgroundResource(R.drawable.userinfo_left_off);
			bnt2.setBackgroundResource(R.drawable.userinfo_choose_off);
			bnt6.setBackgroundResource(R.drawable.userinfo_choose_off);
			bnt8.setBackgroundResource(R.drawable.userinfo_right_in);
			theme.setBackgroundResource(R.drawable.userinfo_mid_off);
			break;
		case R.id.userinfo_theme://话题
			checkEnter(MyPost.class);
//			showWait();
			break;
		case R.id.userinfo_information://通知
			checkEnter(MyNotice.class);
//			showWait();
			break;
		case R.id.wait_bnt:
			wait_next.dismiss();
			break;
		case R.id.userinfo_iBnt1://退出当前
			setResult(2);
			this.finish();
			overridePendingTransition(R.anim.none,R.anim.sectionbook_out);
			break;
		case R.id.userinfo_iv1://头像
			if(uid == 0){
				Utils.bReturnEnter = true;
				startActivityForResult(new Intent(this,EnterActivityView.class), 0);
			}else{
				startActivity(new Intent(this,ShowHead.class));
			}
			break;
		case R.id.SDbnt1://播放模式
			if(Utils.createMusic){
				if(Utils.auto){//从自动切换为手动
					Utils.auto = false;
					Bnt1.setBackgroundResource(R.drawable.buttonplay);
				}else{//从手动切换为自动
					Utils.auto = true;
					Auto();
					Bnt1.setBackgroundResource(R.drawable.buttonpause);
				}
			}
			break;
		case R.id.SDbnt4://分享
			if((Utils.basePath1 + mBookName + Utils.basePath2+ Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(0))) != null){
				SaveShadow.SaveCanvas(this, BitmapFactory.decodeFile(Utils.basePath1 + mBookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(0))));
			}else{
				SaveShadow.SaveCanvas(this, BitmapFactory.decodeFile(Utils.basePath1 + mBookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(2))));
			}
			new ShareComment(this).showShare(false, null, Utils.sharePhoto + "sharePic.jpg");
			break;
		case R.id.SDbnt5://下载
			showDownLoad();
			break;
		case R.id.pop_collect://收藏
			if(Utils.mDBUserInfoHelper.bCollect(tpl_id)){
				showBCollect();
			}else{
				new CollectBook(this,uid + "",psw,bntCollect).collectLocalBook(nameList, tpl_id, updateTime, coverList);
			}
			break;
		case R.id.readMode://切换读书模式
			break;
		case R.id.zuoShuMode://切换做书模式
			mPerspectiveView.setVisibility(View.INVISIBLE);// 自动翻页的时候，打开封面的view先消失
			returnToCover();
			break;
		case R.id.pop_return://关闭工具栏
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
		case R.id.enter_bnt1://注册
			startActivity(new Intent(UserInfo.this,RegisterAccount.class));
			overridePendingTransition(R.anim.workbook_in,R.anim.none);
			break;
		case R.id.warn_bnt2:
			warn_dialog.dismiss();
			break;
		case R.id.memory_bnt2:
			memory_dialog.dismiss();
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
		case R.id.add_attention://添加关注
			if(sp.getInt("uid", 0) == 0){
				startActivity(new Intent(this,EnterActivityView.class));
				return;
			}
			addAttentionPost();
			break;
		case R.id.attention://关注
			checkEnter(MyFollow.class);
			break;
		case R.id.fans://粉丝
			checkEnter(MyFans.class);
			break;
		case R.id.userinfo_back:
			this.finish();
			overridePendingTransition(R.anim.none,R.anim.workbook_out);
			break;
		}
		if(v.getId() == R.id.userinfo_bnt1 || v.getId() == R.id.userinfo_bnt2  || v.getId() == R.id.userinfo_bnt6 ||v.getId() == R.id.userinfo_bnt8){
			this.getSupportFragmentManager().beginTransaction().replace(R.id.userinfo_contains, details).commit();
		}
	}
	String updateTime;
	LinearLayout linearlayout;
	String tpl_id;
	boolean bLong;
	String nameList;
	String coverList;
	public void loading(final String tpl_id ,final LinearLayout linearlayout,final View view, final float x,final  float y,final float width,final  float height,final String updateTime,final String nameList,final String coverList,boolean bLong){//点击获取封面和第一，第二页以及第一首music
		mDownLoad = null;
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "请插入SdCard卡", 1000).show();
			return;
		}
		if(android.os.Build.VERSION.RELEASE.substring(0, 1).equals("2")){
			warn_show();
			return;
		}
		if(mPerspectiveView == null){
			mPerspectiveView = new PerspectiveView(this);
			contains.addView(mPerspectiveView);
		}
		contains.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter(){
			 @Override
               public void onAnimationEnd(Animator animation) {
				 contains.setVisibility(View.VISIBLE);
               }
		});
		this.bLong = bLong;
		if(bLong){
			index = 0;
		}else{
			index = 1;
		}
		this.tpl_id = tpl_id;
		this.updateTime = updateTime;
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
								msg.what = 1;
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
			image_count = StoreSrc.getImagelist().size();
			image_width = StoreSrc.getImage_width();
			image_height = StoreSrc.getImage_height();
			imagelist = StoreSrc.getImagelist();
			audiolist = StoreSrc.getAudiolist();
			bookResult = StoreSrc.getResult();
			mDownLoad = new DownLoad(UserInfo.this, tpl_name, updateTime, tpl_name, image_count, image_width, image_height, imagelist, audiolist,bookResult);
			Message msg2 = new Message();
			msg2.what = 1;
			mHandler.sendMessage(msg2);
		} else {
			Message msg2 = new Message();
			msg2.what = 10;
			mHandler.sendMessage(msg2);
		}
	}
	boolean closeActivity = true;
	Bitmap cover = null;
	Bitmap backCover = null;
	Bitmap innerCover = null;
	@SuppressLint("NewApi")
	public void addPerspectiveView( final int flag,final String bookName) {
		this.linearlayout = JavaBeanLocation.getLinearlayout();
		this.view = JavaBeanLocation.getView();
		this.x = JavaBeanLocation.getX();
		this.y = JavaBeanLocation.getY();
		this.width = JavaBeanLocation.getWidth();
		this.height = JavaBeanLocation.getHeight();
		closeActivity = false;
		
		Matrix matrix = new Matrix();
		matrix.postScale(-1,1);
		if(Utils.currentPage == 0 ){
			cover = BitmapFactory.decodeResource(getResources(), R.drawable.coveralpha);
			backCover = BitmapFactory.decodeResource(getResources(), R.drawable.coveralpha);
			innerCover = BitmapFactory.decodeFile(Utils.basePath1 + bookName+ Utils.basePath2 + NamePic.convertUrlToFileName(StoreSrc.getImagelist().get((Utils.currentPage - 1 ) * 2 + 2)));
		}else if(Utils.currentPage == StoreSrc.getImagelist().size() / 2){
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
		if(mPerspectiveView != null){
			Log.i("last","4");
			mPerspectiveView.setTextures(cover, innerCover,backCover,x - width * 1.23f ,x + width * 1.23f, y, y + height, flag ,StoreSrc.getImage_width(),StoreSrc.getImage_height(),bLong);
			if(flag == 2){
				if(mPerspectiveView != null)
				mPerspectiveView.setVisibility(View.VISIBLE);
			}
			final ObjectAnimator animator = ObjectAnimator.ofFloat(contains,"alpha", 0f, 0.7f);
			animator.setDuration(800);
			animator.setInterpolator(new AccelerateDecelerateInterpolator());
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if(mPerspectiveView != null)
					mPerspectiveView.startAnimation();
					animator.addListener(new AnimatorListener() {
						
						@Override
						public void onAnimationStart(Animator arg0) {
							if(flag == 1){
								new Handler().postDelayed(new Runnable() {
									
									@Override
									public void run() {
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
								msg.what = 3;
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
								
								if(mPerspectiveView != null)
								mPerspectiveView.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter(){
									 @Override
						                public void onAnimationEnd(Animator animation) {
										 parent.setbTrue(false);
												if (mPerspectiveView != null)
												mPerspectiveView.setVisibility(View.GONE);
												contains.removeView(mPerspectiveView);
												mPerspectiveView = null;
												contains.setVisibility(View.INVISIBLE);
						                }
								});
								view.setVisibility(View.VISIBLE);
								linearlayout.setVisibility(View.VISIBLE);
								bClose = true;
								bOpen = false;
								contains.removeView(mCurlView);
								mCurlView = null;
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
		warn_dialog = new commentDialogUtils(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, R.layout.warn_info,  R.style.Theme_dialog);
		warn_dialog.show();
		warn_tv = (TextView)warn_dialog.findViewById(R.id.warn_tv);
		warn_bnt2 =(Button)warn_dialog. findViewById(R.id.warn_bnt2);
		warn_bnt2.setOnClickListener(this);
	}
	private commentDialogUtils memory_dialog;
	TextView memory_tv;
	Button memory_bnt2;
	void memory_show(){
		memory_dialog = new commentDialogUtils(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, R.layout.memory_dialog,  R.style.Theme_dialog);
		memory_dialog.show();
		memory_tv = (TextView)memory_dialog.findViewById(R.id.memory_tv);
		memory_bnt2 =(Button)memory_dialog. findViewById(R.id.memory_bnt2);
		memory_bnt2.setOnClickListener(this);
	}
	Dialog wifiDialog;
	void showDialog(){
		wifiDialog = new commentDialogUtils(this, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, R.layout.wifi_tip, R.style.Theme_dialog);
		Button cancle =(Button)wifiDialog. findViewById(R.id.wifi_bnt1);
		Button ensure =(Button)wifiDialog. findViewById(R.id.wifi_bnt2);
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
				if (new checkNetUtils().checkNet(UserInfo.this)) {
					if(mDownLoad == null){
						if(Utils.SDcardMemory > ReadSDcard.readSDCard()){
							if((ReadSDcard.readSDCard() + ReadSDcard.readFile()) > Utils.SDcardMemory){
								DeleteCache.checkRoom(UserInfo.this);
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
								mDownLoad = new DownLoad(UserInfo.this, tpl_name, updateTime, tpl_name, image_count, image_width, image_height, urlList, audioList,result);
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
	Button Bnt1,Bnt3,Bnt4,Bnt5,Bnt6,bntCollect;
	LinearLayout pop_ll;
	boolean open = false;
	PopupWindow pop ;
	mSeekBar seekBar;
	float startTime ;
	float endTime ;
	float totalTime ;
	Button readBnt,zuoShuBnt;
	@SuppressWarnings("deprecation")
	void showPop(){
		View view  = LayoutInflater.from(this).inflate(R.layout.pop_inflate, null);
		Bnt1 = (Button)view. findViewById(R.id.SDbnt1);
		Bnt3 = (Button)view. findViewById(R.id.SDbnt3);
		Bnt4 = (Button)view. findViewById(R.id.SDbnt4);
		Bnt5 = (Button)view. findViewById(R.id.SDbnt5);
		Bnt6 = (Button)view. findViewById(R.id.pop_return);
		readBnt = (Button) view.findViewById(R.id.readMode);
		zuoShuBnt = (Button) view.findViewById(R.id.zuoShuMode);
		bntCollect = (Button) view.findViewById(R.id.pop_collect);
		readBnt.setOnClickListener(this);
		zuoShuBnt.setOnClickListener(this);
		pop_ll =(LinearLayout)view. findViewById(R.id.pop_parent);
		pop_ll.setVisibility(8);
		if(Utils.bShare == 0){
			Bnt4.setVisibility(8);
		}else{
			Bnt4.setVisibility(1);
		}
		seekBar = (mSeekBar)view. findViewById(R.id.seekBar);
		if(Utils.createMusic){
			if(Utils.auto){
				Bnt1.setBackgroundResource(R.drawable.buttonpause);
			}else{
				Bnt1.setBackgroundResource(R.drawable.buttonplay);
			}
			if(Utils.volume){
				Bnt3.setBackgroundResource(R.drawable.volumep1);
			}else{
				Bnt3.setBackgroundResource(R.drawable.volumep3);
			}
			Bnt3.setOnTouchListener(new OnTouchListener() {
				
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
							ShowSb = true;
							pop_ll.setVisibility(1);
							if(!Utils.volume){
								Utils.volume = true;
								Bnt3.setBackgroundResource(R.drawable.volumep1);
							}
						}else{
							if(ShowSb){
								pop_ll.setVisibility(8);
								ShowSb = false;
							}else{
								if(Utils.volume){//从有声音切换为静音
									Utils.volume = false;
									audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
									Bnt3.setBackgroundResource(R.drawable.volumep3);
								}else{//从静音切换为有声音
									Utils.volume = true;
									audio.setStreamVolume(AudioManager.STREAM_MUSIC, 8, 0);
									Bnt3.setBackgroundResource(R.drawable.volumep1);
								}
							}
						}
						break;
					}
					return false;
				}
			});
		}else{
			Bnt1.setBackgroundResource(R.drawable.buttonplay);
			Bnt3.setBackgroundResource(R.drawable.volumep1);
		}
		pop = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		pop.setFocusable(false);// 设置PopupWindow可获得焦点
		pop.setOutsideTouchable(true);// 设置非PopupWindow区域不可触摸
		pop.setAnimationStyle(R.style.PopupAnimation);
		pop.showAtLocation(findViewById(R.id.user_parent), Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0);
		pop.update();
		mPop = true;
		Bnt1.setOnClickListener(this);
		Bnt3.setOnClickListener(this);
		Bnt4.setOnClickListener(this);
		Bnt5.setOnClickListener(this);
		Bnt6.setOnClickListener(this);
		//这里需要判断。如果收藏数据库里存在，收藏按钮不可按
		if (Utils.mDBUserInfoHelper.bCollect(tpl_id)) {// 已经收藏了这本书，按钮不可按
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
	
	WindowManager wm = null;
	WindowManager.LayoutParams wmParams = null;
	WindowManager.LayoutParams wmParams2 = null;
	MyFloatView myFV = null;
	MyFloatView myFV2 = null;

	private void createView() {
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
	/**
	 * 自动翻页
	 * @author Administrator
	 *
	 */
	void Auto(){
		MusicHelper.music.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				final int i = mCurlView.getCurrentIndex() + 2;
				if(Utils.auto){
					if (i > (StoreSrc.getAudiolist().size())) {
						Message msg = new Message();
						msg.what = 8;
						mHandler.sendMessage(msg);
					} else {
						mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_DOWN, ScreenWidth,	ScreenHeight, 0));
						for (int count = 1; count < 321; count++) {
							mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_MOVE, (int)(Utils.DMWidth / 321)* (321 - count), ScreenHeight, 0));
						}
						mCurlView.onTouch(mCurlView, MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),MotionEvent.ACTION_UP, 1, ScreenHeight, 0));
						Auto();
					}
				}
			}
		});
	}
	/*
	 * 监听音量
	 */
	class volumeThread implements Runnable {

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				Message msg = new Message();
				msg.what = 7;
				UserInfo.this.mHandler.sendMessage(msg);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	/*
	 * 监听返回键
	 */
	public void onBackPressed(){
		//撤销掉back键的功能
//		Message msg = new Message();
//		msg.what = 8;
//		mHandler.sendMessage(msg);
	}
	/*
	 * 下载功能
	 */
	void downLoad(){
		if(Utils.auto){//自动,下载完所有内容再调用timer
			showDialog.setVisibility(View.VISIBLE);
			mDownLoad.saveOther(null);
			showDialog.setVisibility(View.GONE);
			Auto();
		}else{//手动,下载动画直到下载完成再消失。期间可以翻页
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					mDownLoad.saveOther(null);
					Message msg = new Message();
					msg.what = 13;
					mHandler.sendMessage(msg);
				}
			});
			thread.start();
		}
	}
	/**
	 * 自动状态下 手指滑动后切换为手动
	 */
	boolean AutoPage = false;
	boolean starAutoPage = false;
	long firstClick;
	long lastClick;
	// 计算点击的次数
	int count;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
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
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		switch(arg1){
		case 0:
			name = sp.getString("name","");
			uid = sp.getInt("uid",0);
			avatar = sp.getString("avatar", "");
			psw = sp.getString("psw", "");
			userUid = uid;
			if(uid != 0 && psw != null && !psw.equals("")){
				details = new Fragment1(this,userUid,uid + "",psw,attentionTv,fansTv,true,attention,msgTv);
				Utils.sInfo++;
			}
			name_tv.setVisibility(View.VISIBLE);
			name_tv.setText(name);
			bnt1.setTextColor(0xFFFFFFFF);
			bnt2.setColor(0xFFFFFFFF);
			bnt6.setColor(0xFFFFFFFF);
			bnt8.setTextColor(0xFFFF0000);
			theme.setTextColor(0xFFFF0000);
			bnt1.setBackgroundResource(R.drawable.userinfo_left_in);
			bnt2.setBackgroundResource(R.drawable.userinfo_choose_off);
			bnt6.setBackgroundResource(R.drawable.userinfo_choose_off);
			bnt8.setBackgroundResource(R.drawable.userinfo_right_off);
			theme.setBackgroundResource(R.drawable.userinfo_mid_off);
			this.getSupportFragmentManager().beginTransaction().replace(R.id.userinfo_contains, details).commitAllowingStateLoss();
			break;
		case 1:
			showPop();
			break;
		case 3:
			int msgnum;
			if (sp.contains("msgnum")) {
				msgnum = sp.getInt("msgnum", 0);
				if (msgnum == 0) {
					msgTv.setVisibility(View.INVISIBLE);
				} else {
					msgTv.setVisibility(View.VISIBLE);
					msgTv.setText(msgnum + "");
				}
			}
			break;
		default:
			break;
		}
		super.onActivityResult(arg0, arg1, arg2);
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
	/**
	 * 切换做书模式  向前翻页（翻到首页）
	 */
	void returnToCover(){
		JavaBeanZuoShu mJavaBeanZuoShu = new JavaBeanZuoShu();
		ShowZuoShuTips mShowZuoShuTips = new ShowZuoShuTips(); 
//		final GetArtBook mGetArtBook = new GetArtBook(this, mJavaBeanZuoShu,mShowZuoShuTips,mPageProvider,mCurlView,pop);
		final GetZuoShuCache mGetZuoShuCache = new GetZuoShuCache(UserInfo.this, mJavaBeanZuoShu, mShowZuoShuTips, mPageProvider, mCurlView, pop);
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
			
			@Override
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
	/*
	 * 登入判断
	 */
	void checkEnter(Class<?> enterActivity){
		if(bMyshelf){
			String Name = sp.getString("name", "");
			String Psw = sp.getString("psw", "");
			if(Name == null || Psw == null || Name.equals("") || Psw.equals("")){
				startActivityForResult(new Intent(this,EnterActivityView.class), 0);
				return;
			}
		}
		UIHelper.showUserActivity(userUid,UserInfo.this, enterActivity);
		overridePendingTransition(R.anim.workbook_in,R.anim.none);
	}
	/*
	 * 添加关注
	 */
	void addAttentionPost(){
			final Message msg = new Message();
			ArrayList<RequestParameter> parameter = new ArrayList<RequestParameter>();
			parameter.add(new RequestParameter("uid", sp.getInt("uid", 0) + ""));
			parameter.add(new RequestParameter("pwd", sp.getString("psw", "")));
			parameter.add(new RequestParameter("auid", userUid + ""));
			AsyncHttpPost post = new AsyncHttpPost(null, URLs.POST_FENSI_ADD,parameter, new RequestResultCallback() {

						@Override
						public void onSuccess(Object o) {
							String result = (String)o;
							Log.i("result",result);
							JSONObject object;
							try {
								object = new JSONObject(result);
								String mMsg = object.getString("msg");
								if(mMsg.equals("1")){
									msg.what = 15;
								}else if(mMsg.equals("-8")){
									msg.what = 18;
								}else if(mMsg.equals("-9")){
									msg.what = 17;
								}else{
									msg.what = 16;
								}
								mHandler.sendMessage(msg);
							} catch (JSONException e) {
								e.printStackTrace();
							}
						
						}

						@Override
						public void onFail(Exception e) {
							msg.what = 16;
							mHandler.sendMessage(msg);
							Log.i("result", e.toString());
						}
					});
			
			DefaultThreadPool.getInstance().execute(post);
	}
	commentDialogUtils wait_next;
	Button wait;
	TextView tv1;
	TextView tv2;
	void showWait(){
		wait_next = new commentDialogUtils(this, android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT, R.layout.wait_next, R.style.Theme_dialog);
		wait = (Button) wait_next.findViewById(R.id.wait_bnt);
		tv1 = (TextView) wait_next.findViewById(R.id.wait_tv1);
		tv2 = (TextView) wait_next.findViewById(R.id.wait_tv2);
		wait.setOnClickListener(this);
		wait_next.show();
	}
}
