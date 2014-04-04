package com.starbaby.diyBook.clientbean;



import java.io.Serializable;

/**
 * 实体基类：实现序列化
 * @author stone(fanlei123126@163.com)
 * @version 1.0
 * @created 2013-3-29
 */
public abstract class Base implements Serializable {

	public final static String UTF8 = "UTF-8";
	public final static String NODE_ROOT = "starbaby";
	
	protected int errorCode;
	//protected NoticeMsg noticeMsg;
	
	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
/*
	public NoticeMsg getNoticeMsg() {
		return noticeMsg;
	}

	public void setNoticeMsg(NoticeMsg noticeMsg) {
		this.noticeMsg = noticeMsg;
	}
*/
}
