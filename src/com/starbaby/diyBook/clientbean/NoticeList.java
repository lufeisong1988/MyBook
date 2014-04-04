package com.starbaby.diyBook.clientbean;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.starbaby.diyBook.clientapp.AppContext;
import com.starbaby.diyBook.clientapp.AppException;

 

public class NoticeList extends Entity{
	public final static int CATALOG_ALL = 0;
	public final static int CATALOG_COMMENT = 1;//评论主题
	public final static int CATALOG_REPLY_C = 2;//回复评论
	public final static int CATALOG_REPLY_S = 3;//回复机构评论
	public final static int CATALOG_ATTENTION = 4;//加关注
	public final static int CATALOG_FRIEND = 5;//加为好友
	public final static int CATALOG_AT = 6;//@我的
	
	private int pageSize;
	private List<Notice> noticList = new ArrayList<Notice>();
	
	public int getPageSize() {
		return pageSize;
	}

	public List<Notice> getNoticList() {
		return noticList;
	}

	public static NoticeList parseNoticeList(String str) throws AppException{
	    System.out.println("parseNoticeList="+str);
		NoticeList noticeList = new NoticeList();
		try {
			JSONObject object = new JSONObject(str);
			noticeList.msg= object.getInt("msg");
			Log.i("@我的",noticeList.msg + "");
			int size = object.getInt("pagesize");
			noticeList.pageSize = size/AppContext.PAGE_SIZE;
		    if(size>0){
		    	JSONArray dataList =object.getJSONArray("datalist");
			    for(int i=0;i<dataList.length();i++){
					  JSONObject noticeObject=	dataList.getJSONObject(i);
					  Notice notice = new Notice();
					  notice.setId(noticeObject.getInt("id"));
					  notice.setSendUid(noticeObject.getInt("sendUid"));
					  notice.setSendUname(noticeObject.getString("sendUname"));
					  notice.setAvatar(noticeObject.getString("avatar"));
					  notice.setMessage(noticeObject.getString("message"));
					  notice.setTypeId(noticeObject.getInt("typeid"));
					  if(notice.getTypeId()<4 || notice.getTypeId()==6){
						  notice.setRefid(noticeObject.getInt("refid"));
						  notice.setRefmsg(noticeObject.getString("refmsg"));
					  }
					  notice.setDeteline(noticeObject.getString("dateline"));
					  notice.setIgnore(noticeObject.getInt("ignore"));
					  noticeList.getNoticList().add(notice);
					}
		    }
		} catch (JSONException e) {
			e.printStackTrace();
			throw AppException.json(e);
		}
		 //System.out.println("list.size()"+noticeList.getNoticList().size());
		return noticeList;
	}
}
