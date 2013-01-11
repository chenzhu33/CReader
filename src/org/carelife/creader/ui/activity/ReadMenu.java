package org.carelife.creader.ui.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.carelife.creader.bean.ChapterBasicBean;
import org.carelife.creader.bookfile.BookPageFactory;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.db.BookDao;
import org.carelife.creader.ui.component.MyDialogBuilder;
import org.carelife.creader.util.BrightUtil;
import org.carelife.creader.util.FileUtil;
import org.carelife.creader.util.NetworkUtil;
import org.carelife.creader.util.ToastUtil;

import org.carelife.creader.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class ReadMenu extends Activity{
	private ImageButton mybutton_mode;//切换日夜间模式按钮
	private ImageView day_mode,cut1,cut2,cut3,cut4;
	private TextView day_mode_text;
	private RelativeLayout book_list,menu_home,menu_refresh,v_h_trans,progress,setting,night,download,download_layout,menu_top;
	private LinearLayout menu_bottom;
	private ProgressBar download_bar;
	private SharedPreferences sp;
	private Editor edit;
	SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
    Date curDate;
    String time_str;
    BookPageFactory pagefactory;
    int chapterindex_now_reading;
    BookDao bd;
    List<Map<String, Integer>> book_mark = new ArrayList<Map<String,Integer>>();
    int flag_sql = 0,Download_chapter_num;
    String sql;
    ToastUtil toast;
    private String book_name;
	private String author_name;
    LinearLayout backtoread;
    ChapterBasicBean chapter,Download_chapter;
    boolean DownloadFlag = true,Flag_3G = false;
    Dialog dialog_3g_chapter,dialog_makesure;
    int progress_int = 0;
    private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				if(download_layout.getVisibility() == View.VISIBLE){
					download_layout.setVisibility(View.GONE);
				}
				toast.setText("预读失败，请重试！");
				DownloadFlag = true;
				break;
			case 0:
				download_bar.setProgress(progress_int);
				break;
			case 1:
				
				if(download_layout.getVisibility() == View.VISIBLE){
					download_layout.setVisibility(View.GONE);
				}
				toast.setText("预读完成！");
				DownloadFlag = true;
				break;
			}
		}
	};
	private int bright_now;
	private int MINIMUM_BACKLIGHT = 30;
	private int bright_set;
	private boolean Flag_bright_menu = true;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//显示状态栏
//		WindowManager.LayoutParams attr = getWindow().getAttributes();
//		attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
//		getWindow().setAttributes(attr);
//		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//		
//		
//		ReadMenu.this.getWindow().setFlags(~WindowManager.LayoutParams.FLAG_FULLSCREEN,
//	                WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.read_menu);
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		pagefactory = BookPageFactory.get_Instance();
		Download_chapter_num = sp.getInt("Setting_download_num", 30);
		chapterindex_now_reading = sp.getInt("chapterindex_now_reading", 1);
		book_name = sp.getString("menu_book_name", null);
		author_name = sp.getString("menu_author_name", null);
		toast = ToastUtil.getInstance(this);
		
		dialog_makesure = MyDialogBuilder.rawDialog(this, "确认预读", "将为您预读之后的"+Download_chapter_num+"章内容\n是否继续？");
		Button pButton2 = (Button) dialog_makesure.findViewById(R.id.dialog_ok);
		Button cButton2 = (Button) dialog_makesure.findViewById(R.id.dialog_cancer);
		dialog_makesure.setCanceledOnTouchOutside(false);
		dialog_makesure.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if(download_layout.getVisibility() == View.VISIBLE){
						download_layout.setVisibility(View.GONE);
					}
					DownloadFlag = true;
					return false;
				}
				return false;
			}
		});
		pButton2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog_makesure.dismiss();
				if(download_cheak_3g_chapter(chapter,Download_chapter_num)){
					if(download_layout.getVisibility() == View.GONE){
						download_layout.setVisibility(View.VISIBLE);
						if(DownloadFlag){
							download_bar.setProgress(0);
						}
					}
					download_content_chapter(Download_chapter,Download_chapter_num);
				}
			}
		});

		cButton2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(download_layout.getVisibility() == View.VISIBLE){
					download_layout.setVisibility(View.GONE);
				}
				DownloadFlag = true;
				dialog_makesure.dismiss();
			}
		});
		
		dialog_3g_chapter = MyDialogBuilder.rawDialog(this, "确认网络", "您现在正在使用移动网络，预读章节内容竟会耗费一些流量，是否继续？");
		Button pButton = (Button) dialog_3g_chapter.findViewById(R.id.dialog_ok);
		Button cButton = (Button) dialog_3g_chapter.findViewById(R.id.dialog_cancer);
		dialog_3g_chapter.setCanceledOnTouchOutside(false);
		dialog_3g_chapter.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if(download_layout.getVisibility() == View.VISIBLE){
						download_layout.setVisibility(View.GONE);
					}
					DownloadFlag = true;
					return false;
				}
				return false;
			}
		});
		pButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Flag_3G = true;
				if(download_layout.getVisibility() == View.GONE){
					download_layout.setVisibility(View.VISIBLE);
					if(DownloadFlag){
						download_bar.setProgress(0);
					}
				}
				download_content_chapter(Download_chapter, Download_chapter_num);
				dialog_3g_chapter.dismiss();
			}
		});

		cButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(download_layout.getVisibility() == View.VISIBLE){
					download_layout.setVisibility(View.GONE);
				}
				DownloadFlag = true;
				dialog_3g_chapter.dismiss();
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
//		book_mark = getBookMark();
		
