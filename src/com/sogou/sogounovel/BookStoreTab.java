package com.sogou.sogounovel;

import com.sogou.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class BookStoreTab extends TabActivity {

	private TabHost myTabhost;

	private SharedPreferences sp;
	private RadioGroup tabGroup;
	private RadioButton cate, rank;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent;
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);

		setContentView(R.layout.maintab);
		myTabhost = this.getTabHost();
		tabGroup = (RadioGroup) findViewById(R.id.cate_group);
		cate = (RadioButton) findViewById(R.id.tabbar_cate);
		rank = (RadioButton) findViewById(R.id.tabbar_rank);

		ImageView search = (ImageView) findViewById(R.id.catetab_search);

		intent = new Intent().setClass(this, CatePage.class);
		myTabhost.addTab(myTabhost.newTabSpec("cate").setIndicator("")
				.setContent(intent));

		intent = new Intent().setClass(this, RankPage.class);
		myTabhost.addTab(myTabhost.newTabSpec("rank").setIndicator("")
				.setContent(intent));
		
		tabGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tabbar_cate:
					myTabhost.setCurrentTabByTag("cate");
					cate.setTextColor(Color.WHITE);
					rank.setTextColor(Color.BLACK);
					cate.setBackgroundResource(R.drawable.cate_left_bt);
					rank.setBackgroundResource(R.drawable.cate_right_g_bt);
					break;
				case R.id.tabbar_rank:
					myTabhost.setCurrentTabByTag("rank");
					rank.setTextColor(Color.WHITE);
					cate.setTextColor(Color.BLACK);
					rank.setBackgroundResource(R.drawable.cate_right_bt);
					cate.setBackgroundResource(R.drawable.cate_left_g_bt);
					break;
				default:
					break;
				}
			}
		});
		
		search.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(BookStoreTab.this, SearchPage.class);
				BookStoreTab.this.startActivity(intent);
			}
		});
	}

}
