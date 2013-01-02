package com.sogou.sogounovel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sogou.R;
import com.sogou.component.IntroduceDialog;
import com.sogou.component.MyDialogBuilder;
import com.sogou.constdata.ConstData;
import com.sogou.data.book_basic;
import com.sogou.data.chapter_basic;
import com.sogou.db.BookDao;
import com.sogou.service.PushService;
import com.sogou.service.UpdateService;
import com.sogou.stat.DataSendUtil;
import com.sogou.util.DesUtil;
import com.sogou.util.NetworkUtil;
import com.sogou.util.ToastUtil;
import com.sogou.util.XmlUtil;
import com.sogou.util.ZipUtil;
import com.sogou.xmldata.VersionData;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

public class MainActivity extends TabActivity {

	private TextView t1, t2;
	private BroadcastReceiver batteryLevelRcvr;
	private IntentFilter batteryLevelFilter;
	private float level = 1;
	private SharedPreferences sp;
	private Editor edit;
	private ToastUtil toast;

	private TabHost mainTabHost;

	private List<VersionData> versionData;
	private Dialog dialog;
	private int remoteVersion;
	private int month;
	private boolean from_serive = false;
	
	private Intent intentservice;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				// progressbar.setVisibility(View.GONE);
				PackageManager packageManager = MainActivity.this
						.getPackageManager();
				PackageInfo packInfo = null;
				try {
					packInfo = packageManager.getPackageInfo(
							MainActivity.this.getPackageName(), 0);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				if (null == versionData) {
					return;
				}
				if (0 == versionData.size()) {
					return;
				}
				remoteVersion = Integer.valueOf(versionData.get(0)
						.getVersionCode());
				if (packInfo.versionCode == remoteVersion) {
					return;
				} else if (remoteVersion > packInfo.versionCode) {
					int force = versionData.get(0).getControl();
					//强制升级
					if(force == 1){
						//wifi强制下载
						if(NetworkUtil.CheckNetworkState(MainActivity.this) == 1){
							Intent updateIntent = new Intent(MainActivity.this,
									UpdateService.class);
							updateIntent.putExtra(UpdateService.FILE_KEY,
									"SogouNovel_" + remoteVersion + ".apk");
							updateIntent.putExtra(UpdateService.URL_KEY,
									versionData.get(0).getUpdate_url());
							updateIntent.putExtra(UpdateService.ACN_KEY,
									MainActivity.this.getLocalClassName());
							updateIntent.putExtra(UpdateService.CONTROL_KEY,
									versionData.get(0).getControl());
							startService(updateIntent);
						//非wifi下强制弹窗
						}else{
							dialog = new IntroduceDialog(MainActivity.this,
									R.layout.dialog_check_update, R.style.Theme_dialog);
							dialog.setCanceledOnTouchOutside(true);
							if (!dialog.isShowing()) {
								dialog.show();
							}
							TextView t1 = (TextView) dialog
									.findViewById(R.id.dialog_content_update);
							TextView t2 = (TextView) dialog
									.findViewById(R.id.dialog_content);
							t1.setText("发现新版本：" + remoteVersion);
							t2.setText(versionData.get(0).getContent().replaceAll("\n", ""));

							Button pButton = (Button) dialog
									.findViewById(R.id.dialog_ok);
							Button cButton = (Button) dialog
									.findViewById(R.id.dialog_cancer);
							pButton.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									Intent updateIntent = new Intent(MainActivity.this,
											UpdateService.class);
									updateIntent.putExtra(UpdateService.FILE_KEY,
											"SogouNovel_" + remoteVersion + ".apk");
									updateIntent.putExtra(UpdateService.URL_KEY,
											versionData.get(0).getUpdate_url());
									updateIntent.putExtra(UpdateService.ACN_KEY,
											MainActivity.this.getLocalClassName());
									updateIntent.putExtra(UpdateService.CONTROL_KEY,
											0);
									startService(updateIntent);
									if (dialog.isShowing()) {
										dialog.dismiss();
									}
								}
							});

							cButton.setOnClickListener(new OnClickListener() {
								public void onClick(View v) {
									edit.putBoolean("USER_NO_NEED_ATUO_UPDATE_CHEAK", true);
									edit.commit();
									if (dialog.isShowing()) {
										dialog.dismiss();
									}
								}
							});
						}
					}else if(sp.getBoolean("USER_NEED_ATUO_UPDATE_CHEAK",true)|| sp.getInt("USER_NEED_ATUO_UPDATE_CHEAK_MONTH",0) != month){
						//control = 0 一般升级
						dialog = new IntroduceDialog(MainActivity.this,
								R.layout.dialog_check_update, R.style.Theme_dialog);
						dialog.setCanceledOnTouchOutside(true);
						if (!dialog.isShowing()) {
							dialog.show();
						}
						TextView t1 = (TextView) dialog
								.findViewById(R.id.dialog_content_update);
						TextView t2 = (TextView) dialog
								.findViewById(R.id.dialog_content);
						t1.setText("发现新版本：" + remoteVersion);
						t2.setText(versionData.get(0).getContent().replaceAll("\n", ""));

						Button pButton = (Button) dialog
								.findViewById(R.id.dialog_ok);
						Button cButton = (Button) dialog
								.findViewById(R.id.dialog_cancer);
						pButton.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Intent updateIntent = new Intent(MainActivity.this,
										UpdateService.class);
								updateIntent.putExtra(UpdateService.FILE_KEY,
										"SogouNovel_" + remoteVersion + ".apk");
								updateIntent.putExtra(UpdateService.URL_KEY,
										versionData.get(0).getUpdate_url());
								updateIntent.putExtra(UpdateService.ACN_KEY,
										MainActivity.this.getLocalClassName());
								updateIntent.putExtra(UpdateService.CONTROL_KEY,
										versionData.get(0).getControl());
								startService(updateIntent);
								if (dialog.isShowing()) {
									dialog.dismiss();
								}
							}
						});

						cButton.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								Date cencelDate = new Date(System.currentTimeMillis());//获取当前时间
								edit.putInt("USER_NEED_ATUO_UPDATE_CHEAK_MONTH",cencelDate.getMonth());
								edit.putBoolean("USER_NEED_ATUO_UPDATE_CHEAK", false);
								edit.commit();
								if (dialog.isShowing()) {
									dialog.dismiss();
								}
							}
						});
					}
						
				}
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		toast = ToastUtil.getInstance(this);
		Intent intent;
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		monitorBatteryState();
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		month = curDate.getMonth();
		
		mainTabHost = this.getTabHost();

		intent = new Intent(this, MainNovelGrid.class);
		mainTabHost.addTab(mainTabHost.newTabSpec("小说").setIndicator("")
				.setContent(intent));

		intent = new Intent(this, MainNews.class);
		mainTabHost.addTab(mainTabHost.newTabSpec("新闻").setIndicator("")
				.setContent(intent));

		initTextView();
		// 检查更新
		check_update();
		
		//启动更新service
		intentservice = new Intent().setAction(PushService.ACTION);
		startService(intentservice);

		mainTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {
				if ("小说".equals(tabId)) {
					t1.setBackgroundResource(R.drawable.head_slider);
					t2.setBackgroundResource(R.drawable.head_slider_null);
				} else if ("新闻".equals(tabId)) {
					t2.setBackgroundResource(R.drawable.head_slider);
					t1.setBackgroundResource(R.drawable.head_slider_null);
				}

			}
		});

		ImageView bookstore = (ImageView) findViewById(R.id.main_bookstore);
		bookstore.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						BookStoreTab.class);
				MainActivity.this.startActivity(intent);
			}
		});

		ImageView main_setting = (ImageView) findViewById(R.id.main_setting);
		main_setting.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent intent = new Intent(MainActivity.this,
						GlobalSetting.class);
				MainActivity.this.startActivity(intent);
			}
		});
		
		main_setting.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						ImageGalleryActivity.class);
				MainActivity.this.startActivity(intent);
				return false;
			}
		});

		// 第一次启动
		data_init();

		// 第一次启动，向导
		indicator();
		
		
		//test
		try {
			DesUtil.testDES();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				int from_flag = bundle.getInt("from_update_service",0);
				if (from_flag == 1){
					from_serive = true;
				}
			}
		}
		super.onNewIntent(intent);
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			unregisterReceiver(batteryLevelRcvr);
		} catch (Exception e) {
			return;
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(from_serive){
			
			from_serive = false;
			mainTabHost.setCurrentTab(0);
		}
	}

	private void initTextView() {
		t1 = (TextView) findViewById(R.id.main_novel);
		t2 = (TextView) findViewById(R.id.main_news);

		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			DataSendUtil.sendData(MainActivity.this, "lx", "lx");
			mainTabHost.setCurrentTab(index);
		}
	};

	private void check_update() {
		new Thread() {
			public void run() {
				try {
					versionData = XmlUtil.getVersionXML(ConstData.version_url);
					handler.sendEmptyMessage(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void data_init() {

		if (0 == sp.getInt("isFirstStart", 0)) {
			edit.putInt("isFirstStart", 1);
			edit.commit();

			BookDao bd = BookDao.getInstance(MainActivity.this);
			
			book_basic book1 = new book_basic();
			book1.setAuthor_name("逆苍天");
			book1.setBook_md5("cad0cc595aee6361");
			book1.setBook_name("杀神");
			book1.setChapter_md5("bc2de7078f32cd7e");
			book1.setPic_path(Environment.getExternalStorageDirectory()
					+ "/sogounovel/book/5p2A56We_6YCG6IuN5aSp/" + ConstData.cover_string);
			book1.setBegin_buf(0);
			book1.setChapter_index(1);
			book1.setHas_chapterlist(0);
			book1.setIs_loc(1);
			book1.setIs_update(0);
			book1.setNeed_post(0);
			book1.setMax_md5("63a788edd7a61040");
			bd.add_book(book1);

			book1 = new book_basic();
			book1.setAuthor_name("天蚕土豆");
			book1.setBook_md5("7aad3e5152c7c98d");
			book1.setBook_name("武动乾坤");
			book1.setChapter_md5("c2dc0922275e30e4");
			book1.setPic_path(Environment.getExternalStorageDirectory()
					+ "/sogounovel/book/5q2m5Yqo5Lm+5Z2k_5aSp6JqV5Zyf6LGG/" + ConstData.cover_string);
			book1.setBegin_buf(0);
			book1.setChapter_index(1);
			book1.setHas_chapterlist(0);
			book1.setIs_loc(1);
			book1.setIs_update(0);
			book1.setNeed_post(0);
			book1.setMax_md5("faea7b8718239107");
			bd.add_book(book1);

			book1 = new book_basic();
			book1.setAuthor_name("我吃西红柿");
			book1.setBook_md5("a80787d530e1a7cc");
			book1.setBook_name("吞噬星空");
			book1.setChapter_md5("1e94ce217fa58a5f");
			book1.setPic_path(Environment.getExternalStorageDirectory()
					+ "/sogounovel/book/5ZCe5Zms5pif56m6_5oiR5ZCD6KW$57qi5p+$/" + ConstData.cover_string); 
			book1.setBegin_buf(0);
			book1.setChapter_index(1);
			book1.setHas_chapterlist(0);
			book1.setIs_loc(1);
			book1.setIs_update(0);
			book1.setNeed_post(0);
			book1.setMax_md5("973e5991337130b4");
			bd.add_book(book1);
			copycoverTosdcard();
			
			new Thread() {
				public void run() {
					try {
						copyResToSdcard();
						Unzip_bookdata();
						initchapter("吞噬星空", "我吃西红柿", 1);
						initchapter("杀神", "逆苍天", 2);
						initchapter("武动乾坤", "天蚕土豆", 3);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					} finally {
						delete_temp_file();
					}
				}
			}.start();
			
			
		}
	}

	private void indicator() {
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.main_layout);
		if (0 == sp.getInt("hasIndicator1", 0)) {
			edit.putInt("hasIndicator1", 1);
			edit.commit();
			final ImageView indicator1 = new ImageView(MainActivity.this);
			indicator1.setBackgroundResource(R.drawable.indicator_1);
			indicator1.setVisibility(View.VISIBLE);
			indicator1.setScaleType(ScaleType.FIT_XY);
			indicator1.setAdjustViewBounds(true);
			rl.addView(indicator1, new RelativeLayout.LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			indicator1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					indicator1.setVisibility(View.GONE);
					indicator1.setClickable(false);
				}
			});
		}
	}

	private void Unzip_bookdata() {
		try {
			ZipUtil.unZip_init(new File(Environment.getExternalStorageDirectory()
					+ "/sogounovel/book.zip"),
					"" + Environment.getExternalStorageDirectory()
							+ "/sogounovel");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private void copycoverTosdcard(){
		Field[] raw = R.raw.class.getFields();
		for (Field r : raw) {
			try {
				if(!r.getName().equals("book")){
					int id = getResources().getIdentifier(r.getName(), "raw",
							getPackageName());
					BufferedInputStream VideoReader = new BufferedInputStream(
							getResources().openRawResource(id));
					BufferedOutputStream bufEcrivain = null;
					if (r.getName().equals("cacheshashen")){
						File f = new File(Environment.getExternalStorageDirectory()
								+ "/sogounovel/book/5p2A56We_6YCG6IuN5aSp/");
						if (!f.exists()) {
							f.mkdirs();
						}
						String path = Environment.getExternalStorageDirectory()
								+ "/sogounovel/book/5p2A56We_6YCG6IuN5aSp/"+ConstData.cover_string;
						bufEcrivain = new BufferedOutputStream(
								(new FileOutputStream(new File(path))));
						
					}else if (r.getName().equals("cachewudong")){
						File f = new File(Environment.getExternalStorageDirectory()
								+ "/sogounovel/book/5q2m5Yqo5Lm+5Z2k_5aSp6JqV5Zyf6LGG/");
						if (!f.exists()) {
							f.mkdirs();
						}
						String path = Environment.getExternalStorageDirectory()
								+ "/sogounovel/book/5q2m5Yqo5Lm+5Z2k_5aSp6JqV5Zyf6LGG/"+ConstData.cover_string;
						bufEcrivain = new BufferedOutputStream(
								(new FileOutputStream(new File(path))));
						
					}else if (r.getName().equals("cachetunshi")){
						File f = new File(Environment.getExternalStorageDirectory()
								+ "/sogounovel/book/5ZCe5Zms5pif56m6_5oiR5ZCD6KW$57qi5p+$/");
						if (!f.exists()) {
							f.mkdirs();
						}
						String path = Environment.getExternalStorageDirectory()
								+ "/sogounovel/book/5ZCe5Zms5pif56m6_5oiR5ZCD6KW$57qi5p+$/"+ConstData.cover_string;
						bufEcrivain = new BufferedOutputStream(
								(new FileOutputStream(new File(path))));
						
					}
					
					byte[] buff = new byte[20 * 1024];
					int len;
					while ((len = VideoReader.read(buff)) > 0) {
						bufEcrivain.write(buff, 0, len);
					}
					bufEcrivain.flush();
					bufEcrivain.close();
					VideoReader.close();
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void copyResToSdcard() {

		Field[] raw = R.raw.class.getFields();
		for (Field r : raw) {
			try {
				int id = getResources().getIdentifier(r.getName(), "raw",
						getPackageName());
				if (r.getName().equals("book")) {
					File Sogoudir = new File(
							Environment.getExternalStorageDirectory()
									+ "/sogounovel/");
					if (!Sogoudir.exists()) {
						Sogoudir.mkdirs();
					}
					String path = Environment.getExternalStorageDirectory()
							+ "/sogounovel/" + r.getName() + ".zip";
					BufferedOutputStream bufEcrivain = new BufferedOutputStream(
							(new FileOutputStream(new File(path))));
					BufferedInputStream VideoReader = new BufferedInputStream(
							getResources().openRawResource(id));
					byte[] buff = new byte[20 * 1024];
					int len;
					while ((len = VideoReader.read(buff)) > 0) {
						bufEcrivain.write(buff, 0, len);
					}
					bufEcrivain.flush();
					bufEcrivain.close();
					VideoReader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void initchapter(String book_name, String author_name, int index)
			throws IOException, JSONException {
		StringBuilder builder = new StringBuilder();
		FileInputStream fr = null;
		String line;
		BookDao bd = BookDao.getInstance(MainActivity.this);
		File f = new File(Environment.getExternalStorageDirectory()
				+ "/sogounovel/book/c" + index + ".txt");
		fr = new FileInputStream(f);
		List<chapter_basic> temp_list = new ArrayList<chapter_basic>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(fr,
				"GBK"));
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		reader.close();
		JSONObject j_obj = new JSONObject(builder.toString());

		JSONArray jsonObjs = j_obj.getJSONArray("chapter");
		// String s = "";
		int j = 1;
		for (int i = 0; i < jsonObjs.length(); i++) {
			JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
			String name = jsonObj.getString("name");
			String md5_temp = jsonObj.getString("md5");
			// System.out.println("Index:"+ j + " 章节名：" + name + ",md5："
			// + md5_temp+ "\n");
			// s += "Index:"+ j + " 章节名：" + name + ",md5：" + md5_temp+
			// "\n" ;
			chapter_basic temp_c = new chapter_basic(book_name, author_name,
					name, md5_temp, j, 0);
			temp_list.add(temp_c);
			j++;
		}

		bd.insert_chapter(temp_list);
	}

	private void delete_temp_file() {

		File temp_zip = new File(Environment.getExternalStorageDirectory()
				+ "/sogounovel/book.zip");
		if (temp_zip.exists()) {
			temp_zip.delete();
		}
		temp_zip = new File(Environment.getExternalStorageDirectory()
				+ "/sogounovel/book/c1.txt");
		if (temp_zip.exists()) {
			temp_zip.delete();
		}
		temp_zip = new File(Environment.getExternalStorageDirectory()
				+ "/sogounovel/book/c2.txt");
		if (temp_zip.exists()) {
			temp_zip.delete();
		}
		temp_zip = new File(Environment.getExternalStorageDirectory()
				+ "/sogounovel/book/c3.txt");
		if (temp_zip.exists()) {
			temp_zip.delete();
		}

	}

	private void monitorBatteryState() {
		batteryLevelRcvr = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {
				int rawlevel = intent.getIntExtra("level", -1);
				int scale = intent.getIntExtra("scale", -1);
				int health = intent.getIntExtra("health", -1);
				level = 1;
				if (rawlevel >= 0 && scale > 0) {
					level = (float) rawlevel / scale;
				}
				if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
					toast.setText("手机过热....", 3000);
				}
				edit.putFloat("level", level);
				edit.commit();
			}
		};
		batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(batteryLevelRcvr, batteryLevelFilter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "设置");
		menu.add(0, 2, 2, "退出");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST:
			Intent intent = new Intent(MainActivity.this, GlobalSetting.class);
			MainActivity.this.startActivity(intent);
			break;
		case Menu.FIRST + 1:
			MyDialogBuilder.accessDialog(MainActivity.this);
			break;
		default:
			break;

		}
		return false;
	}

}