//		bright_now = BrightUtil.getScreenBrightness(ReadMenu.this);
//		if(bright_now < MINIMUM_BACKLIGHT ){
//			bright_now = MINIMUM_BACKLIGHT;
//		}
//		edit.putInt("bright_now", bright_now);
//		edit.putBoolean("bright_flag", BrightUtil.isAutoBrightness(ReadMenu.this));
//		edit.commit();
//		
//		BrightUtil.stopAutoBrightness(ReadMenu.this);
//		if(pagefactory.getMode()){
//			bright_set = sp.getInt("bright_set_day", bright_now);
//			BrightUtil.setBrightness(ReadMenu.this, bright_set);
//		}else{
//			bright_set = sp.getInt("bright_set_night", MINIMUM_BACKLIGHT);
//			BrightUtil.setBrightness(ReadMenu.this, bright_set);
//		}



		
		
		initmenu();
		backtoread = (LinearLayout) findViewById(R.id.back_to_read);
		backtoread.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Flag_bright_menu = false;
				SogouNovelActivity.from_read_menu = true;
				ReadMenu.this.finish();
			}
		});
//		
//		Bitmap bitmap_mode = null;
//		if (pagefactory.getMode()){
//			bitmap_mode = BitmapFactory.decodeResource(getResources(),   
//		               R.drawable.night);
//		}else{
//			bitmap_mode = BitmapFactory.decodeResource(getResources(),   
//		               R.drawable.day);
//		}
//		mybutton_mode.setImageBitmap(change_pic_size(bitmap_mode));

		
		//判断书签模式
