package com.starbaby.diyBook.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.helper.CurlPage;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.utils.StoreSrc;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.view.CurlView;

/**
 * Bitmap provider
 * @author Administrator
 * page CurlPage对象
 * index 当前显示的第几张图片
 *
 */
@SuppressLint({ "HandlerLeak", "SdCardPath" })
public class PageProvider implements CurlView.PageProvider {
	public int imageWidth;
	public int imageHeight;
	public Bitmap bitmap;
	public CurlView mCurlView;
	public String bookName;
	private Context mContext;
	boolean bToCover = false;
	boolean bTransparent = false;
	private int retrunCoverCount;
	String frontUrl;
	public PageProvider(Context mContext,CurlView mCurlView,String bookName){
		this.mContext = mContext;
		this.mCurlView = mCurlView;
		this.bookName = bookName;
	}
	//设置画册的张数 
	@Override
	public int getPageCount() {
		return ((int)Math.ceil((float)StoreSrc.getImage_count() / 2) );
	}
	//控制 1正常翻页 2切换做书模式 翻到首页
	public void setBToCover(boolean bToCover){
		this.bToCover = bToCover;
	}
	//控制 做书模式下，封面变成做书封面PNG图片
	public void setBToTransparent(boolean bTransparent,String frontUrl){
		this.bTransparent = bTransparent;
		this.frontUrl = frontUrl;
	}
	private Bitmap loadBitmap(int width, int height, final int pageCount,final CurlPage page,String face) throws InterruptedException {
		Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		b.eraseColor(0xffffffff);//设置背景色。
		Canvas c = new Canvas(b);
		bitmap = BitmapFactory.decodeFile(Utils.basePath1 + bookName+ Utils.basePath2 + NamePic.convertUrlToFileName(StoreSrc.getImagelist().get(pageCount)));
		if (bitmap == null) {
			if(face.equals("front")){
				bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.right_default);
			}else if(face.equals("back")){
				bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.left_default);
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
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		return b;
	}
	@Override
	public void updatePage(final CurlPage page, final int width,final int height, final int index,String orection) {
		// index独立：按翻页顺序来。与loadBitmap(int width, int height, int index)里的index无关
		Bitmap front = null;
		Bitmap back = null;
		try {
			if(bToCover){
				if(retrunCoverCount == 0){
					retrunCoverCount = 1;
				}
				front = loadBitmap(width, height, retrunCoverCount * 2,page,"front");
				back = loadBitmap(width, height, retrunCoverCount  * 2 + 1,page,"back");
				bToCover = false;
			}else{
				if(orection == null){
					retrunCoverCount = index ;
				}else if(orection.equals("right")){
					retrunCoverCount = index + 1;
				}
				front = loadBitmap(width, height, index * 2,page,"front");
				back = loadBitmap(width, height, index  * 2 + 1,page,"back");
			}
			if(bTransparent){
				bTransparent = false;
				front = BitmapFactory.decodeFile(Utils.path + NamePic.ZuoShuUrlToFileNamePNG(frontUrl));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Bitmap currentBack = rotate(back);
		if(back != null && !back.isRecycled()){
			back.recycle();
			back = null;
		}
		page.setTexture(front, CurlPage.SIDE_FRONT);
		page.setTexture(currentBack, CurlPage.SIDE_BACK);
	}
}

