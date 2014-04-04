package com.starbaby.diyBook.main;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.controller.VistPageProvider;
import com.starbaby.diyBook.controller.VistSizeChangedObserver;
import com.starbaby.diyBook.helper.NamePic;
import com.starbaby.diyBook.utils.JavaBeanZuoShu;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.view.VistCurlView;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

public class VistBook extends Activity implements OnClickListener{
	
	VistCurlView mCurlView;
	VistPageProvider mPageProvider;
	VistSizeChangedObserver mSizeChangedObserver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vist_book);
		init();
	}
	void init(){
		JavaBeanZuoShu mJavaBeanZuoShu = (JavaBeanZuoShu) getIntent().getExtras().getSerializable("vistBook");
		mCurlView = (VistCurlView) findViewById(R.id.vist_book_view);
		mPageProvider = new VistPageProvider(getApplicationContext(), mJavaBeanZuoShu.playList);
		mSizeChangedObserver = new VistSizeChangedObserver(mCurlView, Utils.path + NamePic.mp3UrlToFileName(mJavaBeanZuoShu.getPlayList().get(0)));
		mCurlView.setShowMusic(false);
		mCurlView.setPageProvider(mPageProvider);
		mCurlView.setSizeChangedObserver(mSizeChangedObserver);
		mCurlView.setCurrentIndex(0);
	}
	PopupWindow menu;
	Button zuoshuBnt,kanshuBnt,addPicBnt,saveBnt,finishBnt;
	void ShowMenu(){
		View view = LayoutInflater.from(this).inflate(R.layout.zuoshu_menu, null);
		menu = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		menu.setAnimationStyle(R.style.PopupAnimation);
		menu.setBackgroundDrawable(new BitmapDrawable());
		menu.showAtLocation(findViewById(R.id.vist_parent), Gravity.RIGHT | Gravity.CENTER_VERTICAL, 0, 0);
		zuoshuBnt = (Button) view.findViewById(R.id.zuoshu_menu_bnt2);
		kanshuBnt = (Button) view.findViewById(R.id.zuoshu_menu_bnt1);
		addPicBnt = (Button) view.findViewById(R.id.zuoshu_menu_addpic);
		saveBnt = (Button) view.findViewById(R.id.zuoshu_menu_save);
		finishBnt = (Button) view.findViewById(R.id.zuoshu_menu_finish);
		addPicBnt.setVisibility(View.GONE);
		saveBnt.setVisibility(View.VISIBLE);
		saveBnt.setText("返回");
		finishBnt.setVisibility(View.INVISIBLE);
		saveBnt.setOnClickListener(this);
		menu.update();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.zuoshu_menu_save:
			this.finish();
			overridePendingTransition(R.anim.none, R.anim.vist_out);
			break;
		}
	}
	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.none, R.anim.vist_out);
	}
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		ShowMenu();
		super.onWindowFocusChanged(hasFocus);
	}
	
	
}
