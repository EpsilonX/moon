package com.shunote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;

import com.shunote.AppCache.Cache;
import com.shunote.AppCache.Configuration;
import com.shunote.Exception.CacheException;

public class PreferencesActivity extends PreferenceActivity {

	private Context mContext;
	private PreferenceCategory account;
	private Preference account_change;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		MyApplication.getInstance().addActivity(this);
		mContext = this;

		account = (PreferenceCategory) findPreference("account");
		account_change = (Preference) findPreference("account_change");

		Configuration config = new Configuration(this);
		String PREFS_NAME = config.getValue("SPTAG");
		SharedPreferences sp = getSharedPreferences(PREFS_NAME,
				MODE_WORLD_READABLE);
		String name = sp.getString("USERNAME", "");
		account.setTitle("当前帐号:" + name);

		account_change
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {

						Cache cache = Cache.getInstance();

						try {
							cache.init(getApplicationContext());
							cache.clear();
						} catch (CacheException e) {
							e.printStackTrace();
						}

						new AlertDialog.Builder(mContext)
								.setMessage("您确认注销改帐号?")
								.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub
												Intent login = new Intent();
												login.setClass(
														PreferencesActivity.this,
														ShunoteActivity.class);
												startActivity(login);
												finish();
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub

											}
										}).show();

						return true;
					}

				});

	}
}
