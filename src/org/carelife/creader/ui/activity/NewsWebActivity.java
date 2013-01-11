package org.carelife.creader.ui.activity;

import java.util.Timer;
import java.util.TimerTask;

import org.carelife.creader.R;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class NewsWebActivity extends Activity {

	WebView newswebview;
	LinearLayout l_bar;
	String cookieString, Orginal_url;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tcwebview);
		l_bar = (LinearLayout) findViewById(R.id.tc_webview_progressbar);

		Orginal_url = this.getIntent().getStringExtra("url");
		newswebview = (WebView) findViewById(R.id.webview_map);
		if (savedInstanceState != null)
			newswebview.restoreState(savedInstanceState);

		try {
			WebSettings s = newswebview.getSettings();
			s.setJavaScriptEnabled(true);
			s.setPluginsEnabled(true);// 设置允许Gears插件来实现网页中的Flash动画显示
			s.setNeedInitialFocus(false);
			s.setSupportZoom(true);
			newswebview.setHorizontalScrollBarEnabled(false);
			newswebview.setVerticalScrollBarEnabled(true);
			newswebview.setVerticalScrollbarOverlay(true);
			newswebview.setWebChromeClient(new chromeClient());
			newswebview.setWebViewClient(new webViewClient());

			s.setUserAgentString(s.getUserAgentString() + " SogouNovel Android");
			s.setPluginState(PluginState.OFF);
			s.setLoadsImagesAutomatically(PreferenceManager
					.getDefaultSharedPreferences(this).getBoolean(
							"loadImageState", true));
			s.setUseWideViewPort(true);// overview
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
		if (Orginal_url != null) {
			newswebview.loadUrl(Orginal_url);
		} else {
			NewsWebActivity.this.finish();
		}
		
		ImageView back = (ImageView) findViewById(R.id.web_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (newswebview.canGoBack()) {
					newswebview.goBack();
				} else {
					NewsWebActivity.this.finish();
				}
					
			}
		});
	}

	class webViewClient extends WebViewClient {

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			System.out.println("loading .... " + url);
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
			NewsWebActivity.this.setProgress(newProgress * 100);
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

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	protected void onSaveInstanceState(Bundle outState) {
		newswebview.saveState(outState);
	}

	@Override
	protected void onStop() {
		super.onStop();
//		long timeout = ViewConfiguration.getZoomControlsTimeout();
//		new Timer().schedule(new TimerTask() {
//			@Override
//			public void run() {
//				if (newswebview != null)
//					newswebview.destroy();
//			}
//		}, timeout);
	}
}
