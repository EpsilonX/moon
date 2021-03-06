package com.shunote;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shunote.AppCache.Cache;
import com.shunote.AppCache.DBHelper;
import com.shunote.Entity.Node;
import com.shunote.Entity.Note;
import com.shunote.Entity.Transform;
import com.shunote.Exception.CacheException;
import com.shunote.HTTP.WebClient;

/**
 * @author silar
 * 
 */

public class NodeListActivity extends Activity {

	private ListViewDrag nodelist;

	private Cache cache = null;

	private List<Node> sons = new ArrayList<Node>();
	private NodeAdapter nodeAdapter;
	private Button node_back;
	private ImageButton node_refresh;
	private TextView hTitle;
	private String FContent;
	private String Ftitle;

	private int id;
	private ProgressDialog mProgressDialog;
	private Context mContext;
	private Activity mA;
	private DBHelper dbHelper;
	private FloatImageText hContent;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.node_list);
		MyApplication.getInstance().addActivity(this);
		mContext = this;
		mA = this;
		dbHelper = new DBHelper(mContext);

		id = getIntent().getIntExtra("ID", 0);
		Log.d("ID_TAG", String.valueOf(id));

		node_back = (Button) findViewById(R.id.node_back);
		node_refresh = (ImageButton) findViewById(R.id.node_refresh);
		nodelist = (ListViewDrag) findViewById(R.id.nodelist);

		View head = LayoutInflater.from(this).inflate(R.layout.node_list_head,
				null);

		hTitle = (TextView) head.findViewById(R.id.node_list_head_title);

		nodelist.addHeaderView(head, null, true);

		nodeAdapter = new NodeAdapter();

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

		nodelist.setLayoutAnimation(controller);

		cache = Cache.getInstance();
		try {
			cache.init(this);
		} catch (CacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GetNoteTask task = new GetNoteTask();
		task.execute(id, 0);

		nodelist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (id != -1) {
					Node nextNode = (Node) parent.getAdapter()
							.getItem(position);
					if (nextNode == null) {
						Toast.makeText(NodeListActivity.this, "已经到尽头了~~",
								Toast.LENGTH_SHORT).show();

					} else {

						Intent next = new Intent();
						Bundle mBundle = new Bundle();
						mBundle.putSerializable("node", nextNode);
						next.putExtras(mBundle);
						next.setClass(NodeListActivity.this,
								NodeNextActivity.class);
						startActivity(next);

					}

				}

			}
		});

		node_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent back = new Intent();
				back.setClass(NodeListActivity.this, ShunoteActivity.class);
				back.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(back);
				overridePendingTransition(R.anim.left_in, R.anim.right_out);
			}
		});

		node_refresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				sons.clear();
				GetNoteTask task = new GetNoteTask();
				task.execute(id, 1);
			}
		});
	}

	class GetNoteTask extends android.os.AsyncTask<Integer, Integer, String> {

		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(0);
		}

		@Override
		protected String doInBackground(Integer... params) {
			Note note = null;
			try {
				if (WebClient.hasInternet(mA) == false && params[1] == 1) {
					Toast.makeText(mContext, "无法连接到网络，请检查网络配置",
							Toast.LENGTH_SHORT).show();
				} else if (params[1] == 0) {
					note = dbHelper.getNote(params[0]);

				} else {
					note = cache.getNote(params[0]);
				}
			} catch (CacheException e) {
				e.printStackTrace();
			}

			return note.getJson();

		}

		@Override
		protected void onPostExecute(String result) {

			dismissDialog(0);

			if (result.equals("")) {
				Toast.makeText(mContext, "没有数据", Toast.LENGTH_SHORT).show();
				return;
			}

			JSONObject ojson = null;

			Node root = null;

			try {
				ojson = new JSONObject(result);
				root = Transform.getInstance().json2Node(ojson);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// root.getId();
			Ftitle = root.getTitle();
			FContent = root.getContent();

			root.getSons();

			hTitle.setText(Ftitle);

			for (Node n : root.getSons()) {
				sons.add(n);

			}
			nodelist.setAdapter(nodeAdapter);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			mProgressDialog.setProgress(values[0]);
		}

	}

	public class NodeAdapter extends ArrayAdapter<Node> {

		public NodeAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			// TODO Auto-generated constructor stub
		}

		NodeAdapter() {
			super(NodeListActivity.this, R.layout.node_item, sons);
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			View row = convertView;

			if (row == null) {
				LayoutInflater inflater = getLayoutInflater();
				row = inflater.inflate(R.layout.node_item, parent, false);

			}
			TextView label = (TextView) row.findViewById(R.id.nodeitem);

			String out = sons.get(position).getTitle();
			label.setText(out);

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
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

	@Override
	public void onBackPressed() {
		Intent back = new Intent();
		back.setClass(NodeListActivity.this, ShunoteActivity.class);
		back.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(back);

		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

}
