package com.starbaby.diyBook.main;
/**
 * 更多书籍  页面
 */
import java.util.ArrayList;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.helper.BookMusicHelper;
import com.starbaby.diyBook.utils.HttpPort;
import com.starbaby.diyBook.utils.JavaBean;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.checkNetUtils;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MoreBook extends BaseActivity implements OnClickListener,OnGestureListener ,OnTouchListener{
	private Button back,bnt1,bnt2,bnt3,bnt4,bnt5,bnt6,bnt7,bnt8,bnt9,bnt10;
	private Intent intent;
	private String section1 = "宝宝益智";
	private String section2 = "精品绘本";
	private String section3 = "早教特色";
	private String section4 = "宝宝成长";
	private String section5 = "胎教故事";
	private String section6 = "幼儿教案";
	private String section7 = "幼儿学习";
	private String section8 = "儿童故事";
	private String section9 = "品牌故事";
	private String section10 = "品质出版";
	private RelativeLayout morebook;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.morebook);
		init();
		listener();
	}
	
	private void init() {
		morebook = (RelativeLayout) findViewById(R.id.morebook);
		back = (Button) findViewById(R.id.back);
		bnt1 = (Button) findViewById(R.id.bnt1);
		bnt2 = (Button) findViewById(R.id.bnt2);
		bnt3 = (Button) findViewById(R.id.bnt3);
		bnt4 = (Button) findViewById(R.id.bnt4);
		bnt5 = (Button) findViewById(R.id.bnt5);
		bnt6 = (Button) findViewById(R.id.bnt6);
		bnt7 = (Button) findViewById(R.id.bnt7);
		bnt8 = (Button) findViewById(R.id.bnt8);
		bnt9 = (Button) findViewById(R.id.bnt9);
		bnt10 = (Button) findViewById(R.id.bnt10);
		
	}
	@SuppressWarnings("deprecation")
	private void listener() {
		morebook.setOnTouchListener(this);
		morebook.setLongClickable(true);
		mGestureDetector = new GestureDetector(this);  
		bnt1.setOnClickListener(this);
		bnt2.setOnClickListener(this);
		bnt3.setOnClickListener(this);
		bnt4.setOnClickListener(this);
		bnt5.setOnClickListener(this);
		bnt6.setOnClickListener(this);
		bnt7.setOnClickListener(this);
		bnt8.setOnClickListener(this);
		bnt9.setOnClickListener(this);
		bnt10.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		JavaBean.bookIdList = new ArrayList<String>();
		JavaBean.bookCoverList = new ArrayList<String>();
		JavaBean.bookNameList = new ArrayList<String>();
		JavaBean.bookUpdateTime = new ArrayList<String>();
		new BookMusicHelper(this).pressBnt();
		switch(v.getId()){
		case R.id.back:
			this.finish();
			overridePendingTransition(R.anim.none,R.anim.sectionbook_out);
			break;
		case R.id.bnt1:
			if(checkNetUtils.checkNet(this)){
				Utils.s1++;
			}
			enterNext(HttpPort.topcate_id_1,section1,Utils.s1);
			break;
		case R.id.bnt2:
			if(checkNetUtils.checkNet(this)){
				Utils.s2++;
			}
			enterNext(HttpPort.topcate_id_2,section2,Utils.s2);
			break;
		case R.id.bnt3:
			if(checkNetUtils.checkNet(this)){
				Utils.s3++;
			}
			enterNext(HttpPort.topcate_id_3,section3,Utils.s3);
			break;
		case R.id.bnt4:
			if(checkNetUtils.checkNet(this)){
				Utils.s4++;
			}
			enterNext(HttpPort.topcate_id_4,section4,Utils.s4);
			break;
		case R.id.bnt5:
			if(checkNetUtils.checkNet(this)){
				Utils.s5++;
			}
			enterNext(HttpPort.topcate_id_5,section5,Utils.s5);
			break;
		case R.id.bnt6:
			if(checkNetUtils.checkNet(this)){
				Utils.s6++;
			}
			enterNext(HttpPort.topcate_id_6,section6,Utils.s6);
			break;
		case R.id.bnt7:
			if(checkNetUtils.checkNet(this)){
				Utils.s7++;
			}
			enterNext(HttpPort.topcate_id_7,section7,Utils.s7);
			break;
		case R.id.bnt8:
			if(checkNetUtils.checkNet(this)){
				Utils.s8++;
			}
			enterNext(HttpPort.topcate_id_8,section8,Utils.s8);
			break;
		case R.id.bnt9:
			if(checkNetUtils.checkNet(this)){
				Utils.s9++;
			}
			enterNext(HttpPort.topcate_id_9,section9,Utils.s9);
			break;
		case R.id.bnt10:
			if(checkNetUtils.checkNet(this)){
				Utils.s10++;
			}
			enterNext(HttpPort.topcate_id_10,section10,Utils.s10);
			break;
		}
		
	}
	void enterNext(String url,String section,int sNumber){
		intent = new Intent(this,BookSection.class);
		intent.putExtra("topcate_id", url);
		intent.putExtra("section", section);
		intent.putExtra("sNumber", sNumber);
		startActivity(intent);
		this.finish();
		overridePendingTransition(R.anim.fade2,R.anim.none2);
	}
	GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 100;
	private static final int FLING_MIN_VELOCITY = 0;  
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
        if (e1.getY()-e2.getY() > FLING_MIN_DISTANCE   
                   && Math.abs(velocityY) > FLING_MIN_VELOCITY) {   
        	this.finish();
			overridePendingTransition(R.anim.none,R.anim.sectionbook_out);
           } else if (e2.getY()-e1.getY() > FLING_MIN_DISTANCE   
                   && Math.abs(velocityY) > FLING_MIN_VELOCITY) {   
               Toast.makeText(this, "滑动方向有误，请反方向操作！^_^", Toast.LENGTH_SHORT).show();   
           }   
           return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	

}
