package com.sogou.sogounovel;

import java.util.ArrayList;
import java.util.List;

import com.sogou.R;
import com.sogou.component.MyDialogBuilder;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainNews extends Activity {

	// 页卡内容
	private ViewPager mPager;
	// Tab页面列表
	private List<View> listViews;
	// 当前页卡编号
	private LocalActivityManager manager = null;
	private List<View> viewlist = new ArrayList<View>();
	private HorizontalScrollView Hscrollview;
	private RadioGroup newsGroup;
	private RadioButton[] newsGroupButtons;
	public static String[] newsGroupName = { "国内", "社会", "军事", "国际", "图库",
			"快讯", "体育", "科技", "财经", "娱乐", "汽车", "女人" };
	private int index_now = 0,index_bug = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news);
		
		Hscrollview = (HorizontalScrollView) findViewById(R.id.newstab_scrollview);
		
//		Hscrollview.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				if (event.getAction() == MotionEvent.ACTION_UP) {
//					//v.setBackgroundColor(Color.parseColor(ConstData.backgroundColor[position % 2]));
//					System.out.println(Hscrollview.getScrollX());
//				}
//				
//				return false;
//			}
//		});

		for (int i = 0; i < newsGroupName.length; i++) {

			View view = (View) LayoutInflater.from(this).inflate(
					R.layout.newstabitem, null);
			TextView textview = (TextView) view.findViewById(R.id.newstabitem);
			textview.setText(newsGroupName[i]);
			viewlist.add(view);

		}

		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);

		initRadioGroup();
		initViewPager();

	}

	private void initRadioGroup() {
		newsGroup = (RadioGroup) findViewById(R.id.news_group);
		newsGroupButtons = new RadioButton[newsGroupName.length];
		for (int i = 0; i < newsGroupName.length; i++) {
			newsGroupButtons[i] = (RadioButton) findViewById(R.id.group_news_1
					+ i);
		}

		newsGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int index = checkedId - R.id.group_news_1;
				for (int i = 0; i < newsGroupName.length; i++) {

					if (i == index) {
						newsGroupButtons[i].setTextColor(Color.WHITE);
						newsGroupButtons[i]
								.setBackgroundResource(R.drawable.news_tab_selected);
					} else {
						newsGroupButtons[i].setTextColor(Color
								.parseColor("#636363"));
						newsGroupButtons[i]
								.setBackgroundResource(R.drawable.news_tab_selected_null);
					}
				}
				//修改条目时候注意修改这个。。。
				if(index == 11){
					index = -1;
				}else if(index == 0){
					index = 12;
				}
				System.out.println(""+(index_now*newsGroupName.length +index));
				index_bug = index_now*newsGroupName.length +index;
				mPager.setCurrentItem(index_now*newsGroupName.length +index);
			}
		});

	}

	/**
	 * 初始化ViewPager
	 */
	private void initViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		listViews = new ArrayList<View>();

		for (int i = 0; i < newsGroupName.length; i++) {
			listViews.add(null);
		}

		MyPagerAdapter mpAdapter = new MyPagerAdapter(listViews);
		mPager.setAdapter(mpAdapter);
		//修改条目时候注意修改这个。。。
		index_now = 500/newsGroupName.length;
		mPager.setCurrentItem(492);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			arg1 = arg1 % newsGroupName.length;
			((ViewPager) arg0).removeView(listViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
//			return mListViews.size();
			return 1000;
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			System.out.println("count is ="+((ViewPager) arg0).getChildCount());
			System.out.println("index is ="+arg1);
			
			index_now = ( arg1 - 1) / newsGroupName.length;
			arg1 = arg1 % newsGroupName.length;
			System.out.println("index is ="+arg1);
			if (listViews.get(arg1) == null) {
				Intent intent = new Intent();
				intent.setClass(MainNews.this, NewsDetail.class);
				intent.putExtra("tabindex", arg1);
				View temp_view = getView("" + arg1, intent);
				listViews.remove(arg1);
				listViews.add(arg1, temp_view);
			}
			if(((ViewPager) arg0).getChildCount()>=3)   
            {   
				((ViewPager) arg0).removeView(listViews.get(arg1));
            }
			
			((ViewPager) arg0).addView(listViews.get(arg1), 0);
			
			return listViews.get(arg1);

		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 * 页卡切换监听，ViewPager改变同样改变TabHost内容
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		private int lastPage = 0;

		public void onPageSelected(int arg0) {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			arg0 = arg0 % newsGroupName.length;
			
			if (arg0 < newsGroupName.length) {
				newsGroupButtons[arg0].setTextColor(Color.WHITE);
				newsGroupButtons[arg0]
						.setBackgroundResource(R.drawable.news_tab_selected);

				newsGroupButtons[lastPage].setTextColor(Color
						.parseColor("#636363"));
				newsGroupButtons[lastPage]
						.setBackgroundResource(R.drawable.news_tab_selected_null);
				
				int[] location = new int[2];
				newsGroupButtons[arg0].getLocationInWindow(location);
				
				if (arg0 > lastPage && location[0] > screenWidth-20) {
					Hscrollview.scrollBy(location[0] - screenWidth + screenWidth / 6, 0);
				}
				else if (arg0 < lastPage && location[0] < 10) {
					Hscrollview.scrollBy(-10+location[0], 0);
				}
				lastPage = arg0;
			}

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {
//			arg0 = arg0 % newsGroupName.length;
		}
	}

	private View getView(String id, Intent intent) {
		Window w = manager.startActivity(id, intent);
		return w.getDecorView();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MyDialogBuilder.accessDialog(MainNews.this);
			return false;
		}
		if (keyCode == KeyEvent.KEYCODE_MENU) {

			return false;
		}

		return super.onKeyDown(keyCode, event);
	}

}
