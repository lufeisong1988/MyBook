package com.starbaby.diyBook.adapter;
/**
 * 各个书籍分类下
 */
import java.util.ArrayList;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.controller.GetPosition;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.main.BookSection;
import com.starbaby.diyBook.open.BookUtils;
import com.starbaby.diyBook.utils.ImageLoader;
import com.starbaby.diyBook.utils.JavaBean;
import com.starbaby.diyBook.utils.Utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class BookShelfAdapter2 extends BaseAdapter{
	private Context mContext;
	private LayoutInflater inflate;
	public ArrayList<String> bookIdList;
	public ArrayList<String> bookCoverList;
	public ArrayList<String> bookNameList;
	public ArrayList<String> bookUpdateTime;
	private JavaBean mJavaBean;
	private ImageLoader imageLoader;
	private String section;
	private int refresh;
	DisplayMetrics  dm;
	int minHeight,minWidth ;
	private NamePic getPicName ;
	private Bitmap bit = null;
	private Animation alphaAnimation = null; 
	public BookShelfAdapter2(Context mContext,JavaBean mJavaBean,String section,int refresh){
		this.mContext = mContext;
		this.mJavaBean = mJavaBean;
		this.refresh = refresh;
		this.section = section;
		this.bookIdList = mJavaBean.bookIdList;
		this.bookCoverList = mJavaBean.bookCoverList;
		this.bookNameList = mJavaBean.bookNameList;
		this.bookUpdateTime = mJavaBean.bookUpdateTime;
		inflate = LayoutInflater.from(mContext);
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		minHeight = (dm.heightPixels - BookUtils.dip2px(mContext, 60)) / 2;
		minWidth = (dm.widthPixels - BookUtils.dip2px(mContext, 240));
		imageLoader=new ImageLoader(mContext.getApplicationContext(),minHeight,minWidth,4);
		getPicName = new NamePic();
		alphaAnimation = AnimationUtils.loadAnimation(mContext,R.anim.imageview_aplah);
		alphaAnimation.setFillEnabled(true);// 启动Fill保持
		alphaAnimation.setFillAfter(true);//设置动画的最后一帧是保留在view上的  
	}
	@Override
	public int getCount() {
		if((int)Math.ceil((float)bookCoverList.size() / 4) > 1 ){
			return (int)Math.ceil((float)bookCoverList.size() / 4) + 1;
		}else{
			return 3;
		}
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (position == getCount() - 1) {
			View view =inflate.inflate(R.layout.listfooter_more, null);
			return view;
		}
		convertView = inflate.inflate(R.layout.shelf_list_item, null);
		final ImageView iv1 =(ImageView)convertView. findViewById(R.id.button_1);
		final ImageView iv2 =(ImageView)convertView. findViewById(R.id.button_2);
		final ImageView iv3 =(ImageView)convertView. findViewById(R.id.button_3);
		final ImageView iv4 =(ImageView)convertView. findViewById(R.id.button_4);
		iv1.setAnimation(alphaAnimation);
		iv2.setAnimation(alphaAnimation);
		iv3.setAnimation(alphaAnimation);
		iv4.setAnimation(alphaAnimation);
		ImageView left = (ImageView)convertView. findViewById(R.id.shelf_image_left);
		ImageView right = (ImageView)convertView. findViewById(R.id.shelf_image_right);
		LinearLayout ll = (LinearLayout)convertView. findViewById(R.id.linearLayout1);
		left.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,minHeight));
		right.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,minHeight));
		ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,minHeight));
		final LinearLayout contain1 =(LinearLayout)convertView. findViewById(R.id.ll1);
		final LinearLayout contain2 =(LinearLayout)convertView. findViewById(R.id.ll2);
		final LinearLayout contain3 =(LinearLayout)convertView. findViewById(R.id.ll3);
		final LinearLayout contain4 =(LinearLayout)convertView. findViewById(R.id.ll4);
		if(position > 0){
			left.setBackgroundResource(R.drawable.bookshelf_layer_left_22);
			right.setBackgroundResource(R.drawable.bookshelf_layer_right_22);
			ll.setBackgroundResource(R.drawable.bookshelf_layer_center_22);
		}
		if(position == 0 || position ==1){
			contain1.setBackgroundResource(R.drawable.blank_book);
			contain2.setBackgroundResource(R.drawable.blank_book);
			contain3.setBackgroundResource(R.drawable.blank_book);
			contain4.setBackgroundResource(R.drawable.blank_book);
		}
		if(4*position+1 < bookCoverList.size() || 4*position+1 == bookCoverList.size()){
			bit = BitmapFactory.decodeFile(Utils.coverPath + section + "/" + getPicName.convertUrlToFileName(bookCoverList.get(4*position+0)));
			if(bit != null){
				new GetPosition(minHeight,minWidth,bit.getHeight(),bit.getWidth(),iv1,4);
				iv1.setImageBitmap(bit);
				contain1.setBackgroundResource(R.drawable.blank_book2);
			}else{
				if(bookCoverList.get(4*position+0) != null && !bookCoverList.get(4*position+0).equals("")){
					imageLoader.DisplayImage(bookCoverList.get(4*position+0),iv1);
					contain1.setBackgroundResource(R.drawable.blank_book2);
				}else{
					iv1.setBackgroundResource(R.drawable.blank_book);
					contain1.setBackgroundResource(0);
				}
			}
		}else{
			if(position > 1){
				contain1.setVisibility(4);
			}
		}
		if(4*position+2 < bookCoverList.size() || 4*position+2 == bookCoverList.size()){
			bit = BitmapFactory.decodeFile(Utils.coverPath + section + "/" + getPicName.convertUrlToFileName(bookCoverList.get(4*position+1)));
			if(bit != null){
				new GetPosition(minHeight,minWidth,bit.getHeight(),bit.getWidth(),iv2,4);
				iv2.setImageBitmap(bit);
				contain2.setBackgroundResource(R.drawable.blank_book2);
			}else{
				if(bookCoverList.get(4*position+1) != null && !bookCoverList.get(4*position+1).equals("")){
					imageLoader.DisplayImage(bookCoverList.get(4*position+1),iv2);
					contain2.setBackgroundResource(R.drawable.blank_book2);
				}else{
					iv2.setBackgroundResource(R.drawable.blank_book);
					contain2.setBackgroundResource(0);
				}
			}
		}else{
			if(position > 1){
				contain2.setVisibility(4);
			}
		}
		if(4*position+3 < bookCoverList.size() || 4*position+3 == bookCoverList.size()){
			bit = BitmapFactory.decodeFile(Utils.coverPath + section + "/" + getPicName.convertUrlToFileName(bookCoverList.get(4*position+2)));
			if(bit != null){
				new GetPosition(minHeight,minWidth,bit.getHeight(),bit.getWidth(),iv3,4);
				iv3.setImageBitmap(bit);
				contain3.setBackgroundResource(R.drawable.blank_book2);
			}else{
				if(bookCoverList.get(4*position+2) != null && !bookCoverList.get(4*position+2).equals("")){
					imageLoader.DisplayImage(bookCoverList.get(4*position+2),iv3);
					contain3.setBackgroundResource(R.drawable.blank_book2);
				}else{
					iv3.setBackgroundResource(R.drawable.blank_book);
					contain3.setBackgroundResource(0);
				}
			}
		}else{
			if(position > 1){
				contain3.setVisibility(4);
			}
		}
		if(4*position+4 < bookCoverList.size() || 4*position+4 == bookCoverList.size()){
			bit = BitmapFactory.decodeFile(Utils.coverPath + section + "/" + getPicName.convertUrlToFileName(bookCoverList.get(4*position+3)));
			if(bit != null){
				new GetPosition(minHeight,minWidth,bit.getHeight(),bit.getWidth(),iv4,4);
				iv4.setImageBitmap(bit);
				contain4.setBackgroundResource(R.drawable.blank_book2);
			}else{
				if(bookCoverList.get(4*position+3) != null && !bookCoverList.get(4*position+3).equals("")){
					imageLoader.DisplayImage(bookCoverList.get(4*position+3),iv4);
					contain4.setBackgroundResource(R.drawable.blank_book2);
				}else{
					iv4.setBackgroundResource(R.drawable.blank_book);
					contain4.setBackgroundResource(0);
				}
			}
		}else{
			if(position > 1){
				contain4.setVisibility(4);
			}
		}
		iv1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				int[] location = new int[2];
				iv1.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				if(!Utils.isFastDoubleClick()){
					((BookSection)mContext).loading(bookIdList.get(4*position+0),contain1,iv1,x,y,iv1.getWidth(),iv1.getHeight(),bookUpdateTime.get(4*position+0),bookNameList.get(4*position + 0),bookCoverList.get(4*position + 0),false);
				}
			}
		});
		iv2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int[] location = new int[2];
				iv2.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				if(!Utils.isFastDoubleClick()){
					((BookSection)mContext).loading(bookIdList.get(4*position+1),contain2,iv2,x,y,iv2.getWidth(),iv2.getHeight(),bookUpdateTime.get(4*position+1),bookNameList.get(4*position + 1),bookCoverList.get(4*position + 1),false);
				}
			}
		});
		iv3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int[] location = new int[2];
				iv3.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				if(!Utils.isFastDoubleClick()){
					((BookSection)mContext).loading(bookIdList.get(4*position+2),contain3,iv3,x,y,iv3.getWidth(),iv3.getHeight(),bookUpdateTime.get(4*position+2),bookNameList.get(4*position + 2),bookCoverList.get(4*position + 2),false);
				}
			}
		});
		iv4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int[] location = new int[2];
				iv4.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				if(!Utils.isFastDoubleClick()){
					((BookSection)mContext).loading(bookIdList.get(4*position+3),contain4,iv4,x,y,iv4.getWidth(),iv4.getHeight(),bookUpdateTime.get(4*position+3),bookNameList.get(4*position + 3),bookCoverList.get(4*position + 3),false);
				}
			}
		});
		iv1.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				int[] location = new int[2];
				iv1.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				if(!Utils.isFastDoubleClick()){
					((BookSection)mContext).loading(bookIdList.get(4*position+0),contain1,iv1,x,y,iv1.getWidth(),iv1.getHeight(),bookUpdateTime.get(4*position+0),bookNameList.get(4*position + 0),bookCoverList.get(4*position + 0),true);
				}
				return true;
			}
		});
		iv2.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				int[] location = new int[2];
				iv2.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				if(!Utils.isFastDoubleClick()){
					((BookSection)mContext).loading(bookIdList.get(4*position+1),contain2,iv2,x,y,iv2.getWidth(),iv2.getHeight(),bookUpdateTime.get(4*position+1),bookNameList.get(4*position + 1),bookCoverList.get(4*position + 1),true);
				}
				return true;
			}
		});
		iv3.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				int[] location = new int[2];
				iv3.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				if(!Utils.isFastDoubleClick()){
					((BookSection)mContext).loading(bookIdList.get(4*position+2),contain3,iv3,x,y,iv3.getWidth(),iv3.getHeight(),bookUpdateTime.get(4*position+2),bookNameList.get(4*position + 2),bookCoverList.get(4*position + 2),true);
				}
				return true;
			}
		});
		iv4.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				int[] location = new int[2];
				iv4.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				if(!Utils.isFastDoubleClick()){
					((BookSection)mContext).loading(bookIdList.get(4*position+3),contain4,iv4,x,y,iv4.getWidth(),iv4.getHeight(),bookUpdateTime.get(4*position+3),bookNameList.get(4*position + 3),bookCoverList.get(4*position + 3),true);
				}
				return true;
			}
		});
		return convertView;
	}
		

}
