package com.starbaby.diyBook.clientui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.FaceAdapter;
import com.starbaby.diyBook.adapter.FacePagerAdapter;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientbean.Comment;
import com.starbaby.diyBook.clientbean.EmojiDate;
import com.starbaby.diyBook.clientcommon.EmoticonUtil;
import com.starbaby.diyBook.clientcommon.FaceConversionUtil;
import com.starbaby.diyBook.clientcommon.StringUtils;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.view.EnterActivityView;
 

public class ReplyDialog extends BaseActivity {
	private InputMethodManager imm;
	private ViewSwitcher mViewSwitcher;
	private AnimationDrawable loadingAnimation;
	private ImageButton mClose;
	private Button 		mSend;
	private ImageView   mFace;
	
	private EditText    mContent;
	private TextView	mQuote;
	private View 		mLoading;
	
	private static final String PREFIX = "回复@";
	
	private String content;
	
	private Boolean first = true;
	
	private int tId;
	private int postId;
	private int uid;
	private String pwd;
	private String author;
	
	/** 显示表情页的viewpager */
	private ViewPager vpFace;
	/** 表情页界面集合 */
	private ArrayList<View> pageViews;
	/** 游标显示布局 */
	private LinearLayout layoutPoint;
	/** 游标点集合 */
	private ArrayList<ImageView> pointViews;
	/** 表情区域 */
	private View view;
	/** 表情数据填充器 */
	private List<FaceAdapter> faceAdapters;
	/** 当前表情页 */
	private int current = 0;
	
	//识别Dialog
	
