package com.starbaby.diyBook.clientui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.FollowAdapter;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientbean.Fans;
import com.starbaby.diyBook.clientbean.FansList;
import com.starbaby.diyBook.clientcommon.StringUtils;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.clientwidget.PullToRefreshListView;

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


public class MyFollow extends BaseActivity {
	private ImageView backImg;
	private PullToRefreshListView followListView;
	
	private List<Fans>    followListData = new ArrayList<Fans>();
	private FollowAdapter followAdapter;
	private View 		  followFooter;
	private TextView      followFooterMore;
	private ProgressBar   followFooterProgress;
	private AppContext   appContext;
	
	
	private int userUid;
	private Handler 	mHandler ;
	private int 		pageIndex = 1;
	private boolean 	isFirst;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.follow);
		System.out.println("MyFollow");
		appContext = (AppContext)getApplication();
		
		initView();
		initData();
	}
	private void initView(){
		System.out.println("initView========start");
		backImg  = (ImageView)findViewById(R.id.follow_back);
		backImg.setOnClickListener(UIHelper.finish(MyFollow.this));
		
	
		
		followFooter = getLayoutInflater().inflate(R.layout.listview_footer, null);
		followFooterMore = (TextView)followFooter.findViewById(R.id.listview_foot_more);
		followFooterProgress = (ProgressBar)followFooter.findViewById(R.id.listview_foot_progress);
	
		
		System.out.println("FollowAdapter========start");
		followListView = (PullToRefreshListView)findViewById(R.id.follow_list);
		followAdapter = new FollowAdapter(this, followListData, imageLoader);
		followListView.setAdapter(followAdapter);
		followListView.addFooterView(followFooter);
		System.out.println("FollowAdapter========end");
	/*	followListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,  int position, long id) {
					if(position==0 || view==followFooter) return;
					Fans fans = null;
					fans = (Fans)parent.getAdapter().getItem(position);
					if(fans==null) return;
					
			}
			
		});*/
		followListView.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
					followListView.onScrollStateChanged(view, scrollState);
					if(followListData.size()==0) return;
					//判断是否滚动到底部
					boolean scrollEnd = false;
					try {
						if(view.getPositionForView(followFooter)==view.getLastVisiblePosition()){
							scrollEnd = true;
						} 
					} catch (Exception e) {
						scrollEnd = false;
					}
					
					int lvDataState = StringUtils.toInt(followListView.getTag());
					if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
					{
						followListView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
						followFooterMore.setText(R.string.load_ing);
						followFooterProgress.setVisibility(View.VISIBLE);
						if(isFirst){
							pageIndex=pageIndex+1;
							isFirst=false;
						}
						loadFollowData(mHandler,UIHelper.LISTVIEW_ACTION_SCROLL,pageIndex); 
					}
					
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				  followListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
			}
		}); 
		
		followListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
				public void onRefresh() {
					loadFollowData(mHandler,UIHelper.LISTVIEW_ACTION_REFRESH,1);
	            }
	        });
		
		System.out.println("initView========end");
	}
	
	private void initData(){
/*		loadingDialog.setLoadText("加载数据中...");
		loadingDialog.show();*/
		//获取userUid
		Bundle bundle = getIntent().getExtras();
		if(bundle != null && bundle.containsKey("userUid")){
			userUid = bundle.getInt("userUid");
		}
	        mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				//loadingDialog.dismiss();
				if(msg.what>=0){
					FansList fansList = (FansList)msg.obj;
					switch(msg.arg1){
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
						pageIndex = 1;
						isFirst = true;
						followListData.clear();
						followListData.addAll(fansList.getFansList());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						if(pageIndex<=msg.what){
							pageIndex++;
						}
						if(followListData.size()>0){
						    for(Fans fans1:fansList.getFansList()){
						    	boolean b = false;
						    	for(Fans fans:followListData){
									 if(fans.getId() == fans1.getId()){
										 b = true;
										 break;
									 }
								 }
						    	  if(!b) followListData.add(fans1);
							}
						}else{
							followListData.addAll(fansList.getFansList());
						}
						break;
					}
					if(msg.what<pageIndex){
						followListView.setTag(UIHelper.LISTVIEW_DATA_FULL);
						followAdapter.notifyDataSetChanged();
						followFooterMore.setText(R.string.load_full);
					}else{
						followListView.setTag(UIHelper.LISTVIEW_DATA_MORE);
						followAdapter.notifyDataSetChanged();
						followFooterMore.setText(R.string.load_more);
					}
					
				}else if(msg.what==-1){
					//有异常--显示加载出错 & 弹出错误消息
					followListView.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					//lvMyNotice_footer_more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(MyFollow.this);
				}else {
					followListView.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					followFooterMore.setText(R.string.load_error);
					UIHelper.ToastMessage(MyFollow.this,getCommentListError(msg.what));
				}
				followFooterProgress.setVisibility(View.GONE);
				if(msg.arg1!=UIHelper.LISTVIEW_ACTION_SCROLL){
					if(msg.arg1==UIHelper.LISTVIEW_ACTION_REFRESH){
						 followListView.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					}else{
						followListView.onRefreshComplete();
					}
					followListView.setSelection(0);
				}
			}
			
		};
		 System.out.println("initData===start");
		loadFollowData(mHandler,UIHelper.LISTVIEW_ACTION_INIT,pageIndex);
	}
	
	private void loadFollowData(final Handler handler,final int action,final int pageIndex){
		 System.out.println("loadFollowData===start");
		new Thread(){
			@Override
			public void run(){
				Message msg = new Message();
				try {
					FansList fansList = appContext.getAttentionList(userUid, pageIndex);
					msg.obj = fansList;
					msg.what = fansList.getMsg();
				} catch (AppException e) {
					e.printStackTrace();
					msg.obj = e;
					msg.what= -1;
				}
				msg.arg1 = action;
				handler.sendMessage(msg);
			}
		}.start();
	}
	
	public String getCommentListError(int err){
		String result="未知错误！";
		switch(err){
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
