package com.starbaby.diyBook.fragment;
/**
 * 主页（幼教）
 */

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.BookShelfAdapter;
import com.starbaby.diyBook.cache.GetCoverCache;
import com.starbaby.diyBook.controller.DeleteCache;
import com.starbaby.diyBook.controller.GetBookCoverInfo;
import com.starbaby.diyBook.controller.ReadSDcard;
import com.starbaby.diyBook.model.CoverThreadPool;
import com.starbaby.diyBook.net.AsyncHttpGet;
import com.starbaby.diyBook.net.DefaultThreadPool;
import com.starbaby.diyBook.net.RequestResultCallback;
import com.starbaby.diyBook.utils.HttpPort;
import com.starbaby.diyBook.utils.JavaBean;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.commentDialogUtils;
import com.starbaby.diyBook.view.PullToRefreshListView;
import com.starbaby.diyBook.view.PullToRefreshListView.OnRefreshListener;

@SuppressLint({ "ValidFragment", "HandlerLeak", "ShowToast" })
public class FragmentS4 extends Fragment{
	private PullToRefreshListView lv ;
	
	Context mContext;
	
	private int page = 2;
	private int lastItem;
	private String section4 = "胎教故事";
	private JavaBean mJavaBean = new JavaBean();
	private BookShelfAdapter mAdapter;
	private boolean bPost = true;
	SharedPreferences sp;
	int outUid = 0;
	TextView msgTv;
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				if(msg.arg1 == 1){
					mAdapter = new BookShelfAdapter(mContext, mJavaBean, section4, 1);
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
				break;
			case 2:
				Toast.makeText(mContext, "连接网络失败", 1000).show();
				break;
			case 12:
				memory_show();
				break;
			}
			super.handleMessage(msg);
		}
	};
	public FragmentS4(){
		
	}
	public FragmentS4(Context mContext,TextView msgTv){
		this.mContext = mContext;
		sp = mContext.getSharedPreferences("diyBook", mContext.MODE_PRIVATE);
		this.msgTv = msgTv;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		LayoutInflater myInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	    View layout = myInflater.inflate(R.layout.bookshelf_gridview4, container, false); 
	    lv = (PullToRefreshListView)layout.findViewById(R.id.gv_lv4);
	    lv.setOnRefreshListener(new OnRefreshListener() {//下拉
            @Override
            public void onRefresh() {
            	/**加载数据，刷新ListView*/
            	getBookInfo(HttpPort.topcateBookUrl + HttpPort.topcate_id_6,1,4,1,section4,false); 
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
				if ((lastItem > new BookShelfAdapter(mContext, mJavaBean, section4, 1).getCount() - 1) || (lastItem == new BookShelfAdapter(mContext, mJavaBean, section4, 1).getCount() - 1)) {
					if(bPost){
						getBookInfo(HttpPort.topcateBookUrl+ HttpPort.topcate_id_6, page, 4, 2, section4, false);
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
	    mAdapter = new BookShelfAdapter(getActivity(), mJavaBean,section4,1);
	    lv.setAdapter(mAdapter);
	    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	    	if(new GetCoverCache(mContext, section4, mJavaBean).getData()){
		    	mAdapter = new BookShelfAdapter(getActivity(), mJavaBean, section4,1);
				lv.setAdapter(mAdapter);
			}else{
				getBookInfo(HttpPort.topcateBookUrl + HttpPort.topcate_id_6 ,1,4,1,section4,false); 
			}
			if(Utils.s6 > 0 && (Utils.s6 % 3) == 0){
				getBookInfo(HttpPort.topcateBookUrl + HttpPort.topcate_id_6 ,1,4,1,section4,true); 
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
								if(Utils.youjiaoBookCache){
									Utils.youjiaoBookCache = false;
									if(!Utils.mDBCacheHelper.saveCache(mSection,bookCoverList.get(j), bookNameList.get(j), bookIdList.get(j),bookUpdateTime.get(j))){
										Utils.youjiaoBookCache = true;
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
									Utils.youjiaoBookCache = true;
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
}


