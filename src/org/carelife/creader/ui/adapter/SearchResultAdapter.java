package org.carelife.creader.ui.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.carelife.creader.bean.BookBasicBean;
import org.carelife.creader.dao.SearchData;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.db.BookDao;
import org.carelife.creader.util.AsynImageLoaderUtil;
import org.carelife.creader.util.FileUtil;
import org.carelife.creader.util.ToastUtil;
import org.carelife.creader.util.UpdateUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.carelife.creader.R;
import org.carelife.creader.ui.activity.BookDetail;
import org.carelife.creader.ui.activity.TcBookActivity;

public class SearchResultAdapter extends BaseAdapter {
	Context context;
	List<SearchData> data;
	private HashMap<String, Bitmap> imageCache = new HashMap<String, Bitmap>();
	private HashMap<String, ImageView> imageViews = new HashMap<String, ImageView>();

	private SharedPreferences sp;
	private Editor edit;

	final Handler handler = new Handler() {
		public void handleMessage(Message message) {
			if (message.obj != null) {
				String url = (String) message.obj;
				imageViews.get(url).setImageBitmap(imageCache.get(url));
			}
		}
	};
	private String book_name;
	private String author_name;
	private BookBasicBean book;
	private BookDao bookdao;
	private String temp_tc_url;

