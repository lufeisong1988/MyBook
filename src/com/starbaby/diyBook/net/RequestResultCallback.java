package com.starbaby.diyBook.net;

public interface RequestResultCallback {
	public void onSuccess(Object o);
	public void onFail(Exception e);
}
