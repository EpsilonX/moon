package com.shunote;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;

import com.shunote.AppCache.Configuration;
import com.shunote.HTTP.WebClient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	String PREFS_NAME = ""; // SharedPrefences's PREF_NAME
	SharedPreferences sp;
	String USERID, JSESSIONID, SESSIONID, USERNAME, PWD; // SP_TAG
	Button button;
	EditText username, pwd;
	Boolean success = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);

		setContentView(R.layout.login);
		button = (Button) findViewById(R.id.login);
		username = (EditText) findViewById(R.id.username);
		pwd = (EditText) findViewById(R.id.password);

		Configuration config = new Configuration(this);
		PREFS_NAME = config.getValue("SPTAG");
		sp = getSharedPreferences(PREFS_NAME, MODE_WORLD_READABLE);

		button.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>();
				Editor spEditor = sp.edit();
				spEditor.putString("USERNAME", username.getText().toString());
				spEditor.putString("PWD", pwd.getText().toString());
				spEditor.commit();
				pairs.add(new BasicNameValuePair("j_username", username
						.getText().toString()));
				pairs.add(new BasicNameValuePair("j_password", pwd.getText()
						.toString()));
				LoginTask login = new LoginTask();
				login.execute(pairs);

				button.setClickable(false);
				Toast.makeText(LoginActivity.this, "正在登录，请稍等...",
						Toast.LENGTH_SHORT).show();
			}

		});

	}

	/**
	 * Login AsyncTask
	 * 
	 * @author Jeffrey
	 * 
	 */
	class LoginTask extends
			android.os.AsyncTask<List<NameValuePair>, Integer, String> {

		@Override
		protected String doInBackground(List<NameValuePair>... params) {

			String result = "";

			// acquire cookie from WebClient's login method
			WebClient.getInstance().init(getApplicationContext());

			Log.d("Login", params[0].get(0).getValue());
			CookieStore localCookieStore = WebClient.getInstance().Login(
					params[0]);

			// put cookie into sp
			Editor spEditor = sp.edit();
			List<Cookie> cookies = localCookieStore.getCookies();
			for (Cookie c : cookies) {
				spEditor.putString(c.getName(), c.getValue());
			}
			spEditor.commit();

			USERID = sp.getString("userid", null);
			JSESSIONID = sp.getString("JSESSIONID", null);
			SESSIONID = sp.getString("sessionid", null);

			result = USERID != null ? "Login Success:" + USERID
					: "Login Failed";

			if (USERID != null)
				success = true;
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT)
					.show();

			if (success == true) {
				Intent mIntent = new Intent();
				mIntent.setClass(LoginActivity.this, ShunoteActivity.class);
				startActivity(mIntent);
				finish();
			}
		}

	}

}
