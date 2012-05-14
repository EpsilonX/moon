package com.shunote;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shunote.AppCache.Configuration;
import com.shunote.Entity.Note;
import com.shunote.HTTP.MyCookieStore;
import com.shunote.HTTP.WebClient;

/**
 * show all note
 * 
 * @author silar
 */

public class ShunoteActivity extends Activity {
	/** Called when the activity is first created. */

	String PREFS_NAME = ""; // SharedPrefences'sPREF_NAME
	SharedPreferences sp;
	String USERID, JSESSIONID, SESSIONID, USERNAME, PWD, HOST; // SP's Tag
	String TAG = "JEFFREY_TAG";

	ArrayList<Note> noteList = new ArrayList<Note>();
	TextView tv;
	ListView listview;
	View relat, liner;
	MyAdapter myAdapter;
	ProgressDialog mProgressDialog;
	ImageButton note_new, note_refresh;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_list);

		// DisplayMetrics dm = getResources().getDisplayMetrics();
		// int i = dm.densityDpi;
		// Log.d("TAG_DPI", String.valueOf(i));

		Configuration config = new Configuration(this);
		PREFS_NAME = config.getValue("SPTAG");
		HOST = config.getValue("host");
		sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		//check network
		if(WebClient.hasInternet(this)==false){
			Toast.makeText(getApplicationContext(), "当前网络不可用!", Toast.LENGTH_LONG);
		}

		tv = (TextView) findViewById(R.id.out);
		listview = (ListView) findViewById(R.id.notelist_list);
		relat = (View) findViewById(R.id.note_relat);
		liner = (View) findViewById(R.id.note_liner);
		note_new = (ImageButton) findViewById(R.id.note_new);
		note_refresh = (ImageButton) findViewById(R.id.note_refresh);

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

		// 设置动画效果
		AnimationSet set = new AnimationSet(true);
		// 渐变透明度动画效果
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(150);
		set.addAnimation(animation);

		// 画面转换位置移动动画效果
		animation = new TranslateAnimation(-100, -50.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, 50, -1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(200);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		controller.setInterpolator(new AccelerateDecelerateInterpolator());
		listview.setLayoutAnimation(controller);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				Note note = noteList.get(arg2);

				Intent node = new Intent();
				node.putExtra("ID", note.getId());
				node.setClass(ShunoteActivity.this, NodeListActivity.class);
				startActivity(node);
			}
		});

		note_new.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		note_refresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				noteList.clear();

				GetDataTask getData = new GetDataTask();
				String url = "/users/" + USERID + "/usernodes";
				getData.execute(url);

			}
		});

	}

	/**
	 * get Data
	 * 
	 * @author Jeffrey
	 * 
	 */
	class GetDataTask extends android.os.AsyncTask<String, Integer, String> {

		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(0);
		}

		@Override
		protected String doInBackground(String... params) {

			String result = "";

			publishProgress(0);

			// get Cookie
			MyCookieStore myc = new MyCookieStore(JSESSIONID, SESSIONID, HOST);

			publishProgress(30);
			WebClient.getInstance().init(getApplicationContext());
			// use WebClient's get data method
			result = WebClient.getInstance().GetData(params[0],
					myc.getCookieStore());

			publishProgress(100);
			Log.i("ShunoteActivity.GetDataTask", "result:" + result);

			// wrong result, login again
			if (!result.startsWith("{")) {

				Log.i("ShunoteActivity.GetDataTask",
						"get data failed,login again!");
				USERNAME = sp.getString("USERNAME", "");
				PWD = sp.getString("PWD", "");

				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				pairs.add(new BasicNameValuePair("j_username", USERNAME));
				pairs.add(new BasicNameValuePair("j_password", PWD));

				CookieStore localCookieStore = WebClient.getInstance().Login(
						pairs);

				// put cookie into sp
				Editor spEditor = sp.edit();
				List<Cookie> cookies = localCookieStore.getCookies();
				for (Cookie c : cookies) {
					spEditor.putString(c.getName(), c.getValue());
				}
				spEditor.commit();

				result = WebClient.getInstance().GetData(params[0],
						localCookieStore);

				Log.i("ShunoteActivity.GetDataTask", "second result:" + result);
			}

			return result;

		}

		@Override
		protected void onProgressUpdate(Integer... values) {

			mProgressDialog.setProgress(values[0]);
		}

		protected void onPostExecute(String result) {

			dismissDialog(0);

			try {

				JSONObject obj = new JSONObject(result);
				JSONArray objects = obj.getJSONArray("data");

				for (int i = 0; i < objects.length(); i++) {
					int id = objects.getJSONObject(i).getInt("id");
					String name = StringEscapeUtils.unescapeHtml(objects
							.getJSONObject(i).getString("title"));
					int root = objects.getJSONObject(i).getInt("root");
					String date = objects.getJSONObject(i).getString(
							"createdate");
					int nodenum =objects.getJSONObject(i).getInt("nodenum");
					// Date d = new Date(date);
					// DateFormat df=DateFormat.getDateInstance();
					// Log.i("time", df.format(d) + "in" + name);
					Note note = new Note(id, name, root, null, date,nodenum);
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
			super(ShunoteActivity.this, R.layout.note_item, noteList);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.note_item, parent, false);

			}
			TextView label = (TextView) row.findViewById(R.id.noteitem);
			TextView time = (TextView) row.findViewById(R.id.note_time);
			TextView count = (TextView) row.findViewById(R.id.note_count);

			String out = noteList.get(position).getName();
			String date = noteList.get(position).getDate();
			Date d = new Date(date);
			DateFormat df = DateFormat.getDateInstance();
			int nodenum = noteList.get(position).getNodenum();
			label.setText(out);
			time.setText(df.format(d));
			count.setText(String.valueOf(nodenum));

			// 奇数设置背景1,偶数设置背景2
			if (position % 2 == 0) {
				row.setBackgroundResource(R.drawable.item_double_select);
			} else {
				row.setBackgroundResource(R.drawable.item_single_select);
			}

			return row;

		}

	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0: // we set this to 0
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("正在加载数据...");
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.setMax(500);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

}