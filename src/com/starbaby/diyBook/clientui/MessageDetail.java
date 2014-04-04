package com.starbaby.diyBook.clientui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.FaceAdapter;
import com.starbaby.diyBook.adapter.FacePagerAdapter;
import com.starbaby.diyBook.adapter.ListViewCommentAdapter;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientbean.Comment;
import com.starbaby.diyBook.clientbean.CommentList;
import com.starbaby.diyBook.clientbean.EmojiDate;
import com.starbaby.diyBook.clientcommon.EmoticonUtil;
import com.starbaby.diyBook.clientcommon.FaceConversionUtil;
import com.starbaby.diyBook.clientcommon.StringUtils;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.clientwidget.PullToRefreshListView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.ClipboardManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

 

/**
 * 留言详情
 * @author stone(fanlei123126@163.com)
 * @version 1.0
 * @created 2013-3-29
 */
public class MessageDetail extends BaseActivity{
	
	private ImageView mBack;
	private ImageView mRefresh;
	private ImageView mCancle;
	private ProgressBar mProgressbar;
	
	private PullToRefreshListView mLvComment;
	private ListViewCommentAdapter lvCommentAdapter;
	private ArrayList<Comment> lvCommentData = new ArrayList<Comment>();
	private View lvComment_footer;
	private TextView lvComment_foot_more;
	private ProgressBar lvComment_foot_progress;
    private ProgressDialog mProgress;
    private Handler mHandler;
    
    private int curFriendId;
 
	private int page=1;
	private boolean frist = true;
	
	private ViewSwitcher mFootViewSwitcher;
	private ImageView mFootEditebox;
//	private LinearLayout mFootStore;
	private EditText mFootEditer;
	private ImageView mFootPubcomment;
	private InputMethodManager imm;
	private ImageView mFootAt;
	private ImageView mFootFace;
	private ImageView mFootVoice;
//	private View mFootView;
	
    private View lvHeader;
    private ImageView userface;
    private TextView username;
    private TextView date;
    private TextView commentCount;
    private TextView viewCount;
    private TextView content;
    private LinearLayout imgLinear;
    private Comment  topComment;
 
	private int _uid;
	private int _tid;
	private String _pwd;
 
	private AppContext ac;
	private final static int DATA_LOAD_ING = 0x001;
	private final static int DATA_LOAD_COMPLETE = 0x002;
	private final static int INPUT_START = 0x003;
	
	private boolean storeAdd = true;
	
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
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.message_detail);
        
        this.initView();        
        this.initData();    
    	this.initViewPager();
		this.initPoint();
		this.initFaceData();
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	
    }
    
    /**
     * 头部加载展示
     * @param type
     */
    private void headButtonSwitch(int type) {
    	switch (type) {
		case DATA_LOAD_ING:
			mProgressbar.setVisibility(View.VISIBLE);
			mRefresh.setVisibility(View.GONE);
			mCancle.setVisibility(View.GONE);
			break;
		case DATA_LOAD_COMPLETE:
			mProgressbar.setVisibility(View.GONE);
			mRefresh.setVisibility(View.VISIBLE);
			mCancle.setVisibility(View.GONE);
			break;
		case INPUT_START:
			mProgressbar.setVisibility(View.GONE);
			mRefresh.setVisibility(View.GONE);
			mCancle.setVisibility(View.VISIBLE);
			break;
		}
    }
    
    //初始化视图控件
    private void initView()
    {
    	 ac = (AppContext)getApplication();
		curFriendId = getIntent().getIntExtra("authorid", 0);
		_tid = getIntent().getIntExtra("tid", 0);
		
    	mBack = (ImageView)findViewById(R.id.message_detail_back);
    	mRefresh = (ImageView)findViewById(R.id.message_detail_refresh);
    	mCancle = (ImageView)findViewById(R.id.message_detail_cancle);
/*    	mHeadTitle = (TextView)findViewById(R.id.message_detail_head_title);*/
    	mProgressbar = (ProgressBar)findViewById(R.id.message_detail_head_progress);
    	
    	imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE); 
    	
    	mFootViewSwitcher = (ViewSwitcher)findViewById(R.id.message_detail_foot_viewswitcher);

    	mFootEditebox = (ImageView)findViewById(R.id.message_detail_footbar_editebox);
    	mFootEditebox.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mFootViewSwitcher.showNext();
				mFootEditer.setVisibility(View.VISIBLE);
				mFootEditer.requestFocus();
				mFootEditer.requestFocusFromTouch();
