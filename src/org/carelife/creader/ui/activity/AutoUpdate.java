package org.carelife.creader.ui.activity;

import java.util.ArrayList;
import java.util.List;

import org.carelife.creader.bean.BookBasicBean;
import org.carelife.creader.db.BookDao;
import org.carelife.creader.service.PushService;
import org.carelife.creader.ui.adapter.AutoUpdateAapter;
import org.carelife.creader.util.ToastUtil;

import org.carelife.creader.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

public class AutoUpdate extends Activity {

	private CheckBox selectAll;

	private ListView bookList;

	private Button commitButton;

	private Button cancelButton;

	private List<Boolean> isCheckedList;

	private List<Pair<String, String>> bookNameList;

	private List<BookBasicBean> basic_list;

	private BookDao bd;
	private boolean cheak_flag = true;
	private ToastUtil toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View title = getLayoutInflater().inflate(R.layout.switch_action_layout,
				null);
		Switch switchBtn = (Switch) title.findViewById(R.id.switchBtn);
		getActionBar().setCustomView(title,
				new ActionBar.LayoutParams(Gravity.RIGHT));
		getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("自动追更");

		switchBtn.setChecked(true);
		switchBtn
				.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO ....
					}
				});

		setContentView(R.layout.autoupdate);

		toast = ToastUtil.getInstance(this);

		selectAll = (CheckBox) findViewById(R.id.checkall);
		bookList = (ListView) findViewById(R.id.autoupdate_list);
		commitButton = (Button) findViewById(R.id.autoupdate_button_commit);
		cancelButton = (Button) findViewById(R.id.autoupdate_button_cancer);

		initData();
		AutoUpdateAapter auAd = new AutoUpdateAapter(AutoUpdate.this,
				bookNameList, isCheckedList);

		bookList.setAdapter(auAd);

		selectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (cheak_flag) {
					for (int i = 0; i < isCheckedList.size(); i++) {
						isCheckedList.set(i, isChecked);
						AutoUpdateAapter auAd = new AutoUpdateAapter(
								AutoUpdate.this, bookNameList, isCheckedList);
						bookList.setAdapter(auAd);
					}
				} else {
					cheak_flag = true;
				}

			}
		});

		bookList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (arg2 < isCheckedList.size() && arg2 >= 0) {
					isCheckedList.set(arg2, !isCheckedList.get(arg2));
					boolean temp_b = cheak_all(isCheckedList);
					if (!temp_b && selectAll.isChecked() || temp_b
							&& !selectAll.isChecked()) {
						cheak_flag = false;
					}
					selectAll.setChecked(temp_b);

					AutoUpdateAapter auAd = new AutoUpdateAapter(
							AutoUpdate.this, bookNameList, isCheckedList);
					bookList.setAdapter(auAd);
				}
			}
		});

		commitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 写入数据库
				for (int i = 0; i < isCheckedList.size(); i++) {
					if (isCheckedList.get(i) == true) {
						bd.set_book_needupdate_force(basic_list.get(i));
					} else {
						bd.del_needupdate(basic_list.get(i));
					}
				}
				toast.setText("设置成功");
				Intent intentservice = new Intent()
						.setAction(PushService.ACTION);
				startService(intentservice);
				AutoUpdate.this.finish();

			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				toast.setText("已取消");
				AutoUpdate.this.finish();
			}
		});
	}

	private boolean cheak_all(List<Boolean> list) {

		for (boolean item : list) {
			if (!item) {
				return false;
			}
		}
		return true;
	}

	private void initData() {
		// TODO
		bookNameList = new ArrayList<Pair<String, String>>();
		isCheckedList = new ArrayList<Boolean>();
		bd = BookDao.getInstance(AutoUpdate.this);
		basic_list = bd.getBook_list();
		if (basic_list != null) {
			for (BookBasicBean book : basic_list) {
				bookNameList.add(new Pair<String, String>(book.getBook_name(),
						book.getAuthor_name()));
				if (book.getNeed_post() == 1)
					isCheckedList.add(true);
				else
					isCheckedList.add(false);
			}
		}
		selectAll.setChecked(cheak_all(isCheckedList));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			AutoUpdate.this.finish();
			break;
		case R.id.menu_search:
			Intent intent = new Intent(AutoUpdate.this, SearchPage.class);
			AutoUpdate.this.startActivity(intent);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}
}
