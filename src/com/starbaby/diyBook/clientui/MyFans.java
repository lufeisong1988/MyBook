package com.starbaby.diyBook.clientui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.ListViewFansAdapter;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientbean.Fans;
import com.starbaby.diyBook.clientbean.FansList;
import com.starbaby.diyBook.clientcommon.StringUtils;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.clientwidget.PullToRefreshListView;
import com.starbaby.diyBook.main.UserInfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyFans extends BaseActivity {
	private ImageView backImg;
	private PullToRefreshListView fansListView;

	private ArrayList<Fans> fansListData = new ArrayList<Fans>();
	private ListViewFansAdapter fansAdapter;
	private View fansFooter;
	private TextView fansFooterMore;
	private ProgressBar fansFooterProgress;
	private AppContext appContext;

	private int userUid;
	private Handler mHandler;
	private int pageIndex = 1;
	private boolean isFirst;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fans);
		System.out.println("MyFans");
		appContext = (AppContext) getApplication();

		initView();
		initData();
	}

	private void initView() {
		System.out.println("initView========start");
		backImg = (ImageView) findViewById(R.id.fans_back);
		backImg.setOnClickListener(UIHelper.finish(MyFans.this));

		fansFooter = getLayoutInflater()
				.inflate(R.layout.listview_footer, null);
		fansFooterMore = (TextView) fansFooter
				.findViewById(R.id.listview_foot_more);
		fansFooterProgress = (ProgressBar) fansFooter
				.findViewById(R.id.listview_foot_progress);

		System.out.println("FansAdapter========start");
		fansListView = (PullToRefreshListView) findViewById(R.id.fans_list);
		fansAdapter = new ListViewFansAdapter(this, fansListData, imageLoader);
		fansListView.setAdapter(fansAdapter);
		fansListView.addFooterView(fansFooter);
		System.out.println("FansAdapter========end");
		
		fansListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.i("fansAvater",fansListData.get(position - 1).getAvatar());
						Log.i("fansUid",fansListData.get(position - 1).getUid() + "");
						Log.i("fansName",fansListData.get(position - 1).getUserName());
						if (position == 0 || view == fansFooter)
							return;
						Fans fans = null;
						fans = (Fans) parent.getAdapter().getItem(position);
						if (fans == null)
							return;
						
						Intent intent = new Intent(MyFans.this,UserInfo.class);
						Bundle bundle = new Bundle();
						//查看他人个人中心
						bundle.putBoolean("bMyshelf", false);
						bundle.putInt("uid", fansListData.get(position - 1).getUid());
						bundle.putString("avater", fansListData.get(position - 1).getAvatar());
						bundle.putString("name", fansListData.get(position - 1).getUserName());
						intent.putExtras(bundle);
//						startActivity(intent);
//						overridePendingTransition(R.anim.workbook_in,R.anim.none);
						
						Message msg = new Message();
						msg.what = 9;
						msg.obj = intent;
						mHandler.sendMessage(msg);
					}

				});
		 
		fansListView.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				fansListView.onScrollStateChanged(view, scrollState);
				if (fansListData.size() == 0)
					return;
				// 判断是否滚动到底部
				boolean scrollEnd = false;
				try {
					if (view.getPositionForView(fansFooter) == view
							.getLastVisiblePosition()) {
						scrollEnd = true;
					}
				} catch (Exception e) {
					scrollEnd = false;
				}

				int lvDataState = StringUtils.toInt(fansListView.getTag());
				if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
					fansListView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
					fansFooterMore.setText(R.string.load_ing);
					fansFooterProgress.setVisibility(View.VISIBLE);
					if (isFirst) {
						pageIndex = pageIndex + 1;
						isFirst = false;
					}
					loadFansData(mHandler, UIHelper.LISTVIEW_ACTION_SCROLL,
							pageIndex);
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				fansListView.onScroll(view, firstVisibleItem, visibleItemCount,
						totalItemCount);
			}
		});

		fansListView
				.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
					public void onRefresh() {
						loadFansData(mHandler,
								UIHelper.LISTVIEW_ACTION_REFRESH, 1);
					}
				});

		System.out.println("initView========end");
	}

	private void initData() {
		/*
		 * loadingDialog.setLoadText("加载数据中..."); loadingDialog.show();
		 */
		//获取 userUid
		Bundle bundle = getIntent().getExtras();
		if(bundle != null && bundle.containsKey("userUid")){
			userUid = bundle.getInt("userUid");
		}
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// loadingDialog.dismiss();
				if(msg.what == 9){
					Intent intent = (Intent) msg.obj;
					startActivity(intent);
					overridePendingTransition(R.anim.workbook_in,R.anim.none);
					return;
				}
				if (msg.what >= 0) {
					FansList fansList = (FansList) msg.obj;
					switch (msg.arg1) {
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
						pageIndex = 1;
						isFirst = true;
						fansListData.clear();
						fansListData.addAll(fansList.getFansList());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						if (pageIndex <= msg.what) {
							pageIndex++;
						}
						if (fansListData.size() > 0) {
							for (Fans fans1 : fansList.getFansList()) {
								boolean b = false;
								for (Fans fans : fansListData) {
									if (fans.getId() == fans1.getId()) {
										b = true;
										break;
									}
								}
								if (!b)
									fansListData.add(fans1);
							}
						} else {
							fansListData.addAll(fansList.getFansList());
						}
						break;
					}
					if (msg.what < pageIndex) {
						fansListView.setTag(UIHelper.LISTVIEW_DATA_FULL);
						fansAdapter.notifyDataSetChanged();
						fansFooterMore.setText(R.string.load_full);
					} else {
						fansListView.setTag(UIHelper.LISTVIEW_DATA_MORE);
						fansAdapter.notifyDataSetChanged();
						fansFooterMore.setText(R.string.load_more);
					}

				} else if (msg.what == -1) {
					// 有异常--显示加载出错 & 弹出错误消息
					fansListView.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					// lvMyNotice_footer_more.setText(R.string.load_error);
					((AppException) msg.obj).makeToast(MyFans.this);
				} else {
					fansListView.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					fansFooterMore.setText(R.string.load_error);
					UIHelper.ToastMessage(MyFans.this,
							getCommentListError(msg.what));
				}
				fansFooterProgress.setVisibility(View.GONE);
				if (msg.arg1 != UIHelper.LISTVIEW_ACTION_SCROLL) {
					if (msg.arg1 == UIHelper.LISTVIEW_ACTION_REFRESH) {
						fansListView
								.onRefreshComplete(getString(R.string.pull_to_refresh_update)
										+ new Date().toLocaleString());
					} else {
						fansListView.onRefreshComplete();
					}
					fansListView.setSelection(0);
				}
			}

		};
		System.out.println("initData===start");
		loadFansData(mHandler, UIHelper.LISTVIEW_ACTION_INIT, pageIndex);
	}

	private void loadFansData(final Handler handler, final int action,
			final int pageIndex) {
		System.out.println("loadFansData===start");
		new Thread() {
			@Override
			public void run() {
				Message msg = new Message();
				try {
					FansList fansList = appContext.getFansList(	userUid, pageIndex);
					msg.obj = fansList;
					msg.what = fansList.getMsg();
				} catch (AppException e) {
					e.printStackTrace();
					msg.obj = e;
					msg.what = -1;
				}
				msg.arg1 = action;
				handler.sendMessage(msg);
			}
		}.start();
	}

	public String getCommentListError(int err) {
		String result = "未知错误！";
		switch (err) {
		case -1:
			result = getString(R.string.app_run_code_error);
			break;
		case -14:
			result = getString(R.string.network_not_connected);
			break;
		case 0:
			result = getString(R.string.msg_opt_user_uid_is_null);
			break;
		}
		return result;
	}
}
