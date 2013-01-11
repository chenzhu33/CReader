package org.carelife.creader.ui.adapter;

import java.util.HashMap;
import java.util.List;

import org.carelife.creader.dao.RankData;
import org.carelife.creader.dao.SearchData;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.util.AsynImageLoaderUtil;
import org.carelife.creader.util.ToastUtil;
import org.carelife.creader.util.XmlUtil;

import android.app.ProgressDialog;
import android.content.Context;
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

public class CateRankListAdapter extends BaseAdapter {
	Context context;
	List<RankData> data;
	ProgressDialog dialog;
	private HashMap<String, Bitmap> imageCache = new HashMap<String, Bitmap>();
	private HashMap<String, ImageView> imageViews = new HashMap<String, ImageView>();
	private String book_name;
	private SearchData book_searchresult;

	private Handler viewHandler;

	final Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case 0:
				if (message.obj != null) {
					String url = (String) message.obj;
					imageViews.get(url).setImageBitmap(imageCache.get(url));
				}
				break;

			case 1:
				if (message.obj != null) {
					String url = (String) message.obj;
					imageViews.get(url).setImageBitmap(imageCache.get(url));
				}
				break;

			default:
				break;
			}

		}
	};

	public CateRankListAdapter(Context context, List<RankData> result,
			Handler viewHandler , ProgressDialog dialog) {
		this.data = result;
		this.context = context;
		this.viewHandler = viewHandler;
		this.dialog = dialog;
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
			convertView = View
					.inflate(context, R.layout.rankcatelistitem, null);
			holder = new HolderView();
			holder.pic = (ImageView) convertView
					.findViewById(R.id.rankcate_list_pic);
			holder.title = (TextView) convertView
					.findViewById(R.id.rankcate_list_title);
			holder.status = (TextView) convertView
					.findViewById(R.id.rankcate_list_status);
			holder.author = (TextView) convertView
					.findViewById(R.id.rankcate_list_author);
			holder.desc = (TextView) convertView
					.findViewById(R.id.rankcate_list_desc);
			holder.layout = (RelativeLayout) convertView
					.findViewById(R.id.rankcatelist_layout);
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

		String status = "[连载]";
		if (data.get(position).getstatus() == 1
				|| data.get(position).getstatus() == 2) {
			status = "[全本]";
		}

		holder.status.setText(status);

		if (null != data.get(position).getauthor_name()) {
			holder.author.setText("作者：" + data.get(position).getauthor_name());
		}
		if (null != data.get(position).getdesc()) {
			holder.desc.setText("简介：" + data.get(position).getdesc());
		}

		if (position % 2 == 0)
			holder.layout.setBackgroundResource(R.drawable.listview_white_selector);
		else
			holder.layout.setBackgroundResource(R.drawable.listview_gray_selector);
		
		return convertView;
	}

	private class HolderView {
		RelativeLayout layout;
		ImageView pic;
		TextView title;
		TextView status;
		TextView author;
		TextView desc;
	}
	
}
