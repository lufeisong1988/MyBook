package com.starbaby.diyBook.fragment;

/**
 * 个人书架
 */
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.BookShelfAdapter3;
import com.starbaby.diyBook.cache.GetCoverCache3;
import com.starbaby.diyBook.controller.DeleteCache;
import com.starbaby.diyBook.controller.ReadSDcard;
import com.starbaby.diyBook.main.UserInfo;
import com.starbaby.diyBook.model.CoverThreadPool3;
import com.starbaby.diyBook.net.AsyncHttpPost;
import com.starbaby.diyBook.net.DefaultThreadPool;
import com.starbaby.diyBook.net.RequestParameter;
import com.starbaby.diyBook.net.RequestResultCallback;
import com.starbaby.diyBook.utils.HttpPort;
import com.starbaby.diyBook.utils.JavaBean3;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.commentDialogUtils;
import com.starbaby.diyBook.view.PullToRefreshListView;
import com.starbaby.diyBook.view.PullToRefreshListView.OnRefreshListener;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

@SuppressLint({ "NewApi", "ValidFragment", "HandlerLeak", "ShowToast" })
public class Fragment1 extends Fragment implements OnClickListener{
	private static long time;
	private Context mContext;
	private PullToRefreshListView lv ;
	private TextView tv;
	private Button bnt2,mineBnt,localBnt,collectBnt;
	private int page = 2;
	private int lastItem;
	private TextView attention, fans;
	private ImageButton attentionBnt;
	private TextView msgTv;
	
	private commentDialogUtils dialog;
	private JavaBean3 mJavaBean3 ;
	private SharedPreferences sp;
	
