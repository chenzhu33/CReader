package org.carelife.creader.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.carelife.creader.db.BookDao;
import org.carelife.creader.ui.adapter.BaseFragmentPagerAdapter;
import org.carelife.creader.ui.fragment.SearchHistoryFragment;
import org.carelife.creader.ui.fragment.SearchHotFragment;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

import org.carelife.creader.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SearchPage extends FragmentActivity {

	LinearLayout outside;
	InputMethodManager imm;
	private SharedPreferences sp;
	private Editor edit;

	private RadioGroup tabGroup;
	private RadioButton hot, history;
	protected BookDao bookDao;
	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchpage);
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		outside = (LinearLayout) findViewById(R.id.search_outside);
		bookDao = BookDao.getInstance(SearchPage.this);

		buildActionBarAndViewPagerTitles();

		outside.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		});

		tabGroup = (RadioGroup) findViewById(R.id.search_group);
		hot = (RadioButton) findViewById(R.id.group_hot);
		history = (RadioButton) findViewById(R.id.group_history);

		tabGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
				case R.id.group_hot:
					pager.setCurrentItem(0);
					hot.setTextColor(Color.WHITE);
					history.setTextColor(Color.BLACK);
					hot.setBackgroundResource(R.drawable.cate_left_bt);
					history.setBackgroundResource(R.drawable.cate_right_g_bt);
					break;
				case R.id.group_history:
					pager.setCurrentItem(1);
					hot.setTextColor(Color.BLACK);
					history.setTextColor(Color.WHITE);
					history.setBackgroundResource(R.drawable.cate_right_bt);
					hot.setBackgroundResource(R.drawable.cate_left_g_bt);
					break;
				default:
					break;
				}
			}
		});

	}

	private void buildActionBarAndViewPagerTitles() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayShowTitleEnabled(true);

		actionBar.setTitle(getString(R.string.search));
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

		List<Fragment> fragmentList = new ArrayList<Fragment>();
		fragmentList.add(SearchHotFragment.newInstance());
		fragmentList.add(SearchHistoryFragment.newInstance());
		FragmentPagerAdapter adapter = new BaseFragmentPagerAdapter(
				getSupportFragmentManager(), fragmentList);

		pager = (ViewPager) findViewById(R.id.vPager);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(pageChangeListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_menu_searchmainactivity,
				menu);
		final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		searchView.setIconifiedByDefault(false);
		searchView.setSubmitButtonEnabled(true);
		searchView.setLayoutParams(new LayoutParams(getResources()
				.getDisplayMetrics().widthPixels / 4 * 3,
				LayoutParams.WRAP_CONTENT));
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {

				String query_word = query.trim();
				if (query_word.equals("")) {
					return true;
				}
				bookDao.insertHistory(query_word);
				try {
					query_word = URLEncoder.encode(query_word, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (query_word.equals("")) {
					return true;
				}
				edit.putString("querystring", query_word);
				edit.commit();

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm.isActive())
					imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
							InputMethodManager.HIDE_NOT_ALWAYS);
				Intent intent = new Intent(SearchPage.this,
						SearchResultList.class);
				startActivity(intent);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			SearchPage.this.finish();
			return true;
		}

		return true;
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int arg0) {

			switch (arg0) {
			case 0:
				hot.setTextColor(Color.WHITE);
				history.setTextColor(Color.BLACK);
				hot.setBackgroundResource(R.drawable.cate_left_bt);
				history.setBackgroundResource(R.drawable.cate_right_g_bt);
				break;
			case 1:
				pager.setCurrentItem(1);
				hot.setTextColor(Color.BLACK);
				history.setTextColor(Color.WHITE);
				history.setBackgroundResource(R.drawable.cate_right_bt);
				hot.setBackgroundResource(R.drawable.cate_left_g_bt);
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
