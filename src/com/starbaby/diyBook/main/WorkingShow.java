package com.starbaby.diyBook.main;
/**
 * 爱做书
 */
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.BookShelfAdapter4;
import com.starbaby.diyBook.cache.GetCoverCache2;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.clientui.FriendsActivity;
import com.starbaby.diyBook.controller.CollectBook;
import com.starbaby.diyBook.controller.DeleteCache;
import com.starbaby.diyBook.controller.GetArtBook;
import com.starbaby.diyBook.controller.GetData;
import com.starbaby.diyBook.controller.GetZuoShuCache;
import com.starbaby.diyBook.controller.HeadImgThread;
import com.starbaby.diyBook.controller.PageProvider;
import com.starbaby.diyBook.controller.ReadSDcard;
import com.starbaby.diyBook.controller.ShowBookInfo;
import com.starbaby.diyBook.controller.SizeChangedObserver;
import com.starbaby.diyBook.helper.BookMusicHelper;
import com.starbaby.diyBook.helper.HttpHelper;
import com.starbaby.diyBook.helper.MusicHelper;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.helper.SaveShadow;
import com.starbaby.diyBook.helper.ShareComment;
import com.starbaby.diyBook.model.CoverThreadPool2;
import com.starbaby.diyBook.model.DownLoad;
import com.starbaby.diyBook.net.AsyncHttpGet;
import com.starbaby.diyBook.net.DefaultThreadPool;
import com.starbaby.diyBook.net.RequestResultCallback;
import com.starbaby.diyBook.open.PerspectiveView;
import com.starbaby.diyBook.utils.HttpPort;
import com.starbaby.diyBook.utils.JavaBean2;
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
import com.starbaby.diyBook.view.PullToRefreshListView;
import com.starbaby.diyBook.view.ShowZuoShuTips;
import com.starbaby.diyBook.view.mSeekBar;
import com.starbaby.diyBook.view.PullToRefreshListView.OnRefreshListener;
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
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
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
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
@SuppressLint({ "HandlerLeak", "ShowToast", "WorldReadableFiles", "Recycle" })
public class WorkingShow extends BaseActivity implements OnClickListener,OnGestureListener ,OnTouchListener{
	private ImageView head,book,title;
	private PullToRefreshListView lv;
	private FrameLayout contains,workingshow;
	private EventLinearLayout parent;
	RelativeLayout showDialog;
	private TextView msgTv;
	private ImageButton commitiBnt;
	
	private JavaBean2 mJavaBean2;
	private CurlView mCurlView;
	private PageProvider mPageProvider;
	private SizeChangedObserver mSizeChangedObserver;
	private AudioManager audio ;
	private PerspectiveView mPerspectiveView;
	private SharedPreferences userInfo ;
	private DownLoad mDownLoad;
	
