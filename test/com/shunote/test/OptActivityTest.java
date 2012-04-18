package com.shunote.test;
import org.junit.Before;
import org.junit.Test;
import com.shunote.OptActivity;
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
		Log.e(tag,"init");
		opt = getActivity();
	}
	
	@Test
	public void testAddNote(){
		String title;
			title = new String("我就不信了");
			opt.addNote(title);
		
	}
}
