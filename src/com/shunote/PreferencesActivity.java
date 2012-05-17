package com.shunote;

import com.shunote.AppCache.Configuration;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {

	private PreferenceCategory account;
	private Preference account_change;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
		MyApplication.getInstance().addActivity(this);

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
						// TODO Auto-generated method stub

						
						
						return true;
					}

				});

	}
}
