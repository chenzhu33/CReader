package org.carelife.creader.ui.view;


import org.carelife.creader.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.FrameLayout;

public class TcWebView extends WebView  {
	
	private Context mContext;
	private MyWebChromeClient mWebChromeClient;
	private View mCustomView;
	private FrameLayout mCustomViewContainer;
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	
	private FrameLayout	mContentView;
	private FrameLayout	mBrowserFrameLayout;
	private FrameLayout	mLayout;
	
    static final String LOGTAG = "TcWebView";
	    
	private void init(Context context) {
		mContext = context;		
//		Activity a = (Activity) mContext;
		
		mLayout = new FrameLayout(context);
		mBrowserFrameLayout = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.custom_screen, null);
		mContentView = (FrameLayout) mBrowserFrameLayout.findViewById(R.id.main_content);
		mCustomViewContainer = (FrameLayout) mBrowserFrameLayout.findViewById(R.id.fullscreen_custom_content);
		
		mLayout.addView(mBrowserFrameLayout, COVER_SCREEN_PARAMS);

		mWebChromeClient = new MyWebChromeClient();
	    setWebChromeClient(mWebChromeClient);
	    
	    setWebViewClient(new MyWebViewClient());
	       
	    // Configure the webview
	    WebSettings s = getSettings();
	    s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
	    // enable navigator.geolocation 
	    s.setGeolocationEnabled(true);
	    s.setGeolocationDatabasePath("/data/data/org.itri.TcWebView/databases/");
	    
	    s.setUserAgentString(s.getUserAgentString() + " SogouNovel Android");
	    
    	s.setPluginsEnabled(true);// 设置允许Gears插件来实现网页中的Flash动画显示
		//		webView.getSettings().setLightTouchEnabled(true);
    	s.setJavaScriptEnabled(true);
		// webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    	s.setPluginsEnabled(false); // 设置允许Gears插件来实现网页中的Flash动画显示
		s.setPluginState(PluginState.OFF);
		s.setLoadsImagesAutomatically(
				PreferenceManager.getDefaultSharedPreferences(mContext).getBoolean(
						"loadImageState", true));
		s.setNeedInitialFocus(false);
		s.setUseWideViewPort(true);// overview
		s.setLoadWithOverviewMode(true);
		s.setSupportZoom(true);
		s.setBuiltInZoomControls(true);//
		s.setAppCacheEnabled(true);
		s.setSaveFormData(true);
		s.setSavePassword(true);
		s.setGeolocationEnabled(true);
		s.setGeolocationDatabasePath(
				"/data/data/com.sogou.activit.src/databases/");
		s.setDomStorageEnabled(true);
	    
	    // enable Web Storage: localStorage, sessionStorage
	    s.setDomStorageEnabled(true);
	    
	    mContentView.addView(this);
	}

	public TcWebView(Context context) {
		super(context);
		init(context);
	}

	public TcWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TcWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	public FrameLayout getLayout() {
		return mLayout;
	}
	
    public boolean inCustomView() {
		return (mCustomView != null);
	}
    
    public void hideCustomView() {
		mWebChromeClient.onHideCustomView();
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		if ((mCustomView != null) && canGoBack()){
    			goBack();
    			return true;
    		}
    	}
    	return super.onKeyDown(keyCode, event);
    }

    private class MyWebChromeClient extends WebChromeClient {
		private Bitmap 		mDefaultVideoPoster;
		private View 		mVideoProgressView;
    	
    	@Override
		public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback)
		{
			//Log.i(LOGTAG, "here in on ShowCustomView");
    		TcWebView.this.setVisibility(View.GONE);
	        
	        // if a view already exists then immediately terminate the new one
	        if (mCustomView != null) {
	            callback.onCustomViewHidden();
	            return;
	        }
	        
	        mCustomViewContainer.addView(view);
	        mCustomView = view;
	        mCustomViewCallback = callback;
	        mCustomViewContainer.setVisibility(View.VISIBLE);
		}
		
		@Override
		public void onHideCustomView() {
			
			if (mCustomView == null)
				return;	       
			
			// Hide the custom view.
			mCustomView.setVisibility(View.GONE);
			
			// Remove the custom view from its container.
			mCustomViewContainer.removeView(mCustomView);
			mCustomView = null;
			mCustomViewContainer.setVisibility(View.GONE);
			mCustomViewCallback.onCustomViewHidden();
			
			TcWebView.this.setVisibility(View.VISIBLE);
			
	        //Log.i(LOGTAG, "set it to webVew");
		}
		
		@Override
		public Bitmap getDefaultVideoPoster() {
			//Log.i(LOGTAG, "here in on getDefaultVideoPoster");	
			if (mDefaultVideoPoster == null) {
				mDefaultVideoPoster = BitmapFactory.decodeResource(
						getResources(), R.drawable.default_video_poster);
		    }
			return mDefaultVideoPoster;
		}
		
		@Override
		public View getVideoLoadingProgressView() {
			//Log.i(LOGTAG, "here in on getVideoLoadingPregressView");
			
	        if (mVideoProgressView == null) {
	            LayoutInflater inflater = LayoutInflater.from(mContext);
	            mVideoProgressView = inflater.inflate(R.layout.video_loading_progress, null);
	        }
	        return mVideoProgressView; 
		}
    	
    	 @Override
         public void onReceivedTitle(WebView view, String title) {
            ((Activity) mContext).setTitle(title);
         }

         @Override
         public void onProgressChanged(WebView view, int newProgress) {
        	 ((Activity) mContext).getWindow().setFeatureInt(Window.FEATURE_PROGRESS, newProgress*100);
         }
         
         @Override
         public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
             callback.invoke(origin, true, false);
         }
    }
	
	private class MyWebViewClient extends WebViewClient {
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	Log.i(LOGTAG, "shouldOverrideUrlLoading: "+url);
	        view.loadUrl(url);
	        return true;
	    }
	}
	
	static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS =
        new FrameLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

}
