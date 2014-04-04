package com.starbaby.diyBook.adapter;

import java.util.ArrayList;

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
import com.starbaby.diyBook.clientbean.Notice;
import com.starbaby.diyBook.clientcommon.FaceConversionUtil;
import com.starbaby.diyBook.clientcommon.UIHelper;
 

public class ListViewNoticeAdapter extends BaseAdapter{
	private Context context;
	private ArrayList<Notice>  listItems;
	private LayoutInflater listContainer;
	private int itemViewResource;
	private ImageLoader imageLoader;
	//private BitmapManager bmpManager;
	private ProgressDialog 	 mProgress;
	
	static class ListItemView{
		public ImageView userface;
		public TextView username;
		public TextView date;
		public TextView content;
		public ImageView attention;
		public TextView reply;
	}
	
	public ListViewNoticeAdapter(Context context,ArrayList<Notice> data,int resource,ImageLoader imageLoader){
		 this.context = context;
		 this.listContainer = LayoutInflater.from(context);
		 this.itemViewResource = resource;
		 this.listItems = data;
		 this.imageLoader = imageLoader;
		// this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_dface_loading));
	}
	
 
	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//自定义视图
		ListItemView listItemView = null;
		if(convertView == null){
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			listItemView = new ListItemView();
			
			//获取控件对象
			listItemView.userface = (ImageView)convertView.findViewById(R.id.active_listitem_userface);
			listItemView.username = (TextView)convertView.findViewById(R.id.active_listitem_username);
			listItemView.content = (TextView)convertView.findViewById(R.id.active_listitem_content);
			listItemView.reply = (TextView)convertView.findViewById(R.id.active_listitem_reply);
			listItemView.attention = (ImageView)convertView.findViewById(R.id.active_listitem_jiagz);
			listItemView.date = (TextView)convertView.findViewById(R.id.active_listitem_date);
			//设置控件集到convertView
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView)convertView.getTag();
		}
		//设置文字和图片
		Notice notice = listItems.get(position);
		/*String faceURL = notice.getAvatar();
		if(StringUtils.isEmpty(faceURL)){
			listItemView.userface.setImageResource(R.drawable.widget_dface);
		} else {
			bmpManager.loadBitmap(faceURL, listItemView.userface);
		}*/
		imageLoader.displayImage(notice.getAvatar(), listItemView.userface);
		
		listItemView.username.setText(notice.getSendUname());
		listItemView.username.setTag(notice);//设置隐藏参数(实体类)
		listItemView.content.setText(FaceConversionUtil.convetToHtml(notice.getMessage(),context)); //notice.getMessage()
		if(notice.getTypeId()<4){
			listItemView.attention.setVisibility(View.GONE);
			listItemView.reply.setVisibility(View.VISIBLE);
			listItemView.reply.setText(FaceConversionUtil.convetToHtml(notice.getRefmsg(),context));  //notice.getRefmsg()
		}else{
			listItemView.reply.setVisibility(View.GONE);
			if(notice.getTypeId()==4){
				listItemView.attention.setTag(notice);
				listItemView.attention.setVisibility(View.VISIBLE);
				listItemView.attention.setOnClickListener(addClickListener);
			}
		}
		
		listItemView.date.setText(notice.getDeteline());
		
		return convertView;
	}
	
	private View.OnClickListener addClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			mProgress = ProgressDialog.show(context, "加关注", "操作中···",true,false); 
			final Notice notice = (Notice)v.getTag();
			final Handler handler = new Handler(){
				public void handleMessage(Message msg){
					if(mProgress!=null)mProgress.dismiss();
					if(msg.what==1){
						delNotice(notice);
						UIHelper.ToastMessage(context, "添加成功");
					}else{
						UIHelper.ToastMessage(context,getErrorInfo(msg.what));
					}
				}
			};
			new Thread(){
				public void run(){
					Message msg = new Message();
					AppContext appContext =  (AppContext)context.getApplicationContext();
					int result  = 0;
					try {
						result = appContext.addFenSi(appContext.getLoginUid(), appContext.getLoginPwd(),notice.getSendUid());
					} catch (AppException e) {
						e.printStackTrace();
						result = -1;
					}
					msg.what=result;
					handler.sendMessage(msg);
				}
			}.start();
		}
	};
	
	private void delNotice(final Notice notice){
	/*	final Handler handler = new Handler(){
			public void handleMessage(Message msg){
				if(msg.what==1){
					listItems.remove(notice);
					ListViewNoticeAdapter.this.notifyDataSetChanged();
				}else{
					UIHelper.ToastMessage(context, "删除消息失败");
				}
			}
		};*/
		new Thread(){
			public void run(){
				Message msg = new Message();
				AppContext appContext =  (AppContext)context.getApplicationContext();
				//int result  = 0;
				try {
					//result = 
					appContext.delNotice(appContext.getLoginUid(), appContext.getLoginPwd(), notice.getId());
				} catch (AppException e) {
					e.printStackTrace();
					//result = -1;
				}
				//msg.what=result;
				//handler.sendMessage(msg);
			}
		}.start();
	}
	
	  
    private String getErrorInfo(final int msg){
		String result = "";
		switch(msg){
			case -2:
				result= context.getString(R.string.msg_opt_user_pwd_error);
				break;
			case -3:
				result=context.getString(R.string.msg_opt_user_uid_is_null);
				break;
			case -4:
				result=context.getString(R.string.msg_opt_user_info_no_exist);
				break;
			case -5:
				result=context.getString(R.string.msg_login_error_five);
				break;
			case -6:
				result=context.getString(R.string.msg_opt_rep_post_id_is_null);
				break;
			case -7:
				result=context.getString(R.string.msg_opt_rep_post_no_exist);
				break;
			case -8:
				result=context.getString(R.string.msg_opt_post_no_right_del_other_post);
				break;
			case -9:
				result= context.getString(R.string.msg_opt_fensi_add_exist);
				break;
			case 0:
				result=context.getString(R.string.msg_opt_post_unknow);
				break;
			case -14:
				result=context.getString(R.string.network_not_connected);
				break;
		}
		return result;
	}
}
