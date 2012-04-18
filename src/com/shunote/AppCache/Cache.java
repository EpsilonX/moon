package com.shunote.AppCache;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.message.BasicNameValuePair;
import com.shunote.Entity.Node;
import com.shunote.Entity.Note;
import com.shunote.HTTP.WebClient;
import android.content.Context;

/**
 * Cache - opt data with Server & DB
 * @author Jeffrey
 *
 */
public class Cache{
	
	private static Cache instance = null;
	
	private DBHelper dbHelper = null;
	
	
	private Cache(){};
	
	/**
	 * Singleton
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
	 * init DB
	 */
	public void initDB(Context con){
		dbHelper = new DBHelper(con);
	}
	
	/**
	 * add Note
	 * @param note
	 * @param url
	 * @param cookieStore
	 */
	public void addNote(int userid,String title, CookieStore cookieStore){
		String url = "/users/" + userid + "/nodes";
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("title",title));
		pairs.add(new BasicNameValuePair("parentid","-1"));
		pairs.add(new BasicNameValuePair("pub", "0"));
		WebClient.getInstance().PostData(url, cookieStore, pairs);
		
		//dbHelper.insertNote(note);
		
	}
	
	/**
	 * update Note
	 * @param note
	 * @param url
	 * @param cookieStore
	 */
	public void updateNote(int userid,Note note, CookieStore cookieStore) {
		String url = "/users/" + userid + "/usernodes/" + note.getId();
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("title", note.getName()));
		pairs.add(new BasicNameValuePair("parentid","-1"));
		pairs.add(new BasicNameValuePair("pub", "0"));
		WebClient.getInstance().PostData(url, cookieStore, pairs);
		
		dbHelper.updateNote(note);
	}
	
	/**
	 * del Note
	 * @param userid
	 * @param noteid
	 * @param cookieStore
	 */
	public void delNote(int userid, int noteid, CookieStore cookieStore){
		String url = "/users/"+userid+"/usernodes/" + noteid;
		
		WebClient.getInstance().DelData(url, cookieStore);
		
		dbHelper.delNote(noteid);
	}
	
	/**
	 * add Node
	 * @param userid
	 * @param node
	 * @param cookieStore
	 */
	public void addNode(int userid,Node node,CookieStore cookieStore){
		String url = "/users/" + userid + "/nodes";
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("title", node.getTitle()));
		pairs.add(new BasicNameValuePair("parentid", Integer.toString(node.getFather().getId())));
		pairs.add(new BasicNameValuePair("content", node.getContent()));
		
		WebClient.getInstance().PostData(url, cookieStore, pairs);
	}
	
	/**
	 * update Node
	 * @param userid
	 * @param node
	 * @param cookieStore
	 */
	public void updateNode(int userid,Node node,CookieStore cookieStore){
		String url = "/users/" + userid + "/nodes/" + node.getId();
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("title", node.getTitle()));
		pairs.add(new BasicNameValuePair("content", node.getContent()));
		
		WebClient.getInstance().PostData(url, cookieStore, pairs);
	}
	
	/**
	 * del Node
	 * @param userid
	 * @param nodeid
	 * @param cookieStore
	 */
	public void delNode(int userid,int nodeid,CookieStore cookieStore){
		String url = "/users/" + userid + "/nodes/" + nodeid;
		
		WebClient.getInstance().DelData(url, cookieStore);
	}
	
}
