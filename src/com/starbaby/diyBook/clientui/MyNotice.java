package com.starbaby.diyBook.clientui;


import java.util.ArrayList;
import java.util.Date;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.ListViewNoticeAdapter;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientbean.Notice;
import com.starbaby.diyBook.clientbean.NoticeList;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.clientwidget.LoadingDialog;
import com.starbaby.diyBook.clientwidget.PullToRefreshListView;
import com.starbaby.diyBook.main.UserInfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;



public class MyNotice extends BaseActivity {
	private int pageIndex = 1;
	private int uid;
	private String pwd;
	
	private AppContext ac;
	private ListViewNoticeAdapter myNoticeAdapter;
	private ImageView backHome;
	private View lvMyNotice_footer;
	private TextView lvMyNotice_footer_more;
	private ProgressBar lvMyNotice_footer_progress;
	private Handler myNoticeHandler;
	private int curLvDataState;
	
	private ImageButton notice_catalog_martket;
	private ImageButton notice_catalog_at;
	private ImageButton notice_catalog_reply;
	
	private PullToRefreshListView lvMyNotice;
	private ArrayList<Notice> myNoticeData = new ArrayList<Notice>();
	private LoadingDialog loading;
	
	private int curCatalog = NoticeList.CATALOG_ALL;
	private boolean isFirst = true;
	private View foot_view;
	private TextView foot_tv;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				Intent intent = (Intent) msg.obj;
				startActivity(intent);
				overridePendingTransition(R.anim.workbook_in,R.anim.none);
				break;
			}
			super.handleMessage(msg);
		}
		
	};
 
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_reply);
		initView();
		initData();
	}
	private void initView(){
		foot_view = LayoutInflater.from(this).inflate(R.layout.list_foot, null);
		foot_tv = (TextView) foot_view.findViewById(R.id.list_foot_tv);
		
		ac = (AppContext)getApplication();
		uid = ac.getLoginUid();
		pwd = ac.getLoginPwd();
		
		backHome = (ImageView)findViewById(R.id.my_reply_back);
		backHome.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(3);
				MyNotice.this.finish();
				overridePendingTransition(R.anim.none,R.anim.workbook_out);
			}
		});
		
		notice_catalog_martket = (ImageButton)findViewById(R.id.notice_market_ib);
		notice_catalog_at  = (ImageButton)findViewById(R.id.notice_at_ib);
		notice_catalog_reply = (ImageButton)findViewById(R.id.notice_reply_ib);
		
		notice_catalog_martket.setOnClickListener(this.catalogIbClick(notice_catalog_martket, NoticeList.CATALOG_ALL));
		notice_catalog_at.setOnClickListener(this.catalogIbClick(notice_catalog_at, NoticeList.CATALOG_AT));
		notice_catalog_reply.setOnClickListener(this.catalogIbClick(notice_catalog_reply, NoticeList.CATALOG_COMMENT));
		
		notice_catalog_martket.setEnabled(false);	
		
		lvMyNotice_footer = getLayoutInflater().inflate(R.layout.listview_footer, null);
		lvMyNotice_footer_more = (TextView)lvMyNotice_footer.findViewById(R.id.listview_foot_more);
		lvMyNotice_footer_progress = (ProgressBar)lvMyNotice_footer.findViewById(R.id.listview_foot_progress);
		
		myNoticeAdapter = new ListViewNoticeAdapter(this, myNoticeData, R.layout.active_listitem,imageLoader);
		lvMyNotice = (PullToRefreshListView)findViewById(R.id.my_reply_listview);
		lvMyNotice.addFooterView(lvMyNotice_footer);
		lvMyNotice.setAdapter(myNoticeAdapter);
		lvMyNotice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//点击头部，底部无效
				if( view==lvMyNotice_footer) return;
				
				Notice notice = null;
				//判断是否是TextView
				if(view instanceof TextView){
					notice = (Notice)view.getTag();
				} else {
					TextView tv = (TextView)view.findViewById(R.id.active_listitem_username);
					notice = (Notice)tv.getTag();
				}
				if(notice == null) return;  
        		
        		//跳转
				if(notice.getTypeId()<4 || notice.getTypeId()==6){
					Intent intent  = new Intent(MyNotice.this,MessageDetail.class);
					intent.putExtra("tid", notice.getRefid());//点击的用户uid
					intent.putExtra("authorid", ac.getLoginUid());//自己的uid
					startActivity(intent);
					overridePendingTransition(R.anim.workbook_in,R.anim.none);
//					UIHelper.showPostDetail(view.getContext(), notice.getRefid(),ac.getLoginUid());
				}else {
					//转到他人个人
					Intent intent = new Intent(MyNotice.this,UserInfo.class);
					Bundle bundle = new Bundle();
							bundle.putBoolean("bMyshelf", false);
							bundle.putInt("uid", notice.getSendUid());
							Log.i("userUid",notice.getSendUid() + "");
							bundle.putString("avater", notice.getAvatar());
							bundle.putString("name", notice.getSendUname());
							intent.putExtras(bundle);
							Message msg = new Message();
							msg.what = 1;
							msg.obj = intent;
							mHandler.sendMessage(msg);
//					UIHelper.showPersonalCenter(view.getContext(), notice.getSendUid());
				}
			}
			 
		});
		
		lvMyNotice.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//点击头部、底部栏无效
        		if(  view == lvMyNotice_footer) return false;
        		
        		if(!ac.isLogin()) UIHelper.showLogin(MyNotice.this);
        		
        		//final Notice Notice  = (Notice)parent.getAdapter().getItem(position);
        		TextView username = (TextView)view.findViewById(R.id.active_listitem_username);
				
				final Notice notice = (Notice)username.getTag();
				
        		if(notice == null) return false;  
        
        	    final Handler handler = new Handler(){
        				public void handleMessage(Message msg){
        					
        					if(loading != null)	loading.dismiss();
        					
        					if(msg.what==1){
        						myNoticeData.remove(notice);
        						myNoticeAdapter.notifyDataSetChanged();
        					}else {
        						UIHelper.ToastMessage(MyNotice.this,getErrorInfo(msg.what));
        					}
        				}
        			};
        			
        			final Thread thread = new Thread(){
        				public void run(){
        					Message msg = new Message();
        					
							try {
								int result = ac.delNotice(uid,pwd,notice.getId());
								msg.what = result;
							} catch (AppException e) {
								e.printStackTrace();
								msg.what = -1;
							}
							 handler.sendMessage(msg);
        				}
        			};
        			UIHelper.showDelOptionDialog(MyNotice.this,"消息通知",loading,thread);
        		
				return true;
			}
			
		});
		
		
		lvMyNotice.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
					lvMyNotice.onScrollStateChanged(view,scrollState);
					if(myNoticeData.size()==0) return;
					//判断是否滚动到底部
					boolean scrollEnd = false;
					
					try{
						if(view.getPositionForView(lvMyNotice_footer) == view.getLastVisiblePosition())
							scrollEnd = true;
					}catch(Exception e){
						scrollEnd = false;
					}
