package org.carelife.creader.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.carelife.creader.dao.UrlHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.carelife.creader.R;

public class AutoUpdateAapter extends BaseAdapter {
	List<Pair<String, String>> dataList;
	List<Boolean> isAutoList;
	Context context;
	SharedPreferences sp;
	Editor edit;
	
	public AutoUpdateAapter(Context context, List<Pair<String, String>> name,
			List<Boolean> isAutoList) {
		if(name != null){
			this.dataList = name;
		}else{
			this.dataList = new ArrayList<Pair<String,String>>();
		}
		
		this.isAutoList = isAutoList;
		this.context = context;
	}

	public int getCount() {
		return dataList.size();
	}

	public Object getItem(int position) {
		return dataList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		HolderView holder;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.autoupdateitem,
					null);
			holder = new HolderView();
			holder.itemName = (TextView) convertView
					.findViewById(R.id.autoupdate_bookname);
			holder.check = (CheckBox) convertView
					.findViewById(R.id.autoupdate_checkbox);
			holder.layout = (RelativeLayout)convertView.findViewById(R.id.autoupdate_relative_layout);
			convertView.setTag(holder);

		} else {
			holder = (HolderView) convertView.getTag();
		}

		if (null != dataList.get(position)) {
			holder.itemName.setText(dataList.get(position).first.toString().trim()+" ¡ª¡ª "+dataList.get(position).second.toString().trim());
		}
		if (null != isAutoList.get(position)) {
			holder.check.setChecked(isAutoList.get(position));
		}
		holder.layout.setBackgroundResource(UrlHelper.backgroundViewSelector[position % 2]);
		return convertView;
	}

	private class HolderView {
		TextView itemName;
		CheckBox check;
		RelativeLayout layout;
	}
}