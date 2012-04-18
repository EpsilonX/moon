package com.shunote;

import org.apache.http.client.CookieStore;

import com.shunote.AppCache.Cache;
import com.shunote.Entity.Note;
import com.shunote.HTTP.MyCookieStore;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class OptActivity extends Activity {
	
	private CookieStore cookieStore = null;
	private SharedPreferences sp = null;
	String USERID,JSESSIONID, SESSIONID, USERNAME, PWD; // SP�и����ֶ�
	int userid;
	private Cache cache = null;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		sp = getSharedPreferences("data", MODE_WORLD_READABLE);
		
		// fetch data from SP
		USERID = sp.getString("userid", null);
		JSESSIONID = sp.getString("JSESSIONID", null);
		SESSIONID = sp.getString("sessionid", null);
		userid = Integer.parseInt(USERID);
		MyCookieStore myc = new MyCookieStore(JSESSIONID,SESSIONID);
		cookieStore = myc.getCookieStore();	
		
		cache = Cache.getInstance();
		
		cache.initDB(this);
	}
	
	public void addNote(String title){
		Log.v("addNote.userid", USERID);
		cache.addNote(userid, title, cookieStore);
	}
}
