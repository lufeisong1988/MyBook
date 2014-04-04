package com.starbaby.diyBook.adapter;

import java.util.ArrayList;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientbean.Comment;
import com.starbaby.diyBook.clientcommon.FaceConversionUtil;
import com.starbaby.diyBook.clientcommon.UIHelper;
 

 
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 用户留言详情Adapter类
 * @author stone(fanlei123126@163.com)
 * @version 1.0
 * @created 2013-3-29
 */
public class ListViewCommentAdapter extends BaseAdapter {
	private Context 					context;//运行上下文
	private ArrayList<Comment> 			listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源
	private  ImageLoader				imageLoader;
//	private DisplayImageOptions 		options;
	//private BitmapManager 				bmpManager;
	static class ListItemView{				//自定义控件集合  
			public ImageView userface;
		 	public TextView username;  
/*		 	public TextView content;
*/		    public TextView date;  
	 }  

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewCommentAdapter(Context context, ArrayList<Comment> data,int resource,ImageLoader imageLoader) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
	//	this.options = options;
		//this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_dface_loading));
	}
	
	public int getCount() {
		return listItems.size();
	}

	public Object getItem(int arg0) {
		return null;
	}

	public long getItemId(int arg0) {
		return 0;
	} 
 
	   
	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("method", "getView");
		
		//自定义视图
		ListItemView  listItemView = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取控件对象
			listItemView.userface = (ImageView)convertView.findViewById(R.id.message_detail_listitem_userface);
 			listItemView.username = (TextView)convertView.findViewById(R.id.message_detail_listitem_username);
 			//listItemView.content = (TextView)convertView.findViewById(R.id.message_detail_listitem_content);       					 
			listItemView.date = (TextView)convertView.findViewById(R.id.message_detail_listitem_date);
		/*	listItemView.contentll = (LinearLayout)convertView.findViewById(R.id.messagedetail_listitem_contentll);
			listItemView.client= (TextView)convertView.findViewById(R.id.messagedetail_listitem_client);*/
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		
		//设置文字和图片
		 Comment comment = listItems.get(position); 
		 listItemView.username.setText( UIHelper.parsePostpan(comment.getAuthor(),FaceConversionUtil.convetToHtml(comment.getMessage(),context)));// comment.getAuthor(),comment.getMessage(),"")
		 listItemView.username.setTag(comment);
		 listItemView.date.setText(comment.getDateline());
		 listItemView.userface.setTag(comment);
		// listItemView.userface.setOnClickListener(faceClickListener);
	//	String faceURL =URLs.FCAE_URL_SMALL+comment.getAuthorid();
		//AppContext ac = (AppContext)context.getApplicationContext();
	/*	if(comment.getAuthorid()<=0){
			listItemView.userface.setImageResource(R.drawable.widget_dface);
		}else{
			 
			
		}
		*/
		 if(imageLoader==null){
			 imageLoader =ImageLoader.getInstance();
		 }
		 imageLoader.displayImage(comment.getAvatarurl(), listItemView.userface);
		 return convertView;
	}
	//用户个人中心
/*	private View.OnClickListener faceClickListener = new View.OnClickListener(){
		public void onClick(View v) {
			Comment comment = (Comment)v.getTag();
			UIHelper.showPersonalCenter(v.getContext(),comment.getAuthorid());
		}
	};*/
}