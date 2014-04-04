package com.starbaby.diyBook.clientui;

import java.util.ArrayList;
import java.util.List;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.adapter.ViewPagerAdapter;
import com.starbaby.diyBook.clientcommon.ImageUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ImageUploadPager extends Activity implements View.OnClickListener,
		OnPageChangeListener {
	private ImageButton backHome;
	private ImageButton delImg;

	private LinearLayout ll;
	private ViewPager vp;
	private ViewPagerAdapter vpAdapter;
	private ArrayList<View> views = new ArrayList<View>();
	private LinearLayout.LayoutParams mParams;

	/* private ArrayList<Bitmap> smallPics = new ArrayList<Bitmap>(); */
	private ArrayList<String> pics = new ArrayList<String>();
	// 底部小点图片
	private ImageView[] dots;

	private ImageView[] images;
	// 记录当前选中位置
	private int currentIndex;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_upload_pager);
		mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);

		initData();

		initView();
	}

	private void initData() {
		pics = getIntent().getStringArrayListExtra("list");
		// System.out.println("smaooPics==="+pics.size());
		currentIndex = getIntent().getIntExtra("position", 0);
	}

	private void initView() {
		backHome = (ImageButton) findViewById(R.id.image_upload_pager_back_btn);
		delImg = (ImageButton) findViewById(R.id.image_upload_pager_delete_btn);
		backHome.setOnClickListener(backHomeListener);
		delImg.setOnClickListener(delImgListener);

		vp = (ViewPager) findViewById(R.id.image_upload_pager_viewpager);

		ll = (LinearLayout) findViewById(R.id.image_upload_pager_linear_dot);

		images = new ImageView[pics.size()];
		// 初始化引导图片列表
		for (int i = 0; i < pics.size(); i++) {
			images[i] = new ImageView(this);
			images[i].setLayoutParams(mParams);
			images[i].setImageBitmap(ImageUtils.getBitmapByPath(pics.get(i)));
			views.add(images[i]);
		}

		// 初始化Adapter
		vpAdapter = new ViewPagerAdapter(views);
		vp.setAdapter(vpAdapter);
		vp.setCurrentItem(currentIndex);
		// 绑定回调
		vp.setOnPageChangeListener(this);
		// 初始化底部小点
		initDots();
	}

	private View.OnClickListener backHomeListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			setResult(RESULT_OK);
			finish();
		}
	};

	private View.OnClickListener delImgListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.putExtra("position", currentIndex);
			intent.putExtra("isDel", true);
			setResult(RESULT_OK, intent);
			finish();
		}
	};

	private void initDots() {
		dots = new ImageView[pics.size()];
		// System.out.println("dots==="+dots.length);
		// 循环取得小点图片
		for (int i = 0; i < pics.size(); i++) {
			dots[i] = new ImageView(this);
			dots[i].setLayoutParams(mParams);
			dots[i].setPadding(15, 15, 15, 15);
			dots[i].setImageResource(R.drawable.dot);
			dots[i].setOnClickListener(this);
			dots[i].setEnabled(true);
			dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
			ll.addView(dots[i]);
		}
		dots[currentIndex].setEnabled(false);// 即选中状态
	}

	/**
	 * 设置当前的引导页
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= pics.size()) {
			return;
		}
		vp.setCurrentItem(position);
	}

	/**
	 * 设置当前引导小点的选中
	 */
	private void setCurDot(int position) {
		if (position < 0 || position > pics.size() - 1
				|| currentIndex == position) {
			return;
		}
		dots[position].setEnabled(false);
		dots[currentIndex].setEnabled(true);
		currentIndex = position;
	}

	/**
	 * 当滑动状态改变时调用
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	// 当当前页面被滑动时调用
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	// 当新的页面被选中时调用
	@Override
	public void onPageSelected(int position) {
		// 设置底部小点选中状态
		setCurDot(position);
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
	}

}
