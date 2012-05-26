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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
	private boolean online = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);

		setContentView(R.layout.login);
		button = (Button) findViewById(R.id.login);
		username = (EditText) findViewById(R.id.username);
		pwd = (EditText) findViewById(R.id.password);

		Configuration config = new Configuration(this);
		PREFS_NAME = config.getValue("SPTAG");
		sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

		online = WebClient.hasInternet(this);

		if (online == true) {

			button.setClickable(true);
		}

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
			WebClient.refresh();
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

			if (success == true) {
				Intent mIntent = new Intent();
				mIntent.setClass(LoginActivity.this, ShunoteActivity.class);
				startActivity(mIntent);
				finish();
			} else {
				Toast.makeText(LoginActivity.this, "登录失败,请重新登录",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	/**
	 * 重写Back键方法，弹出对话框，确定是否要关闭程序
	 */

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("温馨提示")
				.setMessage("您是否要退出书'笔记？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stubgo

						MyApplication.getInstance().exit();

					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).show();

	}

	/**
	 * 添加菜单按钮
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/* menu.add(组ID，项ID，显示顺序，显示标题) */
		menu.add(0, 0, 0, "关于")
				.setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(0, 1, 1, "退出").setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int item_id = item.getItemId();
		switch (item_id) {

		case 0:

			dialog();
			break;
		case 1:

			MyApplication.getInstance().exit();

			break;
		}

		return true;

	}

	// 定义对话框
	protected void dialog() {

		LayoutInflater inflater = LayoutInflater.from(this);
		View layout = inflater.inflate(R.layout.about,
				(ViewGroup) findViewById(R.id.av));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		AlertDialog alertDialog = builder.create();
		alertDialog.setButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				dialog.dismiss();

			}
		});
		alertDialog.show();
	}
}
