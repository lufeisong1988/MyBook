package com.starbaby.diyBook.clientui;


import com.starbaby.diyBook.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

 

public class SnsDelDialog extends BaseActivity {
	
	private int		tid;
	private int 	position;
	private Button  btn_ok;
	private Button  btn_cle;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sns_del_dialog);
		btn_ok = (Button)findViewById(R.id.del_ok_btn);
		btn_cle = (Button)findViewById(R.id.del_cancle_btn);
		btn_ok.setOnClickListener(okClickListener);
		btn_cle.setOnClickListener(cleClickListener);
		tid = getIntent().getIntExtra("tid", 0);
		position = getIntent().getIntExtra("position", 0);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	private View.OnClickListener okClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			 Intent intent = new Intent();
		     intent.putExtra("isDel", true);
		     intent.putExtra("tid", tid);
		     intent.putExtra("position", position);
		     setResult(RESULT_OK,intent);
		     finish();
		}
	};
	
   private View.OnClickListener cleClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			finish();
		}
	};
 
	
}
