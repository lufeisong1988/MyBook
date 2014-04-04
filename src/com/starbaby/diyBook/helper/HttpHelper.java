package com.starbaby.diyBook.helper;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.starbaby.diyBook.utils.HttpPort;
/**
 * get网络请求类
 * @author Administrator
 *
 */


public class HttpHelper {
	public static String GetNewBook(String url) throws ParseException, IOException{
//		HttpGet get = new HttpGet(url);
//		HttpClient client = new DefaultHttpClient();
//		HttpResponse response = client.execute(get);
//		//设置连接超时时间为40s
//		HttpConnectionParams.setConnectionTimeout(client.getParams(),40000);
//		// 设置读取超时为20s
//		HttpConnectionParams.setSoTimeout(client.getParams(),20000);
//		HttpEntity entity = response.getEntity();
//		return EntityUtils.toString(entity, "UTF-8");
		String result = null;
		HttpClient client = new DefaultHttpClient();
		//设置连接超时时间为40s
		HttpConnectionParams.setConnectionTimeout(client.getParams(),40000);
		//设置读取超时为20s
		HttpConnectionParams.setSoTimeout(client.getParams(),20000);
		//使用get请求
		HttpGet get = new HttpGet(url);
		//执行请求，获取响应结果
		HttpResponse respone;
		try {
			respone = client.execute(get);
			//获得响应状态码
			int code = respone.getStatusLine().getStatusCode() ;
			//判断请求是否成功
			if(code == 200){
				result = EntityUtils.toString(respone.getEntity());
			}else{
				result = null;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
