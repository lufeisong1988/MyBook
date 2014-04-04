package com.starbaby.diyBook.controller;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.utils.SdcardpathUtils;

/**
 * 监听头像
 * @author Administrator
 *
 */
public class HeadImgThread implements Runnable{
	private ImageView head;
	private Context mContext;
	SharedPreferences sp;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				sp = mContext.getSharedPreferences("diyBook", mContext.MODE_WORLD_READABLE);
				String name = sp.getString("avatar", "");
				if(name != null && !name.equals("")){
					if((new File(SdcardpathUtils.headName)).exists()){
						Bitmap headBit = BitmapFactory.decodeFile(SdcardpathUtils.headName);
						if(headBit != null){
							head.setImageBitmap(headBit);
						}
					}else{
						head.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.work_head));
					}
				}else{
					head.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.work_head));
				}
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	public HeadImgThread(ImageView head,Context mContext){
		this.head = head;
		this.mContext = mContext;
	}
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			Message msg = new Message();
			msg.what = 1;
			mHandler.sendMessage(msg);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
