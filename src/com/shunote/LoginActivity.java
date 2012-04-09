package com.shunote;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;

import com.shunote.HTTP.WebClient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	String PREFS_NAME = "data"; //SharedPrefences的PREF_NAME
	SharedPreferences sp;
	String USERID, JSESSIONID, SESSIONID,USERNAME,PWD; //SP中各个字段
	Button button;
	EditText username, pwd;
	Boolean success=false;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login);
		button = (Button) findViewById(R.id.login);
		username = (EditText) findViewById(R.id.username);
		pwd =  (EditText) findViewById(R.id.password);
		
		sp = getSharedPreferences(PREFS_NAME, MODE_WORLD_READABLE);
		
		button.setOnClickListener(new View.OnClickListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
		
			LoginTask login = new LoginTask();
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			sp.edit().putString("USERNAME", username.getText().toString());
			sp.edit().putString("PWD", pwd.getText().toString());
			sp.edit().commit();
			pairs.add(new BasicNameValuePair("j_username",
					username.getText().toString()));
			pairs.add(new BasicNameValuePair("j_password", pwd.getText().toString()));
			login.execute(pairs);	
		}
			
		});
		
	}
	
	/**
	 * 用户登录异步进程
	 * @author Jeffrey
	 *
	 */
	class LoginTask extends
			android.os.AsyncTask<List<NameValuePair>, Integer, String> {

		@Override
		protected String doInBackground(List<NameValuePair>... params) {
			
			String result = "";		
			
			//调用HTTP包中webclient类的登录方法，返回cookie
			CookieStore localCookieStore = WebClient.getInstance().Login(params[0]);
			
			//将cookie存入SP
			Editor spEditor = sp.edit();
			List<Cookie> cookies = localCookieStore.getCookies();
			for (Cookie c : cookies) {
				spEditor.putString(c.getName(), c.getValue());
			}
			spEditor.commit();			

			USERID = sp.getString("userid", null);
			JSESSIONID = sp.getString("JSESSIONID", null);
			SESSIONID = sp.getString("sessionid", null);

			result = USERID!=null?"登录成功!\n用户ID为:"+USERID:"登录失败!";
			
			if (USERID!=null) success=true;
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
			
			if(success==true){
				Intent mIntent = new Intent();
				mIntent.setClass(LoginActivity.this, ShunoteActivity.class);
				startActivity(mIntent);
				finish();
			}
		}

	}

}
