package org.carelife.creader.ui.activity;

import org.carelife.creader.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WebViewActivity extends Activity {

	private WebView myWebview;
	private LinearLayout l_bar;
	private String orginalUrl;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tcwebview);
		l_bar = (LinearLayout) findViewById(R.id.tc_webview_progressbar);

		orginalUrl = getIntent().getStringExtra("url");
		myWebview = (WebView) findViewById(R.id.webview_map);
		if (savedInstanceState != null)
			myWebview.restoreState(savedInstanceState);

		try {
			WebSettings s = myWebview.getSettings();
			s.setJavaScriptEnabled(true);
			s.setPluginsEnabled(true);
			s.setNeedInitialFocus(false);
			s.setSupportZoom(true);
			myWebview.setHorizontalScrollBarEnabled(false);
			myWebview.setVerticalScrollBarEnabled(true);
			myWebview.setVerticalScrollbarOverlay(true);
			myWebview.setWebChromeClient(new chromeClient());
			myWebview.setWebViewClient(new webViewClient());

			s.setUserAgentString(s.getUserAgentString() + " SogouNovel Android");
			s.setPluginState(PluginState.OFF);
			s.setLoadsImagesAutomatically(PreferenceManager
					.getDefaultSharedPreferences(this).getBoolean(
							"loadImageState", true));
			s.setUseWideViewPort(true);
			s.setLoadWithOverviewMode(true);
			s.setBuiltInZoomControls(true);//
			s.setAppCacheEnabled(true);
			s.setSaveFormData(true);
			s.setSavePassword(true);
			s.setGeolocationEnabled(true);
			s.setGeolocationDatabasePath("/data/data/com.sogou.activit.src/databases/");
			s.setDomStorageEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (orginalUrl != null) {
			myWebview.loadUrl(orginalUrl);
		} else {
			WebViewActivity.this.finish();
		}

		ImageView back = (ImageView) findViewById(R.id.web_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (myWebview.canGoBack()) {
					myWebview.goBack();
				} else {
					WebViewActivity.this.finish();
				}
			}
		});
	}

	class webViewClient extends WebViewClient {

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			l_bar.setVisibility(View.VISIBLE);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			l_bar.setVisibility(View.GONE);
			super.onPageFinished(view, url);
		}

	}

	class chromeClient extends WebChromeClient {

		public void onProgressChanged(WebView view, int newProgress) {
			WebViewActivity.this.setProgress(newProgress * 100);
			super.onProgressChanged(view, newProgress);
		}

		public void onReceivedTitle(WebView view, String title) {

		}

		public void onGeolocationPermissionsShowPrompt(String origin,
				GeolocationPermissions.Callback callback) {
			super.onGeolocationPermissionsShowPrompt(origin, callback);
			callback.invoke(origin, true, false);
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		myWebview.saveState(outState);
	}

}
