package com.sogou.sogounovel;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sogou.R;
import com.sogou.adapter.ImageAdapter;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageDetail extends Activity {
	private Gallery myGallery;
	private ImageView imageView;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_detail);
		myGallery = (Gallery) findViewById(R.id.myGallery);
		imageView = (ImageView) findViewById(R.id.mainImage);
		String imageName = this.getIntent().getStringExtra("pic_url");

		try {
			imageView.setImageBitmap(BitmapFactory
					.decodeStream(new BufferedInputStream(this.getAssets().open(
							imageName))));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		BufferedInputStream buf = null;
		for (int i = 0; i < 10; i++) {
			try {
				InputStream in = this.getAssets().open(
						"images/1 (" + i + ").jpg");
				buf = new BufferedInputStream(in);
				bitmaps.add(BitmapFactory.decodeStream(buf));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ImageAdapter adapter = new ImageAdapter(this, bitmaps);

		TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);

		adapter.setmGalleryItemBackground(typedArray.getResourceId(
				R.styleable.Gallery_android_galleryItemBackground, 0));

		myGallery.setAdapter(adapter);
	}
}
