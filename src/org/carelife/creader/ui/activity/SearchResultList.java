package org.carelife.creader.ui.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.carelife.creader.dao.SearchData;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.db.BookDao;
import org.carelife.creader.ui.adapter.SearchResultAdapter;
import org.carelife.creader.util.FileUtil;
import org.carelife.creader.util.ToastUtil;
import org.carelife.creader.util.XmlUtil;

import org.carelife.creader.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

public class SearchResultList extends Activity implements OnScrollListener {
	ListView rank_cate_list = null;
	private SearchResultAdapter list_Adapter;
	View listaddview;
	int mCount = 1;// 列表第一页
	int MAX_COUNT = 1;// 列表最大页
	FileUtil fm;
	private SharedPreferences sp;
	private Editor edit;
	ToastUtil toast;
	int list_once = 10;
	HashMap<String, Object> xml_data;
	List<SearchData> data_list = new ArrayList<SearchData>();
	String searchstring;
	Button back_bt;
	LinearLayout progressbar;

	EditText querytext;
	ImageView search_button;
	ImageButton clear_button;
	InputMethodManager imm;
	BookDao bookDao;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				progressbar.setVisibility(View.GONE);
				ToastUtil.getInstance(SearchResultList.this).setText("亲，您的网络不给力啊，稍后再试吧...");
				break;
			case 0:
				if (null == xml_data) {
					toast.setText("没有搜索结果啦，不好意思:P");
					progressbar.setVisibility(View.GONE);
					return;
				}
				data_list = (List<SearchData>) xml_data.get("searchitem");
				MAX_COUNT = Integer.parseInt((String) xml_data.get("maxpage"));

				progressbar.setVisibility(View.GONE);
				rank_cate_list.setVisibility(View.VISIBLE);
				LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				listaddview = lay.inflate(R.layout.booklistmore, null);
				rank_cate_list.addFooterView(listaddview);

				list_Adapter = new SearchResultAdapter(SearchResultList.this,
						data_list);
				rank_cate_list.setAdapter(list_Adapter);
				rank_cate_list.setOnScrollListener(SearchResultList.this);
				if (MAX_COUNT == 1) {
					invisibleFooter();
				}
				break;
			case 1:
				if (null == xml_data) {
					toast.setText("没有数据啦");
					invisibleFooter();
					return;
				}
				List<SearchData> temp_list = (List<SearchData>) xml_data
						.get("searchitem");

				for (int j = 0; j < temp_list.size(); j++) {
					data_list.add(temp_list.get(j));
				}

				rank_cate_list.invalidateViews();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.searchresultlist);

		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		fm = new FileUtil();
		toast = ToastUtil.getInstance(this);

		progressbar = (LinearLayout) findViewById(R.id.rankcatelist_progressbar2);
		progressbar.setVisibility(View.VISIBLE);

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		querytext = (EditText) findViewById(R.id.query_text2);
		clear_button = (ImageButton) findViewById(R.id.search_query_clear2);
		search_button = (ImageView) findViewById(R.id.search_query_button2);
		bookDao = BookDao.getInstance(SearchResultList.this);

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
				Intent intent = new Intent(SearchResultList.this,
						SearchResultList.class);
				SearchResultList.this.finish();
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

		searchstring = sp.getString("querystring", "");
		if (searchstring.equals("")) {
			toast.setText("亲，查询词为空~");
			SearchResultList.this.finish();
		}
		String show_string = searchstring;
		try {
			show_string = URLDecoder.decode(show_string,"utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		querytext.setText(show_string);
		
		rank_cate_list = (ListView) findViewById(R.id.cate_rank_list2);
		
//		//反射更改快速下拉条
//				try { 
//				    Field f = AbsListView.class.getDeclaredField("mFastScroller"); 
//				    f.setAccessible(true); 
//				    Object o=f.get(rank_cate_list); 
//				    f=f.getType().getDeclaredField("mThumbDrawable"); 
//				    f.setAccessible(true); 
//				    Drawable drawable=(Drawable) f.get(o); 
//				    drawable= getResources().getDrawable(R.drawable.scroller);
//				    f.set(o,drawable); 
////				    Toast.makeText(this, f.getType().getName(), 1000).show(); 
//				} catch (Exception e) { 
//				    throw new RuntimeException(e); 
//				}

		new Thread() {
			public void run() {
				try {
					xml_data = XmlUtil.getXML(UrlHelper.searchurl, searchstring,
							"&p=" + mCount +"&fixpos=0", 0);
					handler.sendEmptyMessage(0);
				} catch (IOException e) {
					handler.sendEmptyMessage(-1);
					e.printStackTrace();
				}
			}
		}.start();

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
			SearchResultList.this.finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void invisibleFooter() {
		rank_cate_list.removeFooterView(listaddview);
	}

	/*--------------------       滚动加载部分        ------------------------------*/
	private AsyncTask<Long, Void, Void> mTask;

	private void additionalReading() {

		if (mCount >= MAX_COUNT) {
			invisibleFooter();
			return;
		}

		if (mTask != null && mTask.getStatus() == AsyncTask.Status.RUNNING) {
			return;
		}

		mTask = new AsyncTask<Long, Void, Void>() {
			@Override
			protected Void doInBackground(Long[] params) {
				try {
					Thread.sleep(params[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			};

			protected void onPostExecute(Void result) {
				mCount++;
				new Thread() {
					public void run() {
						try {
							xml_data = XmlUtil.getXML(UrlHelper.searchurl, searchstring,
									"&p=" + mCount +"&fixpos=0", 0);
						} catch (IOException e) {
							ToastUtil.getInstance(SearchResultList.this).setText("亲，您的网络不给力啊，稍后再试吧...");
							e.printStackTrace();
						}
						handler.sendEmptyMessage(1);
					}
				}.start();

			};
//		}.execute();
		}.execute(Math.abs(new Random(System.currentTimeMillis()).nextLong() % 2000));

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
