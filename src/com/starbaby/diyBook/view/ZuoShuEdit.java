package com.starbaby.diyBook.view;

import com.starbaby.diyBook.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

public class ZuoShuEdit extends Activity implements OnClickListener{
	ImageButton close;
	Button commit;
	EditText name,info;
	String nameEdit,infoEdit;
	RelativeLayout parent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zuoshu_info_edit);
		init();
		listener();
	}
	void init(){
		close = (ImageButton) findViewById(R.id.zuoshu_close);
		commit = (Button) findViewById(R.id.zuoshu_ok);
		name = (EditText) findViewById(R.id.edit_name);
		info = (EditText) findViewById(R.id.edit_introduce);
		parent = (RelativeLayout) findViewById(R.id.zuoshu_parent);
	}
	void listener(){
		close.setOnClickListener(this);
		commit.setOnClickListener(this);
		parent.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.zuoshu_parent:
			((InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ZuoShuEdit.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
			break;
		case R.id.zuoshu_close://关闭
			((InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ZuoShuEdit.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
			setResult(4, null);
			this.finish();
			break;
		case R.id.zuoshu_ok://提交
			((InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ZuoShuEdit.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
			nameEdit = name.getEditableText().toString();
			infoEdit = info.getEditableText().toString();
			if(nameEdit == null || nameEdit.equals("") || infoEdit == null || infoEdit.equals("")){
				Toast.makeText(this, "请输入完整信息", Toast.LENGTH_LONG).show();
			}else{
				if(nameEdit.length() < 8 ){
					Toast.makeText(this, "标题少于8字符", Toast.LENGTH_LONG).show();
				}else if(infoEdit.length() < 12){
					Toast.makeText(this, "描述少于12个字符", Toast.LENGTH_LONG).show();
				}else{
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("name", nameEdit);
					bundle.putString("info", infoEdit);
					intent.putExtras(bundle);
					setResult(3, intent);
					this.finish();
				}
			}
			break;
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		((InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(ZuoShuEdit.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS); 
		return super.onTouchEvent(event);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.i("back","noway");
	}
	
}
