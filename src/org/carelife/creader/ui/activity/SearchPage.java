package org.carelife.creader.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.carelife.creader.db.BookDao;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;

import org.carelife.creader.R;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SearchPage extends TabActivity {

	EditText querytext;
	ImageView search_button;
	ImageButton clear_button;
	LinearLayout outside;
	InputMethodManager imm;
	private SharedPreferences sp;
	private Editor edit;

	private TabHost searchTabhost;
	private RadioGroup tabGroup;
	private RadioButton hot, history;
	protected BookDao bookDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.searchpage);
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		querytext = (EditText) findViewById(R.id.query_text);
		outside = (LinearLayout) findViewById(R.id.search_outside);
		clear_button = (ImageButton) findViewById(R.id.search_query_clear);
		search_button = (ImageView) findViewById(R.id.search_query_button);
		bookDao = BookDao.getInstance(SearchPage.this);
		outside.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		});

		search_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				String query_word = querytext.getText().toString().trim();
				if (query_word.equals("")) {
					return;
				}
				bookDao.insertHistory(query_word);
				try {
					query_word = URLEncoder.encode(query_word, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (query_word.equals("")) {
					return;
				}
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				edit.putString("querystring", query_word);
				edit.commit();
				Intent intent = new Intent(SearchPage.this,
						SearchResultList.class);
				startActivity(intent);
			}
		});

		clear_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				querytext.setText("");
			}
		});

		querytext.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
				if (!querytext.getText().toString().trim().equals("")) {
					clear_button.setVisibility(View.VISIBLE);
				} else {
					clear_button.setVisibility(View.GONE);
				}
			}
		});

		searchTabhost = this.getTabHost();
		tabGroup = (RadioGroup) findViewById(R.id.search_group);
		hot = (RadioButton) findViewById(R.id.group_hot);
		history = (RadioButton) findViewById(R.id.group_history);

		// searchTabhost.setBackgroundColor(Color.argb(150, 22, 70, 150));
		Intent intent = new Intent().setClass(this, SearchHot.class);
		searchTabhost.addTab(searchTabhost.newTabSpec("searchhot")
				.setIndicator("").setContent(intent));

		intent = new Intent().setClass(this, SearchHistory.class);
		searchTabhost.addTab(searchTabhost.newTabSpec("searchhistory")
				.setIndicator("").setContent(intent));

		tabGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
				case R.id.group_hot:
					searchTabhost.setCurrentTabByTag("searchhot");
					hot.setTextColor(Color.WHITE);
					history.setTextColor(Color.BLACK);
					hot.setBackgroundResource(R.drawable.cate_left_bt);
					history.setBackgroundResource(R.drawable.cate_right_g_bt);
					break;
				case R.id.group_history:
					searchTabhost.setCurrentTabByTag("searchhistory");
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

}
