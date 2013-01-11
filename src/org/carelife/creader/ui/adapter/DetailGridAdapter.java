package org.carelife.creader.ui.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.carelife.creader.util.AsynImageLoaderUtil;

import org.carelife.creader.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailGridAdapter extends BaseAdapter {
	private Context mContext;
	private GridViewHolder gridholder;

	private List<Map<String, String>> imageTitleMap;
	
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
	

	public DetailGridAdapter(Context c, List<Map<String, String>> list_data) {
		mContext = c;
		imageTitleMap = list_data;
	}

	public int getCount() {
		return imageTitleMap.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View
					.inflate(mContext, R.layout.bookdetailother, null);
			gridholder = new GridViewHolder();
			gridholder.book_name = (TextView) convertView
					.findViewById(R.id.bookdetailother_title);
			gridholder.book_pic = (ImageView) convertView
					.findViewById(R.id.bookdetailother_pic);
			convertView.setTag(gridholder);
		} else {
			gridholder = (GridViewHolder) convertView.getTag();
		}

		String a = imageTitleMap.get(position).get("title").toString().trim();
		gridholder.book_name.setText(a);

		if (null != imageTitleMap.get(position).get("pic")) {
			
			final String pic_url = imageTitleMap.get(position).get("pic");
			imageViews.put(pic_url, gridholder.book_pic);
			gridholder.book_pic.setImageResource(R.drawable.book_default);
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
			
			
			
			
//			Bitmap bitmap = AsynImageLoaderUtil.loadImageFromNet(imageTitleMap
//					.get(position).get("pic"));
//			if (bitmap != null) {
//				gridholder.book_pic.setImageBitmap(bitmap);
//			} else {
//				gridholder.book_pic.setImageResource(R.drawable.cover_ebk);
//			}

		} else {
			gridholder.book_pic.setImageResource(R.drawable.book_default);
		}

		return convertView;
	}

	public class GridViewHolder {
		private TextView book_name;
		private ImageView book_pic;
	}

}