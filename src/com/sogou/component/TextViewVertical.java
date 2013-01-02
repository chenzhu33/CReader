package com.sogou.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.View;

public class TextViewVertical extends View {

	public static final int LAYOUT_CHANGED = 1;
	private Paint paint;
	private int mTextPosx = 0;
	private int mTextPosy = 0;
	private int mTextWidth = 0;
	private int mTextHeight = 0;
	private int mFontHeight = 0;
	private float mFontSize = 24;
	private int mRealLine = 0;
	private int mLineWidth = 0;
	private int TextLength = 0;
	private int oldwidth = 0;
	private String text = "";
	private Handler mHandler = null;
	private Matrix matrix;
	BitmapDrawable drawable = (BitmapDrawable) getBackground();

	public TextViewVertical(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TextViewVertical(Context context, AttributeSet attrs) {
		super(context, attrs);
		matrix = new Matrix();
		paint = new Paint();
		paint.setTextAlign(Align.CENTER);
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		try {
			mFontSize = Float.parseFloat(attrs.getAttributeValue(null,
					"textSize"));
		} catch (Exception e) {
		}
	}

	public final void setText(String text) {
		if(text.length()>5)
			this.text = text.substring(0,5);
		else 
			this.text = text;
		this.TextLength = this.text.length();
		if (mTextHeight > 0)
			GetTextInfo();
	}

	public final void setTextSize(float size) {
		if (size != paint.getTextSize()) {
			mFontSize = size;
			if (mTextHeight > 0)
				GetTextInfo();
		}
	}

	public final void setTextColor(int color) {
		paint.setColor(color);
	}

	public final void setTextARGB(int a, int r, int g, int b) {
		paint.setARGB(a, r, g, b);
	}

	public void setTypeface(Typeface tf) {
		if (this.paint.getTypeface() != tf) {
			this.paint.setTypeface(tf);
		}
	}

	public void setLineWidth(int LineWidth) {
		mLineWidth = LineWidth;
	}

	public int getTextWidth() {
		return mTextWidth;
	}

	public void setHandler(Handler handler) {
		mHandler = handler;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (drawable != null) {
			Bitmap t = drawable.getBitmap();
			Bitmap b = Bitmap.createBitmap(t, 0, 0,
					mTextWidth, mTextHeight);
			canvas.drawBitmap(b, matrix, paint);
		}

		draw(canvas, this.text);
	}

	private void draw(Canvas canvas, String thetext) {
		char ch;
		mTextPosy = 0;
		mTextPosx = mTextWidth - mLineWidth;
		for (int i = 0; i < this.TextLength; i++) {
			ch = thetext.charAt(i);
			if (ch == '\n') {
				mTextPosx -= mLineWidth;
				mTextPosy = 0;
			} else {
				mTextPosy += mFontHeight;
				if (mTextPosy > this.mTextHeight) {
					mTextPosx -= mLineWidth;
					i--;
					mTextPosy = 0;
				} else {
					canvas.drawText(String.valueOf(ch), mTextPosx, mTextPosy,
							paint);
				}
			}
		}

		// activity.getHandler().sendEmptyMessage(TestFontActivity.UPDATE);
	}

	private void GetTextInfo() {
    	char ch;
    	int h = 0;
    	paint.setTextSize(mFontSize);

    	if(mLineWidth==0){
    		float[] widths = new float[1];
    		paint.getTextWidths("Õý", widths);
    		mLineWidth=(int) Math.ceil(widths[0] * 1.1);
    	}
    	
    	FontMetrics fm = paint.getFontMetrics();  
      	mFontHeight = (int) (FloatMath.ceil(fm.descent - fm.top) * 0.9);
      	

      	mRealLine=0;
    	for (int i = 0; i < this.TextLength; i++) {
    		ch = this.text.charAt(i);
    		if (ch == '\n') {
	    	    mRealLine++;
	    	    h = 0;
    		} else {
    			h += mFontHeight;
    			if (h > this.mTextHeight) {
    				mRealLine++;
    				i--;
    				h = 0;
    			} else {
    				if (i == this.TextLength - 1) {
    					mRealLine++;
    				}
    			}
    	   }
    	}
    	mRealLine++;
    	mTextWidth = mLineWidth*mRealLine;
    	measure(mTextWidth, getHeight());
        layout(getLeft(), getTop(), getLeft()+mTextWidth, getBottom());
    }

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredHeight = measureHeight(heightMeasureSpec);
		// int measuredWidth = measureWidth(widthMeasureSpec);
		if (mTextWidth == 0)
			GetTextInfo();
		setMeasuredDimension(mTextWidth, measuredHeight);
		if (oldwidth != getWidth()) {//
			oldwidth = getWidth();
			if (mHandler != null)
				mHandler.sendEmptyMessage(LAYOUT_CHANGED);
		}
	}

	private int measureHeight(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		int result = 500;
		if (specMode == MeasureSpec.AT_MOST) {
			result = specSize;
		} else if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		}
		mTextHeight = result;
		return result;
	}
	/*
	 * private int measureWidth(int measureSpec) { int specMode =
	 * MeasureSpec.getMode(measureSpec); int specSize =
	 * MeasureSpec.getSize(measureSpec); int result = 500; if (specMode ==
	 * MeasureSpec.AT_MOST){ result = specSize; }else if (specMode ==
	 * MeasureSpec.EXACTLY){ result = specSize; } return result; }
	 */
}