//		Map<String, Integer> map = new HashMap<String, Integer>();
//		map.put("chapter_num", chapterindex_now_reading);
//		map.put("begin_buf", pagefactory.get_m_mbBufBegin());
//		if ( book_mark.indexOf(map) == -1 ){
//			mybutton_bookmark.setImageBitmap(change_pic_size(BitmapFactory.decodeResource(getResources(), R.drawable.bookmark)));
//		}else{
//			mybutton_bookmark.setImageBitmap(change_pic_size(BitmapFactory.decodeResource(getResources(), R.drawable.bookmark_active)));
//		}
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		edit.commit();
		if(Flag_bright_menu){
			BrightUtil.setBrightness(ReadMenu.this, sp.getInt("bright_now", 76));
			if (sp.getBoolean("bright_flag", false)){
				BrightUtil.startAutoBrightness(ReadMenu.this);
			}
		}
		Flag_bright_menu = true;
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private Bitmap change_pic_size(Bitmap pic) {
		Matrix matrix = new Matrix();
		int w_temp = pic.getWidth();
		int h_temp = pic.getHeight();
//		System.out.println(w_temp+","+h_temp);
		matrix.postScale(((float) 50/w_temp), ((float) 50/h_temp)); 
		
		return Bitmap.createBitmap(pic, 0, 0,w_temp, h_temp, matrix, true);
	}
	
	private void initmenu(){
//			Bitmap bitmap_list = BitmapFactory.decodeResource(getResources(),   
//		               R.drawable.chapterlist);
//			bitmap_list = change_pic_size(bitmap_list);
//			Bitmap bitmap_set = BitmapFactory.decodeResource(getResources(),   
//		               R.drawable.setting);
//			bitmap_set = change_pic_size(bitmap_set);
//			//day or night mode
//			
//			Bitmap bitmap_font = BitmapFactory.decodeResource(getResources(),   
//		               R.drawable.menu_font);
//			bitmap_font = change_pic_size(bitmap_font);
//			
//			//bookmark
//			
//
//			
//			Bitmap bitmap_home = BitmapFactory.decodeResource(getResources(), 
//					R.drawable.home);
//			bitmap_home = change_pic_size(bitmap_home);
//		
//			Bitmap bitmap_pre = BitmapFactory.decodeResource(getResources(), 
//					R.drawable.left);
//			bitmap_pre = change_pic_size(bitmap_pre);
//			
//			Bitmap bitmap_next = BitmapFactory.decodeResource(getResources(), 
//					R.drawable.right);
//			bitmap_next = change_pic_size(bitmap_next);
//			
//			Bitmap bitmap_bight = BitmapFactory.decodeResource(getResources(), 
//					R.drawable.bight_set);
//			bitmap_bight = change_pic_size(bitmap_bight);
//			
//			Bitmap bitmap_jump = BitmapFactory.decodeResource(getResources(), 
//					R.drawable.jump_chapter);
//			bitmap_jump = change_pic_size(bitmap_jump);
//			
//			Bitmap bitmap_back = BitmapFactory.decodeResource(getResources(), 
//					R.drawable.menu_back);
//			bitmap_back = change_pic_size(bitmap_back);
//			
//			//顶上书签
//			mybutton_bookmark = (ImageView) findViewById(R.id.menu_book_mark);
//			//上面5个按钮
//			ImageButton mybutton_home = (ImageButton) findViewById(R.id.menu_home);
//			ImageButton mybutton_list = (ImageButton) findViewById(R.id.menu_list);
//			mybutton_mode = (ImageButton) findViewById(R.id.menu_mode);
//			ImageButton mybutton_font = (ImageButton) findViewById(R.id.menu_font);
//			ImageButton mybutton_bight = (ImageButton) findViewById(R.id.menu_bight);
//			//下面5个按钮
//			ImageButton mybutton_pre = (ImageButton) findViewById(R.id.menu_pre);
//			ImageButton mybutton_next = (ImageButton) findViewById(R.id.menu_next);
//			ImageButton mybutton_jump = (ImageButton) findViewById(R.id.menu_select_chapter);
//			ImageButton mybutton_set = (ImageButton) findViewById(R.id.menu_set);
//			ImageButton mybutton_back = (ImageButton) findViewById(R.id.menu_back);
//
//			
//			mybutton_home.setImageBitmap(bitmap_home);
//			mybutton_list.setImageBitmap(bitmap_list);
//			mybutton_font.setImageBitmap(bitmap_font);
//			mybutton_bight.setImageBitmap(bitmap_bight);
//			
//			
//			mybutton_pre.setImageBitmap(bitmap_pre);
//			mybutton_next.setImageBitmap(bitmap_next);
//			mybutton_jump.setImageBitmap(bitmap_jump);
//			mybutton_set.setImageBitmap(bitmap_set);
//			mybutton_back.setImageBitmap(bitmap_back);
			menu_top = (RelativeLayout) findViewById(R.id.menu_top);
			menu_bottom = (LinearLayout) findViewById(R.id.menu_bottom);
			book_list = (RelativeLayout) findViewById(R.id.menu_booklist);
			menu_home = (RelativeLayout) findViewById(R.id.menu_home);
			menu_refresh = (RelativeLayout) findViewById(R.id.menu_refresh);
			v_h_trans = (RelativeLayout) findViewById(R.id.menu_hengping);
			progress = (RelativeLayout) findViewById(R.id.menu_jindu);
			setting = (RelativeLayout) findViewById(R.id.menu_shezhi);
			night = (RelativeLayout) findViewById(R.id.menu_yejian);
			download = (RelativeLayout) findViewById(R.id.menu_huancun);
			download_layout = (RelativeLayout) findViewById(R.id.menu_download_progress_bar);
			download_bar = (ProgressBar) findViewById(R.id.download_progress_bar);
			day_mode = (ImageView) findViewById(R.id.menu_yejian1);
			day_mode_text =  (TextView) findViewById(R.id.menu_yejian1_text);
			cut1 = (ImageView) findViewById(R.id.menu_cut1);
			cut2 = (ImageView) findViewById(R.id.menu_cut2);
			cut3 = (ImageView) findViewById(R.id.menu_cut3);
			cut4 = (ImageView) findViewById(R.id.menu_cut4);
			
			
			if(pagefactory.getMode()){
				day_mode.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.yejian));
				day_mode_text.setText("夜间");
				menu_top.setBackgroundResource(R.color.read_menu_backgroundcolor);
				menu_bottom.setBackgroundResource(R.color.read_menu_backgroundcolor);
				book_list.setBackgroundResource(R.drawable.menu_booklist_selector);
				menu_home.setBackgroundResource(R.drawable.menu_booklist_selector);
				menu_refresh.setBackgroundResource(R.drawable.menu_booklist_selector); 
				v_h_trans.setBackgroundResource(R.drawable.menu_button_selector);
				progress.setBackgroundResource(R.drawable.menu_button_selector);
				setting.setBackgroundResource(R.drawable.menu_button_selector);
				night.setBackgroundResource(R.drawable.menu_button_selector);
				download.setBackgroundResource(R.drawable.menu_button_selector);
				download_layout.setBackgroundResource(R.color.read_menu_backgroundcolor);
				cut1.setVisibility(View.GONE);
				cut2.setVisibility(View.GONE);
				cut3.setVisibility(View.GONE);
				cut4.setVisibility(View.GONE);
			}else{
				day_mode.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.rijian));
				day_mode_text.setText("白天");
				menu_top.setBackgroundResource(R.color.read_menu_backgroundcolor_night);
				menu_bottom.setBackgroundResource(R.color.read_menu_backgroundcolor_night);
				book_list.setBackgroundResource(R.drawable.menu_booklist_night_selector);
				menu_home.setBackgroundResource(R.drawable.menu_booklist_night_selector);
				menu_refresh.setBackgroundResource(R.drawable.menu_booklist_night_selector); 
				v_h_trans.setBackgroundResource(R.drawable.menu_button_night_selector);
				progress.setBackgroundResource(R.drawable.menu_button_night_selector);
				setting.setBackgroundResource(R.drawable.menu_button_night_selector);
				night.setBackgroundResource(R.drawable.menu_button_night_selector);
				download.setBackgroundResource(R.drawable.menu_button_night_selector);
				download_layout.setBackgroundResource(R.color.read_menu_backgroundcolor_night);
				cut1.setVisibility(View.VISIBLE);
				cut2.setVisibility(View.VISIBLE);
				cut3.setVisibility(View.VISIBLE);
				cut4.setVisibility(View.VISIBLE);
			}
			
			
			download_layout.setVisibility(View.GONE);
			
			book_list.setOnClickListener(new OnClickListener() {
				
				

				public void onClick(View v) {
					// TODO Auto-generated method stub
					edit.putString("book_name_chapter", book_name);
					edit.putString("author_name_chapter", author_name);
					edit.putBoolean("force_fromweb_chapter", false);
					edit.putBoolean("detail_goto_chapter", false);
					edit.commit();
					Intent intent = new Intent(ReadMenu.this, ChapterList.class);
					ReadMenu.this.startActivity(intent);
					ReadMenu.this.finish();
				}
			});
			
			menu_home.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					edit.putBoolean("menu_home", true);
					edit.commit();
					SogouNovelActivity.from_read_menu = true;
					Flag_bright_menu = false;
					ReadMenu.this.finish();
				}
			});
			
			
			menu_refresh.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(NetworkUtil.checkWifiAndGPRS(ReadMenu.this)){
						edit.putBoolean("force_refresh", true);
						edit.commit();
						SogouNovelActivity.from_read_menu = true;
						Flag_bright_menu = false;
						ReadMenu.this.finish();
					}else{
						toast.setText("亲，您的网络不给力啊，稍后再试吧...");
						return;
					}
				}
			});
			
			
			v_h_trans.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (ReadMenu.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//						ReadMenu.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						edit.putBoolean("orientation", false);
						edit.commit();
					}else if (ReadMenu.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//						ReadMenu.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
						edit.putBoolean("orientation", true);
						edit.commit();
					}
					SogouNovelActivity.from_read_menu = true;
					Flag_bright_menu = false;
					ReadMenu.this.finish();
				}
			});
			
			
			
			progress.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					edit.putBoolean("menu_jump_show", true);
					edit.commit();
					SogouNovelActivity.from_read_menu = true;
					Flag_bright_menu = false;
					ReadMenu.this.finish();
				}
				
			});
			
			setting.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					edit.putBoolean("menu_composite_set_show", true);
					edit.commit();
					SogouNovelActivity.from_read_menu = true;
					Flag_bright_menu = false;
					ReadMenu.this.finish();
				}
				
			});
			
			night.setOnClickListener(new View.OnClickListener(){
				public void onClick(View v){
					pagefactory.changeMode();
					if(pagefactory.getMode()){
						edit.putInt("daymode", 1);
					}else{
						edit.putInt("daymode", 0);
					}
					edit.commit();
					SogouNovelActivity.from_read_menu = true;
					Flag_bright_menu = false;
					ReadMenu.this.finish();
				}
				
			});
			
			download.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					if(download_layout.getVisibility() == View.GONE){
//						download_layout.setVisibility(View.VISIBLE);
//						if(DownloadFlag){
//							download_bar.setProgress(0);
//						}
//					}else{
//						download_layout.setVisibility(View.GONE);
//					}
					
					if(DownloadFlag){
						DownloadFlag = false;
						bd = BookDao.getInstance(ReadMenu.this);
						chapter = bd.getChapter_from_index(book_name, author_name, chapterindex_now_reading);
//						File temp_f = new File(chapter.getChapter_path());
//						if(temp_f.exists()){
//							temp_f = new File(FileUtil.book_temp_dir+book_name+"_"+author_name+"/"+chapter.get)
//						}
						if(!dialog_makesure.isShowing()){
							Flag_bright_menu = false;
							dialog_makesure.show();
						}
						
					}
					
				}
			});
			
			
			
