package com.shunote;

import com.shunote.AppCache.Cache;
import com.shunote.Entity.Node;
import com.shunote.Entity.Note;
import com.shunote.Exception.CacheException;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class OptActivity extends Activity {
	
	int userid;
	private Cache cache = null;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		
		cache = Cache.getInstance();
		try{
			cache.init(this);
		}catch(CacheException e){
			e.printStackTrace();
		}
	}
	
	public void addNote(String title) throws CacheException{
		cache.addNote(title);
	}
	
	public void delNote(int id) throws CacheException{
		Log.d("delNote","noteid= "+id);
		cache.delNote(id);
	}
	
	public Note getNote(int id) throws CacheException{
			Log.d("getNote","noteid= " + id);
		Note note = cache.getNote(id);
		return note;
	}
	
	public void updateNote(Note note) throws CacheException{
		Log.d("updateNote","noteid= " + note.getId());
		cache.updateNote(note);
	}
	
	public void addNode(Node node,int noteid) throws CacheException{
		cache.addNode(node, noteid);
	}
	
	public void delNode(Node node,int noteid) throws CacheException {
		cache.delNode(node, noteid);
	}
	
	public void updateNode(Node node,int noteid) throws CacheException{
		cache.updateNode(node, noteid);
	}
	
	public void changePosition(int target,Node node,int noteid) throws CacheException{
		cache.changePosition(target, node, noteid);
	}
}
