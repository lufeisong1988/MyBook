package com.starbaby.diyBook.utils;

import java.util.ArrayList;

/**
 * 爱做书接口下返回的书本列表信息
 * @author Administrator
 *
 */
public class JavaBean2 {
	public static ArrayList<String> bookCoverList = new ArrayList<String>();
	public static ArrayList<String> bookNameList = new ArrayList<String>();
	public static ArrayList<String> bookIdList = new ArrayList<String>();
	public static ArrayList<String> bookUpdateTime = new ArrayList<String>();
	public static ArrayList<String> getBookCoverList() {
		return bookCoverList;
	}
	public static void setBookCoverList(ArrayList<String> bookCoverList) {
		JavaBean2.bookCoverList = bookCoverList;
	}
	public static ArrayList<String> getBookNameList() {
		return bookNameList;
	}
	public static void setBookNameList(ArrayList<String> bookNameList) {
		JavaBean2.bookNameList = bookNameList;
	}
	public static ArrayList<String> getBookIdList() {
		return bookIdList;
	}
	public static void setBookIdList(ArrayList<String> bookIdList) {
		JavaBean2.bookIdList = bookIdList;
	}
	public static ArrayList<String> getBookUpdateTime() {
		return bookUpdateTime;
	}
	public static void setBookUpdateTime(ArrayList<String> bookUpdateTime) {
		JavaBean2.bookUpdateTime = bookUpdateTime;
	}
	
}
