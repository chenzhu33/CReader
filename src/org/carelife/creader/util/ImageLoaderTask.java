package org.carelife.creader.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.carelife.creader.bean.FlowTagBean;
import org.carelife.creader.ui.view.FlowView;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;

public class ImageLoaderTask extends AsyncTask<FlowTagBean, Void, Bitmap> {

	private FlowTagBean param;
	private final WeakReference<FlowView> imageViewReference;

	public ImageLoaderTask(FlowView imageView) {
		imageViewReference = new WeakReference<FlowView>(imageView);

	}

	@Override
	protected Bitmap doInBackground(FlowTagBean... params) {

		param = params[0];
		return loadImageFile(param.getFileName(), param.getAssetManager());
	}

	private Bitmap loadImageFile(final String filename,
			final AssetManager manager) {
		InputStream is = null;
		try {
			Bitmap bmp = null;
			// Bitmap bmp = BitmapCache.getInstance().getBitmap(filename,
			// param.getAssetManager());
			BufferedInputStream buf;
			try {
				buf = new BufferedInputStream(param.getAssetManager().open(
						filename));
				bmp = BitmapFactory.decodeStream(buf);

			} catch (IOException e) {

				e.printStackTrace();
			}

			return bmp;
		} catch (Exception e) {
			Log.e(this.getClass().getSimpleName(), "fetchDrawable failed", e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (imageViewReference != null) {
			final FlowView imageView = imageViewReference.get();
			if (imageView != null) {
				if (bitmap != null) {
					int width = bitmap.getWidth();
					int height = bitmap.getHeight();

					LayoutParams lp = imageView.getLayoutParams();
					lp.height = (height * param.getItemWidth()) / width;
					imageView.setLayoutParams(lp);
					imageView.bitmap = bitmap;
					imageView.setImageBitmap(imageView.bitmap);

					Handler h = imageView.getViewHandler();
					Message m = h.obtainMessage(this.param.what,
							this.param.getFlowId(), lp.height, imageView);
					h.sendMessage(m);

				}
			}
		}
	}
}