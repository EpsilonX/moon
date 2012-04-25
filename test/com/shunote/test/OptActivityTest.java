package com.shunote.test;
import org.junit.Before;
import org.junit.Test;

import com.shunote.OptActivity;
import com.shunote.Entity.Note;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

public class OptActivityTest extends ActivityInstrumentationTestCase2<OptActivity> {
	
	private OptActivity opt = null;
	public OptActivityTest() {
		super("com.shunote",OptActivity.class);
	}

	@Before
	protected void setUp() throws Exception {
		String tag = "setUp";
		Log.d(tag,"init");
		opt = getActivity();
	}
	
	@Test
	public void testAddNote(){
		String title;
			title = new String("试试看");
			opt.addNote(title);		
	}
	
//	@Test
//	public void testDelNote(){
//		opt.delNote(10000);
//	}
//	
//	@Test
//	public void testGetNote(){
//		Note note = opt.getNote(10000);
//		assertEquals(10000, note.getId());
//	}
//	
//	@Test
//	public void testUpdateNote(){
//		Note note = opt.getNote(321);
//		note.setName("老1的test第三版");
//		opt.updateNote(note);
//	}
}
