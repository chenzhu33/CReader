package com.sogou.receive;

import com.sogou.service.PushService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
			Intent it = new Intent().setAction(PushService.ACTION);
			//it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(it);
	}

}
