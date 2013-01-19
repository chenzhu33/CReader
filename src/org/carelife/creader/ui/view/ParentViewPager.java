package org.carelife.creader.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ParentViewPager extends ViewPager {

	public ParentViewPager(Context context) {
		super(context);
	}

	public ParentViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
		if(getCurrentItem() == 1)
			return false;
		else
			return super.onInterceptTouchEvent(motionEvent);
	}

}
