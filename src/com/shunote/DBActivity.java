package com.shunote;

import java.util.ArrayList;

import com.shunote.AppCache.DBHelper;
import com.shunote.Entity.Image;
import com.shunote.Entity.Note;
import android.app.Activity;
import android.os.Bundle;

public class DBActivity extends Activity {
	private  DBHelper dbhelper ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbhelper = new DBHelper(this);	
	}
	
	public void insertNote(Note note){
		dbhelper.insertNote(note);
	}
	
	public ArrayList<Note> getNote(){
		return dbhelper.getNoteList();
	}
	
	public void updateNote(Note note){
		dbhelper.updateNote(note);
	}
	
	public void delNote(int id){
		dbhelper.delNote(id);
	}
	
	public void insertIMG(Image image){
		dbhelper.insertIMG(image.getUrl(), image.getData());
	}

}
