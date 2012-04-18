package com.shunote.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import android.util.Log;

/**
 * WebClient
 * @author Jeffrey
 *
 */
public class WebClient {
	
	private static WebClient instance= null;
	
	DefaultHttpClient httpclient;
	
	final String LOGIN_URL1 = "http://shunote.com/zhishidian/user";
	final String LOGIN_URL2 = "http://shunote.com/zhishidian/j_security_check";
	
	private final String host = "http://shunote.com/zhishidian";
	
	private WebClient(){};
	
	/**
	 * Singleton
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
	 * Login
	 * @param pairs 
	 * @return CookieStore
	 */
	public CookieStore Login(List<NameValuePair> pairs){

		CookieStore localCookieStore = new BasicCookieStore();
		
		try {

			// /zhishidian/user/  

			HttpGet httpGet = new HttpGet(LOGIN_URL1);
			httpclient.execute(httpGet);
			CookieStore cookieStore1 = httpclient.getCookieStore();

			HttpContext context = new BasicHttpContext();

			// session
			HttpPost httpPost = new HttpPost(LOGIN_URL2);
			httpPost.setEntity(new UrlEncodedFormEntity(pairs));
			httpclient.setCookieStore(cookieStore1);
			HttpResponse response2 = httpclient.execute(httpPost, context);
			Log.v("WEBCLIENT",response2.getStatusLine().toString());
			
			//cookie
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
		    Log.v("WEBCLIENT","return url:"+currentUrl);
			

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return localCookieStore;
	}
	
	/**
	 * Get Data
	 * @param relatedurl url
	 * @param cookieStore local CookieStore
	 * @return data
	 */
	public String GetData(String relatedurl,CookieStore cookieStore){
		
		String result = "";
		
		httpclient.setCookieStore(cookieStore);

		// HTTPGET
		String url = host + relatedurl;

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
	
	public String PostData(String url,CookieStore cookieStore,List<NameValuePair> nameValuePairs){
		
		String resultstr = null;
		httpclient.setCookieStore(cookieStore);
		
		url = host + url;
		
		Log.v("WEBCLIENT.PostData","url="+url);
		
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);

        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
        
		HttpPost httpPost = new HttpPost(url);
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
			
			HttpResponse response = httpclient.execute(httpPost);
	         
	         resultstr =EntityUtils.toString(response.getEntity());
	         
	         Log.v("WEBCLIENT.POSTDATA","return"+resultstr);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

        
         
		return resultstr;
	}
	
	public String DelData(String url,CookieStore cookieStore) {
		
		String resultstr = null;
		httpclient.setCookieStore(cookieStore);
		
		url = host + url;
		
		HttpDelete httpDel = new HttpDelete(url);
		
		HttpResponse response;
		try {
			response = httpclient.execute(httpDel);
			resultstr = EntityUtils.toString(response.getEntity());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return resultstr;
	}
		
}
