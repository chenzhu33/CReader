package com.sogou.component;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.sogou.R;

public class MyDialogBuilder {
	public static void accessDialog(Context ctx) {
		final Context context = ctx;
		final Dialog dialog = new IntroduceDialog(context,
				R.layout.dialog_clear_history, R.style.Theme_dialog);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		TextView t1 = (TextView) dialog.findViewById(R.id.dialog_title);
		TextView t2 = (TextView) dialog.findViewById(R.id.dialog_content);
		t1.setText("确认退出");
		t2.setText("您真的要退出搜狗阅读吗？");

		Button pButton = (Button) dialog.findViewById(R.id.dialog_ok);
		Button cButton = (Button) dialog.findViewById(R.id.dialog_cancer);
		pButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
				((Activity)context).finish();
			}
		});

		cButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
	
	public static Dialog rawDialog(Context ctx, String title, String content) {
		Dialog dialog = new IntroduceDialog(ctx,
				R.layout.dialog_clear_history, R.style.Theme_dialog);
		dialog.setCanceledOnTouchOutside(true);
		TextView t1 = (TextView) dialog.findViewById(R.id.dialog_title);
		TextView t2 = (TextView) dialog.findViewById(R.id.dialog_content);
		t1.setText(title);
		t2.setText(content);
		return dialog;
	}
	
	public static Dialog waitingDialog(Context ctx, String title, String content) {
		Dialog dialog = new IntroduceDialog(ctx,
				R.layout.dialog_waiting, R.style.Theme_dialog);
		dialog.setCanceledOnTouchOutside(true);
		TextView t1 = (TextView) dialog.findViewById(R.id.dialog_title);
		TextView t2 = (TextView) dialog.findViewById(R.id.dialog_content);
		t1.setText(title);
		t2.setText(content);
		return dialog;
	}
}
