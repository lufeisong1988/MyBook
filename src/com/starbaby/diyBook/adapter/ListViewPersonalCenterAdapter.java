package com.starbaby.diyBook.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientbean.Post;
import com.starbaby.diyBook.clientcommon.FaceConversionUtil;

public class ListViewPersonalCenterAdapter extends BaseAdapter {
	
	private Context 					context;//运行上下文
	private ArrayList<Post> 			listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private ImageLoader					imageLoader;
	//private BitmapManager 				bmpManager;
	
	static class ListItemView{				//自定义控件集合  
		public ImageView type;
		public TextView content;
	    public TextView date;  
	    public TextView messageCount;
	    public TextView viewCount;
	    public ImageView postTop;
	    public ImageView picture;
	}  
	
	public ListViewPersonalCenterAdapter(Context context,ArrayList<Post> posts,ImageLoader imageLoader){
		super();
		this.context = context;
		this.listContainer = LayoutInflater.from(context);
		this.listItems = posts;
		this.imageLoader = imageLoader;
		//this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.school_default));
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
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//自定义视图
		ListItemView  listItemView = null;

		if (convertView == null) {
			listItemView = new ListItemView();
				//获取list_item布局文件的视图
				convertView = listContainer.inflate(R.layout.personal_center_list_item_nopic_new, null);
				//获取控件
				listItemView.type  = (ImageView)convertView.findViewById(R.id.personal_center_post_type);
				listItemView.content = (TextView)convertView.findViewById(R.id.personal_listitem_content);
				listItemView.messageCount = (TextView)convertView.findViewById(R.id.personal_listitem_personal_count);
				listItemView.date = (TextView)convertView.findViewById(R.id.personal_listitem_view_date);
				listItemView.viewCount = (TextView)convertView.findViewById(R.id.personal_listitem_view_count);
				listItemView.postTop = (ImageView)convertView.findViewById(R.id.personal_listitem_personal_post_top);
				listItemView.picture = (ImageView)convertView.findViewById(R.id.personal_listitem_content_pic);
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		 
		//设置文字和图片
		Post post = listItems.get(position);
		//System.out.println(post.getSource());
		if(post.getSource().equals("Android客户端")){
			listItemView.type.setBackgroundResource(R.drawable.android);
		} else if(post.getSource().equals("iPhone客户端")){
			listItemView.type.setBackgroundResource(R.drawable.apple);
		} else if(post.getSource().equals("家长圈")){
			listItemView.type.setBackgroundResource(R.drawable.qipao);
		}
		if(post.getDisplayorder()>6){
			listItemView.postTop.setVisibility(View.VISIBLE);
		}else{
			listItemView.postTop.setVisibility(View.GONE);
		}
		listItemView.content.setText(FaceConversionUtil.convetToHtml(post.getMessage(),context));//post.getMessage()
/*		listItemView.content.setTag(post);//设置隐藏参数(实体类)
*/		listItemView.date.setText(post.getDateline());
		listItemView.messageCount.setText(post.getReplies());
		listItemView.viewCount.setText(post.getViews());
	 
		if(post.getPicCount()>0){
			imageLoader.displayImage(post.getPicURL(),  listItemView.picture);
			listItemView.picture.setVisibility(View.VISIBLE);
		}else{
			listItemView.picture.setVisibility(View.GONE);
		}
		return convertView;
	}

}
