package com.starbaby.diyBook.fragment;
/**
 * 系统设置
 */
import java.io.File;
import java.text.DecimalFormat;

import com.starbaby.diyBook.R;
import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.controller.CheckUpdate;
import com.starbaby.diyBook.controller.DeleteCache;
import com.starbaby.diyBook.controller.Update;
import com.starbaby.diyBook.main.MainActivity;
import com.starbaby.diyBook.main.UserInfo;
import com.starbaby.diyBook.utils.SdcardpathUtils;
import com.starbaby.diyBook.utils.Utils;
import com.starbaby.diyBook.utils.commentDialogUtils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

@SuppressLint({ "ValidFragment", "UseValueOf", "HandlerLeak", "ShowToast", "WorldReadableFiles" })
public class Fragment4 extends Fragment implements OnClickListener{
	Context mContext;
	RelativeLayout rl3,rl4,rl6;
	ToggleButton tb1,tb2;
	TextView tv;
	private SharedPreferences sp;
	private int uid ;
	private String avatar;
	private String psw;
	private int bWifi = 1;
	private int bVolum = 1;
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 1:
				clearDialog.dismiss();
				Toast.makeText(mContext, "清理完成", 1000).show();
				break;
			}
			super.handleMessage(msg);
		}
	};
	@SuppressWarnings({ "static-access", "deprecation" })
	public Fragment4() {
	}
	public Fragment4(Context mContext) {
		this.mContext = mContext;
		sp = mContext.getSharedPreferences("diyBook",mContext.MODE_WORLD_READABLE);
		uid = sp.getInt("uid",0);
		avatar = sp.getString("avatar", "");
		psw = sp.getString("psw", "");
		bWifi = sp.getInt("wifi", 0);
		bVolum = sp.getInt("volum", 0);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if(container == null){
			return null;
		}
		LayoutInflater inflate = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflate.inflate(R.layout.fragment_2, container,false);
		rl3 =(RelativeLayout)layout. findViewById(R.id.fragment_2_rl3);
		rl4 =(RelativeLayout)layout. findViewById(R.id.fragment_2_rl4);
		rl6 =(RelativeLayout)layout. findViewById(R.id.fragment_2_rl6);
		tb1 =(ToggleButton)layout. findViewById(R.id.fragment_2_rb1);
		tb2 =(ToggleButton)layout. findViewById(R.id.fragment_2_rb2);
		tv = (TextView)layout. findViewById(R.id.fragemtn_2_tv7);
		tv .setText("(当前版本号:" + new Update().getVerName(mContext) + ")");
		listener();
		if(bWifi == 1){
			tb1.setBackgroundResource(R.drawable.on);
		}else if(bWifi == 2){
			tb1.setBackgroundResource(R.drawable.off);
		}
		if(bVolum == 1){
			tb2.setBackgroundResource(R.drawable.on);
		}else if(bVolum == 2){
			tb2.setBackgroundResource(R.drawable.off);
		}
		if(uid != 0 && psw != null && !psw.equals("")){//已经登入
			
		}else{//没有登入
			rl6.setVisibility(View.GONE);
		}
		return layout;
	}
	void listener(){
		rl3.setOnClickListener(this);
		rl4.setOnClickListener(this);
		rl6.setOnClickListener(this);
		tb1.setOnClickListener(this);
		tb2.setOnClickListener(this);
	}
	@SuppressWarnings({ "static-access", "deprecation" })
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.fragment_2_rl3://空间管理
			showZone();
			break;
		case R.id.fragment_2_rl4://版本管理
			new CheckUpdate(mContext,true).checkUpdate();
			break;
		case R.id.fragment_2_rl6://退出登入
			File headImgFile = new File(SdcardpathUtils.headName);
			if (headImgFile.exists()) {
				headImgFile.delete();
			}
			SharedPreferences userInfo = mContext.getSharedPreferences("diyBook",mContext.MODE_WORLD_READABLE);
			SharedPreferences.Editor editor = userInfo.edit();
			editor.clear();
			editor.commit();
			AppContext ac = (AppContext) mContext.getApplicationContext();;
			ac.cleanLoginInfo();//清除登陆信息
			//删除个人收藏信息
			Utils.mDBUserInfoHelper.deleteCollectBook();
			DeleteCache.delAllFile(Utils.coverPath + Utils.collect_uid);
			Fragment2 detail = new Fragment2(getActivity());
			Fragment4 detail4 = new Fragment4(getActivity());
			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.userinfo_contains, detail).remove(detail4).commit();
			Toast.makeText(mContext, "退出成功",1000).show();
			UserInfo.bnt1.setTextColor(0xFFFF0000);
			UserInfo.bnt2.setColor(0xFFFF0000);
			UserInfo.bnt6.setColor(0xFFFFFFFF);
			UserInfo.bnt8.setTextColor(0xFFFF0000);
			UserInfo.theme.setTextColor(0xFFFF0000);
			UserInfo.bnt1.setBackgroundResource(R.drawable.userinfo_left_off);
			UserInfo.bnt2.setBackgroundResource(R.drawable.userinfo_choose_in);
			UserInfo.bnt6.setBackgroundResource(R.drawable.userinfo_choose_off);
			UserInfo.bnt8.setBackgroundResource(R.drawable.userinfo_right_off);
			UserInfo.theme.setBackgroundResource(R.drawable.userinfo_mid_off);
			UserInfo.attentionTv.setText("0");
			UserInfo.fansTv.setText("0");
			UserInfo.name_tv.setVisibility(View.INVISIBLE);
			UserInfo.msgTv.setVisibility(View.INVISIBLE);
			MainActivity.msgTv.setVisibility(View.INVISIBLE);
			break;
		case R.id.fragment_2_rb1://wifi
			SharedPreferences.Editor edit = sp.edit();
			if(tb1.isChecked()){
				edit.putInt("wifi", 1);//打开
				tb1.setBackgroundResource(R.drawable.on);
			}else{
				edit.putInt("wifi", 2);//不打开
				tb1.setBackgroundResource(R.drawable.off);
			}
			edit.commit();
			break;
		case R.id.fragment_2_rb2://声音
			SharedPreferences.Editor edit2 = sp.edit();
			if(tb2.isChecked()){
				edit2.putInt("volum", 1);//打开
				tb2.setBackgroundResource(R.drawable.on);
			}else{
				edit2.putInt("volum", 2);//不打开
				tb2.setBackgroundResource(R.drawable.off);
			}
			edit2.commit();
			break;
		case R.id.fragment4_bnt://清空缓存
			ShowChoice();
			mDialog.dismiss();
			break;
		case R.id.choice_iBnt1:
			clearDialog.dismiss();
			break;
		case R.id.choice_iBnt2:
			pb.setVisibility(1);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					if(new File(Utils.basePath1).exists()){
						DeleteCache.delAllFile(Utils.basePath1);
						Utils.mDBHelper.deleteListData();
						Utils.mDBUserInfoHelper.deleteLocalAndCollect();
					}
					if(new File(Utils.zuoshuCache).exists()){
						DeleteCache.delAllFile(Utils.zuoshuCache);
						Utils.mDBPlayinfoHelper.deleteAllInfo();
					}
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
				}
			}).start();
			break;
		}
	}
	/**
	 * 展示空间存储信息
	 */
	long totalSize = 0,appSize = 0;
	long count;
	String room,totalRoom;
	Dialog mDialog;
	TextView tv1,tv2,tv3;
	Button bnt;
	void showZone(){
		mDialog = new commentDialogUtils(mContext, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, R.layout.fragment_4, R.style.Theme_dialog);
		tv1 = (TextView) mDialog.findViewById(R.id.fragment4_tv1);
		tv2 = (TextView) mDialog.findViewById(R.id.fragment4_tv2);
		tv3 = (TextView) mDialog.findViewById(R.id.fragment4_tv3);
		bnt = (Button) mDialog.findViewById(R.id.fragment4_bnt);
		showInfo();
		bnt.setOnClickListener(this);
		mDialog.show();
	}
	private void showInfo(){
		try {
			if(!new File(Utils.basePath1 ).exists()){
				room = "0 M";
				count = 0;
				totalSize = 0;
			}else{
				totalSize = getFolderSize(new File(Utils.basePath1 ));
				room = setFileSize(totalSize);
				count =  Utils.mDBHelper.BookCount();
			}
			if(!new File(Utils.basePath ).exists()){
				totalRoom = "0 M";
			}else{
				appSize = getFolderSize(new File(Utils.basePath ));
				totalRoom = setFileSize(appSize - totalSize);
			}
			tv2.setText("本地书籍" + count +"本");
			tv1.setText("已用(可释放)空间:" + room );
			tv3.setText("其他数据 :" + totalRoom);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/*
     * 获取文件夹大小 
     * @param file File实例 
     * @return long 单位为M 
     * @throws Exception 
     */  
    public static long getFolderSize(java.io.File file)throws Exception{  
        long size = 0;  
        java.io.File[] fileList = file.listFiles();  
        for (int i = 0; i < fileList.length; i++)  
        {  
            if (fileList[i].isDirectory())  
            {  
                size = size + getFolderSize(fileList[i]);  
            } else  
            {  
                size = size + fileList[i].length();  
            }  
        }  
        return size;  
    }  
    /*
     * 文件大小单位转换 
     *  
     * @param size 
     * @return 
     */  
    public static String setFileSize(long size) {  
        DecimalFormat df = new DecimalFormat("###.##");  
        float f = ((float) size / (float) (1024 * 1024));  
        if (f < 1.0) {  
            float f2 = ((float) size / (float) (1024));  
            return df.format(new Float(f2).doubleValue()) + "KB";  
        } else {  
            return df.format(new Float(f).doubleValue()) + "M";  
        }  
    }
    /*
     * 清空缓存
     */
    Dialog clearDialog;
	ProgressBar pb;
	void ShowChoice(){
		clearDialog = new commentDialogUtils(mContext, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, R.layout.choice, R.style.Theme_dialog);
		ImageButton cancle =(ImageButton)clearDialog. findViewById(R.id.choice_iBnt1);
		ImageButton ensure =(ImageButton)clearDialog. findViewById(R.id.choice_iBnt2);
		pb =(ProgressBar)clearDialog. findViewById(R.id.choice_pb);
		pb.setVisibility(8);
		cancle.setOnClickListener(this);
		ensure.setOnClickListener(this);
		clearDialog.show();
	}
}
