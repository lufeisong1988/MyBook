package com.starbaby.diyBook.clientui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.ListViewPersonalCenterAdapter;
import com.starbaby.diyBook.adapter.ListViewPersonalDiaryAdapter;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientbean.Post;
import com.starbaby.diyBook.clientbean.PostList;
import com.starbaby.diyBook.clientcommon.StringUtils;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.clientwidget.LoadingDialog;
import com.starbaby.diyBook.clientwidget.PullToRefreshListView;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewSwitcher;

 

public class MyPost extends BaseActivity {
	
	private ImageView backHome;

	private PullToRefreshListView lvChat;
	private ArrayList<Post> lvChatData = new ArrayList<Post>();
	private View lvChat_footer;
	private TextView lvChat_footer_more;
	private ProgressBar lvChat_footer_progress;
	private ViewSwitcher lvChat_footer_viewSwitcher;
	private Button lvChat_footer_pub_btn;
	private Handler chatHandler;
	
	private PullToRefreshListView lvMoment;
	private ArrayList<Post> lvMomentData = new ArrayList<Post>();
	private View lvMoment_footer;
	private TextView lvMoment_footer_more;
	private ProgressBar lvMoment_footer_progress;
	private ViewSwitcher lvMoment_footer_viewSwitcher;
	private Button lvMoment_footer_pub_btn;
	private Handler momentHandler;
 
	private PullToRefreshListView lvDiary;
	private ArrayList<Post> lvDiaryData = new ArrayList<Post>();
	private View lvDiary_footer;
	private TextView lvDiary_footer_more;
	private ProgressBar lvDiary_footer_progress;
	private ViewSwitcher lvDiary_footer_viewSwitcher;
	private Button lvDiary_footer_pub_btn;
	private Handler diaryHandler;
 
	private AppContext ac;
	
/*	private int curUid;*/
	private int chatIndex = 1;
	private int momentIndex = 1;
	private int diaryIndex = 1;
    
	private LoadingDialog loading;
	
	private int curUid;
	
	private boolean firstMoment = true;
	private boolean firstDiary = true;
	private boolean firstChat = true;
	
	private Button personChatBtn;
	private Button personDiaryBtn;
	private Button personMomentBtn;
	private int curCatalog = PostList.POST_TYPE_CHAT;
	
