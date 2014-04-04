package com.starbaby.diyBook.fragment;
/**
 * 分享好友
 * @author Administrator
 *
 */
import com.starbaby.diyBook.R;
import com.starbaby.diyBook.main.Share;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;


public class Fragment6 extends Fragment implements OnClickListener{
	private ImageView iv1,iv2;
	public Fragment6(){
		
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		if (container == null) 
		{
            return null;
        }
		LayoutInflater myInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	    View layout = myInflater.inflate(R.layout.fragment_6, container, false); 
	    iv1 =(ImageView)layout. findViewById(R.id.fragment6_iv1);
	    iv2 =(ImageView)layout. findViewById(R.id.fragment6_iv2);
	    iv1.setOnClickListener(this);
	    iv2.setOnClickListener(this);
		return layout;
	}
	@Override
	public void onClick(View v) {
		Intent intent = new Intent(Fragment6.this.getActivity(),Share.class);
		switch(v.getId()){
		case R.id.fragment6_iv1:
			intent.putExtra("name", "weChatFriend");
			break;
		case R.id.fragment6_iv2:
			intent.putExtra("name", "weChat");
			break;
		}
		startActivity(intent);
	}
}