	private Button[] nickName;
	private int[] buttonName;
	private int[] unClick = {
			R.drawable.userinfo_shelf_off,R.drawable.userinfo_shelf_off,R.drawable.userinfo_shelf_off
	};
	private int[] click = {
			R.drawable.userinfo_shelf_in,R.drawable.userinfo_shelf_in,R.drawable.userinfo_shelf_in
	};
	private String uid;
	private String psw;
	private String userUid;
	private boolean bMyShelf;
	private BookShelfAdapter3 mAdapter;
	private boolean bPost = true;
	private boolean bPull = true;
	private boolean bCollect = false;
	private int flag = 1;
	ProgressDialog mDeleteDialog;
	/**
	 * updateTime 是根据间隔多少时间自动刷新 个人中心里 我的作品信息列表
	 */
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				if(msg.arg1 == 1){
					mAdapter = new BookShelfAdapter3(mContext, mJavaBean3,uid,flag,Fragment1.this);
					lv.setAdapter(mAdapter);
					attention.setText(attentiontotal + "");
					fans.setText(fanstotal + "");
					if(msg.arg2 == 0){//消息提示，为0 说明没有留言。不显示
						msgTv.setVisibility(View.INVISIBLE);
					}else{
						if(userUid.equals(uid)){//查看自己的
							msgTv.setVisibility(View.VISIBLE);
							msgTv.setText(msg.arg2 + "");
						}
					}
					if(userUid.equals(uid)){//查看自己的
						attentionBnt.setVisibility(View.INVISIBLE);
					}else{
						if(note_state == 1 || note_state == 0){
							attentionBnt.setVisibility(View.VISIBLE);
						}
					}
				}else{
					mAdapter.notifyDataSetChanged();
					bPost = true;
				}
				page++;
				break;
			case 1:
				Toast.makeText(mContext, "连接网络失败", Toast.LENGTH_LONG).show();
				bPost = true;
				break;
			case 2:
				mAdapter = new BookShelfAdapter3(mContext, mJavaBean3,uid,flag,Fragment1.this);
				lv.setAdapter(mAdapter);
				break;
			case 3:
				mAdapter.notifyDataSetChanged();
				break;
			case 4:
				Toast.makeText(mContext, "收藏失败", Toast.LENGTH_LONG).show();
				break;
			case 5:
				locaOrCollectShelf("collect");
				break;
			case 6:
				mDeleteDialog.dismiss();
				Utils.mDBUserInfoHelper.deleteMyBook(mTpl_id);
				new GetCoverCache3(mContext, mJavaBean3,uid).getData();
				mAdapter = new BookShelfAdapter3(mContext, mJavaBean3, uid, flag,Fragment1.this);
				lv.setAdapter(mAdapter);
				Toast.makeText(mContext, "删除成功",Toast.LENGTH_LONG).show();
				break;
			case 7:
				mDeleteDialog.dismiss();
				Toast.makeText(mContext, "删除失败",Toast.LENGTH_LONG).show();
				 break;
			case 12:
				memory_show();
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	public Fragment1(){
	}
	public Fragment1(Context mContext,int uid,String userUid,String psw,TextView attention,TextView fans,boolean bMyshelf,
			ImageButton attentionBnt,TextView msgTv){
		this.mContext = mContext;
		this.uid = uid +"";
		this.psw = psw;
		this.bMyShelf = bMyshelf;
		this.userUid = userUid;
		this.attention = attention;
		this.attentionBnt = attentionBnt;
		this.fans = fans;
		this.msgTv = msgTv;
		sp = mContext.getSharedPreferences("diyBook", mContext.MODE_WORLD_READABLE);
		mDeleteDialog = new ProgressDialog(mContext);
		mDeleteDialog.setCanceledOnTouchOutside(false);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		if (container == null) 
		{
            return null;
        }
		LayoutInflater myInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	    View layout = myInflater.inflate(R.layout.fragment_1, container, false); 
	    lv = (PullToRefreshListView)layout.findViewById(R.id.fragment_lv1);
	    mineBnt = (Button) layout.findViewById(R.id.fragment1_mine);
	    localBnt = (Button) layout.findViewById(R.id.fragment1_local);
	    collectBnt = (Button) layout.findViewById(R.id.fragment1_collect);
	    if(!bMyShelf){
	    	localBnt.setVisibility(View.INVISIBLE);
	    	collectBnt.setVisibility(View.INVISIBLE);
	    	mineBnt.setText("他的作品");
	    }else{
	    	 mineBnt.setOnClickListener(this);
	    }
	    nickName = new Button[]{
	    		mineBnt,localBnt,collectBnt
	    };
	    buttonName = new int[]{
	    		R.id.fragment1_mine,R.id.fragment1_local,R.id.fragment1_collect
	    };
	    mineBnt.setBackgroundResource(R.drawable.userinfo_shelf_in);
	    Button bnt1 =(Button)layout. findViewById(R.id.fragment1_bnt1);
	    Button bnt2 =(Button)layout. findViewById(R.id.fragment1_bnt2);
		lv.setOnRefreshListener(new OnRefreshListener() {// 下拉
			@Override
			public void onRefresh() {
				if (bPull) {
					/** 加载数据，刷新ListView */
					postData(1, 1);
				}
				if(bCollect){
					/*
					 * 向服务器请求收藏信息
					 */
					JavaBean3.bookIdList = new ArrayList<String>();
					sync_collect(JavaBean3.bookIdList,true,true);
				}
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						lv.onRefreshComplete();
					}
				}, 3000);
			}
		});
	        
		lv.setOnScrollListener(new OnScrollListener() {// 上拉

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(bPull){
					if ((lastItem > new BookShelfAdapter3(mContext, mJavaBean3,uid, flag,Fragment1.this).getCount() - 1)
							|| (lastItem == new BookShelfAdapter3(mContext,mJavaBean3, uid, flag,Fragment1.this).getCount() - 1)) {
						if (bPost) {
							postData(page, 2);
							bPost = false;
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem - 2 + visibleItemCount;
			}
		});
	    bnt1.setOnClickListener(this);
	    bnt2.setOnClickListener(this);
	   
	    localBnt.setOnClickListener(this);
	    collectBnt.setOnClickListener(this);
	    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			if(bMyShelf){
				showCache();
			}else{
				showOther();
			}
		}else{
	    	Toast.makeText(mContext, "请插入SdCard卡", 1000).show();
	    }
		return layout;
	}
	@Override
	public void onClick(View v) {
		for(int i = 0;i < nickName.length;i++){
			nickName[i].setBackgroundResource(unClick[i]);
			if(v.getId() == buttonName[i]){
				nickName[i].setBackgroundResource(click[i]);
			}
		}
		switch (v.getId()){
		case R.id.fragment1_bnt1:
			show();
			break;
		case R.id.fragment_1_bnt2:
			dialog.dismiss();
			break;
		case R.id.fragment1_bnt2:
			show();
			break;
		case R.id.fragment1_mine:
			bPull = true;
			bCollect = false;
			flag = 1;
			 if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					showCache();
				}else{
			    	Toast.makeText(mContext, "请插入SdCard卡", 1000).show();
			    }
			break;
		case R.id.fragment1_local:
			bPull = false;
			bCollect = false;
			flag = 2;
			if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				Toast.makeText(mContext, "请插入SdCard卡", 1000).show();
			}else{
				locaOrCollectShelf("local");
			}
			break;
		case R.id.fragment1_collect:
			bPull = false;
			bCollect = true;
			flag = 1;
			if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				Toast.makeText(mContext, "请插入SdCard卡", 1000).show();
			}else{
				locaOrCollectShelf("collect");
			}
			break;
		}
		
	}
	/*
	 * 个人作品
	 */
	void showCache(){
		JavaBean3.bookCoverList = new ArrayList<String>();
		JavaBean3.bookNameList = new ArrayList<String>();
		JavaBean3.bookIdList = new ArrayList<String>();
		JavaBean3.bookUpdateTime = new ArrayList<String>();
		
		mAdapter = new BookShelfAdapter3(getActivity(), mJavaBean3,uid,flag,Fragment1.this);
	    lv.setAdapter(mAdapter);
		if(new GetCoverCache3(mContext, mJavaBean3,uid).getData()){
			mAdapter = new BookShelfAdapter3(mContext, mJavaBean3, uid, flag,Fragment1.this);
			lv.setAdapter(mAdapter);
		}else{
			postData(1,1);
		}
		Long time = sp.getLong("updateTime", 0);
		if(time == null || time.equals("")){
			time = (long) 1000000000;
		}
		if(Utils.sInfo > 0 && (Utils.sInfo % 3) == 0 || ( System.currentTimeMillis() / 1000 - time) > 24 * 60 * 60){
			postData(1,1);
		}
	}
	/*
	 * 他人作品
	 */
	void showOther(){
		JavaBean3.bookCoverList = new ArrayList<String>();
		JavaBean3.bookNameList = new ArrayList<String>();
		JavaBean3.bookIdList = new ArrayList<String>();
		JavaBean3.bookUpdateTime = new ArrayList<String>();
		
		mAdapter = new BookShelfAdapter3(getActivity(), mJavaBean3,uid,flag,Fragment1.this);
	    lv.setAdapter(mAdapter);
	    postData(1,1);
	}
	/*
	 * 本地书架Or collect书架
	 */
	ArrayList<String> tpl_id;
	void locaOrCollectShelf(String flag){
		tpl_id = new ArrayList<String>();
		JavaBean3.bookCoverList = new ArrayList<String>();
		JavaBean3.bookNameList = new ArrayList<String>();
		JavaBean3.bookIdList = new ArrayList<String>();
		JavaBean3.bookUpdateTime = new ArrayList<String>();
		Cursor cursor = null;
		if(flag.equals("local")){
			cursor = Utils.mDBUserInfoHelper.getLocalBook();
			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				for(int i = 0; i < cursor.getCount();i++){
					JavaBean3.bookCoverList.add(cursor.getString(cursor.getColumnIndex("COVERURL")));
					JavaBean3.bookNameList.add(cursor.getString(cursor.getColumnIndex("NAME")));
					JavaBean3.bookIdList.add(cursor.getString(cursor.getColumnIndex("TPL_ID")));
					JavaBean3.bookUpdateTime.add(cursor.getString(cursor.getColumnIndex("TIME")));
					cursor.moveToNext();
				}
				cursor.close();
			}
			
		}else if(flag.equals("collect")){
			cursor = Utils.mDBUserInfoHelper.getCollectBook();
			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				for(int i = 0;i < cursor.getCount();i++){
					JavaBean3.bookCoverList.add(cursor.getString(cursor.getColumnIndex("COVERURL")));
					JavaBean3.bookNameList.add(cursor.getString(cursor.getColumnIndex("NAME")));
					JavaBean3.bookIdList.add(cursor.getString(cursor.getColumnIndex("ID")));
					JavaBean3.bookUpdateTime.add(cursor.getString(cursor.getColumnIndex("TIME")));
					cursor.moveToNext();
				}
				cursor.close();
			}else{
				sync_collect(JavaBean3.bookIdList,true,true);
			}
		}
		Message msg = new Message();
		msg.what = 2;
		mHandler.sendMessage(msg);
	}
	/*
	 * 删除本地书本
	 */
	public void deleteLocalBook(String tpl_id){
		DeleteCache.delAllFile(Utils.basePath1 + tpl_id);
		Utils.mDBUserInfoHelper.deleteLocalBook(tpl_id);
		Utils.mDBHelper.deleteBOOK_info(tpl_id);
		Utils.mDBHelper.deleteIMG_info(tpl_id);
		Utils.mDBHelper.deleteMP3_info(tpl_id);
		Utils.mDBHelper.deleteUPDATE_info(tpl_id);
		locaOrCollectShelf("local");
		Message msg = new Message();
		msg.what = 3;
		mHandler.sendMessage(msg);
	}
	/*
	 * collect 本地书本
	 */
	public void collectLocalBook(String name,String id,String time,String coverUrl){
		Utils.mDBUserInfoHelper.deleteLocalBook(id);
		locaOrCollectShelf("local");//刷新ui
		if(!Utils.mDBUserInfoHelper.bCollect(id)){
			Utils.mDBUserInfoHelper.saveCollectBook(Integer.parseInt(Utils.collect_uid),name,id,time,coverUrl);
			ArrayList<String> list = new ArrayList<String>();
			Cursor cursor = null;
			cursor = Utils.mDBUserInfoHelper.getCollectBook();
			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				for(int i = 0;i < cursor.getCount();i++){
					list.add(cursor.getString(cursor.getColumnIndex("ID")));
					cursor.moveToNext();
				}
				cursor.close();
			}
			sync_collect(list,false,true);
		}
	}
	/*
	 * 删除我的作品里的书籍 与服务器同步 
	 */
	String mTpl_id;
	public void deleteMyWork(final String tpl_id){
		this.mTpl_id = tpl_id;
		mDeleteDialog.show();
		ArrayList<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("uid", uid));
		parameter.add(new RequestParameter("pwd", psw));
		parameter.add(new RequestParameter("product_id", tpl_id));
		AsyncHttpPost post = new AsyncHttpPost(null, HttpPort.deleteBook, parameter, new RequestResultCallback() {
			
			@Override
			public void onSuccess(Object o) {
				String result = (String)o;
				if(result != null && !result.equals("")){
					try {
						JSONObject obj = new JSONObject(result);
						String msg = obj.getString("msg");
						if(Integer.parseInt(msg) == 1){
							Message msg2 = new Message();
							msg2.what = 6;
							mHandler.sendMessage(msg2);
							if(Utils.mDBUserInfoHelper.bExist(tpl_id)){//如果本地读过，就删除本地
								DeleteCache.delAllFile(Utils.basePath1 + tpl_id);
								Utils.mDBUserInfoHelper.deleteLocalBook(tpl_id);
								Utils.mDBHelper.deleteBOOK_info(tpl_id);
								Utils.mDBHelper.deleteIMG_info(tpl_id);
								Utils.mDBHelper.deleteMP3_info(tpl_id);
								Utils.mDBHelper.deleteUPDATE_info(tpl_id);
							} 
							if(Utils.mDBUserInfoHelper.bCollect(tpl_id)){//如果收藏中包含这本书，就删除（从服务器重新请求）
								JavaBean3.bookIdList = new ArrayList<String>();
								sync_collect(JavaBean3.bookIdList,true,false);
							}
						}else{
							Message msg2 = new Message();
							msg2.what = 7;
							mHandler.sendMessage(msg2);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
			
			@Override
			public void onFail(Exception e) {
				Message msg2 = new Message();
				msg2.what = 7;
				mHandler.sendMessage(msg2);
			}
		});
		DefaultThreadPool.getInstance().execute(post);
	}
	/*
	 *	同步本地与线上的数据
	 */
	void sync_collect(ArrayList<String> bookIdList,final boolean refresh,final boolean bShow){
		StringBuffer buffer = new StringBuffer();
		if(bookIdList.size() == 0){
			time = 1000000000;
		}else{
			time = System.currentTimeMillis() / 1000;
			for(int i = 0 ;i < bookIdList.size();i++){
				if(i != (bookIdList.size() - 1)){
					buffer.append(bookIdList.get(i).toString() + ",");
				}else{
					buffer.append(bookIdList.get(i).toString() );
				}
			}
		}
		
		ArrayList<RequestParameter> parameter = new ArrayList<RequestParameter>();
		parameter.add(new RequestParameter("uid", uid + ""));
		parameter.add(new RequestParameter("pwd", psw + ""));
		parameter.add(new RequestParameter("bookids", buffer.toString() + ""));
		parameter.add(new RequestParameter("timestamp",time + ""));
		AsyncHttpPost post = new AsyncHttpPost(null, HttpPort.sync_collect,parameter, new RequestResultCallback() {

					@Override
					public void onSuccess(Object o) {
						String result = (String) o;
						JSONObject object;
						JSONObject array;
						JSONArray data;
						
						ArrayList<String> book_id = new ArrayList<String>();
						ArrayList<String> book_name = new ArrayList<String>();
						ArrayList<String> thumb_img = new ArrayList<String>();
						ArrayList<String> update_time = new ArrayList<String>();
						String issync;
						String bookids;
						String timestamp;
						String msg;
						if (result != null && !result.equals("")) {
							Log.i("result", result);
							try {
								object = new JSONObject(result);
								msg = object.getString("msg");
								if(Integer.parseInt(msg) == 1){
									issync = object.getString("issync");
									if (Integer.parseInt(issync) == 1) {//本地要同步服务器
										array = new JSONObject(object.getString("collection"));
										bookids = array.getString("bookids");
										timestamp = array.getString("timestamp");
										data = new JSONArray(object.getString("data"));
										for(int i = 0;i < data.length();i++){
											book_id.add(data.getJSONObject(i).getString("book_id"));
											book_name.add(data.getJSONObject(i).getString("book_name"));
											thumb_img.add(data.getJSONObject(i).getString("thumb_img"));
											update_time.add(data.getJSONObject(i).getString("update_time"));
										}
										if(refresh){
											new CoverThreadPool3(mContext, thumb_img,Utils.collect_uid).saveCover();
											Utils.mDBUserInfoHelper.deleteCollectBook();
											for(int j = 0;j < thumb_img.size();j++){
												Utils.mDBUserInfoHelper.saveCollectBook(Integer.parseInt(Utils.collect_uid),book_name.get(j), book_id.get(j),update_time.get(j),thumb_img.get(j));
											}
											if(bShow){
												Message msg2 = new Message();
												msg2.what = 5;
												mHandler.sendMessage(msg2);
											}
										}
									}
								}else{
									Message msg2 = new Message();
									msg2.what = 4;
									mHandler.sendMessage(msg2);
								}
								
							} catch (JSONException e1) {
								e1.printStackTrace();
							}

						}
					}

					@Override
					public void onFail(Exception e) {
						Log.i("result", e.toString());
					}
				});
		DefaultThreadPool.getInstance().execute(post);
	}
	int attentiontotal;
	int fanstotal;
	int note_state;
	int msgnum;
	private void postData(final int mPage,final int refresh){
		if(Utils.SDcardMemory > ReadSDcard.readSDCard()){
			if((ReadSDcard.readSDCard() + ReadSDcard.readFile()) > Utils.SDcardMemory){
				DeleteCache.checkRoom(mContext);
			}else{
				Message msg = new Message();
				msg.what = 12;
				mHandler.sendMessage(msg);
				return;
			}
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				DefaultHttpClient client = new DefaultHttpClient();
				List<NameValuePair> list = new ArrayList<NameValuePair>();
				NameValuePair pair1 = new BasicNameValuePair("uid", uid);  
				NameValuePair pair2 = new BasicNameValuePair("guest_id", userUid + "");  
				NameValuePair pair3 = new BasicNameValuePair("page", mPage + "");  
				list.add(pair1);
				list.add(pair2);
				list.add(pair3);
				UrlEncodedFormEntity entity;
				try {
					entity = new UrlEncodedFormEntity(list,"UTF-8");
					HttpPost post = new HttpPost(HttpPort.myWorkShowUrl);  
	                post.setEntity(entity); 
	                HttpResponse response;  
	                //设置连接超时时间为40s
	        		HttpConnectionParams.setConnectionTimeout(client.getParams(),40000);
	        		//设置读取超时为20s
	        		HttpConnectionParams.setSoTimeout(client.getParams(),20000);
	                response=client.execute(post); 
	                if (response.getStatusLine().getStatusCode() == 200) {
						String result = EntityUtils.toString(response.getEntity());
						Log.i("result",result);
						if(result != null && !result.equals("")){
							JSONObject object;
							String nullObject;
							JSONArray array = null;
							object = new JSONObject(result);
							int total_size = object.getInt("msg");
							attentiontotal = object.getInt("attentiontotal");//关注数量
							fanstotal = object.getInt("fanstotal");//粉丝数
							note_state = object.getInt("note_state");// 关注状态：0-自己/未登录；1-未关注； 2-已关注；3-相互关注
							msgnum = object.getInt("msgnum");//未读消息数
							
							array = object.optJSONArray("data");//判断array返回的是否为null
							if(array == null){
								Log.i("array","null");
								Message msg = new Message();
								msg.arg1 = refresh;
								msg.what = 0;
								mHandler.sendMessage(msg);
								return;
							}
							array = object.getJSONArray("data");
							if(refresh == 1){
								ArrayList<String> bookCoverList = new ArrayList<String>();
								ArrayList<String> bookNameList = new ArrayList<String>();
								ArrayList<String> bookIdList = new ArrayList<String>();
								ArrayList<String> bookUpdateTime = new ArrayList<String>();
								mJavaBean3.bookCoverList = new ArrayList<String>();
								mJavaBean3.bookNameList = new ArrayList<String>();
								mJavaBean3.bookIdList = new ArrayList<String>();
								mJavaBean3.bookUpdateTime = new ArrayList<String>();
								for(int i = 0;i < array.length();i++){
									JSONObject list2 = array.getJSONObject(i);
									String bCompleted = list2.getString("is_completed");
									String templates_id = list2.getString("product_id");
									String templates_name = list2.getString("product_name");
									String thumb_img = list2.getString("thumb_img");
									String update_time = list2.getString("update_time");
											
									mJavaBean3.bookCoverList.add(thumb_img);
									mJavaBean3.bookNameList.add(templates_name);
									mJavaBean3.bookIdList.add(templates_id);
									mJavaBean3.bookUpdateTime.add(update_time);
									bookCoverList.add(thumb_img);
									bookNameList.add(templates_name);
									bookIdList.add(templates_id);
									bookUpdateTime.add(update_time);
								}
								if(bMyShelf){
									if(Utils.userBookCache){
										Utils.userBookCache = false;
										new CoverThreadPool3(mContext, bookCoverList, uid).saveCover();
										Utils.mDBUserInfoHelper.deleteUserInfo(Integer.parseInt(uid));
										for(int j = 0;j < bookCoverList.size();j++){
											Utils.mDBUserInfoHelper.saveCoverUrl(Integer.parseInt(uid), bookNameList.get(j),  bookIdList.get(j), bookUpdateTime.get(j), bookCoverList.get(j));
										}
										Utils.userBookCache = true;
									}
									
									/*
									 * 把个人信息存入到本地数据库中SharePerference 
									 */

									SharedPreferences.Editor editor = sp.edit();
									editor.putLong("updateTime", System.currentTimeMillis() / 1000);
									editor.putInt("attentiontotal", attentiontotal);
									editor.putInt("fanstotal", fanstotal);
									editor.putInt("note_state", note_state);
									editor.putInt("msgnum", msgnum);
									editor.commit();
								}
							}else{
								for(int i = 0;i < array.length();i++){
									JSONObject list2 = array.getJSONObject(i);
									String templates_id = list2.getString("product_id");
									String templates_name = list2.getString("product_name");
									String thumb_img = list2.getString("thumb_img");
									String update_time = list2.getString("update_time");
									mJavaBean3.bookCoverList.add(thumb_img);
									mJavaBean3.bookNameList.add(templates_name);
									mJavaBean3.bookIdList.add(templates_id);
									mJavaBean3.bookUpdateTime.add(update_time);
								}
							}
							Message msg = new Message();
							msg.arg1 = refresh;
							msg.arg2 = msgnum;
							msg.what = 0;
							mHandler.sendMessage(msg);
						}else{
							Message msg = new Message();
							msg.what = 1;
							mHandler.sendMessage(msg);
						}
	                }
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}  
			}
		}).start();
	}
	void show(){
		dialog = new commentDialogUtils((UserInfo)mContext, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, R.layout.fragment_1_show,  R.style.Theme_dialog);
		dialog.show();
		tv = (TextView)dialog.findViewById(R.id.fragment_1_tv);
		bnt2 =(Button)dialog. findViewById(R.id.fragment_1_bnt2);
		bnt2.setOnClickListener(this);
		tv.setText("     打开任意一本书，展开菜单，点击”做书模式“即可制作属于自己的书本，保存后会保存到个人中心的个人书架-----专属宝宝的免费高级私人定制！");
	}
	private commentDialogUtils memory_dialog;
	TextView memory_tv;
	Button memory_bnt2;
	void memory_show(){
		memory_dialog = new commentDialogUtils(mContext, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, R.layout.memory_dialog,  R.style.Theme_dialog);
		memory_dialog.show();
		memory_tv = (TextView)memory_dialog.findViewById(R.id.memory_tv);
		memory_bnt2 =(Button)memory_dialog. findViewById(R.id.memory_bnt2);
		memory_bnt2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				memory_dialog.dismiss();
			}
		});
	}
	/*
	 * 删除书本dialog
	 */
}
