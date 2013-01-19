package org.carelife.creader.ui.adapter;

import java.util.HashMap;
import java.util.List;

import org.carelife.creader.dao.RssData;
import org.carelife.creader.util.AsynImageLoaderUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.carelife.creader.R;

public class NewsListAdapter extends BaseAdapter {
	Context context;
	List<RssData> data;
	private HashMap<String, Bitmap> imageCache = new HashMap<String, Bitmap>();
	private HashMap<String, ImageView> imageViews = new HashMap<String, ImageView>();

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

	public NewsListAdapter(Context context, List<RssData> result) {
		this.data = result;
		this.context = context;
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
			convertView = View.inflate(context, R.layout.newslistitem, null);
			holder = new HolderView();
			holder.pic = (ImageView) convertView
					.findViewById(R.id.news_list_pic);
			holder.title = (TextView) convertView
					.findViewById(R.id.news_list_title);
			holder.content = (TextView) convertView
					.findViewById(R.id.news_list_content);
			holder.layout = (RelativeLayout) convertView
					.findViewById(R.id.news_relative_layout);
			convertView.setTag(holder);
		} else {
			holder = (HolderView) convertView.getTag();
		}

		if (null != data.get(position).getUrl()) {
			final String pic_url = data.get(position).getUrl();
			imageViews.put(pic_url, holder.pic);
			new Thread() {
				public void run() {
					if (imageCache.containsKey(pic_url)) {
						if (imageCache.get(pic_url) != null) {
							Message message = handler.obtainMessage(1, pic_url);
							handler.sendMessage(message);
							return;
						}
					}
					if (pic_url.equals("")) {
						return;
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
			// ÔÝÎÞÍ¼Æ¬

		}
		if (null != data.get(position).getTitle()) {
			holder.title.setText(data.get(position).getTitle());
		}
		String desp = data.get(position).getDescription();

		if (null != desp) {
			holder.content.setText(desp.replaceAll("[ |¡¡| ]*", ""));
		}

		if (position % 2 == 0)
			holder.layout
					.setBackgroundResource(R.drawable.listview_white_selector);
		else
			holder.layout
					.setBackgroundResource(R.drawable.listview_gray_selector);

		return convertView;
	}

	private class HolderView {
		RelativeLayout layout;
		ImageView pic;
		TextView title;
		TextView content;
	}
}
