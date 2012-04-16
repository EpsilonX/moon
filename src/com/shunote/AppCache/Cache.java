package com.shunote.AppCache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import com.shunote.Entity.Note;
import com.shunote.HTTP.WebClient;
import android.content.Context;

/**
 * Cache层操作方法类
 * @author Jeffrey
 *
 */
public class Cache{
	
	private static Cache instance = null;
	
	private DBHelper dbHelper = null;
	
	
	private Cache(){};
	
	/**
	 * Singleton模式
	 * @return instance
	 */
	public static Cache getInstance(){
		if (instance == null) {
			instance = new Cache();
			return instance;
		}else{
			return instance;
		}
	}
	
	/**
	 * 初始化数据库
	 */
	public void initDB(Context con){
		dbHelper = new DBHelper(con);
	}
	
	/**
	 * 添加笔记
	 * @param note
	 * @throws JSONException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void addNote(Note note, String url, CookieStore cookieStore) throws JSONException, ClientProtocolException, IOException{
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("title",note.getName()));
		pairs.add(new BasicNameValuePair("parentid","-1"));
		WebClient.getInstance().PostData(url, cookieStore, pairs);
		
		dbHelper.insertNote(note);
		
	}
	
	
	
	
	
}
