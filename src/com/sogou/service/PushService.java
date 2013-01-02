package com.sogou.service;


import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.sogou.db.BookDao;
import com.sogou.sogounovel.MainActivity;
import com.sogou.util.UpdateUtil;
import com.sogou.R;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class PushService extends Service{
	
	public static final String ACTION = "com.sogou.service.PushService";
	Timer timer;
	BookDao bd;
	TestTask task;
	boolean is_stop = false;
	Date date;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		timer = new Timer("test_timer", true);
		System.out.println("service create!");
	}
	
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		begin(5);
		System.out.println("service start!");
		
		
	}
	
	public void begin(int seconds){
		if(!is_stop){
			if(task == null){
				task = new TestTask();
			}
			timer.cancel();
			timer = new Timer("test_timer", true);
			timer.schedule(new TestTask(), seconds*1000 ,86400*1000);
			
			
			//test
//			timer.schedule(new TestTask(), seconds*1000 ,10*1000);
		}
	}
	
	class TestTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
//			Random random = new Random();
//			if( random.nextInt(10) > 7){
//				update_remind();
//			}
			update_remind();
		}
		
	}
	
	private void update_remind(){
		System.out.println("service alive!");
		
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		int hour = curDate.getHours();
		if(hour < 10 || hour > 20){
			System.out.println("service delay!");
			timer.cancel();
			timer = new Timer("test_timer", true);
			timer.schedule(new TestTask(), 14400 *1000 ,86400*1000);
//			timer.schedule(new TestTask(), 5000 ,5000);
			return;
		}
		
		List<String> notify_list = UpdateUtil.cheakupdate(this);
		if(notify_list != null && notify_list.size() != 0){
			notify(notify_list);
		}
		
	}
	
	private void notify(List<String> notify_list){
		//通知栏消息
		NotificationManager manager = (NotificationManager) getSystemService(
				 Context.NOTIFICATION_SERVICE); 
		Intent post_intent = new Intent(this,MainActivity.class);
		post_intent.putExtra("from_update_service", 1);
				 
	     //构建一个通知对象
		Notification notification = new Notification(R.drawable.ic_launcher, 
	                "来自搜狗阅读器的更新消息", System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity( 
       		 this, 
       		 0, 
       		post_intent,
             0
        );
		if(notify_list.size() >= 2){
			notification.setLatestEventInfo(getApplicationContext(),
					notify_list.get(0)+"等书更新", 
		             "快来看"+notify_list.get(0)+","+notify_list.get(1)+"...吧！", 
		             pendingIntent);
		}else{
			notification.setLatestEventInfo(getApplicationContext(),
					notify_list.get(0)+"更新", 
		             "快来看"+notify_list.get(0)+"吧！", 
		             pendingIntent);
		}
		
		
		notification.flags|=Notification.FLAG_AUTO_CANCEL; //自动终止
	    notification.defaults |= Notification.DEFAULT_SOUND; //默认声音
	    manager.notify(0, notification);
		
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		is_stop = true;
		task = null;
		timer.cancel();
		System.out.println("service stop!");
	}
	

}
