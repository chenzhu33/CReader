package org.carelife.creader.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import org.carelife.creader.R;
public class SplashActivity extends Activity {

	private final int SPLASH_DISPLAY_LENGHT = 1000;
	private boolean cancel_flag = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		cancel_flag = true;
		new Handler().postDelayed(new Runnable() {
			public void run() {
				if(cancel_flag){
					Intent mainIntent = new Intent(SplashActivity.this,
							MainActivity.class);
					SplashActivity.this.startActivity(mainIntent);
					SplashActivity.this.finish();
				}
				
			}

		}, SPLASH_DISPLAY_LENGHT);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stubf
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
				cancel_flag = false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}