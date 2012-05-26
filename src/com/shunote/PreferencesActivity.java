package com.shunote;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;
import android.widget.Toast;

import com.shunote.AppCache.Cache;
import com.shunote.AppCache.Configuration;
import com.shunote.AppCache.DBHelper;
import com.shunote.Entity.Note;
import com.shunote.Exception.CacheException;
import com.shunote.HTTP.MyCookieStore;
import com.shunote.HTTP.WebClient;

public class PreferencesActivity extends PreferenceActivity implements
		Preference.OnPreferenceClickListener,
		Preference.OnPreferenceChangeListener {

	String PREFS_NAME = ""; // SharedPrefences'sPREF_NAME
	SharedPreferences sp;
	String USERID, JSESSIONID, SESSIONID, USERNAME, PWD, HOST; // SP's Tag

	private Cache cache;

	private boolean online = false;

	private DBHelper dbHelper = null;

	private PreferenceCategory account;
	private Preference account_change, refresh_now;
	private CheckBoxPreference refresh_auto, image_auto;
	private ProgressDialog mProgressDialog;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		MyApplication.getInstance().addActivity(this);

		Configuration config = new Configuration(this);
		PREFS_NAME = config.getValue("SPTAG");
		HOST = config.getValue("host");
		sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		dbHelper = new DBHelper(this);

		// get info from sp
		USERID = sp.getString("userid", null);
		JSESSIONID = sp.getString("JSESSIONID", null);
		SESSIONID = sp.getString("sessionid", null);

		account = (PreferenceCategory) findPreference("account");
		account_change = (Preference) findPreference("account_change");
		refresh_now = (Preference) findPreference("refresh_now");
		refresh_auto = (CheckBoxPreference) findPreference("refresh_auto");
		image_auto = (CheckBoxPreference) findPreference("image_auto");

		name = sp.getString("USERNAME", "");
		account.setTitle("当前帐号:" + name);

		account_change.setOnPreferenceClickListener(this);
		refresh_now.setOnPreferenceClickListener(this);
		refresh_auto.setOnPreferenceChangeListener(this);
		image_auto.setOnPreferenceChangeListener(this);

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {

		if (preference.equals(refresh_auto)) {

		}
		if (preference.equals(image_auto)) {

		}

		return true;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {

		if (preference.equals(account_change)) {

			new AlertDialog.Builder(PreferencesActivity.this)
					.setMessage("您确认注销改帐号?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method

									Cache cache = Cache.getInstance();

									try {
										cache.init(getApplicationContext());
										cache.clear();
									} catch (CacheException e) {
										e.printStackTrace();
									}

									Intent login = new Intent();
									login.setClass(PreferencesActivity.this,
											LoginActivity.class);
									startActivity(login);
									finish();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).show();

		}

		if (preference.equals(refresh_now)) {

			// check network
			online = WebClient.hasInternet(PreferencesActivity.this);

			if (online == false) {

				Toast.makeText(PreferencesActivity.this, "无法连接到网络，请检查网络配置",
						Toast.LENGTH_SHORT).show();

			} else {

				GetDataTask task = new GetDataTask();
				String url = "/users/" + USERID + "/usernodes";
				task.execute(url);

			}

		}

		return true;
	}

	/**
	 * get Data
	 * 
	 * @author Silar&Jeffrey
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
					Note note;
					try {
						cache = Cache.getInstance();
						cache.init(PreferencesActivity.this);
						note = cache.getNote(id);
						Note dbNote = dbHelper.getNote(id);
						if (dbNote == null) {
							dbHelper.insertNote(note);
						} else {
							if (dbNote.equals(note) == false) {
								dbHelper.updateNote(note);
							}
						}
					} catch (CacheException e) {
						e.printStackTrace();
					}

					Toast.makeText(PreferencesActivity.this, "数据更新完毕",
							Toast.LENGTH_SHORT).show();
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
