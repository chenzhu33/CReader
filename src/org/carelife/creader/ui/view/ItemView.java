package org.carelife.creader.ui.view;

import org.carelife.creader.util.NetworkUtil;

import org.carelife.creader.R;
import org.carelife.creader.ui.activity.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ItemView {
	private View mainView;
	private ListView listView;
	private String[] title_string;
	private String[] goto_string;
	private Context context;

	public ItemView() {
	}

	public ItemView(Context context, String[] title_string, String[] goto_string) {
		this.context = context;
		this.title_string = title_string;
		this.goto_string = goto_string;
		mainView = LayoutInflater.from(context).inflate(R.layout.sidebar_layout,
				null);
		initView();
	}

	private void initView() {
		listView = (ListView) mainView.findViewById(R.id.sidebar_list);
		listView.setAdapter(new ItemAdapter());
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				if(!NetworkUtil.checkWifiAndGPRS(context)){
					Toast.makeText(context,"亲，您的网络不给力啊，去调整下吧",Toast.LENGTH_SHORT).show();
					return;
				}
				SharedPreferences sp = context.getSharedPreferences("sogounovel", Context.MODE_PRIVATE);
				Editor edit = sp.edit();
				edit.putString("caterankstring", goto_string[arg2]);
				edit.putString("catename", title_string[arg2]);
				edit.commit();
				((Activity) context).finish();
				Intent intent = new Intent(context,CateList.class);
				context.startActivity(intent);
				
			}
		});

	}

	public void setWidth(int w) {
		LayoutParams p = listView.getLayoutParams();
		p.width = w;
		listView.setLayoutParams(p);
	}
	
	public View getView() {
		return mainView;
	}

	public class ItemAdapter extends BaseAdapter {

		public int getCount() {
			return title_string.length;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final HolderView holder;
			if(convertView == null ){
				convertView = View.inflate(context, R.layout.sidebarlistitem, null);
				holder = new HolderView();
				holder.t = (TextView)convertView.findViewById(R.id.sidebar_item);
				convertView.setTag(holder);
			}else{
				holder = (HolderView)convertView.getTag();
			}
			holder.t.setText(title_string[position]);
			return convertView;
		}

		private class HolderView {
			TextView t;
		}
	}
}
