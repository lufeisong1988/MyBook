package com.starbaby.diyBook.adapter;
/**
 * 分享适配器
 */
import com.starbaby.diyBook.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareAdapter extends BaseAdapter{
	String[] name = {
			"新浪微博","腾讯微博","QQ空间","微信好友","微信朋友圈","QQ",
	};
	int[] imgId = {
			R.drawable.logo_sinaweibo,R.drawable.logo_tencentweibo,R.drawable.logo_qzone,R.drawable.logo_wechat,R.drawable.logo_wechatmoments,R.drawable.logo_qq
	};
	Context mContext;
	public ShareAdapter(Context mContext){
		this.mContext = mContext;
	}
	@Override
	public int getCount() {
		return 6;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(R.layout.share_inflate, null);
		ImageView iv =(ImageView) convertView.findViewById(R.id.shareIV);
		TextView tv =(TextView) convertView.findViewById(R.id.shareTV);
		iv.setImageResource(imgId[position]);
		tv.setText(name[position]);
		return convertView;
	}

}
