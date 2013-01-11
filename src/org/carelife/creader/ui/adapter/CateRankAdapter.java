package org.carelife.creader.ui.adapter;

import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.util.NetworkUtil;
import org.carelife.creader.util.ToastUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.carelife.creader.R;
import org.carelife.creader.ui.activity.CateList;

public class CateRankAdapter extends BaseAdapter {
	Context context;
	String[] name;
	String[] gotoData;
	HolderView holder;
	SharedPreferences sp;
	Editor edit;

	public CateRankAdapter(Context context, String[] n, String[] gotoData) {
		this.name = n;
		this.gotoData = gotoData;
		this.context = context;
		sp = context.getSharedPreferences("sogounovel", Context.MODE_PRIVATE);
		edit = sp.edit();
	}

	public int getCount() {
		return name.length;
	}

	public Object getItem(int position) {
		return name[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.rankcateitem, null);
			holder = new HolderView();
			holder.title = (TextView) convertView
					.findViewById(R.id.rankcate_data);
			holder.layout = (RelativeLayout) convertView
					.findViewById(R.id.rankcateRelative);
			convertView.setTag(holder);
		} else {
			holder = (HolderView) convertView.getTag();
		}

		if (position < name.length) {
			holder.title.setText(name[position]);
		}
		holder.layout.setBackgroundResource(UrlHelper.backgroundColor[position % 2]);

		holder.layout.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				TextView t = (TextView) v.findViewById(R.id.rankcate_data);

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					v.setBackgroundResource(R.drawable.item_selected);
					t.setTextColor(Color.WHITE);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					v.setBackgroundResource(UrlHelper.backgroundColor[position % 2]);
					t.setTextColor(context.getResources().getColor(
							R.color.textcolor63));
					if (!NetworkUtil.checkWifiAndGPRS(context)) {
						ToastUtil.getInstance(context).setText(
								"亲，您的网络不给力啊，去调整下吧");
						return true;
					}
					edit.putString("caterankstring", gotoData[position]);
					edit.putString("catename", name[position]);
					edit.commit();
					Intent intent = new Intent(context, CateList.class);
					context.startActivity(intent);
				} else if (event.getAction() != MotionEvent.ACTION_MOVE) {
					v.setBackgroundResource(UrlHelper.backgroundColor[position % 2]);
					t.setTextColor(context.getResources().getColor(
							R.color.textcolor63));
				}
				return true;
			}
		});
		return convertView;
	}

	private class HolderView {
		TextView title;
		RelativeLayout layout;
	}

}