	public SearchResultAdapter(Context context, List<SearchData> result) {
		this.data = result;
		this.context = context;
		sp = context.getSharedPreferences("sogounovel", Context.MODE_PRIVATE);
		edit = sp.edit();
		bookdao = BookDao.getInstance(context);
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final HolderView holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.searchlistitem, null);
			holder = new HolderView();
			holder.pic = (ImageView) convertView
					.findViewById(R.id.search_list_pic);
			holder.title = (TextView) convertView
					.findViewById(R.id.search_list_title);
			holder.author = (TextView) convertView
					.findViewById(R.id.search_list_author);
			holder.desc = (TextView) convertView
					.findViewById(R.id.search_list_desc);
			holder.layout = (RelativeLayout) convertView
					.findViewById(R.id.search_relative_layout);
			convertView.setTag(holder);
		} else {
			holder = (HolderView) convertView.getTag();
		}

		if (null != data.get(position).getpicurl()) {
			final String pic_url = data.get(position).getpicurl();
			imageViews.put(pic_url, holder.pic);
			holder.pic.setImageResource(R.drawable.book_default);
			new Thread() {
				public void run() {
					if (imageCache.containsKey(pic_url)) {
						if (imageCache.get(pic_url) != null) {
							Message message = handler.obtainMessage(1, pic_url);
							handler.sendMessage(message);
							return;
						}
					}
					Bitmap bitmap = AsynImageLoaderUtil
							.loadImageFromNet(pic_url);
					if (bitmap != null) {
						imageCache.put(pic_url, bitmap);
					} else {
						return;
					}
					Message message = handler.obtainMessage(0, pic_url);
					handler.sendMessage(message);
				}
			}.start();
		} else {
			holder.pic.setImageResource(R.drawable.book_default);
		}

		if (null != data.get(position).getbookname()) {
			holder.title.setText(data.get(position).getbookname());
		}

		if (null != data.get(position).getauthor_name()) {
			holder.author.setText("作者：" + data.get(position).getauthor_name());
		}
		if (null != data.get(position).getdesc()) {
			holder.desc.setText("简介：" + data.get(position).getdesc());
		}

		//holder.layout.setBackgroundColor(Color.parseColor(ConstData.backgroundColor[position % 2]));
		holder.layout.setBackgroundResource(UrlHelper.backgroundColor[position % 2]);

		holder.layout.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				TextView title = (TextView) v
						.findViewById(R.id.search_list_title);
				TextView author = (TextView) v
						.findViewById(R.id.search_list_author);
				TextView desc = (TextView) v
						.findViewById(R.id.search_list_desc);

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.drawable.item_selected);
					title.setTextColor(Color.WHITE);
					author.setTextColor(Color.WHITE);
					desc.setTextColor(Color.WHITE);

				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundResource(UrlHelper.backgroundColor[position % 2]);
					title.setTextColor(context.getResources().getColor(R.color.list_title));
					author.setTextColor(context.getResources().getColor(R.color.list_content));
					desc.setTextColor(context.getResources().getColor(R.color.list_content));

					if (position >= data.size()) {
						ToastUtil.getInstance(context).setText("亲底下没了啊");
						return true;
					}

					temp_tc_url = data.get(position).geturl();
					try {
						temp_tc_url = URLEncoder.encode(temp_tc_url, "utf-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (data.get(position).getloc() != 1) {
						
						//非本地存储书籍入库
						book_name = data.get(position).getbookname();
						author_name = data.get(position).getauthor_name();
						
						new Thread() {

							public void run() {
								try {
									SaveBitmap(AsynImageLoaderUtil.loadImageFromNet_throw(data.get(position).getpicurl()));
									book = new BookBasicBean(book_name,author_name,null);
									book.setIs_loc(data.get(position).getloc());
									book.setChapter_md5(UrlHelper.tc_url + temp_tc_url);
									book.setPic_path(FileUtil.new_dir + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) +"/" + UrlHelper.cover_string);
									bookdao.add_book(book);
									String temp_max_chapter = UpdateUtil.cheak_maxchaptercode(context, book_name);
									book.setMax_md5(temp_max_chapter);
								} catch (IOException e) {
									book = new BookBasicBean(book_name,author_name,null);
									book.setIs_loc(data.get(position).getloc());
									book.setChapter_md5(UrlHelper.tc_url + temp_tc_url);
									bookdao.add_book(book);
									String temp_max_chapter = UpdateUtil.cheak_maxchaptercode(context, book_name);
									book.setMax_md5(temp_max_chapter);
									e.printStackTrace();
								} finally{
									bookdao.insert_maxmd5(book);
								}
							}
						}.start();
						
						edit.putString("webview_book_name", book_name);
						edit.putString("webview_author_name",author_name);
						edit.putString("webview_url", UrlHelper.tc_url
								+ temp_tc_url);
						edit.commit();
						Intent intent = new Intent(context,
								TcBookActivity.class);
						context.startActivity(intent);
					} else {
						Intent intent = new Intent(context, BookDetail.class);
						intent.putExtra("SearchData", data.get(position));
						context.startActivity(intent);
					}
				} else if (event.getAction() != MotionEvent.ACTION_MOVE) {
					v.setBackgroundResource(UrlHelper.backgroundColor[position % 2]);
					title.setTextColor(context.getResources().getColor(R.color.list_title));
					author.setTextColor(context.getResources().getColor(R.color.list_content));
					desc.setTextColor(context.getResources().getColor(R.color.list_content));

				}
				return true;
			}
		});

		return convertView;
	}

	private class HolderView {
		RelativeLayout layout;
		ImageView pic;
		TextView title;

		TextView author;
		TextView desc;

	}
	
	
	private void SaveBitmap(Bitmap bmp){
//		Bitmap bitmap = Bitmap.createBitmap(800, 600, Config.ARGB_8888);  
//		Canvas canvas = new Canvas(bitmap);
//		//加载背景图片
//		Bitmap bmps = BitmapFactory.decodeResource(getResources(), R.drawable.playerbackground);
//		canvas.drawBitmap(bmps, 0, 0, null);
//		//加载要保存的画面
//		canvas.drawBitmap(bmp, 10, 100, null);
//		//保存全部图层
//		canvas.save(Canvas.ALL_SAVE_FLAG);
//		canvas.restore();
		//存储路径
		File file = new File(FileUtil.new_dir + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name));
		if(!file.exists()){
			file.mkdirs();
		}
		File file_temp = new File(file.getPath()+ "/" + UrlHelper.cover_string);
		if(!file_temp.exists()){
			try {
				file_temp.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file_temp.getPath());
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
				fileOutputStream.close();
				System.out.println("saveBmp is here:"+file.getPath()+ "/" + UrlHelper.cover_string);
			} catch (Exception e) {
						e.printStackTrace();
		}
	}
	
}
