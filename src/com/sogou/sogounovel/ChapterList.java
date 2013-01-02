package com.sogou.sogounovel;

import com.sogou.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sogou.constdata.ConstData;
import com.sogou.data.book_basic;
import com.sogou.data.chapter_basic;
import com.sogou.db.BookDao;
import com.sogou.util.DownloadUtil;
import com.sogou.util.FileUtil;
import com.sogou.util.NetworkUtil;
import com.sogou.util.ToastUtil;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;

public class ChapterList extends Activity {
	ListView book_list = null;
	List<Map<String, Object>> list_data = new ArrayList<Map<String, Object>>();
	List<HashMap<String, Object>> all_data = new ArrayList<HashMap<String, Object>>();

	List<chapter_basic> chapter_list = new ArrayList<chapter_basic>();

	private BookAdapter book_Adapter;
	View listaddview;
	private SharedPreferences sp;
	private Editor edit;
	String book_name;
	String author_name;
	boolean force_fromweb;
	Intent intent;
	ToastUtil toast;
	DownloadUtil DU;
	BookDao bd;
	ProgressDialog dialog;
	int user_force_close = 0;
	private int user_force_close_update = 0;
	book_basic book;
	int book_mark = 0;
	boolean from_flag;
	ProgressBar refresh_dialog;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) { 
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				if(dialog.isShowing()){
					dialog.dismiss();
				}
				ToastUtil.getInstance(ChapterList.this).setText(
						"亲，您的网络不给力啊，稍后再试吧...");
				break;
			case -2:
				// dialog2.dismiss();
				refresh_dialog.setVisibility(View.GONE);
				ToastUtil.getInstance(ChapterList.this).setText(
						"亲，您的网络不给力啊，稍后再试吧...");
				break;

			case 0:
				if (msg.obj != null) {
					chapter_list = (List<chapter_basic>) msg.obj;
				}

				if (chapter_list == null) {
					// 罪恶之城
					if(dialog.isShowing()){
						dialog.dismiss();
					}
					// dialog2.dismiss();
					refresh_dialog.setVisibility(View.GONE);
					book_list.setVisibility(View.VISIBLE);
					toast.setText("暂无数据,稍后再试吧...");
					break;
				}
				book = bd.getBook(book_name, author_name);
				if (book != null) {
					book_mark = book.getChapter_index() - 1;
				}

				book_Adapter = new BookAdapter(ChapterList.this, chapter_list);
				book_list.setAdapter(book_Adapter);

				// book_list.smoothScrollToPosition(book_mark);
				book_list.setSelection(book_mark);

				book_list.setVisibility(View.VISIBLE);
				if(dialog.isShowing()){
					dialog.dismiss();
				}
				// dialog2.dismiss();
				refresh_dialog.setVisibility(View.GONE);
				break;
			case 1:

				if (chapter_list == null) {
					// 罪恶之城
					if(dialog.isShowing()){
						dialog.dismiss();
					}
					book_list.setVisibility(View.VISIBLE);
					toast.setText("暂无数据,稍后再试吧...");
					break;
				}
				book = bd.getBook(book_name, author_name);
				if (book != null) {
					book_mark = book.getChapter_index() - 1;
				}

				book_Adapter = new BookAdapter(ChapterList.this, chapter_list);
				book_list.setAdapter(book_Adapter);

				// book_list.smoothScrollToPosition(book_mark);
				book_list.setSelection(book_mark);
				book_list.setVisibility(View.VISIBLE);

				// dialog2.show();
				refresh_dialog.setVisibility(View.VISIBLE);
				toast.setText("更新目录");

				// if(chapter_list == null){
				//
				// }else{
				// Message message = handler.obtainMessage(0, chapter_list);
				// handler.sendMessage(message);
				// }

				new Thread() {
					public void run() {
						HashMap<String, Object> temp_data;
						try {
							temp_data = (HashMap<String, Object>) DU
									.getChapterList(book_name, author_name,
											ChapterList.this, false);
							List<chapter_basic> chapter_list_temp = (List<chapter_basic>) temp_data
									.get("chapter_list");
							if (user_force_close_update == 0
									&& chapter_list_temp != null
									&& chapter_list_temp.size() != 0) {
								chapter_list = chapter_list_temp;
								Message message = handler.obtainMessage(0,
										chapter_list);
								handler.sendMessage(message);
							} else {
								user_force_close_update = 0;
								// dialog2.dismiss();
								refresh_dialog.setVisibility(View.GONE);
							}
						} catch (Exception e) {
							handler.sendEmptyMessage(-2);
							e.printStackTrace();
						}

					}
				}.start();

				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置activity无标题
		setContentView(R.layout.chapterlist);

		book_list = (ListView) findViewById(R.id.chapter_listview);

		// // 反射更改快速下拉条
		// try {
		// Field f = AbsListView.class.getDeclaredField("mFastScroller");
		// f.setAccessible(true);
		// Object o = f.get(book_list);
		// f = f.getType().getDeclaredField("mThumbDrawable");
		// f.setAccessible(true);
		// Drawable drawable = (Drawable) f.get(o);
		// drawable = getResources().getDrawable(R.drawable.scroller);
		// f.set(o, drawable);
		// // Toast.makeText(this, f.getType().getName(), 1000).show();
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }

		book_Adapter = new BookAdapter(ChapterList.this, null);
		book_list.setAdapter(book_Adapter);
		book_list.setVisibility(View.VISIBLE);

		toast = ToastUtil.getInstance(this);
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		dialog = new ProgressDialog(ChapterList.this);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setTitle("获取目录");
		dialog.setMessage("数据加载中，请稍后。。。");
		dialog.setIndeterminate(false);
		// dialog = MyDialogBuilder.waitingDialog(ChapterList.this, "获取目录",
		// "数据加载中，请稍后。。。");
		dialog.setOnKeyListener(new OnKeyListener() {

			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					user_force_close = 1;
					book_Adapter = new BookAdapter(ChapterList.this,
							chapter_list);
					book_list.setAdapter(book_Adapter);
					book_list.setVisibility(View.VISIBLE);
					toast.setText("已取消。。。");
					// chapter_list = bd.getChapter_list(book_name,
					// author_name);
					// Message message = handler.obtainMessage(0, chapter_list);
					// handler.sendMessage(message);
					return false;
					// return true;
				}

				return false;
			}
		});
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		refresh_dialog = (ProgressBar) findViewById(R.id.refreash_progress);
		refresh_dialog.setVisibility(View.GONE);
		// dialog2 = new ProgressDialog(ChapterList.this);
		// dialog2.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// dialog2.setTitle("检查目录更新");
		// dialog2.setMessage("更新目录中，请稍后。。。");
		// dialog2.setIndeterminate(false);
		// dialog2.setCancelable(true);
		// dialog2.setCanceledOnTouchOutside(true);
		// //
		// // dialog2 = MyDialogBuilder.waitingDialog(ChapterList.this,
		// "检查目录更新",
		// // "更新目录中，请稍后。。。");
		// dialog2.setOnKeyListener(new OnKeyListener() {
		//
		// public boolean onKey(DialogInterface dialog, int keyCode,
		// KeyEvent event) {
		// if (keyCode == KeyEvent.KEYCODE_BACK) {
		// user_force_close_update = 1;
		// return false;
		// }
		//
		// return false;
		// }
		// });
		// dialog2.setCanceledOnTouchOutside(false);
		// dialog2.setCancelable(true);
		LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		listaddview = lay.inflate(R.layout.chapter_force_update, null);
		book_list.addFooterView(listaddview);

		listaddview.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog.show();
				new Thread() {
					public void run() {
						HashMap<String, Object> temp_data;
						try {
							temp_data = (HashMap<String, Object>) DU
									.getChapterList(book_name, author_name,
											ChapterList.this, true);
							List<chapter_basic> chapter_list_temp = (List<chapter_basic>) temp_data
									.get("chapter_list");
							if (user_force_close == 0
									&& chapter_list_temp != null
									&& chapter_list_temp.size() != 0) {
								chapter_list = chapter_list_temp;
								Message message = handler.obtainMessage(0,
										chapter_list);
								handler.sendMessage(message);
							} else {
								user_force_close = 0;
								if(dialog.isShowing()){
									dialog.dismiss();
								}
							}
						} catch (Exception e) {
							handler.sendEmptyMessage(-1);
							e.printStackTrace();
						}

					}
				}.start();
			}
		});

		book_name = sp.getString("book_name_chapter", "武动乾坤");
		author_name = sp.getString("author_name_chapter", "天蚕土豆");
		force_fromweb = sp.getBoolean("force_fromweb_chapter", true);
		from_flag = sp.getBoolean("detail_goto_chapter", false);
		edit.putBoolean("detail_goto_chapter", false);
		edit.commit();
		DU = new DownloadUtil();
		bd = BookDao.getInstance(ChapterList.this);

		if (!from_flag) {
			if (sp.getBoolean("orientation", true)) {
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
		}

		if (force_fromweb) {

			if (NetworkUtil.checkWifiAndGPRS(this)) {
				dialog.show();

				new Thread() {
					public void run() {
						HashMap<String, Object> temp_data;
						try {
							temp_data = (HashMap<String, Object>) DU
									.getChapterList(book_name, author_name,
											ChapterList.this, false);
							chapter_list = (List<chapter_basic>) temp_data
									.get("chapter_list");

							if (user_force_close == 0) {
								Message message = handler.obtainMessage(0,
										chapter_list);
								handler.sendMessage(message);
							} else {
								user_force_close = 0;
							}
						} catch (Exception e) {
							handler.sendEmptyMessage(-1);
							e.printStackTrace();
						}

					}
				}.start();
			} else {
				dialog = new ProgressDialog(ChapterList.this);
				dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				dialog.setTitle("获取目录");
				dialog.setMessage("无网络连接，读取本地缓存");
				dialog.setIndeterminate(false);
				dialog.setCancelable(true);
				// dialog = MyDialogBuilder.waitingDialog(ChapterList.this,
				// "获取目录", "无网络连接，读取本地缓存");
				dialog.show();

				chapter_list = bd.getChapter_list(book_name, author_name);
				Message message = handler.obtainMessage(0, chapter_list);
				handler.sendMessage(message);
			}

		} else {

			chapter_list = bd.getChapter_list(book_name, author_name);

			Message message = handler.obtainMessage(1, chapter_list);
			handler.sendMessage(message);

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(dialog.isShowing()){
			dialog.dismiss();
		}
		ChapterList.this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (refresh_dialog.getVisibility() == View.VISIBLE) {
				user_force_close_update = 1;
				toast.setText("已取消更新目录", 2000);
				refresh_dialog.setVisibility(View.GONE);
				return true;
			}

		}

		return super.onKeyDown(keyCode, event);
	}

	public class BookAdapter extends BaseAdapter {
		List<chapter_basic> data_list;
		Context context;

		public BookAdapter(Context context, List<chapter_basic> results) {
			if (results == null) {
				this.data_list = new ArrayList<chapter_basic>();
			} else {
				this.data_list = results;
			}
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
				convertView = View.inflate(context, R.layout.chapterlistitem,
						null);
				holder = new HolderView();
				holder.layout = (RelativeLayout) convertView
						.findViewById(R.id.chapterlist_item_layout);
				holder.title = (TextView) convertView
						.findViewById(R.id.chapter_title);
				holder.mark = (ImageView) convertView
						.findViewById(R.id.chapter_mark);
				// holder.info =
				// (TextView)convertView.findViewById(R.id.book_info);
				convertView.setTag(holder);
			} else {
				holder = (HolderView) convertView.getTag();
			}


			if (book_mark == position) {
				holder.layout.setBackgroundResource(R.drawable.bookmark_bg);
				holder.mark.setVisibility(View.VISIBLE);
			} else {
				holder.layout.setBackgroundResource(ConstData.backgroundColor[position % 2]);
				holder.mark.setVisibility(View.GONE);
			}

			holder.title.setText((position + 1)
					+ ".  "
					+ data_list.get(position).getChapter_name().toString()
							.trim());

			holder.layout.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					TextView t = (TextView) v.findViewById(R.id.chapter_title);
					ImageView mark = (ImageView) v
							.findViewById(R.id.chapter_mark);

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						v.setBackgroundResource(R.drawable.item_selected);
						t.setTextColor(Color.WHITE);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						t.setTextColor(getResources().getColor(R.color.textcolor63));
						if (book_mark == position) {
							v.setBackgroundResource(R.drawable.bookmark_bg);
							mark.setVisibility(View.VISIBLE);
						} else {
							v.setBackgroundResource(ConstData.backgroundColor[position % 2]);
							mark.setVisibility(View.GONE);
						}

						if (position >= chapter_list.size()) {
							toast.setText("章节不存在");
							return true;
						}

						if (from_flag) {
							intent = new Intent(ChapterList.this,
									SogouNovelActivity.class);
							chapter_basic temp_chapter = chapter_list
									.get(position);
							book_basic temp_book = new book_basic(temp_chapter
									.getBook_name(), temp_chapter
									.getAuthor_name(), temp_chapter
									.getChapter_md5(), temp_chapter
									.getChapter_index());

							temp_book.setIs_loc(1);
							temp_book.setBegin_buf(0);
							temp_book.setPic_path(FileUtil.new_dir + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "/" + ConstData.cover_string);
							bd.add_book(temp_book);

							intent.putExtra("book_info", temp_book);

						} else {
							intent = new Intent(ChapterList.this,
									SogouNovelActivity.class);

							chapter_basic temp_chapter = chapter_list
									.get(position);
							book_basic temp_book = new book_basic(temp_chapter
									.getBook_name(), temp_chapter
									.getAuthor_name(), temp_chapter
									.getChapter_md5(), temp_chapter
									.getChapter_index());
							edit.putInt("change_chapter", 1);
							edit.commit();
							intent.putExtra("book_info", temp_book);
						}

						startActivity(intent);

					} else {
						t.setTextColor(getResources().getColor(R.color.textcolor63));
						if (book_mark == position) {
							v.setBackgroundResource(R.drawable.bookmark_bg);
							mark.setVisibility(View.VISIBLE);
						} else {
							v.setBackgroundResource(ConstData.backgroundColor[position % 2]);
							mark.setVisibility(View.GONE);
						}
					}

					return true;
				}
			});

			return convertView;
		}

		private class HolderView {
			RelativeLayout layout;
			TextView title;
			ImageView mark;
		}
	}

}
