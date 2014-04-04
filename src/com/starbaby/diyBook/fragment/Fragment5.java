package com.starbaby.diyBook.fragment;
/**
 * 计算空间大小
 */
import java.io.File;
import java.text.DecimalFormat;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.controller.CalculateFile;
import com.starbaby.diyBook.controller.DeleteCache;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.commentDialogUtils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Administrator
 *
 */
@SuppressLint({ "ValidFragment", "ShowToast", "HandlerLeak", "UseValueOf" })
public class Fragment5 extends Fragment implements OnClickListener{
	private TextView tv1,tv2,tv3;
	private Button bnt;
	private long totalSize = 0,appSize = 0;
	private long count;
	private String room,totalRoom;
	private Context mContext;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				mDialog.dismiss();
				showInfo();
				Toast.makeText(mContext, "清理完成", 1000).show();
				break;
			}
			super.handleMessage(msg);
		}
	};
	public Fragment5(){
	}
	public Fragment5(Context mContext){
		this.mContext = mContext;
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) 
	{
		if (container == null) 
		{
            return null;
        }
		LayoutInflater myInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
	    View layout = myInflater.inflate(R.layout.fragment_4, container, false); 
	    tv1 = (TextView)layout.findViewById(R.id.fragment4_tv1);
	    tv2 = (TextView)layout.findViewById(R.id.fragment4_tv2);
	    tv3 = (TextView)layout.findViewById(R.id.fragment4_tv3);
	    bnt = (Button)layout.findViewById(R.id.fragment4_bnt);
	    bnt.setOnClickListener(this);
	    showInfo();
		return layout;
	}
	private void showInfo(){
		try {
			if(!new File(Utils.basePath1 ).exists()){
				room = "0 M";
				count = 0;
				totalSize = 0;
			}else{
				totalSize = CalculateFile.getFolderSize(new File(Utils.basePath1 ));
				room = CalculateFile.setFileSize(totalSize);
				count =  Utils.mDBHelper.BookCount();
			}
			if(!new File(Utils.basePath ).exists()){
				totalRoom = "0 M";
			}else{
				appSize = CalculateFile.getFolderSize(new File(Utils.basePath ));
				totalRoom = CalculateFile.setFileSize(appSize - totalSize);
			}
			tv2.setText("本地书籍" + count +"本");
			tv1.setText("已用(可释放)空间:" + room );
			tv3.setText("其他数据 :" + totalRoom);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.fragment4_bnt:
			ShowChoice();
			break;
		case R.id.choice_iBnt1:
			mDialog.dismiss();
			break;
		case R.id.choice_iBnt2:
			
			pb.setVisibility(1);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					if(new File(Utils.basePath1).exists()){
						DeleteCache.delAllFile(Utils.basePath1);
						Utils.mDBHelper.deleteListData();
					}
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
				}
			}).start();
			break;
		}
	}  
	Dialog mDialog;
	ProgressBar pb;
	void ShowChoice(){
		mDialog = new commentDialogUtils(mContext, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, R.layout.choice, R.style.Theme_dialog);
		ImageButton cancle =(ImageButton)mDialog. findViewById(R.id.choice_iBnt1);
		ImageButton ensure =(ImageButton)mDialog. findViewById(R.id.choice_iBnt2);
		pb =(ProgressBar)mDialog. findViewById(R.id.choice_pb);
		pb.setVisibility(8);
		cancle.setOnClickListener(this);
		ensure.setOnClickListener(this);
		mDialog.show();
	}
}
