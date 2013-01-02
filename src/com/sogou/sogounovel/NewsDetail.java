package com.sogou.sogounovel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sogou.R;
import com.sogou.adapter.NewsListAdapter;
import com.sogou.component.MyDialogBuilder;
import com.sogou.constdata.NewsUrl;
import com.sogou.util.NetworkUtil;
import com.sogou.util.ToastUtil;
import com.sogou.util.XmlUtil;
import com.sogou.xmldata.RssData;

public class NewsDetail extends Activity implements OnScrollListener {
	private ListView lView;
	private List<RssData> xml_data;
	private NewsListAdapter lAdapter;
	private TextView badNet;
	private LinearLayout progressbar;
	private View listaddview;
	private LinearLayout ll;
	private ToastUtil toast;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				progressbar.setVisibility(View.GONE);
				ll.setVisibility(View.GONE);
				lView.setVisibility(View.INVISIBLE);
				badNet.setVisibility(View.VISIBLE);
				break;
			case 0:
				badNet.setVisibility(View.INVISIBLE);
				lAdapter = new NewsListAdapter(NewsDetail.this, xml_data);
				lView.setAdapter(lAdapter);
				progressbar.setVisibility(View.GONE);
				lView.setVisibility(View.VISIBLE);
				break;
			case 1:
				badNet.setVisibility(View.INVISIBLE);
				listaddview.setVisibility(View.GONE);
				ll.setVisibility(View.GONE);
				lAdapter = new NewsListAdapter(NewsDetail.this, xml_data);
				lView.setAdapter(lAdapter);
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newslist);

		lView = (ListView) findViewById(R.id.news_list);
		progressbar = (LinearLayout) findViewById(R.id.news_progressbar);
		toast = ToastUtil.getInstance(NewsDetail.this);
		LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		listaddview = lay.inflate(R.layout.newsheader, null);
		ll = (LinearLayout) listaddview.findViewById(R.id.newscontent);
		lView.addHeaderView(listaddview);
		listaddview.setVisibility(View.GONE);
		ll.setVisibility(View.GONE);
		lView.setOnScrollListener(NewsDetail.this);
		badNet = (TextView)findViewById(R.id.news_badnetwork);
		
		badNet.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(NetworkUtil.checkWifiAndGPRS(NewsDetail.this)){
					progressbar.setVisibility(View.VISIBLE);
					badNet.setVisibility(View.INVISIBLE);
					new Thread() {
						public void run() {
							int index = NewsDetail.this.getIntent().getIntExtra("tabindex",
									0);
							xml_data = new ArrayList<RssData>();
							try {
								List<RssData> tmp_data_1 = XmlUtil
										.getNewsXML(NewsUrl.newsurl[index][0]);
								List<RssData> tmp_data_2 = XmlUtil
										.getNewsXML(NewsUrl.newsurl[index][1]);
								List<RssData> tmp_data_3 = XmlUtil
										.getNewsXML(NewsUrl.newsurl[index][2]);
								if (tmp_data_1 != null && tmp_data_1.size() != 0) {
									for (RssData rd : tmp_data_1) {
										xml_data.add(rd);
									}
								}
								if (tmp_data_2 != null && tmp_data_2.size() != 0) {
									for (RssData rd : tmp_data_2) {
										xml_data.add(rd);
									}
								}
								if (tmp_data_3 != null && tmp_data_3.size() != 0) {
									for (RssData rd : tmp_data_3) {
										xml_data.add(rd);
									}
								}
								handler.sendEmptyMessage(0);
							} catch (IOException e) {
								if(xml_data == null || xml_data.size() == 0){
									handler.sendEmptyMessage(-1);
								}else{
									handler.sendEmptyMessage(1);
								}
								e.printStackTrace();
							}
						}
					}.start();
				}else{
					toast.setText("亲，您的网络不给力啊，稍后再试吧...");
				}
				
			}
		});
		
		new Thread() {
			public void run() {
				int index = NewsDetail.this.getIntent().getIntExtra("tabindex",
						0);
				xml_data = new ArrayList<RssData>();
				try {
					List<RssData> tmp_data_1 = XmlUtil
							.getNewsXML(NewsUrl.newsurl[index][0]);
					List<RssData> tmp_data_2 = XmlUtil
							.getNewsXML(NewsUrl.newsurl[index][1]);
					List<RssData> tmp_data_3 = XmlUtil
							.getNewsXML(NewsUrl.newsurl[index][2]);
					if (tmp_data_1 != null && tmp_data_1.size() != 0) {
						for (RssData rd : tmp_data_1) {
							xml_data.add(rd);
						}
					}
					if (tmp_data_2 != null && tmp_data_2.size() != 0) {
						for (RssData rd : tmp_data_2) {
							xml_data.add(rd);
						}
					}
					if (tmp_data_3 != null && tmp_data_3.size() != 0) {
						for (RssData rd : tmp_data_3) {
							xml_data.add(rd);
						}
					}
					handler.sendEmptyMessage(0);
				} catch (IOException e) {
					if(xml_data == null || xml_data.size() == 0){
						handler.sendEmptyMessage(-1);
					}else{
						handler.sendEmptyMessage(1);
					}
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MyDialogBuilder.accessDialog(NewsDetail.this.getParent());
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {

			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		firstItem = firstVisibleItem;
	}

	private int firstItem;

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 当往上拉时更新数据，将data清空然后去重新加载
		if (firstItem == 0 && scrollState == SCROLL_STATE_IDLE) {
			ll.setVisibility(View.VISIBLE);
			listaddview.setVisibility(View.VISIBLE);
			if(NetworkUtil.checkWifiAndGPRS(NewsDetail.this)){
				updateData();
			}else{
				ll.setVisibility(View.GONE);
				listaddview.setVisibility(View.GONE);
				toast.setText("亲，您的网络不给力啊，稍后再试吧...");
			}
		}
	}

	private AsyncTask<Long, Void, Void> mTask;

	private void updateData() {

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
				new Thread() {
					public void run() {
						int index = NewsDetail.this.getIntent().getIntExtra(
								"tabindex", 0);
						try {
							xml_data = new ArrayList<RssData>();
							List<RssData> tmp_data_1 = XmlUtil
									.getNewsXML(NewsUrl.newsurl[index][0]);
							List<RssData> tmp_data_2 = XmlUtil
									.getNewsXML(NewsUrl.newsurl[index][1]);
							List<RssData> tmp_data_3 = XmlUtil
									.getNewsXML(NewsUrl.newsurl[index][2]);
							if (tmp_data_1 != null && tmp_data_1.size() != 0) {
								for (RssData rd : tmp_data_1) {
									xml_data.add(rd);
								}
							}
							if (tmp_data_2 != null && tmp_data_2.size() != 0) {
								for (RssData rd : tmp_data_2) {
									xml_data.add(rd);
								}
							}
							if (tmp_data_3 != null && tmp_data_3.size() != 0) {
								for (RssData rd : tmp_data_3) {
									xml_data.add(rd);
								}
							}
							handler.sendEmptyMessage(1);

						} catch (IOException e) {
							if(xml_data == null || xml_data.size() == 0){
								handler.sendEmptyMessage(-1);
							}else{
								handler.sendEmptyMessage(1);
							}
							
							e.printStackTrace();
						}
					}
				}.start();

			};
//		}.execute();
		}.execute(Math.abs(new Random(System.currentTimeMillis()).nextLong() % 2000));

	}

}