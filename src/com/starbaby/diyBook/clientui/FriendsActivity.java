package com.starbaby.diyBook.clientui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.FaceAdapter;
import com.starbaby.diyBook.adapter.FacePagerAdapter;
import com.starbaby.diyBook.adapter.ListViewFriendsAdapter;
import com.starbaby.diyBook.adapter.ListViewFriendsAdapter.CallShowInterface;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientbean.Comment;
import com.starbaby.diyBook.clientbean.EmojiDate;
import com.starbaby.diyBook.clientbean.FriendPost;
import com.starbaby.diyBook.clientbean.FriendPostList;
import com.starbaby.diyBook.clientcommon.EmoticonUtil;
import com.starbaby.diyBook.clientcommon.FaceConversionUtil;
import com.starbaby.diyBook.clientcommon.StringUtils;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.clientwidget.NewDataToast;
import com.starbaby.diyBook.clientwidget.PullToRefreshListView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FriendsActivity extends BaseActivity implements CallShowInterface {

	private int pageIndex = 1;
	private String pwd;
	private int uid;
	private int mTid;
	private int mPid;
	private int mPosition;
	private int topPosition, topY;
	private String mNickName;

	private String mContent;
	private AppContext appContext;
	private InputMethodManager imm;
	private Button marketBtn;
	private Button chatBtn;
	private Button diaryBtn;
	private Button momentBtn;

	private int curPostCatalog = FriendPostList.POST_TYPE_ALL;
	private int post_type = AppContext.POST_DEFAULT_TYPE;

	/* private ImageView backHome; */
	private ImageView pubPost,backHome;
	private LinearLayout mFooterLL;

	private PullToRefreshListView lvFriendsAll;
	private ListViewFriendsAdapter lvFriendsAllAdapter;
	private ArrayList<FriendPost> lvFriendsAllData = new ArrayList<FriendPost>();
	private View lvFriends_all_footer;
	private TextView lvFriends_all_footer_more;
	private ProgressBar lvFriends_all_footer_progress;
	private Handler lvFriendsAllHandler;

	private PullToRefreshListView lvFriendsChat;
	private ListViewFriendsAdapter lvFriendsChatAdapter;
	private ArrayList<FriendPost> lvFriendsChatData = new ArrayList<FriendPost>();
	private View lvFriends_chat_footer;
	private TextView lvFriends_chat_footer_more;
	private ProgressBar lvFriends_chat_footer_progress;
	private Handler lvFriendsChatHandler;

	private PullToRefreshListView lvFriendsDiary;
	private ListViewFriendsAdapter lvFriendsDiaryAdapter;
	private ArrayList<FriendPost> lvFriendsDiaryData = new ArrayList<FriendPost>();
	private View lvFriends_diary_footer;
	private TextView lvFriends_diary_footer_more;
	private ProgressBar lvFriends_diary_footer_progress;
	private Handler lvFriendsDiaryHandler;

	private PullToRefreshListView lvFriendsMoment;
	private ListViewFriendsAdapter lvFriendsMomentAdapter;
	private ArrayList<FriendPost> lvFriendsMomentData = new ArrayList<FriendPost>();
	private View lvFriends_moment_footer;
	private TextView lvFriends_moment_footer_more;
	private ProgressBar lvFriends_moment_footer_progress;
	private Handler lvFriendsMomentHandler;

	private ProgressDialog mProgress;
	private boolean isFirst = true;
	private boolean isResh = false;

	private PostReceiver postReceiver;

	private boolean softTag = true;
	// 底部评论框
	private ImageView mFootPubcomment;//提交评论按钮
	private EditText mFootEditer;
	private ImageView mFootFace;
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

	private int markTimes = 1, chatTimes = 1, diaryTimes = 1, momentTimes = 1;
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sns_friends);
		appContext = (AppContext) getApplication();

		this.initView();
		// initData();
		this.initViewPager();
		this.initPoint();
		this.initFaceData();
	}

	// 帖子发布广播接收器
	public class PostReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// System.out.println("广播接收数据");
			int what = intent.getIntExtra("MSG_WAHT", 0);
			int catalog = intent.getIntExtra("MSG_CATALOG", 0);
			if (what == 1) {
				post_type = AppContext.POST_TIME_TYPE;
				/*
				 * if(mCurSel==0){ isResh=false; }else{ mCurSel = 0;
				 * //当个人中心发布话题的时候 mScrollLayout.snapToScreen(mCurSel); }
				 */
				loadLvFriendsData(catalog, 1, headTypeSetting(curPostCatalog),
						UIHelper.LISTVIEW_ACTION_REFRESH, post_type);
				post_type = AppContext.POST_DEFAULT_TYPE;
			} else {
				UIHelper.ToastMessage(FriendsActivity.this, "帖子发布失败");
			}
		}

	}

	@Override
	public void onStart() {
		super.onStart();
		// 注册广播接收器
		postReceiver = new PostReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.starbaby.friendsbook.app.action.APP_POSTPUB");
		registerReceiver(postReceiver, filter);
	}

	private View.OnClickListener startPubPost = new View.OnClickListener() {
		public void onClick(View v) {
			UIHelper.showMessageType(FriendsActivity.this);
		}
	};

	private void initView() {
		sp = this.getSharedPreferences("diyBook", MODE_WORLD_READABLE);
//		uid = StringUtils.toInt(getIntent().getStringExtra("uid"), 0);
//		pwd = getIntent().getStringExtra("pwd");
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		marketBtn = (Button) findViewById(R.id.sns_friends_head_type_all);
		chatBtn = (Button) findViewById(R.id.sns_friends_head_type_chat);
		diaryBtn = (Button) findViewById(R.id.sns_friends_head_type_diary);
		momentBtn = (Button) findViewById(R.id.sns_friends_head_type_moment);

		marketBtn
				.setOnClickListener(headTypeBtnClick(FriendPostList.POST_TYPE_ALL));
		chatBtn.setOnClickListener(headTypeBtnClick(FriendPostList.POST_TYPE_CHAT));
		diaryBtn.setOnClickListener(headTypeBtnClick(FriendPostList.POST_TYPE_DIARY));
		momentBtn
				.setOnClickListener(headTypeBtnClick(FriendPostList.POST_TYPE_MOMENT));
		// 初始化头部菜单
		
		backHome = (ImageView) findViewById(R.id.sns_friends_back);
		backHome.setOnClickListener(UIHelper.finish(this));

		pubPost = (ImageView) findViewById(R.id.sns_freinds_pub);
		pubPost.setOnClickListener(startPubPost);

		initFrameListView();
		// initListView();

		// 初始化底部菜单
		initFooter();

	}

	private void initFrameListView() {
		this.initFriendAllListView();
		this.initFriendChatListView();
		this.initFriendDiaryListView();
		this.initFriendMomentListView();

		this.initFrameListViewData();

	}

	private void initFrameListViewData() {
		// 初始化Hanlder
		lvFriendsAllHandler = this.getLvHandler(lvFriendsAll,
				lvFriendsAllAdapter, lvFriends_all_footer_more,
				lvFriends_all_footer_progress);
		lvFriendsChatHandler = this.getLvHandler(lvFriendsChat,
				lvFriendsChatAdapter, lvFriends_chat_footer_more,
				lvFriends_chat_footer_progress);
		lvFriendsDiaryHandler = this.getLvHandler(lvFriendsDiary,
				lvFriendsDiaryAdapter, lvFriends_diary_footer_more,
				lvFriends_diary_footer_progress);
		lvFriendsMomentHandler = this.getLvHandler(lvFriendsMoment,
				lvFriendsMomentAdapter, lvFriends_moment_footer_more,
				lvFriends_moment_footer_progress);

		if (lvFriendsAllData.isEmpty()) {
			loadLvFriendsData(curPostCatalog, 1, lvFriendsAllHandler,
					UIHelper.LISTVIEW_ACTION_INIT, post_type);
		}
	}

	private void initFriendAllListView() {
		lvFriends_all_footer = getLayoutInflater().inflate(
				R.layout.listview_footer, null);
		lvFriends_all_footer_more = (TextView) lvFriends_all_footer
				.findViewById(R.id.listview_foot_more);
		lvFriends_all_footer_progress = (ProgressBar) lvFriends_all_footer
				.findViewById(R.id.listview_foot_progress);

		lvFriendsAllAdapter = new ListViewFriendsAdapter(this,
				lvFriendsAllData, R.layout.sns_timeline_item, uid, imageLoader);
		lvFriendsAll = (PullToRefreshListView) findViewById(R.id.sns_friends_all_listview);
		lvFriendsAll.setClickable(true);
		lvFriendsAll.addFooterView(lvFriends_all_footer);
		lvFriendsAll.setAdapter(lvFriendsAllAdapter);
		lvFriendsAll.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (mFooterLL.getVisibility() == View.VISIBLE) {
					lvFriendsAll.postDelayed(new Runnable() {
						@Override
						public void run() {
							lvFriendsAll.setSelectionFromTop(topPosition, topY);
						}
					}, 500);
					imm.hideSoftInputFromWindow(mFootEditer.getWindowToken(), 0);
					mFooterLL.setVisibility(View.GONE);
				}

				return false;
			}
		});

		lvFriendsAll.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvFriendsAll.onScrollStateChanged(view, scrollState);
				if (lvFriendsAllData.size() == 0)
					return;
				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(lvFriends_all_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int curLvDataState = StringUtils.toInt(lvFriendsAll.getTag());
				if (scrollEnd && curLvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lvFriendsAll.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvFriends_all_footer_more.setText(R.string.load_ing);
					lvFriends_all_footer_progress.setVisibility(View.VISIBLE);
					if (isFirst) {
						pageIndex = pageIndex + 1;
						isFirst = false;
					}
					loadLvFriendsData(curPostCatalog, pageIndex,
							lvFriendsAllHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL, post_type);
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lvFriendsAll.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});
		lvFriendsAll
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {
						loadLvFriendsData(curPostCatalog, 1,
								lvFriendsAllHandler,
								UIHelper.LISTVIEW_ACTION_REFRESH, post_type);
					}
				});
	}

	private void initFriendChatListView() {
		lvFriends_chat_footer = getLayoutInflater().inflate(
				R.layout.listview_footer, null);
		lvFriends_chat_footer_more = (TextView) lvFriends_chat_footer
				.findViewById(R.id.listview_foot_more);
		lvFriends_chat_footer_progress = (ProgressBar) lvFriends_chat_footer
				.findViewById(R.id.listview_foot_progress);

		lvFriendsChatAdapter = new ListViewFriendsAdapter(this,
				lvFriendsChatData, R.layout.sns_timeline_item, uid, imageLoader);
		lvFriendsChat = (PullToRefreshListView) findViewById(R.id.sns_friends_chat_listview);
		lvFriendsChat.setClickable(true);
		lvFriendsChat.addFooterView(lvFriends_chat_footer);
		lvFriendsChat.setAdapter(lvFriendsChatAdapter);
		lvFriendsChat.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (mFooterLL.getVisibility() == View.VISIBLE) {
					lvFriendsChat.postDelayed(new Runnable() {
						@Override
						public void run() {
							lvFriendsChat
									.setSelectionFromTop(topPosition, topY);
						}
					}, 500);
					imm.hideSoftInputFromWindow(mFootEditer.getWindowToken(), 0);
					mFooterLL.setVisibility(View.GONE);
				}

				return false;
			}
		});

		lvFriendsChat.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvFriendsChat.onScrollStateChanged(view, scrollState);
				if (lvFriendsChatData.size() == 0)
					return;
				// 判断是否滚动到底部
				boolean scrollEnd = false;

				try {
					if (view.getPositionForView(lvFriends_chat_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}
				int curLvDataState = StringUtils.toInt(lvFriendsChat.getTag());
				if (scrollEnd && curLvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lvFriendsChat.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvFriends_chat_footer_more.setText(R.string.load_ing);
					lvFriends_chat_footer_progress.setVisibility(View.VISIBLE);
					if (isFirst) {
						pageIndex = pageIndex + 1;
						isFirst = false;
					}
					loadLvFriendsData(curPostCatalog, pageIndex,
							lvFriendsChatHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL, post_type);
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lvFriendsChat.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);
			}
		});
		lvFriendsChat
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {
						loadLvFriendsData(curPostCatalog, 1,
								lvFriendsChatHandler,
								UIHelper.LISTVIEW_ACTION_REFRESH, post_type);
					}
				});
	}

	private void initFriendDiaryListView() {
		lvFriends_diary_footer = getLayoutInflater().inflate(
				R.layout.listview_footer, null);
		lvFriends_diary_footer_more = (TextView) lvFriends_diary_footer
				.findViewById(R.id.listview_foot_more);
		lvFriends_diary_footer_progress = (ProgressBar) lvFriends_diary_footer
				.findViewById(R.id.listview_foot_progress);

		lvFriendsDiaryAdapter = new ListViewFriendsAdapter(this,
				lvFriendsDiaryData, R.layout.sns_timeline_item, uid,
				imageLoader);
		lvFriendsDiary = (PullToRefreshListView) findViewById(R.id.sns_friends_diary_listview);
		lvFriendsDiary.setClickable(true);
		lvFriendsDiary.addFooterView(lvFriends_diary_footer);
		lvFriendsDiary.setAdapter(lvFriendsDiaryAdapter);
		lvFriendsDiary.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (mFooterLL.getVisibility() == View.VISIBLE) {
					lvFriendsDiary.postDelayed(new Runnable() {
						@Override
						public void run() {
							lvFriendsDiary.setSelectionFromTop(topPosition,
									topY);
						}
					}, 500);
					imm.hideSoftInputFromWindow(mFootEditer.getWindowToken(), 0);
					mFooterLL.setVisibility(View.GONE);
				}

				return false;
			}
		});

		lvFriendsDiary.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvFriendsDiary.onScrollStateChanged(view, scrollState);
				if (lvFriendsDiaryData.size() == 0)
					return;
				// 判断是否滚动到底部
				boolean scrollEnd = false;

				try {
					if (view.getPositionForView(lvFriends_diary_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int curLvDataState = StringUtils.toInt(lvFriendsDiary.getTag());
				if (scrollEnd && curLvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lvFriendsDiary.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvFriends_diary_footer_more.setText(R.string.load_ing);
					lvFriends_diary_footer_progress.setVisibility(View.VISIBLE);
					if (isFirst) {
						pageIndex = pageIndex + 1;
						isFirst = false;
					}
					loadLvFriendsData(curPostCatalog, pageIndex,
							lvFriendsDiaryHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL, post_type);
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lvFriendsDiary.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);
			}
		});
		lvFriendsDiary
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {
						loadLvFriendsData(curPostCatalog, 1,
								lvFriendsDiaryHandler,
								UIHelper.LISTVIEW_ACTION_REFRESH, post_type);
					}
				});
	}

	private void initFriendMomentListView() {
		lvFriends_moment_footer = getLayoutInflater().inflate(
				R.layout.listview_footer, null);
		lvFriends_moment_footer_more = (TextView) lvFriends_moment_footer
				.findViewById(R.id.listview_foot_more);
		lvFriends_moment_footer_progress = (ProgressBar) lvFriends_moment_footer
				.findViewById(R.id.listview_foot_progress);

		lvFriendsMomentAdapter = new ListViewFriendsAdapter(this,
				lvFriendsMomentData, R.layout.sns_timeline_item, uid,
				imageLoader);
		lvFriendsMoment = (PullToRefreshListView) findViewById(R.id.sns_friends_moment_listview);
		lvFriendsMoment.setClickable(true);
		lvFriendsMoment.addFooterView(lvFriends_moment_footer);
		lvFriendsMoment.setAdapter(lvFriendsMomentAdapter);
		lvFriendsMoment.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (mFooterLL.getVisibility() == View.VISIBLE) {
					lvFriendsMoment.postDelayed(new Runnable() {
						@Override
						public void run() {
							lvFriendsMoment.setSelectionFromTop(topPosition,
									topY);
						}
					}, 500);
					imm.hideSoftInputFromWindow(mFootEditer.getWindowToken(), 0);
					mFooterLL.setVisibility(View.GONE);
				}

				return false;
			}
		});

		lvFriendsMoment.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				lvFriendsMoment.onScrollStateChanged(view, scrollState);
				if (lvFriendsMomentData.size() == 0)
					return;
				// 判断是否滚动到底部
				boolean scrollEnd = false;

				try {
					if (view.getPositionForView(lvFriends_moment_footer) == view
							.getLastVisiblePosition())
						scrollEnd = true;
				} catch (Exception e) {
					scrollEnd = false;
				}

				int curLvDataState = StringUtils.toInt(lvFriendsMoment.getTag());
				if (scrollEnd && curLvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					lvFriendsMoment.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					lvFriends_moment_footer_more.setText(R.string.load_ing);
					lvFriends_moment_footer_progress
							.setVisibility(View.VISIBLE);
					if (isFirst) {
						pageIndex = pageIndex + 1;
						isFirst = false;
					}
					loadLvFriendsData(curPostCatalog, pageIndex,
							lvFriendsMomentHandler,
							UIHelper.LISTVIEW_ACTION_SCROLL, post_type);
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lvFriendsMoment.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);
			}
		});
		lvFriendsMoment
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {
						loadLvFriendsData(curPostCatalog, 1,
								lvFriendsAllHandler,
								UIHelper.LISTVIEW_ACTION_REFRESH, post_type);
					}
				});
	}

	private void initFooter() {
		mFooterLL = (LinearLayout) findViewById(R.id.sns_freind_footer);
		mFootFace = (ImageView) findViewById(R.id.sns_freind_footer_face);
		mFootFace.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showOrHideIMM();
			}
		});
		mFootPubcomment = (ImageView) findViewById(R.id.sns_freind_footer_pubcomment);
		mFootPubcomment.setOnClickListener(messagePubClickListener);
		mFootPubcomment.setEnabled(true); // 回复可点击

		mFootEditer = (EditText) findViewById(R.id.sns_freind_footer_editer);

		vpFace = (ViewPager) findViewById(R.id.vp_contains);
		layoutPoint = (LinearLayout) findViewById(R.id.iv_image);
		view = findViewById(R.id.ll_facechoose);
	}

	private View.OnClickListener messagePubClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			uid = sp.getInt("uid", 0);
			pwd = sp.getString("psw", "");
			if(uid == 0){
				UIHelper.showLogin(FriendsActivity.this);
				return;
			}
			mContent = FaceConversionUtil.convertToMsg(
					mFootEditer.getEditableText(), FriendsActivity.this);// mFootEditer.getText().toString();
			if (StringUtils.isEmpty(mContent)) {
				return;
			}

			mProgress = ProgressDialog.show(FriendsActivity.this, null,
					"发送中···", true, true);

			mFootPubcomment.setEnabled(false); // 设置不可点击
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					if (mProgress != null)
						mProgress.dismiss();
					mFootPubcomment.setEnabled(true); // 回复可点击
					if (msg.what == 1) {
						Comment comm = (Comment) msg.obj;
						int msgCode = comm.getMsg();
						if (msgCode == 1) {
							mFootEditer.setText("");
							mFootEditer.setHint("");
							mPid = 0;

							mFooterLL.setVisibility(View.GONE);
							imm.hideSoftInputFromWindow(
									mFootEditer.getWindowToken(), 0);

							Comment comment = new Comment();
							comment.setAuthor(appContext.getLoginName());
							comment.setMessage(mContent);
							comment.setPid(comm.getPid());
							switch (curPostCatalog) {
							case FriendPostList.POST_TYPE_ALL:
								lvFriendsAllData.get(mPosition).getComments()
										.add(comment);
								lvFriendsAllAdapter.notifyDataSetChanged();
								break;
							case FriendPostList.POST_TYPE_CHAT:
								lvFriendsChatData.get(mPosition).getComments()
										.add(comment);
								lvFriendsChatAdapter.notifyDataSetChanged();
								break;
							case FriendPostList.POST_TYPE_DIARY:
								lvFriendsDiaryData.get(mPosition).getComments()
										.add(comment);
								lvFriendsDiaryAdapter.notifyDataSetChanged();
								break;
							case FriendPostList.POST_TYPE_MOMENT:
								lvFriendsMomentData.get(mPosition)
										.getComments().add(comment);
								lvFriendsMomentAdapter.notifyDataSetChanged();
								break;
							}
							/*
							 * friendData.get(mPosition).getComments().add(comment
							 * ); friendsAdapter.notifyDataSetChanged();
							 */
						} else if (comm.getErrorCode() == -14) {
							UIHelper.ToastMessage(FriendsActivity.this,
									R.string.network_not_connected);
						}
					} else if (msg.what == -1) {
						((AppException) msg.obj)
								.makeToast(FriendsActivity.this);
					} else {
						UIHelper.ToastMessage(FriendsActivity.this,
								getErrorInfo(msg.what));
					}
				}
			};
			if (mPid > 0) {
				mContent = "回复@" + mNickName + ": " + mContent;
				replyCommentThread(handler);
			} else {
				commentThread(handler);
			}
		}
	};
	/*
	 * 回复主题
	 */
	private void commentThread(final Handler handler) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					if (appContext.isNetworkConnected()) {
						Log.i("uid",uid + "");
						Log.i("pwd",pwd + "");
						Log.i("mTid",mTid + "");
						Log.i("mContent",mContent + "");
						Comment res = appContext.pubComment(uid, pwd, mTid,
								mContent);
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
	/*
	 * 回复他人
	 */
	private void replyCommentThread(final Handler handler) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					if (appContext.isNetworkConnected()) {
						Comment comment = appContext.repComment(uid, pwd, mTid,
								mPid, mContent);
						msg.what = 1;
						msg.obj = comment;
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

	/*
	 * private void initData(){ friendHandler = new Handler(){ public void
	 * handleMessage(Message msg){
	 * //System.out.println("msg.what=="+msg.what+",msg.arg1="
	 * +msg.arg1+",msg.arg2="+msg.arg2); if(msg.what>=0){ FriendPostList list =
	 * (FriendPostList)msg.obj; switch(msg.arg1){ case
	 * UIHelper.LISTVIEW_ACTION_INIT: case UIHelper.LISTVIEW_ACTION_REFRESH:
	 * pageIndex = 1; isFirst = true; friendData.clear();
	 * friendData.addAll(list.getPostlist()); break; case
	 * UIHelper.LISTVIEW_ACTION_SCROLL: if(pageIndex<=msg.what) pageIndex++;
	 * if(friendData.size()>0){ for(FriendPost post:list.getPostlist()){ boolean
	 * b = false; for(FriendPost post2:friendData){
	 * if(post.getTid()==post2.getTid()){ b = true; break; } } if(!b)
	 * friendData.add(post); } }else { friendData.addAll(liMomentgetPostlist());
	 * } break; } if(msg.what<pageIndex){ curLvDataState =
	 * UIHelper.LISTVIEW_DATA_FULL; friendsAdapter.notifyDataSetChanged();
	 * lvFriends_footer_more.setText(R.string.load_full); } else{//
	 * if(msg.what>=pageIndex) curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
	 * friendsAdapter.notifyDataSetChanged();
	 * lvFriends_footer_more.setText(R.string.load_more); } } else
	 * if(msg.what==-1){ //有异常--显示加载出错 & 弹出错误消息
	 * lvFriends.setTag(UIHelper.LISTVIEW_DATA_MORE);
	 * lvFriends_footer_more.setText(R.string.load_error);
	 * ((AppException)msg.obj).makeToast(FriendsActivity.this); } else {
	 * UIHelper.ToastMessage(FriendsActivity.this,getErrorInfo(msg.what) ); }
	 * if(friendData.size()==0){ curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
	 * lvFriends_footer_more.setText(R.string.load_empty); }
	 * lvFriends_footer_progress.setVisibility(View.GONE); if(msg.arg1 !=
	 * UIHelper.LISTVIEW_ACTION_SCROLL){ lvFriends.setSelection(0);//返回头部
	 * lvFriends.onRefreshComplete(getString(R.string.pull_to_refresh_update) +
	 * new Date().toLocaleString()); } } };
	 * loadLvFriendsData(1,friendHandler,UIHelper.LISTVIEW_ACTION_INIT); }
	 */

	@Override
	protected void onActivityResult(final int requestCode,
			final int resultCode, final Intent data) {
		if (resultCode != RESULT_OK)
			return;
		boolean isDel = data.getBooleanExtra("isDel", false);
		final int position = data.getIntExtra("position", -1);
		if (requestCode == UIHelper.REQUEST_CODE_FOR_DEL && isDel
				&& position >= 0) {
			mProgress = ProgressDialog.show(this, "删除信息", "删除中···", true, true);
			final Handler handler = new Handler() {
				public void handleMessage(Message msg) {
					if (mProgress != null)
						mProgress.dismiss();
					if (msg.what == 1) {
						switch (curPostCatalog) {
						case FriendPostList.POST_TYPE_ALL:
							lvFriendsAllData.remove(position);
							lvFriendsAllAdapter.notifyDataSetChanged();
							break;
						case FriendPostList.POST_TYPE_CHAT:
							lvFriendsChatData.remove(position);
							lvFriendsChatAdapter.notifyDataSetChanged();
							break;
						case FriendPostList.POST_TYPE_DIARY:
							lvFriendsDiaryData.remove(position);
							lvFriendsDiaryAdapter.notifyDataSetChanged();
							break;
						case FriendPostList.POST_TYPE_MOMENT:
							lvFriendsMomentData.remove(position);
							lvFriendsMomentAdapter.notifyDataSetChanged();
							break;
						}
						/*
						 * friendData.remove(position);
						 * friendsAdapter.notifyDataSetChanged();
						 */
						UIHelper.ToastMessage(FriendsActivity.this, "删除成功");
					} else {
						UIHelper.ToastMessage(FriendsActivity.this,
								"删除失败，错误代码=" + msg.what);
					}

				}
			};
			new Thread() {
				public void run() {
					Message msg = new Message();
					int result = 0;
					try {
						result = appContext.delPost(uid,
								appContext.getLoginPwd(),
								data.getIntExtra("tid", 0));
					} catch (AppException e) {
						e.printStackTrace();
						result = -1;
					}
					msg.what = result;
					handler.sendMessage(msg);
				}
			}.start();
		}
	}

	private View.OnClickListener headTypeBtnClick(final int catalog) {
		return new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Handler handler = headTypeSetting(catalog);
				if (isResh) {
					isResh = false;
					loadLvFriendsData(curPostCatalog, 1, handler,
							UIHelper.LISTVIEW_ACTION_REFRESH, post_type);
				} else {
					loadLvFriendsData(curPostCatalog, 1, handler,
							UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG, post_type);
				}
			}
		};
	}

	private Handler headTypeSetting(int catalog) {
		Handler handler = lvFriendsAllHandler;
		if (catalog == FriendPostList.POST_TYPE_ALL) {
			handler = lvFriendsAllHandler;
			marketBtn.setEnabled(false);
			lvFriendsAll.setVisibility(View.VISIBLE);
			markTimes++;
			if (markTimes % 3 == 0) {
				isResh = true;
				markTimes = 0;
			}
		} else {
			marketBtn.setEnabled(true);
			lvFriendsAll.setVisibility(View.GONE);
		}

		if (catalog == FriendPostList.POST_TYPE_CHAT) {
			handler = lvFriendsChatHandler;
			chatBtn.setEnabled(false);
			lvFriendsChat.setVisibility(View.VISIBLE);
			if (chatTimes % 3 == 0) {
				isResh = true;
				chatTimes = 0;
			}
			chatTimes++;
		} else {
			lvFriendsChat.setVisibility(View.GONE);
			chatBtn.setEnabled(true);
		}

		if (catalog == FriendPostList.POST_TYPE_DIARY) {
			handler = lvFriendsDiaryHandler;
			diaryBtn.setEnabled(false);
			lvFriendsDiary.setVisibility(View.VISIBLE);
			if (diaryTimes % 3 == 0) {
				isResh = true;
				diaryTimes = 0;
			}
			diaryTimes++;
		} else {
			lvFriendsDiary.setVisibility(View.GONE);
			diaryBtn.setEnabled(true);
		}

		if (catalog == FriendPostList.POST_TYPE_MOMENT) {
			handler = lvFriendsMomentHandler;
			momentBtn.setEnabled(false);
			lvFriendsMoment.setVisibility(View.VISIBLE);
			if (momentTimes % 3 == 0) {
				isResh = true;
				momentTimes = 0;
			}
			momentTimes++;
		} else {
			lvFriendsMoment.setVisibility(View.GONE);
			momentBtn.setEnabled(true);
		}
		curPostCatalog = catalog;
		return handler;
	}

	private void loadLvFriendsData(final int catalog, final int pageIndex,
			final Handler handler, final int action, final int type) {
		new Thread() {
			public void run() {
				Message msg = new Message();
				boolean isRefresh = false;
				if (action == UIHelper.LISTVIEW_ACTION_REFRESH
						|| action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;

				try {
					 FriendPostList postList
					 =appContext.getFriendList(pageIndex,isRefresh,uid,catalog);
//					FriendPostList postList = appContext.getMessageList(
//							catalog, pageIndex, isRefresh, type);
					msg.what = postList.getPageSize();
					msg.obj = postList;
				} catch (AppException e) {
					e.printStackTrace();
					msg.obj = e;
					msg.what = -1;
				}
				int target = UIHelper.LISTVIEW_DATATYPE_FRIENDS_ALL;
				switch (catalog) {
				case FriendPostList.POST_TYPE_CHAT:
					target = UIHelper.LISTVIEW_DATATYPE_FRIENDS_CHAT;
					break;
				case FriendPostList.POST_TYPE_DIARY:
					target = UIHelper.LISTVIEW_DATATYPE_FRIENDS_DIARY;
					break;
				case FriendPostList.POST_TYPE_MOMENT:
					target = UIHelper.LISTVIEW_DATATYPE_FRIENDS_MOMENT;
					break;
				}
				msg.arg1 = action;// 告知handler当前action
				msg.arg2 = target;
				handler.sendMessage(msg);
			}
		}.start();
	}

	private Handler getLvHandler(final PullToRefreshListView lv,
			final BaseAdapter adapter, final TextView more,
			final ProgressBar progress) {
		return new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what >= 0) {
					handleLvData(msg.what, msg.obj, msg.arg1, msg.arg2);
					if (msg.what < pageIndex) {
						lv.setTag(UIHelper.LISTVIEW_DATA_FULL);
						adapter.notifyDataSetChanged();
						if (msg.what == 0) {
							more.setText(R.string.load_error);
						} else {
							more.setText(R.string.load_full);
						}
					} else {
						lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
						adapter.notifyDataSetChanged();
						more.setText(R.string.load_more);
					}
				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					lv.setTag(UIHelper.LISTVIEW_DATA_MORE);
					more.setText(R.string.load_error);
					((AppException) msg.obj).makeToast(FriendsActivity.this);
				} else if (msg.what == -14) {
					more.setText(R.string.load_error);
					UIHelper.ToastMessage(FriendsActivity.this,
							R.string.network_not_connected);
				}
				if (adapter.getCount() == 0) {
					lv.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					more.setText(R.string.load_empty);
				}

				progress.setVisibility(ProgressBar.GONE);
				if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
					lv.onRefreshComplete(getString(R.string.pull_to_refresh_update)
							+ new Date().toLocaleString());
					lv.setSelection(0);
				} else if (msg.arg1 == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
					lv.onRefreshComplete();
					lv.setSelection(0);
				}
			}
		};
	}

	private void handleLvData(int what, Object obj, int actionType, int objType) {
		switch (actionType) {
		case UIHelper.LISTVIEW_ACTION_INIT:
		case UIHelper.LISTVIEW_ACTION_REFRESH:
		case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
			int newData = 0;
			isFirst = true;
			pageIndex = 1;
			switch (objType) {
			case UIHelper.LISTVIEW_DATATYPE_FRIENDS_ALL:
				FriendPostList fpAll = (FriendPostList) obj;
				if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH) {
					if (lvFriendsAllData.size() > 0) {
						for (FriendPost allNew : fpAll.getPostlist()) {
							boolean b = false;
							for (FriendPost allOld : lvFriendsAllData) {
								if (allNew.getTid() == allOld.getTid()) {
									b = true;
									break;
								}
							}
							if (!b)
								newData++;
						}
					} else {
						newData = 10;
					}
				}
				lvFriendsAllData.clear();
				lvFriendsAllData.addAll(fpAll.getPostlist());
				if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH) {
					// 提示新加载数据
					if (newData > 0) {
						NewDataToast.makeText(
								this,
								getString(R.string.new_data_toast_message,
										newData), appContext.isAppSound())
								.show();
					} else {
						NewDataToast.makeText(this,
								getString(R.string.new_data_toast_none), false)
								.show();
					}
				}
				break;
			case UIHelper.LISTVIEW_DATATYPE_FRIENDS_CHAT:
				FriendPostList fpChat = (FriendPostList) obj;
				if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH) {
					if (lvFriendsChatData.size() > 0) {
						for (FriendPost chatNew : fpChat.getPostlist()) {
							boolean b = false;
							for (FriendPost chatOld : lvFriendsChatData) {
								if (chatNew.getTid() == chatOld.getTid()) {
									b = true;
									break;
								}
							}
							if (!b)
								newData++;
						}
					} else {
						newData = 10;
					}
				}
				lvFriendsChatData.clear();
				lvFriendsChatData.addAll(fpChat.getPostlist());
				if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH) {
					// 提示新加载数据
					if (newData > 0) {
						NewDataToast.makeText(
								this,
								getString(R.string.new_data_toast_message,
										newData), appContext.isAppSound())
								.show();
					} else {
						NewDataToast.makeText(this,
								getString(R.string.new_data_toast_none), false)
								.show();
					}
				}
				break;
			case UIHelper.LISTVIEW_DATATYPE_FRIENDS_DIARY:
				FriendPostList fpDiary = (FriendPostList) obj;
				if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH) {
					if (lvFriendsDiaryData.size() > 0) {
						for (FriendPost diaryNew : fpDiary.getPostlist()) {
							boolean b = false;
							for (FriendPost diaryOld : lvFriendsDiaryData) {
								if (diaryNew.getTid() == diaryOld.getTid()) {
									b = true;
									break;
								}
							}
							if (!b)
								newData++;
						}
					} else {
						newData = 10;
					}
				}
				lvFriendsDiaryData.clear();
				lvFriendsDiaryData.addAll(fpDiary.getPostlist());
				if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH) {
					// 提示新加载数据
					if (newData > 0) {
						NewDataToast.makeText(
								this,
								getString(R.string.new_data_toast_message,
										newData), appContext.isAppSound())
								.show();
					} else {
						NewDataToast.makeText(this,
								getString(R.string.new_data_toast_none), false)
								.show();
					}
				}
				break;
			case UIHelper.LISTVIEW_DATATYPE_FRIENDS_MOMENT:
				FriendPostList fpMoment = (FriendPostList) obj;
				if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH) {
					if (lvFriendsMomentData.size() > 0) {
						for (FriendPost momentNew : fpMoment.getPostlist()) {
							boolean b = false;
							for (FriendPost momentOld : lvFriendsMomentData) {
								if (momentNew.getTid() == momentOld.getTid()) {
									b = true;
									break;
								}
							}
							if (!b)
								newData++;
						}
					} else {
						newData = 10;
					}
				}
				lvFriendsMomentData.clear();
				lvFriendsMomentData.addAll(fpMoment.getPostlist());
				if (actionType == UIHelper.LISTVIEW_ACTION_REFRESH) {
					// 提示新加载数据
					if (newData > 0) {
						NewDataToast.makeText(
								this,
								getString(R.string.new_data_toast_message,
										newData), appContext.isAppSound())
								.show();
					} else {
						NewDataToast.makeText(this,
								getString(R.string.new_data_toast_none), false)
								.show();
					}
				}
				break;
			}
			break;
		case UIHelper.LISTVIEW_ACTION_SCROLL:
			switch (objType) {
			case UIHelper.LISTVIEW_DATATYPE_FRIENDS_ALL:
				FriendPostList fpAll = (FriendPostList) obj;
				if (pageIndex <= what) {
					pageIndex++;
				}
				if (lvFriendsAllData.size() > 0) {
					for (FriendPost fpNew : fpAll.getPostlist()) {
						boolean b = false;
						for (FriendPost fbOld : lvFriendsAllData) {
							if (fbOld.getTid() == fpNew.getTid()) {
								b = true;
								break;
							}
						}
						if (!b) {
							lvFriendsAllData.add(fpNew);
						}
					}
				} else {
					lvFriendsAllData.addAll(fpAll.getPostlist());
				}
				break;
			case UIHelper.LISTVIEW_DATATYPE_FRIENDS_CHAT:
				FriendPostList fpChat = (FriendPostList) obj;
				if (pageIndex <= what) {
					pageIndex++;
				}
				if (lvFriendsChatData.size() > 0) {
					for (FriendPost fpNew : fpChat.getPostlist()) {
						boolean b = false;
						for (FriendPost fbOld : lvFriendsChatData) {
							if (fbOld.getTid() == fpNew.getTid()) {
								b = true;
								break;
							}
						}
						if (!b) {
							lvFriendsChatData.add(fpNew);
						}
					}
				} else {
					lvFriendsChatData.addAll(fpChat.getPostlist());
				}
				break;
			case UIHelper.LISTVIEW_DATATYPE_FRIENDS_DIARY:
				FriendPostList fpDiary = (FriendPostList) obj;
				if (pageIndex <= what) {
					pageIndex++;
				}
				if (lvFriendsDiaryData.size() > 0) {
					for (FriendPost fpNew : fpDiary.getPostlist()) {
						boolean b = false;
						for (FriendPost fbOld : lvFriendsDiaryData) {
							if (fbOld.getTid() == fpNew.getTid()) {
								b = true;
								break;
							}
						}
						if (!b) {
							lvFriendsDiaryData.add(fpNew);
						}
					}
				} else {
					lvFriendsDiaryData.addAll(fpDiary.getPostlist());
				}
				break;
			case UIHelper.LISTVIEW_DATATYPE_FRIENDS_MOMENT:
				FriendPostList fpMoment = (FriendPostList) obj;
				if (pageIndex <= what) {
					pageIndex++;
				}
				if (lvFriendsMomentData.size() > 0) {
					for (FriendPost fpNew : fpMoment.getPostlist()) {
						boolean b = false;
						for (FriendPost fbOld : lvFriendsMomentData) {
							if (fbOld.getTid() == fpNew.getTid()) {
								b = true;
								break;
							}
						}
						if (!b) {
							lvFriendsMomentData.add(fpNew);
						}
					}
				} else {
					lvFriendsMomentData.addAll(fpMoment.getPostlist());
				}
				break;
			}
			break;
		}
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
		if (mFootFace.getTag() == null) {
			// 隐藏软键盘
			imm.hideSoftInputFromWindow(mFootEditer.getWindowToken(), 0);
			softTag = false;
			// 显示表情
			showFace();
		} else {
			// 显示软键盘
			imm.showSoftInput(mFootEditer, 0);
			softTag = true;
			// 隐藏表情
			hideFace();
		}
	}

	private void initViewPager() {
		pageViews = new ArrayList<View>();
		// 左侧加空白
		View nullView1 = new View(this);
		pageViews.add(nullView1);
		// 中间添加表情页
		faceAdapters = new ArrayList<FaceAdapter>();
		// System.out.println(emojis.size());
		int pageCount = (int) Math.ceil(EmojiDate.emojiImgArrray.size() / 20);
		for (int i = 0; i < pageCount; i++) {
			GridView view = new GridView(this);
			FaceAdapter adapter = new FaceAdapter(this, FaceConversionUtil
					.getInstace().getData(i));
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
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
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
			} else {
				mFootEditer.getText().insert(
						selection,
						Html.fromHtml(EmoticonUtil
								.formatFaces(EmojiDate.emojiImgToNumArray.get(
										emojiId).toString()), EmoticonUtil
								.getImageGetter(FriendsActivity.this), null));
			}
		}
	};

	private void initPoint() {

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

	private String getErrorInfo(final int msg) {
		String result = "";
		switch (msg) {
		case -2:
			result = this.getString(R.string.msg_opt_user_pwd_error);
			break;
		case -3:
			result = this.getString(R.string.msg_opt_user_uid_is_null);
			break;
		case -4:
			result = this.getString(R.string.msg_opt_user_info_no_exist);
			break;
		case -5:
			result = this.getString(R.string.msg_login_error_five);
			break;
		case -14:
			result = this.getString(R.string.network_not_connected);
			break;
		}
		return result;
	}

	/*
	 * @Override public void showCommentBox(boolean isShow) { this.isShow =
	 * isShow; }
	 */

	@Override
	public void showSoft(int position, int tid, int pid, String nickName) {
		PullToRefreshListView lvFriends = lvFriendsAll;
		switch (curPostCatalog) {
		case FriendPostList.POST_TYPE_CHAT:
			lvFriends = lvFriendsAll;
			break;
		case FriendPostList.POST_TYPE_DIARY:
			lvFriends = lvFriendsAll;
			break;
		case FriendPostList.POST_TYPE_MOMENT:
			lvFriends = lvFriendsAll;
			break;
		}
		setTopPosition(lvFriends);
		mPosition = position;
		mTid = tid;
		mPid = pid;
		if (StringUtils.isEmpty(nickName)) {
			mFootEditer.setHint("");
		} else {
			mNickName = nickName;
			mFootEditer.setHint("回复" + mNickName + ":");
		}
		mFootEditer.requestFocus();
		mFooterLL.setVisibility(View.VISIBLE);
		if (softTag) {
			imm.showSoftInput(mFootEditer, 0);
		} else {
			showFace();
		}
		setListViewPos(lvFriends);
	}

	private void setListViewPos(final PullToRefreshListView lvFriends) {
		// System.out.println("pos===="+mPosition);
		if (mPosition != 0)
			lvFriends.post(new Runnable() {
				@Override
				public void run() {
					lvFriends.setSelectionFromTop(mPosition + 1, -20);
				}
			});
	}

	private void setTopPosition(final PullToRefreshListView lvFriends) {
		topPosition = lvFriends.getFirstVisiblePosition();
		View v = lvFriends.getChildAt(0);
		topY = (v == null) ? 0 : v.getTop();
		// System.out.println("topPosition======"+topPosition+",topY==="+topY);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(postReceiver);
	}
}
