package com.sogou.sogounovel;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sogou.R;
import com.sogou.component.IntroduceDialog;
import com.sogou.db.BookDao;
import com.sogou.util.ToastUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchHistory extends Activity {
	ListView historyList = null;
	List<Map<String, Object>> historyBookList;
	private HistoryBookAdapter book_Adapter;
	View listaddview;
	private SharedPreferences sp;
	private Editor edit;
	ToastUtil toast;
	BookDao bookDao;
	LinearLayout llayout;
	IntroduceDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.searchhistory);
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		toast = ToastUtil.getInstance(this);

		bookDao = BookDao.getInstance(SearchHistory.this);

		historyList = (ListView) findViewById(R.id.searchhistory_list);
	
		historyList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				if (position >= historyBookList.size()) {
					toast.setText("Ç×,Çáµãµã£¬±ð¼±°¡");
					return;
				}
				String query_word = historyBookList.get(position)
						.get("bookname").toString().trim();
				bookDao.insertHistory(query_word);

				try {
					query_word = URLEncoder.encode(query_word, "utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent(SearchHistory.this,
						SearchResultList.class);
				edit.putString("querystring", query_word);
				edit.commit();
				startActivity(intent);
			}
		});
		llayout = (LinearLayout) findViewById(R.id.searchhistory_clearall);
		llayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		historyBookList = bookDao.getHistory();
		if (historyBookList == null) {
			historyBookList = new ArrayList<Map<String, Object>>();
		}
		book_Adapter = new HistoryBookAdapter(SearchHistory.this);
		historyList.setAdapter(book_Adapter);
	}
	
	protected void dialog() {
		dialog = new IntroduceDialog(SearchHistory.this,
				R.layout.dialog_clear_history, R.style.Theme_dialog);

		dialog.show();

		Button pButton = (Button) dialog.findViewById(R.id.dialog_ok);
		Button cButton = (Button) dialog.findViewById(R.id.dialog_cancer);
		pButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				for (int i = 0; i < historyBookList.size(); i++) {
					String name = (String)historyBookList.get(i).get("bookname");
					bookDao.delHistorySearch(name);
				}
				historyBookList.clear();
				book_Adapter.updateDataset();
				dialog.dismiss();
			}
		});
	
		cButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

				dialog.dismiss();
			}
		});


	}

	private class HistoryBookAdapter extends BaseAdapter {
		Context context;
		HolderView holder;

		public HistoryBookAdapter(Context context) {
			this.context = context;
		}

		public int getCount() {
			return historyBookList.size();
		}

		public Object getItem(int position) {
			return historyBookList.get(position).get("bookname");
		}

		public long getItemId(int position) {
			return position;
		}

		public void updateDataset() {
			this.notifyDataSetChanged();
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = View.inflate(context, R.layout.searchhistoryitem,
						null);
				holder = new HolderView();
				holder.title = (TextView) convertView
						.findViewById(R.id.searchhistory_bookname);
				holder.clear = (ImageView) convertView
						.findViewById(R.id.searchhistory_clear);
				convertView.setTag(holder);

				holder.clear.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						String name = (String)(historyBookList.get(position).get("bookname"));
						historyBookList.remove(position);
						bookDao.delHistorySearch(name);
						book_Adapter = new HistoryBookAdapter(
								SearchHistory.this);
						historyList.setAdapter(book_Adapter);
					}
				});
			} else {
				holder = (HolderView) convertView.getTag();
			}

			if (null != historyBookList.get(position)) {
				holder.title.setText(historyBookList.get(position)
						.get("bookname").toString().trim());
			}
			return convertView;
		}

		private class HolderView {
			TextView title;
			ImageView clear;
		}
	}
}
