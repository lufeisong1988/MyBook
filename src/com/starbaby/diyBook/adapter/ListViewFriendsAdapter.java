package com.starbaby.diyBook.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientbean.Comment;
import com.starbaby.diyBook.clientbean.FriendPost;
import com.starbaby.diyBook.clientcommon.FaceConversionUtil;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.clientui.FriendsActivity;
import com.starbaby.diyBook.clientwidget.CollapsibleTextView;
import com.starbaby.diyBook.main.UserInfo;
 

public class ListViewFriendsAdapter extends BaseAdapter {
	private HashMap<Integer,Integer>          hashMap;
	private FriendsActivity 		context;
	private List<FriendPost> 		listItems;
	private LayoutInflater  listCountainer;
	private int 			itemViewResource;
	private ImageLoader		imageLoader;
	private int 			uid;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				Intent intent = (Intent) msg.obj;
				context.startActivity(intent);
				context.overridePendingTransition(R.anim.workbook_in,R.anim.none);
				break;
			}
			super.handleMessage(msg);
		}
	};
	//回调函数
	public interface CallShowInterface{
		public void  showSoft(int position,int tid,int pid,String nickName);
	}
 
	static class ListItemView{
		public ImageView   avatarImgBtn;
		public TextView    nickName;
		public CollapsibleTextView    descContent;
		public TextView    publishTime;
		public TextView    delTv;
		public LinearLayout imgLL;
		public LinearLayout replyFather;
		public LinearLayout replyLL;
		public ImageButton  showComment;
	}
	
	public ListViewFriendsAdapter(FriendsActivity context,List<FriendPost> data,int resource,int uid,ImageLoader imageLoader){
		this.hashMap = new HashMap<Integer,Integer>();
		this.context = context;
		this.listCountainer = LayoutInflater.from(context);
		this.itemViewResource = resource;
		this.listItems = data;
		this.uid = uid;
		this.imageLoader = imageLoader;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		//自定义视图
		ListItemView listItemView = null;
		if(convertView==null){
			//获取list_item布局文件布局
			convertView = listCountainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取控件对象
			listItemView.avatarImgBtn = (ImageView)convertView.findViewById(R.id.album_avatar_iv);
			listItemView.nickName = (TextView)convertView.findViewById(R.id.album_nickname_tv);
			listItemView.descContent = (CollapsibleTextView)convertView.findViewById(R.id.album_desc_tv);
			listItemView.delTv = (TextView)convertView.findViewById(R.id.album_del);
			listItemView.publishTime = (TextView)convertView.findViewById(R.id.album_publish_time);
			listItemView.imgLL = (LinearLayout)convertView.findViewById(R.id.album_images_li);
			listItemView.replyFather = (LinearLayout)convertView.findViewById(R.id.album_list_fatherview);
			listItemView.replyLL = (LinearLayout)convertView.findViewById(R.id.album_comment_list_li);
			listItemView.showComment = (ImageButton)convertView.findViewById(R.id.album_show_comment_tv);
			
			convertView.setTag(listItemView);
		}else{
			listItemView = (ListItemView)convertView.getTag();
		}
		
		final FriendPost post = listItems.get(position);
		
		imageLoader.displayImage(post.getAvatarurl(), listItemView.avatarImgBtn);
		listItemView.avatarImgBtn.setTag(post);
		listItemView.avatarImgBtn.setOnClickListener(new OnClickListener() {
			/*头像的监听事件
			 * 1.如果uid 和 guest_id 不同。  查看别人，只能看到别人完整 且 公开的作品。
			 * 2.如果uid 和 guest_id 相同， 查看自己，能看到所有自己的作品。
			 * (non-Javadoc)
			 * @see android.view.View.OnClickListener#onClick(android.view.View)
			 */
			@Override
			public void onClick(View v) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						int uid = post.getAuthorid();//当前头像的uid
						String avater = post.getAvatarurl();//获取头像图片url(指获取他人的)
						String name = post.getAuthor();//他人的name
						SharedPreferences sp = context.getSharedPreferences("diyBook", context.MODE_WORLD_WRITEABLE);
						int guest_id = sp.getInt("uid", 0);//当前登入用户的uid
						Intent intent = new Intent(context,UserInfo.class);
						Bundle bundle = new Bundle();
						if(uid == guest_id){//查看自己个人中心
							bundle.putBoolean("bMyshelf", true);
							bundle.putInt("uid",0);
						}else{//查看他人个人中心
							bundle.putBoolean("bMyshelf", false);
							bundle.putInt("uid", uid);
							bundle.putString("avater", avater);
							bundle.putString("name", name);
						}
						intent.putExtras(bundle);
						Message msg = new Message();
						msg.what = 1;
						msg.obj = intent;
						mHandler.sendMessage(msg);
						
					}
				}).start();
				
			}
		});
		listItemView.nickName.setText(post.getAuthor());
		listItemView.descContent.setDesc(FaceConversionUtil.convetToHtml(post.getMessage(),context),BufferType.NORMAL,hashMap,position); //post.getMessage()
		listItemView.descContent.onOpClick(descOpClickListener(post.getMessage(),listItemView,position));
		listItemView.descContent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("listItemView.descContent","onClick");
			}
		});
		listItemView.imgLL.removeAllViews();
		if(post.getPicCount()>0){
			listItemView.imgLL.setVisibility(View.VISIBLE);
    	    final  ArrayList<String>   smallImages = post.getSmallPics();
    	    final  ArrayList<String>   bigImages =   post.getBigPics();
    	    int k=0,m=0;
			for(int i=0;i<post.getPicCount();i++){
				if(i%3==0) 
				  { 
				    View view = View.inflate(context, R.layout.message_detail_imagesline, null);
					listItemView.imgLL.addView(view);
					k=k+1;	
					m=0;
				  }
				
				ImageView imgView = (ImageView)((LinearLayout)listItemView.imgLL.getChildAt(k-1)).getChildAt(m++);
				imageLoader.displayImage(smallImages.get(i), imgView);
				imgView.setVisibility(View.VISIBLE);
				final int index = i;
				imgView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						UIHelper.showImageViewPager(context, smallImages,bigImages,index);	 
					}
				});
			}
		} else {
			listItemView.imgLL.setVisibility(View.GONE);
		}
		
		
		
		 
		listItemView.publishTime.setText(post.getDateline());
		if(post.getAuthorid()==uid){
			listItemView.delTv.setVisibility(View.VISIBLE);
			listItemView.delTv.setOnClickListener(delSnsItem(post.getTid(),position));
		}else{
			listItemView.delTv.setVisibility(View.GONE);
		}
		
		listItemView.replyLL.removeAllViews();
		if(post.getComments().size()>0){
			listItemView.replyFather.setVisibility(View.VISIBLE);
			for(Comment comment:post.getComments()){
				TextView comTv = new TextView(context);
				comTv.setBackgroundResource(R.drawable.sns_comment_detail_item_bg);
				comTv.setText(UIHelper.parseFriendPostpan(comment.getAuthor(),FaceConversionUtil.convetToHtml(comment.getMessage(),context)));//comment.getMessage()
				comTv.setOnClickListener(showKeyWordReply(listItemView,position,post.getTid(),comment.getPid(),comment.getAuthor()));
				listItemView.replyLL.addView(comTv);
			}
		}else{
			listItemView.replyFather.setVisibility(View.GONE);
		}
		
		listItemView.showComment.setOnClickListener(showKeyWordComment(listItemView,position,post.getTid()));
		listItemView.showComment.setTag(position);
		return convertView;
	}
	
	private View.OnClickListener descOpClickListener(final CharSequence charSequence,final ListItemView listItemView,final int position){
		return new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				if(hashMap.get(position)==1){
					hashMap.put(position, 2);
				} else if(hashMap.get(position)==2){
					hashMap.put(position, 1);
				}
				listItemView.descContent.setDesc(charSequence, BufferType.NORMAL, hashMap, position);
			}
			
		};
	}
	
	/**
	 * 显示底部输入对话框和软盘
	 * @param listItemView
	 * @param position
	 * @param tid
	 * @return
	 */
	private View.OnClickListener showKeyWordComment(final ListItemView listItemView,final int position,final int tid) {
			return new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					context.showSoft(position,tid,0,"");
				}
			};
	}
	private View.OnClickListener showKeyWordReply(final ListItemView listItemView,final int position,final int tid,final int pid,final String nickName) {
		return new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				context.showSoft(position,tid,pid,nickName);
			}
		};
	}

	private View.OnClickListener delSnsItem(final int tid,final int position){
			return new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					UIHelper.showSnsDelDialog(context, tid, position);
				}
		};
	}
}
