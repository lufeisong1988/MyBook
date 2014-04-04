package com.starbaby.diyBook.main;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
/**
 * 继承FragmentActivity类。实现登入8小时后重新登入
 */
import com.starbaby.diyBook.utils.SdcardpathUtils;
import com.starbaby.diyBook.utils.Utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

public class BaseFActivity extends FragmentActivity{
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss     ");
	Date currentDate;
	@Override
	protected void onResume() {
		super.onResume();
		  //app 从后台唤醒，进入前台
		currentDate = new Date(System.currentTimeMillis());
		SharedPreferences sp = this.getSharedPreferences("diyBook", MODE_WORLD_READABLE);
		int uid = sp.getInt("uid", 0);
		Cursor mCursor = Utils.mDBUserInfoHelper.getEnterTime(uid);
		if(mCursor .getCount() > 0){
			mCursor.moveToFirst();
			String str = mCursor.getString(mCursor.getColumnIndex("TIME"));
			Date enterTime;
			try {
				enterTime = formatter.parse(str);
				long diff = currentDate.getTime() - enterTime.getTime();
				if(diff > 432000000){//大于5天
					Toast.makeText(BaseFActivity.this, "登入超时", 1000).show();
					File headImgFile = new File(SdcardpathUtils.headName);
					if (headImgFile.exists()) {
						headImgFile.delete();
					}
					SharedPreferences userInfo = getSharedPreferences("diyBook",MODE_WORLD_READABLE);
					SharedPreferences.Editor editor = userInfo.edit();
					editor.clear();
					editor.commit();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		mCursor.close();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (!isAppOnForeground()) {
			
			// app 进入后台

			// 全局变量isActive = false 记录当前已经进入后台
		}
	}
	 /**
     * 程序是否在前台运行
     * 
     * @return
     */
    public boolean isAppOnForeground() {
            // Returns a list of application processes that are running on the
            // device
             
            ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            String packageName = getApplicationContext().getPackageName();

            List<RunningAppProcessInfo> appProcesses = activityManager
                            .getRunningAppProcesses();
            if (appProcesses == null)
                    return false;

            for (RunningAppProcessInfo appProcess : appProcesses) {
                    // The name of the process that this object is associated with.
                    if (appProcess.processName.equals(packageName)
                                    && appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                            return true;
                    }
            }
            return false;
    }
}
