package com.shunote;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

import com.shunote.AppCache.Configuration;
import com.shunote.Entity.Note;
import com.shunote.HTTP.MyCookieStore;
import com.shunote.HTTP.WebClient;

public class ShunoteActivity extends Activity {
	/** Called when the activity is first created. */
	String PREFS_NAME = ""; // SharedPrefences'sPREF_NAME
	SharedPreferences sp;
	String USERID, JSESSIONID, SESSIONID, USERNAME, PWD,HOST; // SP's Tag
	String TAG = "JEFFREY_TAG";

	ArrayList<Note> noteList = new ArrayList<Note>();
	TextView tv;
	ListView listview;
	MyAdapter myAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notelist);

		Configuration config = new Configuration(this);
		PREFS_NAME = config.getValue("SPTAG");
		HOST = config.getValue("host");
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
			MyCookieStore myc = new MyCookieStore(JSESSIONID, SESSIONID,HOST);

			WebClient.getInstance().init(getApplicationContext());
			// use WebClient's get data method
			result = WebClient.getInstance().GetData(params[0], myc.getCookieStore());

			Log.i("ShunoteActivity.GetDataTask","result:"+result);
			return result;

		}

		protected void onPostExecute(String result) {
			try {
				JSONObject obj = new JSONObject(result);
				JSONArray objects = obj.getJSONArray("data");
				
				//JSONArray objects = new JSONArray(result);

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
			String out = noteList.get(position).getName()+" id:"+noteList.get(position).getId();
			label.setText(out);
			return row;

		}

	}

}