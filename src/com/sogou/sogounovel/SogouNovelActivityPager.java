package com.sogou.sogounovel;

import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;

import com.sogou.R;
import android.os.Bundle;
import com.sogou.sogounovel.*;
import com.sogou.sogounovel.ChapterList.BookAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import com.sogou.util.DownloadUtil;
import com.sogou.util.FileUtil;
import com.sogou.util.NetworkUtil;
import com.sogou.util.ToastUtil;
import com.sogou.util.BrightUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView.ScaleType;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.sogou.bookfile.BookPageFactory;
import com.sogou.bookfile.PageWidget;
import com.sogou.bookfile.PageWidget_onepager;
import com.sogou.bookfile.PageWidget_pager;
import com.sogou.bookfile.Pre_BookPageFactory;
import com.sogou.component.MyDialogBuilder;
import com.sogou.data.book_basic;
import com.sogou.data.chapter_basic;
import com.sogou.db.*;

public class SogouNovelActivityPager extends Activity {

	/** Called when the activity is first created. */
	private PageWidget_onepager mPageWidget;
	Bitmap mCurPageBitmap, mNextPageBitmap,mPrePageBitmap;
	Canvas mCurPageCanvas, mNextPageCanvas,mPrePageCanvas;
	BookPageFactory pagefactory;
	Pre_BookPageFactory pagefactory_pre;
	Pre_BookPageFactory pagefactory_next;
	int chapterindex_now_reading,chapterindex_pre,chapterindex_next;
	
	private LinearLayout menu_composite_set,menu_color_set;
	private LinearLayout menu_jump;
	private SeekBar bight_seekbar;
	private SeekBar jump_seekbar;
	private boolean jump_flag = true,bight_flag = true;
	TextView jump_show;
	ImageView font_up;
	ImageView font_down;
	ImageView block_small;
	ImageView block_mid;
	ImageView block_large;
	ImageView bg_set_brown,bg_set_green,bg_set_purple,bg_set_blue,bg_set_gray;
	ImageView sun_big,sun_small;

	private boolean is_touch = true;
	private int BLOCK_SMALL = 10,BLOCK_MID = 20 ,BLOCK_LARGE = 30;
	private int MINIMUM_BACKLIGHT = 30 , DEFULAT_SIZE = 32;
	private int FLAG=0;
	static int first_start;
	static int front_size = 32;
	static int front_size_flag = 0;
	static int change_chapter = 0;
	
	private int temp_curbuf,temp_prebuf,temp_nextbuf,pre_read_status = 0;
	private SharedPreferences sp;
	private Editor edit;
	static String chapter_path;
	static String book_name,author_name;
	public static boolean from_read_menu = false;
	FileUtil fm = null;
	int preread_flag = 0;
    float level = 1;
    private BroadcastReceiver batteryLevelRcvr;
    private IntentFilter batteryLevelFilter;
    int line_block = 0;
    int bright_now;
    int bright_set;
    BookDao bookdao;
    String sql;
    SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
    Date curDate;
    String time_str;
    int flag_sql;
    ToastUtil toast;
	int screenWidthDip; 
	int screenHeightDip;
	boolean key_down = false;
	book_basic book_info,DownLoad_book,book_info_temp;
	chapter_basic chapter_pre,chapter_next,Download_chapter;
	int Download_num,Download_chapter_num;
	boolean Flag_3G = false,Flag_double_sure = false,Pre_read_Flag = true,Flag_bright = true; 
	DownloadUtil DU;
	Dialog dialog_3g,dialog_3g_chapter;
	ProgressDialog dialog_download;
	DisplayMetrics dm;
	int SET_NUM; //设置好的预读书目
	int[] font_color = {0xff2f291e,0xff2f291e,0xff3c2d50,0xff233a5b,Color.BLACK};
	int[] backgrougcolor = {R.drawable.bg_gray,R.drawable.bg_brown,R.drawable.bg_green,R.drawable.bg_blue,R.drawable.bg_purple};
	int[] backgrougcolor_night = {R.drawable.bg_night};
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -2:
				if(dialog_download.isShowing()){
					dialog_download.dismiss();
				}
				ToastUtil.getInstance(SogouNovelActivityPager.this).setText("亲，无法刷新章节...");
				break;
			case -1:
				if(dialog_download.isShowing()){
					dialog_download.dismiss();
				}
				ToastUtil.getInstance(SogouNovelActivityPager.this).setText("亲，您的网络不给力啊，稍后再试吧...");
				book_info = book_info_temp;
				if(book_info != null){
					chapter_path = book_info.getChapter_path();
					chapterindex_now_reading = book_info.getChapter_index();
					try {
						pagefactory.openbook(chapter_path);
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						toast.setText("文件不存在啊亲，去目录再挑一章吧");
						break;
					}
				}
				pagefactory.refreshpage();
				pagefactory.onDraw(mCurPageCanvas);
				mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
				mPageWidget.postInvalidate();
				break;
			case 0:
				try {
					pagefactory.openbook(chapter_path);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					toast.setText("文件不存在啊亲，去目录再挑一章吧");
					//返回之前页
					handler.sendEmptyMessage(-1);
					break;
				}
				pagefactory.refreshchapter();
				mPageWidget.setCornerX_forfixbug();
				pagefactory.onDraw(mCurPageCanvas);
				mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
				mPageWidget.postInvalidate();
				Pre_read_Flag = true;
				if (!pre_read_chapter()){
					SogouNovelActivityPager.this.finish();
				}
				if(dialog_download.isShowing()){
					dialog_download.dismiss();
				}
				break;
			
			case 1:
				try {
					pagefactory_next.openbook(chapter_next.getChapter_path());
				} catch (Exception e1) {
					pagefactory_next.set_null();
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
				
			case 2:
				try {
					pagefactory_pre.openbook(chapter_pre.getChapter_path());
				} catch (Exception e1) {
					pagefactory_pre.set_null();
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			case 3:
				String temp_refresh_path = FileUtil.new_dir + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "/refresh/" +book_info.getChapter_md5() + ".txt";
				pagefactory.delBookFile();
				FileUtil.copyFile(temp_refresh_path, chapter_path);
				new File(temp_refresh_path).delete();
				try {
					pagefactory.openbook(chapter_path);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					handler.sendEmptyMessage(-2);
					break;
				}
				pagefactory.refreshchapter();
				mPageWidget.setCornerX_forfixbug();
				pagefactory.onDraw(mCurPageCanvas);
				mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
				mPageWidget.postInvalidate();
				Pre_read_Flag = true;
				if (!pre_read_chapter()){
					SogouNovelActivityPager.this.finish();
				}
				if(dialog_download.isShowing()){
					dialog_download.dismiss();
				}
				toast.setText("刷新完毕！");
				break;
			}
		}
	};
	

//	public SogouNovelActivity(String s) {
//		// TODO Auto-generated constructor stub
//		chapter_path = s;
//	}
//	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		System.out.println("OnCreate!");
		
		first_start = 1;
		monitorBatteryState();
		toast = ToastUtil.getInstance(this);
		fm = new FileUtil();
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		dm = new DisplayMetrics();  
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidthDip = dm.widthPixels; 
		screenHeightDip = dm.heightPixels;
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		bookdao = BookDao.getInstance(this);
		//statusbar显示隐藏flag
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,  WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		edit = sp.edit();
		setContentView(R.layout.activity_sogou_novel_viewpager);
		
		indicator();
		
		dialog_3g = MyDialogBuilder.rawDialog(this, "确认网络", "您现在正在使用移动网络，预读章节内容竟会耗费一些流量，是否继续？");
		Button pButton = (Button) dialog_3g.findViewById(R.id.dialog_ok);
		Button cButton = (Button) dialog_3g.findViewById(R.id.dialog_cancer);
		pButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Flag_3G = true;
				download_content(DownLoad_book,Download_num);
				dialog_3g.dismiss();				
			}
		});

		cButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog_3g.dismiss();
			}

		});
		dialog_3g.setCanceledOnTouchOutside(false);
		
		dialog_3g_chapter = MyDialogBuilder.rawDialog(this, "确认网络", "您现在正在使用移动网络，预读章节内容竟会耗费一些流量，是否继续？");
		Button pcButton = (Button) dialog_3g_chapter.findViewById(R.id.dialog_ok);
		Button ccButton = (Button) dialog_3g_chapter.findViewById(R.id.dialog_cancer);
		pcButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Flag_3G = true;
				download_content_chapter(Download_chapter, Download_chapter_num);
				dialog_3g_chapter.dismiss();
			}

		});
		ccButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog_3g_chapter.dismiss();
			}

		});
		
		dialog_3g_chapter.setCanceledOnTouchOutside(false);
		
		
		
		dialog_download = new ProgressDialog(SogouNovelActivityPager.this);  
		dialog_download.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
		dialog_download.setTitle("数据下载");  
		dialog_download.setMessage("正在预读章节。。。");  
		dialog_download.setIndeterminate(false);  
		dialog_download.setCancelable(true);  
		
		//dialog_download = MyDialogBuilder.waitingDialog(this, "数据下载", "正在预读章节。。。");
		dialog_download.setCanceledOnTouchOutside(false);
		
