package com.sogou.sogounovel;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.sogou.R;
import com.sogou.util.ToastUtil;

public class CacheSetting extends Activity {
	RelativeLayout[] rLayouts;
	RadioButton[] rdButtons;
	SharedPreferences sp;
	Editor edit;
	private Button commitButton;
	private Button cancelButton;
	private int old_num;
	private ToastUtil toast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);

		
		
		setContentView(R.layout.cachesetting);
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		
		toast = ToastUtil.getInstance(this);
		
		old_num = sp.getInt("novelcache", 2);
		
		commitButton = (Button) findViewById(R.id.cache_button_commit);
		cancelButton = (Button) findViewById(R.id.cache_button_cancer);
		commitButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				toast.setText("设置成功");
				CacheSetting.this.finish();
			}
		});
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edit.putInt("novelcache", old_num);
				switch (old_num) {
					case 1:
						edit.putInt("Setting_download_num", 10);
						break;
						
					case 2:
						edit.putInt("Setting_download_num", 30);
						break;
						
					case 3:
						edit.putInt("Setting_download_num", 50);
						break;
	
					default:
						break;
				}
				edit.commit();
				toast.setText("已取消");
				CacheSetting.this.finish();
			}
		});
		rLayouts = new RelativeLayout[3];
		rLayouts[0] =(RelativeLayout) findViewById(R.id.rlayout1);
		rLayouts[1] =(RelativeLayout) findViewById(R.id.rlayout2);
		rLayouts[2] =(RelativeLayout) findViewById(R.id.rlayout3);
		
		rdButtons = new RadioButton[3];
		rdButtons[0] = (RadioButton) findViewById(R.id.radioButton1);
		rdButtons[1] = (RadioButton) findViewById(R.id.radioButton2);
		rdButtons[2] = (RadioButton) findViewById(R.id.radioButton3);
		
		rdButtons[0].setChecked(false);
		rdButtons[1].setChecked(false);
		rdButtons[2].setChecked(false);
		rdButtons[old_num-1].setChecked(true);
		
		rLayouts[0].setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				rdButtons[0].setChecked(true);
				rdButtons[1].setChecked(false);
				rdButtons[2].setChecked(false);
				edit.putInt("novelcache", 1);
				edit.putInt("Setting_download_num", 10);
				edit.commit();
			}
		});
		rdButtons[0].setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				rdButtons[0].setChecked(true);
				rdButtons[1].setChecked(false);
				rdButtons[2].setChecked(false);
				edit.putInt("novelcache", 1);
				edit.putInt("Setting_download_num", 10);
				edit.commit();
			}
		});

		rLayouts[1].setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				rdButtons[0].setChecked(false);
				rdButtons[1].setChecked(true);
				rdButtons[2].setChecked(false);
				edit.putInt("novelcache", 2);
				edit.putInt("Setting_download_num", 30);
				edit.commit();
			}
		});
		rdButtons[1].setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				rdButtons[0].setChecked(false);
				rdButtons[1].setChecked(true);
				rdButtons[2].setChecked(false);
				edit.putInt("novelcache", 2);
				edit.putInt("Setting_download_num", 30);
				edit.commit();
			}
		});

		rLayouts[2].setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				rdButtons[0].setChecked(false);
				rdButtons[1].setChecked(false);
				rdButtons[2].setChecked(true);
				edit.putInt("novelcache", 3);
				edit.putInt("Setting_download_num", 50);
				edit.commit();
			}
		});
		rdButtons[2].setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				rdButtons[0].setChecked(false);
				rdButtons[1].setChecked(false);
				rdButtons[2].setChecked(true);
				edit.putInt("novelcache", 3);
				edit.putInt("Setting_download_num", 50);
				edit.commit();
			}
		});
	}

}
