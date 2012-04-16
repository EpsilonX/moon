package com.shunote.HTTP;

import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;

/**
 * 本地CookieStore构造类
 * @author Jeffrey
 *
 */
public class MyCookieStore {
	
	private BasicCookieStore cookieStore = null;
	
	public MyCookieStore(String JSESSIONID,String SESSIONID){
		cookieStore = new BasicCookieStore();
		BasicClientCookie cookie1 = new BasicClientCookie("JSESSIONID",
				JSESSIONID);
		cookie1.setPath("/");
		cookie1.setDomain("shunote.com");
		cookie1.setVersion(0);
		BasicClientCookie cookie2 = new BasicClientCookie("sessionid",
				SESSIONID);
		cookie1.setPath("/");
		cookie1.setDomain("shunote.com");
		cookie1.setVersion(0);
		cookieStore.addCookie(cookie1);
		cookieStore.addCookie(cookie2);
	}
	
	/**
	 * 获得CookieStore
	 * @return
	 */
	public BasicCookieStore getCookieStore(){
		return cookieStore;
	}
	
}