//		if(download_cheak_3g(chapter_info,10)){
//			download_content(DownLoad_book,Download_num);
//		}
		//绘制基本界面
		mPageWidget = (PageWidget_onepager)findViewById(R.id.page_widget_pager);
		//关闭硬件加速4.0系统兼容,使用xml配置可以达到兼容，不然会报错
		if(android.os.Build.VERSION.SDK_INT >= 11) {
			mPageWidget.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		mPageWidget.setScreen(screenWidthDip, screenHeightDip);
		mCurPageBitmap = Bitmap.createBitmap(screenWidthDip, screenHeightDip, Bitmap.Config.ARGB_8888);
		mNextPageBitmap = Bitmap.createBitmap(screenWidthDip, screenHeightDip, Bitmap.Config.ARGB_8888);
		mPrePageBitmap = Bitmap.createBitmap(screenWidthDip, screenHeightDip, Bitmap.Config.ARGB_8888);
		
		mCurPageCanvas = new Canvas(mCurPageBitmap);
		mNextPageCanvas = new Canvas(mNextPageBitmap);
		mPrePageCanvas = new Canvas(mPrePageBitmap);
		
		pagefactory = BookPageFactory.get_Instance();
		pagefactory.setScreen(screenWidthDip, screenHeightDip);
		pagefactory_pre = new Pre_BookPageFactory();
		pagefactory_next = new Pre_BookPageFactory();
		
		pagefactory.setBgBitmap_day(BitmapFactory.decodeResource(
				this.getResources(), backgrougcolor[sp.getInt("bg_pic_day",0)]));
		pagefactory.setBgBitmap_night(BitmapFactory.decodeResource(
				this.getResources(), backgrougcolor_night[sp.getInt("bg_pic_bight",0)]));
		
		switch (backgrougcolor[sp.getInt("bg_pic_day",0)]) {
			case R.drawable.bg_gray:
				pagefactory.set_fontcolor_day(font_color[4]);
				break;
				
			case R.drawable.bg_brown:
				pagefactory.set_fontcolor_day(font_color[0]);
				break;
				
			case R.drawable.bg_green:
				pagefactory.set_fontcolor_day(font_color[1]);
				break;
			
			case R.drawable.bg_blue:
				pagefactory.set_fontcolor_day(font_color[3]);
				break;	
	
			case R.drawable.bg_purple:
				pagefactory.set_fontcolor_day(font_color[2]);
				break;	
			default:
				break;
		}
		
		menu_composite_set = (LinearLayout) findViewById(R.id.menu_composite_set);
		menu_color_set = (LinearLayout) findViewById(R.id.menu_bg_set);
		
		if(sp.getInt("daymode", 1) == 1){
			pagefactory.setMode(true);
			menu_composite_set.setBackgroundResource(R.drawable.setting_bg);
		}else{
			pagefactory.setMode(false);
			menu_composite_set.setBackgroundResource(R.drawable.setting_bg_night);
			menu_color_set.setBackgroundResource(R.drawable.color_select_bg_night);
		}
		
		
		menu_composite_set.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		menu_jump = (LinearLayout) findViewById(R.id.menu_jump);
		menu_jump.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		initMenu();
		
		book_info = this.getIntent().getParcelableExtra(
				"book_info");
	
	
		if (book_info == null){
			toast.setText("未知错误，请重试");
			SogouNovelActivityPager.this.finish();
		}
		book_info_temp = book_info;
		line_block = sp.getInt("lineblock", BLOCK_SMALL);
		pagefactory.setm_nLineSpaceing(line_block);
		
		book_name = book_info.getBook_name();
		author_name = book_info.getAuthor_name();
		pagefactory.set_book_name(book_name);
		int wl = screenWidthDip < screenHeightDip ? screenWidthDip : screenHeightDip;
		DEFULAT_SIZE = (int) (DEFULAT_SIZE*((float)wl*0.85/480));
		front_size = sp.getInt("wordsize", DEFULAT_SIZE);
		pagefactory.changefront(front_size);
		level = sp.getFloat("level", 1);
		pagefactory.setlevel(level);
		
		
		
		SET_NUM = sp.getInt("Download_num", 10);
		
		chapter_path = book_info.getChapter_path();
		chapterindex_now_reading = book_info.getChapter_index();
		
		if(fm.file_is_exists(chapter_path)){
			try {
				pagefactory.openbook(chapter_path);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				toast.setText("文件不存在啊亲，去目录再挑一章吧");

			}
			
			if(book_info.getBegin_buf() > 0){
				
				pagefactory.set_m_mbBufBegin(book_info.getBegin_buf());
				
			}
			
			pagefactory.onDraw(mCurPageCanvas);
			mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
		}else{
			Pre_read_Flag = false;
			
			
			if(download_cheak_3g(book_info,SET_NUM)){
				download_content(DownLoad_book,Download_num);
			}
			
		}
		
		
		
//		dbinit();
		   
//		mPageWidget = new PageWidget(this);
//		setContentView(mPageWidget);

		
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		
		

//		if(path_flag == 1){
//			path_flag = 0;
//			edit.putInt("path_flag", 0);
//			edit.commit();
//			chapter_path = sp.getString("path_now_reading", "");
//			chapterindex_now_reading = fm.get_chapter_from_path(chapter_path);
//			//System.out.println("dj :"+chapter_path.substring(chapter_path.lastIndexOf("/")));
//			try {
//				pagefactory.openbook(chapter_path);
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//				toast.setText("文件不存在啊亲，去目录再挑一章吧");
//
//			}
//			pagefactory.onDraw(mCurPageCanvas);
//		}else{
//			chapterindex_now_reading = sp.getInt("chapterindex_now_reading", 1);
//			String temp_path = fm.book_dir+sp.getString("book_now_reading", "")+"/"+chapterindex_now_reading+".txt";
//			try {
//				pagefactory.openbook(temp_path);
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//				toast.setText("文件不存在啊亲，去目录再挑一章吧");
//				
//			}
//			pagefactory.set_m_mbBufBegin(sp.getInt("book_mark", 0));
//			pagefactory.onDraw(mCurPageCanvas);
//			
//		}
//		mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
//		
		mPageWidget.setOnTouchListener(new OnTouchListener() {
			private float downx = 0;

			public boolean onTouch(View v, MotionEvent e) {
				// TODO Auto-generated method stub
				
				boolean ret=false;
				
				if (v == mPageWidget) {
					
					//System.out.println("ontouch!"+e.getAction());
					
//					if(FLAG == 0 && !menu.isShowing()){
//						if (e.getX()>160 && e.getX()<240 && e.getY()>350 && e.getY()<450){
//							show(v);
//							FLAG=1;
//							return false;
//						}
//						
//					}else{
//						if (e.getY()>600){
//							return false;
//						}else{
//							if (menu.isShowing())
//								menu.dismiss();
//							if(e.getAction() == MotionEvent.ACTION_UP){
//								System.out.println(" UP !X is = "+e.getX()+" ,Y is = "+e.getY());
//								FLAG = 0;
//							} 
//							return false;
//							
//						}
//
//					}
					
					if ((!pagefactory.cheakpage_begin() || pagefactory_pre.get_isopenfile())&&(!pagefactory.cheakpage_end() || pagefactory_next.get_isopenfile())){
						mPageWidget.abortAnimation();
					}else if(!mPageWidget.mScroller.isFinished()){
						toast.setText("亲，珍爱屏幕，您慢点翻呗~");
						return false;
					}
//					
//					if(!mPageWidget.mScroller.isFinished()){
//						return false;
//					}
//					
					
					DisplayMetrics dm = new DisplayMetrics();  
					getWindowManager().getDefaultDisplay().getMetrics(dm);
					int screenWidthDip = dm.widthPixels; 
					int screenHeightDip = dm.heightPixels;
					
//					System.out.println("X is = "+e.getX()+" ,Y is = "+e.getY());
					
					
//					System.out.println("OnTouch!"+e.getAction());
					if (e.getAction() == MotionEvent.ACTION_DOWN) {
						is_touch = true;
						downx  = 0;
						if( menu_composite_set.getVisibility() == View.GONE 
								&& menu_jump.getVisibility() == View.GONE){
							if (e.getX() > screenWidthDip * 0.35 &&
									e.getX() < screenWidthDip * 0.65 &&
									e.getY() > screenHeightDip * 0.3 &&
									e.getY() < screenHeightDip * 0.7){
								
								is_touch = false;
								downx = e.getX();
								return true;
							}
						}else{
							if (menu_composite_set.getVisibility() == View.VISIBLE
									|| menu_jump.getVisibility() == View.VISIBLE){
//									mPageWidget.setCornerX_forfixbug();
								menumiss();
							}
							return false;
						}
						
						
						
							
						level = sp.getFloat("level", 1);
						pagefactory.setlevel(level);
						
						temp_curbuf = pagefactory.get_m_mbBufBegin();
						temp_prebuf = temp_curbuf;
						temp_nextbuf = temp_curbuf;
						pre_read_status = 0;
						
						pagefactory.onDraw(mCurPageCanvas);
						try {

							pagefactory.prePage();

						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						if (pagefactory.isfirstPage() && pagefactory_pre.get_isopenfile()){
							try {
								chapter_up();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							pre_read_status = 1;
						}
						temp_prebuf = pagefactory.get_m_mbBufBegin();

						pagefactory.onDraw(mPrePageCanvas);
						
						//翻章
						if(pre_read_status != 1){
							pagefactory.set_m_mbBufBegin(temp_curbuf);
							pagefactory.refreshpage();
						}else{
							try {
								pagefactory.nextPage();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (pagefactory.islastPage() && pagefactory_next.get_isopenfile()){
								chapter_down();
								pagefactory.refreshpage();
							}
							
						}
						
						
						try {
							pagefactory.nextPage();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						if (pagefactory.islastPage() && pagefactory_next.get_isopenfile()){
							chapter_down();
							pre_read_status = 2;
							
						}
						
						temp_nextbuf = pagefactory.get_m_mbBufBegin();
						
						pagefactory.onDraw(mNextPageCanvas);
						
						if(pre_read_status != 2){
							pagefactory.set_m_mbBufBegin(temp_curbuf);
							pagefactory.refreshpage();
						}else{
							try {

								pagefactory.prePage();

							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							if (pagefactory.isfirstPage() && pagefactory_pre.get_isopenfile()){
								try {
									chapter_up();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
						}

//						if(pagefactory.cheakpage_begin() && !pagefactory_pre.get_isopenfile()){
//							mPageWidget.setCornerX_forfixbug();
//							pagefactory.onDraw(mCurPageCanvas);
//							mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
//							mPageWidget.postInvalidate();
//							if(chapterindex_now_reading == 1){
//								toast.setText("亲，别翻啦到头啦~");
//							}else if (!NetworkUtil.checkWifiAndGPRS(SogouNovelActivityPager.this)){
//								toast.setText("亲，没有网络，没法读取之前的章节呀");
//							}else if(NetworkUtil.checkWifiAndGPRS(SogouNovelActivityPager.this)){
//								pre_read_chapter();
//							}else{
//								toast.setText("亲，别翻啦到头啦~");
//							}
//							
//							return false;
//						}
//						
//						if(pagefactory.cheakpage_end() && !pagefactory_next.get_isopenfile() ){
//							mPageWidget.setCornerX_forfixbug();
//							pagefactory.onDraw(mCurPageCanvas);
//							mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
//							mPageWidget.postInvalidate();
//							if(chapterindex_now_reading == bookdao.get_chapter_count(book_name, author_name)){
//								toast.setText("亲，别翻啦到头啦~");
//							}else if (!NetworkUtil.checkWifiAndGPRS(SogouNovelActivityPager.this)){
//								toast.setText("亲，没有网络，没法读取之后的章节呀");
//							}else if(NetworkUtil.checkWifiAndGPRS(SogouNovelActivityPager.this)){
//								pre_read_chapter();
//							}else{
//								toast.setText("亲，别翻啦到头啦~");
//							}
//							
//							return false;
//						}

						mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
						ret = mPageWidget.doTouchEvent(e);
						return ret;
					}else if (e.getAction() == MotionEvent.ACTION_MOVE){
						if(!is_touch){
							if(Math.abs(e.getX() - downx) <= 5){
								return true;
							}
							is_touch = true;
							mPageWidget.calcCornerXY(e.getX(), e.getY());
							mPageWidget.fist_move(e.getX(), e.getY());
							level = sp.getFloat("level", 1);
							pagefactory.setlevel(level);
							
							temp_curbuf = pagefactory.get_m_mbBufBegin();
							temp_prebuf = temp_curbuf;
							temp_nextbuf = temp_curbuf;
							pre_read_status = 0;
							
							pagefactory.onDraw(mCurPageCanvas);
							try {

								pagefactory.prePage();

							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							if (pagefactory.isfirstPage() && pagefactory_pre.get_isopenfile()){
								try {
									chapter_up();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								pre_read_status = 1;
							}
							temp_prebuf = pagefactory.get_m_mbBufBegin();

							pagefactory.onDraw(mPrePageCanvas);
							
							//翻章
							if(pre_read_status != 1){
								pagefactory.set_m_mbBufBegin(temp_curbuf);
								pagefactory.refreshpage();
							}else{
								try {
									pagefactory.nextPage();
								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								if (pagefactory.islastPage() && pagefactory_next.get_isopenfile()){
									chapter_down();
									pagefactory.refreshpage();
								}
								
							}
							
							
							try {
								pagefactory.nextPage();
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (pagefactory.islastPage() && pagefactory_next.get_isopenfile()){
								chapter_down();
								
								pre_read_status = 2;
								
							}
							
							temp_nextbuf = pagefactory.get_m_mbBufBegin();
							
							pagefactory.onDraw(mNextPageCanvas);
							
							if(pre_read_status != 2){
								pagefactory.set_m_mbBufBegin(temp_curbuf);
								pagefactory.refreshpage();
							}else{
								try {

									pagefactory.prePage();

								} catch (Exception e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								
								if (pagefactory.isfirstPage() && pagefactory_pre.get_isopenfile()){
									try {
										chapter_up();
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
							}

							mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
						}
						
						ret = mPageWidget.doTouchEvent(e);
						return ret;
					}else{
						if(is_touch){
							
							if(mPageWidget.canDragOver()){
								
								if(mPageWidget.DragToNext()){
									
									if(pre_read_status == 0){
										pagefactory.set_m_mbBufBegin(temp_nextbuf);
										pagefactory.refreshpage();
									}else{
										try {
											pagefactory.nextPage();
										} catch (Exception e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										if (pagefactory.islastPage() && pagefactory_next.get_isopenfile()){
											chapter_down_go();
										}
										
									}
									
								}else{
									
									if(pre_read_status == 0){
										pagefactory.set_m_mbBufBegin(temp_prebuf);
										pagefactory.refreshpage();
									}else{
										
										try {

											pagefactory.prePage();

										} catch (Exception e1) {
											// TODO Auto-generated catch block
											e1.printStackTrace();
										}
										
										if (pagefactory.isfirstPage() && pagefactory_pre.get_isopenfile()){
											try {
												chapter_up_go();
											} catch (Exception e1) {
												// TODO Auto-generated catch block
												e1.printStackTrace();
											}
										}
										
									}
									
								}
								
							
							}
							
							ret = mPageWidget.doTouchEvent(e);
							return ret;
						}else{
							mPageWidget.setCornerX_forfixbug();
							pagefactory.onDraw(mCurPageCanvas);
							mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
							mPageWidget.postInvalidate();
							edit.putInt("chapterindex_now_reading", chapterindex_now_reading);
							edit.putString("menu_book_name", book_name);
							edit.putString("menu_author_name", author_name);
							edit.commit();
							Flag_bright = false;
							Intent intent = new Intent(SogouNovelActivityPager.this,ReadMenu.class);
							startActivity(intent); 
							return false;
						}
					}
//						System.out.println("asdasdasdasd");
//						System.out.println(e.getAction());

						
					
					
					 
				}
				return false;
			}

		});
	}
	/*----------- onCreate  finish------------------*/
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(batteryLevelRcvr);
//		dbclose();
		System.out.println("onDestroy!");
		super.onDestroy();
		File temp_del_file = pagefactory.getBookFile();
		if(temp_del_file != null){
			pagefactory.Destory();
			pagefactory_pre.Destory();
			pagefactory_next.Destory();
			FileUtil.delAllFile(temp_del_file.getParent());
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		showStatusBar();
		//退出之前的自动记录
		if(book_info != null){
			chapter_basic temp_c = bookdao.getChapter_from_index(book_name, author_name, chapterindex_now_reading);
			if(temp_c != null){
				book_info.setChapter_md5(temp_c.getChapter_md5());
			}
			book_info.setChapter_index(chapterindex_now_reading);
			book_info.setBegin_buf(pagefactory.get_m_mbBufBegin());
			
			bookdao.insert_book_mark(book_info);
		}
		edit.putInt("chapterindex_now_reading", chapterindex_now_reading);
		edit.putString("book_now_reading", book_name);
		edit.putInt("book_mark", pagefactory.get_m_mbBufBegin());
		edit.commit();
		if(Flag_bright){
			BrightUtil.setBrightness(SogouNovelActivityPager.this, sp.getInt("bright_now", 76));
			if (sp.getBoolean("bright_flag", false)){
				BrightUtil.startAutoBrightness(SogouNovelActivityPager.this);
			}
		}
		Flag_bright = true;
		
		
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
//	@Override
//	protected void onNewIntent(Intent intent) {
//		if (intent != null) {
//			Bundle bundle = intent.getExtras();
//		if (bundle != null) {
//			chapter_path = bundle.getString("path");
//			path_flag = bundle.getInt("path_flag");
//			}
//		}
//		super.onNewIntent(intent);
//	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				book_info_temp = book_info;
				book_info = bundle.getParcelable("book_info");
				if (book_info == null){
					toast.setText("未知错误，请重试");
					book_info = book_info_temp;
					SogouNovelActivityPager.this.finish();
				}
			}
		}
		super.onNewIntent(intent);
	}



	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(sp.getBoolean("orientation", true)){
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else{
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		
//		System.out.println("OnResume!");
		
		
		
		menumiss();
		
		
		if(!from_read_menu){
			bright_now = BrightUtil.getScreenBrightness(SogouNovelActivityPager.this);
			if(bright_now < MINIMUM_BACKLIGHT){
				bright_now = MINIMUM_BACKLIGHT;
			}
			edit.putInt("bright_now", bright_now);
			edit.putBoolean("bright_flag", BrightUtil.isAutoBrightness(SogouNovelActivityPager.this));
			edit.commit();
		}else {
			from_read_menu = false;
			if(sp.getBoolean("menu_home", false)){
				edit.putBoolean("menu_home", false);
				edit.commit();
				Intent intent = new Intent(SogouNovelActivityPager.this,MainActivity.class);
				startActivity(intent); 
				SogouNovelActivityPager.this.finish();
			}
		}
		
		hideStatusBar();
		BrightUtil.stopAutoBrightness(SogouNovelActivityPager.this);
		if(pagefactory.getMode()){
			bright_set = sp.getInt("bright_set_day", bright_now);
			BrightUtil.setBrightness(SogouNovelActivityPager.this, bright_set);
		}else{
			bright_set = sp.getInt("bright_set_night", MINIMUM_BACKLIGHT);
			BrightUtil.setBrightness(SogouNovelActivityPager.this, bright_set);
		}
		

		
		if (first_start == 1){
			first_start = 0;
			if(Pre_read_Flag){
				if (!pre_read_chapter()){
					SogouNovelActivityPager.this.finish();
				}
			}
			return;
		}
		
		
//			Intent settingintent = getIntent();
//			Bundle extras = getIntent().getExtras(); 
//			String value = null;
//			if (extras != null)
//				value = extras.getString("path");
//			String value = settingintent.getStringExtra("path");
//			front_size_flag = sp.getInt("wordsize_flag", 0);
		change_chapter = sp.getInt("change_chapter", 0);
		//System.out.println("onResume ! chapter_path is ="+chapter_path);

//				front_size = Integer.parseInt(value);
//				System.out.println(front_size);
		pagefactory.setBgBitmap_day(BitmapFactory.decodeResource(
				this.getResources(), backgrougcolor[sp.getInt("bg_pic_day",0)]));
		pagefactory.setBgBitmap_night(BitmapFactory.decodeResource(
				this.getResources(), backgrougcolor_night[sp.getInt("bg_pic_bight",0)]));
		line_block = sp.getInt("lineblock", BLOCK_SMALL);
		pagefactory.setm_nLineSpaceing(line_block);
		front_size = sp.getInt("wordsize", DEFULAT_SIZE);
		pagefactory.changefront(front_size);
		pagefactory.refreshpage();
		level = sp.getFloat("level", 1);
		pagefactory.setlevel(level);
		
//		if(path_flag == 1){
//			path_flag = 0;
//			edit.putInt("path_flag", 0);
//			edit.commit();
//			chapter_path = sp.getString("path_now_reading", "");
//			chapterindex_now_reading = fm.get_chapter_from_path(chapter_path);
//			//System.out.println(chapter_path);
//			try {
//				pagefactory.openbook(chapter_path);
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//				toast.setText("文件不存在啊亲，去目录再挑一章吧");
//			}
//				pagefactory.refreshchapter();
//
//		}else{
//			chapterindex_now_reading = sp.getInt("chapterindex_now_reading", 1);
//			String temp_path = fm.book_dir+sp.getString("book_now_reading", "")+"/"+chapterindex_now_reading+".txt";
//			try {
//				pagefactory.openbook(temp_path);
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//				toast.setText("文件不存在啊亲，去目录再挑一章吧");
//			}
//			pagefactory.set_m_mbBufBegin(sp.getInt("book_mark", 0));
//		}
		
//		if(sp.getBoolean("jump_to_next", false)){
//			edit.putBoolean("jump_to_next", false);
//			edit.commit();
//			mPageWidget.setCornerX_forfixbug();
//			pagefactory.onDraw(mCurPageCanvas);
//			jump_to_next();
//			pagefactory.onDraw(mNextPageCanvas);
//			mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
//			mPageWidget.protect_touch(screenWidthDip-5, screenHeightDip-5);
//		}
//		if(sp.getBoolean("jump_to_pre", false)){
//			edit.putBoolean("jump_to_pre", false);
//			edit.commit();
//			mPageWidget.setCornerX_forfixbug();
//			pagefactory.onDraw(mCurPageCanvas);
//			jump_to_pre();
//			pagefactory.onDraw(mNextPageCanvas);
//			mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap);
//			mPageWidget.protect_touch(5, 5);
//		}
		if(sp.getBoolean("menu_jump_show", false)){
			edit.putBoolean("menu_jump_show", false);
			edit.commit();
			menu_jump_show();
		}
		if(sp.getBoolean("menu_composite_set_show", false)){
			edit.putBoolean("menu_composite_set_show", false);
			edit.commit();
			menu_composite_set_show();
		}


		
		mPageWidget.setCornerX_forfixbug();
		pagefactory.onDraw(mCurPageCanvas);
		mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
		mPageWidget.postInvalidate();
		
		if(change_chapter == 1){
			
			edit.putInt("change_chapter", 0);
			edit.commit();
			
			chapter_path = book_info.getChapter_path();
			chapterindex_now_reading = book_info.getChapter_index();
			
			if(fm.file_is_exists(chapter_path)){
				try {
					pagefactory.openbook(chapter_path);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					toast.setText("文件不存在啊亲，去目录再挑一章吧");

				}
				
				pagefactory.refreshchapter();
				pagefactory.onDraw(mCurPageCanvas);
				mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
				mPageWidget.postInvalidate();
			}else{
				Pre_read_Flag = false;
				if(download_cheak_3g(book_info,SET_NUM)){
//					pagefactory.refreshchapter();
					download_content(DownLoad_book,Download_num);
				}
				
			}
			
			
			if(Pre_read_Flag){
				if (!pre_read_chapter()){
					SogouNovelActivityPager.this.finish();
				}
			}
		}
		
		if(sp.getBoolean("force_refresh", false)){
			edit.putBoolean("force_refresh", false);
			edit.commit();
			if(dialog_download.isShowing()){
				dialog_download.show();
			}
			toast.setText("开始为您刷新章节内容...");
			if(NetworkUtil.checkWifiAndGPRS(this)){
				new Thread(){
					public void run(){
						DU = new DownloadUtil();
						try {
							DU.get_Bookcontext_refresh(book_info.getBook_name(), book_info.getAuthor_name(),
									book_info.getChapter_md5(), 1, SogouNovelActivityPager.this);
							handler.sendEmptyMessage(3);
						} catch (Exception e) {
							// TODO Auto-generated catch block
//							File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
//									+ FileUtil.cheak_string(author_name) + "/" + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
//									+ chapter_pre.getChapter_md5() + "_" + 1 + ".zip");
//							if (temp_f.exists()) {
//								temp_f.delete();
//								// return true;
//							}
							handler.sendEmptyMessage(-2);
							e.printStackTrace();
						} finally{
							File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
									+ FileUtil.cheak_string(author_name) + "/" + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
									+ book_info.getChapter_md5() + "_" + 1 + ".zip");
							if (temp_f.exists()) {
								temp_f.delete();
								// return true;
							}
						}
						
					}
				}.start();
				
			}else{
				toast.setText("亲，您的网络不给力啊，无法刷新章节...");
			}
			
			
		}
	
		//test book mark
//		book_basic b_test = bookdao.getBook(book_name, author_name);
//		bookdao.getBook_list();
	}
	/*---------------- onResume  finish------------------*/
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			
			//横屏
			mPageWidget.setScreen(screenHeightDip, screenWidthDip);
			mCurPageBitmap = Bitmap.createBitmap(screenHeightDip, screenWidthDip, Bitmap.Config.ARGB_8888);
			mNextPageBitmap = Bitmap
					.createBitmap(screenHeightDip, screenWidthDip, Bitmap.Config.ARGB_8888);
			mPrePageBitmap = Bitmap.createBitmap(screenHeightDip, screenWidthDip, Bitmap.Config.ARGB_8888);

			mCurPageCanvas = new Canvas(mCurPageBitmap);
			mNextPageCanvas = new Canvas(mNextPageBitmap);
			mPrePageCanvas = new Canvas(mPrePageBitmap);
			pagefactory.setScreen(screenHeightDip, screenWidthDip);
			pagefactory.refreshpage();
			mPageWidget.setCornerX_forfixbug();
			pagefactory.onDraw(mCurPageCanvas);
			mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
			mPageWidget.postInvalidate();
			return;
			
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			
        	//竖屏
			mPageWidget.setScreen(screenWidthDip, screenHeightDip);
			mCurPageBitmap = Bitmap.createBitmap(screenWidthDip, screenHeightDip, Bitmap.Config.ARGB_8888);
			mNextPageBitmap = Bitmap
					.createBitmap(screenWidthDip, screenHeightDip, Bitmap.Config.ARGB_8888);
			mPrePageBitmap = Bitmap
					.createBitmap(screenWidthDip, screenHeightDip, Bitmap.Config.ARGB_8888);

			mCurPageCanvas = new Canvas(mCurPageBitmap);
			mNextPageCanvas = new Canvas(mNextPageBitmap);
			mPrePageCanvas = new Canvas(mPrePageBitmap);
			pagefactory.setScreen(screenWidthDip, screenHeightDip);
			pagefactory.refreshpage();
			mPageWidget.setCornerX_forfixbug();
			pagefactory.onDraw(mCurPageCanvas);
			mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
			mPageWidget.postInvalidate();
			return;
        } 
		
		
	}

	
	
	private boolean pre_read_chapter(){
		if(preread_flag != 0){
			toast.setText("慢点啊亲");
			return false;
		}
		preread_flag = 1;
		
		chapterindex_pre = chapterindex_now_reading - 1;
		chapterindex_next = chapterindex_now_reading + 1;
		
		
		chapter_next = bookdao.getChapter_from_index(book_name, author_name, chapterindex_next);
		if(chapter_next == null){
			pagefactory_next.set_null();
			bookdao.set_book_needupdate(book_info);
		}else{
			if(fm.file_is_exists(chapter_next.getChapter_path())){
				try {
					pagefactory_next.openbook(chapter_next.getChapter_path());
				} catch (Exception e1) {
					pagefactory_next.set_null();
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else{
				if(download_cheak_3g_chapter(chapter_next,5)){
					download_content_chapter(Download_chapter,Download_chapter_num);
				}else{
					pagefactory_next.set_null();
				}
				
			}
		}
		
		if(chapterindex_now_reading == 1){
			pagefactory_pre.set_null();
		}else{
			chapter_pre = bookdao.getChapter_from_index(book_name, author_name, chapterindex_pre);
			if(chapter_pre != null){
				if(fm.file_is_exists(chapter_pre.getChapter_path())){
					try {
						pagefactory_pre.openbook(chapter_pre.getChapter_path());
					} catch (Exception e1) {
						pagefactory_pre.set_null();
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else{
					if(NetworkUtil.checkWifiAndGPRS(this)){
						new Thread(){
							public void run(){
								DU = new DownloadUtil();
								try {
									DU.get_Bookcontext(chapter_pre.getBook_name(), chapter_pre.getAuthor_name(),
											chapter_pre.getChapter_md5(), 1, SogouNovelActivityPager.this);
								} catch (Exception e) {
									// TODO Auto-generated catch block
//									File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
//											+ FileUtil.cheak_string(author_name) + "/" + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
//											+ chapter_pre.getChapter_md5() + "_" + 1 + ".zip");
//									if (temp_f.exists()) {
//										temp_f.delete();
//										// return true;
//									}
									e.printStackTrace();
								} finally{
									File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
											+ FileUtil.cheak_string(author_name) + "/" + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
											+ chapter_pre.getChapter_md5() + "_" + 1 + ".zip");
									if (temp_f.exists()) {
										temp_f.delete();
										// return true;
									}
								}
								handler.sendEmptyMessage(2);
							}
						}.start();
						
					}else{
						toast.setText("无网络连接，无法获取前一章内容。");
						pagefactory_pre.set_null();
					}
				}
			}else{
				pagefactory_pre.set_null();
			}
			
		}
		
		
		preread_flag = 0;
		return true;
	}
	
	private boolean pre_read_next_chapter(){
		if(preread_flag != 0){
			toast.setText("慢点啊亲");
			return false;
		}
		preread_flag = 1;
		
		chapterindex_next = chapterindex_now_reading + 1;
		
		
		chapter_next = bookdao.getChapter_from_index(book_name, author_name, chapterindex_next);
		if(chapter_next == null){
			pagefactory_next.set_null();
			bookdao.set_book_needupdate(book_info);
		}else{
			if(fm.file_is_exists(chapter_next.getChapter_path())){
				try {
					pagefactory_next.openbook(chapter_next.getChapter_path());
				} catch (Exception e1) {
					pagefactory_next.set_null();
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}else{
				if(download_cheak_3g_chapter(chapter_next,5)){
					download_content_chapter(Download_chapter,Download_chapter_num);
				}else{
					pagefactory_next.set_null();
				}
				
			}
		}
		
		preread_flag = 0;
		return true;
	}
	
	
	private boolean pre_read_pre_chapter(){
		if(preread_flag != 0){
			toast.setText("慢点啊亲");
			return false;
		}
		preread_flag = 1;
		
		chapterindex_pre = chapterindex_now_reading - 1;
		
		if(chapterindex_now_reading == 1){
			pagefactory_pre.set_null();
		}else{
			chapter_pre = bookdao.getChapter_from_index(book_name, author_name, chapterindex_pre);
			if(chapter_pre != null){
				if(fm.file_is_exists(chapter_pre.getChapter_path())){
					try {
						pagefactory_pre.openbook(chapter_pre.getChapter_path());
					} catch (Exception e1) {
						pagefactory_pre.set_null();
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else{
					if(NetworkUtil.checkWifiAndGPRS(this)){
						new Thread(){
							public void run(){
								DU = new DownloadUtil();
								try {
									DU.get_Bookcontext(chapter_pre.getBook_name(), chapter_pre.getAuthor_name(),
											chapter_pre.getChapter_md5(), 1, SogouNovelActivityPager.this);
								} catch (Exception e) {
									// TODO Auto-generated catch block
//									File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
//											+ FileUtil.cheak_string(author_name) + "/" + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
//											+ chapter_pre.getChapter_md5() + "_" + 1 + ".zip");
//									if (temp_f.exists()) {
//										temp_f.delete();
//										// return true;
//									}
									e.printStackTrace();
								} finally{
									File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
											+ FileUtil.cheak_string(author_name) + "/" + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
											+ chapter_pre.getChapter_md5() + "_" + 1 + ".zip");
									if (temp_f.exists()) {
										temp_f.delete();
										// return true;
									}
								}
								handler.sendEmptyMessage(2);
							}
						}.start();
						
					}else{
						toast.setText("无网络连接，无法获取前一章内容。");
						pagefactory_pre.set_null();
					}
				}
			}else{
				pagefactory_pre.set_null();
			}
			
		}
		
		
		preread_flag = 0;
		return true;
	}
	
	private void chapter_down(){
		pagefactory_pre.chapter_copy(pagefactory);
		pagefactory.chapter_down_copy(pagefactory_next);
		chapterindex_now_reading = chapterindex_now_reading + 1;
		if (!pre_read_next_chapter()){
			SogouNovelActivityPager.this.finish();
		}
		return;
	}
	private void chapter_down_go(){
		pagefactory_pre.chapter_copy(pagefactory);
		pagefactory.chapter_down_copy(pagefactory_next);
		toast.setText("进入下一章");
		chapterindex_now_reading = chapterindex_now_reading + 1;
		if (!pre_read_next_chapter()){
			SogouNovelActivityPager.this.finish();
		}
		return;
	}
	
	private void chapter_up() throws Exception{
		pagefactory_next.chapter_copy(pagefactory);
		pagefactory.chapter_up_copy(pagefactory_pre);
		chapterindex_now_reading = chapterindex_now_reading - 1;
		if (!pre_read_pre_chapter()){
			SogouNovelActivityPager.this.finish();
		}
		return;
	}
	private void chapter_up_go() throws Exception{
		pagefactory_next.chapter_copy(pagefactory);
		pagefactory.chapter_up_copy(pagefactory_pre);
		toast.setText("进入上一章");
		chapterindex_now_reading = chapterindex_now_reading - 1;
		if (!pre_read_pre_chapter()){
			SogouNovelActivityPager.this.finish();
		}
		return;
	}
	
	private void jump_to_pre(){
		if(pagefactory_pre.get_isopenfile()){
			pagefactory.chapter_down_copy(pagefactory_pre);
			chapterindex_now_reading = chapterindex_now_reading - 1;
			if (!pre_read_chapter()){
				SogouNovelActivityPager.this.finish();
			}
		}else{
			mPageWidget.setCornerX_forfixbug();
			pagefactory.clear_chapter();
			toast.setText("亲，没有前一章啦~");
		}
	}
	
	private void jump_to_next(){
		if(pagefactory_next.get_isopenfile()){
			pagefactory.chapter_down_copy(pagefactory_next);
			chapterindex_now_reading = chapterindex_now_reading + 1;
			if (!pre_read_chapter()){
				SogouNovelActivityPager.this.finish();
			}
		}else{
			mPageWidget.setCornerX_forfixbug();
			try {
				pagefactory.tolastpage();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			toast.setText("亲，没有后一章啦~");
		}
	}
	
	

	
	
	private void initMenu(){    

		
		//几个隐藏的拖动条
		bight_seekbar = (SeekBar) findViewById(R.id.seekbar_main_bight_set);
		jump_seekbar = (SeekBar) findViewById(R.id.seekbar_main_jump);
		jump_show = (TextView) findViewById(R.id.jump_show);
		font_down = (ImageView) findViewById(R.id.font_down);
		font_up = (ImageView) findViewById(R.id.font_up);
		block_small = (ImageView) findViewById(R.id.block_small);
		block_mid = (ImageView) findViewById(R.id.block_mid);
		block_large = (ImageView) findViewById(R.id.block_large);
		bg_set_brown = (ImageView) findViewById(R.id.bg_set_brown);
		bg_set_green = (ImageView) findViewById(R.id.bg_set_green);
		bg_set_purple = (ImageView) findViewById(R.id.bg_set_purple);
		bg_set_blue = (ImageView) findViewById(R.id.bg_set_blue);
		bg_set_gray = (ImageView) findViewById(R.id.bg_set_gray);
		sun_small = (ImageView) findViewById(R.id.menu_sun_small);
		sun_big = (ImageView) findViewById(R.id.menu_sun_big);
		
		jump_seekbar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				pagefactory.set_m_mbBufBegin_bypercent(jump_seekbar.getProgress());
				mPageWidget.setCornerX_forfixbug();
				pagefactory.onDraw(mCurPageCanvas);
				mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
				mPageWidget.postInvalidate();
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				if(jump_flag){
					jump_show.setText(progress+"%");
				}else{
					jump_flag = true;
				}
				
			}
		});
		
		
		bight_seekbar.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {
			
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				if(pagefactory.getMode()){
					edit.putInt("bright_set_day", bight_seekbar.getProgress()+30);
				}else{
					edit.putInt("bright_set_night", bight_seekbar.getProgress()+30);
				}
				edit.commit();
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				if(bight_flag){
					BrightUtil.setBrightness(SogouNovelActivityPager.this, progress+30);
				}else{
					bight_flag = true;
				}
			}
		});
		
		font_up.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sp.getInt("wordsize", DEFULAT_SIZE) <= 56){
					int font_size = sp.getInt("wordsize", DEFULAT_SIZE);
					pagefactory.changefront(font_size + 2);
					pagefactory.refreshpage();
					mPageWidget.setCornerX_forfixbug();
					pagefactory.onDraw(mCurPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
					mPageWidget.postInvalidate();
					edit.putInt("wordsize", font_size + 2);
					edit.commit();
				}
			}
		});
		
		font_down.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sp.getInt("wordsize", DEFULAT_SIZE) >= 16){
					int font_size = sp.getInt("wordsize", DEFULAT_SIZE);
					pagefactory.changefront(font_size - 2);
					pagefactory.refreshpage();
					mPageWidget.setCornerX_forfixbug();
					pagefactory.onDraw(mCurPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
					mPageWidget.postInvalidate();
					edit.putInt("wordsize", font_size - 2);
					edit.commit();
				}
			}
		});
		
		block_small.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sp.getInt("lineblock", BLOCK_SMALL) != BLOCK_SMALL){
					block_small.setImageResource(R.drawable.block_small_hover);
					block_mid.setImageResource(R.drawable.block_mid);
					block_large.setImageResource(R.drawable.block_large);
					pagefactory.setm_nLineSpaceing(BLOCK_SMALL);
					pagefactory.refreshpage();
					mPageWidget.setCornerX_forfixbug();
					pagefactory.onDraw(mCurPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
					mPageWidget.postInvalidate();
					edit.putInt("lineblock", BLOCK_SMALL);
					edit.commit();
				}
			}
		});
		
		block_mid.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sp.getInt("lineblock", BLOCK_SMALL) != BLOCK_MID){
					block_small.setImageResource(R.drawable.block_small);
					block_mid.setImageResource(R.drawable.block_mid_hover);
					block_large.setImageResource(R.drawable.block_large);
					pagefactory.setm_nLineSpaceing(BLOCK_MID);
					pagefactory.refreshpage();
					mPageWidget.setCornerX_forfixbug();
					pagefactory.onDraw(mCurPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
					mPageWidget.postInvalidate();
					edit.putInt("lineblock", BLOCK_MID);
					edit.commit();
				}
			}
		});
		
		block_large.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(sp.getInt("lineblock", BLOCK_SMALL) != BLOCK_LARGE){
					block_small.setImageResource(R.drawable.block_small);
					block_mid.setImageResource(R.drawable.block_mid);
					block_large.setImageResource(R.drawable.block_large_hover);
					pagefactory.setm_nLineSpaceing(BLOCK_LARGE);
					pagefactory.refreshpage();
					mPageWidget.setCornerX_forfixbug();
					pagefactory.onDraw(mCurPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
					mPageWidget.postInvalidate();
					edit.putInt("lineblock", BLOCK_LARGE);
					edit.commit();
				}
			}
		});
		
		bg_set_gray.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(backgrougcolor[sp.getInt("bg_pic_day",0)] != R.drawable.bg_gray){
					bg_set_gray.setImageResource(R.drawable.gray_hover);
					bg_set_blue.setImageResource(R.drawable.blue);
					bg_set_brown.setImageResource(R.drawable.brown);
					bg_set_green.setImageResource(R.drawable.green);
					bg_set_purple.setImageResource(R.drawable.purple);
					
					pagefactory.set_fontcolor_day(font_color[4]);
					
					pagefactory.setBgBitmap_day(BitmapFactory.decodeResource(
							SogouNovelActivityPager.this.getResources(), R.drawable.bg_gray));
					pagefactory.refreshpage();
					mPageWidget.setCornerX_forfixbug();
					pagefactory.onDraw(mCurPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
					mPageWidget.postInvalidate();
					edit.putInt("bg_pic_day", 0);
					edit.commit();
				}
			}
		});
		
		bg_set_blue.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(backgrougcolor[sp.getInt("bg_pic_day",0)] != R.drawable.bg_blue){
					bg_set_gray.setImageResource(R.drawable.gray);
					bg_set_blue.setImageResource(R.drawable.blue_hover);
					bg_set_brown.setImageResource(R.drawable.brown);
					bg_set_green.setImageResource(R.drawable.green);
					bg_set_purple.setImageResource(R.drawable.purple);
					
					pagefactory.set_fontcolor_day(font_color[3]);
					
					pagefactory.setBgBitmap_day(BitmapFactory.decodeResource(
							SogouNovelActivityPager.this.getResources(), R.drawable.bg_blue));
					pagefactory.refreshpage();
					mPageWidget.setCornerX_forfixbug();
					pagefactory.onDraw(mCurPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
					mPageWidget.postInvalidate();
					edit.putInt("bg_pic_day", 3);
					edit.commit();
				}
			}
		});
		
		bg_set_green.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(backgrougcolor[sp.getInt("bg_pic_day",0)] != R.drawable.bg_green){
					bg_set_gray.setImageResource(R.drawable.gray);
					bg_set_blue.setImageResource(R.drawable.blue);
					bg_set_brown.setImageResource(R.drawable.brown);
					bg_set_green.setImageResource(R.drawable.green_hover);
					bg_set_purple.setImageResource(R.drawable.purple);
					
					pagefactory.set_fontcolor_day(font_color[1]);
					pagefactory.setBgBitmap_day(BitmapFactory.decodeResource(
							SogouNovelActivityPager.this.getResources(), R.drawable.bg_green));
					pagefactory.refreshpage();
					mPageWidget.setCornerX_forfixbug();
					pagefactory.onDraw(mCurPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
					mPageWidget.postInvalidate();
					edit.putInt("bg_pic_day", 2);
					edit.commit();
				}
			}
		});
		
		bg_set_brown.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(backgrougcolor[sp.getInt("bg_pic_day",0)] != R.drawable.bg_brown){
					bg_set_gray.setImageResource(R.drawable.gray);
					bg_set_blue.setImageResource(R.drawable.blue);
					bg_set_brown.setImageResource(R.drawable.brown_hover);
					bg_set_green.setImageResource(R.drawable.green);
					bg_set_purple.setImageResource(R.drawable.purple);
					
					pagefactory.set_fontcolor_day(font_color[0]);
					pagefactory.setBgBitmap_day(BitmapFactory.decodeResource(
							SogouNovelActivityPager.this.getResources(), R.drawable.bg_brown));
					pagefactory.refreshpage();
					mPageWidget.setCornerX_forfixbug();
					pagefactory.onDraw(mCurPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
					mPageWidget.postInvalidate();
					edit.putInt("bg_pic_day", 1);
					edit.commit();
				}
			}
		});
		
		bg_set_purple.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(backgrougcolor[sp.getInt("bg_pic_day",0)] != R.drawable.bg_purple){
					bg_set_gray.setImageResource(R.drawable.gray);
					bg_set_blue.setImageResource(R.drawable.blue);
					bg_set_brown.setImageResource(R.drawable.brown);
					bg_set_green.setImageResource(R.drawable.green);
					bg_set_purple.setImageResource(R.drawable.purple_hover);
					
					pagefactory.set_fontcolor_day(font_color[2]);
					pagefactory.setBgBitmap_day(BitmapFactory.decodeResource(
							SogouNovelActivityPager.this.getResources(), R.drawable.bg_purple));
					pagefactory.refreshpage();
					mPageWidget.setCornerX_forfixbug();
					pagefactory.onDraw(mCurPageCanvas);
					mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
					mPageWidget.postInvalidate();
					edit.putInt("bg_pic_day", 4);
					edit.commit();
				}
			}
		});
		
		sun_small.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int temp_bright = bight_seekbar.getProgress() + 30;
				if(temp_bright >= 50){
					if(pagefactory.getMode()){
						edit.putInt("bright_set_day", temp_bright - 20);
					}else{
						edit.putInt("bright_set_night", temp_bright - 20);
					}
					edit.commit();
					bight_seekbar.setProgress(temp_bright - 50);
					BrightUtil.setBrightness(SogouNovelActivityPager.this, temp_bright - 20);
				}else{
					if(pagefactory.getMode()){
						edit.putInt("bright_set_day", MINIMUM_BACKLIGHT);
					}else{
						edit.putInt("bright_set_night", MINIMUM_BACKLIGHT);
					}
					edit.commit();
					bight_seekbar.setProgress(0);
					BrightUtil.setBrightness(SogouNovelActivityPager.this, MINIMUM_BACKLIGHT);
				}
				
			}
		});
		
		sun_big.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int temp_bright = bight_seekbar.getProgress() + 30;
				if(temp_bright < 235){
					if(pagefactory.getMode()){
						edit.putInt("bright_set_day", temp_bright + 20);
					}else{
						edit.putInt("bright_set_night", temp_bright + 20);
					}
					edit.commit();
					bight_seekbar.setProgress(temp_bright - 10);
					BrightUtil.setBrightness(SogouNovelActivityPager.this, temp_bright + 20);
				}else{
					if(pagefactory.getMode()){
						edit.putInt("bright_set_day", 255);
					}else{
						edit.putInt("bright_set_night", 255);
					}
					edit.commit();
					bight_seekbar.setProgress(225);
					BrightUtil.setBrightness(SogouNovelActivityPager.this, 255);
				}
				
			}
		});
		
	}
	
