package com.shunote.AppCache;

import java.util.ArrayList;

import com.shunote.Entity.Note;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	
	public static final String dbname = "shunote.db";
	public static final int version = 1;
	private String tag = "DBHelper";

	public DBHelper(Context context) {
		super(context, dbname, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS Note("
						+"id INTEGER PRIMARY KEY,"
						+"name varchar(20),"
						+"root INTEGER,"
						+"json TEXT)");		
		db.execSQL("CREATE TABLE IF NOT EXISTS Image("
						+"id INTEGER PRIMARY KEY AUTOINCREMENT,"
						+"url TEXT,"
						+"data TEXT)");
		Log.d(tag,"DB Created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS Note");
		db.execSQL("DROP TABLE IF EXISTS Image");
		onCreate(db);	
		Log.d(tag,"DB Upgraded");
	}
	
	public void clear(SQLiteDatabase db){
		db.execSQL("DROP TABLE IF EXISTS Note");
		db.execSQL("DROP TABLE IF EXISTS Image");
		Log.d(tag,"DB Cleared");
	}
	
	public Note getNote(int id){
		Note note = null;
		SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = {"id","name","root","json"};
		String[] params = {Integer.toString(id)};
		Cursor result = db.query("Note", columns, "id=?", params, null, null, null);
		if(result.moveToFirst()){
			note = new Note(result.getInt(0), result.getString(1),result.getInt(2), result.getString(3));
			Log.d(tag,"get Note title = " + note.getName());
		}else{
			Log.e(tag,"failed to get Note id=" + id);
		}
		return note;		
	}
	
	public void insertNote(Note note){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("id", note.getId());
		cv.put("name", note.getName());
		cv.put("root", note.getRoot());
		cv.put("json", note.getJson());
		if(db.insert("Note", "name", cv)!=-1){
			Log.d(tag,"A new Note inserted! id= " + note.getId());
		}else{
			Log.d(tag,"failed! id= " + note.getId() + "exists!");
		}
		db.close();
		
	}

	public void delNote(int id){
		SQLiteDatabase db = this.getWritableDatabase();
		if(db.delete("Note", "id=?", new String[]{String.valueOf(id)})!=0){
			Log.d(tag,"A Note deleted! id = " + id);
		}else{
			Log.d(tag,"fail to delete Note id = " + id);
		}
		
		db.close();
		
	}
	
	public ArrayList<Note> getNoteList(){
		SQLiteDatabase db = this.getReadableDatabase();
		int total=0;
		Cursor result = db.rawQuery("SELECT * FROM Note", null);
		result.moveToFirst();
		ArrayList<Note> noteList = new ArrayList<Note>();
		while(!result.isAfterLast()){
			int id = result.getInt(0);
			String name = result.getString(1);
			int root = result.getInt(2);
			String json = result.getString(3);
			Note note = new Note(id,name,root,json);
			noteList.add(note);
			result.moveToNext();
			total++;
		}
		Log.d(tag,total+" notes fetched!");
		result.close();
		return noteList;
	}
	
	public void updateNote(Note note){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("id", note.getId());
		cv.put("name", note.getName());
		cv.put("root", note.getRoot());
		cv.put("json", note.getJson());
		if(db.update("Note", cv,"id=?", new String[]{String.valueOf(note.getId())})!=0){
			Log.d(tag,"update note! id = "+ note.getId());
		}else{
			Log.d(tag,"update failed! noteid = "+ note.getId());
		}
		db.close();
		
	}
	
	public void insertIMG(String url,String data){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("url", url);
		cv.put("data", data);
		db.insert("Image", "url", cv);
		db.close();
		Log.d(tag,"insert IMG");
	}
	
	public String getIMG(String url){
		SQLiteDatabase db = this.getReadableDatabase();
		String[] columns = {"data"};
		String[] params = {url};
		Cursor result = db.query("Image", columns, "url=?", params, null, null, null);
		result.moveToFirst();
		String data = result.getString(0);
		result.close();
		return data;
	}
	
	public void delIMG(String url){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete("Image", "url=?",new String[]{url});
		db.close();
	}
}
