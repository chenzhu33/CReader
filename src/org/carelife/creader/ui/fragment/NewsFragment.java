package org.carelife.creader.ui.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.carelife.creader.R;
import org.carelife.creader.dao.RssData;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.support.pulltorefresh.PullToRefreshBase;
import org.carelife.creader.support.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import org.carelife.creader.support.pulltorefresh.PullToRefreshListView;
import org.carelife.creader.ui.activity.NewsWebActivity;
import org.carelife.creader.ui.adapter.NewsListAdapter;
import org.carelife.creader.util.NetworkUtil;
import org.carelife.creader.util.ToastUtil;
import org.carelife.creader.util.XmlUtil;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class NewsFragment extends Fragment {

	private int index;
	private List<RssData> adapterData;
	private NewsListAdapter lAdapter;
	private TextView badNet;
	private LinearLayout progressbar;
	private ToastUtil toast;
	private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;

	private Context context;
	
	public static NewsFragment newInstance(int index) {
		NewsFragment f = new NewsFragment();
		Bundle b = new Bundle();
		b.putInt("index", index);
		f.setArguments(b);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.index = getArguments().getInt("index");
		toast = ToastUtil.getInstance(getActivity());
		context = getActivity();
		View v = inflater.inflate(R.layout.newslist, container, false);

		mPullRefreshListView = (PullToRefreshListView) v
				.findViewById(R.id.news_list);

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getActivity()
								.getApplicationContext(), System
								.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// Do work to refresh the list here.
						adapterData.clear();
						new GetDataTask().execute(0);
						new GetDataTask().execute(1);
						new GetDataTask().execute(2);
					}
				});
		actualListView = mPullRefreshListView.getRefreshableView();
		getActivity().registerForContextMenu(actualListView);

		adapterData = new ArrayList<RssData>();
		lAdapter = new NewsListAdapter(getActivity(), adapterData);
		actualListView.setAdapter(lAdapter);

		actualListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if(null == adapterData.get(arg2).getLink()) {
					toast.setText("此条无记录，抱歉啊。。.");
					return;
				}
				if(NetworkUtil.checkWifiAndGPRS(context)){
					Intent intent = new Intent(context, NewsWebActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("url", adapterData.get(arg2).getLink());
					context.startActivity(intent);
				}else{
					toast.setText("亲，您的网络不给力啊，稍后再试吧...");
					return;
				}				
			}
		});
		
		progressbar = (LinearLayout) v.findViewById(R.id.news_progressbar);

		badNet = (TextView) v.findViewById(R.id.news_badnetwork);

		badNet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (NetworkUtil.checkWifiAndGPRS(getActivity())) {
					progressbar.setVisibility(View.VISIBLE);
					badNet.setVisibility(View.INVISIBLE);
					adapterData.clear();
					new GetDataTask().execute(0);
					new GetDataTask().execute(1);
					new GetDataTask().execute(2);
				} else {
					toast.setText("亲，您的网络不给力啊，稍后再试吧...");
				}

			}
		});
		adapterData.clear();
		new GetDataTask().execute(0);
		new GetDataTask().execute(1);
		new GetDataTask().execute(2);

		return v;
	}

	private class GetDataTask extends AsyncTask<Integer, Void, List<RssData>> {

		@Override
		protected List<RssData> doInBackground(Integer... params) {
			List<RssData> tmp = null;
			try {
				tmp = XmlUtil.getNewsXML(UrlHelper.newsurl[index][params[0]]);
				if (tmp != null && tmp.size() != 0) {
					return tmp;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<RssData> data) {
			if (data == null && adapterData == null) {
				progressbar.setVisibility(View.GONE);
				mPullRefreshListView.setVisibility(View.INVISIBLE);
				badNet.setVisibility(View.VISIBLE);
			} else if (data != null) {
				badNet.setVisibility(View.INVISIBLE);
				progressbar.setVisibility(View.GONE);
				mPullRefreshListView.setVisibility(View.VISIBLE);
				adapterData.addAll(data);
				lAdapter.notifyDataSetChanged();
			}

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			super.onPostExecute(data);
		}
	}

}
