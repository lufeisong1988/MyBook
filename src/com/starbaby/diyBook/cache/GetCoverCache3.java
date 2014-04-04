package com.starbaby.diyBook.cache;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

import com.starbaby.diyBook.utils.JavaBean3;
import com.starbaby.diyBook.utils.Utils;

/**
 * 各个分类下书本的缓存3(用户中心)
 * @author Administrator
 *
 */
public class GetCoverCache3 {
	String section;
	Context mContext;
	JavaBean3 mJavaBean;
	String uid;
	public GetCoverCache3(Context mContext,JavaBean3 mJavaBean,String uid){
		this.mContext = mContext;
		this.mJavaBean = mJavaBean;
		this.uid = uid;
	}
	public boolean getData(){
		Cursor mCursor = Utils.mDBUserInfoHelper.getCoverUrl(Integer.parseInt(uid));
		mJavaBean.bookCoverList = new ArrayList<String>();
		mJavaBean.bookNameList = new ArrayList<String>();
		mJavaBean.bookIdList = new ArrayList<String>();
		mJavaBean.bookUpdateTime = new ArrayList<String>();
		if(mCursor.getCount() != 0){
			for (int i = 0; i < mCursor.getCount(); i++) {
				mJavaBean.bookCoverList.add(mCursor.getString(mCursor.getColumnIndex("COVERURL")));
				mJavaBean.bookNameList.add(mCursor.getString(mCursor.getColumnIndex("NAME")));
				mJavaBean.bookIdList.add(mCursor.getString(mCursor.getColumnIndex("ID")));
				mJavaBean.bookUpdateTime.add(mCursor.getString(mCursor.getColumnIndex("TIME")));
				mCursor.moveToNext();
			}
			mCursor.close();
			return true;
		}
		mCursor.close();
		return false;
	}
}