//				mFootView.setVisibility(View.VISIBLE);
				imm.showSoftInput(mFootEditer, 0);
				headButtonSwitch(INPUT_START);
			}
		});
    	
//    	mFootStore = (LinearLayout)findViewById(R.id.message_detail_footbar_store);
    	//mFootStore.setOnClickListener(storeClickListener);
    	
    	mFootEditer = (EditText)findViewById(R.id.message_detail_foot_editer);
    	mFootEditer.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showOrHideIMM();
			}
		});
    	
    	mFootEditer.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					switchView(); //改变头部和底部按钮
					return true;
				}
				return false;
			}
		});
   
    	mFootAt = (ImageView)findViewById(R.id.message_detail_foot_at);
    	mFootAt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!ac.isLogin()){
					UIHelper.showLogin(MessageDetail.this);
					return;
				}
			//	 UIHelper.showAtContact(MessageDetail.this);
			}
		});
		
    	mFootVoice = (ImageView)findViewById(R.id.message_detail_foot_voice);
    	mFootVoice.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});
    	
    	
    	mFootFace = (ImageView)findViewById(R.id.message_detail_foot_face);
    	mFootFace.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showOrHideIMM();
			}
		});
    	
    	String mInitParams = "appid=" + getString(R.string.app_id);
		//初始化转写Dialog, appid需要在http://open.voicecloud.cn获取.
    	
//		mFootView = findViewById(R.id.message_detail_layout_bottom);
//		mFootView.setVisibility(View.GONE);
		
    	vpFace = (ViewPager) findViewById(R.id.vp_contains);
		layoutPoint = (LinearLayout) findViewById(R.id.iv_image);
		view = findViewById(R.id.ll_facechoose);
    	
    	mFootPubcomment = (ImageView)findViewById(R.id.message_detail_foot_pubcomment);
    	mFootPubcomment.setOnClickListener(messagePubClickListener);
    	mFootPubcomment.setEnabled(true); //回复可点击
    	//编辑器添加文本监听
    //	mFootEditer.addTextChangedListener(UIHelper.getTextWatcher(this, tempMessageKey));
    	
    	//显示临时编辑内容
    	//UIHelper.showTempEditContent(this, mFootEditer, tempMessageKey);
    	
    	
    	//mHeadTitle.setText(getString(R.string.message_detail_head_title,curFriendName));
    	mBack.setOnClickListener(UIHelper.finish(this));
    	mRefresh.setOnClickListener(refreshClickListener);
    	mCancle.setOnClickListener(cancleClickListener);
    	
    	lvHeader = View.inflate(this, R.layout.mamabweibo_list_item_nopic, null);
    	userface = (ImageView)lvHeader.findViewById(R.id.message_detail_userface);
    	username = (TextView)lvHeader.findViewById(R.id.message_detail_username);
    	
    	date = (TextView)lvHeader.findViewById(R.id.message_datail_date);
    	commentCount = (TextView)lvHeader.findViewById(R.id.message_detail_message_count);
    	viewCount = (TextView)lvHeader.findViewById(R.id.message_detail_view_count);
    	content = (TextView)lvHeader.findViewById(R.id.message_detail_content);
 
    	imgLinear  = (LinearLayout)lvHeader.findViewById(R.id.message_detail_images_keeper_li);
    	
    	lvComment_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
    	lvComment_foot_more = (TextView)lvComment_footer.findViewById(R.id.listview_foot_more);
        lvComment_foot_progress = (ProgressBar)lvComment_footer.findViewById(R.id.listview_foot_progress);

    	lvCommentAdapter = new ListViewCommentAdapter(this, lvCommentData, R.layout.mamabweibo_list_item,imageLoader); 
    	mLvComment = (PullToRefreshListView)findViewById(R.id.message_list_listview);
    	mLvComment.addHeaderView(lvHeader);//把动弹详情放进listview头部
        mLvComment.addFooterView(lvComment_footer);//添加底部视图  必须在setAdapter前
        mLvComment.setAdapter(lvCommentAdapter); 
        mLvComment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//System.out.println("position=="+position+";view==lvComment_foote"+(view==lvComment_footer));
				if(position==0||view==lvComment_footer|| view == lvHeader) return;

				Comment com = null;
				//判断是否是TextVIew
				if(view instanceof TextView){
        			com = (Comment)view.getTag();
        		}else{
            		ImageView img = (ImageView)view.findViewById(R.id.message_detail_listitem_userface);
            		com = (Comment)img.getTag();
        		} 
        		if(com == null){
        			
        			return; 
        		}
        		//跳转--回复评论界面
