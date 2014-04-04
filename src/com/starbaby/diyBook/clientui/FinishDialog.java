package com.starbaby.diyBook.clientui;


import com.starbaby.diyBook.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;


public class FinishDialog extends BaseActivity {
	
	private Button  btn_ok;
	private Button  btn_cle;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.finish_dialog);
		btn_ok = (Button)findViewById(R.id.finish_btn0);
		btn_cle = (Button)findViewById(R.id.finish_btn1);
		btn_ok.setOnClickListener(okClickListener);
		btn_cle.setOnClickListener(cleClickListener);
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
		     intent.putExtra("isFinish", true);
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
