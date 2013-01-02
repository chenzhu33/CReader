package test;

import com.sogou.R;

import java.util.ArrayList;  
import java.util.List;  
  
import android.app.LocalActivityManager;  
import android.app.TabActivity;  
import android.content.Context;  
import android.content.Intent;  
import android.graphics.BitmapFactory;  
import android.graphics.Matrix;  
import android.os.Bundle;  
import android.os.Parcelable;  
import android.support.v4.view.PagerAdapter;  
import android.support.v4.view.ViewPager;  
import android.support.v4.view.ViewPager.OnPageChangeListener;  
import android.util.DisplayMetrics;  
import android.util.Log;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.view.Window;  
import android.view.animation.Animation;  
import android.view.animation.TranslateAnimation;  
import android.widget.ImageView;  
import android.widget.TabHost;  
import android.widget.TabHost.OnTabChangeListener;  
import android.widget.TextView;  
  
public class testrolltabhost extends TabActivity {  
  
        //页卡内容   
        private ViewPager mPager;  
        // Tab页面列表   
        private List<View> listViews;   
        // 当前页卡编号   
        private LocalActivityManager manager = null;  
        private final Context context = testrolltabhost.this;  
        private TabHost mTabHost;  
  
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
          
                  
//        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        setContentView(R.layout.rolltabhost);  
          
//        mTabHost =  getTabHost();    
//        mTabHost.addTab(mTabHost.newTabSpec("0").setIndicator(    
//                "正在听").setContent(    
//                new Intent(this, BookShelf.class)));   
//          
//        mTabHost.addTab(mTabHost.newTabSpec("1").setIndicator(    
//                "本地听").setContent(    
//                new Intent(this, BookShelf.class)));   
//          
//        mTabHost.addTab(mTabHost.newTabSpec("2").setIndicator(    
//                "网络听").setContent(    
//                new Intent(this, BookShelf.class)));    
          
        mTabHost.setCurrentTab(0);    
          
        //tabhost改变同样改变ViewPager的内容   
        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {  
              
            public void onTabChanged(String tabId) {  
                mPager.setCurrentItem(Integer.parseInt(tabId));  
            }  
        });  
          
        manager = new LocalActivityManager(this, true);  
        manager.dispatchCreate(savedInstanceState);  
          
        InitViewPager();  
    }  
      
        /** 
         * 初始化ViewPager 
         */  
        private void InitViewPager() {  
//                mPager = (ViewPager) findViewById(R.id.vPager);  
//                listViews = new ArrayList<View>();  
//                MyPagerAdapter mpAdapter = new MyPagerAdapter(listViews);  
//                Intent intent = new Intent(context, BookShelf.class);  
//                listViews.add(getView("A", intent));  
//                Intent intent2 = new Intent(context, BookShelf.class);  
//                listViews.add(getView("B", intent2));  
//                Intent intent3 = new Intent(context, BookShelf.class);  
//                listViews.add(getView("C", intent3));  
//                mPager.setAdapter(mpAdapter);  
//                mPager.setCurrentItem(0);  
//                mPager.setOnPageChangeListener(new MyOnPageChangeListener());  
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
                        ((ViewPager) arg0).removeView(mListViews.get(arg1));  
                }  
  
                @Override  
                public void finishUpdate(View arg0) {  
                }  
  
                @Override  
                public int getCount() {  
                        return mListViews.size();  
                }  
  
                @Override  
                public Object instantiateItem(View arg0, int arg1) {  
                        ((ViewPager) arg0).addView(mListViews.get(arg1), 0);  
                        return mListViews.get(arg1);  
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
  
                public void onPageSelected(int arg0) {  
                        switch (arg0) {  
                        case 0:  
                                mTabHost.setCurrentTab(0);    
                                break;  
                        case 1:  
                                mTabHost.setCurrentTab(1);  
                                break;  
                        case 2:  
                                mTabHost.setCurrentTab(2);  
                                break;  
                        }  
                }  
  
                public void onPageScrolled(int arg0, float arg1, int arg2) {  
                }  
  
                public void onPageScrollStateChanged(int arg0) {  
                }  
        }  
          
        private View getView(String id,Intent intent)  
        {  
                return manager.startActivity(id, intent).getDecorView();  
        }  
}  