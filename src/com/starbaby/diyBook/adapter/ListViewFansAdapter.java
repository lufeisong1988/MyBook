package com.starbaby.diyBook.adapter;

 

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientbean.Fans;
 
/**
 * 用户留言Adapter类
 * @author stone(fanlei123126@163.com)
 * @version 1.0
 * @created 2013-3-29
 */
public class ListViewFansAdapter extends BaseAdapter {
	private Context 					context;//运行上下文
	private ArrayList<Fans> 				listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private ImageLoader					imageLoader;
	//private BitmapManager 				bmpManager;
	
	static class ListItemView{				//自定义控件集合  
		public ImageView userface;
		public TextView  name;
	 }  

	/**
	 * 实例化Adapter
	 * @param context
	 * @param data
	 * @param resource
	 */
	public ListViewFansAdapter(Context context, ArrayList<Fans> data,ImageLoader imageLoader) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.listItems = data;
		this.imageLoader = imageLoader;
		//this.bmpManager = new BitmapManager(BitmapFactory.decodeResource(context.getResources(), R.drawable.widget_dface_loading));
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
			convertView = listContainer.inflate(R.layout.fans_list, null);
			//获取控件
			listItemView.userface = (ImageView)convertView.findViewById(R.id.fans_list_avatar);
			listItemView.name = (TextView)convertView.findViewById(R.id.fans_list_name);
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}
		//设置文字和图片
		Fans fans = listItems.get(position);
 		listItemView.name.setText(fans.getUserName());
	 
	/*	listItemView.userface.setTag(fans);
		listItemView.userface.setOnClickListener(faceClickListener);*/
		//bmpManager.loadBitmap(fans.getAvatar(), listItemView.userface);
		imageLoader.displayImage(fans.getAvatar(), listItemView.userface);
 		
		return convertView;
	}
 
}