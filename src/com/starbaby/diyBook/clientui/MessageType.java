package com.starbaby.diyBook.clientui;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientbean.FriendPostList;
import com.starbaby.diyBook.clientcommon.UIHelper;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
 

public class MessageType extends BaseActivity implements OnClickListener{
 
	private LinearLayout chatLL;
	private LinearLayout diaryLL;
	private LinearLayout momentLL;
	private ImageButton closeBtn;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pub_message_type);
		
		this.initView();
		
	}
	
	private void initView(){
		chatLL = (LinearLayout)findViewById(R.id.pub_type_chat_ll);
		diaryLL = (LinearLayout)findViewById(R.id.pub_type_diary_ll);
		momentLL = (LinearLayout)findViewById(R.id.pub_type_moment_ll);
		closeBtn = (ImageButton)findViewById(R.id.message_type_close_button);
		
		chatLL.setOnClickListener(this);
		diaryLL.setOnClickListener(this);
		momentLL.setOnClickListener(this);
		closeBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v==chatLL){
			UIHelper.showMessagePub(MessageType.this,FriendPostList.POST_TYPE_CHAT);
			finish();
		}else if(v==diaryLL){
			UIHelper.showMessagePub(MessageType.this,FriendPostList.POST_TYPE_DIARY);
			finish();
		}else if( v==momentLL){
			UIHelper.showMessagePub(MessageType.this,FriendPostList.POST_TYPE_MOMENT);
			finish();
		} else{
			finish();
		}
	}
}
