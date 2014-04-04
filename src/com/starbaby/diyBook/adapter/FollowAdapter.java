package com.starbaby.diyBook.adapter;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;
import com.starbaby.diyBook.clientbean.Fans;
import com.starbaby.diyBook.clientcommon.UIHelper;

public class FollowAdapter extends BaseAdapter {
	private Context mContext;
	private List<Fans> listItems;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	private ProgressDialog mProgress;
	public FollowAdapter(Context context,List<Fans> data,ImageLoader imageLoader){
		this.mContext = context;
		this.listItems = data;
		this.inflater = LayoutInflater.from(context);
		this.imageLoader = imageLoader;
	}
	
	static class ListItemView{
		public ImageView avatar;
		public TextView  name;
		public ImageView attention;
	}
	
	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup  parent) {
		ListItemView itemView = null;
		if(convertView==null){
			itemView = new ListItemView();
			convertView = inflater.inflate(R.layout.follow_item, null);
			itemView.avatar = (ImageView)convertView.findViewById(R.id.follow_item_avatar);
			itemView.name = (TextView)convertView.findViewById(R.id.follow_item_name);
			itemView.attention = (ImageView)convertView.findViewById(R.id.follow_item_attention);
			
			convertView.setTag(itemView);
		}else{
			itemView =(ListItemView)convertView.getTag();
		}
		
		Fans follow = listItems.get(position);
		 System.out.println("follow==="+follow.getAvatar());
		imageLoader.displayImage(follow.getAvatar(),itemView.avatar);
		itemView.name.setText(follow.getUserName());
		
		itemView.attention.setTag(follow);
		itemView.attention.setOnClickListener(attentionClickListener);
		return convertView;
	}
	

	private View.OnClickListener attentionClickListener =new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mProgress = ProgressDialog.show(mContext, "取消关注", "操作中···",true,false); 
						final Fans follow =(Fans)v.getTag();
						final Handler handler = new Handler(){
							public void handleMessage(Message msg){
								if(mProgress!=null)mProgress.dismiss();
								if(msg.what==1){
									listItems.remove(follow);
									FollowAdapter.this.notifyDataSetChanged();
									UIHelper.ToastMessage(mContext, "取消成功");
								}else{
									UIHelper.ToastMessage(mContext, "取消失败，错误代码="+msg.what);
								}
							}
						};
						new Thread(){
							@Override
							public void run(){
								Message msg = new Message();
								AppContext appContext =  (AppContext)mContext.getApplicationContext();
								int result = 0;
								try {
									result = appContext.cancleAttention(appContext.getLoginUid(),appContext.getLoginPwd(),follow.getUid());
								} catch (AppException e) {
									e.printStackTrace();
								}
								msg.what = result;
								handler.sendMessage(msg);
							}
						}.start();
					}
				 };
}
