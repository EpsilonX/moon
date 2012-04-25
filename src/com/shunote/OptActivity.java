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
		
		cache = Cache.getInstance();
		try{
			cache.init(this);
		}catch(CacheException e){
			e.printStackTrace();
		}
	}
	
	public void addNote(String title){
		cache.addNote( title);
	}
	
	public void delNote(int id){
		Log.d("delNote","noteid= "+id);
		cache.delNote( id);
	}
	
	public Note getNote(int id){
		Note note = null;
		try{
			Log.d("getNote","noteid= " + id);
			note = cache.getNote( id);
		}catch(CacheException e){
			e.printStackTrace();
		}
		return note;
	}
	
	public void updateNote(Note note){
		Log.d("updateNote","noteid= " + note.getId());
		cache.updateNote( note);
	}
	
	public void addNode(Node node){
		
	}
}
