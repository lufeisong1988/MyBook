/*
   Copyright 2012 Harri Smatt

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.starbaby.diyBook.view;




import com.starbaby.diyBook.R;
import com.starbaby.diyBook.helper.CurlMesh;
import com.starbaby.diyBook.helper.CurlPage;
import com.starbaby.diyBook.helper.CurlRenderer;
import com.starbaby.diyBook.helper.MusicHelper;
import com.starbaby.diyBook.open.PerspectiveView;
import com.starbaby.diyBook.utils.StoreSrc;
import com.starbaby.diyBook.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * OpenGL ES View.
 *使用于书本的翻页
 * @author harism
 */
@SuppressLint("NewApi")
public class CurlView extends GLSurfaceView implements View.OnTouchListener,
		com.starbaby.diyBook.helper.CurlRenderer.Observer {
	
	public static int timer;
	// Curl state. We are flipping none, left or right page.
	private static final int CURL_LEFT = 1;
	private static final int CURL_NONE = 0;
	public static final int CURL_RIGHT = 2;

	// Constants for mAnimationTargetEvent.
	private static final int SET_CURL_TO_LEFT = 1;
	private static final int SET_CURL_TO_RIGHT = 2;
	private static final int SET_CURL_TO_NO = 3;
	/**
	 * 1.Shows one page at the center of view. 
	 * 2.Shows two pages side by side.
	 */
	public static final int SHOW_ONE_PAGE = 1;
	public static final int SHOW_TWO_PAGES = 2;

	private boolean mAllowLastPageCurl = true;

	private boolean mAnimate = false;
	private long mAnimationDurationTime = 300;
	private PointF mAnimationSource = new PointF();
	private long mAnimationStartTime;
	private PointF mAnimationTarget = new PointF();
	private int mAnimationTargetEvent;

	private PointF mCurlDir = new PointF();

	private PointF mCurlPos = new PointF();
	public int mCurlState = CURL_NONE;
	// Current bitmap index. This is always showed as front of right page.
	private int mCurrentIndex = 0;
	//手指开始拖动的点
	private PointF mDragStartPos = new PointF();
	private boolean mEnableTouchPressure = false;
	// 初始化Bitmap的宽高，会在renderer渲染后重新赋值
	private int mPageBitmapHeight = -1;
	private int mPageBitmapWidth = -1;
	// 书页卷角
	private CurlMesh mPageCurl;
	private CurlMesh mPageLeft;
	private PageProvider mPageProvider;
	private CurlMesh mPageRight;

	private PointerPosition mPointerPos = new PointerPosition();

	public CurlRenderer mRenderer;
	public boolean mRenderLeftPage = true;
	//判断，横屏：展示2页。竖屏：展示一页
	private SizeChangedObserver mSizeChangedObserver;

	// 默认竖屏。所以是显示一页的状态（这里我强制横屏，显示2页）
	private int mViewMode = SHOW_ONE_PAGE;
	private Context mContext;
	PerspectiveView perspectiveView;
	private boolean bVisiable = true;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				perspectiveView.setVisibility(View.INVISIBLE);
				break;
			}
			super.handleMessage(msg);
		}
		
	};

	/**
	 * Default constructor.
	 */
	public CurlView(Context ctx,PerspectiveView perspectiveView) {
		super(ctx);
		init(ctx);
		this.mContext = ctx;
		this.perspectiveView = perspectiveView;
	}

	/**
	 * Default constructor.
	 */
	public CurlView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);
		init(ctx);
	}

	/**
	 * Default constructor.
	 */
	public CurlView(Context ctx, AttributeSet attrs, int defStyle) {
		this(ctx, attrs);
	}

	/**
	 * 返回当前页索引,page是从第0页开始，显示在右边那页（类似封面）
	 * 
	 */
	public int getCurrentIndex() {
		return mCurrentIndex;
	}
	/**
	 * 初始化渲染器CurlRenderer
	 */
	private void init(Context ctx) {
		mRenderer = new CurlRenderer(this);
		// 设置背景透明
		setZOrderOnTop(true);
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		getHolder().setFormat(PixelFormat.TRANSPARENT);
		
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		// 手指按下的监听事件
		setOnTouchListener(this);
		// 参数10，不用考虑:就是实现卷角
		mPageLeft = new CurlMesh(10);
		mPageRight = new CurlMesh(10);
		mPageCurl = new CurlMesh(10);
		mPageLeft.setFlipTexture(true);
		mPageRight.setFlipTexture(false);
		
	}

	@Override
	public void onDrawFrame() {
		// We are not animating.
		if (mAnimate == false) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		// 开始滑动书页
		if (currentTime >= mAnimationStartTime + mAnimationDurationTime) {//翻页结束的状态
			if (mAnimationTargetEvent == SET_CURL_TO_RIGHT) {
				// 翻上一页
				CurlMesh right = mPageCurl;
				CurlMesh curl = mPageRight;
				right.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
				right.setFlipTexture(false);
				right.reset();
				mRenderer.removeCurlMesh(curl);
				mPageCurl = curl;
				mPageRight = right;
				if (mCurlState == CURL_LEFT) {
					--mCurrentIndex;
					Utils.currentPage = mCurrentIndex;
					if(mCurrentIndex > 0){
						if(Utils.createMusic){
							MusicHelper.changeAndPlayMusic(mCurrentIndex);
						}
					}
				}else{
					if (mCurrentIndex == 0) {

					} else {
						if(Utils.createMusic){
							MusicHelper.keepMusic();
						}
					}
				}
			} else if (mAnimationTargetEvent == SET_CURL_TO_LEFT) {
				// 翻下一页
				CurlMesh left = mPageCurl;
				CurlMesh curl = mPageLeft;
				left.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				left.setFlipTexture(true);
				left.reset();
				mRenderer.removeCurlMesh(curl);
				if (!mRenderLeftPage) {
					mRenderer.removeCurlMesh(left);
				}
				mPageCurl = curl;
				mPageLeft = left;
				if (mCurlState == CURL_RIGHT) {
					++mCurrentIndex;
					Utils.currentPage = mCurrentIndex;
					if(mCurrentIndex < mPageProvider.getPageCount()){
						if(Utils.createMusic){
							MusicHelper.changeAndPlayMusic(mCurrentIndex);
						}
					}
				}else{
					if(mCurrentIndex == 0){
						
					}else{
						if(Utils.createMusic){
							if(mCurrentIndex != StoreSrc.audiolist.size())
							MusicHelper.keepMusic();
						}
					}
				}
			}
			mCurlState = CURL_NONE;
			mAnimate = false;
			requestRender();
		} else {//翻页中
			mPointerPos.mPos.set(mAnimationSource);
			float t = 1f - ((float) (currentTime - mAnimationStartTime) / mAnimationDurationTime);
			t = 1f - (t * t * t * (3 - 2 * t));
			mPointerPos.mPos.x += (mAnimationTarget.x - mAnimationSource.x) * t;
			mPointerPos.mPos.y += (mAnimationTarget.y - mAnimationSource.y) * t;
			updateCurlPos(mPointerPos);
		}
	}

	@Override
	public void onPageSizeChanged(int width, int height) {
		mPageBitmapWidth = width;
		mPageBitmapHeight = height;
		updatePages();
		requestRender();
	}
	/**
	 * 设置单页在屏幕上显示的大小
	 */
	@Override
	public void onSizeChanged(int w, int h, int ow, int oh) {
		super.onSizeChanged(w, h, ow, oh);
		requestRender();
		if (mSizeChangedObserver != null) {
			mSizeChangedObserver.onSizeChanged(w, h);
		}
	}

	@Override
	public void onSurfaceCreated() {
		mPageLeft.resetTexture();
		mPageRight.resetTexture();
		mPageCurl.resetTexture();
	}
	/**
	 * 根据手势来卷曲翻页
	 */
	MediaPlayer open ;//翻页音效
	boolean bFilep = true;
	@Override
	public boolean onTouch(View view, MotionEvent me) {
		
		// 如果当前没有拖动图片
		if (mAnimate || mPageProvider == null) {
			return false;
		}
		
		// We need page rects quite extensively so get them for later use.
		RectF rightRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT);
		RectF leftRect = mRenderer.getPageRect(CurlRenderer.PAGE_LEFT);

		// Store pointer position.
		mPointerPos.mPos.set(me.getX(), me.getY());
		mRenderer.translate(mPointerPos.mPos);
		if (mEnableTouchPressure) {
			mPointerPos.mPressure = me.getPressure();
		} else {
			mPointerPos.mPressure = 0.8f;
		}

		switch (me.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			bFilep = true;
			
			if(perspectiveView != null){
				if(bVisiable){
					new Thread(new  Runnable() {
						public void run() {
							Message msg = new Message();
							msg.what = 1;
							mHandler.sendMessage(msg);
						}
					}).start();
					bVisiable =  false;
				}
			}
			open = MediaPlayer.create(mContext, R.raw.open);
			mDragStartPos.set(mPointerPos.mPos);

			// 首先确保图片的高度不超过预设的高度。如果超过就把预设的高重新赋值给图片。
			if (mDragStartPos.y > rightRect.top) {
				mDragStartPos.y = rightRect.top;
			} else if (mDragStartPos.y < rightRect.bottom) {
				mDragStartPos.y = rightRect.bottom;
			}

			if (mViewMode == SHOW_TWO_PAGES) {// 横屏
				// 向右滑动
				if (mDragStartPos.x < rightRect.left && mCurrentIndex > 0) {
					mDragStartPos.x = leftRect.left;
					startCurl(CURL_LEFT);
				} else if (mDragStartPos.x >= rightRect.left && mCurrentIndex < mPageProvider.getPageCount()) {// 向左滑动
					mDragStartPos.x = rightRect.right;
					Log.e("rightRect.right=", rightRect.right+"");
					if (!mAllowLastPageCurl && mCurrentIndex >= mPageProvider.getPageCount() - 1) { 
						return false;
					}
					startCurl(CURL_RIGHT);
				}
			} else if (mViewMode == SHOW_ONE_PAGE) {// 竖屏，暂不考虑
				float halfX = (rightRect.right + rightRect.left) / 2;
				if (mDragStartPos.x < halfX && mCurrentIndex > 0) {
					mDragStartPos.x = rightRect.left;
					startCurl(CURL_LEFT);
				} else if (mDragStartPos.x >= halfX && mCurrentIndex < mPageProvider.getPageCount()) {
					mDragStartPos.x = rightRect.right;
					if (!mAllowLastPageCurl && mCurrentIndex >= mPageProvider.getPageCount() - 1) {
						return false;
					}
					startCurl(CURL_RIGHT);
				}
			}
			if (mCurlState == CURL_NONE) {
				return false;
			}
		}
		
		case MotionEvent.ACTION_MOVE: {// 翻页ing:暂停音乐
			
			updateCurlPos(mPointerPos);
			if(Utils.createMusic){
				MusicHelper.pauseMusic();
			}
			break;
		}
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			
			if (mCurlState == CURL_LEFT || mCurlState == CURL_RIGHT) {
				mAnimationSource.set(mPointerPos.mPos);
				mAnimationStartTime = System.currentTimeMillis();
				if ((mViewMode == SHOW_ONE_PAGE && mPointerPos.mPos.x > (rightRect.left + rightRect.right) / 2) || mViewMode == SHOW_TWO_PAGES && mPointerPos.mPos.x > rightRect.left) {
						
					mAnimationTarget.set(mDragStartPos);
					mAnimationTarget.x = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).right;
					mAnimationTargetEvent = SET_CURL_TO_RIGHT;//向右翻页
				} else {
					mAnimationTarget.set(mDragStartPos);
					if (mCurlState == CURL_RIGHT || mViewMode == SHOW_TWO_PAGES) {
						mAnimationTarget.x = leftRect.left;
					} else {
						mAnimationTarget.x = rightRect.left;
					}
					mAnimationTargetEvent = SET_CURL_TO_LEFT;//向左翻页
				}
				mAnimate = true;
				requestRender();
			}else{
				mAnimationTargetEvent = SET_CURL_TO_NO;
			}
			break;
		}
		}
		return true;
	}

	/**
	 * 准许最后一页可以卷曲
	 */
	public void setAllowLastPageCurl(boolean allowLastPageCurl) {
		mAllowLastPageCurl = allowLastPageCurl;
	}

	/**
	 * 设置背景色（注：demo下整个activity都是GLSurfaceView）
	 */
	@Override
	public void setBackgroundColor(int color) {
		mRenderer.setBackgroundColor(color);
		requestRender();
	}

	/**
	 * 设置当前页卷曲的坐标（具体参数）
	 */
	private void setCurlPos(PointF curlPos, PointF curlDir, double radius) {
		if (mCurlState == CURL_RIGHT || (mCurlState == CURL_LEFT && mViewMode == SHOW_ONE_PAGE)) {
			RectF pageRect = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT);
			if (curlPos.x >= pageRect.right) {
				mPageCurl.reset();
				requestRender();
				return;
			}else{
				if(bFilep){
					if(open != null){
						Log.i("MediaPlayer Right","open");
						open.start();
						open.setOnCompletionListener(new OnCompletionListener() {
							
							@Override
							public void onCompletion(MediaPlayer mp) {
								open.release();
								open = null;
								Log.i("MediaPlayer Right","null");
							}
						});
						open.setOnErrorListener(new OnErrorListener() {
							
							@Override
							public boolean onError(MediaPlayer mp, int what, int extra) {
								// TODO Auto-generated method stub
								return false;
							}
						});
						open.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
							
							@Override
							public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
								// TODO Auto-generated method stub
								
							}
						});
						open.setOnPreparedListener(new OnPreparedListener() {
							
							@Override
							public void onPrepared(MediaPlayer mp) {
								// TODO Auto-generated method stub
								
							}
						});
					}
					bFilep = false;
				}
			}
			if (curlPos.x < pageRect.left) {
				curlPos.x = pageRect.left;
			}
			if (curlDir.y != 0) {
				float diffX = curlPos.x - pageRect.left;
				float leftY = curlPos.y + (diffX * curlDir.x / curlDir.y);
				if (curlDir.y < 0 && leftY < pageRect.top) {
					curlDir.x = curlPos.y - pageRect.top;
					curlDir.y = pageRect.left - curlPos.x;
				} else if (curlDir.y > 0 && leftY > pageRect.bottom) {
					curlDir.x = pageRect.bottom - curlPos.y;
					curlDir.y = curlPos.x - pageRect.left;
				}
			}
		} else if (mCurlState == CURL_LEFT) {
			RectF pageRect = mRenderer.getPageRect(CurlRenderer.PAGE_LEFT);
			if (curlPos.x <= pageRect.left) {
				mPageCurl.reset();
				requestRender();
				return;
			}else{
				if(bFilep){
					if(open != null){
						Log.i("MediaPlayer Left","open");
						open.start();
						open.setOnCompletionListener(new OnCompletionListener() {
							
							@Override
							public void onCompletion(MediaPlayer mp) {
								open.release();
								open = null;
								Log.i("MediaPlayer Left","null");
							}
						});
						open.setOnErrorListener(new OnErrorListener() {
							
							@Override
							public boolean onError(MediaPlayer mp, int what, int extra) {
								// TODO Auto-generated method stub
								return false;
							}
						});
						open.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {
							
							@Override
							public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
								// TODO Auto-generated method stub
								
							}
						});
						open.setOnPreparedListener(new OnPreparedListener() {
							
							@Override
							public void onPrepared(MediaPlayer mp) {
								// TODO Auto-generated method stub
								
							}
						});
					}
					bFilep = false;
				}
			}
			if (curlPos.x > pageRect.right) {
				curlPos.x = pageRect.right;
			}
			if (curlDir.y != 0) {
				float diffX = curlPos.x - pageRect.right;
				float rightY = curlPos.y + (diffX * curlDir.x / curlDir.y);
				if (curlDir.y < 0 && rightY < pageRect.top) {
					curlDir.x = pageRect.top - curlPos.y;
					curlDir.y = curlPos.x - pageRect.right;
				} else if (curlDir.y > 0 && rightY > pageRect.bottom) {
					curlDir.x = curlPos.y - pageRect.bottom;
					curlDir.y = pageRect.right - curlPos.x;
				}
			}
		}

		double dist = Math.sqrt(curlDir.x * curlDir.x + curlDir.y * curlDir.y);
		if (dist != 0) {
			curlDir.x /= dist;
			curlDir.y /= dist;
			mPageCurl.curl(curlPos, curlDir, radius);
		} else {
			mPageCurl.reset();
		}

		requestRender();
	}

	/**
	 * 判断 设置打开app显示的第index页，是否超出了页数总和，如果超出就选最后一页作为显示页，否则index作为显示页
	 */
	public void setCurrentIndex(int index) {
		if (mPageProvider == null || index < 0) {
			mCurrentIndex = 0;
		} else {
			if (mAllowLastPageCurl) {
				mCurrentIndex = Math.min(index, mPageProvider.getPageCount());
			} else {
				mCurrentIndex = Math.min(index,mPageProvider.getPageCount() - 1);
			}
		}
		updatePages();
		requestRender();
	}

	public void setEnableTouchPressure(boolean enableTouchPressure) {
		mEnableTouchPressure = enableTouchPressure;
	}

	/**
	 * .1f will produce a 10% margin.
	 * 设置图片padding手机四边的距离
	 */
	public void setMargins(float left, float top, float right, float bottom) {
		mRenderer.setMargins(left, top, right, bottom);
	}

	/**
	 * 刷新当前显示页
	 */
	public void setPageProvider(PageProvider pageProvider) {
		mPageProvider = pageProvider;
		mCurrentIndex = 0;
		updatePages();
		requestRender();
	}

	/**
	 * 默认书页第一面（注：不是第一页）在右边
	 */
	public void setRenderLeftPage(boolean renderLeftPage) {
		mRenderLeftPage = renderLeftPage;
	}

	/**
	 * 获取屏幕状态（横屏：展示2页，竖屏：展示一页）
	 */
	public void setSizeChangedObserver(SizeChangedObserver observer) {
		mSizeChangedObserver = observer;
	}

	/**
	 * 横屏 ：显示两页，竖屏：显示单页
	 */
	public void setViewMode(int viewMode) {
		switch (viewMode) {
		case SHOW_ONE_PAGE:
			mViewMode = viewMode;
			mPageLeft.setFlipTexture(true);
			mRenderer.setViewMode(CurlRenderer.SHOW_ONE_PAGE);
			break;
		case SHOW_TWO_PAGES:
			mViewMode = viewMode;
			mPageLeft.setFlipTexture(false);
			mRenderer.setViewMode(CurlRenderer.SHOW_TWO_PAGES);
			break;
		}
	}

	/**
	 * 根据手势的滑动 ，切换页面
	 */
	@SuppressWarnings("static-access")
	private void startCurl(int page) {
		switch (page) {
		case CURL_RIGHT: {//向左滑动
			// 清除不必要的内存
			mRenderer.removeCurlMesh(mPageLeft);
			mRenderer.removeCurlMesh(mPageRight);
			mRenderer.removeCurlMesh(mPageCurl);

			// 对右页就行卷角
			CurlMesh curl = mPageRight;
			mPageRight = mPageCurl;
			mPageCurl = curl;

			if (mCurrentIndex > 0) {
				mPageLeft.setFlipTexture(true);
				mPageLeft.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				mPageLeft.reset();
				if (mRenderLeftPage) {
					mRenderer.addCurlMesh(mPageLeft);
				}
			}
			if (mCurrentIndex < mPageProvider.getPageCount() - 1) {
				updatePage(mPageRight.getTexturePage(), mCurrentIndex + 1,null);
				mPageRight.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
				mPageRight.setFlipTexture(false);
				mPageRight.reset();
				mRenderer.addCurlMesh(mPageRight);
			}

			// Add curled page to renderer.
			mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
			mPageCurl.setFlipTexture(false);
			mPageCurl.reset();
			mRenderer.addCurlMesh(mPageCurl);

			mCurlState = CURL_RIGHT;
			break;
			
		}

		case CURL_LEFT: {// 向右滑动
			if(new StoreSrc().currentPage){
				mCurrentIndex = 1;
				updatePage(mPageLeft.getTexturePage(), 0,null);
			}
			mRenderer.removeCurlMesh(mPageLeft);
			mRenderer.removeCurlMesh(mPageRight);
			mRenderer.removeCurlMesh(mPageCurl);

			// 对左页就行卷角
			CurlMesh curl = mPageLeft;
			mPageLeft = mPageCurl;
			mPageCurl = curl;

			if (mCurrentIndex > 1) {
				updatePage(mPageLeft.getTexturePage(), mCurrentIndex - 2,"right");
				mPageLeft.setFlipTexture(true);
				mPageLeft.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				mPageLeft.reset();
				if (mRenderLeftPage) {
					mRenderer.addCurlMesh(mPageLeft);
				}
			}

			// If there is something to show on right page add it to renderer.
			if (mCurrentIndex < mPageProvider.getPageCount()) {
				mPageRight.setFlipTexture(false);
				mPageRight.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
				mPageRight.reset();
				mRenderer.addCurlMesh(mPageRight);
			}

			// How dragging previous page happens depends on view mode.
			if (mViewMode == SHOW_ONE_PAGE|| (mCurlState == CURL_LEFT && mViewMode == SHOW_TWO_PAGES)) {
				mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
				mPageCurl.setFlipTexture(false);
			} else {
				mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
				mPageCurl.setFlipTexture(true);
			}
			mPageCurl.reset();
			mRenderer.addCurlMesh(mPageCurl);

			mCurlState = CURL_LEFT;
			new StoreSrc().currentPage = false;
			break;
		}

		}
	}

	/**
	 * 不断的刷新卷角随手指的坐标
	 */
	private void updateCurlPos(PointerPosition pointerPos) {

		double radius = mRenderer.getPageRect(CURL_RIGHT).width() / 3;
		radius *= Math.max(1f - pointerPos.mPressure, 0f);
		mCurlPos.set(pointerPos.mPos);

		if (mCurlState == CURL_RIGHT || (mCurlState == CURL_LEFT && mViewMode == SHOW_TWO_PAGES)) {

			mCurlDir.x = mCurlPos.x - mDragStartPos.x;
			mCurlDir.y = mCurlPos.y - mDragStartPos.y;
			float dist = (float) Math.sqrt(mCurlDir.x * mCurlDir.x + mCurlDir.y * mCurlDir.y);
			float pageWidth = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).width();
			double curlLen = radius * Math.PI;
			if (dist > (pageWidth * 2) - curlLen) {
				curlLen = Math.max((pageWidth * 2) - dist, 0f);
				radius = curlLen / Math.PI;
			}

			if (dist >= curlLen) {
				double translate = (dist - curlLen) / 2;
				if (mViewMode == SHOW_TWO_PAGES) {
					mCurlPos.x -= mCurlDir.x * translate / dist;
				} else {
					float pageLeftX = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).left;
					radius = Math.max(Math.min(mCurlPos.x - pageLeftX, radius),0f);
				}
				mCurlPos.y -= mCurlDir.y * translate / dist;
			} else {
				double angle = Math.PI * Math.sqrt(dist / curlLen);
				double translate = radius * Math.sin(angle);
				mCurlPos.x += mCurlDir.x * translate / dist;
				mCurlPos.y += mCurlDir.y * translate / dist;
			}
		}
		else if (mCurlState == CURL_LEFT) {

			float pageLeftX = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).left;
			radius = Math.max(Math.min(mCurlPos.x - pageLeftX, radius), 0f);

			float pageRightX = mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT).right;
			mCurlPos.x -= Math.min(pageRightX - mCurlPos.x, radius);
			mCurlDir.x = mCurlPos.x + mDragStartPos.x;
			mCurlDir.y = mCurlPos.y - mDragStartPos.y;
		}

		setCurlPos(mCurlPos, mCurlDir, radius);
	}

	/**
	 * page CurlPage对象。用来设置当前页面的属性
	 * index 当前页数
	 */
	public void updatePage(CurlPage page, int index,String orection) {
		// 初始化page
		page.reset();
		mPageProvider.updatePage(page, mPageBitmapWidth, mPageBitmapHeight,index,orection);
		
	}

	/**
	 * 刷新操作后的页面
	 */
	private void updatePages() {
		if (mPageProvider == null || mPageBitmapWidth <= 0 || mPageBitmapHeight <= 0) {
			return;
		}
		
		// Remove meshes from renderer.
		mRenderer.removeCurlMesh(mPageLeft);
		mRenderer.removeCurlMesh(mPageRight);
		mRenderer.removeCurlMesh(mPageCurl);

		int leftIdx = mCurrentIndex - 1;
		int rightIdx = mCurrentIndex;
		int curlIdx = -1;
		if (mCurlState == CURL_LEFT) {//向右滑动
			curlIdx = leftIdx;
			--leftIdx;
			
		} else if (mCurlState == CURL_RIGHT) {//向左滑动
			curlIdx = rightIdx;
			++rightIdx;
			
		}
		if (rightIdx >= 0 && rightIdx < mPageProvider.getPageCount()) {
			updatePage(mPageRight.getTexturePage(), rightIdx,null);
			mPageRight.setFlipTexture(false);
			mPageRight.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
			mPageRight.reset();
			mRenderer.addCurlMesh(mPageRight);
		}
		if (leftIdx >= 0 && leftIdx < mPageProvider.getPageCount()) {
			updatePage(mPageLeft.getTexturePage(), leftIdx,null);
			mPageLeft.setFlipTexture(true);
			mPageLeft.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
			mPageLeft.reset();
			if (mRenderLeftPage) {
				mRenderer.addCurlMesh(mPageLeft);
			}
		}
		if (curlIdx >= 0 && curlIdx < mPageProvider.getPageCount()) {
			updatePage(mPageCurl.getTexturePage(), curlIdx,null);

			if (mCurlState == CURL_RIGHT) {
				mPageCurl.setFlipTexture(true);
				mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_RIGHT));
			} else {
				mPageCurl.setFlipTexture(false);
				mPageCurl.setRect(mRenderer.getPageRect(CurlRenderer.PAGE_LEFT));
			}

			mPageCurl.reset();
			mRenderer.addCurlMesh(mPageCurl);
		}
	}

	/**
	 * 得到书一共几页，刷新显示页面的接口
	 */
	public interface PageProvider {
		public int getPageCount();
		public void updatePage(CurlPage page, int width, int height, int index,String orection);
	}

	/**
	 * Simple holder for pointer position.
	 */
	private class PointerPosition {
		PointF mPos = new PointF();
		float mPressure;
	}

	/**
	 * 页面大小改变的接口（即padding手机四周）
	 */
	public interface SizeChangedObserver {
		public void onSizeChanged(int width, int height);
	}

}
