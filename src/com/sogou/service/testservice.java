package com.sogou.service;


import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.sogou.db.BookDao;
import com.sogou.sogounovel.MainActivity;
import com.sogou.R;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class testservice extends Service{
	
	Timer timer;
	BookDao bd;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("service create!");
	}
	
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		System.out.println("service start!");
		begin(5);

		
		
	}
	
	public void begin(int seconds){
		timer = new Timer("test_timer", true);
		timer.schedule(new TestTask(), seconds*1000 ,seconds*1000);
	}
	
	class TestTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Random random = new Random();
			if( random.nextInt(10) > 7){
				update_remind();
			}
			
		}
		
	}
	
	private void update_remind(){
		bd = BookDao.getInstance(this);
		bd.open();
		String book_name ="";
		Random random = new Random();
		switch (random.nextInt(4)) {
		case 0:
			book_name = "鹿鼎记";
			break;
		case 1:
			book_name = "西游记";
			break;
		case 2:
			book_name = "遮天";
			break;
		case 3:
			book_name = "斗破苍穹";
			break;
		default:
			book_name = "鹿鼎记";
			break;
		}
		
		bd.update_data(book_name);
		bd.close();
		notify(book_name);
		
	}
	
	private void notify(String name){
		//通知栏消息
		NotificationManager manager = (NotificationManager) getSystemService(
				 Context.NOTIFICATION_SERVICE); 
	     //构建一个通知对象
		Notification notification = new Notification(R.drawable.ic_launcher, 
	                "来自sogou阅读的"+name+"更新消息", System.currentTimeMillis());
		PendingIntent pendingIntent = PendingIntent.getActivity( 
       		 this, 
       		 0, 
             new Intent(this,MainActivity.class),
             0
        );
		
		notification.setLatestEventInfo(getApplicationContext(),
				 name+"更新", 
	             "快来看"+name+"吧", 
	             pendingIntent);
		
		notification.flags|=Notification.FLAG_AUTO_CANCEL; //自动终止
	    notification.defaults |= Notification.DEFAULT_SOUND; //默认声音
	    manager.notify(0, notification);
		
	}
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		timer.cancel();
		System.out.println("service stop!");
	}
	

}
