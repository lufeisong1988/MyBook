package com.starbaby.diyBook.utils;

import java.util.ArrayList;
/**
 * 用户中心 个人作品
 * @author Administrator
 *
 */
public class JavaBean3 {
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
		JavaBean3.bookUpdateTime = bookUpdateTime;
	}
	
}
