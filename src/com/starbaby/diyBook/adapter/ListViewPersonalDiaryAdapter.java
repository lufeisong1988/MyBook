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
 

/**
 * 用户留言Adapter类
 * @author stone(fanlei123126@163.com)
 * @version 1.0
 * @created 2013-3-29
 */
public class ListViewPersonalDiaryAdapter extends BaseAdapter {
	private Context 					context;//运行上下文
	private ArrayList<Post> 			listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private ImageLoader					imageLoader;
	//private BitmapManager 				bmpManager;

    
	//定义两个int常量标记不同Item视图
	public static final int NO_PIC_ITEM = 0;
	public static final int PIC_ITEM = 1;
	public static final int COUNT_ITEM = 2;
	
	static class ListItemView{				//自定义控件集合  
			public TextView content;
		    public TextView date;  
		    public TextView messageCount;
		    public TextView viewCount;
		    public ImageView picture;
	 }  
	

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewPersonalDiaryAdapter(Context context, ArrayList<Post> data,ImageLoader imageLoader) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.listItems = data;
		this.imageLoader = imageLoader;
		//this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.school_default));
	}
	
	@Override
	public int getViewTypeCount(){
		return COUNT_ITEM;
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
	
	/**
	 * ListView Item设置
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		//自定义视图
		ListItemView  listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
				//获取list_item布局文件的视图
			 convertView = listContainer.inflate(R.layout.personal_center_diary_item, null);
				//获取控件
			 listItemView.content = (TextView)convertView.findViewById(R.id.personal_diary_listitem_content);
			 listItemView.date = (TextView)convertView.findViewById(R.id.personal_diary_listitem_view_date);
			 listItemView.messageCount = (TextView)convertView.findViewById(R.id.personal_diary_listitem_msg_count);
			 listItemView.viewCount = (TextView)convertView.findViewById(R.id.personal_diary_listitem_view_count);
			 listItemView.picture = (ImageView)convertView.findViewById(R.id.personal_diary_listitem_content_pic);
			 
			//设置控件集到convertView
			 convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}

		//设置文字和图片
		Post post = listItems.get(position);

		//初始化回复列表
		listItemView.content.setText(FaceConversionUtil.convetToHtml(post.getMessage(),context));// post.getMessage()
 		listItemView.date.setText(post.getDateline());
	    listItemView.messageCount.setText(post.getReplies());
		listItemView.viewCount.setText(post.getViews());
		
		if(post.getPicCount()>0){
			listItemView.picture.setVisibility(View.VISIBLE);
			imageLoader.displayImage(post.getPicURL(), listItemView.picture);
			//bmpManager.loadBitmap(post.getPicURL(), listItemView.picture);
			listItemView.content.setMaxLines(3);
		}else{
			listItemView.picture.setVisibility(View.GONE);
		}
		return convertView;
	}
}