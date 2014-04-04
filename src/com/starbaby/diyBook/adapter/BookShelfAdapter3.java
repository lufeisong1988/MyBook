package com.starbaby.diyBook.adapter;
/**
 * 个人中心（书架）
 * 我的作品   已读书籍   精选书籍
 */
import java.util.ArrayList;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.controller.GetPosition;
import com.starbaby.diyBook.fragment.Fragment1;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.main.UserInfo;
import com.starbaby.diyBook.open.BookUtils;
import com.starbaby.diyBook.utils.ImageLoader;
import com.starbaby.diyBook.utils.JavaBean3;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.commentDialogUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BookShelfAdapter3 extends BaseAdapter {
	private Context mContext;
	public ArrayList<String> bookIdList;
	public ArrayList<String> bookCoverList;
	public ArrayList<String> bookNameList;
	public ArrayList<String> bookUpdateTime;
	@SuppressWarnings("unused")
	private JavaBean3 mJavaBean;
	private ImageLoader imageLoader;
	private String uid;
	private int flag;
	DisplayMetrics  dm;
	int minHeight,minWidth;
	private Bitmap bit = null;
	private Animation alphaAnimation = null;  
	Fragment1 fragment1;
	int firstCount = -1;//我的作品。第一次长按的position，如果与第二次单击的position不匹配，则删除键就消失，否则删除该书本
	boolean bDelete = false;//我的作品。控制第二次按下去时候删除（短按 长按2中方式）
	private ImageButton firstIBnt;//传值，不同行数的imagebutton（删除按钮）
	@SuppressWarnings("static-access")
	public BookShelfAdapter3(Context mContext,JavaBean3 mJavaBean,String uid,int flag,Fragment1 fragment1){
		this.mContext = mContext;
		this.mJavaBean = mJavaBean;
		this.flag = flag;
		Log.i("flag",flag + "");
		this.uid = uid;
		this.fragment1 = fragment1;
		this.bookIdList = mJavaBean.bookIdList;
		this.bookCoverList = mJavaBean.bookCoverList;
		this.bookNameList = mJavaBean.bookNameList;
		this.bookUpdateTime = mJavaBean.bookUpdateTime;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
		minHeight = (dm.heightPixels - dm.heightPixels / 6 - BookUtils.dip2px(mContext, 20)) / 2;
		minWidth = (dm.widthPixels - BookUtils.dip2px(mContext, 240));
		imageLoader=new ImageLoader(mContext.getApplicationContext(),minHeight,minWidth,3);
		alphaAnimation = AnimationUtils.loadAnimation(mContext,R.anim.imageview_aplah);
		alphaAnimation.setFillEnabled(true);// 启动Fill保持
		alphaAnimation.setFillAfter(true);//设置动画的最后一帧是保留在view上的  
	}
	@Override
	public int getCount() {
		if((int)Math.ceil((float)bookCoverList.size() / 3) > 1 ){
			return (int)Math.ceil((float)bookCoverList.size() / 3) + 1;
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
	int position;
	@SuppressWarnings("deprecation")
	@SuppressLint("InlinedApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		this.position = position;
		if (position == getCount() - 1) {
			View view =LayoutInflater.from(mContext).inflate(R.layout.listfooter_more, null);
			return view;
		}
		convertView = LayoutInflater.from(mContext).inflate(R.layout.shelf_list_item2, null);
		final ImageView iv1 =(ImageView)convertView. findViewById(R.id.fragment_button_1);
		final ImageView iv2 =(ImageView)convertView. findViewById(R.id.fragment_button_2);
		final ImageView iv3 =(ImageView)convertView. findViewById(R.id.fragment_button_3);
		final ImageButton delete1 = (ImageButton) convertView.findViewById(R.id.local_delete1);
		final ImageButton delete2 = (ImageButton) convertView.findViewById(R.id.local_delete2);
		final ImageButton delete3 = (ImageButton) convertView.findViewById(R.id.local_delete3);
		delete1.setVisibility(View.GONE);
		delete2.setVisibility(View.GONE);
		delete3.setVisibility(View.GONE);
		iv1.setAnimation(alphaAnimation);  
        iv2.setAnimation(alphaAnimation);  
        iv3.setAnimation(alphaAnimation);  
		ImageView left = (ImageView)convertView. findViewById(R.id.fragment_shelf_image_left);
		ImageView right = (ImageView)convertView. findViewById(R.id.fragment_shelf_image_right);
		LinearLayout ll = (LinearLayout)convertView. findViewById(R.id.fragment_linearLayout1);
		left.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,minHeight));
		right.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,minHeight));
		ll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,minHeight));
		final LinearLayout contain1 =(LinearLayout)convertView. findViewById(R.id.fragment_ll1);
		final LinearLayout contain2 =(LinearLayout)convertView. findViewById(R.id.fragment_ll2);
		final LinearLayout contain3 =(LinearLayout)convertView. findViewById(R.id.fragment_ll3);
		if(position > 0){
			left.setBackgroundResource(R.drawable.bookshelf_layer_left_2);
			right.setBackgroundResource(R.drawable.bookshelf_layer_right_2);
			ll.setBackgroundResource(R.drawable.bookshelf_layer_center_2);
		}
		if(3*position+1 < bookCoverList.size() || 3*position+1 == bookCoverList.size()){
			bit = BitmapFactory.decodeFile(Utils.coverPath + uid + "/" + NamePic.convertUrlToFileName(bookCoverList.get(3*position+0)));
			if(bit != null){
				new GetPosition(minHeight,minWidth,bit.getHeight(),bit.getWidth(),iv1,3);
				iv1.setImageBitmap(bit);
				contain1.setBackgroundResource(R.drawable.blank_book2);
			}else{
				if(bookCoverList.get(3*position+0) != null && !bookCoverList.get(3*position+0).equals("")){
					imageLoader.DisplayImage(bookCoverList.get(3*position+0),iv1);
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
		if(3*position+2 < bookCoverList.size() || 3*position+2 == bookCoverList.size()){
			bit = BitmapFactory.decodeFile(Utils.coverPath + uid + "/" + NamePic.convertUrlToFileName(bookCoverList.get(3*position+1)));
			if(bit != null){
				new GetPosition(minHeight,minWidth,bit.getHeight(),bit.getWidth(),iv2,3);
				iv2.setImageBitmap(bit);
				contain2.setBackgroundResource(R.drawable.blank_book2);
			}else{
				if(bookCoverList.get(3*position+1) != null && !bookCoverList.get(3*position+1).equals("")){
					imageLoader.DisplayImage(bookCoverList.get(3*position+1),iv2);
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
		if(3*position+3 < bookCoverList.size() || 3*position+3 == bookCoverList.size()){
			bit = BitmapFactory.decodeFile(Utils.coverPath + uid + "/" + NamePic.convertUrlToFileName(bookCoverList.get(3*position+2)));
			if(bit != null){
				new GetPosition(minHeight,minWidth,bit.getHeight(),bit.getWidth(),iv3,3);
				iv3.setImageBitmap(bit);
				contain3.setBackgroundResource(R.drawable.blank_book2);
			}else{
				if(bookCoverList.get(3*position+2) != null && !bookCoverList.get(3*position+2).equals("")){
					imageLoader.DisplayImage(bookCoverList.get(3*position+2),iv3);
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
		iv1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(flag == 1){
					if(bDelete){
						if((3 * position + 0) == firstCount){	//	第二次短按 与第一次长按时同一本书 （该书本不打开。进行删除）
							showDeleteTips(3*position+0);
							return;
						}else{
							firstIBnt.setVisibility(View.GONE);
							firstCount = -1;
							bDelete = false;
							return;
						}
					}
				}
				delete1.setVisibility(View.GONE);
				int[] location = new int[2];
				iv1.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				if(!Utils.isFastDoubleClick()){
					((UserInfo)mContext).loading(bookIdList.get(3*position+0),contain1,iv1,x,y,iv1.getWidth(),iv1.getHeight(),bookUpdateTime.get(3*position+0),bookNameList.get(3*position + 0),bookCoverList.get(3*position + 0),false);
				}
			}
		});
		iv2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(flag == 1){
					if(bDelete){
						if((3 * position + 1) == firstCount){	//	第二次短按 与第一次长按时同一本书 （该书本不打开。进行删除）
							showDeleteTips(3*position+1);
							return;
						}else{
							firstIBnt.setVisibility(View.GONE);
							firstCount = -1;
							bDelete = false;
							return;
						}
					}
				}
				delete2.setVisibility(View.GONE);
				int[] location = new int[2];
				iv2.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				if(!Utils.isFastDoubleClick()){
					((UserInfo)mContext).loading(bookIdList.get(3*position+1),contain2,iv2,x,y,iv2.getWidth(),iv2.getHeight(),bookUpdateTime.get(3*position+1),bookNameList.get(3*position + 1),bookCoverList.get(3*position + 1),false);
				}
			}
		});
		iv3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(flag == 1){
					if(bDelete){
						if((3 * position + 2) == firstCount){	//	第二次短按 与第一次长按时同一本书 （该书本不打开。进行删除）
							showDeleteTips(3*position+2);
							return;
						}else{
							firstIBnt.setVisibility(View.GONE);
							firstCount = -1;
							bDelete = false;
							return;
						}
					}
				}
				delete3.setVisibility(View.GONE);
				int[] location = new int[2];
				iv3.getLocationOnScreen(location);
				int x = location[0];
				int y = location[1];
				if(!Utils.isFastDoubleClick()){
					((UserInfo) mContext).loading(bookIdList.get(3*position+2),contain3,iv3,x,y,iv3.getWidth(),iv3.getHeight(),bookUpdateTime.get(3*position+2),bookNameList.get(3*position + 2),bookCoverList.get(3*position +2),false);
				}
			}
		});
		if(flag == 2){//已读书籍长按事件（收藏  删除）
			iv1.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					delete1.setVisibility(View.VISIBLE);
					return true;
				}
			});
			iv2.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					delete2.setVisibility(View.VISIBLE);
					return true;
				}
			});
			iv3.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					delete3.setVisibility(View.VISIBLE);
					return true;
				}
			});
		}else if(flag == 1){//我的作品长按事件
			iv1.setOnLongClickListener(new OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View v) {
					if(bDelete){
						if((3 * position + 0) == firstCount){//第二次长按 与第一次长按时同一本书 （进行删除操作）
							showDeleteTips(3*position+0);
						}else{//第二次长按 与第一次长按不是同一本书（标识清零）
							firstIBnt.setVisibility(View.GONE);
							firstCount = -1;
						}
						bDelete = false;
					}else{//第一次长按
						delete1.setVisibility(View.VISIBLE);
						firstCount = 3 * position + 0;
						bDelete = true;
						firstIBnt = delete1;
					}
					return true;
				}
			});
			iv2.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					if(bDelete){
						if((3 * position + 1) == firstCount){//第二次长按 与第一次长按时同一本书 （进行删除操作）
							showDeleteTips(3*position+1);
						}else{//第二次长按 与第一次长按不是同一本书（标识清零）
							firstIBnt.setVisibility(View.GONE);
							firstCount = -1;
						}
						bDelete = false;
					}else{//第一次长按
						delete2.setVisibility(View.VISIBLE);
						firstCount = 3 * position + 1;
						bDelete = true;
						firstIBnt = delete2;
					}
					return true;
				}
			});
			iv3.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					if(bDelete){
						if((3 * position + 2) == firstCount){//第二次长按 与第一次长按时同一本书 （进行删除操作）
							showDeleteTips(3*position+2);
						}else{//第二次长按 与第一次长按不是同一本书（标识清零）
							firstIBnt.setVisibility(View.GONE);
							firstCount = -1;
						}
						bDelete = false;
					}else{//第一次长按
						delete3.setVisibility(View.VISIBLE);
						firstCount = 3 * position + 2;
						bDelete = true;
						firstIBnt = delete3;
					}
					return true;
				}
			});
			
		}
		//删除 和 收藏的操作
		delete1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(flag == 2){
					fragment1.deleteLocalBook(bookIdList.get(3*position+0));
				}else if(flag == 1){
					showDeleteTips(3*position+0);
				}
				
			}
		});
		delete2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(flag == 2){
					fragment1.deleteLocalBook(bookIdList.get(3*position+1));
				}else if(flag == 1){
					showDeleteTips(3*position+1);
				}
				
			}
		});
		delete3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(flag == 2){
					fragment1.deleteLocalBook(bookIdList.get(3*position+2));
				}else if(flag == 1){
					showDeleteTips(3*position+2);
				}
				
			}
		});
		return convertView;
	}
	//删除操作提示
	commentDialogUtils dialog;
	TextView tv1,tv2,tv3;
	Button cancle,ok;
	void showDeleteTips(final int mPosition){
		dialog = new commentDialogUtils(mContext, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, R.layout.wifi_tip, R.style.Theme_dialog);
		tv1 = (TextView) dialog.findViewById(R.id.wifi_tv1);
		tv2 = (TextView) dialog.findViewById(R.id.wifi_tv2);
		tv3 = (TextView) dialog.findViewById(R.id.wifi_tv3);
		cancle = (Button) dialog.findViewById(R.id.wifi_bnt1);
		ok = (Button) dialog.findViewById(R.id.wifi_bnt2);
		tv1.setText("");
		tv2.setText("确定删除？");
		tv2.setTextSize(25);
		tv3.setText("");
		cancle.setText("取消");
		ok.setText("确定");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		cancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		ok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				firstCount = -1;
				bDelete = false;
				fragment1.deleteMyWork(bookIdList.get(mPosition));
			}
		});
	}
}
