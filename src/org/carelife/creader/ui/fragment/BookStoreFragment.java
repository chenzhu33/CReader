package org.carelife.creader.ui.fragment;

import org.carelife.creader.R;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.ui.activity.CateList;
import org.carelife.creader.util.NetworkUtil;
import org.carelife.creader.util.ToastUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BookStoreFragment extends Fragment {
	private ListView rank_list;

	private CateRankAdapter cradapter;

	private int index;

	public static BookStoreFragment newInstance(int index) {
		BookStoreFragment f = new BookStoreFragment();
		Bundle b = new Bundle();
		b.putInt("index", index);
		f.setArguments(b);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.index = getArguments().getInt("index");

		View v = inflater.inflate(R.layout.catepage, container, false);
		rank_list = (ListView) v.findViewById(R.id.rankcate_list);

		cradapter = new CateRankAdapter(getActivity(),
				UrlHelper.book_cate[index]);
		rank_list.setAdapter(cradapter);
		rank_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (!NetworkUtil.checkWifiAndGPRS(getActivity())) {
					ToastUtil.getInstance(getActivity()).setText(
							"亲，您的网络不给力啊，去调整下吧");
					return;
				}
				SharedPreferences sp = getActivity().getSharedPreferences(
						"sogounovel", Context.MODE_PRIVATE);
				Editor edit = sp.edit();
				edit.putString("caterankstring",
						UrlHelper.goto_data[index][arg2]);
				edit.putString("catename", UrlHelper.book_cate[index][arg2]);
				edit.commit();
				Intent intent = new Intent(getActivity(), CateList.class);
				getActivity().startActivity(intent);

			}
		});
		return v;
	}

	public class CateRankAdapter extends BaseAdapter {
		Context context;
		String[] name;
		HolderView holder;

		public CateRankAdapter(Context context, String[] n) {
			this.name = n;
			this.context = context;
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

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				convertView = View
						.inflate(context, R.layout.rankcateitem, null);
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
			if (position % 2 == 0)
				holder.layout
						.setBackgroundResource(R.drawable.listview_white_selector);
			else
				holder.layout
						.setBackgroundResource(R.drawable.listview_gray_selector);

			return convertView;
		}

		private class HolderView {
			TextView title;
			RelativeLayout layout;
		}

	}
}
