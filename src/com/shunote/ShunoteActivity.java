package com.shunote;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;


import com.shunote.HTTP.WebClient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ShunoteActivity extends Activity {
	/** Called when the activity is first created. */
	String PREFS_NAME = "data"; //SharedPrefences的PREF_NAME
	SharedPreferences sp;
	String USERID, JSESSIONID, SESSIONID,USERNAME,PWD; //SP中各个字段
	String TAG = "JEFFREY_TAG";
	Button button;
	EditText username, pwd;
	TextView tv;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		sp = getSharedPreferences(PREFS_NAME, 0);

		button = (Button) findViewById(R.id.login);
		tv = (TextView) findViewById(R.id.out);
		username = (EditText) findViewById(R.id.username);
		pwd =  (EditText) findViewById(R.id.password);

		//调入SP中用户信息
		USERID = sp.getString("userid", null);
		JSESSIONID = sp.getString("JSESSIONID", null);
		SESSIONID = sp.getString("sessionid", null);
		
		//如果SP中不存在用户信息则需登录
		if (USERID == null) {
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
		} else {
			button.setClickable(false);
			Log.v(TAG, "USERID=" + USERID);
			Log.v(TAG, "JSESSIONID=" + JSESSIONID);
			Log.v(TAG, "SESSIONID=" + SESSIONID);
			
			tv.append("用户已登录!\n");
			GetDataTask getData = new GetDataTask();
			String url = "/users/"+USERID;
			getData.execute(url);
		}

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

			result =  localCookieStore!=null?"登录成功!\n用户ID为:"+USERID:"登录失败!";
			
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			tv.append(result);
		}

	}

	/**
	 * 获取数据异步进程
	 * @author Jeffrey
	 * 
	 */
	class GetDataTask extends android.os.AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			String result = "用户信息：";

			// 填入Cookie信息
			CookieStore cookieStore = new BasicCookieStore();
			BasicClientCookie cookie1 = new BasicClientCookie("JSESSIONID",JSESSIONID);
			cookie1.setPath("/");
			cookie1.setDomain("shunote.com");
			cookie1.setVersion(0);
			BasicClientCookie cookie2 = new BasicClientCookie("sessionid",SESSIONID);
			cookie1.setPath("/");
			cookie1.setDomain("shunote.com");
			cookie1.setVersion(0);
			cookieStore.addCookie(cookie1);
			cookieStore.addCookie(cookie2);

			//调用HTTP包中webclient的getdata方法获取数据
			result = WebClient.getInstance().GetData(params[0], cookieStore);
			
			return result;
			
		}

		protected void onPostExecute(String result) {
//			try {
//				JSONObject object = new JSONObject(result);
//				JSONObject jnode = object.getJSONObject("nodes");
//				tv.setText(jnode.toString());
//				Node root = Transform.getInstance().json2Node(jnode);
//				JSONObject jroot = Transform.getInstance().node2Json(root);
//				tv.setText(jroot.toString());
//
//				Node root2 = Transform.getInstance().json2Node(jroot);
//				JSONObject jroot2 = Transform.getInstance().node2Json(root2);
//				tv.setText(jroot2.toString());
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			tv.append(result);

		}

	}
}