//	public void dbinit(){
//		dbHelper = new DatabaseHelper(SogouNovelActivity.this,"Novel_db");
//		db = dbHelper.getWritableDatabase();
//		sql = "CREATE TABLE IF NOT EXISTS `book_mark` (" +
//				"`id` INTEGER PRIMARY KEY," +
//				"`book_name` VARCHAR(255) DEFAULT ''," +
//				"`chapter_num` INT(11) DEFAULT 0," +
//				"`begin_buf` INT(11) DEFAULT 0," +
//				"`update_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00'," +
//				"`percent` VARCHAR(255) DEFAULT ''," +
//				"`type` INT(11) DEFAULT 0" +
//				")";
//		db.execSQL(sql);
//	}
	
//	public List<Map<String, Integer>> getBookMark(){
//	
//		Cursor cursor = db.query("book_mark", new String[]{"chapter_num","begin_buf"},
//				"book_name=? and type=?", new String[]{book_now_reading,"1"}, null, null, null);
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
	
	
//	public void dbclose(){
//		db.close();
//	}
	
	
	

		
	public void menumiss(){
		menu_composite_set_miss();
		menu_jump_miss();
		mPageWidget.setCornerX_forfixbug();
		pagefactory.onDraw(mCurPageCanvas);
		mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
		mPageWidget.postInvalidate();
		return;
	}
	
	public void menu_composite_set_show(){
//		if (pagefactory.getMode()){
//			menu_composite_set.setBackgroundResource(sp.getInt("bg_pic_day", R.drawable.bg_gray));
//		}else{
//			menu_composite_set.setBackgroundResource(sp.getInt("bg_pic_night", R.drawable.bg_night));
//		}
		
		
		
		if(pagefactory.getMode()){
			bight_flag = false;
			bight_seekbar.setProgress(sp.getInt("bright_set_day", bright_now) - 30);
			menu_composite_set.setBackgroundResource(R.drawable.setting_bg);
			menu_color_set.setBackgroundResource(R.drawable.color_select_bg);
		}else{
			bight_flag = false;
			bight_seekbar.setProgress(sp.getInt("bright_set_night", MINIMUM_BACKLIGHT) - 30);
			menu_composite_set.setBackgroundResource(R.drawable.setting_bg_night);
			menu_color_set.setBackgroundResource(R.drawable.color_select_bg_night);
		}
		
		
		
		switch (sp.getInt("lineblock", BLOCK_SMALL)) {
			case 10:
				block_small.setImageResource(R.drawable.block_small_hover);
				block_mid.setImageResource(R.drawable.block_mid);
				block_large.setImageResource(R.drawable.block_large);
				break;
				
			case 20:
				block_small.setImageResource(R.drawable.block_small);
				block_mid.setImageResource(R.drawable.block_mid_hover);
				block_large.setImageResource(R.drawable.block_large);
				break;
				
			case 30:
				block_small.setImageResource(R.drawable.block_small);
				block_mid.setImageResource(R.drawable.block_mid);
				block_large.setImageResource(R.drawable.block_large_hover);
				break;
	
			default:
				break;
		}
		
		
		switch (backgrougcolor[sp.getInt("bg_pic_day",0)]) {
			case R.drawable.bg_gray:
				bg_set_gray.setImageResource(R.drawable.gray_hover);
				bg_set_blue.setImageResource(R.drawable.blue);
				bg_set_brown.setImageResource(R.drawable.brown);
				bg_set_green.setImageResource(R.drawable.green);
				bg_set_purple.setImageResource(R.drawable.purple);
				break;
				
			case R.drawable.bg_brown:
				bg_set_gray.setImageResource(R.drawable.gray);
				bg_set_blue.setImageResource(R.drawable.blue);
				bg_set_brown.setImageResource(R.drawable.brown_hover);
				bg_set_green.setImageResource(R.drawable.green);
				bg_set_purple.setImageResource(R.drawable.purple);
				break;
				
			case R.drawable.bg_green:
				bg_set_gray.setImageResource(R.drawable.gray);
				bg_set_blue.setImageResource(R.drawable.blue);
				bg_set_brown.setImageResource(R.drawable.brown);
				bg_set_green.setImageResource(R.drawable.green_hover);
				bg_set_purple.setImageResource(R.drawable.purple);
				break;
			
			case R.drawable.bg_blue:
				bg_set_gray.setImageResource(R.drawable.gray);
				bg_set_blue.setImageResource(R.drawable.blue_hover);
				bg_set_brown.setImageResource(R.drawable.brown);
				bg_set_green.setImageResource(R.drawable.green);
				bg_set_purple.setImageResource(R.drawable.purple);
				break;	
	
			case R.drawable.bg_purple:
				bg_set_gray.setImageResource(R.drawable.gray);
				bg_set_blue.setImageResource(R.drawable.blue);
				bg_set_brown.setImageResource(R.drawable.brown);
				bg_set_green.setImageResource(R.drawable.green);
				bg_set_purple.setImageResource(R.drawable.purple_hover);
				break;	
			default:
				break;
		}
		
		menu_composite_set.setVisibility(View.VISIBLE);
		return;
	}
	public void menu_composite_set_miss(){
		menu_composite_set.setVisibility(View.GONE);
		return;
	}

	
	public void menu_jump_show(){
		if (pagefactory.getMode()){
			menu_jump.setBackgroundResource(R.color.jump_menu_backgroundcolor);
		}else{
			menu_jump.setBackgroundResource(R.color.read_menu_backgroundcolor_night);
		}
		jump_flag = false;
		jump_seekbar.setProgress(pagefactory.get_percent_num());
		jump_show.setText(pagefactory.get_percent_num()+"%");
		menu_jump.setVisibility(View.VISIBLE);
		return;
	}
	public void menu_jump_miss(){
		menu_jump.setVisibility(View.GONE);
		return;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stubf
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (menu_composite_set.getVisibility() == View.VISIBLE
					|| menu_jump.getVisibility() == View.VISIBLE){
//					mPageWidget.setCornerX_forfixbug();
				menumiss();
			}
			edit.putInt("chapterindex_now_reading", chapterindex_now_reading);
			edit.putString("menu_book_name", book_name);
			edit.putString("menu_author_name", author_name);
			edit.commit();
			mPageWidget.setCornerX_forfixbug();
			Flag_bright = false;
			Intent intent = new Intent(SogouNovelActivityPager.this,ReadMenu.class);
			startActivity(intent);
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (menu_composite_set.getVisibility() == View.VISIBLE
					|| menu_jump.getVisibility() == View.VISIBLE){
//					mPageWidget.setCornerX_forfixbug();
				menumiss();
				return true;
			}
			
		}
		
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP){
			if(key_down){
				key_down = false;
			}else{
				key_down = true;
				return true;
			}
			if ((!pagefactory.cheakpage_begin() || pagefactory_pre.get_isopenfile())&&(!pagefactory.cheakpage_end() || pagefactory_next.get_isopenfile())){
				mPageWidget.abortAnimation();
			}else if(!mPageWidget.mScroller.isFinished()){
				toast.setText("亲，珍爱屏幕，您慢点翻呗~");
				return true;
			}
			if (menu_composite_set.getVisibility() == View.VISIBLE
					||  menu_jump.getVisibility() == View.VISIBLE){
//					mPageWidget.setCornerX_forfixbug();
				menumiss();
			}
			if(pagefactory.cheakpage_begin() && !pagefactory_pre.get_isopenfile()){
				mPageWidget.setCornerX_forfixbug();
				pagefactory.onDraw(mCurPageCanvas);
				mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
				mPageWidget.postInvalidate();
				if(chapterindex_now_reading == 1){
					toast.setText("亲，别翻啦到头啦~");
				}else if (!NetworkUtil.checkWifiAndGPRS(SogouNovelActivityPager.this)){
					toast.setText("亲，没有网络，没法读取之前的章节呀");
				}else if(NetworkUtil.checkWifiAndGPRS(SogouNovelActivityPager.this)){
					pre_read_chapter();
				}else{
					toast.setText("亲，别翻啦到头啦~");
				}
				return true;
			}
			
			pagefactory.onDraw(mCurPageCanvas);
			try {

				pagefactory.prePage();

			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			if (pagefactory.isfirstPage() && pagefactory_pre.get_isopenfile()){
				try {
					chapter_up();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			pagefactory.onDraw(mNextPageCanvas);
			mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
			mPageWidget.protect_touch(5, 5);
			return true;
			
		}
		if(event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
			if(key_down){
				key_down = false;
			}else{
				key_down = true;
				return true;
			}
			if ((!pagefactory.cheakpage_begin() || pagefactory_pre.get_isopenfile())&&(!pagefactory.cheakpage_end() || pagefactory_next.get_isopenfile())){
				mPageWidget.abortAnimation();
			}else if(!mPageWidget.mScroller.isFinished()){
				toast.setText("亲，珍爱屏幕，您慢点翻呗~");
				return true;
			}
			if (menu_composite_set.getVisibility() == View.VISIBLE
					 || menu_jump.getVisibility() == View.VISIBLE){
//					mPageWidget.setCornerX_forfixbug();
				menumiss();
			}
			if(pagefactory.cheakpage_end() && !pagefactory_next.get_isopenfile() ){
				mPageWidget.setCornerX_forfixbug();
				pagefactory.onDraw(mCurPageCanvas);
				mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
				mPageWidget.postInvalidate();
				if(chapterindex_now_reading == bookdao.get_chapter_count(book_name, author_name)){
					toast.setText("亲，别翻啦到头啦~");
				}else if (!NetworkUtil.checkWifiAndGPRS(SogouNovelActivityPager.this)){
					toast.setText("亲，没有网络，没法读取之后的章节呀");
				}else if(NetworkUtil.checkWifiAndGPRS(SogouNovelActivityPager.this)){
					pre_read_chapter();
				}else{
					toast.setText("亲，别翻啦到头啦~");
				}
				return true;
			}
			pagefactory.onDraw(mCurPageCanvas);
			try {
				pagefactory.nextPage();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (pagefactory.islastPage() && pagefactory_next.get_isopenfile()){
				chapter_down();
			}
			pagefactory.onDraw(mNextPageCanvas);
			mPageWidget.setBitmaps(mCurPageBitmap, mNextPageBitmap,mPrePageBitmap);
			mPageWidget.protect_touch(screenWidthDip-5, screenHeightDip-5);
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
	
	
	//statusbar显示隐藏
	private void hideStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void showStatusBar() {
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
    }

    private void indicator() {
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.read_rl);
		if (0 == sp.getInt("hasIndicator2", 0)) {
			edit.putInt("hasIndicator2", 1);
			edit.commit();
			final ImageView indicator2 = new ImageView(SogouNovelActivityPager.this);
			indicator2.setBackgroundResource(R.drawable.indicator_2);
			indicator2.setVisibility(View.VISIBLE);
			indicator2.setScaleType(ScaleType.FIT_XY);
			indicator2.setAdjustViewBounds(true);
			rl.addView(indicator2, new RelativeLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			indicator2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					indicator2.setVisibility(View.GONE);
					indicator2.setClickable(false);
				}
			});
		}
	}

    private void monitorBatteryState() {
        batteryLevelRcvr = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
//                StringBuilder sb = new StringBuilder();
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int health = intent.getIntExtra("health", -1);
                level = 1;// percentage, or -1 for unknown
                if (rawlevel >= 0 && scale > 0) {
                    level = (float)rawlevel / scale;
                }
                if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
                	toast.setText("亲，电池过热啦~松开点。。握太紧了");
                }
                edit.putFloat("level", level);
                edit.commit();
            }
        };
        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelRcvr, batteryLevelFilter);
    }
    
    private boolean download_cheak_3g(book_basic bb ,int num){
    	
    	this.DownLoad_book = bb;
    	this.Download_num = num;
    	
    	if(NetworkUtil.checkWifiAndGPRS(this)){
    		
			if(NetworkUtil.CheckNetworkState(this) == 0){
				handler.sendEmptyMessage(-1);
				return false;
			}else if (NetworkUtil.CheckNetworkState(this)==2 && !Flag_3G){
				dialog_3g.show();
				return false;
			}else{
				return true;
			}
			
		}else{
			handler.sendEmptyMessage(-1);
			return false;
		}
    	
    }
    
    
    private void download_content(final book_basic bb , final int num){
    	
    	
    	if(bb != null){
    		dialog_download.show();
    		
			new Thread(){
				public void run(){
					DU = new DownloadUtil();
					try {
						DU.get_Bookcontext(bb.getBook_name(), bb.getAuthor_name(),
								bb.getChapter_md5(), num, SogouNovelActivityPager.this);
					} catch (Exception e) {
						// TODO Auto-generated catch block
//						File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
//								+  FileUtil.cheak_string(author_name) + "/" +  FileUtil.cheak_string(book_name) + "_" +  FileUtil.cheak_string(author_name) + "_"
//								+ bb.getChapter_md5() + "_" + num + ".zip");
//						if (temp_f.exists()) {
//							temp_f.delete();
//							// return true;
//						}
						handler.sendEmptyMessage(-1);
						e.printStackTrace();
					} finally{
						File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
								+  FileUtil.cheak_string(author_name) + "/" +  FileUtil.cheak_string(book_name) + "_" +  FileUtil.cheak_string(author_name) + "_"
								+ bb.getChapter_md5() + "_" + num + ".zip");
						if (temp_f.exists()) {
							temp_f.delete();
							// return true;
						}
					}
					handler.sendEmptyMessage(0);
				}
			}.start();
		}
    }
   
    
    
    
    private boolean download_cheak_3g_chapter(chapter_basic cb ,int num){
    	
    	this.Download_chapter = cb;
    	this.Download_chapter_num = num;
    	
    	if(NetworkUtil.checkWifiAndGPRS(this)){
    		
			if(NetworkUtil.CheckNetworkState(this) == 0){
				toast.setText("无网络连接，无法获取后一章内容。");
			}else if (NetworkUtil.CheckNetworkState(this)==2 && !Flag_3G){
				if(dialog_3g.isShowing()){
//					Flag_double_sure = true;
				}else{
					dialog_3g_chapter.show();
				}
				
			}else{
				return true;
			}
			
		}else{
			toast.setText("无网络连接，无法获取后一章内容。");
		}
    	
    	return false;
    }
    
    
    private void download_content_chapter(final chapter_basic cb , final int num){
    	
    	
    	if(cb != null){
    		toast.setText("开始获取后"+num+"章内容。");
    		
			new Thread(){
				public void run(){
					DU = new DownloadUtil();
					try {
						DU.get_Bookcontext(cb.getBook_name(), cb.getAuthor_name(),
								cb.getChapter_md5(), num, SogouNovelActivityPager.this);
					} catch (Exception e) {
						// TODO Auto-generated catch block
//						File temp_f = new File(FileUtil.book_temp_dir +  FileUtil.cheak_string(book_name) + "_"
//								+  FileUtil.cheak_string(author_name) + "/" +  FileUtil.cheak_string(book_name) + "_" +  FileUtil.cheak_string(author_name) + "_"
//								+ cb.getChapter_md5() + "_" + num + ".zip");
//						if (temp_f.exists()) {
//							temp_f.delete();
//							// return true;
//						}
						e.printStackTrace();
					} finally {
						File temp_f = new File(FileUtil.book_temp_dir +  FileUtil.cheak_string(book_name) + "_"
								+  FileUtil.cheak_string(author_name) + "/" +  FileUtil.cheak_string(book_name) + "_" +  FileUtil.cheak_string(author_name) + "_"
								+ cb.getChapter_md5() + "_" + num + ".zip");
						if (temp_f.exists()) {
							temp_f.delete();
							// return true;
						}
					}
					handler.sendEmptyMessage(1);
				}
			}.start();
		}
    }
    
    
}

    
