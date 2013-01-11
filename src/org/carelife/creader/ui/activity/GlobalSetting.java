package org.carelife.creader.ui.activity;

import java.util.List;

import org.carelife.creader.dao.VersionData;
import org.carelife.creader.service.UpdateService;
import org.carelife.creader.ui.adapter.GlobalSettingAapter;
import org.carelife.creader.ui.adapter.GlobalSettingFragment;
import org.carelife.creader.ui.component.IntroduceDialog;
import org.carelife.creader.util.ToastUtil;
import org.carelife.creader.R;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GlobalSetting extends Activity {
	GlobalSettingAapter gsAdapter;
	List<VersionData> versionData;
	Dialog dialog;
	int remoteVersion;
	ProgressDialog dialog_wait;
	protected boolean user_force_close = false;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case -1:
				dialog_wait.dismiss();
				ToastUtil.getInstance(GlobalSetting.this).setText("亲，您的网络不给力啊，稍后再试吧...");
				break;
			case 0:
				if(dialog_wait.isShowing()){
					dialog_wait.dismiss();
				}
				// progressbar.setVisibility(View.GONE);
				PackageManager packageManager = GlobalSetting.this
						.getPackageManager();
				PackageInfo packInfo = null;
				try {
					packInfo = packageManager.getPackageInfo(
							GlobalSetting.this.getPackageName(), 0);
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				if (null == versionData) {
					ToastUtil.getInstance(GlobalSetting.this).setText(
							"无法获取版本信息");
					return;
				}
				if (0 == versionData.size()) {
					ToastUtil.getInstance(GlobalSetting.this).setText(
							"无法获取版本信息");
					return;
				}
				remoteVersion = Integer.valueOf(versionData.get(0).getVersionCode());
				if (packInfo.versionCode == remoteVersion) {
					ToastUtil.getInstance(GlobalSetting.this).setText(
							"已经是最新版本了！");
					return;
				} else if (remoteVersion > packInfo.versionCode) {
					dialog = new IntroduceDialog(GlobalSetting.this,
							R.layout.dialog_check_update, R.style.Theme_dialog);
					dialog.setCanceledOnTouchOutside(true);
					if(!dialog.isShowing()){
						dialog.show();
					}
					TextView t1 = (TextView) dialog.findViewById(R.id.dialog_content_update);
					TextView t2 = (TextView) dialog.findViewById(R.id.dialog_content);
					t1.setText("发现新版本：" + remoteVersion);
					t2.setText(versionData.get(0).getContent().replaceAll("\n", ""));

					Button pButton = (Button) dialog.findViewById(R.id.dialog_ok);
					Button cButton = (Button) dialog.findViewById(R.id.dialog_cancer);
					pButton.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							Intent updateIntent = new Intent(GlobalSetting.this, UpdateService.class);
							updateIntent.putExtra(UpdateService.FILE_KEY, "SogouNovel_" + remoteVersion + ".apk");
							updateIntent.putExtra(UpdateService.URL_KEY, versionData.get(0).getUpdate_url());
							updateIntent.putExtra(UpdateService.ACN_KEY, GlobalSetting.this.getLocalClassName());
							updateIntent.putExtra(UpdateService.CONTROL_KEY, versionData.get(0).getControl());
							startService(updateIntent);
							if(dialog.isShowing()){
								dialog.dismiss();
							}
						}
					});

					cButton.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							if(dialog.isShowing()){
								dialog.dismiss();
							}
						}
					});
				}
		
				break;
			}
		}
	};

//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
////		requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//		setContentView(R.layout.globalsetting);
//
//		List<String> items = new ArrayList<String>();
//		items.add("预读设置");
//		items.add("自动追更");
//		items.add("检查更新");
//		items.add("意见反馈");
//		items.add("关于我们");
//
//		gsAdapter = new GlobalSettingAapter(this, items);
//
//		dialog_wait = new ProgressDialog(GlobalSetting.this);  
//		dialog_wait.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
//		dialog_wait.setTitle("检查更新");  
//		dialog_wait.setMessage("检查应用更新中，请稍后。。。");  
//		dialog_wait.setIndeterminate(false);  
//		dialog_wait.setCancelable(true);
//		dialog_wait.setOnKeyListener(new OnKeyListener() {
//
//			public boolean onKey(DialogInterface dialog, int keyCode,
//					KeyEvent event) {
//				if (keyCode == KeyEvent.KEYCODE_BACK) {
//					user_force_close = true;
//					return false;
//				}
//
//				return false;
//			}
//		});
//		dialog_wait.setCanceledOnTouchOutside(false);
//		dialog_wait.setCancelable(true);
//		
//		ListView lViem = (ListView) findViewById(R.id.global_setting);
//		lViem.setAdapter(gsAdapter);
//
//		lViem.setOnItemClickListener(new OnItemClickListener() {
//
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//
//				Intent intent = new Intent();
//				switch (arg2) {
//				case 0:
//					intent.setClass(GlobalSetting.this, CacheSetting.class);
//					GlobalSetting.this.startActivity(intent);
//					break;
//				case 1:
//					intent.setClass(GlobalSetting.this, AutoUpdate.class);
//					GlobalSetting.this.startActivity(intent);
//					break;
//				case 2:
//					if(!dialog_wait.isShowing()){
//						dialog_wait.show();
//						new Thread() {
//							public void run() {
//								
//								try {
//									versionData = XmlUtil
//											.getVersionXML(UrlHelper.version_url);
//									if(!user_force_close){
//										handler.sendEmptyMessage(0);
//									}else{
//										user_force_close = false;
//									}
//								} catch (IOException e) {
//									handler.sendEmptyMessage(-1);
//									e.printStackTrace();
//								}
//
//								
//							}
//						}.start();
//					}
//					break;
//				case 3:
//					intent.setClass(GlobalSetting.this, Advisor.class);
//					GlobalSetting.this.startActivity(intent);
//					break;
//				case 4:
//					intent.setClass(GlobalSetting.this, AboutUs.class);
//					GlobalSetting.this.startActivity(intent);
//					break;
//				default:
//					break;
//				}
//
//			}
//		});
//		
//		
//		
//		
//	}
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("设置");

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new GlobalSettingFragment())
                    .commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case android.R.id.home:
                GlobalSetting.this.finish();
                return true;
        }
        return false;
    }
}