	private String section = "爱做书";
	private int lastItem;
	private int ScreenWidth,ScreenHeight;
	private int index ;
	private int mCurrentPage = 2;
	private int maxVolume;
	private int curVolume;
	private boolean mFV = false;
	private boolean mFV2 = false;
	private boolean mPop = false;
	private boolean ShowSb = false;
	private boolean bPost = true;
	private String mBookName;
	private BookShelfAdapter4 mAdapter;
	private boolean bClose = true;
	boolean bOpen = false;
	boolean bSlide = true;
	int outUid = 0;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				mCurrentPage++;
				if(msg.arg1 == 1){
					mAdapter = new BookShelfAdapter4(WorkingShow.this, mJavaBean2, section, 1);
					lv.setAdapter(mAdapter);
				}else if (msg.arg1 == 2){
					mAdapter.notifyDataSetChanged();
					bPost = true;
				}
				getNoticeNo();
				break;
			case 1:
				Utils.readCount++;
				parent.setbTrue(true);//打开后，书本后的所有控件都失去焦点
				showDialog.setVisibility(View.GONE);
				mBookName  = StoreSrc.getTpl_name();
				addPerspectiveView(1, mBookName);
				break;
			case 2:
				Toast.makeText(WorkingShow.this, "连接网络失败", 1000).show();
				showDialog.setVisibility(View.GONE);
				break;
			case 3:
				bOpen = true;
				audio.setStreamVolume(AudioManager.STREAM_MUSIC, Utils.bVolum(WorkingShow.this), 0);
				contains.setBackgroundColor(0x88888888);
				mCurlView = new CurlView(WorkingShow.this,mPerspectiveView);
				mPageProvider = new PageProvider(WorkingShow.this, mCurlView, mBookName);
				mSizeChangedObserver = new SizeChangedObserver(mCurlView);
				new ShowBookInfo(WorkingShow.this, mCurlView, contains, index, mPageProvider, mSizeChangedObserver).BookInfo();
				if(Utils.auto){
					Auto();
				}
				createView();
				if(Utils.readNet){
					if(Utils.bWifi(WorkingShow.this)){
						if(!checkNetUtils.checkWifi(WorkingShow.this)){
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
				showDialog.setVisibility(View.GONE);
				Toast.makeText(WorkingShow.this, "下载完成", 1000).show();
				break;
			case 5:
				downLoadDialog.dismiss();
				Toast.makeText(WorkingShow.this, "下载完成", 1000).show();
				break;
			case 7:
				maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				curVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
				if(curVolume == 0){
					Bnt3.setBackgroundResource(R.drawable.volumep3);
					if(Utils.auto){
						Utils.auto = false;
						Bnt1.setBackgroundResource(R.drawable.buttonplay);
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
				showDialog.setVisibility(View.GONE);
				break;
			case 11:
				showDialog.setVisibility(View.VISIBLE);
				break;
			case 12:
				memory_show();
				break;
			case 13:
				showDialog.setVisibility(View.GONE);
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workingshow);
		ScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		ScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
		userInfo = WorkingShow.this.getSharedPreferences("diyBook",MODE_WORLD_READABLE);
		init();
		listener();
		getNoticeNo();
		new Thread(new HeadImgThread(head,this)).start();
	}
	@Override
	protected void onDestroy() {
		Log.i("onDestory","onDestory");
		super.onDestroy();
	}
	protected void onStop(){
		MusicHelper.pauseMusic();
		Log.i("onStop","onStop");
		super.onStop();
	}
	@Override
	protected void onPause() {
		Log.i("onPause","onPause");
		super.onPause();
		
	}
	@Override
	protected void onResume() {
		Log.i("onResume","onResume");
		super.onResume();
		MusicHelper.keepMusic();
	}
	
	@Override
	protected void onRestart() {
		Log.i("onRestart","onRestart");
		super.onRestart();
	}
	
	@Override
	protected void onStart() {
		Log.i("onStart","onStart");
		super.onStart();
	}
	private void init() {
		commitiBnt = (ImageButton) findViewById(R.id.comment_iBnt2);
		msgTv = (TextView) findViewById(R.id.workshowing_notice);
		workingshow = (FrameLayout) findViewById(R.id.workingshow);
		parent = (EventLinearLayout) findViewById(R.id.work_parent);
		head = (ImageView) findViewById(R.id.work_head);
		book = (ImageView) findViewById(R.id.work_book);
		title = (ImageView) findViewById(R.id.worK_title);
		lv = (PullToRefreshListView) findViewById(R.id.work_list);
		mAdapter = new BookShelfAdapter4(WorkingShow.this, mJavaBean2, section, 1);
		lv.setAdapter(mAdapter);
		
		lv.setOnRefreshListener(new OnRefreshListener() {//下拉
            @Override
            public void onRefresh() {
            	/**加载数据，刷新ListView*/
            	getData(1,1,section,false); 
            	new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						lv.onRefreshComplete();
					}
				}, 3000);
            }
        });
        
	    lv.setOnScrollListener(new OnScrollListener() {//上拉
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if ((lastItem > new BookShelfAdapter4(WorkingShow.this, mJavaBean2,section,1).getCount() - 1) || (lastItem == new BookShelfAdapter4(WorkingShow.this, mJavaBean2,section,1).getCount() - 1)) {
					if(bPost){
						getData(mCurrentPage, 2, section, false);
						bPost = false;
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem - 2 + visibleItemCount;
			}
		});
		contains = (FrameLayout) findViewById(R.id.work_containers);
		showDialog = (RelativeLayout) findViewById(R.id.workingshow_showDialog);
		audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audio.setMode(0);
        audio.setRingerMode(2);
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, 8, 0);
		mJavaBean2 = new JavaBean2();
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			showCache();
			showOpenBook();
		}else{
	    	Toast.makeText(this, "请插入SdCard卡", 1000).show();
	    }
	}
	void showOpenBook(){
		mPerspectiveView = new PerspectiveView(this);
		contains.addView(mPerspectiveView);
		showDialog.setVisibility(View.GONE);
	}
	void showCache(){
		if(new GetCoverCache2(WorkingShow.this, section, mJavaBean2).getData()){
			mAdapter = new BookShelfAdapter4(WorkingShow.this, mJavaBean2, section, 1);
			lv.setAdapter(mAdapter);
		}else{
			mCurrentPage = 1;
			getData(1,1,section,false);
		}
		if(Utils.sWork > 0 && (Utils.sWork % 3) == 0){
			getData(1,1,section,true);
		}
		
	}
	@SuppressWarnings("deprecation")
	private void listener() {
		workingshow.setOnTouchListener(this);
		workingshow.setLongClickable(true);
		mGestureDetector = new GestureDetector(this);  
		head.setOnClickListener(this);
		book.setOnClickListener(this);
		title.setOnClickListener(this);
		commitiBnt.setOnClickListener(this);
	}

	@SuppressWarnings("static-access")
	private void getData(int currentPage,final int refresh,final String mSection,final boolean sCount) {
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
		outUid = userInfo.getInt("uid", 0);
		AsyncHttpGet httpget = new AsyncHttpGet(null, HttpPort.workShowUrl + currentPage + "/" + outUid,null, new RequestResultCallback() {

			@SuppressWarnings({ "unused" })
			@Override
			public void onSuccess(Object o) {
				final String result = (String) o;
				Log.i("resutl",result);
				JSONObject object;
				JSONArray array;
				String msgNum = "0";
				if(result != null)
				if(refresh == 1){
					ArrayList<String> bookCoverList = new ArrayList<String>();
					ArrayList<String> bookNameList = new ArrayList<String>();
					ArrayList<String> bookIdList = new ArrayList<String>();
					ArrayList<String> bookUpdateTime = new ArrayList<String>();
					mJavaBean2.bookCoverList = new ArrayList<String>();
					mJavaBean2.bookNameList = new ArrayList<String>();
					mJavaBean2.bookIdList = new ArrayList<String>();
					mJavaBean2.bookUpdateTime = new ArrayList<String>();
					try {
						object = new JSONObject(result);
						msgNum = object.getString("msgnum");
						int total_size = object.getInt("total_size");
						array = object.getJSONArray("all_works");
						for (int i = 0; i < array.length(); i++) {
							JSONObject list = array.getJSONObject(i);
							String templates_id = list.getString("product_id");
							String templates_name = list.getString("product_name");
							String thumb_img = list.getString("thumb_img");
							String update_time = list.getString("update_time");
							mJavaBean2.bookCoverList.add(thumb_img);
							mJavaBean2.bookNameList.add(templates_name);
							mJavaBean2.bookIdList.add(templates_id);
							mJavaBean2.bookUpdateTime.add(update_time);
							bookCoverList.add(thumb_img);
							bookNameList.add(templates_name);
							bookIdList.add(templates_id);
							bookUpdateTime.add(update_time);
							Log.i("bookCoverList",thumb_img);
						}
						Log.i("bookCoverList",bookCoverList.size() + "");
					} catch (JSONException e) {
						e.printStackTrace();
					}
					new CoverThreadPool2(WorkingShow.this, bookCoverList, mSection).saveCover();
					Utils.mDBCacheHelper.deleteSection(mSection);
					for (int j = 0; j < bookCoverList.size(); j++) {
						if(Utils.workBookCache){
							Utils.workBookCache = false;
							if (!Utils.mDBCacheHelper.saveCache(mSection,bookCoverList.get(j),bookNameList.get(j),bookIdList.get(j),bookUpdateTime.get(j))) {
								Utils.workBookCache = true;
								if (sCount) {
									Utils.sWork--;
									break;
								}
							}
							Utils.workBookCache = true;
						}
						
					}
				}else{
					try {
						object = new JSONObject(result);
						msgNum = object.getString("msgnum");
						int total_size = object.getInt("total_size");
						array = object.getJSONArray("all_works");
						for (int i = 0; i < array.length(); i++) {
							JSONObject list = array.getJSONObject(i);
							String templates_id = list.getString("product_id");
							String templates_name = list.getString("product_name");
							String thumb_img = list.getString("thumb_img");
							String update_time = list.getString("update_time");
							mJavaBean2.bookCoverList.add(thumb_img);
							mJavaBean2.bookNameList.add(templates_name);
							mJavaBean2.bookIdList.add(templates_id);
							mJavaBean2.bookUpdateTime.add(update_time);
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				SharedPreferences.Editor editor = userInfo.edit();
				editor.putInt("msgnum", Integer.parseInt(msgNum));
				editor.commit();
				Message msg = new Message();
				msg.what = 0;
				msg.arg1 = refresh;
				mHandler.sendMessage(msg);
			}

			@Override
			public void onFail(Exception e) {
				Log.i("connect fail", e.toString());
				Message msg = new Message();
				msg.what = 2;
				mHandler.sendMessage(msg);
				bPost = true;
			}
		});
		DefaultThreadPool.getInstance().execute(httpget);
	}
	@Override
	public void onClick(View v) {
		new BookMusicHelper(this).pressBnt();
		switch(v.getId()){
		case R.id.work_head:
			Intent intent = new Intent(WorkingShow.this, UserInfo.class);
			Bundle bundle = new Bundle();
			bundle.putBoolean("bMyshelf", true);
			bundle.putInt("uid",0);//访问的是自己的头像，所以不传uid
			intent.putExtras(bundle);
			startActivity(intent);
			overridePendingTransition(R.anim.sectionbook_in,R.anim.none);
			break;
		case R.id.work_book://返回
			setResult(2);
			this.finish();
			overridePendingTransition(R.anim.none,R.anim.workbook_out);
			break;
		case R.id.SDbnt1://播放模式
			if (Utils.createMusic) {
				if (Utils.auto) {// 从自动切换为手动
					Utils.auto = false;
					Bnt1.setBackgroundResource(R.drawable.buttonplay);
				} else {// 从手动切换为自动
					Utils.auto = true;
					Auto();
					Bnt1.setBackgroundResource(R.drawable.buttonpause);
				}
			}
			break;
		case R.id.SDbnt4://分享
			if((Utils.basePath1 + mBookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(0))) != null){
				SaveShadow.SaveCanvas(this, BitmapFactory.decodeFile(Utils.basePath1 + mBookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(0))));
//				new ShareComment(this).showShare(false,null,Utils.basePath1 + mBookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(0)));
			}else{
				SaveShadow.SaveCanvas(this, BitmapFactory.decodeFile(Utils.basePath1 + mBookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(2))));
//				new ShareComment(this).showShare(false,null,Utils.basePath1 + mBookName + Utils.basePath2+ NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(2)));
			}
			new ShareComment(this).showShare(false, null,Utils.sharePhoto + "sharePic.jpg");
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
					showBCollect();
				}else{
					new CollectBook(this,Uid + "",Psw,bntCollect).collectLocalBook(nameList, tpl_id, updateTime, coverList);
				}
			}
			break;
		case R.id.readMode://切换读书模式
			break;
		case R.id.zuoShuMode://切换做书模式
			mPerspectiveView.setVisibility(View.INVISIBLE);// 自动翻页的时候，打开封面的view先消失
			returnToCover();
			break;
		case R.id.wait_bnt:
			wait_next.dismiss();
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
		case R.id.fragment_1_bnt2:
			dialog.dismiss();
			break;
		case R.id.worK_title:
			show();
			break;
		case R.id.warn_bnt2:
			warn_dialog.dismiss();
			break;
		case R.id.memory_bnt2:
			memory_dialog.dismiss();
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
		case R.id.comment_iBnt2:
			checkBEnter();
			break;
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
	 * @param flag
	 * @param bookName
	 */
	View view;
	float x,y,width,height;
	int flag;
	String updateTime;
	String tpl_id;
	LinearLayout linearlayout;
	boolean bLong;
	String nameList;
	String coverList;
	public void loading(final String tpl_id ,final LinearLayout linearlayout,final View view,final float x,final float y,final float width,final float height, final String updateTime,final String nameList,final String coverList,boolean bLong){//点击获取封面和第一，第二页以及第一首music
		mDownLoad = null;
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "请插入SdCard卡", 1000).show();
			return;
		}
		if(android.os.Build.VERSION.RELEASE.substring(0, 1).equals("2")){
			warn_show();
			return;
		}
		String Name = userInfo.getString("name", "");
		String Psw = userInfo.getString("psw", "");
		if(Name == null || Psw == null || Name.equals("") || Psw.equals("")){
			if(Utils.readCount > 5){
				startActivity(new Intent(this,EnterActivityView.class));
				return;
			}
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
		this.flag = flag;
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
						if (LastUpdateTime.equals(updateTime)) {// 判断时间戳。如果一致就读本地。否则删除本地，再在线读取
							Log.i("info","本地");
							Utils.readNet = false;
							if(new GetData(linearlayout,view,x,y,width,height).getLocalData(tpl_id.toString())){
								Message msg = new Message();
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
						}else{
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
			mDownLoad = new DownLoad(WorkingShow.this, tpl_name, updateTime, tpl_name, image_count, image_width, image_height, imagelist, audiolist,bookResult);
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
	@SuppressLint("NewApi")
	public void addPerspectiveView(final int flag,final String bookName) {
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
		if(mPerspectiveView != null){
			mPerspectiveView.setTextures(cover, innerCover,backCover,x - width * 1.23f ,x + width * 1.23f, y, y + height, flag ,StoreSrc.getImage_width(),StoreSrc.getImage_height(),bLong);
			if(flag == 2){
				mPerspectiveView.setVisibility(View.VISIBLE);
			}
			final ObjectAnimator animator = ObjectAnimator.ofFloat(contains,"alpha", 0f, 0.7f);
			animator.setDuration(800);
			animator.setInterpolator(new AccelerateDecelerateInterpolator());
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					mPerspectiveView.startAnimation();
					animator.addListener(new AnimatorListener() {
						
						@Override
						public void onAnimationStart(Animator arg0) {
							if(flag == 1){
								new Handler().postDelayed(new Runnable() {
									
									@Override
									public void run() {
										bSlide = false;
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
								mPerspectiveView.animate().alpha(0f).setDuration(200).setListener(new AnimatorListenerAdapter(){
									 @Override
						                public void onAnimationEnd(Animator animation) {
										 parent.setbTrue(false);
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
								bSlide = true;
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
			if (Utils.auto) {
				Bnt1.setBackgroundResource(R.drawable.buttonpause);
			} else {
				Bnt1.setBackgroundResource(R.drawable.buttonplay);
			}
			if (Utils.volume) {
				Bnt3.setBackgroundResource(R.drawable.volumep1);
			} else {
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
		pop.showAtLocation(findViewById(R.id.work_parent), Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0);
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
	class volumeThread implements Runnable {

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				Message msg = new Message();
				msg.what = 7;
				WorkingShow.this.mHandler.sendMessage(msg);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
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
	private commentDialogUtils dialog;
	TextView tv;
	Button bnt2;
	void show(){
		dialog = new commentDialogUtils(this, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, R.layout.fragment_1_show,  R.style.Theme_dialog);
		dialog.show();
		tv = (TextView)dialog.findViewById(R.id.fragment_1_tv);
		bnt2 =(Button)dialog. findViewById(R.id.fragment_1_bnt2);
		bnt2.setOnClickListener(this);
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
				if (new checkNetUtils().checkNet(WorkingShow.this)) {
					if(mDownLoad == null){
						if(Utils.SDcardMemory > ReadSDcard.readSDCard()){
							if((ReadSDcard.readSDCard() + ReadSDcard.readFile()) > Utils.SDcardMemory){
								DeleteCache.checkRoom(WorkingShow.this);
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
								mDownLoad = new DownLoad(WorkingShow.this, tpl_name, updateTime, tpl_name, image_count, image_width, image_height, urlList, audioList,result);
								mDownLoad.saveOther(sb);
								Message msg = new Message();
								msg.what = 5;
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
						msg.what = 5;
						mHandler.sendMessage(msg);
					}
				}
				super.run();
			}
		};
		thread3.start();
	}
	public void onBackPressed(){
//		Message msg = new Message();
//		msg.what = 8;
//		mHandler.sendMessage(msg);
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
		mGestureDetector.onTouchEvent(ev);
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
		if (Utils.bOpen && bSlide) {
			if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				this.finish();
				overridePendingTransition(R.anim.none,R.anim.workbook_out);
			} else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
					&& Math.abs(velocityX) > FLING_MIN_VELOCITY) {
				Toast.makeText(this, "滑动方向有误，请反方向操作！^_^", 1000).show();
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
	commentDialogUtils wait_next;
	Button wait;
	void showWait(){
		wait_next = new commentDialogUtils(this, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT, R.layout.wait_next, R.style.Theme_dialog);
		wait = (Button) wait_next.findViewById(R.id.wait_bnt);
		TextView tv1 = (TextView) wait_next.findViewById(R.id.wait_tv1);
		TextView tv2 = (TextView) wait_next.findViewById(R.id.wait_tv2);
		tv1.setText(" 手机做书功能急急忙忙开发中，敬请期待新版本和关注我们的版本发布公告！ 手机版本未更新前，可在网页http://diy.starbaby.cn 做书，然后会同步在手机上显示。");
		tv2.setText("");
		tv1.setTextSize(13);
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
	/**
	 * 切换做书模式  向前翻页（翻到首页）
	 */
	void returnToCover(){
		JavaBeanZuoShu mJavaBeanZuoShu = new JavaBeanZuoShu();
		ShowZuoShuTips mShowZuoShuTips = new ShowZuoShuTips(); 
//		final GetArtBook mGetArtBook = new GetArtBook(this, mJavaBeanZuoShu,mShowZuoShuTips,mPageProvider,mCurlView,pop);
		final GetZuoShuCache mGetZuoShuCache = new GetZuoShuCache(WorkingShow.this, mJavaBeanZuoShu, mShowZuoShuTips, mPageProvider, mCurlView, pop);
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

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		switch (arg1) {
		case 1:
			showPop();
			break;
		}
		super.onActivityResult(arg0, arg1, arg2);
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
	void checkBEnter(){//点击朋友圈 判断是否已经登入
		UIHelper.showActivity(this, FriendsActivity.class);
		overridePendingTransition(R.anim.workbook_in,R.anim.none);
	}
}
