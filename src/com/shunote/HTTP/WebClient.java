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

import com.shunote.AppCache.Configuration;

import android.content.Context;
import android.util.Log;

/**
 * WebClient
 * @author Jeffrey
 *
 */
public class WebClient {
	
	private static WebClient instance= null;
	
	DefaultHttpClient httpclient;
	
	private String host = "";
	
	private String LOGIN_URL1 = "";
	
	private String LOGIN_URL2 = "";
	
	private String tag = "WebClient";
	
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
	
	public void init(Context con){
		Configuration config = new Configuration(con);
		host = "http://"+config.getValue("host") + ":" + config.getValue("ports");
		LOGIN_URL1 = host+"/zhishidian/user";
		LOGIN_URL2 = host+"/j_security_check";
		host = host + "/zhishidian";
		Log.d(tag,"host="+host);
		
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
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
			Log.d(tag,pairs.get(0).getName()+ ": "+pairs.get(0).getValue());
			Log.d(tag,"Login url="+LOGIN_URL1);
			HttpGet httpGet = new HttpGet(LOGIN_URL1);
			httpclient.execute(httpGet);
			CookieStore cookieStore1 = httpclient.getCookieStore();
			
			Log.d(tag,"cookie host=" + cookieStore1.getCookies().get(0).getDomain());
			Log.d(tag,"cookie hostports=" + cookieStore1.getCookies().get(0).getPorts());

			HttpContext context = new BasicHttpContext();

			Log.d(tag,"Login url2 = " + LOGIN_URL2);
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
		    Log.d("WEBCLIENT","return url:"+currentUrl);
		    
		    httpclient.getConnectionManager().closeExpiredConnections();
			
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
		
		Log.d(tag,"url=" + url);
		
		Log.d(tag,"session="+cookieStore.getCookies().get(0).getValue());
		
		Log.d(tag,"sessionhost="+cookieStore.getCookies().get(0).getDomain());

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
        
		HttpPost httpPost = new HttpPost(url);
		
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));
			
			HttpResponse response = httpclient.execute(httpPost);
	         
	         resultstr =EntityUtils.toString(response.getEntity());
	         
	         Log.d("WEBCLIENT.POSTDATA","return"+resultstr);
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
