package com.starbaby.diyBook.utils;

import java.util.ArrayList;
/**
 * 书本信息 
 * 每个分类下的所有书本信息 
 * @author Administrator
 *
 */
public class JavaBean {
	public static ArrayList<String> bookList = new ArrayList<String>();
	public static ArrayList<String> bookCoverList = new ArrayList<String>();
	public static ArrayList<String> bookNameList = new ArrayList<String>();
	public static ArrayList<String> bookIdList = new ArrayList<String>();
	public static ArrayList<String> bookUpdateTime = new ArrayList<String>();
	public static ArrayList<String> getBookList() {
		return bookList;
	}
	public static void setBookList(ArrayList<String> bookList) {
		JavaBean.bookList = bookList;
	}
	public static ArrayList<String> getBookCoverList() {
		return bookCoverList;
	}
	public static void setBookCoverList(ArrayList<String> bookCoverList) {
		JavaBean.bookCoverList = bookCoverList;
	}
	public static ArrayList<String> getBookNameList() {
		return bookNameList;
	}
	public static void setBookNameList(ArrayList<String> bookNameList) {
		JavaBean.bookNameList = bookNameList;
	}
	public static ArrayList<String> getBookIdList() {
		return bookIdList;
	}
	public static void setBookIdList(ArrayList<String> bookIdList) {
		JavaBean.bookIdList = bookIdList;
	}
	public static ArrayList<String> getBookUpdateTime() {
		return bookUpdateTime;
	}
	public static void setBookUpdateTime(ArrayList<String> bookUpdateTime) {
		JavaBean.bookUpdateTime = bookUpdateTime;
	}
	
}
