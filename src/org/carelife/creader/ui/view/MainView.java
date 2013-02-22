package org.carelife.creader.ui.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.carelife.creader.bean.BookBasicBean;
import org.carelife.creader.dao.RankData;
import org.carelife.creader.dao.SearchData;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.db.BookDao;
import org.carelife.creader.ui.adapter.CateRankListAdapter;
import org.carelife.creader.util.AsynImageLoaderUtil;
import org.carelife.creader.util.FileUtil;
import org.carelife.creader.util.ToastUtil;
import org.carelife.creader.util.UpdateUtil;
import org.carelife.creader.util.XmlUtil;
import org.carelife.creader.ui.activity.*;

import org.carelife.creader.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainView implements OnScrollListener {
	private View mainView;
	private Context context;

	ListView rank_cate_list = null;
	private CateRankListAdapter list_Adapter;
	View listaddview;
	int mCount = 1;// 列表第一页
	int MAX_COUNT = 1;// 列表最大页
	FileUtil fm;
	private SharedPreferences sp;
	private Editor edit;

	int start = 0;
	int list_once = 10;
	HashMap<String, Object> xml_data;
	List<RankData> data_list = new ArrayList<RankData>();
	String catestring;
	int max_num;
	ImageView sidebar;
	ImageView search;
	TextView cateTitle;
	private ProgressDialog dialog;
	LinearLayout progressbar;
	SearchData book_searchresult, temp_result;
	String book_name, author_name, temp_tc_url;
	BookBasicBean book;
	BookDao bookdao;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				progressbar.setVisibility(View.GONE);
				ToastUtil.getInstance(context).setText("亲，您的网络不给力啊，稍后再试吧...");
				break;
			case 0: // 初次加载
				if (null == xml_data) {
					ToastUtil.getInstance(context).setText(
							"亲，您的网络不给力啊，检查下网络设置吧！");
					progressbar.setVisibility(View.GONE);
					return;
				}
				data_list = (List<RankData>) xml_data.get("rankitem");
				max_num = Integer.parseInt((String) xml_data.get("count"));

				MAX_COUNT = (int) Math.ceil((double) max_num
						/ (double) list_once);

				progressbar.setVisibility(View.GONE);
				rank_cate_list.setVisibility(View.VISIBLE);
				LayoutInflater lay = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				listaddview = lay.inflate(R.layout.booklistmore, null);
				rank_cate_list.addFooterView(listaddview);

				list_Adapter = new CateRankListAdapter(context, data_list,
						handler, dialog);
				rank_cate_list.setAdapter(list_Adapter);
				rank_cate_list.setOnScrollListener(MainView.this);
				rank_cate_list
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								if (arg2 >= data_list.size()) {
									ToastUtil.getInstance(context).setText(
											"亲底下没了啊");
									return;
								}
								book_name = data_list.get(arg2).getbookname();
								dialog.show();
								new Thread() {
									public void run() {
										book_searchresult = XmlUtil
												.getSearchOneXML(book_name);
										Message message = handler
												.obtainMessage(2,
														book_searchresult);
										handler.sendMessage(message);
									}
								}.start();

							}
						});
				if (MAX_COUNT == 1) {
					invisibleFooter();
				}
				break;
			case 1: // 续加
				if (null == xml_data) {
					ToastUtil.getInstance(context).setText("没有数据啦");
					invisibleFooter();
					return;
				}
				List<RankData> temp_list = (List<RankData>) xml_data
						.get("rankitem");

				for (int j = 0; j < temp_list.size(); j++) {
					data_list.add(temp_list.get(j));
				}

				mCount++;
				list_Adapter.notifyDataSetChanged();
				rank_cate_list.invalidateViews();
				// list_Adapter = new CateRankListAdapter(context, data_list,
				// handler);
				// rank_cate_list.setAdapter(list_Adapter);
				break;

			case 2: // 处理点击事件
				if (msg.obj != null) {
					temp_result = (SearchData) msg.obj;
					temp_tc_url = temp_result.geturl();
					try {
						temp_tc_url = URLEncoder.encode(temp_tc_url, "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (temp_result.getloc() != 1) {

						// 非本地存储书籍入库
						book_name = temp_result.getbookname();
						author_name = temp_result.getauthor_name();

						new Thread() {
							public void run() {
								try {
									SaveBitmap(AsynImageLoaderUtil
											.loadImageFromNet_throw(temp_result
													.getpicurl()));
									book = new BookBasicBean(book_name,
											author_name, null);
									book.setIs_loc(temp_result.getloc());
									book.setChapter_md5(UrlHelper.tc_url
											+ temp_tc_url);
									book.setPic_path(FileUtil.new_dir
											+ FileUtil.cheak_string(book_name)
											+ "_"
											+ FileUtil
													.cheak_string(author_name)
											+ "/" + UrlHelper.cover_string);
									bookdao.add_book(book);
									String temp_max_chapter = UpdateUtil
											.cheak_maxchaptercode(context,
													book_name);
									book.setMax_md5(temp_max_chapter);
								} catch (IOException e) {
									book = new BookBasicBean(book_name,
											author_name, null);
									book.setIs_loc(temp_result.getloc());
									book.setChapter_md5(UrlHelper.tc_url
											+ temp_tc_url);
									bookdao.add_book(book);
									String temp_max_chapter = UpdateUtil
											.cheak_maxchaptercode(context,
													book_name);
									book.setMax_md5(temp_max_chapter);
									e.printStackTrace();
								} finally {
									bookdao.insert_maxmd5(book);
								}
							}
						}.start();
						Intent intent = new Intent(context,
								WebViewActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("url", UrlHelper.tc_url + temp_tc_url);
						dialog.dismiss();
						context.startActivity(intent);
					} else {
						Intent intent = new Intent(context, BookDetail.class);
						intent.putExtra("SearchData", temp_result);
						dialog.dismiss();
						context.startActivity(intent);
					}
				} else {
					dialog.dismiss();
					ToastUtil.getInstance(context).setText(
							"亲，您的网络不给力啊，稍后再试吧...");
				}
				break;
			}
			// 处理UI
		}
	};

	public MainView() {
	}

	public MainView(Context context, MyMoveView myMoveView) {
		this.context = context;
		mainView = LayoutInflater.from(context).inflate(R.layout.rankcatelist,
				null);
		initView();
	}

	public void initView() {

		sp = context.getSharedPreferences("sogounovel", Context.MODE_PRIVATE);
		edit = sp.edit();
		fm = new FileUtil();
		bookdao = BookDao.getInstance(context);
		dialog = new ProgressDialog(context);
		dialog.setTitle("加载中");
		dialog.setMessage("数据加载中...");

		progressbar = (LinearLayout) this.mainView
				.findViewById(R.id.rankcatelist_progressbar);
		progressbar.setVisibility(View.VISIBLE);

		catestring = sp.getString("caterankstring", "xuanhuan");
		rank_cate_list = (ListView) this.mainView
				.findViewById(R.id.cate_rank_list);

		new Thread() {
			public void run() {
				try {
					xml_data = XmlUtil.getXML(catestring, start, list_once);
					handler.sendEmptyMessage(0);
				} catch (IOException e) {
					handler.sendEmptyMessage(-1);
					e.printStackTrace();
				}
			}
		}.start();

		indicator();
	}

	private void indicator() {
		RelativeLayout rl = (RelativeLayout) this.mainView
				.findViewById(R.id.rankcatelist_rl);
		if (0 == sp.getInt("hasIndicator3", 0)) {
			edit.putInt("hasIndicator3", 1);
			edit.commit();
			final ImageView indicator3 = new ImageView(context);
			indicator3.setBackgroundResource(R.drawable.indicator_3);
			indicator3.setVisibility(View.VISIBLE);
			indicator3.setScaleType(ScaleType.FIT_XY);
			indicator3.setAdjustViewBounds(true);
			rl.addView(indicator3, new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			indicator3.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					indicator3.setVisibility(View.GONE);
					indicator3.setClickable(false);
				}
			});
		}
	}

	public View getView() {
		return mainView;
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
				start += list_once;

				new Thread() {
					public void run() {
						try {
							xml_data = XmlUtil.getXML(catestring, start,
									list_once);
							handler.sendEmptyMessage(1);

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}.start();

			};
			// }.execute();
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

	private void SaveBitmap(Bitmap bmp) {
		// Bitmap bitmap = Bitmap.createBitmap(800, 600, Config.ARGB_8888);
		// Canvas canvas = new Canvas(bitmap);
		// //加载背景图片
		// Bitmap bmps = BitmapFactory.decodeResource(getResources(),
		// R.drawable.playerbackground);
		// canvas.drawBitmap(bmps, 0, 0, null);
		// //加载要保存的画面
		// canvas.drawBitmap(bmp, 10, 100, null);
		// //保存全部图层
		// canvas.save(Canvas.ALL_SAVE_FLAG);
		// canvas.restore();
		// 存储路径
		File file = new File(FileUtil.new_dir
				+ FileUtil.cheak_string(book_name) + "_"
				+ FileUtil.cheak_string(author_name));
		if (!file.exists()) {
			file.mkdirs();
		}
		File file_temp = new File(file.getPath() + "/" + UrlHelper.cover_string);
		if (!file_temp.exists()) {
			try {
				file_temp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(
					file_temp.getPath());
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
			fileOutputStream.close();
			System.out.println("saveBmp is here:" + file.getPath() + "/"
					+ UrlHelper.cover_string);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