//			
//			
//			
//			mybutton_bight.setOnClickListener(new View.OnClickListener(){
//				public void onClick(View v){
//					edit.putBoolean("menu_bright_show", true);
//					edit.commit();
//					ReadMenu.this.finish();
//				}
//				
//			});
//			
//			
//			
//			
//			
//			mybutton_pre.setOnClickListener(new View.OnClickListener(){
//				public void onClick(View v){
//					edit.putBoolean("jump_to_pre", true);
//					edit.commit();
//					ReadMenu.this.finish();
//				}
//				
//			});
//			
//			mybutton_next.setOnClickListener(new View.OnClickListener(){
//				public void onClick(View v){
//					edit.putBoolean("jump_to_next", true);
//					edit.commit();
//					ReadMenu.this.finish();
//				}
//				
//			});
//			
//			mybutton_back.setOnClickListener(new View.OnClickListener(){
//				public void onClick(View v){
//					//返回书签
//					edit.putBoolean("menu_back", true);
//					edit.commit();
//					ReadMenu.this.finish();
//				}
//			});
//			
//			mybutton_home.setOnClickListener(new View.OnClickListener(){
//				public void onClick(View v){
//					//返回书签
//					if (ReadMenu.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//						ReadMenu.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//						edit.putBoolean("orientation", false);
//						edit.commit();
//					}else if (ReadMenu.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//						ReadMenu.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//						edit.putBoolean("orientation", true);
//						edit.commit();
//					}
//					
//				}
//				
//			});
//			
//			mybutton_bookmark.setOnClickListener(new OnClickListener() {
//				
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					toast.setText("HI");
//				}
//			});
//			
////			mybutton_bookmark.setOnClickListener(new View.OnClickListener(){
////				public void onClick(View v){
////					curDate = new Date(System.currentTimeMillis());//获取当前时间
////					time_str = formatter.format(curDate);
////					Map<String, Integer> map = new HashMap<String, Integer>();
////					map.put("chapter_num", chapterindex_now_reading);
////					map.put("begin_buf", pagefactory.get_m_mbBufBegin());
////					if ( book_mark.indexOf(map) == -1 ){
////						//插入并且更换图片
////						flag_sql = 1;
////						sql = "insert into book_mark (book_name,chapter_num,begin_buf,update_time,percent,type)values" +
////								"('"+pagefactory.get_book_name()+"',"+chapterindex_now_reading+","+pagefactory.get_m_mbBufBegin()+"," +
////								"'"+time_str+"','"+pagefactory.get_percent()+"',1)";
////						if (bd.execSQL(sql)){
////							toast.setText("加入书签成功");
////							mybutton_bookmark.setImageBitmap(change_pic_size(BitmapFactory.decodeResource(getResources(), R.drawable.bookmark_active)));
////							book_mark.add(map);
////						}else{
////							toast.setText("加入书签失败");
////						}
////
////					}else{
////						//删除书签更换图片
////						flag_sql = 1;
////						sql = "delete from book_mark where type = 1 and book_name = '"+pagefactory.get_book_name()+"' and chapter_num = "+chapterindex_now_reading+" and " +
////								" begin_buf = "+pagefactory.get_m_mbBufBegin();
////						if (bd.execSQL(sql)){
////							toast.setText("删除书签成功");
////							mybutton_bookmark.setImageBitmap(change_pic_size(BitmapFactory.decodeResource(getResources(), R.drawable.bookmark)));
////							book_mark.remove(map);
////						}else{
////							toast.setText("删除书签失败");
////							
////						}
////						
////					}
////				}
////				
////			});
//			
//			
//			
//			mybutton_mode.setOnClickListener(new View.OnClickListener(){
//				public void onClick(View v){
//					pagefactory.changeMode();
//					if(pagefactory.getMode()){
//						edit.putInt("daymode", 1);
//					}else{
//						edit.putInt("daymode", 0);
//					}
//					edit.commit();
//					ReadMenu.this.finish();
//				}
//				
//			});
//			
//			mybutton_list.setOnClickListener(new View.OnClickListener(){
//				public void onClick(View v){
//					Intent intent = new Intent(ReadMenu.this,Chapter_Bookmark_tab.class);
//					edit.putInt("chapterindex_now_reading", chapterindex_now_reading);
//					edit.putString("book_now_reading", pagefactory.get_book_name());
//					edit.commit();
//					startActivity(intent);
//					ReadMenu.this.finish();
//					
//				}
//				
//			});
//			
//			
//			mybutton_set.setOnClickListener(new View.OnClickListener(){
//				public void onClick(View v){
//					
//					Intent intent = new Intent(ReadMenu.this,ReadSetting.class);
//					startActivity(intent);
//					ReadMenu.this.finish();
//					
//				}
//				
//			});
//		
	}
	
