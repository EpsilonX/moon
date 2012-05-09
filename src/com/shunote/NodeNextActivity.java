package com.shunote;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
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
import com.shunote.Entity.Node;
import com.shunote.Exception.CacheException;

public class NodeNextActivity extends Activity {

	private ListViewDrag nodelist;
	private Cache cache = null;
	private NodeAdapter nodeAdapter;
	private Button node_back;
	private ImageButton node_refresh;

	private List<Node> sons = new ArrayList<Node>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.node_list);

		Node node = (Node) getIntent().getSerializableExtra("node");

		Log.d("NodeNext", node.getTitle());

		node_back = (Button) findViewById(R.id.node_back);
		node_refresh = (ImageButton) findViewById(R.id.node_refresh);
		nodelist = (ListViewDrag) findViewById(R.id.nodelist);

		View head = LayoutInflater.from(this).inflate(R.layout.nodehead, null);

		TextView hTitle = (TextView) head.findViewById(R.id.head_title);

		String Ftitle = node.getTitle();
		final String FContent = node.getContent();
		hTitle.setText(Ftitle);

		for (Node n : node.getSons()) {
			sons.add(n);
		}

		nodelist.addHeaderView(head, null, true);

		nodeAdapter = new NodeAdapter();
		nodelist.setAdapter(nodeAdapter);

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
			e.printStackTrace();
		}

		nodelist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				if (id == -1) {
					View relat1 = (View) view
							.findViewById(R.id.nodelist_relat1);
					View relat2 = (View) view
							.findViewById(R.id.nodelist_relat2);

					if (relat1.getVisibility() == View.INVISIBLE
							|| relat2.getVisibility() == View.GONE) {
						relat1.setVisibility(View.VISIBLE);
						relat2.setVisibility(View.VISIBLE);

						TextView hContent = (TextView) relat2
								.findViewById(R.id.head_content);
						hContent.setVisibility(View.VISIBLE);
						hContent.setText(FContent);

						Button b1 = (Button) relat1
								.findViewById(R.id.nodelist_b1);
						b1.setVisibility(View.VISIBLE);

						b1.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub

								Toast.makeText(NodeNextActivity.this, "测试",
										Toast.LENGTH_SHORT).show();
							}
						});

					} else {
						relat1.setVisibility(View.INVISIBLE);
						relat2.setVisibility(View.GONE);
					}
				} else {

					Node nextNode = (Node) parent.getAdapter()
							.getItem(position);
					if (nextNode == null) {
						Toast.makeText(NodeNextActivity.this, "已经到尽头了~~",
								Toast.LENGTH_SHORT).show();

					} else {

						Intent next = new Intent();
						Bundle mBundle = new Bundle();
						mBundle.putSerializable("node", nextNode);
						next.putExtras(mBundle);
						next.setClass(NodeNextActivity.this,
								NodeNextActivity.class);
						startActivity(next);
					}

				}

			}

		});

		node_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		node_refresh.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

	}

	public class NodeAdapter extends ArrayAdapter<Node> {

		public NodeAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			// TODO Auto-generated constructor stub
		}

		NodeAdapter() {
			super(NodeNextActivity.this, R.layout.node_item, sons);
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
}
