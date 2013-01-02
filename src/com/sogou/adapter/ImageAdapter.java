package com.sogou.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class ImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context context;
//	private Integer[] mImageIds;
	private List<Bitmap> images;

	public ImageAdapter(Context context, List<Bitmap> image) {
		this.context = context;
		this.images = image;
	}

	public int getCount() {
		return images.size();
		//return mImageIds.length;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(context);
		imageView.setImageBitmap(images.get(position));
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, 200));
		imageView.setBackgroundResource(mGalleryItemBackground);
		return imageView;
	}

	public int getmGalleryItemBackground() {
		return mGalleryItemBackground;
	}

	public void setmGalleryItemBackground(int mGalleryItemBackground) {
		this.mGalleryItemBackground = mGalleryItemBackground;
	}

}