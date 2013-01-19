package org.carelife.creader.ui.activity;

import org.carelife.creader.R;
import org.carelife.creader.ui.view.MyMoveView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;

public class CateList extends Activity {
	private MyMoveView moveView;
	private ActionBar actionBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int Height = display.getHeight();
		moveView = new MyMoveView(this);
		moveView.initScreenSize(width, Height);
		setContentView(moveView);

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		SharedPreferences sp = CateList.this.getSharedPreferences("sogounovel", Context.MODE_PRIVATE);
		actionBar.setTitle(sp.getString("catename", "Πώ»Γ"));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_menu_rankcate, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			CateList.this.finish();
			break;
		case R.id.menu_search:
			Intent intent = new Intent(CateList.this, SearchPage.class);
			CateList.this.startActivity(intent);
			break;
		case R.id.menu_sidebar:
			int now_state = moveView.getNowState();
			if (now_state == MyMoveView.MAIN) {
				moveView.moveToLeft(true);
			} else {
				moveView.moveToMain(true);
			}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

}