//        		UIHelper.showCommentReply(MessageDetail.this, _tid, com.getPid(), com.getAuthor(), com.getMessage());
        		dismissView();
        		Intent intent = new Intent(MessageDetail.this,ReplyDialog.class);
        		Bundle bundle = new Bundle();
        		bundle.putInt("tid",  _tid);
        		bundle.putInt("poistId", com.getPid());
        		bundle.putString("author", com.getAuthor());
        		Log.i("_tid",_tid + "");
        		Log.i("com.getPid()",com.getPid() + "");
        		Log.i("com.getAuthor()",com.getAuthor() + "");
        		intent.putExtras(bundle);
        		startActivity(intent);
			}
		});
        
        mLvComment.setOnScrollListener(new AbsListView.OnScrollListener() {
        	public void onScrollStateChanged(AbsListView view, int scrollState) {
				mLvComment.onScrollStateChanged(view, scrollState);
				//数据为空--不用继续下面代码了
				if(lvCommentData.size() == 0) return;
				
				//判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if(view.getPositionForView(lvComment_footer) == view.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				int curLvDataState = StringUtils.toInt(mLvComment.getTag());
				if(scrollEnd && curLvDataState==UIHelper.LISTVIEW_DATA_MORE) 
				{
					mLvComment.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvComment_foot_more.setText(R.string.load_ing);
					lvComment_foot_progress.setVisibility(View.VISIBLE);
					//当前pageIndex
					//int pageIndex = lvSumData/20;
					if(frist){
						page=page+1;
						frist=false;
					}
					loadLvCommentData(_tid, page, mHandler, UIHelper.LISTVIEW_ACTION_SCROLL);
				}
			}
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				  mLvComment.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		});
        mLvComment.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadLvCommentData(_tid, 1, mHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
         }
     });
    }
    
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
    	if(resultCode !=RESULT_OK) return;
    	if(data==null) return;
    	if(requestCode == UIHelper.REQUEST_CODE_FOR_REPLY){
    		loadLvCommentData(data.getIntExtra("reply_tid", 0),1,mHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
    	}else if(requestCode == UIHelper.REQUEST_CODE_FOR_DIALOGIN){
    	 	if(data.getBooleanExtra("LOGIN", false)){
		    	if(_uid==0)
		    	_uid =((AppContext)getApplication()).getLoginUid();
		    	if(StringUtils.isEmpty(_pwd))
				_pwd =((AppContext)getApplication()).getLoginPwd();
		    	replyPost();
	    	}
    	} else if(requestCode==UIHelper.REQUEST_CODE_FOR_ATME){
    		String atme = "@"+data.getStringExtra("name")+" ";
    		mFootEditer.getText().insert(mFootEditer.getSelectionStart(), atme);
    	}
    }
    
    private void initLvHeader(CommentList list){
    	topComment = (Comment)list.getCommentMap().get("tcomment");
    	
    	 if(topComment==null){ 
    		 return;
    	 }
		//  UIHelper.showUserFace(userface,URLs.FCAE_URL_SMALL+comment.getAuthorid());
    	 userface.setTag(topComment);
    	// UIHelper.showUserFace(userface,topComment.getAvatarurl());
    	 imageLoader.displayImage(topComment.getAvatarurl(), userface);
    	// userface.setOnClickListener(faceClickListener);
		 username.setText(topComment.getAuthor());
		  date.setText(topComment.getDateline());
		  commentCount.setText(String.valueOf(topComment.getReplies()));
		  viewCount.setText(String.valueOf(topComment.getViews()));
	      content.setText(FaceConversionUtil.convetToHtml(topComment.getMessage(),MessageDetail.this)); //topComment.getMessage()
	      content.setOnLongClickListener(copyLoingClick);
	      if(topComment.getPicCount()>0){
	    	  	int imgLength = topComment.getPicCount();
	    	  	//List<Picture> 	listItems = topComment.getPics();
	    	    final ArrayList<String>   smallImages = topComment.getSmallImg();
	    	    final ArrayList<String>   bigImages = topComment.getBigImg();
	    	    int k=0,m=0;
	    	    for(int i=0;i<imgLength;i++){
		    		if(i%3==0) 
					  { 
		    			LayoutInflater inflater =LayoutInflater.from(MessageDetail.this);
					    LinearLayout view = (LinearLayout)inflater.inflate(R.layout.message_detail_imagesline, null);
					    imgLinear.addView(view);
						k=k+1;	
						m=0;
					  }
					//System.out.println(listItemView.imgLL.getChildAt(k)+",k-===="+k);
					ImageView imgView = (ImageView)((LinearLayout)imgLinear.getChildAt(k-1)).getChildAt(m++);
					//ImageView imgView = (ImageView)convertView.findViewById(imgArray[i]);
					imageLoader.displayImage(smallImages.get(i), imgView);
					//UIHelper.showLoadImage(imgView,smallImages.get(i),null);
					imgView.setVisibility(View.VISIBLE);
					final int index = i;
					imgView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							UIHelper.showImageViewPager(MessageDetail.this, smallImages,bigImages,index);	 
						}
					});
	    	    }
	      }
    }
	//用户个人中心
