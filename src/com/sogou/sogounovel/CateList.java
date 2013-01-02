package com.sogou.sogounovel;

import com.sogou.ui.MyMoveView;
import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;

public class CateList extends Activity {
	MyMoveView moveView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int Height = display.getHeight();
		moveView = new MyMoveView(this);
		moveView.initScreenSize(width, Height);
		setContentView(moveView);

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_MENU) {

			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			CateList.this.finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}


}
