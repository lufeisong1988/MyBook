package com.starbaby.diyBook.controller;
/**
 * 每次向服务器请求当前页面的书本封面
 */
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.starbaby.diyBook.adapter.BookShelfAdapter;
import com.starbaby.diyBook.adapter.BookShelfAdapter2;
import com.starbaby.diyBook.model.CoverThreadPool;
import com.starbaby.diyBook.net.AsyncHttpGet;
import com.starbaby.diyBook.net.DefaultThreadPool;
import com.starbaby.diyBook.net.RequestResultCallback;
import com.starbaby.diyBook.utils.JavaBean;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.view.PullToRefreshListView;

public class GetBookCoverInfo {
	Context mContext;
	JavaBean mJavaBean;
	PullToRefreshListView lv;
	String section;
	int flag;
	BookShelfAdapter mAdapter;
	BookShelfAdapter2 mAdapter2;
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				if(msg.arg1 == 1){
					if(flag == 1){
						mAdapter = new BookShelfAdapter(mContext,mJavaBean,section,msg.arg1); 
						lv.setAdapter(mAdapter);
					}else if(flag == 2){
						mAdapter2 = new BookShelfAdapter2(mContext,mJavaBean,section,msg.arg1); 
						lv.setAdapter(mAdapter2);
					}
				}else{
					if(flag == 1){
						mAdapter.notifyDataSetChanged();
					}else if(flag == 2){
						mAdapter2.notifyDataSetChanged();
					}
				}
				break;
			case 2:
				Toast.makeText(mContext, "连接网络失败", 1000).show();
				break;
			}
			
			super.handleMessage(msg);
		}
	};
	public GetBookCoverInfo(Context mContext,BookShelfAdapter mAdapter,JavaBean mJavaBean,PullToRefreshListView lv,String section,int flag){
		this.mContext = mContext;
		this.mJavaBean = mJavaBean;
		this.lv = lv;
		this.section = section;
		this.flag = flag;
		this.mAdapter = mAdapter;
	}
	public GetBookCoverInfo(Context mContext,BookShelfAdapter2 mAdapter2,JavaBean mJavaBean,PullToRefreshListView lv,String section,int flag){
		this.mContext = mContext;
		this.mJavaBean = mJavaBean;
		this.lv = lv;
		this.section = section;
		this.flag = flag;
		this.mAdapter2 = mAdapter2;
	}
	/**
	 * 获取分类下的书本信息
	 * @param Url
	 * @param countPage
	 * @param flag
	 * @param refresh 1:下拉刷新 保存改页面的缓存，便于下一次打开直接显示（增加用户体验），2：上拉更多
	 */
	public void getBookInfo(final String Url, final int countPage, final int flag, final int refresh,final String mSection,final boolean sCount ) {
		AsyncHttpGet get = new AsyncHttpGet(null, Url + "/" + countPage, null,new RequestResultCallback() {

			@SuppressWarnings("static-access")
					public void onSuccess(Object o) {
						String result = (String) o;
						if(result != null){
							Log.i("result",result);
						}
						JSONArray array = null;
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
								if(!Utils.mDBCacheHelper.saveCache(mSection,bookCoverList.get(j), bookNameList.get(j), bookIdList.get(j),bookUpdateTime.get(j))){
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
							}
						}else{
							try {
								JSONObject object = new JSONObject(result);
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
					}
				});
		DefaultThreadPool.getInstance().execute(get);
	}
	
}
