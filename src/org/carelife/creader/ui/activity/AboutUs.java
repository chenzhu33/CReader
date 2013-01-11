package org.carelife.creader.ui.activity;

import org.carelife.creader.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class AboutUs extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.aboutus);
		TextView t = (TextView)findViewById(R.id.aboutus_version);
		
		PackageManager packageManager = getPackageManager();
        PackageInfo packInfo;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(),0);
	        String version = packInfo.versionName;
			t.setText("°æ±¾£º"+version);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
}
