package org.carelife.creader.ui.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.carelife.creader.bean.BookBasicBean;
import org.carelife.creader.bean.ChapterBasicBean;
import org.carelife.creader.dao.RankData;
import org.carelife.creader.dao.SearchData;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.db.BookDao;
import org.carelife.creader.ui.adapter.DetailGridAdapter;
import org.carelife.creader.ui.component.MyDialogBuilder;
import org.carelife.creader.util.AsynImageLoaderUtil;
import org.carelife.creader.util.DownloadUtil;
import org.carelife.creader.util.FileUtil;
import org.carelife.creader.util.ToastUtil;
import org.carelife.creader.util.UpdateUtil;
import org.carelife.creader.util.XmlUtil;

import org.carelife.creader.R;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BookDetail extends Activity {

	private final String TAG = "BookDetal";

	private TextView title;
	private TextView title2;
	private TextView author;
	private TextView type;
	private TextView status;
	private TextView description;

	private ImageView cover;

	private Button reader;
	private ImageView catalogue;
	private ImageView span;

	private boolean isSpan = true, CancelFlag = false;
	private String desc, book_name, author_name, search_book_name, temp_type,
			pic_url;
	private BookDao bookdao; 
	private BookBasicBean book;
	private DownloadUtil DU;
	private SharedPreferences sp;
	private Editor edit;
	private SearchData sData, temp_result;

	private List<Map<String, String>> list_data;
	private GridView lovers;
	private Bitmap bitmap;
	private LinearLayout progressbar;
	private ProgressDialog dialog;
	private List<ChapterBasicBean> temp_list = new ArrayList<ChapterBasicBean>();
	private String temp_tc_url;
	private String book_name_web_view;
	private String author_name_web_view;
	private int b1, b2, b3;

	HashMap<String, String> book_cates = new HashMap<String, String>();
	List<RankData> rlist;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -2:
				progressbar.setVisibility(View.GONE);
				lovers.setVisibility(View.VISIBLE);
				ToastUtil.getInstance(BookDetail.this).setText("亲，您的网络不给力啊，稍后再试吧...");
				break;
			case -1:
				dialog.dismiss();
				ToastUtil.getInstance(BookDetail.this).setText("亲，您的网络不给力啊，稍后再试吧...");
				break;
			case 0:
				if (CancelFlag) {
					CancelFlag = false;
					break;
				}
				edit.putString("book_name_chapter", book_name);
				edit.putString("author_name_chapter", author_name);
				edit.putBoolean("force_fromweb_chapter", true);
				edit.putBoolean("detail_goto_chapter", true);
				edit.commit();
				Intent intent = new Intent(BookDetail.this,
						SogouNovelActivity.class);
				intent.putExtra("book_info", book);
				dialog.dismiss();
				BookDetail.this.startActivity(intent);
				break;
			case 1:
				HashMap<String, Object> datas = null;
				if (msg.obj != null) {
					datas = (HashMap<String, Object>) msg.obj;
				} else {
					handler.sendEmptyMessage(-2);
					break;
				}
				rlist = (List<RankData>) datas.get("rankitem");
				int size = rlist.size();
				list_data = new ArrayList<Map<String, String>>();
				Random random = new Random();
				b1 = random.nextInt(size);
				b2 = random.nextInt(size);
				b3 = random.nextInt(size);
				for (int i = 0; i < 3; i++) {
					int j = 0;
					switch (i) {
					case 0:
						j = b1;
						break;
					case 1:
						j = b2;
						break;
					case 2:
						j = b3;
						break;

					default:
						break;
					}
					HashMap<String, String> data = new HashMap<String, String>();
					data.put("title", rlist.get(j).getbookname());
					data.put("pic", rlist.get(j).getpicurl());
					list_data.add(data);
				}
				lovers.setAdapter(new DetailGridAdapter(BookDetail.this,
						list_data));

				lovers.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> arg0, View arg1,
							int position, long arg3) {
						if (position >= list_data.size()) {
							ToastUtil.getInstance(BookDetail.this).setText(
									"亲底下没了啊");
							return;
						}
						int j = 0;
						switch (position) {
						case 0:
							j = b1;
							break;
						case 1:
							j = b2;
							break;
						case 2:
							j = b3;
							break;

						default:
							break;
						}

						search_book_name = rlist.get(j).getbookname();
						if(!dialog.isShowing()){
							dialog.show();
						}
						new Thread() {
							public void run() {
								SearchData temp_sd = XmlUtil
										.getSearchOneXML(search_book_name);
								Message message = handler.obtainMessage(2,
										temp_sd);
								handler.sendMessage(message);
							}
						}.start();
						// list_data.get(arg2);
					}
				});
				progressbar.setVisibility(View.GONE);
				lovers.setVisibility(View.VISIBLE);
				break;
			case 2: // 处理点击事件
				if (msg.obj != null) {
					temp_result = (SearchData) msg.obj;
					
					if (temp_result.getloc() != 1) {
						temp_tc_url = temp_result.geturl();
						try {
							temp_tc_url = URLEncoder.encode(temp_tc_url,
									"utf-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						book_name_web_view = temp_result.getbookname();
						author_name_web_view = temp_result.getauthor_name();

						new Thread() {
							public void run() {
								try {
									SaveBitmap(AsynImageLoaderUtil
											.loadImageFromNet_throw(temp_result
													.getpicurl()),book_name_web_view,author_name_web_view);
									book = new BookBasicBean(book_name_web_view,
											author_name_web_view, null);
									book.setIs_loc(temp_result.getloc());
									book.setChapter_md5(UrlHelper.tc_url
											+ temp_tc_url);
									book.setPic_path(FileUtil.new_dir
											+ FileUtil.cheak_string(book_name_web_view) + "_"
											+ FileUtil.cheak_string(author_name_web_view)
											+ "/" + UrlHelper.cover_string);
									bookdao.add_book(book);
									String temp_max_chapter = UpdateUtil.cheak_maxchaptercode(BookDetail.this, book_name_web_view);
									book.setMax_md5(temp_max_chapter);
								} catch (IOException e) {
									book = new BookBasicBean(book_name_web_view,
											author_name_web_view, null);
									book.setIs_loc(temp_result.getloc());
									book.setChapter_md5(UrlHelper.tc_url
											+ temp_tc_url);
									bookdao.add_book(book);
									String temp_max_chapter = UpdateUtil.cheak_maxchaptercode(BookDetail.this, book_name_web_view);
									book.setMax_md5(temp_max_chapter);
									e.printStackTrace();
								} finally {
									bookdao.insert_maxmd5(book);
								}
							}
						}.start();

						edit.putString("webview_book_name", book_name_web_view);
						edit.putString("webview_author_name",
								author_name_web_view);
						edit.putString("webview_url", UrlHelper.tc_url
								+ temp_tc_url);
						edit.commit();
						if(dialog.isShowing()){
							dialog.dismiss();
						}
						Intent intent2 = new Intent(BookDetail.this,
								TcBookActivity.class);
						intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						BookDetail.this.startActivity(intent2);
					} else {
						initData(temp_result);
					}
				}else{
					if(dialog.isShowing()){
						dialog.dismiss();
					}
					ToastUtil.getInstance(BookDetail.this).setText("亲，您的网络不给力啊，稍后再试吧...");
				}
				break;
			case 3:
				if (bitmap != null) {
					cover.setImageBitmap(bitmap);
				} else {
					cover.setImageResource(R.drawable.book_default);
				}
				break;

			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.bookdetail);
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		for (int i = 0; i < UrlHelper.book_cate.length; i++) {
			book_cates.put(UrlHelper.book_cate[i], UrlHelper.goto_data[i]);
		}

		sData = (SearchData) this.getIntent().getParcelableExtra("SearchData");
		if (sData == null) {
			Log.e(TAG, "search data is null!!!");
			this.finish();
		}

		DU = new DownloadUtil();
		bookdao = BookDao.getInstance(this);
		title = (TextView) findViewById(R.id.detail_book_title);
		title2 = (TextView) findViewById(R.id.detail_book_title2);
		author = (TextView) findViewById(R.id.detail_book_author);
		type = (TextView) findViewById(R.id.detail_book_type);
		status = (TextView) findViewById(R.id.detail_book_status);
		description = (TextView) findViewById(R.id.detail_book_description2);
		cover = (ImageView) findViewById(R.id.detail_book_pic);
		reader = (Button) findViewById(R.id.detail_start_read);
		catalogue = (ImageView) findViewById(R.id.detail_catalogue);
		span = (ImageView) findViewById(R.id.detail_span);
		lovers = (GridView) findViewById(R.id.detail_other_gridview);
		progressbar = (LinearLayout) findViewById(R.id.bookdetail_progressbar);

		dialog = new ProgressDialog(BookDetail.this);
		dialog.setTitle("获取数据");  
		dialog.setMessage("数据加载中，请稍后。。。");  
		dialog.setCancelable(true);
		
//		dialog = MyDialogBuilder.waitingDialog(BookDetail.this, "获取数据",
//				"数据加载中，请稍后。。。");
		dialog.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					CancelFlag = true;
					return false;
				}

				return false;
			}
		});
		
		
		initData(sData);
		
		description.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isSpan) {
					span.setBackgroundResource(R.drawable.span);

					// description.setMaxLines(3);
					// description.setText(desc);
					if (desc.length() <= 70) {
						description.setText(desc);
					} else {
						description.setText(desc.substring(0, 67) + "...");
					}
					isSpan = true;
				} else {
					span.setBackgroundResource(R.drawable.shrink);
					// description.setMaxLines(10);
					description.setText(desc);
					isSpan = false;
				}
			}
		});

		span.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!isSpan) {
					span.setBackgroundResource(R.drawable.span);

					// description.setMaxLines(3);
					// description.setText(desc);
					if (desc.length() <= 70) {
						description.setText(desc);
					} else {
						description.setText(desc.substring(0, 67) + "...");
					}
					isSpan = true;
				} else {
					span.setBackgroundResource(R.drawable.shrink);
					// description.setMaxLines(10);
					description.setText(desc);
					isSpan = false;
				}
			}
		});

		reader.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// Intent intent = new Intent(BookDetail.this, );
				dialog.show();
				SaveBitmap(bitmap,book_name,author_name);
				new Thread() {
					public void run() {
						try {
							
							BookBasicBean book_temp = bookdao.getBook(book_name, author_name);
							
							if(book_temp != null){
								book = book_temp;
								bookdao.add_book(book);
								handler.sendEmptyMessage(0);
								return;
							}
							
							temp_list = DU.getChapterList_For_Beginread(
									book_name, author_name, BookDetail.this);
							if (temp_list != null && temp_list.size() != 0) {
								book.setChapter_index(1);
								book.setChapter_md5(temp_list.get(0)
										.getChapter_md5());
								book.setMax_md5(temp_list.get(temp_list.size() - 1 ).getChapter_md5());
								bookdao.add_book(book);
								handler.sendEmptyMessage(0);
							}

						} catch (Exception e) {
							handler.sendEmptyMessage(-1);
							e.printStackTrace();
						}

					}
				}.start();
			}
		});

		catalogue.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				edit.putString("book_name_chapter", book_name);
				edit.putString("author_name_chapter", author_name);
				edit.putBoolean("force_fromweb_chapter", true);
				edit.putBoolean("detail_goto_chapter", true);
				edit.commit();
				SaveBitmap(bitmap,book_name,author_name);
				Intent intent = new Intent(BookDetail.this, ChapterList.class);
				BookDetail.this.startActivity(intent);
			}
		});

	}

	private void initData(SearchData temp_search) {
		progressbar.setVisibility(View.VISIBLE);
		lovers.setVisibility(View.INVISIBLE);
		bitmap = null;
		book_name = temp_search.getbookname();
		author_name = temp_search.getauthor_name();

		title.setText(book_name);
		title2.setText(book_name);
		author.setText("作者：" + author_name);
		type.setText("类型：" + temp_search.gettype());

		status.setText("状态：[连载]");
		if (temp_search.getstatus() == 1) {
			status.setText("状态：[全本]");
		}

		book = new BookBasicBean();
		book.setBook_name(book_name);
		book.setAuthor_name(author_name);
		book.setIs_loc(temp_search.getloc());
		book.setBegin_buf(0);

		book.setPic_path(FileUtil.new_dir + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name)
				+ "/" + UrlHelper.cover_string);

		desc = temp_search.getdesc();

		if (desc.length() <= 70) {
			description.setText(desc);
			span.setVisibility(View.GONE);
		} else {
			description.setText(desc.substring(0, 67) + "...");
		}
		pic_url = temp_search.getpicurl();
		temp_type = temp_search.gettype();
		new Thread() {
			public void run() {
				bitmap = AsynImageLoaderUtil.loadImageFromNet(pic_url);
				handler.sendEmptyMessage(3);
				HashMap<String, Object> datas;
				try {
					datas = XmlUtil.getXML(UrlHelper.rankurl,
							book_cates.get(temp_type), "&length=" + 20, 1);
					Message message = handler.obtainMessage(1, datas);
					handler.sendMessage(message);
				} catch (IOException e) {
					handler.sendEmptyMessage(-2);
					e.printStackTrace();
				}

			}
		}.start();
		if(dialog.isShowing()){
			dialog.dismiss();
		}
	}

	public void SaveBitmap(Bitmap bmp , String book_name_bm,String author_name_bm) {
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
		File file = new File(FileUtil.new_dir + FileUtil.cheak_string(book_name_bm) + "_" + FileUtil.cheak_string(author_name_bm));
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
			System.out.println("saveBmp is here:" + file.getPath()
					+ "/" + UrlHelper.cover_string);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
