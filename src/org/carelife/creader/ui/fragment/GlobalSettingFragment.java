package org.carelife.creader.ui.fragment;

import org.carelife.creader.R;
import org.carelife.creader.util.LogUtil;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

public class GlobalSettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
	private static String VERSION = "version_key";
	private static String CACHE = "cache";
	private Preference cache;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref);

		findPreference(GlobalSettingFragment.VERSION).setSummary(
				buildVersionInfo());
		cache = findPreference(GlobalSettingFragment.CACHE);
		buildSummary();

        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
		
	}

	private void buildSummary() {
		String value = PreferenceManager.getDefaultSharedPreferences(
				getActivity()).getString(GlobalSettingFragment.CACHE, "10");
		cache.setSummary(getActivity().getResources().getStringArray(
				R.array.cache)[(Integer)(Integer.valueOf(value)/20)]);
	}
	
	private String buildVersionInfo() {
		String version = "";
		PackageManager packageManager = getActivity().getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getActivity()
					.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			LogUtil.e(e.getMessage());
		}

		if (packInfo != null) {
			version = packInfo.versionName;
		}

		if (!TextUtils.isEmpty(version)) {
			return version;
		} else {
			return "";
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {

        if (arg1.equals(GlobalSettingFragment.CACHE)) {
            

        }

        buildSummary();
		
	}
}
