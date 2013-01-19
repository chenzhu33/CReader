package org.carelife.creader.ui.activity;

import java.util.ArrayList;
import java.util.List;

import org.carelife.creader.R;
import org.carelife.creader.ui.adapter.BaseFragmentPagerAdapter;
import org.carelife.creader.ui.fragment.NewsFragment;
import org.carelife.creader.ui.fragment.NovelFragment;
import com.viewpagerindicator.TabPageIndicator;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends FragmentActivity {
	private ActionBar actionBar;
	private ViewPager pager;
	private TabPageIndicator indicator;
	public static String[] newsGroupName = { "国内", "社会", "军事", "国际", "图库",
			"快讯", "体育", "科技", "财经", "娱乐", "汽车", "女人" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);

		buildActionBar();
		buildViewPager();

	}

	private void buildViewPager() {
		List<Fragment> fragmentList = new ArrayList<Fragment>();
		fragmentList.add(NovelFragment.newInstance());
		for (int i = 0; i < newsGroupName.length; i++) {
			fragmentList.add(NewsFragment.newInstance(i));
		}

		FragmentPagerAdapter adapter = new MainFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList);

		pager = (ViewPager) findViewById(R.id.vPager);
		pager.setOffscreenPageLimit(5);
		pager.setAdapter(adapter);
		pager.setCurrentItem(0);
		//pager.setOnPageChangeListener(pageChangeListener);

		indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.setOnPageChangeListener(pageChangeListener);

	}

	private void buildActionBar() {
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle("CReader");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("launcher", false);
			startActivity(intent);
			break;
		case R.id.menu_novel:
			pager.setCurrentItem(0);
			break;
		case R.id.menu_news:
			pager.setCurrentItem(1);
			break;
		case R.id.menu_settings:
			Intent intent2 = new Intent(MainActivity.this, GlobalSetting.class);
			MainActivity.this.startActivity(intent2);
			break;
		case R.id.menu_addbook:
			Intent intent3 = new Intent(MainActivity.this,
					BookStoreActivity.class);
			MainActivity.this.startActivity(intent3);
			break;
		case R.id.menu_searchbook:
			Intent intent4 = new Intent(MainActivity.this, SearchPage.class);
			MainActivity.this.startActivity(intent4);
			break;
		case R.id.menu_exit:
			MainActivity.this.finish();
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	private class MainFragmentPagerAdapter extends BaseFragmentPagerAdapter {

		public MainFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public MainFragmentPagerAdapter(FragmentManager fm,
				List<Fragment> fragments) {
			super(fm, fragments);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (position == 0)
				return "小说";
			return newsGroupName[(position - 1) % newsGroupName.length];
		}
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {
			if(0 == arg0) {
				indicator.setVisibility(View.GONE);
			} else {
				indicator.setVisibility(View.VISIBLE);
			}
		}

	};

}