	private ListViewPersonalCenterAdapter chatAdapter;
	private ListViewPersonalCenterAdapter momentAdapter;
	private ListViewPersonalDiaryAdapter  diaryAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_post);
		initView();
		initData();
		this.loadLvPostData(curUid,curCatalog,1,chatHandler,UIHelper.LISTVIEW_ACTION_INIT);
	}
	private void initView(){
		ac =  ((AppContext)getApplication());
		loading = new LoadingDialog(this);	  
		
		//curUid = getIntent().getIntExtra("his_id", 0);
		Bundle bundle = getIntent().getExtras();
		if(bundle != null && bundle.containsKey("userUid")){
			curUid = bundle.getInt("userUid");
		}
//		curUid = ac.getLoginUid();
		backHome = (ImageView)findViewById(R.id.personal_post_back);
		backHome.setOnClickListener(UIHelper.finish(this));
		//lvHeader = View.inflate(this, R.layout.personal_post_header, null);
		
		initLvChat();
		initLvDiary();
		initLvMoment();
		initPostType();
	}
	
 
	 
	private void initPostType(){
		 personChatBtn= (Button)findViewById(R.id.personal_post_head_chat);
		 personDiaryBtn=(Button)findViewById(R.id.personal_post_head_diary);
		 personMomentBtn=(Button)findViewById(R.id.personal_post_head_moment);
	 
		 personChatBtn.setOnClickListener(personTypeBtnClick(personChatBtn,PostList.POST_TYPE_CHAT));
		 personDiaryBtn.setOnClickListener(personTypeBtnClick(personDiaryBtn, PostList.POST_TYPE_DIARY));
		 personMomentBtn.setOnClickListener(personTypeBtnClick(personMomentBtn,PostList.POST_TYPE_MOMENT));
	}
	
	private View.OnClickListener personTypeBtnClick(final Button btn,final int catalog){
    	return new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				if(v==personChatBtn){
					lvChat.setVisibility(View.VISIBLE);
					personChatBtn.setEnabled(false);
				}else{
					lvChat.setVisibility(View.GONE);
					personChatBtn.setEnabled(true);
				}
				
				if(v==personDiaryBtn){
					lvDiary.setVisibility(View.VISIBLE);
					personDiaryBtn.setEnabled(false);
				}else{
					lvDiary.setVisibility(View.GONE);
					personDiaryBtn.setEnabled(true);
				}
				
				if(v==personMomentBtn){
					lvMoment.setVisibility(View.VISIBLE);
					personMomentBtn.setEnabled(false);
				}else{
					lvMoment.setVisibility(View.GONE);
					personMomentBtn.setEnabled(true);
				}
				
				curCatalog = catalog;
				//otherTip.setVisibility(View.GONE);
				
				if(curCatalog==PostList.POST_TYPE_DIARY){
					loadLvPostData(curUid,curCatalog, 1, diaryHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
				}else if(curCatalog==PostList.POST_TYPE_CHAT){
					loadLvPostData(curUid,curCatalog, 1, chatHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
				}else if(curCatalog==PostList.POST_TYPE_MOMENT){
					loadLvPostData(curUid,curCatalog, 1, momentHandler, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
				}
			}
    		
    	};
    }
    
	
	private void initLvChat(){
		lvChat_footer = getLayoutInflater().inflate(R.layout.personal_listview_footer_myself, null);
		lvChat_footer_more = (TextView)lvChat_footer.findViewById(R.id.personal_listview_footer_myself_more);
		lvChat_footer_progress = (ProgressBar)lvChat_footer.findViewById(R.id.personal_listview_footer_myself_progress);
		lvChat_footer_viewSwitcher=(ViewSwitcher)lvChat_footer.findViewById(R.id.personal_listview_footer_myself_viewswitcher);
		lvChat_footer_pub_btn =(Button)lvChat_footer.findViewById(R.id.personal_listview_footer_myself_warn_pub_btn);
		lvChat_footer_pub_btn.setOnClickListener(startPubPost);
		
		chatAdapter = new ListViewPersonalCenterAdapter(this,lvChatData,imageLoader);
		
		lvChat = (PullToRefreshListView)findViewById(R.id.personal_post_chat_listview);
	//	lvPost.addHeaderView(lvHeader);
		lvChat.setVisibility(View.VISIBLE);
		lvChat.addFooterView(lvChat_footer);
		lvChat.setAdapter(chatAdapter);
		lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//点击底部栏无效
				if(view == lvChat_footer) return;
				
        		Post post = null;
        		post = (Post)parent.getAdapter().getItem(position);
        		if(post == null) return;  
        		//跳转到留言详情
        		Intent intent  = new Intent(MyPost.this,MessageDetail.class);
				intent.putExtra("tid", post.getTid());//点击的用户uid
				intent.putExtra("authorid", post.getAuthorid());//自己的uid
				startActivity(intent);
				overridePendingTransition(R.anim.workbook_in,R.anim.none);
        	  //  UIHelper.showPostDetail(view.getContext(), post.getTid(),post.getAuthorid()); 
			}
			 
		});
		
		lvChat.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//点击底部栏无效
				if(view == lvChat_footer) return false;
				Post  post = (Post)parent.getAdapter().getItem(position);
				final Post _post = post;
				if(post==null) return true;
				if(ac.isLogin() && ac.getLoginUid() ==post.getAuthorid()){
					final Handler handler = new Handler(){
						public void handleMessage(Message msg){
							if(loading != null)	loading.dismiss();
        					
        					if(msg.what==1){
        						lvChatData.remove(_post);
        						chatAdapter.notifyDataSetChanged();
        					}else {
        						UIHelper.ToastMessage(MyPost.this,getErrorInfo(msg.what)); //弹出错误消息对话框
        					}
						}
					};
					final Thread thread = new Thread(){
						public void run(){
							Message msg = new Message();
							
							try {
								int result = ac.delPost(ac.getLoginUid(),ac.getLoginPwd(),_post.getTid());
								msg.what = result;
							} catch (AppException e) {
								e.printStackTrace();
								msg.what = -1;
							}
							 handler.sendMessage(msg);
						}
					};
					UIHelper.showDelOptionDialog(MyPost.this,"个人话题",loading,thread);
				}
				return false;
			}
		});
		
		lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
					lvChat.onScrollStateChanged(view,scrollState);
					if(lvChatData.size()==0) return;
					//判断是否滚动到底部
					boolean scrollEnd = false;
					try{
						if(view.getPositionForView(lvChat_footer)==view.getLastVisiblePosition())
							scrollEnd = true;
					}catch(Exception  e){
							scrollEnd = false;
					}
					int lvDataState = StringUtils.toInt(lvChat.getTag());
					if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
					{
						lvChat.setTag(UIHelper.LISTVIEW_DATA_LOADING);
						lvChat_footer_more.setText(R.string.load_ing);
						lvChat_footer_progress.setVisibility(View.VISIBLE);
						if(firstChat){
							chatIndex=chatIndex+1;
							firstChat=false;
						}
						loadLvPostData(curUid,curCatalog,chatIndex,chatHandler,UIHelper.LISTVIEW_ACTION_SCROLL);
					}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				 // lvPost.onScroll(view,firstVisibleItem,visibleItemCount,totalItemCount);
			}
		}); 
		lvChat.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadLvPostData(curUid,curCatalog,1,chatHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
		
	}
	
	private void initLvMoment(){
		lvMoment_footer = getLayoutInflater().inflate(R.layout.personal_listview_footer_myself, null);
		lvMoment_footer_more = (TextView)lvMoment_footer.findViewById(R.id.personal_listview_footer_myself_more);
		lvMoment_footer_progress = (ProgressBar)lvMoment_footer.findViewById(R.id.personal_listview_footer_myself_progress);
		lvMoment_footer_viewSwitcher = (ViewSwitcher)lvMoment_footer.findViewById(R.id.personal_listview_footer_myself_viewswitcher);
		lvMoment_footer_pub_btn =(Button)lvMoment_footer.findViewById(R.id.personal_listview_footer_myself_warn_pub_btn);
		lvMoment_footer_pub_btn.setOnClickListener(startPubPost);
		
		momentAdapter = new ListViewPersonalCenterAdapter(this,lvMomentData,imageLoader);
		lvMoment = (PullToRefreshListView)findViewById(R.id.personal_post_moment_listview);
	//	lvMoment.addHeaderView(lvHeader);
	 //lvMoment.setVisibility(View.VISIBLE);
		lvMoment.addFooterView(lvMoment_footer);
		lvMoment.setAdapter(momentAdapter);
		lvMoment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//点击底部栏无效
				if(view == lvMoment_footer) return;
				
        		Post post = null;
        		post = (Post)parent.getAdapter().getItem(position);
        		if(post == null) return;  
        		//跳转到留言详情
        	   // UIHelper.showPostDetail(view.getContext(), post.getTid(),post.getAuthorid()); 
			}
			 
		});
		
		lvMoment.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//点击底部栏无效
				if(view == lvMoment_footer) return false;
				Post  post = (Post)parent.getAdapter().getItem(position);
				final Post _post = post;
				if(post==null) return true;
				if(ac.isLogin() && ac.getLoginUid() ==post.getAuthorid()){
					final Handler handler = new Handler(){
						public void handleMessage(Message msg){
							if(loading != null)	loading.dismiss();
        					
        					if(msg.what==1){
        						lvMomentData.remove(_post);
        						momentAdapter.notifyDataSetChanged();
        					}else {
        						UIHelper.ToastMessage(MyPost.this,getErrorInfo(msg.what)); //弹出错误消息对话框
        					}
						}
					};
					final Thread thread = new Thread(){
						public void run(){
							Message msg = new Message();
							
							try {
								int result = ac.delPost(ac.getLoginUid(),ac.getLoginPwd(),_post.getTid());
								msg.what = result;
							} catch (AppException e) {
								e.printStackTrace();
								msg.what = -1;
							}
							 handler.sendMessage(msg);
						}
					};
					UIHelper.showDelOptionDialog(MyPost.this,"个人话题",loading,thread);
				}
				return false;
			}
		});
		
		lvMoment.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
					lvMoment.onScrollStateChanged(view,scrollState);
					if(lvMomentData.size()==0) return;
					//判断是否滚动到底部
					boolean scrollEnd = false;
					try{
						if(view.getPositionForView(lvMoment_footer)==view.getLastVisiblePosition())
							scrollEnd = true;
					}catch(Exception  e){
							scrollEnd = false;
					}
					int lvDataState = StringUtils.toInt(lvMoment.getTag());
					if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
					{
						lvMoment.setTag(UIHelper.LISTVIEW_DATA_LOADING);
						lvMoment_footer_more.setText(R.string.load_ing);
						lvMoment_footer_progress.setVisibility(View.VISIBLE);
						if(firstMoment){
							momentIndex=momentIndex+1;
							firstMoment=false;
						}
						loadLvPostData(curUid,curCatalog,momentIndex,momentHandler,UIHelper.LISTVIEW_ACTION_SCROLL);
					}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				 // lvPost.onScroll(view,firstVisibleItem,visibleItemCount,totalItemCount);
			}
		}); 
 
		lvMoment.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			public void onRefresh() {
				loadLvPostData(curUid,curCatalog,1,momentHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
	}
	
	
	private void initLvDiary(){
		lvDiary_footer = getLayoutInflater().inflate(R.layout.personal_listview_footer_myself, null);
		lvDiary_footer_more = (TextView)lvDiary_footer.findViewById(R.id.personal_listview_footer_myself_more);
		lvDiary_footer_progress = (ProgressBar)lvDiary_footer.findViewById(R.id.personal_listview_footer_myself_progress);
		lvDiary_footer_viewSwitcher =(ViewSwitcher) lvDiary_footer.findViewById(R.id.personal_listview_footer_myself_viewswitcher);
		lvDiary_footer_pub_btn =(Button)lvDiary_footer.findViewById(R.id.personal_listview_footer_myself_warn_pub_btn);
		lvDiary_footer_pub_btn.setOnClickListener(startPubPost);
		
		diaryAdapter = new ListViewPersonalDiaryAdapter(this,lvDiaryData,imageLoader);
		lvDiary = (PullToRefreshListView)findViewById(R.id.personal_post_diary_listview);
	//	lvDiary.addHeaderView(lvHeader);
		lvDiary.addFooterView(lvDiary_footer);
		lvDiary.setAdapter(diaryAdapter);
		lvDiary.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//点击底部栏无效
				if(view == lvDiary_footer) return;
				
        		Post post = null;
        		post = (Post)parent.getAdapter().getItem(position);
        		if(post == null) return;  
        		//跳转到留言详情
        	   // UIHelper.showPostDetail(view.getContext(), post.getTid(),post.getAuthorid()); 
			}
			 
		});
		
		lvDiary.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//点击底部栏无效
				if(view == lvDiary_footer) return false;
				Post  post = (Post)parent.getAdapter().getItem(position);
				final Post _post = post;
				if(post==null) return true;
				if(ac.isLogin() && ac.getLoginUid() ==post.getAuthorid()){
					final Handler handler = new Handler(){
						public void handleMessage(Message msg){
							if(loading != null)	loading.dismiss();
        					
        					if(msg.what==1){
        						lvDiaryData.remove(_post);
        						String key = "mypostlist_"+_post.getAuthorid()+"_"+1+"_"+curCatalog;
        						ac.removeProperty(key); 
        						diaryAdapter.notifyDataSetChanged();
        					}else {
        						UIHelper.ToastMessage(MyPost.this,getErrorInfo(msg.what)); //弹出错误消息对话框
        					}
						}
					};
					final Thread thread = new Thread(){
						public void run(){
							Message msg = new Message();
							
							try {
								int result = ac.delPost(ac.getLoginUid(),ac.getLoginPwd(),_post.getTid());
								msg.what = result;
							} catch (AppException e) {
								e.printStackTrace();
								msg.what = -1;
							}
							 handler.sendMessage(msg);
						}
					};
					UIHelper.showDelOptionDialog(MyPost.this,"个人话题",loading,thread);
				}
				return false;
			}
		});
		
		lvDiary.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				   lvDiary.onScrollStateChanged(view,scrollState);
					if(lvDiaryData.size()==0) return;
					//判断是否滚动到底部
					boolean scrollEnd = false;
					try{
						if(view.getPositionForView(lvDiary_footer)==view.getLastVisiblePosition())
							scrollEnd = true;
					}catch(Exception  e){
							scrollEnd = false;
					}
					int lvDataState = StringUtils.toInt(lvDiary.getTag());
					if(scrollEnd && lvDataState==UIHelper.LISTVIEW_DATA_MORE)
					{
						lvDiary.setTag(UIHelper.LISTVIEW_DATA_LOADING);
						lvDiary_footer_more.setText(R.string.load_ing);
						lvDiary_footer_progress.setVisibility(View.VISIBLE);
						if(firstDiary){
							diaryIndex=diaryIndex+1;
							firstDiary=false;
						}
						loadLvPostData(curUid,curCatalog,diaryIndex,diaryHandler,UIHelper.LISTVIEW_ACTION_SCROLL);
					}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				//lvDiary.onScroll(view,firstVisibleItem,visibleItemCount,totalItemCount);
			}
		}); 
		lvDiary.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
				public void onRefresh() {
					loadLvPostData(curUid,curCatalog,1,diaryHandler,UIHelper.LISTVIEW_ACTION_REFRESH);
	            }
	        });
		
	}
	 
	
	private void initData(){
		chatHandler = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what>=0){
					PostList list = (PostList)msg.obj;
					switch(msg.arg1){
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
					case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
						chatIndex =1;
						firstChat = true;
						lvChatData.clear();
						lvChatData.addAll(list.getPostlist());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						if(chatIndex<=msg.what)
							chatIndex++;
						if(lvChatData.size()>0){
							for(Post post:list.getPostlist()){
								boolean b = false;
								for(Post post2:lvChatData){
									if(post.getTid()==post2.getTid()){
										b = true;
										break;
									}
								}
								if(!b) lvChatData.add(post);
							}
						}else {
							lvChatData.addAll(list.getPostlist());
						}
						break;
					}
					if(msg.what<chatIndex){
						lvChat.setTag(UIHelper.LISTVIEW_DATA_FULL);
						chatAdapter.notifyDataSetChanged();
						lvChat_footer_more.setText(R.string.load_full);
					} else if(msg.what>=chatIndex){
						lvChat.setTag(UIHelper.LISTVIEW_DATA_MORE);
						momentAdapter.notifyDataSetChanged();
						lvChat_footer_more.setText(R.string.load_more);
					}
				} else if(msg.what==-1){
					//有异常--显示加载出错 & 弹出错误消息
					lvChat.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvChat_footer_more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(MyPost.this);
				}
				if(chatAdapter.getCount()==0){
					lvChat.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					lvChat_footer_viewSwitcher.setDisplayedChild(1); 
				   // lvChat_footer_warnTxt.setText("该用户没有发布任何话题");
				}else if(lvChat_footer_viewSwitcher.getDisplayedChild()==1){
					lvChat_footer_viewSwitcher.setDisplayedChild(0); 
				}
				lvChat_footer_progress.setVisibility(View.GONE);
			    if(msg.arg1 != UIHelper.LISTVIEW_ACTION_SCROLL){
			    	if(msg.arg1==UIHelper.LISTVIEW_ACTION_REFRESH){
			    		lvChat.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					}else{
						lvChat.onRefreshComplete();
					}
					lvChat.setSelection(0);//返回头部
				}
			}
		};
		
		momentHandler = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what>=0){
					PostList list = (PostList)msg.obj;
					switch(msg.arg1){
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
					case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
						momentIndex =1;
						firstMoment = true;
						lvMomentData.clear();
						lvMomentData.addAll(list.getPostlist());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						if(momentIndex<=msg.what)
							momentIndex++;
						if(lvMomentData.size()>0){
							for(Post post:list.getPostlist()){
								boolean b = false;
								for(Post post2:lvMomentData){
									if(post.getTid()==post2.getTid()){
										b = true;
										break;
									}
								}
								if(!b) lvMomentData.add(post);
							}
						}else {
							lvMomentData.addAll(list.getPostlist());
						}
						break;
					}
					if(msg.what<momentIndex){
						lvMoment.setTag(UIHelper.LISTVIEW_DATA_FULL);
						momentAdapter.notifyDataSetChanged();
						lvMoment_footer_more.setText(R.string.load_full);
					} else if(msg.what>=momentIndex){
						lvMoment.setTag(UIHelper.LISTVIEW_DATA_MORE);
						momentAdapter.notifyDataSetChanged();
						lvMoment_footer_more.setText(R.string.load_more);
					}
				} else if(msg.what==-1){
					//有异常--显示加载出错 & 弹出错误消息
					lvMoment.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvMoment_footer_more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(MyPost.this);
				}
				if(momentAdapter.getCount()==0){
					lvMoment.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					lvMoment_footer_viewSwitcher.setDisplayedChild(1);
				//	lvMoment_footer_warnTxt.setText("该用户没有发布任何话题");
				}else if(lvMoment_footer_viewSwitcher.getDisplayedChild()==1){
					lvMoment_footer_viewSwitcher.setDisplayedChild(0);
				}
				lvMoment_footer_progress.setVisibility(View.GONE);
			    if(msg.arg1 != UIHelper.LISTVIEW_ACTION_SCROLL){
			    	if(msg.arg1==UIHelper.LISTVIEW_ACTION_REFRESH){
			    		lvMoment.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					}else{
						lvMoment.onRefreshComplete();
					}
					lvMoment.setSelection(0);//返回头部
				}
			}
		};
		diaryHandler = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what>=0){
					PostList list = (PostList)msg.obj;
					switch(msg.arg1){
					case UIHelper.LISTVIEW_ACTION_INIT:
					case UIHelper.LISTVIEW_ACTION_REFRESH:
					case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
						diaryIndex =1;
						firstDiary = true;
						lvDiaryData.clear();
						lvDiaryData.addAll(list.getPostlist());
						break;
					case UIHelper.LISTVIEW_ACTION_SCROLL:
						if(diaryIndex<=msg.what)
							diaryIndex++;
						if(lvDiaryData.size()>0){
							for(Post post:list.getPostlist()){
								boolean b = false;
								for(Post post2:lvDiaryData){
									if(post.getTid()==post2.getTid()){
										b = true;
										break;
									}
								}
								if(!b) lvDiaryData.add(post);
							}
						}else {
							lvDiaryData.addAll(list.getPostlist());
						}
						break;
					}
					if(msg.what<diaryIndex){
						lvDiary.setTag(UIHelper.LISTVIEW_DATA_FULL);
						diaryAdapter.notifyDataSetChanged();
						lvDiary_footer_more.setText(R.string.load_full);
					} else if(msg.what>=diaryIndex){
						lvDiary.setTag(UIHelper.LISTVIEW_DATA_MORE);
						diaryAdapter.notifyDataSetChanged();
						lvDiary_footer_more.setText(R.string.load_more);
					}
				} else if(msg.what==-1){
					//有异常--显示加载出错 & 弹出错误消息
					lvDiary.setTag(UIHelper.LISTVIEW_DATA_MORE);
					lvDiary_footer_more.setText(R.string.load_error);
					((AppException)msg.obj).makeToast(MyPost.this);
				}
				if(diaryAdapter.getCount()==0){
					lvDiary.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
					lvDiary_footer_viewSwitcher.setDisplayedChild(1);
				}else if(lvDiary_footer_viewSwitcher.getDisplayedChild()==1){
					lvDiary_footer_viewSwitcher.setDisplayedChild(0);
				}
				lvDiary_footer_progress.setVisibility(View.GONE);
				if(msg.arg1 != UIHelper.LISTVIEW_ACTION_SCROLL){
					if(msg.arg1==UIHelper.LISTVIEW_ACTION_REFRESH){
						lvDiary.onRefreshComplete(getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
					}else{
						lvDiary.onRefreshComplete();
					}
					lvDiary.setSelection(0);//返回头部
				}
			}
		};
	}
 
	private void loadLvPostData(final int hisId,final int catalog,final int pageIndex,final Handler handler,final int action){
		new Thread(){
			public void run(){
				Message msg = new Message();
				boolean isRefresh = false;
				if(action == UIHelper.LISTVIEW_ACTION_REFRESH || action == UIHelper.LISTVIEW_ACTION_SCROLL)
					isRefresh = true;
				try {
					PostList postList =ac.getMyPostList(hisId,catalog,pageIndex,curUid,isRefresh);
					//System.out.println("postList.size="+postList.getPostlist().size());
					msg.what = postList.getPageSize();
					msg.obj = postList;
				} catch (AppException e) {
					e.printStackTrace();
					msg.obj = e;
					msg.what = -1;
				}
				msg.arg1 = action;//告知handler当前action
                handler.sendMessage(msg);
			}
		}.start();
		
	}
	
	private View.OnClickListener startPubPost = new  View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			UIHelper.showActivity(MyPost.this, MessageType.class);
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
			case -9:
				result= this.getString(R.string.msg_opt_fensi_add_exist);
				break;
			case -10:
				result=this.getString(R.string.msg_opt_post_unknow);
				break;
			case -14:
				result=this.getString(R.string.network_not_connected);
				break;
		}
		return result;
	}
}
