package org.carelife.creader.ui.activity;

import java.util.ArrayList;
import java.util.List;

import org.carelife.creader.R;
import org.carelife.creader.ui.adapter.BaseFragmentPagerAdapter;
import org.carelife.creader.ui.fragment.BookStoreFragment;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class BookStoreActivity extends FragmentActivity {

	private RadioButton cate, rank;
	private RadioGroup rGroup;
	private ActionBar actionBar;
	private ViewPager vPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSharedPreferences("sogounovel", MODE_PRIVATE);
		setContentView(R.layout.maintab);

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle("Êé¿â");

		List<Fragment> fragmentList = new ArrayList<Fragment>();
		fragmentList.add(BookStoreFragment.newInstance(0));
		fragmentList.add(BookStoreFragment.newInstance(1));
		
		vPager = (ViewPager) findViewById(R.id.vPager);
		vPager.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(),fragmentList));
		vPager.setOnPageChangeListener(pageChangeListener);

		rGroup = (RadioGroup)findViewById(R.id.cate_group);
		cate = (RadioButton) findViewById(R.id.tabbar_cate);
		rank = (RadioButton) findViewById(R.id.tabbar_rank);
		
		rGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch (checkedId) {
				case R.id.tabbar_cate:
					vPager.setCurrentItem(0);
					cate.setTextColor(Color.WHITE);
					rank.setTextColor(Color.BLACK);
					cate.setBackgroundResource(R.drawable.cate_left_bt);
					rank.setBackgroundResource(R.drawable.cate_right_g_bt);
					break;
				case R.id.tabbar_rank:
					vPager.setCurrentItem(1);
					rank.setTextColor(Color.WHITE);
					cate.setTextColor(Color.BLACK);
					rank.setBackgroundResource(R.drawable.cate_right_bt);
					cate.setBackgroundResource(R.drawable.cate_left_g_bt);
					break;
				default:
					break;
				}				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_menu_bookstore, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			BookStoreActivity.this.finish();
			break;
		case R.id.menu_search:
			Intent intent = new Intent(BookStoreActivity.this, SearchPage.class);
			BookStoreActivity.this.startActivity(intent);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				cate.setTextColor(Color.WHITE);
				rank.setTextColor(Color.BLACK);
				cate.setBackgroundResource(R.drawable.cate_left_bt);
				rank.setBackgroundResource(R.drawable.cate_right_g_bt);
				break;
			case 1:
				rank.setTextColor(Color.WHITE);
				cate.setTextColor(Color.BLACK);
				rank.setBackgroundResource(R.drawable.cate_right_bt);
				cate.setBackgroundResource(R.drawable.cate_left_g_bt);
				break;
			default:
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
}
