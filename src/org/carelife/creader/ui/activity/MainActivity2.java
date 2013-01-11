package org.carelife.creader.ui.activity;

import java.util.ArrayList;
import java.util.List;

import org.carelife.creader.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

@SuppressWarnings("deprecation")
public class MainActivity2 extends Activity {
	private ActionBar actionBar;
	private ViewPager vp;
	public static String[] viewGroup = { "小说", "新闻" };

	private LocalActivityManager manager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle("CReader");
		
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		
		vp = (ViewPager) findViewById(R.id.vPager);
        final ArrayList<View> list = new ArrayList<View>();
        Intent intent = new Intent(MainActivity2.this, MainNovelGrid.class);
        list.add(getView("A", intent));
        Intent intent2 = new Intent(MainActivity2.this, MainNews.class);
        list.add(getView("B", intent2));

        vp.setAdapter(new MyPagerAdapter(list));
        vp.setCurrentItem(0);
        vp.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, MainActivity2.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("launcher", false);
			startActivity(intent);
			break;
		case R.id.menu_novel:
			vp.setCurrentItem(0);
			break;
		case R.id.menu_news:
			vp.setCurrentItem(1);
			break;
		case R.id.menu_settings:
			Intent intent2 = new Intent(MainActivity2.this, GlobalSetting.class);
			MainActivity2.this.startActivity(intent2);
			break;
		case R.id.menu_addbook:
			Intent intent3 = new Intent(MainActivity2.this,
					BookStoreTab.class);
			MainActivity2.this.startActivity(intent3);
			break;
		case R.id.menu_exit:
			MainActivity2.this.finish();
			break;	
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

    /**
     * Pager适配器
     */
    public class MyPagerAdapter extends PagerAdapter{
        List<View> list =  new ArrayList<View>();
        public MyPagerAdapter(ArrayList<View> list) {
            this.list = list;
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                Object object) {
            ViewPager pViewPager = ((ViewPager) container);
            pViewPager.removeView(list.get(position));
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ViewPager pViewPager = ((ViewPager) arg0);
            pViewPager.addView(list.get(arg1));
            return list.get(arg1);
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

	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageSelected(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {
			// arg0 = arg0 % newsGroupName.length;
		}
	}

	private View getView(String id, Intent intent) {
		Window w = manager.startActivity(id, intent);
		return w.getDecorView();
	}

}