/*	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Comment comment = (Comment)v.getTag();
			UIHelper.showPersonalCenter(v.getContext(),comment.getAuthorid());
		}
	};*/
	
	private View.OnLongClickListener copyLoingClick = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			CharSequence[] items = {
					 "复制"
				};
				AlertDialog imageDialog = new AlertDialog.Builder(v.getContext()).setTitle("提示").setIcon(android.R.drawable.btn_star).setItems(items,
					new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int item)
						{
							//复制操作
							if( item == 0 )
							{
													 
								ClipboardManager clipboard = (ClipboardManager)getSystemService(MessageDetail.CLIPBOARD_SERVICE);
								clipboard.setText(content.getText());
							//	ClipData clip = ClipData.newPlainText("simple text",content.getText());
								//clipboard.setPrimaryClip(clip);
							}
						}}).create();
				
				 imageDialog.show();
			return true;
		}
	};
 
    //初始化控件数据
	private void initData()
	{			
    	mHandler = new Handler()
		{
			public void handleMessage(Message msg) {
				
				headButtonSwitch(DATA_LOAD_COMPLETE);
				
				if(msg.what >= 0){						
					CommentList list = (CommentList)msg.obj;
					//Notice notice = list.getNotice();
					//处理listview数据
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
						//lvSumData = msg.what;
						page=1;
						lvCommentData.clear();//先清除原有数据
						//System.out.println("getCommentList==="+list.getCommentList().size());
						initLvHeader(list);
						lvCommentData.addAll(list.getCommentList());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						//lvSumData += msg.what;
						if(page<=msg.what)
							page++;
						
						if(lvCommentData.size() > 0){
							for(Comment com1 : list.getCommentList()){
								boolean b = false;
								for(Comment com2 : lvCommentData){
									if(com1.getPid() == com2.getPid() && com1.getAuthorid() == com2.getAuthorid()){
										b = true;
										break;
									}
								}
								if(!b) lvCommentData.add(com1);
							}
						}else{
							lvCommentData.addAll(list.getCommentList());
						}
						break;
					}	
					
				    if(msg.what < page){
				    	mLvComment.setTag(UIHelper.LISTVIEW_DATA_FULL);
						lvCommentAdapter.notifyDataSetChanged();
						lvComment_foot_more.setText(R.string.load_full);
					}else if(msg.what>=page)  {					
						mLvComment.setTag(UIHelper.LISTVIEW_DATA_MORE);
						lvCommentAdapter.notifyDataSetChanged();
						lvComment_foot_more.setText(R.string.load_more);
					}
				}  else if(msg.what == -3){
					//有异常--也显示更多 & 弹出错误消息
					mLvComment.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvComment_foot_more.setText(R.string.load_error);
				} else {
					mLvComment.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					lvComment_foot_more.setText(R.string.load_full);
					UIHelper.ToastMessage(MessageDetail.this,getCommentListError(msg.what));
				}
				if(lvCommentData.size()==0 && msg.what >= 0){
					mLvComment.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					lvComment_foot_more.setText(R.string.reply_load_empty);
				}
				lvComment_foot_progress.setVisibility(View.GONE);
				if(msg.arg1 != UIHelper.LISTVIEW_ACTION_SCROLL){
					mLvComment.setSelection(0);//返回头部
					mLvComment.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
				}
			}
		};
		this.loadLvCommentData(_tid,1,mHandler,UIHelper.LISTVIEW_ACTION_INIT);
    }
	
	@Override
	protected void onResume(){
		super.onResume();
		_uid = ac.getLoginUid();
		_pwd = ac.getLoginPwd();
		
	}
    /**
     * 线程加载评论数据
     * @param id 当前文章id
     * @param catalog 分类
     * @param pageIndex 当前页数
     * @param handler 处理器
     * @param action 动作标识
     */
	private void loadLvCommentData(final int id,final int pageIndex,final Handler handler,final int action){  
		
		this.headButtonSwitch(DATA_LOAD_ING);
		mLvComment.setVisibility(ListView.VISIBLE);
		/*AppContext.executor.execute(*/new Thread(){
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					CommentList commentlist = ((AppContext)getApplication()).getCommentList(id, pageIndex, isRefresh);	
					if(commentlist.getMsg()==1){
						msg.what = commentlist.getPageSize();
					} else {
						msg.what = commentlist.getMsg();
					}
					msg.obj = commentlist;
	            } catch (AppException e) {
	            	e.printStackTrace();
	            	msg.what = -3;
	            	msg.obj = e;
	            }
				msg.arg1 = action;//告知handler当前action
                handler.sendMessage(msg);
			}
		}.start();
	} 

