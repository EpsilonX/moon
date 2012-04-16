package com.shunote.test;

import java.util.ArrayList;

import junit.framework.Test;

import com.shunote.DBActivity;
import com.shunote.Entity.Note;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class DBHelperTest extends ActivityInstrumentationTestCase2<DBActivity> {
	
	private DBActivity dba = null;
	private Test suite;
	public DBHelperTest() {
		super("com.shunote",DBActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		String tag = "setUp";
		Log.e(tag,"init");
		dba = getActivity();
	}
	
	public void testGetNote(){
		String tag = "testGetNote";
		Log.e(tag,"start test get note");
		ArrayList<Note> list = dba.getNote();
		Note note = list.get(0);
		assertEquals(2, note.getId());
	}
	
	public void testInsertNote(){
		String tag = "testInsertNote";
		Log.e(tag,"start test insert note:");
		Note note = new Note(2, "test", 1, "blabla");
		dba.insertNote(note);
	}
	
	public void testUpdateNote(){
		String tag = "testUpdateNote";
		Log.e(tag,"start test update note:");
		Note note = new Note(2, "fuck",1,"blabal");
		dba.updateNote(note);
		Note note2 = dba.getNote().get(0);
		assertEquals("fuck", note2.getName());
	}
	
	public void testDelNote(){
		dba.delNote(1);
		Note note = dba.getNote().get(0);
		assertEquals(2,note.getId());		
	}
	
}
