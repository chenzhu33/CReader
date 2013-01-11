package org.carelife.creader.ui.activity;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.carelife.creader.dao.RankData;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.db.BookDao;
import org.carelife.creader.util.ToastUtil;
import org.carelife.creader.util.XmlUtil;

import org.carelife.creader.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchHot extends Activity {
	private ListView hotList = null;
	private List<String> hotBookList;
	private BookAdapter book_Adapter;
	private SharedPreferences sp;
	private Editor edit;
	private ToastUtil toast;
	private BookDao bookDao;
	private HashMap<String, Object> xml_data;
	private LinearLayout progressbar;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				progressbar.setVisibility(View.GONE);
				ToastUtil.getInstance(SearchHot.this).setText("亲，您的网络不给力啊，稍后再试吧...");
				break;
			case 0:
				if (null == xml_data) {
					toast.setText("没网络啊，检查下网络设置吧！");
					progressbar.setVisibility(View.GONE);
					return;
				}
				hotBookList = new ArrayList<String>();
				List<RankData> rankl = (List<RankData>) xml_data
						.get("rankitem");
				for (int i = 0; i < rankl.size(); i++) {
					hotBookList.add(rankl.get(i).getbookname());
				}
				bookDao.insertHotword(hotBookList);
				progressbar.setVisibility(View.GONE);
			case 1:
				hotList = (ListView) findViewById(R.id.searchhot_list);
				book_Adapter = new BookAdapter(SearchHot.this, hotBookList);
				hotList.setAdapter(book_Adapter);
				progressbar.setVisibility(View.GONE);
				hotList.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> l, View v,
							int position, long id) {
						if (position >= hotBookList.size()) {
							toast.setText("亲,轻点点，别急啊");
							return;
						}
						String query_word = hotBookList.get(position)
								.toString().trim();
						bookDao.insertHistory(query_word);
						try {
							query_word = URLEncoder.encode(query_word, "utf-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						Intent intent = new Intent(SearchHot.this,
								SearchResultList.class);
						edit.putString("querystring", query_word);
						edit.commit();
						startActivity(intent);
					}
				});
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.searchhot);
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		toast = ToastUtil.getInstance(this);
		bookDao = BookDao.getInstance(SearchHot.this);
		progressbar = (LinearLayout) findViewById(R.id.searchhot_progressbar);

		hotBookList = bookDao.getHotwords();
		if (hotBookList == null) {
			new Thread() {
				public void run() {
					HashMap<String, Object> tmp_data;
					try {
						tmp_data = XmlUtil.getXML(
								UrlHelper.rankurl, UrlHelper.goto_data_search, "",
								1);
						int count = 0;
						if(tmp_data != null){
							count = Integer.parseInt((String) tmp_data.get("count"));
						}
						
						xml_data = XmlUtil.getXML(UrlHelper.rankurl,
								UrlHelper.goto_data_search, "&length=" + count, 1);
						handler.sendEmptyMessage(0);

					} catch (IOException e) {
						handler.sendEmptyMessage(-1);
						e.printStackTrace();
					}

				}
			}.start();
		} else {
			handler.sendEmptyMessage(1);
		}

	}

	private class BookAdapter extends BaseAdapter {
		List<String> data_list;
		Context context;

		public BookAdapter(Context context, List<String> results) {
			this.data_list = results;
			this.context = context;
		}

		public int getCount() {
			return data_list.size();
		}

		public Object getItem(int position) {
			return data_list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			HolderView holder;
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.searchhotitem,
						null);
				holder = new HolderView();
				holder.title = (TextView) convertView
						.findViewById(R.id.searchhot_bookname);
				convertView.setTag(holder);
			} else {
				holder = (HolderView) convertView.getTag();
			}

			if (null != data_list.get(position)) {
				holder.title.setText(data_list.get(position).toString().trim());
			}
			return convertView;
		}

		private class HolderView {
			TextView title;
		}
	}
}