/*					int curLvDataState = StringUtils.toInt(mLvComment.getTag());*/
					if(scrollEnd && curLvDataState==UIHelper.LISTVIEW_DATA_MORE)
					{
						lvMyNotice.setTag(UIHelper.LISTVIEW_DATA_LOADING);
						lvMyNotice_footer_more.setText(R.string.load_ing);
						lvMyNotice_footer_progress.setVisibility(View.VISIBLE);
						if(isFirst){
							pageIndex=pageIndex+1;
							isFirst=false;
						}
						loadLvMyNoticeData(curCatalog,pageIndex,myNoticeHandler,UIHelper.LISTVIEW_ACTION_SCROLL);
					}
					
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		}); 
		lvMyNotice.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadLvMyNoticeData(curCatalog,pageIndex,myNoticeHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
	}
	
	private View.OnClickListener catalogIbClick(final ImageButton ib,final int catalog){
		return new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(ib==notice_catalog_martket)
					notice_catalog_martket.setEnabled(false);
				else 
					notice_catalog_martket.setEnabled(true);
				if(ib==notice_catalog_at)
					notice_catalog_at.setEnabled(false);
				else 
					notice_catalog_at.setEnabled(true);
				if(ib==notice_catalog_reply)
					notice_catalog_reply.setEnabled(false);
				else
					notice_catalog_reply.setEnabled(true);
				
				curCatalog = catalog;
				loadLvMyNoticeData(curCatalog,1,myNoticeHandler,UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
			}
		};
	}
	
	private void initData(){
		myNoticeHandler = new Handler(){
			public void handleMessage(Message msg){
				System.out.println("msg.what=="+msg.what+",msg.arg1="+msg.arg1+",msg.arg2="+msg.arg2);
				if(msg.what>=0){ 
					NoticeList list = (NoticeList)msg.obj;
					// System.out.println(",list.size"+list.getNoticList().size());
					switch(msg.arg1){
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
					case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
						pageIndex = 1;
						isFirst = true;
						myNoticeData.clear();
						myNoticeData.addAll(list.getNoticList());
						SharedPreferences sp = MyNotice.this.getSharedPreferences("diyBook", MODE_WORLD_READABLE);
						SharedPreferences.Editor editor = sp.edit();
						editor.putInt("msgnum", 0);
						editor.commit();
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						if(pageIndex<=msg.what)
							pageIndex++;
						if(myNoticeData.size()>0){
							for(Notice Notice:list.getNoticList()){
								boolean b = false;
								for(Notice Notice2:myNoticeData){
									if(Notice.getId()==Notice2.getId()){
										b = true;
										break;
									}
								}
								if(!b) myNoticeData.add(Notice);
							}
						}else {
							myNoticeData.addAll(list.getNoticList());
						}
						break;
					}
					if(msg.what<pageIndex){
						curLvDataState = UIHelper.LISTVIEW_DATA_FULL;
						myNoticeAdapter.notifyDataSetChanged();
						lvMyNotice_footer_more.setText(R.string.load_full);
						
						
					} else if(msg.what>=pageIndex){
						curLvDataState = UIHelper.LISTVIEW_DATA_MORE;
						myNoticeAdapter.notifyDataSetChanged();
						lvMyNotice_footer_more.setText(R.string.load_more);
					}
					if(myNoticeData.size()==0){
//						消息数为空
						curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
						lvMyNotice_footer_more.setText("");
						foot_tv.setText("亲，当前没有任何消息哦！");
						if(lvMyNotice.getChildCount() > 0){
							lvMyNotice.removeHeaderView(foot_view);
						}
						lvMyNotice.addHeaderView(foot_view);
						
					}else{
						if(lvMyNotice.getChildCount() > 0){
							lvMyNotice.removeHeaderView(foot_view);
						}
					}
				} else if(msg.what==-1){
					//连接网络失败
					//有异常--显示加载出错 & 弹出错误消息
					curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
//					lvMyNotice_footer_more.setText(R.string.load_error);
//					((AppException)msg.obj).makeToast(MyNotice.this);
					lvMyNotice_footer_more.setText("");
					foot_tv.setText("亲，当前网络出错！");
					if(lvMyNotice.getChildCount() > 0){
						lvMyNotice.removeHeaderView(foot_view);
					}
					
					lvMyNotice.addHeaderView(foot_view);
					
				} else if(msg.what==-14){
					//连接网络失败
//					UIHelper.ToastMessage(MyNotice.this, R.string.network_not_connected);
					lvMyNotice_footer_more.setText("");
					foot_tv.setText("亲，当前网络出错！");
					if(lvMyNotice.getChildCount() > 0){
						lvMyNotice.removeHeaderView(foot_view);
					}
					lvMyNotice.addHeaderView(foot_view);
				}
//				if(myNoticeData.size()==0){
////					消息数为空
//					curLvDataState = UIHelper.LISTVIEW_DATA_EMPTY;
////					lvMyNotice_footer_more.setText(R.string.load_empty);
//					lvMyNotice_footer_more.setText("");
//					foot_tv.setText("亲，当前没有任何消息哦！");
//					if(lvMyNotice.getChildCount() > 0){
//						lvMyNotice.removeHeaderView(foot_view);
//					}
//					lvMyNotice.addHeaderView(foot_view);
//					
//				}
				lvMyNotice_footer_progress.setVisibility(View.GONE);
				if(msg.arg1 != UIHelper.LISTVIEW_ACTION_SCROLL){
					if(msg.arg1==UIHelper.LISTVIEW_ACTION_REFRESH){
						 lvMyNotice.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					}else{
						lvMyNotice.onRefreshComplete();
					}
					lvMyNotice.setSelection(0);//返回头部
				}
			}
		};
		this.loadLvMyNoticeData(curCatalog,1,myNoticeHandler,UIHelper.LISTVIEW_ACTION_INIT);
	}
	
	
	private void loadLvMyNoticeData(final int catalog,final int pageIndex,final Handler handler,final int action){
		
		new Thread(){
			public void run(){
				Message msg = new Message();
				try { 
					if(ac.isNetworkConnected()){
						 NoticeList noticeList =ac.getNoticeList(catalog,uid,pwd,pageIndex);
						 msg.what = noticeList.getPageSize();
						 msg.obj = noticeList;
					} else{
						
						 msg.what=-14;
					}
				} catch (AppException e) {
					e.printStackTrace();
					msg.obj = e;
					msg.what = -1;
				}
				msg.arg1 = action;//告知handler当前action
				Log.i("msg",msg.what + "");
				handler.sendMessage(msg);
			}
		}.start();
		
	}
	
	
	private View.OnClickListener refreshClickListener = new View.OnClickListener() {
		public void onClick(View v) {	
			loadLvMyNoticeData(curCatalog,1,myNoticeHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
		}
	};
	
    
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
				result=this.getString(R.string.msg_opt_post_no_right_del_other_post);
				break;
			case 0:
				result=this.getString(R.string.msg_opt_post_unknow);
				break;
			case -14:
				result=this.getString(R.string.network_not_connected);
				break;
		}
		return result;
	}
}
