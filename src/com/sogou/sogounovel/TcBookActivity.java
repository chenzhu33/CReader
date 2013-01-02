package com.sogou.sogounovel;

import java.util.Timer;
import java.util.TimerTask;

import com.sogou.R;
import com.sogou.db.BookDao;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TcBookActivity extends Activity {

	WebView bookwebview;
	private SharedPreferences sp;
	LinearLayout l_bar;
	BookDao bookdao;
	String cookieString, Orginal_url, book_name, author_name;

	/** Called when the activity is first created. */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tcwebview);
		bookdao = BookDao.getInstance(this);
		l_bar = (LinearLayout) findViewById(R.id.tc_webview_progressbar);
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		sp.edit();
		Orginal_url = sp.getString("webview_url", "");
		book_name = sp.getString("webview_book_name", "");
		author_name = sp.getString("webview_author_name", "");
		System.out.println("Orginal URL is =" + Orginal_url);

		bookwebview = (WebView) findViewById(R.id.webview_map);
		if (savedInstanceState != null)
			bookwebview.restoreState(savedInstanceState);

		try {
			WebSettings s = bookwebview.getSettings();
			s.setJavaScriptEnabled(true);
			s.setPluginsEnabled(true);// 设置允许Gears插件来实现网页中的Flash动画显示
			// webView.getSettings().setLightTouchEnabled(true);
			s.setNeedInitialFocus(false);
			s.setSupportZoom(true);
			// webView.getSettings().setBuiltInZoomControls(true);//
			bookwebview.setHorizontalScrollBarEnabled(false);
			bookwebview.setVerticalScrollBarEnabled(true);
			bookwebview.setVerticalScrollbarOverlay(true);
			bookwebview.setWebChromeClient(new chromeClient());
			bookwebview.setWebViewClient(new webViewClient());

			// cookieString = "mypos= liuxu ; domain=.wap.sogou.com";
			// //cookieManager.setCookie("cookie", cookieString);
			// CookieSyncManager.createInstance(TcBookActivity.this);
			// CookieManager cookieManager = CookieManager.getInstance();
			// cookieManager.setCookie("wap.sogou.com", cookieString);
			// CookieSyncManager.getInstance().sync();

			s.setUserAgentString(s.getUserAgentString() + " SogouNovel Android");// 设置UA
			// //Log.e("user agent",webView.getSettings().getUserAgentString());
			// webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
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
			bookwebview.loadUrl(Orginal_url);
		} else {
			TcBookActivity.this.finish();
		}

		ImageView back = (ImageView) findViewById(R.id.web_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bookwebview.canGoBack()) {
					bookwebview.goBack();
				} else {
					TcBookActivity.this.finish();
				}
			}
		});
	}

	class webViewClient extends WebViewClient {
		// 重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。

		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			System.out.println("loading .... " + url);
			view.loadUrl(url);
			// 如果不需要其他对点击链接事件的处理返回true，否则返回false
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
			// 动态在标题栏显示进度条
			TcBookActivity.this.setProgress(newProgress * 100);
			super.onProgressChanged(view, newProgress);
		}

		public void onReceivedTitle(WebView view, String title) {

		}

		// 允许webview定位

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
		bookwebview.saveState(outState);
	}

	@Override
	protected void onStop() {
		super.onStop();
//		long timeout = ViewConfiguration.getZoomControlsTimeout();
//		new Timer().schedule(new TimerTask() {
//			@Override
//			public void run() {
//				if (bookwebview != null)
//					bookwebview.destroy();
//			}
//		}, timeout);
	}
}
