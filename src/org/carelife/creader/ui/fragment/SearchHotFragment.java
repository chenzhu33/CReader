package org.carelife.creader.ui.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.carelife.creader.R;
import org.carelife.creader.dao.RankData;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.db.BookDao;

import org.carelife.creader.ui.activity.SearchResultList;
import org.carelife.creader.util.ToastUtil;
import org.carelife.creader.util.XmlUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SearchHotFragment extends Fragment {

	private ListView hotList = null;
	private List<String> hotBookList;
	private BookAdapter book_Adapter;
	private SharedPreferences sp;
	private Editor edit;
	private ToastUtil toast;
	private BookDao bookDao;
	private LinearLayout progressbar;

	public static SearchHotFragment newInstance() {
		SearchHotFragment f = new SearchHotFragment();
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

		hotBookList = bookDao.getHotwords();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.searchhot, container, false);
		progressbar = (LinearLayout) v.findViewById(R.id.searchhot_progressbar);
		if (hotBookList == null) {
			hotBookList = new ArrayList<String>();
			new GetDataTask().execute();
		} else {
			progressbar.setVisibility(View.GONE);
		}
		book_Adapter = new BookAdapter(getActivity(), hotBookList);

		hotList = (ListView) v.findViewById(R.id.searchhot_list);
		hotList.setAdapter(book_Adapter);
		hotList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> l, View v, int position,
					long id) {
				if (position >= hotBookList.size()) {
					toast.setText("亲,轻点点，别急啊");
					return;
				}
				String query_word = hotBookList.get(position).toString().trim();
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

		return v;
	}

	private class GetDataTask extends
			AsyncTask<Void, Void, HashMap<String, Object>> {

		@Override
		protected HashMap<String, Object> doInBackground(Void... params) {
			HashMap<String, Object> tmp_data = null;
			HashMap<String, Object> tmp_data2 = null;
			try {
				tmp_data = XmlUtil.getXML(UrlHelper.rankurl,
						UrlHelper.goto_data_search, "", 1);
				int count = 0;
				if (tmp_data != null) {
					count = Integer.parseInt((String) tmp_data.get("count"));
				}

				tmp_data2 = XmlUtil.getXML(UrlHelper.rankurl,
						UrlHelper.goto_data_search, "&length=" + count, 1);

			} catch (IOException e) {
				e.printStackTrace();
			}
			return tmp_data2;
		}

		@Override
		protected void onPostExecute(HashMap<String, Object> data) {
			if (data == null) {
				progressbar.setVisibility(View.GONE);
				toast.setText("亲，您的网络不给力啊，稍后再试吧...");
			} else {
				@SuppressWarnings("unchecked")
				List<RankData> rankl = (List<RankData>) data.get("rankitem");
				for (int i = 0; i < rankl.size(); i++) {
					hotBookList.add(rankl.get(i).getbookname());
				}
				bookDao.insertHotword(hotBookList);
				progressbar.setVisibility(View.GONE);
			}
			super.onPostExecute(data);
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
