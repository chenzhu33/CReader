package org.carelife.creader.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import org.carelife.creader.R;
import org.carelife.creader.ui.activity.BookStoreListActivity;
import org.carelife.creader.util.NetworkUtil;

public class CateListFragment extends ListFragment {

	private String[] title_string;
	private String[] goto_string;
	private Context context;

	public static CateListFragment newInstance(String[] title_string,
			String[] goto_string) {
		CateListFragment f = new CateListFragment();
		f.title_string = title_string;
		f.goto_string = goto_string;
//		Bundle b = new Bundle();
//		b.putInt("index", index);
//		f.setArguments(b);
		return f;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		return inflater.inflate(R.layout.sidebar_layout, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		context = getActivity();
		setListAdapter(new ItemAdapter());
		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (!NetworkUtil.checkWifiAndGPRS(context)) {
					Toast.makeText(context, "亲，您的网络不给力啊，去调整下吧",
							Toast.LENGTH_SHORT).show();
					return;
				}
				SharedPreferences sp = context.getSharedPreferences(
						"sogounovel", Context.MODE_PRIVATE);
				Editor edit = sp.edit();
				edit.putString("caterankstring", goto_string[arg2]);
				edit.putString("catename", title_string[arg2]);
				edit.commit();
				((Activity) context).finish();
				Intent intent = new Intent(context, BookStoreListActivity.class);
				context.startActivity(intent);

			}
		});
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
			if (convertView == null) {
				convertView = View.inflate(context, R.layout.sidebarlistitem,
						null);
				holder = new HolderView();
				holder.t = (TextView) convertView
						.findViewById(R.id.sidebar_item);
				convertView.setTag(holder);
			} else {
				holder = (HolderView) convertView.getTag();
			}
			holder.t.setText(title_string[position]);
			return convertView;
		}

		private class HolderView {
			TextView t;
		}
	}
}
