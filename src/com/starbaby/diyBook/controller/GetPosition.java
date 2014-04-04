package com.starbaby.diyBook.controller;
/**
 * 根据分辨率 使书封面自动等比放大适应
 */

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GetPosition {
	public GetPosition(int height,int width,int picHeight,int picWdith,ImageView iv,int count){
		int IVHeight = height - 22 - 12;
		int IVWidth = width / count - 11;
		int currentHeight;
		int currentWidth;
		float Scale = ((float)IVHeight / IVWidth);
		float scale = ((float)picHeight / picWdith);
		if(Scale > scale){//height填充满了
			currentHeight = (picHeight * IVWidth) / picWdith;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(IVWidth, currentHeight);
			iv.setLayoutParams(params);
		}else if(Scale < scale){//width填充满了
			currentWidth = (IVHeight * picWdith) / picHeight;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(currentWidth, IVHeight);
			iv.setLayoutParams(params);
		}else{//正好铺满
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(IVWidth, IVHeight);
			iv.setLayoutParams(params);
		}
	}
}
