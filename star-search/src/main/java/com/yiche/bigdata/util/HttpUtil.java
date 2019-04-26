package com.yiche.bigdata.util;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpUtil {

	public static String get(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpEntity entit = null;
		String retrunstr = "";
		try {
			// 创建httpget.
			HttpGet httpget = new HttpGet(url);
			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpget);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				retrunstr = EntityUtils.toString(entity);
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retrunstr;
	}
	
	public static String post(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		String retrunstr = "";
		try {
			// 创建httpget.
			HttpPost httpPost = new HttpPost(url);
			// 执行get请求.
			CloseableHttpResponse response = httpclient.execute(httpPost);
			try {
				// 获取响应实体
				HttpEntity entity = response.getEntity();
				retrunstr = EntityUtils.toString(entity);
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭连接,释放资源
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retrunstr;
	}

}
