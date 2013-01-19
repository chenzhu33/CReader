package org.carelife.creader.ui.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.carelife.creader.R;
import org.carelife.creader.db.BookDao;
import org.carelife.creader.ui.activity.SearchResultList;
import org.carelife.creader.ui.component.IntroduceDialog;
import org.carelife.creader.util.ToastUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchHistoryFragment extends Fragment {
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

	public static SearchHistoryFragment newInstance() {
		SearchHistoryFragment f = new SearchHistoryFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = getActivity().getSharedPreferences("sogounovel",
				Context.MODE_PRIVATE);
		edit = sp.edit();
		toast = ToastUtil.getInstance(getActivity());
		bookDao = BookDao.getInstance(getActivity());

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.searchhistory, container, false);
		historyList = (ListView) v.findViewById(R.id.searchhistory_list);
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
				Intent intent = new Intent(getActivity(),
						SearchResultList.class);
				edit.putString("querystring", query_word);
				edit.commit();
				startActivity(intent);
			}
		});
		llayout = (LinearLayout) v.findViewById(R.id.searchhistory_clearall);
		llayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog();
			}
		});
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		historyBookList = bookDao.getHistory();
		if (historyBookList == null) {
			historyBookList = new ArrayList<Map<String, Object>>();
		}
		book_Adapter = new HistoryBookAdapter(getActivity());
		historyList.setAdapter(book_Adapter);
	}

	protected void dialog() {
		dialog = new IntroduceDialog(getActivity(),
				R.layout.dialog_clear_history, R.style.Theme_dialog);

		dialog.show();

		Button pButton = (Button) dialog.findViewById(R.id.dialog_ok);
		Button cButton = (Button) dialog.findViewById(R.id.dialog_cancer);
		pButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				for (int i = 0; i < historyBookList.size(); i++) {
					String name = (String) historyBookList.get(i).get(
							"bookname");
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
						String name = (String) (historyBookList.get(position)
								.get("bookname"));
						historyBookList.remove(position);
						bookDao.delHistorySearch(name);
						book_Adapter = new HistoryBookAdapter(getActivity());
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
