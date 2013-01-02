package com.sogou.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetworkUtil {
	
	public static boolean checkWifiAndGPRS(Context context) {
		// 检测网络连接
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED
							|| info[i].getState() == State.CONNECTING) {
						return true;
					}
				}
			}
		}
		return false;
		// ConnectivityManager conMan = (ConnectivityManager)
		// getSystemService(Context.CONNECTIVITY_SERVICE);
		// // mobile 3G Data Network
		// android.net.NetworkInfo.State mobile = conMan.getNetworkInfo(
		// ConnectivityManager.TYPE_MOBILE).getState();
		// // wifi
		// android.net.NetworkInfo.State wifi = conMan.getNetworkInfo(
		// ConnectivityManager.TYPE_WIFI).getState();
		// if(wifi == State.CONNECTED || wifi == State.CONNECTING)
		// return true;
		// if(mobile == State.CONNECTED || mobile == State.CONNECTING)
		// return true;
		// return false;
	}

	
	
	public static int CheckNetworkState(Context c)
    {
        ConnectivityManager manager = (ConnectivityManager)c.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if(manager == null){
        	return 0;
        } else {
        	State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
	        State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
	        //如果3G、wifi、2G等网络状态是连接的，则退出，否则显示提示信息进入网络设置界面
	        if(wifi == State.CONNECTED||wifi==State.CONNECTING)
	        	return 1;
	        if(mobile == State.CONNECTED||mobile==State.CONNECTING)
	        	return 2;
	        
		}
        
        return 0;
        
    }

	
}
