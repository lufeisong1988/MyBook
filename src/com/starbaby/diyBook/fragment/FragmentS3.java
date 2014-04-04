package com.starbaby.diyBook.fragment;
/**
 * 主页(新书)
 */

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.BookShelfAdapter;
import com.starbaby.diyBook.cache.GetCoverCache;
import com.starbaby.diyBook.controller.DeleteCache;
import com.starbaby.diyBook.controller.ReadSDcard;
import com.starbaby.diyBook.model.CoverThreadPool;
import com.starbaby.diyBook.net.AsyncHttpGet;
import com.starbaby.diyBook.net.DefaultThreadPool;
import com.starbaby.diyBook.net.RequestResultCallback;
import com.starbaby.diyBook.utils.HttpPort;
import com.starbaby.diyBook.utils.JavaBean;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.commentDialogUtils;
import com.starbaby.diyBook.view.EventLinearLayout;
import com.starbaby.diyBook.view.PullToRefreshListView;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "ValidFragment", "ShowToast", "HandlerLeak" })
public class FragmentS3 extends Fragment {
	private PullToRefreshListView lv ;
	
	Context mContext;
	
	private int page = 2;
	private int lastItem;
	private String section3 = "新书";
	private JavaBean mJavaBean = new JavaBean();
	private BookShelfAdapter mAdapter;
	private boolean bPost = true;
	Animation translate_down;
	Intent intent;
	Animation translate_up;ImageView animLeft;ImageView animRight;EventLinearLayout parent;
	SharedPreferences sp;
	int outUid = 0;
	TextView msgTv;
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				if(msg.arg1 == 1){
					mAdapter = new BookShelfAdapter(mContext, mJavaBean, section3, 1);
					lv.setAdapter(mAdapter);
				}else{
					mAdapter.notifyDataSetChanged();
					bPost = true;
				}
				page++;
				if(sp.contains("msgnum")){
					int msgnum = sp.getInt("msgnum", 0);
					if(msgnum == 0){
						msgTv.setVisibility(View.INVISIBLE);
					}else{
						msgTv.setVisibility(View.VISIBLE);
						msgTv.setText(msgnum + "");
					}
				}
				if(Utils.bOpenDoor){
					if(Utils.bOpenApp){
						animation(10);
					}
				}
				break;
			case 2:
				Toast.makeText(mContext, "连接网络失败", 1000).show();
				break;
			case 3:
				animation(10);
				break;
			case 4:
				poster_show();
				break;
			case 12:
				memory_show();
				break;
			}
			super.handleMessage(msg);
		}
	};
	public FragmentS3(){
		
	}
	public FragmentS3(Context mContext,Animation translate_down,
	 Animation translate_up,ImageView animLeft,ImageView animRight,EventLinearLayout parent,TextView msgTv){
		this.mContext = mContext;
		this.animLeft = animLeft;
		this.animRight = animRight;
		this.parent = parent;
		sp = mContext.getSharedPreferences("diyBook", mContext.MODE_PRIVATE);
		this.msgTv = msgTv;
		
	}
	ServiceConnection conn;
	public FragmentS3(Context mContext,Animation translate_down,
			 Animation translate_up,ImageView animLeft,ImageView animRight,EventLinearLayout parent,Intent intent,ServiceConnection conn,TextView msgTv){
		this.conn = conn;
				this.mContext = mContext;
				this.animLeft = animLeft;
				this.animRight = animRight;
				this.parent = parent;
				this.intent = intent;
				this.msgTv = msgTv;
				sp = mContext.getSharedPreferences("diyBook", mContext.MODE_PRIVATE);
			}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		LayoutInflater myInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	    View layout = myInflater.inflate(R.layout.bookshelf_gridview3, container, false); 
	    lv = (PullToRefreshListView)layout.findViewById(R.id.gv_lv3);
	    lv.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {//下拉
            @Override
            public void onRefresh() {
            	/**加载数据，刷新ListView*/
            	getBookInfo(HttpPort.NewBookUrl,1,3,1,section3,false);
            	new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						lv.onRefreshComplete();
					}
				}, 3000);
            }
        });
        
	    lv.setOnScrollListener(new PullToRefreshListView.OnScrollListener() {//上拉
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if ((lastItem > new BookShelfAdapter(mContext, mJavaBean, section3, 1).getCount() - 1) || (lastItem == new BookShelfAdapter(mContext, mJavaBean, section3, 1).getCount() - 1)) {
					if(bPost){
						getBookInfo(HttpPort.NewBookUrl,page,3,2,section3,false); 
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
	    mAdapter = new BookShelfAdapter(getActivity(), mJavaBean,section3,1);
	    lv.setAdapter(mAdapter);
	    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	    	if(new GetCoverCache(mContext, section3, mJavaBean).getData()){
				mAdapter = new BookShelfAdapter(getActivity(), mJavaBean, section3,1);
				lv.setAdapter(mAdapter);
				if(Utils.bOpenDoor){
					if(Utils.bOpenApp){
						animation(6000);
					}
				}
			}else{
				getBookInfo(HttpPort.NewBookUrl,1,3,1,section3,false); 
			}
			if(Utils.sNew > 0 && (Utils.sNew % 3) == 0){
				getBookInfo(HttpPort.NewBookUrl,1,3,1,section3,true); 
			}
			
	    }else{
	    	Toast.makeText(mContext, "请插入SdCard卡", 1000).show();
	    }
		return layout;
	}
	/**
	 * 获取分类下的书本信息
	 * @param Url
	 * @param countPage
	 * @param flag
	 * @param refresh 1:下拉刷新 保存改页面的缓存，便于下一次打开直接显示（增加用户体验），2：上拉更多
	 */
	public void getBookInfo(final String Url, final int countPage, final int flag, final int refresh,final String mSection,final boolean sCount ) {
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
		outUid = sp.getInt("uid", 0);
		AsyncHttpGet get = new AsyncHttpGet(null, Url + "/" + countPage + "/" + outUid, null,new RequestResultCallback() {

			@SuppressWarnings("static-access")
					public void onSuccess(Object o) {
						String result = (String) o;
						Log.i("result",result);
						JSONArray array = null;
						String msgNum = "0";
						if(refresh == 1){
							ArrayList<String> bookCoverList = new ArrayList<String>();
							ArrayList<String> bookNameList = new ArrayList<String>();
							ArrayList<String> bookIdList = new ArrayList<String>();
							ArrayList<String> bookUpdateTime = new ArrayList<String>();
							mJavaBean.bookCoverList = new ArrayList<String>();
							mJavaBean.bookNameList = new ArrayList<String>();
							mJavaBean.bookIdList = new ArrayList<String>();
							mJavaBean.bookUpdateTime = new ArrayList<String>();
							try {
								JSONObject object = new JSONObject(result);
								msgNum = object.getString("msgnum");
								if(flag == 3){
									array = object.getJSONArray("newbooks");
								}else {
									array = object.getJSONArray("topcate_books");
								}
								for (int i = 0; i < array.length(); i++) {
									JSONObject books = array.getJSONObject(i);
									String templates_id = books.getString("templates_id");
									String templates_name = books.getString("templates_name");
									String thumb_img = books.getString("thumb_img");
									String update_time = books.getString("update_time");
									mJavaBean.bookCoverList.add(thumb_img);
									mJavaBean.bookNameList.add(templates_name);
									mJavaBean.bookIdList.add(templates_id);
									mJavaBean.bookUpdateTime.add(update_time);
									bookCoverList.add(thumb_img);
									bookNameList.add(templates_name);
									bookIdList.add(templates_id);
									bookUpdateTime.add(update_time);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
							
							new CoverThreadPool(mContext, bookCoverList, mSection).saveCover();
							Utils.mDBCacheHelper.deleteSection(mSection);
							for(int j = 0;j < bookCoverList.size();j++){
								if(Utils.newBookCache){
									Utils.newBookCache = false;
									if(!Utils.mDBCacheHelper.saveCache(mSection,bookCoverList.get(j), bookNameList.get(j), bookIdList.get(j),bookUpdateTime.get(j))){
										Utils.newBookCache = true;
										if(sCount){
											if(mSection.equals("宝宝益智")){
												Utils.s1--;
											}else if(mSection.equals("精品绘本")){
												Utils.s2--;
											}else if(mSection.equals("早教特色")){
												Utils.s3--;
											}else if(mSection.equals("宝宝成长")){
												Utils.s4--;
											}else if(mSection.equals("胎教故事")){
												Utils.s5--;
											}else if(mSection.equals("幼儿教案")){
												Utils.s6--;
											}else if(mSection.equals("幼儿学习")){
												Utils.s7--;
											}else if(mSection.equals("儿童故事")){
												Utils.s8--;
											}else if(mSection.equals("品牌故事")){
												Utils.s9--;
											}else if(mSection.equals("专题产品")){
												Utils.s10--;
											}else if(mSection.equals("新书")){
												Utils.sNew--;
											}
											break;
										}
									}
									Utils.newBookCache = true;
								}
								
							}
						}else{
							try {
								JSONObject object = new JSONObject(result);
								msgNum = object.getString("msgnum");
								if(flag == 3){
									array = object.getJSONArray("newbooks");
								}else {
									array = object.getJSONArray("topcate_books");
								}
								for (int i = 0; i < array.length(); i++) {
									JSONObject books = array.getJSONObject(i);
									String templates_id = books.getString("templates_id");
									String templates_name = books.getString("templates_name");
									String thumb_img = books.getString("thumb_img");
									String update_time = books.getString("update_time");
									mJavaBean.bookCoverList.add(thumb_img);
									mJavaBean.bookNameList.add(templates_name);
									mJavaBean.bookIdList.add(templates_id);
									mJavaBean.bookUpdateTime.add(update_time);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						
						SharedPreferences.Editor editor = sp.edit();
						editor.putInt("msgnum", Integer.parseInt(msgNum));
						editor.commit();
						Message msg = new Message();
						msg.arg1 = refresh;
						msg.what = 1;
						mHandler.sendMessage(msg);
					}
					@Override
					public void onFail(Exception e) {
						Log.i("GetBookCoverInfo",e.toString());
						Message msg = new Message();
						msg.what = 2;
						mHandler.sendMessage(msg);
						bPost = true;
					}
				});
		DefaultThreadPool.getInstance().execute(get);
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
	void animation(long time){
		translate_down = AnimationUtils.loadAnimation(mContext, R.anim.translate_right);
		translate_up = AnimationUtils.loadAnimation(mContext,R.anim.translate_left);
		translate_up.setFillEnabled(true);
		translate_up.setFillAfter(true);
		translate_down.setFillEnabled(true);
		translate_down.setFillAfter(true);
		translate_up.setStartOffset(time);
		translate_down.setStartOffset(time);
		animLeft.setAnimation(translate_up);
		animRight.setAnimation(translate_down);
		translate_down.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				parent.setbTrue(true);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				parent.setbTrue(false);
				if(Utils.bOpenDoor){
					Message msg = new Message();
					msg.what = 4;
					mHandler.sendMessage(msg);
				}
				if(!Utils.bOpenDoor){
					parent.setbTrue(true);
				}
				Utils.bOpenDoor = false;
				animLeft.setClickable(false);
				animRight.setClickable(false);
			}
		});
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				animLeft.setClickable(false);
				animRight.setClickable(false);
				if (Utils.bOpenDoor) {
					mContext.bindService(intent, conn, mContext.BIND_AUTO_CREATE);
				}
			}
		}, time);
		animLeft.startAnimation(translate_up);
		animRight.startAnimation(translate_down);
		new Thread(new volumeThread()).start();
	}
	private commentDialogUtils poster_dialog;
	TextView poster_tv;
	Button poster_bnt2;
	void poster_show(){
		poster_dialog = new commentDialogUtils(mContext, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, R.layout.show_poster,  R.style.Theme_dialog);
		poster_dialog.show();
		poster_bnt2 =(Button)poster_dialog. findViewById(R.id.poster_bnt2);
		poster_bnt2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				poster_dialog.dismiss();
			}
		});
	}
	class volumeThread implements Runnable {

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				if(!Utils.bOpenDoor){
					translate_down.cancel();
					translate_up.cancel();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
	
}