/*	private View.OnClickListener storeClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(!ac.isLogin()){
				Intent intent = new Intent(MessageDetail.this,Login.class);
				startActivity(intent);
				return;
			}
			if(ac.getLoginUid()==curFriendId){
				UIHelper.ToastMessage(MessageDetail.this, "不能收藏自己的帖子！");
				return;
			}
			if(!storeAdd) {
				UIHelper.ToastMessage(MessageDetail.this, "已经收藏成功！");
				return;
			}
			final Handler handler = new Handler(){
				public void handleMessage(Message msg){
					if(msg.what==1){
						UIHelper.ToastMessage(MessageDetail.this, "添加收藏成功！");
						storeAdd = false;
					}else if(msg.what==-1){
						((AppException)msg.obj).makeToast(MessageDetail.this);
					} else {
					   UIHelper.ToastMessage(MessageDetail.this,getErrorInfo(msg.what) );
					}
				}
			};
			
			new Thread(){
				@Override
				public void run(){
					Message message = new Message();
					try{
						if(ac.isNetworkConnected()){
							Store store = ac.addStore(_uid, _pwd, _tid);
						    message.what = store.getMsg();
						} else {
						   message.what=-14;
						}
					}catch (AppException e) {
		            	e.printStackTrace();
		            	message.what = -1;
		            	message.obj = e;
		            }
					handler.sendMessage(message);
				}
			}.start();
		}
	};
	*/
	private View.OnClickListener refreshClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			loadLvCommentData(_tid,1,mHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
		}
	};
	private View.OnClickListener cancleClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switchView();
		}
	};
	
	private View.OnClickListener messagePubClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			if(!ac.isLogin()){
				UIHelper.showLogin(MessageDetail.this);
				return;
			}
			if(_uid==0 && curFriendId==0) return;

			replyPost();
		}
	};
	
	private void switchView(){
		if(mFootViewSwitcher.getDisplayedChild()==1){
			mFootViewSwitcher.setDisplayedChild(0);
			mFootEditer.clearFocus();
			imm.hideSoftInputFromWindow(mFootEditer.getWindowToken(), 0);
			mFootEditer.setVisibility(View.GONE);
//			mFootView.setVisibility(View.GONE);
			headButtonSwitch(DATA_LOAD_COMPLETE);
			view.setVisibility(View.GONE);
		}
	}
	
	public void replyPost(){
		 final String _content = FaceConversionUtil.convertToMsg(mFootEditer.getEditableText(), MessageDetail.this);
		 //System.out.println("_content============"+_content);
		//_content = FaceConversionUtil.convertToMsg(mFootEditer.getEditableText(), MessageDetail.this);//mFootEditer.getText().toString();
		if(StringUtils.isEmpty(_content)){
			UIHelper.ToastMessage(MessageDetail.this, "请输入留言内容");
			return;
		}
		
		mProgress = ProgressDialog.show(MessageDetail.this, null, "发送中···",true,false); 
		
		mFootPubcomment.setEnabled(false); //设置不可点击
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) {
				if(mProgress!=null)mProgress.dismiss();
				mFootPubcomment.setEnabled(true); //回复可点击
				if(msg.what == 1){
					Comment comm = (Comment)msg.obj;
					int msgCode = comm.getMsg();
						if(msgCode==1){
							//恢复初始底部栏
						//	mFootViewSwitcher.setDisplayedChild(0);
							mFootEditer.setText("");
				        	//清除之前保存的编辑内容
							//ac.removeProperty(tempMessageKey);
							switchView();
							loadLvCommentData(_tid,1,mHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
				     	}else if(msg.what==-14){
				     		UIHelper.ToastMessage(MessageDetail.this, R.string.network_not_connected);
				     	}else{
				     	/*	boolean clean = false; //是否清除登陆信息
							if(msgCode==-3 ||msgCode==-2 || msgCode==-4 ||msgCode==-5){
								clean=true;
								((AppContext)getApplication()).Logout(); //注销登录
							}*/
							UIHelper.ToastMessage(MessageDetail.this,getErrorInfo(msgCode)); //弹出错误消息对话框
				     	}
				}                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     
				else {
					((AppException)msg.obj).makeToast(MessageDetail.this);
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg =new Message();
				try {
					if(ac.isNetworkConnected()){
						Comment res = ac.pubComment(_uid,_pwd, _tid, _content);
						msg.what = 1;
						msg.obj = res;
					} else {
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
	
	private String getCommentListError(final int msg){
		String result="";
		switch(msg){
		case -1:
			result = this.getString(R.string.msg_opt_rep_post_id_is_null);
			break;
		case -2:
			result = this.getString(R.string.msg_opt_rep_post_no_exist);
			break;
		case -14:
			result = this.getString(R.string.network_not_connected);
			break;
		}
		return result;
	}
	
	
	 private void showFace() {
			mFootFace.setImageResource(R.drawable.widget_bar_keyboard);
			mFootFace.setTag(1);
			view.setVisibility(View.VISIBLE);
	    }
	 
	 private void hideFace() {
	    	mFootFace.setImageResource(R.drawable.widget_bar_face);
	    	mFootFace.setTag(null);
	    	view.setVisibility(View.GONE);
	    }
	 
	 private void showOrHideIMM() {
	    	if(mFootFace.getTag() == null){
				//隐藏软键盘
				imm.hideSoftInputFromWindow(mFootEditer.getWindowToken(), 0);
				//显示表情
				showFace();				
			}else{
				//显示软键盘
				imm.showSoftInput(mFootEditer, 0);
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
			int selection = mFootEditer.getSelectionStart();
			if (emojiId == R.drawable.face_del_icon) {
				String text = mFootEditer.getText().toString();
				if (selection > 0) {
					String text2 = text.substring(selection - 1);
					if (">".equals(text2)) {
						int start = text.lastIndexOf("<");
						int end = selection;
						mFootEditer.getText().delete(start, end);
						return;
					}
					mFootEditer.getText().delete(selection - 1, selection);
				}
			}else{
				mFootEditer.getText().insert(selection, Html.fromHtml(EmoticonUtil.formatFaces(EmojiDate.emojiImgToNumArray.get(emojiId).toString()),EmoticonUtil.getImageGetter(MessageDetail.this), null));
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
		dismissView();
		return super.onTouchEvent(event);
	}
	void dismissView(){
		try{
			mFootViewSwitcher.setDisplayedChild(0);
			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MessageDetail.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
