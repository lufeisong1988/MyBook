package com.starbaby.diyBook.controller;

import java.io.File;
import java.util.ArrayList;

import com.starbaby.diyBook.helper.CurlPage;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.view.CreateCurlView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;



/**
 * Bitmap provider
 * @author Administrator
 * page CurlPage对象
 * index 当前显示的第几张图片
 *
 */
@SuppressLint({ "HandlerLeak", "SdCardPath" })
public class CreatePageProvider implements CreateCurlView.PageProvider {
	public int imageWidth;
	public int imageHeight;
	public Bitmap bitmap;
	private Context mContext;
	ArrayList<String> playList;
	public CreatePageProvider(Context mContext,ArrayList<String> playList){
		this.mContext = mContext;
		this.playList = playList;
	}
	//设置画册的张�?
	public int getPageCount() {
		return playList.size() / 2;
	}
	
	private Bitmap loadBitmap(int width, int height, final int pageCount,final CurlPage page,String face) throws InterruptedException {
		Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		b.eraseColor(0xffffffff);//设置背景色�?
		Canvas c = new Canvas(b);
		String[] str = (NamePic.mp3UrlToFileName(playList.get(pageCount))).split("\\.");
		String picName = str[0] + ".jpg";
		File existFile = new File(Utils.path + picName);
		if(existFile.exists()){//判断是否存在JPG图片,如果存在：1.该图不需要修改 2.改图已经修改完成
			bitmap = BitmapFactory.decodeFile(existFile.toString());
			if(face.equals("back")){
				Log.i("face backJPG",Utils.path + picName);
			}else{
				Log.i("face frontJPG",Utils.path + picName);
			}
		}else{//不存在。说明改图是PNG格式。需要进行操作
			bitmap = BitmapFactory.decodeFile(Utils.path + NamePic.mp3UrlToFileName(playList.get(pageCount)));
			if(face.equals("front")){
				Log.i("face frontPNG",Utils.path + NamePic.mp3UrlToFileName(playList.get(pageCount)));
			}else{
				Log.i("face backPNG",Utils.path + NamePic.mp3UrlToFileName(playList.get(pageCount)));
			}
		}
	
		return bitmap;
	}
	private Bitmap rotate(Bitmap bitmap){
		Bitmap currentBack = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(currentBack);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Matrix m = new Matrix();
		m.postScale(-1, 1);
		Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
//		if(bitmap != null && !bitmap.isRecycled()){
//			bitmap.recycle();
//			bitmap = null;
//		}
		return b;
	}
	public void updatePage(final CurlPage page, final int width,final int height, final int index) {
		// index独立：按翻页顺序来�?与loadBitmap(int width, int height, int index)里的index无关
		Bitmap front = null;
		Bitmap back = null;
		try {
			front = loadBitmap(width, height, index * 2,page,"front");
			back = loadBitmap(width, height, index  * 2 + 1,page,"back");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(front != null){
			page.setTexture(front, CurlPage.SIDE_FRONT);
		}
		if(back != null){
			Bitmap currentBack = rotate(back);
			back.recycle();
			back = null;
			page.setTexture(currentBack, CurlPage.SIDE_BACK);
		}
	}
}

