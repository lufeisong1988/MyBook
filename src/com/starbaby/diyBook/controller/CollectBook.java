package com.starbaby.diyBook.controller;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.model.CoverThreadPool3;
import com.starbaby.diyBook.net.AsyncHttpPost;
import com.starbaby.diyBook.net.DefaultThreadPool;
import com.starbaby.diyBook.net.RequestParameter;
import com.starbaby.diyBook.net.RequestResultCallback;
import com.starbaby.diyBook.utils.HttpPort;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.commentDialogUtils;

public class CollectBook {
	Long time;
	Context mContext;
	String uid;
	String psw;
	Button collectBnt;
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				collectBnt.setBackgroundResource(R.drawable.collect2);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				Toast.makeText(mContext, "收藏成功", Toast.LENGTH_LONG).show();
				break;
			case 2:
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				dialog.dismiss();
				Toast.makeText(mContext, "收藏失败", Toast.LENGTH_LONG).show();
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	public CollectBook(Context mContext,String uid,String psw,Button collectBnt){
		this.mContext = mContext;
		this.uid = uid;
		this.psw = psw;
		this.collectBnt = collectBnt;
		collectDialog();
	}
	/*
	 * collect 本地书本
	 */
	public void collectLocalBook(String name,String id,String time,String coverUrl){
		if(!Utils.mDBUserInfoHelper.bCollect(id)){
			
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
			sync_collect(list,name,id,time,coverUrl);
		}
	}
	/*
	 *	同步本地与线上的数据
	 */
	void sync_collect(ArrayList<String> bookIdList,final String mName,final String mId,final String mTime,final String mCoverUrl){
		StringBuffer buffer = new StringBuffer();
		if(bookIdList.size() == 0){
			time = (long) 1000000000;
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
		parameter.add(new RequestParameter("uid", uid));
		parameter.add(new RequestParameter("pwd", psw));
		parameter.add(new RequestParameter("bookids", buffer.toString() + ""));
		parameter.add(new RequestParameter("timestamp",time + ""));
		AsyncHttpPost post = new AsyncHttpPost(null, HttpPort.sync_collect,parameter, new RequestResultCallback() {

					@Override
					public void onSuccess(Object o) {
						String result = (String) o;
						JSONObject object;
						String issync;
						String msg;
						if (result != null && !result.equals("")) {
							Log.i("result", result);
							try {
								object = new JSONObject(result);
								msg = object.getString("msg");
								if(Integer.parseInt(msg) == 1){
									Utils.mDBUserInfoHelper.saveCollectBook(Integer.parseInt(Utils.collect_uid),mName,mId,mTime,mCoverUrl);
									Message msg2 = new Message();
									msg2.what = 1;
									mHandler.sendMessage(msg2);
									issync = object.getString("issync");
									if (Integer.parseInt(issync) == 1) {//本地要同步服务器
										
									}
								}
							} catch (JSONException e1) {
								e1.printStackTrace();
							}

						}
					}

					@Override
					public void onFail(Exception e) {
						Message msg2 = new Message();
						msg2.what = 2;
						mHandler.sendMessage(msg2);
						Log.i("result", e.toString());
					}
				});
		DefaultThreadPool.getInstance().execute(post);
	}
	commentDialogUtils dialog;
	void collectDialog(){
		dialog = new commentDialogUtils(mContext, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT
				, R.layout.collect_dialog, R.style.Theme_dialog);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}
}