	@Override 
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reply_dialog);
		this.initView();
		
		this.initViewPager();
		this.initPoint();
		this.initFaceData();
	}
	
	private void initView(){
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		
		tId = getIntent().getIntExtra("tid", 0);
		postId = getIntent().getIntExtra("poistId", 0);
		author = getIntent().getStringExtra("author");
		
		mViewSwitcher = (ViewSwitcher)findViewById(R.id.replydialog_view_switcher);
		mLoading = (View)findViewById(R.id.reply_dialog_loading);
		mClose = (ImageButton)findViewById(R.id.replydialog_close_button);
		
		mSend = (Button)findViewById(R.id.replydialog_reply_button);
		mSend.setEnabled(true);
		
		mContent = (EditText)findViewById(R.id.reply_pub_content);
		mContent.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(first){
					imm.showSoftInput(mContent, 0);
					//隐藏表情
					hideFace();
					first = false;
				}else{
					showOrHideIMM();
				}
			}
		});
		
		mQuote = (TextView)findViewById(R.id.reply_pub_quote);
		
		mQuote.setText(UIHelper.parseQuoteSpan(author,FaceConversionUtil.convetToHtml(getIntent().getStringExtra("content"), ReplyDialog.this)));
		
		mClose.setOnClickListener(UIHelper.finish(ReplyDialog.this));
		mSend.setOnClickListener(replyClickListener);
		
		mFace = (ImageView)findViewById(R.id.reply_pub_face);
		mFace.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showOrHideIMM();
			}
		});
		
	   	vpFace = (ViewPager) findViewById(R.id.vp_contains);
		layoutPoint = (LinearLayout) findViewById(R.id.iv_image);
	    view = findViewById(R.id.ll_facechoose);
	    
		String mInitParams = "appid=" + getString(R.string.app_id);
		//初始化转写Dialog, appid需要在http://open.voicecloud.cn获取.
	}
	
	private View.OnClickListener replyClickListener = new View.OnClickListener() {
		
		
		@Override
		public void onClick(View v) {
			content = FaceConversionUtil.convertToMsg(mContent.getEditableText(), ReplyDialog.this); 
			if(StringUtils.isEmpty(content)){
				UIHelper.ToastMessage(v.getContext(), "请输入评论内容");
				return;
			} else {
				content = PREFIX+author+": "+content;  
			}
			
			final AppContext ac = (AppContext)getApplication();
			if(!ac.isLogin()){
				UIHelper.showLogin(ReplyDialog.this);
				return;
			}
			uid = ac.getLoginUid();
			pwd = ac.getLoginPwd();
			
			mClose.setVisibility(View.GONE);
			view.setVisibility(View.GONE); //hide face
			mSend.setEnabled(false);
			
			loadingAnimation = (AnimationDrawable)mLoading.getBackground();
			loadingAnimation.start();
			mViewSwitcher.showNext();
			
			final Handler handler = new Handler(){
				public void handleMessage(Message msg){
					mSend.setEnabled(true);
					if(msg.what==1){
						Intent intent = new Intent();
						intent.putExtra("reply_tid", tId);
						setResult(RESULT_OK,intent);
						finish();
					} else if(msg.what==-1){
						mViewSwitcher.showPrevious();
						mClose.setVisibility(View.VISIBLE);
						((AppException)msg.obj).makeToast(ReplyDialog.this);
					}else {
						mViewSwitcher.showPrevious();
						mClose.setVisibility(View.VISIBLE);
						/*boolean clean = false; //是否清除登陆信息
						//System.out.println("msg.what=="+msg.what);
						if(msg.what==-3 ||msg.what==-2 || msg.what==-4 ||msg.what==-5){
							clean=true;
							((AppContext)getApplication()).Logout(); //注销登录
						}*/
						UIHelper.ToastMessage(ReplyDialog.this,getErrorInfo(msg.what));
						//UIHelper.showErrorDialog(ReplyDialog.this,getErrorInfo(msg.what), clean); //弹出错误消息对话框
					}
				}
			};
			
			new Thread(){
				public void run(){
					Message msg = new Message();
					Comment comment = new Comment();
					try {
						if(ac.isNetworkConnected()){
							comment = ac.repComment(uid, pwd, tId, postId,content);
							msg.what = comment.getMsg();
						}else {
							msg.what = -14;
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
	};

	 private void showFace() {
			mFace.setImageResource(R.drawable.widget_bar_keyboard);
			mFace.setTag(1);
			view.setVisibility(View.VISIBLE);
	    }
	 
	 private void hideFace() {
	    	mFace.setImageResource(R.drawable.widget_bar_face);
	    	mFace.setTag(null);
	    	view.setVisibility(View.GONE);
	    }
	 
	 private void showOrHideIMM() {
	    	if(mFace.getTag() == null){
				//隐藏软键盘
				imm.hideSoftInputFromWindow(mContent.getWindowToken(), 0);
				//显示表情
				showFace();			
			}else{
				//显示软键盘
				imm.showSoftInput(mContent, 0);
				//隐藏表情
				hideFace();
			}
	    }
	 
	 private void initViewPager(){
		 pageViews = new ArrayList<View>();
		 //左侧加空白
		 View nullView1 = new View(this);
		 pageViews.add(nullView1);
		 //中间添加表情页
		 faceAdapters = new ArrayList<FaceAdapter>();
		// System.out.println(emojis.size());
		 int pageCount = (int) Math.ceil(EmojiDate.emojiImgArrray.size() / 20);
		 for(int i=0;i<pageCount;i++){
			 GridView view = new GridView(this);
			 FaceAdapter adapter = new FaceAdapter(this,FaceConversionUtil.getInstace().getData(i));
			 view.setAdapter(adapter);
			 faceAdapters.add(adapter);
			 view.setOnItemClickListener(faceItemClickListener);
			 view.setNumColumns(7);
			 view.setBackgroundColor(Color.TRANSPARENT);
			 view.setHorizontalSpacing(1);
			 view.setVerticalSpacing(1);
			 view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			 view.setCacheColorHint(0);
			 view.setPadding(5, 0, 5, 0);
			 view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			 view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		     view.setGravity(Gravity.CENTER);
			 pageViews.add(view);
		 }
		 
			// 右侧添加空页面
			View nullView2 = new View(this);
			// 设置透明背景
			nullView2.setBackgroundColor(Color.TRANSPARENT);
			pageViews.add(nullView2);
	 }
	 
	 OnItemClickListener faceItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Integer emojiId = (Integer) faceAdapters.get(current).getItem(arg2);
			int selection = mContent.getSelectionStart();
			if (emojiId == R.drawable.face_del_icon) {
				String text = mContent.getText().toString();
				if (selection > 0) {
					String text2 = text.substring(selection - 1);
					if (">".equals(text2)) {
						int start = text.lastIndexOf("<");
						int end = selection;
						mContent.getText().delete(start, end);
						return;
					}
					mContent.getText().delete(selection - 1, selection);
				}
			}else{
				mContent.getText().insert(selection, Html.fromHtml(EmoticonUtil.formatFaces(EmojiDate.emojiImgToNumArray.get(emojiId).toString()),EmoticonUtil.getImageGetter(ReplyDialog.this), null));
			}
		}
	};
	 
	 private void initPoint(){
			pointViews = new ArrayList<ImageView>();
			ImageView imageView;
			for (int i = 0; i < pageViews.size(); i++) {
				imageView = new ImageView(this);
				imageView.setBackgroundResource(R.drawable.d1);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
				layoutParams.leftMargin = 10;
				layoutParams.rightMargin = 10;
				layoutParams.width = 8;
				layoutParams.height = 8;
				layoutPoint.addView(imageView, layoutParams);
				if (i == 0 || i == pageViews.size() - 1) {
					imageView.setVisibility(View.GONE);
				}
				if (i == 1) {
					imageView.setBackgroundResource(R.drawable.d2);
				}
				pointViews.add(imageView);

			}
	 }
	 
		/**
		 * 填充数据
		 */
		private void initFaceData() {
			vpFace.setAdapter(new FacePagerAdapter(pageViews));
			vpFace.setCurrentItem(1);
			current = 0;
			vpFace.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					
					current = arg0 - 1;
					// 描绘分页点
					drawPoint(arg0);
					// 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
					if (arg0 == pointViews.size() - 1 || arg0 == 0) {
						if (arg0 == 0) {
							vpFace.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
							pointViews.get(1).setBackgroundResource(R.drawable.d2);
						} else {
							vpFace.setCurrentItem(arg0 - 1);// 倒数第二屏
							pointViews.get(arg0 - 1).setBackgroundResource(
									R.drawable.d2);
						}
					}
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {

				}
			});

		}
		
		/**
		 * 绘制游标背景
		 */
		public void drawPoint(int index) {
			for (int i = 1; i < pointViews.size(); i++) {
				if (index == i) {
					pointViews.get(i).setBackgroundResource(R.drawable.d2);
				} else {
					pointViews.get(i).setBackgroundResource(R.drawable.d1);
				}
			}
		}

	private String getErrorInfo(final int msg){
		String result = "";
		switch(msg){
			case -2:
				result=this.getString(R.string.msg_opt_user_pwd_error);
				break;
			case -3:
				result=this.getString(R.string.msg_opt_user_uid_is_null);
				break;
			case -4:
				result=this.getString(R.string.msg_opt_user_info_no_exist);
				break;
			case -5:
				result=this.getString(R.string.msg_login_error_five);
				break;
			case -6:
				result=this.getString(R.string.msg_opt_rep_post_id_is_null);
				break;
			case -7:
				result=this.getString(R.string.msg_opt_rep_post_no_exist);
				break;
			case -8:
				result=this.getString(R.string.msg_opt_rep_post_content_is_null);
				break;
			case -9:
				result=this.getString(R.string.msg_opt_post_ing);
				break;
			case -10:
				result=this.getString(R.string.msg_opt_post_unknow);
				break;
			case -11:
				result=this.getString(R.string.msg_opt_post_isdel);
				break;
			case -14:
				result=this.getString(R.string.network_not_connected);
				break;
		}
		return result;
	}

	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		try{
			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ReplyDialog.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
		}catch(Exception e){
			e.printStackTrace();
			Log.i("EnterActivity exception",e.toString());
		}
		return super.onTouchEvent(event);
	}
	

}