//	public List<Map<String, Integer>> getBookMark(){
//		
//		Cursor cursor = bd.get_book_mark(pagefactory.get_book_name());
//		 List<Map<String, Integer>> book_mark_temp = new ArrayList<Map<String,Integer>>();
//		while(cursor.moveToNext()){
//			Integer chapter_num = (int) cursor.getShort(cursor.getColumnIndex("chapter_num"));
//			Integer begin_buf = (int) cursor.getShort(cursor.getColumnIndex("begin_buf"));
//			//System.out.println("data is = "+chapter_num +" , "+begin_buf);
//			
//			Map<String, Integer> map = new HashMap<String, Integer>();
//			map.put("chapter_num", chapter_num);
//			map.put("begin_buf", begin_buf);
//			book_mark_temp.add(map);
//		}
//		cursor.close();
//		return book_mark_temp;
//	}
	
	private boolean download_cheak_3g_chapter(ChapterBasicBean cb ,int num){
    	
    	this.Download_chapter = cb;
    	this.Download_chapter_num = num;
    	
    	if(NetworkUtil.checkWifiAndGPRS(this)){
    		
			if(NetworkUtil.CheckNetworkState(this) == 0){
				toast.setText("无网络连接，无法获取详情页内容。");
				if(download_layout.getVisibility() == View.VISIBLE){
					download_layout.setVisibility(View.GONE);
				}
				DownloadFlag = true;
			}else if (NetworkUtil.CheckNetworkState(this)== 2 && !Flag_3G){
				dialog_3g_chapter.show();
			}else{
				return true;
			}
			
		}else{
			toast.setText("无网络连接，无法获取详情页内容。");
			if(download_layout.getVisibility() == View.VISIBLE){
				download_layout.setVisibility(View.GONE);
			}
			DownloadFlag = true;
		}
    	
    	return false;
    }
    
    
    private void download_content_chapter(final ChapterBasicBean cb , final int num){
    	if(cb != null){
    		toast.setText("开始预读之后"+num+"章内容。");
			new Thread(){
				public void run(){
					if(get_Bookcontext(cb.getBook_name(), cb.getAuthor_name(),cb.getChapter_md5(), num, ReadMenu.this ,handler)){
						handler.sendEmptyMessage(1);
					}else{
						handler.sendEmptyMessage(-1);
					}
					
				}
			}.start();
		}
    }
	
	public boolean get_Bookcontext(String book_name , String author_name ,String  md5 ,int num,Context c,Handler myhandler){
			
			String url = UrlHelper.context_url;
	//		url="http://10.14.135.43/novelapi/novelDetailServlet?b.n=%E9%81%AE%E5%A4%A9&b.a=%E8%BE%B0%E4%B8%9C&md5=96def07ad858190f&count=10";
			if (null == book_name && null == author_name){
	    		return false;
	    	}
	    	
	    	try {
				url = url + "b.n=" + URLEncoder.encode(book_name, "utf-8") +"&b.a="+ URLEncoder.encode(author_name, "utf-8") + 
						"&md5=" + URLEncoder.encode(md5, "utf-8") +"&count=" + num;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(url);
	    	
			try {
				File temp_d = new File(FileUtil.book_temp_dir+FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name)+"/");
				if(!temp_d.exists()){
					temp_d.mkdirs();
				}
				
				File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
						+ FileUtil.cheak_string(author_name) + "/" + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
						+ md5 + "_" + num + ".zip");
				if(temp_f.exists()){
					temp_f.delete();
	//				FileUtil fu = new FileUtil();
	//				fu.UnZipBook(book_name+"_"+author_name+"_"+md5+"_"+num, book_name, author_name);
	//				return true;
				}
				temp_f.createNewFile();
				
	    		
				FileOutputStream output = new FileOutputStream(temp_f);
				HttpGet httpGet = new HttpGet(url);
	    		HttpClient client = new DefaultHttpClient();
	    		HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					
					HttpEntity entity = response.getEntity();
					
					Header[] headers = response.getAllHeaders();
					
	//				for(int i = 0;i < headers.length ; i++){
	//					System.out.println("name = "+headers[i].getName() +" , values is ="+headers[i].getValue());
	//				}
					int total_len = (int) entity.getContentLength();
					DecimalFormat df = new DecimalFormat("#0");
	//				System.out.println("len is ="+total_len);
					
					InputStream content = entity.getContent();
					byte buf[] = new byte[1024];
					int downLoadFilePosition = 0;
					int numread;
					while ((numread = content.read(buf)) != -1) {
						
						output.write(buf, 0, numread);
						downLoadFilePosition += numread;
						progress_int = (int) (downLoadFilePosition * 100 / total_len);
						myhandler.sendEmptyMessage(0);
	//					System.out.println(df.format(downLoadFilePosition* 100/total_len ) + "%");
					}
					if(temp_f.exists()){
						FileUtil fu = new FileUtil();
						fu.UnZipBook(FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
								+ md5 + "_" + num, book_name, author_name);
						return true;
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
//						+ FileUtil.cheak_string(author_name) + "/" + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
//						+ md5 + "_" + num + ".zip");
//				if(temp_f.exists()){
//					temp_f.delete();
//	//				return true;
//				}
				e.printStackTrace();
				return false;
			} finally{
				File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
						+ FileUtil.cheak_string(author_name) + "/" + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
						+ md5 + "_" + num + ".zip");
				if(temp_f.exists()){
					temp_f.delete();
	//				return true;
				}
			}
			
			return true;
		}
    
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stubf
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			Flag_bright_menu = false;
			SogouNovelActivity.from_read_menu = true;
			ReadMenu.this.finish();
			return false;
		}else if (keyCode == KeyEvent.KEYCODE_BACK) {
			Flag_bright_menu = false;
			SogouNovelActivity.from_read_menu = true;
			ReadMenu.this.finish();
			return false;
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
