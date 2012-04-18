package com.shunote;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.shunote.Entity.Note;
import com.shunote.HTTP.MyCookieStore;
import com.shunote.HTTP.WebClient;

public class ShunoteActivity extends Activity {
	/** Called when the activity is first created. */
	String PREFS_NAME = "data"; // SharedPrefences'sPREF_NAME
	SharedPreferences sp;
	String USERID, JSESSIONID, SESSIONID, USERNAME, PWD; // SP's Tag
	String TAG = "JEFFREY_TAG";

	ArrayList<Note> noteList = new ArrayList<Note>();
	TextView tv;
	ListView listview;
	MyAdapter myAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notelist);

		sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

		tv = (TextView) findViewById(R.id.out);
		listview = (ListView) findViewById(R.id.notelist_list);

		myAdapter = new MyAdapter();

		// get info from sp
		USERID = sp.getString("userid", null);
		JSESSIONID = sp.getString("JSESSIONID", null);
		SESSIONID = sp.getString("sessionid", null);

		// if user does not login, start LoginActivity
		if (USERID == null) {
			Intent mIntent = new Intent(this, LoginActivity.class);
			startActivity(mIntent);
			finish();
		} else {
			Log.v(TAG, "USERID=" + USERID);
			Log.v(TAG, "JSESSIONID=" + JSESSIONID);
			Log.v(TAG, "SESSIONID=" + SESSIONID);

			GetDataTask getData = new GetDataTask();
			String url = "/users/" + USERID + "/usernodes";
			getData.execute(url);
		}

	}

	/**
	 * get Data
	 * 
	 * @author Jeffrey
	 * 
	 */
	class GetDataTask extends android.os.AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			String result = "";

			// get Cookie
			MyCookieStore myc = new MyCookieStore(JSESSIONID, SESSIONID);

			// use WebClient's get data method
			result = WebClient.getInstance().GetData(params[0], myc.getCookieStore());

			return result;

		}

		protected void onPostExecute(String result) {
			try {
				JSONArray objects = new JSONArray(result);

				for (int i = 0; i < objects.length(); i++) {
					int id = objects.getJSONObject(i).getInt("id");
					String name = objects.getJSONObject(i).getString("title");
					int root = objects.getJSONObject(i).getInt("root");
					Note note = new Note(id, name, root, null);
					noteList.add(note);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			listview.setAdapter(myAdapter);
		}

	}

	public class MyAdapter extends ArrayAdapter<Note> {

		MyAdapter() {
			super(ShunoteActivity.this, R.layout.noteitem, noteList);

		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.noteitem, parent, false);

			}
			TextView label = (TextView) row.findViewById(R.id.noteitem);

			// View liner = (View) row.findViewById(R.id.nodelist_relat1);
			// Button b1 = (Button) liner.findViewById(R.id.nodelist_b1);
			// b1.setVisibility(View.GONE);
			label.setText(noteList.get(position).getName());
			return row;

		}

	}

}