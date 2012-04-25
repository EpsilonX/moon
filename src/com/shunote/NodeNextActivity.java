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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shunote.AppCache.Cache;
import com.shunote.Entity.Node;
import com.shunote.Exception.CacheException;

public class NodeNextActivity extends Activity {

	private ListViewDrag nodelist;
	private Cache cache = null;
	private NodeAdapter nodeAdapter;

	private List<Node> sons = new ArrayList<Node>();

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.node_list);

		Node node = (Node) getIntent().getSerializableExtra("node");

		Log.d("NodeNext", node.getTitle());

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
