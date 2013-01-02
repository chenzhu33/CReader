package com.sogou.adapter;

import java.util.List;
import com.sogou.R;
import com.sogou.component.TextViewVertical;
import com.sogou.data.book_basic;
import com.sogou.util.FileUtil;
import com.sogou.util.ToastUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class BookGridAdapter extends BaseAdapter {
	// 定义Context
	private Context mContext;
	private GridViewHolder gridholder;
	private List<book_basic> dataList;
	private Bitmap t_Bitmap;
	int BOOK_WIDTH = 80;
	int BOOK_HEIGH = 102;
	FileUtil fm;
	ToastUtil toast;
	private SharedPreferences sp;
	private int[] shelfPics = { R.drawable.shelf_left, R.drawable.shelf_mid,
			R.drawable.shelf_right };

	// public BookGridAdapter(Context c, List<Map<String, Object>> results) {
	// mContext = c;
	// this.data_list = results;
	// fm = new FileUtil();
	// sp = c.getSharedPreferences("sogounovel", 0);
	// sp.edit();
	// toast = ToastUtil.getInstance(c);
	// }

	public BookGridAdapter(Context c, List<book_basic> results) {
		mContext = c;
		this.dataList = results;
		fm = new FileUtil();
		sp = c.getSharedPreferences("sogounovel", 0);
		sp.edit();
		toast = ToastUtil.getInstance(c);
	}

	// 获取图片的个数
	public int getCount() {
		return dataList.size();
	}

	// 获取图片在库中的位置
	public Object getItem(int position) {
		return position;
	}

	// 获取图片ID
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			convertView = View.inflate(mContext, R.layout.bookgridviewitem,
					null);
			gridholder = new GridViewHolder();
			gridholder.book_name = (TextViewVertical) convertView
					.findViewById(R.id.bookgrid_name);
			gridholder.book_pic = (ImageView) convertView
					.findViewById(R.id.bookgrid_pic);
			gridholder.book_pic_back = (ImageView) convertView
					.findViewById(R.id.bookgrid_pic_backgroud);
			gridholder.book_pic_update = (ImageView) convertView
					.findViewById(R.id.bookgrid_pic_update);
			gridholder.shelf_pic = (ImageView) convertView
					.findViewById(R.id.bookgrid_shelf);
			gridholder.name_bg = (RelativeLayout) convertView
					.findViewById(R.id.bookgrid_namebg);
			convertView.setTag(gridholder);
		} else {
			gridholder = (GridViewHolder) convertView.getTag();
		}

		String a = dataList.get(position).getBook_name().trim();
		gridholder.book_pic_update.setVisibility(View.GONE);
		if (a.equals("add_a_book")) {
			gridholder.book_name.setText("");
			gridholder.book_pic.setVisibility(View.INVISIBLE);
			gridholder.book_pic_back.setVisibility(View.VISIBLE);
			gridholder.book_pic_back.setImageResource(R.drawable.book_add);
			gridholder.name_bg.setVisibility(View.GONE);
		} else if (a.equals("_space_")) {
			gridholder.book_name.setText("");
			gridholder.name_bg.setVisibility(View.GONE);
			gridholder.book_pic_back.setVisibility(View.GONE);
			gridholder.book_pic.setVisibility(4);
		} else {
			gridholder.book_pic.setVisibility(0);
			Bitmap bm = null;
			try{
				bm = BitmapFactory.decodeFile(dataList.get(position)
						.getPic_path());
			}catch(OutOfMemoryError e){
				bm = null;
			}
			if (null != bm) {
				// t_Bitmap = createBitmap(bm, BOOK_WIDTH, BOOK_HEIGH);
				// if (null != data_list.get(position).get("is_update"))
				// t_Bitmap = update_Bitmap(t_Bitmap);
//				bm = update_Bitmap(bm);
				if(dataList.get(position).getIs_update() == 1){
					gridholder.book_pic_update.setVisibility(View.VISIBLE);
				}else{
					gridholder.book_pic_update.setVisibility(View.GONE);
				}
				
				gridholder.book_name.setText("");
				gridholder.name_bg.setVisibility(View.GONE);
				try{
					gridholder.book_pic.setImageBitmap(bm);
					gridholder.book_pic.setVisibility(View.VISIBLE);
					gridholder.book_pic_back.setImageResource(R.drawable.main_book_bg);
				}catch(Exception e){
					gridholder.book_pic.setVisibility(View.INVISIBLE);
					gridholder.book_pic_back.setImageResource(R.drawable.book_default);
				}catch(OutOfMemoryError e1){
					gridholder.book_pic.setVisibility(View.INVISIBLE);
					gridholder.book_pic_back.setImageResource(R.drawable.book_default);
				}
				gridholder.book_pic_back.setVisibility(View.VISIBLE);
			} else {
				// if (null != data_list.get(position).get("is_update")) {
				// t_Bitmap = update_Bitmap(BitmapFactory.decodeResource(
				// mContext.getResources(), R.drawable.book_default));
				// gridholder.book_pic.setImageBitmap(t_Bitmap);
				// } else {
				gridholder.book_name.setText(a);
				gridholder.book_name.setVisibility(View.VISIBLE);
				gridholder.name_bg.setVisibility(View.VISIBLE);
				gridholder.book_pic_back.setImageResource(R.drawable.book_default);
				gridholder.book_pic_back.setVisibility(View.VISIBLE);
				gridholder.book_pic.setVisibility(View.INVISIBLE);

				// }
			}

		}

		gridholder.shelf_pic.setImageResource(shelfPics[position % 3]);

		// gridholder.book_pic.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		//
		// }
		//
		// });
		//
		// gridholder.book_pic.setOnLongClickListener(new OnLongClickListener()
		// {
		//
		// public boolean onLongClick(View v) {
		// return false;
		// };
		// });

		return convertView;
	}

	public void updateData(List<book_basic> data_list) {
		this.dataList = data_list;
	}

	private Bitmap createBitmap(Bitmap pic, float w, float h) {
		if (pic == null) {
			return null;
		}
		Matrix matrix = new Matrix();
		int w_temp = pic.getWidth();
		int h_temp = pic.getHeight();
		// System.out.println(w_temp+","+h_temp);
		matrix.postScale(((float) 114 / w_temp), ((float) 151 / h_temp));

		Bitmap book_pic_temp = Bitmap.createBitmap(pic, 0, 0, w_temp, h_temp,
				matrix, true);

		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(127, 161, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		// draw top into
		// cv.drawBitmap(BitmapFactory.decodeResource(mContext.getResources(),
		// R.drawable.bookshelf_book_top), 0, 0, null);// 在 0，0坐标开始画入top
		// // draw left into
		// cv.drawBitmap(BitmapFactory.decodeResource(mContext.getResources(),
		// R.drawable.bookshelf_book_left), 0, 7, null);// 在0,7画入left

		cv.drawBitmap(BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.book_backgroud), 0, 0, null);// 画底

		// draw book_pic
		cv.drawBitmap(book_pic_temp, 2, 2, null);// 画封面

		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储

		book_pic_temp.recycle();

		matrix = new Matrix();
		// System.out.println(w_temp+","+h_temp);
		matrix.postScale(((float) w / 127), ((float) h / 161));

		return Bitmap.createBitmap(newb, 0, 0, 127, 161, matrix, true);

	}
	

	private Bitmap update_Bitmap(Bitmap pic) {
		Bitmap newb = Bitmap.createBitmap(pic.getWidth(), pic.getHeight(),
				Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(pic, 0, 0, null);
		//画更新便签
		cv.drawBitmap(BitmapFactory.decodeResource(mContext.getResources(),
				R.drawable.gengxin), 10, 0, null);
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		return newb;
	}

	public class GridViewHolder {
		private TextViewVertical book_name;
		private ImageView book_pic;
		private ImageView book_pic_back;
		private ImageView book_pic_update;
		private ImageView shelf_pic;
		private RelativeLayout name_bg;
	}
}
