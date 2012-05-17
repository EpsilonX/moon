package com.shunote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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

import com.shunote.AppCache.Cache;
import com.shunote.AppCache.Configuration;
import com.shunote.AppCache.DBHelper;
import com.shunote.Entity.Note;
import com.shunote.Exception.CacheException;
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
	private Context mContext;
	private Activity mA;
	private Cache cache;

	private boolean online = false;

	private DBHelper dbHelper = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_list);
		MyApplication.getInstance().addActivity(this);
		mContext = this;
		mA = this;
		// DisplayMetrics dm = getResources().getDisplayMetrics();
		// int i = dm.densityDpi;
		// Log.d("TAG_DPI", String.valueOf(i));

		Configuration config = new Configuration(this);
		PREFS_NAME = config.getValue("SPTAG");
		HOST = config.getValue("host");
		sp = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		dbHelper = new DBHelper(this);

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

		online = WebClient.hasInternet(this);

		offline_fetch();

		// check network
		if (online == false) {

			Toast.makeText(this, "无法连接到网络，请检查网络配置", Toast.LENGTH_SHORT).show();

		} else {

			online_fetch();

		}

		// 设置动画效果
		AnimationSet set = new AnimationSet(true);
		// 渐变透明度动画效果
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(150);
		set.addAnimation(animation);

		// 画面转换位置移动动画效果
		animation = new TranslateAnimation(-100, -1.0f,
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

				overridePendingTransition(R.anim.right_in, R.anim.left_out);

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

				// check network
				online = WebClient.hasInternet(mA);

				if (online == false) {

					Toast.makeText(mContext, "无法连接到网络，请检查网络配置",
							Toast.LENGTH_SHORT).show();

				} else {

					online_fetch();

				}

			}
		});

	}

	public void offline_fetch() {

		ArrayList<Note> list = dbHelper.getNoteList();

		if (list.size() == 0) {
			Toast.makeText(mContext, "没有数据，请先连接网络", Toast.LENGTH_SHORT).show();
			return;
		}
		for (Note n : list) {
			noteList.add(n);
		}

		listview.setAdapter(myAdapter);
	}

	public void online_fetch() {
		// if user does not login, start LoginActivity
		if (USERID == null) {
			Intent mIntent = new Intent(this, LoginActivity.class);
			startActivity(mIntent);
			finish();
		} else {
			Log.v(TAG, "USERID=" + USERID);
			Log.v(TAG, "JSESSIONID=" + JSESSIONID);
			Log.v(TAG, "SESSIONID=" + SESSIONID);

			noteList.clear();

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
						cache.init(mContext);
						note = cache.getNote(id);
						Note dbNote = dbHelper.getNote(id);
						if (dbNote == null) {
							dbHelper.insertNote(note);
						} else {
							if (dbNote.equals(note) == false) {
								dbHelper.updateNote(note);
							}
						}
						noteList.add(note);
					} catch (CacheException e) {
						e.printStackTrace();
					}

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
			SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
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

	/**
	 * 重写Backt键方法，弹出对话框，确定是否要关闭程序
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
		menu.add(0, 1, 1, "设置").setIcon(android.R.drawable.ic_menu_manage);
		menu.add(0, 2, 2, "退出").setIcon(
				android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		int item_id = item.getItemId();
		switch (item_id) {

		case 0:

			break;
		case 1:

			Intent prefer = new Intent();
			prefer.setClass(ShunoteActivity.this, PreferencesActivity.class);
			startActivity(prefer);

			break;
		case 2:

			MyApplication.getInstance().exit();

			break;
		}

		return true;

	}

}