package com.shunote.HTTP;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * WebClient�࣬��������˽������ӣ����ͺͷ�������
 * @author Jeffrey
 *
 */
public class WebClient {
	
	private static WebClient instance= null;
	
	DefaultHttpClient httpclient;
	
	final String LOGIN_URL1 = "http://shunote.com/zhishidian/user";
	final String LOGIN_URL2 = "http://shunote.com/zhishidian/j_security_check";
	
	private WebClient(){};
	
	/**
	 * Singletonģʽ
	 * @return instance
	 */
	public static WebClient getInstance(){
		if (instance == null) {
			instance = new WebClient();
			instance.httpclient = new DefaultHttpClient();
			return instance;
		}else{
			return instance;
		}
	}
	
	/**
	 * ��¼����
	 * @param pairs �û���������
	 * @return CookieStore
	 */
	public CookieStore Login(List<NameValuePair> pairs){

		CookieStore localCookieStore = new BasicCookieStore();
		
		try {

			// �ȵ�¼/zhishidian/user/��ȡcookie

			HttpGet httpGet = new HttpGet(LOGIN_URL1);
			httpclient.execute(httpGet);
			CookieStore cookieStore1 = httpclient.getCookieStore();

			HttpContext context = new BasicHttpContext();

			// ����Cookie��¼��ȡ�µ�session
			HttpPost httpPost = new HttpPost(LOGIN_URL2);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
			httpclient.setCookieStore(cookieStore1);
			HttpResponse response2 = httpclient.execute(httpPost, context);
			Log.v("WEBCLIENT",response2.getStatusLine().toString());
			
			// ��ȡcookie�еĸ�����Ϣ
			HttpGet httpGet2 = new HttpGet(LOGIN_URL1);
			httpclient.execute(httpGet2);
			
			localCookieStore = httpclient.getCookieStore();

			HttpUriRequest currentReq = (HttpUriRequest) context
					.getAttribute(ExecutionContext.HTTP_REQUEST);
			HttpHost currentHost = (HttpHost) context
					.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
			String currentUrl = (currentReq.getURI().isAbsolute()) ? currentReq
					.getURI().toString()
					: (currentHost.toURI() + currentReq.getURI());
		    Log.v("WEBCLIENT","��ǰ��ַΪ:"+currentUrl);
			

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return localCookieStore;
	}
	
	/**
	 * ��ȡ���ݷ���
	 * @param relatedurl ���·��
	 * @param cookieStore ���ش洢��CookieStore
	 * @return data
	 */
	public String GetData(String relatedurl,CookieStore cookieStore){
		
		String result = "";
		
		httpclient.setCookieStore(cookieStore);

		// ����HTTPGET����
		String url = "http://shunote.com/zhishidian" + relatedurl;

		HttpGet httpGet = new HttpGet(url);

		try {
			HttpResponse httpResponse = httpclient.execute(httpGet);
			result = EntityUtils.toString(httpResponse.getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
}
