package org.carelife.creader.ui.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.carelife.creader.dao.SearchData;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.db.BookDao;
import org.carelife.creader.ui.adapter.SearchResultAdapter;
import org.carelife.creader.util.ToastUtil;
import org.carelife.creader.util.XmlUtil;

import org.carelife.creader.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

public class SearchResultList extends Activity implements OnScrollListener {
	private ListView rank_cate_list = null;
	private SearchResultAdapter listAdapter;
	private View listaddview;
	private int mCount = 1;
	private int MAX_COUNT = 1;
	private SharedPreferences sp;
	private Editor edit;
	private ToastUtil toast;
	private List<SearchData> adapterData;
	private String searchstring;
	private LinearLayout progressbar;
	private BookDao bookDao;

	private GetDataTask mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchresultlist);

		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		toast = ToastUtil.getInstance(this);

        buildActionBarAndViewPagerTitles();
		
		progressbar = (LinearLayout) findViewById(R.id.rankcatelist_progressbar2);
		progressbar.setVisibility(View.VISIBLE);

		bookDao = BookDao.getInstance(SearchResultList.this);

		searchstring = sp.getString("querystring", "");
		if (searchstring.equals("")) {
			toast.setText("亲，查询词为空~");
			SearchResultList.this.finish();
		}
		
		rank_cate_list = (ListView) findViewById(R.id.cate_rank_list2);
		LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listaddview = lay.inflate(R.layout.booklistmore, null);
		rank_cate_list.addFooterView(listaddview);
		adapterData = new ArrayList<SearchData>();
		listAdapter = new SearchResultAdapter(SearchResultList.this,
				adapterData);
		rank_cate_list.setAdapter(listAdapter);
		rank_cate_list.setOnScrollListener(SearchResultList.this);
		new GetDataTask().execute();
	}
	
	private void buildActionBarAndViewPagerTitles() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);

        actionBar.setTitle(getString(R.string.search));
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu_searchmainactivity, menu);
        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setLayoutParams(new LayoutParams(getResources().getDisplayMetrics().widthPixels / 4 * 3, LayoutParams.WRAP_CONTENT));
		String show_string = searchstring;
		try {
			show_string = URLDecoder.decode(show_string,"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        searchView.setQuery(show_string, false);
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
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive())
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
				
				edit.putString("querystring", query_word);
				edit.commit();
				Intent intent = new Intent(SearchResultList.this,
						SearchResultList.class);
				SearchResultList.this.finish();
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
                SearchResultList.this.finish();
            	return true;
        }

        return true;
    }

	private void invisibleFooter() {
		rank_cate_list.removeFooterView(listaddview);
	}

	private class GetDataTask extends AsyncTask<Integer, Void, HashMap<String, Object>> {

		@Override
		protected HashMap<String, Object> doInBackground(Integer... params) {
			HashMap<String, Object> tmpData = null;
			mCount++;
			try {
				tmpData = XmlUtil.getXML(UrlHelper.searchurl, searchstring,
						"&p=" + mCount +"&fixpos=0", 0);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return tmpData;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(HashMap<String, Object> data) {
			progressbar.setVisibility(View.GONE);
			if(data == null) {
				ToastUtil.getInstance(SearchResultList.this).setText("亲，您的网络不给力啊，稍后再试吧...");
			} else {
				rank_cate_list.setVisibility(View.VISIBLE);
				adapterData.addAll((List<SearchData>) data.get("searchitem"));
				MAX_COUNT = Integer.parseInt((String) data.get("maxpage"));
				if (MAX_COUNT == 1) {
					invisibleFooter();
				}
				listAdapter.notifyDataSetChanged();
			}

			super.onPostExecute(data);
		}
	}
	
	private void additionalReading() {
		if (mCount >= MAX_COUNT) {
			invisibleFooter();
			return;
		}
		if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
			return;
		}
		mTask = new GetDataTask();
		mTask.execute();
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		if (totalItemCount == firstVisibleItem + visibleItemCount) {
			additionalReading();
		}
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}
}
