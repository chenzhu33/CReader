package org.carelife.creader.ui.activity;

import java.util.ArrayList;
import java.util.List;

import org.carelife.creader.bean.BookBasicBean;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.db.BookDao;
import org.carelife.creader.ui.adapter.BookGridAdapter;
import org.carelife.creader.ui.component.IntroduceDialog;
import org.carelife.creader.ui.component.MyDialogBuilder;
import org.carelife.creader.util.FileUtil;
import org.carelife.creader.util.UpdateUtil;

import org.carelife.creader.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainNovelGrid extends Activity {

	private List<String> book_update_data = new ArrayList<String>();
	private SharedPreferences sp;
	private Editor edit;
	private BookDao bd;
	private GridView gridview;
	private BookGridAdapter bookGridAdapter;
	private List<BookBasicBean> basic_list = new ArrayList<BookBasicBean>();
	private Dialog bookInfoDialog;
	private FileUtil fm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bd = BookDao.getInstance(MainNovelGrid.this);
		bd.open();
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		fm = new FileUtil();

		setContentView(R.layout.bookgridview);
		initGridView();
		bookGridAdapter = new BookGridAdapter(MainNovelGrid.this, basic_list);
		gridview = (GridView) findViewById(R.id.bookgridview);
		gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridview.setBackgroundColor(Color.WHITE);
		gridview.setAdapter(bookGridAdapter);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v,
					final int position, long id) {
				if (!basic_list.get(position).getBook_name()
						.equals("add_a_book")
						&& !basic_list.get(position).getBook_name()
								.equals("_space_")) {

					if (basic_list.get(position).getIs_loc() != 1) {

						edit.putString("webview_url", UrlHelper.tc_url
								+ basic_list.get(position).getChapter_md5());
						edit.commit();

						bd.update_book_time(basic_list.get(position)
								.getBook_name(), basic_list.get(position)
								.getAuthor_name());
						bd.del_update(basic_list.get(position));
						// 更新非本地库最后章节
						new Thread() {
							public void run() {
								try {
									String temp_max_chapter = UpdateUtil
											.cheak_maxchaptercode(
													MainNovelGrid.this,
													basic_list.get(position)
															.getBook_name());
									basic_list.get(position).setMax_md5(
											temp_max_chapter);
									bd.insert_maxmd5(basic_list.get(position));
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}.start();
						Intent intent = new Intent(MainNovelGrid.this,
								TcBookActivity.class);
						MainNovelGrid.this.startActivity(intent);

					} else {
						Intent intent = new Intent(MainNovelGrid.this,
								SogouNovelActivityPager.class);
						bd.del_update(basic_list.get(position));
						intent.putExtra("book_info", basic_list.get(position));
						MainNovelGrid.this.startActivity(intent);

					}

				} else if (basic_list.get(position).getBook_name()
						.equals("add_a_book")) {
					Intent intent = new Intent(MainNovelGrid.this,
							BookStoreTab.class);
					MainNovelGrid.this.startActivity(intent);
				}
			}
		});
		gridview.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				final int position = arg2;
				if (!basic_list.get(arg2).getBook_name().equals("add_a_book")
						&& !basic_list.get(arg2).getBook_name()
								.equals("_space_")) {
					bookInfoDialog = new IntroduceDialog(MainNovelGrid.this,
							R.layout.dialog_bookintro, R.style.Theme_dialog);
					bookInfoDialog.setCanceledOnTouchOutside(true);
					TextView bookname = (TextView) bookInfoDialog
							.findViewById(R.id.dialog_book_title);
					TextView authorname = (TextView) bookInfoDialog
							.findViewById(R.id.dialog_book_author);
					TextView updatetime = (TextView) bookInfoDialog
							.findViewById(R.id.dialog_book_date);
					TextView type = (TextView) bookInfoDialog
							.findViewById(R.id.dialog_book_type);
					TextView readtime = (TextView) bookInfoDialog
							.findViewById(R.id.dialog_book_readtime);
					TextView size = (TextView) bookInfoDialog
							.findViewById(R.id.dialog_book_size);
					TextView location = (TextView) bookInfoDialog
							.findViewById(R.id.dialog_book_location);
					ImageView bookpic = (ImageView) bookInfoDialog
							.findViewById(R.id.dialog_book_pic);
					Button deletebook = (Button) bookInfoDialog
							.findViewById(R.id.dialog_delete);

					BookBasicBean book_info = basic_list.get(position);

					String temp_title = (String) book_info.getBook_name();
					if (temp_title.length() > 8) {
						temp_title = temp_title.substring(0, 8) + "...  ";
					}
					bookname.setText(temp_title);
					authorname.setText("作者：" + book_info.getAuthor_name()
							+ "      ");
					updatetime.setText("更新："
							+ book_info.getUpdate_time().substring(0, 10)
							+ "      ");
					type.setText("格式：txt" + "      ");
					readtime.setText("时间："
							+ book_info.getCreate_time().substring(0, 10)
							+ "      ");
					size.setText("大小："
							+ (String) fm.BookSize(book_info.getBook_name(),
									book_info.getAuthor_name()) + "      ");
					location.setText("位置：sd卡" + "      ");
					Bitmap bm = BitmapFactory.decodeFile(book_info
							.getPic_path());
					if (bm != null)
						bookpic.setImageBitmap(bm);
					else {
						bookpic.setImageResource(R.drawable.book_default_s);
					}
					deletebook.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							FileUtil.delete_book(basic_list.get(position)
									.getBook_name(), basic_list.get(position)
									.getAuthor_name(), MainNovelGrid.this);
							if (position == -1) {
								return;
							}

							basic_list.remove(position);
							
							for (int i = basic_list.size() - 1; i >= 0; i--) {
								if (basic_list.get(i).getBook_name()
										.equals("_space_")) {
									basic_list.remove(i);
								}
							}
							int booknumber = basic_list.size();
							if (booknumber < 9) {
								for (int i = booknumber; i < 9; i++) {
									BookBasicBean addspace1 = new BookBasicBean();
									addspace1.setBook_name("_space_");
									basic_list.add(addspace1);
								}
							} else if (booknumber % 3 != 0) {
								for (int i = booknumber % 3; i < 3; i++) {
									BookBasicBean addspace1 = new BookBasicBean();
									addspace1.setBook_name("_space_");
									basic_list.add(addspace1);
								}
							}

							bookGridAdapter.updateData(basic_list);
							bookGridAdapter.notifyDataSetChanged();
							bookInfoDialog.dismiss();

						}
					});
					bookInfoDialog.show();
					// Intent intent = new Intent(MainNovelGrid.this,
					// BookInfoDialog.class);
					// intent.putExtra("position", arg2);
					// intent.putExtra("book_info", basic_list.get(position));
					// MainNovelGrid.this.startActivityForResult(intent, 1);

				}
				return false;
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			Bundle b = data.getExtras();
			int position = b.getInt("position");
			if (position == -1) {
				return;
			}
			basic_list.remove(position);

			if (basic_list.size() < 9) {
				for (int i = basic_list.size(); i < 9; i++) {
					BookBasicBean addspace1 = new BookBasicBean();
					addspace1.setBook_name("_space_");
					basic_list.add(addspace1);
				}
			} else if (basic_list.size() % 3 != 0) {
				for (int i = basic_list.size() % 3; i < 3; i++) {
					BookBasicBean addspace1 = new BookBasicBean();
					addspace1.setBook_name("_space_");
					basic_list.add(addspace1);
				}
			}

			bookGridAdapter = new BookGridAdapter(MainNovelGrid.this,
					basic_list);
			gridview.setAdapter(bookGridAdapter);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initGridView();
		if (basic_list == null || book_update_data == null) {
			return;
		}
		bookGridAdapter.updateData(basic_list);
		bookGridAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MyDialogBuilder.accessDialog(MainNovelGrid.this);
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {

			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void initGridView() {
		basic_list = bd.getBook_list();
		if (basic_list != null) {
			for (int l = 0; l < basic_list.size(); l++) {
				System.out.println(basic_list.get(l).getBook_name()
						+ basic_list.get(l).getAuthor_name() + " max is ="
						+ basic_list.get(l).getMax_md5() + " need_post is ="
						+ basic_list.get(l).getNeed_post() + " is_update is ="
						+ basic_list.get(l).getIs_update());
			}
		}

		if (basic_list == null) {
			basic_list = new ArrayList<BookBasicBean>();
		}

		if (basic_list != null) {
			BookBasicBean addBook = new BookBasicBean();
			addBook.setBook_name("add_a_book");
			basic_list.add(addBook);
			if (basic_list.size() < 9) {
				for (int i = basic_list.size(); i < 9; i++) {
					BookBasicBean addspace1 = new BookBasicBean();
					addspace1.setBook_name("_space_");
					basic_list.add(addspace1);
				}
			} else if (basic_list.size() % 3 != 0) {
				for (int i = basic_list.size() % 3; i < 3; i++) {
					BookBasicBean addspace1 = new BookBasicBean();
					addspace1.setBook_name("_space_");
					basic_list.add(addspace1);
				}
			}
		}

	}
}
