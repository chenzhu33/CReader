package org.carelife.creader.ui.adapter;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.carelife.creader.R;

public class GlobalSettingAapter extends BaseAdapter {
	List<String> data_list;
	Context context;
	SharedPreferences sp;
	Editor edit;

	public GlobalSettingAapter(Context context, List<String> settingitem) {
		this.data_list = settingitem;
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

	public View getView(final int position, View convertView, ViewGroup parent) {
		HolderView holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.globalsettingitem,
					null);
			holder = new HolderView();
			holder.itemName = (TextView) convertView
					.findViewById(R.id.globalsetting_item);
			holder.version = (TextView) convertView
					.findViewById(R.id.globalsetting_version);
			holder.go = (ImageView) convertView
					.findViewById(R.id.globalsetting_go);
			holder.rl = (RelativeLayout) convertView
					.findViewById(R.id.globalsetting_rl);
			convertView.setTag(holder);

		} else {
			holder = (HolderView) convertView.getTag();
		}

		if (position % 2 == 0)
			holder.rl.setBackgroundResource(R.drawable.listview_white_selector);
		else
			holder.rl.setBackgroundResource(R.drawable.listview_gray_selector);

		if (position == 2) {
			holder.version.setVisibility(0);
			PackageManager packageManager = context.getPackageManager();
			PackageInfo packInfo;
			try {
				packInfo = packageManager.getPackageInfo(
						context.getPackageName(), 0);
				holder.version.setText(packInfo.versionName);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			holder.go.setVisibility(4);
		}
		if (null != data_list.get(position)) {
			holder.itemName.setText(data_list.get(position).toString().trim());
		}
		return convertView;
	}

	private class HolderView {
		TextView itemName;
		TextView version;
		RelativeLayout rl;
		ImageView go;
	}
}