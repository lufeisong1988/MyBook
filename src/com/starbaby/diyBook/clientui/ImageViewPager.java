package com.starbaby.diyBook.clientui;

import java.io.File;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientcommon.ImageUtils;
import com.starbaby.diyBook.clientcommon.UIHelper;
import com.starbaby.diyBook.clientwidget.DotView;
import com.starbaby.diyBook.clientwidget.HackyViewPager;
import com.starbaby.diyBook.clientwidget.PhotoView;

public class ImageViewPager extends BaseActivity implements
		OnPageChangeListener {

	private LinearLayout llDot;
	private HackyViewPager mViewPager;
	private static final String STATE_POSITION = "STATE_POSITION";
	private ProgressBar mProgressBar;
	// private ImageView mImg;
	/* private static ArrayList<Bitmap> smallBit = new ArrayList<Bitmap>(); */
	private static ArrayList<String> smallStr = new ArrayList<String>();
	private static ArrayList<String> bigStr = new ArrayList<String>();
	private DotView dota;
	private DisplayImageOptions options;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pager_img);
		mViewPager = (HackyViewPager) findViewById(R.id.viewpager);
		mProgressBar = (ProgressBar) findViewById(R.id.paper_progressbar);
		// mImg = (ImageView)findViewById(R.id.paper_image);

		smallStr = getIntent().getStringArrayListExtra("imgs");

		bigStr = getIntent().getStringArrayListExtra("bigImgs");

		options = new DisplayImageOptions.Builder()
				// .resetViewBeforeLoading(true)
				.cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
				.build();

		llDot = (LinearLayout) findViewById(R.id.linear_dot);
		dota = new DotView(this, smallStr.size());
		llDot.addView(dota);

		int index = getIntent().getIntExtra("index", 0);
		if (savedInstanceState != null) {
			index = savedInstanceState.getInt(STATE_POSITION);
		}
		dota.setPos(index);

		mViewPager.setAdapter(new SamplePagerAdapter());
		mViewPager.setCurrentItem(index);
		mViewPager.setOnPageChangeListener(this);

		/*
		 * if(imageLoader.getDiscCache().get(bigStr.get(index))==null){
		 * initData(index); }
		 */
		/*
		 * if(!replceImg(index)){ initData(index); }
		 */
		/*
		 * LinearLayout.LayoutParams dotaParams = new
		 * LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
		 * LayoutParams.WRAP_CONTENT); dotaParams.bottomMargin = (int)
		 * getResources().getDimension(R.dimen.dota_margin);
		 * mViewPager.addView(dota,dotaParams);
		 */
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, mViewPager.getCurrentItem());
	}

	/*
	 * public ArrayList<Bitmap> getBitMapFromRegion(ArrayList<String> imgs){
	 * ArrayList<Bitmap> bits = new ArrayList<Bitmap>(); for(int
	 * i=0;i<imgs.size();i++){ bits.add(getBitMapByUrl(imgs.get(i))); } return
	 * bits; }
	 */

	private class SamplePagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return smallStr.size();
		}

		public View instantiateItem(ViewGroup container, final int position) {
			final PhotoView photoView = new PhotoView(ImageViewPager.this);
			final boolean bigImg = imageLoader.getDiscCache()
					.get(bigStr.get(position)).exists();
			if (bigImg) {
				imageLoader.displayImage(bigStr.get(position), photoView,
						options);
			} else {
				final File smallFile = imageLoader.getDiscCache().get(
						smallStr.get(position));
				final boolean smallImg = smallFile.exists();
				imageLoader.displayImage(bigStr.get(position), photoView,
						options, new SimpleImageLoadingListener() {
							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								if (smallImg) {
									photoView.setImageBitmap(ImageUtils
											.getBitmapByFile(smallFile));
								}
								if (!bigImg)
									mProgressBar.setVisibility(View.VISIBLE);
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								String message = null;
								switch (failReason.getType()) {
								case IO_ERROR:
									message = "Input/Output error";
									break;
								case DECODING_ERROR:
									message = "Image can't be decoded";
									break;
								case NETWORK_DENIED:
									message = "Downloads are denied";
									break;
								case OUT_OF_MEMORY:
									message = "Out Of Memory error";
									break;
								case UNKNOWN:
									message = "Unknown error";
									break;
								}
								// Toast.makeText(ImagePagerActivity.this,
								// message, Toast.LENGTH_SHORT).show();
								UIHelper.ToastMessage(ImageViewPager.this,
										message);
								mProgressBar.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								mProgressBar.setVisibility(View.GONE);
							}
						});
			}
			((ViewPager) container).addView(photoView, 0);
			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		dota.setPos(position);
		/*
		 * final String bigURL = bigStr.get(position);
		 * if(getBitMapByUrl(bigURL)==null){ initData(position,bigURL); }
		 */
		/*
		 * File bigImg = imageLoader.getDiscCache().get(bigStr.get(position));
		 * if(bigImg==null){ initData(position); }
		 */
	}

	/*
	 * private void initData(final int position){
	 * System.out.println("initData");
	 * imageLoader.loadImage(bigStr.get(position),new
	 * SimpleImageLoadingListener() {
	 * 
	 * @Override public void onLoadingStarted(String imageUri, View view) {
	 * mProgressBar.setVisibility(View.VISIBLE); }
	 * 
	 * @Override public void onLoadingFailed(String imageUri, View view,
	 * FailReason failReason) { String message = null; switch
	 * (failReason.getType()) { case IO_ERROR: message = "Input/Output error";
	 * break; case DECODING_ERROR: message = "Image can't be decoded"; break;
	 * case NETWORK_DENIED: message = "Downloads are denied"; break; case
	 * OUT_OF_MEMORY: message = "Out Of Memory error"; break; case UNKNOWN:
	 * message = "Unknown error"; break; }
	 * //Toast.makeText(ImagePagerActivity.this, message,
	 * Toast.LENGTH_SHORT).show(); UIHelper.ToastMessage(ImageViewPager.this,
	 * message); mProgressBar.setVisibility(View.GONE); }
	 * 
	 * @Override public void onLoadingComplete(String imageUri, View view,
	 * Bitmap loadedImage) { mProgressBar.setVisibility(View.GONE); } }); }
	 */
	// 线程加载图片
	/*
	 * private void initData(final int position,final String bigURL) { //final
	 * String ErrMsg = getString(R.string.msg_load_image_fail); final
	 * ProgressDialog mProgress = ProgressDialog.show(ImageViewPager.this, null,
	 * "加载中···",true,true); //spinner.setVisibility(View.VISIBLE);
	 * mProgressBar.setVisibility(View.VISIBLE); final Handler handler = new
	 * Handler(){ public void handleMessage(Message msg) {
	 * //spinner.setVisibility(View.GONE); if(mProgress!=null)
	 * mProgress.dismiss(); mProgressBar.setVisibility(View.GONE);
	 * if(msg.what==1 && msg.obj != null){ mViewPager.setAdapter(new
	 * SamplePagerAdapter()); mViewPager.setCurrentItem(position); //new
	 * SamplePagerAdapter().instantiateItem(mViewPager, position); }else{
	 * Toast.makeText(ImageViewPager.this, "大图片加载异常", 5).show(); } } }; new
	 * Thread(){ public void run() { Message msg = new Message(); Bitmap bmp =
	 * null; String filename = FileUtils.getFileName(bigURL); try { if(bmp ==
	 * null){ bmp = ApiClient.getNetBitmap(bigURL); if(bmp != null){ try {
	 * //写图片缓存 ImageUtils.saveImage(ImageViewPager.this, filename, bmp); } catch
	 * (IOException e) { e.printStackTrace(); } //缩放图片 bmp =
	 * ImageUtils.reDrawBitMap(ImageViewPager.this, bmp); } } msg.what = 1;
	 * msg.obj = bmp; } catch (AppException e) { e.printStackTrace(); msg.what =
	 * -1; msg.obj = e; } handler.sendMessage(msg); } }.start();
	 * 
	 * }
	 */

	// 替换小图片成功为true,失败为false
	/*
	 * private boolean isExistBigImg(int index){ boolean result = false;
	 * if(getBitMapByUrl(bigStr.get(index))!=null) result = true;
	 * 
	 * return result; }
	 */

	// 通过URL获取bitmap,失败返回null
	/*
	 * private Bitmap getBitMapByUrl(String imgURL){ String filename =
	 * FileUtils.getFileName(imgURL); String filepath = getFilesDir() +
	 * File.separator + filename; File file = new File(filepath); Bitmap bmp =
	 * null; if(file.exists()){ bmp = ImageUtils.getBitmap(ImageViewPager.this,
	 * filename); } return bmp; }
	 */
}
