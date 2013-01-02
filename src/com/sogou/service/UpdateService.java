package com.sogou.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Observable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.sogou.R;
import com.sogou.sogounovel.MainActivity;
import com.sogou.util.FileUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;


public class UpdateService extends Service {
	public static final int DOWNLOAD_ING = 0;
	public static final int DOWNLOAD_PAUSE = 1;
	public static final int DOWNLOAD_COMPLETE = 2;
	public static final int DOWNLOAD_FAIL = 3;
	public static final int DOWNLOAD_SPACE_NOT_ENOUGH = 4;
	
	private static final String SPACE_NOT_ENOUGH = "space_not_enough";
	
	public static final String FILE_KEY = "fileName";
	public static final String URL_KEY = "updateUrl";
	
	public static final String ACN_KEY = "activityClassName";
	public static final String CONTROL_KEY = "control";
	
	public static final int CONTROL_DEFAULT_VALUE = 0;
	
	private Thread downloadThread;
	private int control;
	private boolean N_FLAG = true;
	
	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//文件存储
	private FileOutputStream updateFileOs = null;
	
	private String fileAbsolutePath = null;
	 
	//通知栏
	private NotificationManager updateNotificationManager = null;
	private Notification updateNotification = null;
	//通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;
	
	private String mFileName = null;
	

	class SavedFile{
		public SavedFile(String fileName, Context mContext) throws IOException{
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				String dir = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/sogounovel/download/Version/";
				clearDownloadApk(dir);
				fileAbsolutePath = dir + fileName;
				File tmp = new File(dir);
				if(!tmp.exists()){
					tmp.mkdirs();
				}
			}else{
				fileAbsolutePath = getFilesDir().getAbsolutePath() + "/"; 
				clearDownloadApk(fileAbsolutePath);
			}
		}
		public void clearDownloadApk(String dir){
		    Log.i("clearDownloadApk", dir);
            File downloadDirFile = new File(dir);
            if(downloadDirFile.isDirectory()){
                File[] files = downloadDirFile.listFiles();
                if(files != null){
                    for(int i = 0; i < files.length; i++){
                        File file = files[i];
                        String filename = file.getName().toLowerCase();
                        if(filename.startsWith("sogounovel") && filename.endsWith("apk")){
                            Log.i("file delete", filename);
                            if(!filename.equals(mFileName.toLowerCase())){//不删除同版本文件
                            	file.delete();
                            }
                        }
                    }
                }
            }
		}
	}
	private String url;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    //创建文件
		try{
		    url = intent.getStringExtra(URL_KEY);
		    control = intent.getIntExtra("control", CONTROL_DEFAULT_VALUE);
		    
		    this.updateNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		    this.updateNotification = new Notification();
		    this.updateNotification.icon = R.drawable.ic_launcher;
		    //设置下载过程中，点击通知栏，回到主界面
		    updateIntent = new Intent(this, MainActivity.class);
		    //updateIntent = new Intent();    
		    //updateIntent = new Intent(Intent.ACTION_MAIN);  
		    //updateIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
		    //updateIntent.setComponent(new ComponentName(this.getPackageName(), this.getPackageName() + "." + intent.getStringExtra(ACN_KEY)));  
		    //updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		  
		    updatePendingIntent = PendingIntent.getActivity(this,0,updateIntent,0);
		    if(control == CONTROL_DEFAULT_VALUE){
    		    //设置通知栏显示内容
    		    updateNotification.tickerText = "开始下载";
    		    updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
    		    updateNotification.setLatestEventInfo(this,"SogouNovel更新","0%",updatePendingIntent);
    		    //发出通知
//    		    if(woWenWenYYConfig.getBoolean(Values.OUT_APPLICATION)){
//    		    	updateNotificationManager.notify(0,updateNotification);
//    		    }else{
//    		    	updateNotificationManager.cancel(0);
//    		    }
    		    updateNotificationManager.notify(0,updateNotification);
    		    
		    }
		    //开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		    if(this.downloadThread != null){
		        Log.i("this.downloadThread is alive", String.valueOf(this.downloadThread.isAlive()));
		    }
		    if(this.downloadThread == null || (this.downloadThread != null && !this.downloadThread.isAlive())){
		    	mFileName = intent.getStringExtra(FILE_KEY);
		        new SavedFile(mFileName , UpdateService.this);
		        updateRunnable upRunnable = new updateRunnable();
		        upRunnable.resume();
		    }else if(this.downloadThread != null && this.downloadThread.isAlive()){
		        super.onStartCommand(intent, flags, startId);
		    }
		}catch(Exception e){
			e.printStackTrace();
		}     
	    return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy(){
		if(updateFileOs!=null){
			try {
				updateFileOs.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private Handler updateHandler = new  Handler(){
	    @Override
	    public void handleMessage(Message msg) {
	    	
	        switch(msg.what){
	            case DOWNLOAD_COMPLETE:
	                //点击安装PendingIntent、
//	                if(woWenWenYYConfig.iskeyExist(Values.INTRO)){
//	                	woWenWenYYConfig.putBoolean(Values.INTRO, true);
//	                }
	                Uri uri = Uri.fromFile(new File(UpdateService.this.fileAbsolutePath));
	                Intent installIntent = new Intent(Intent.ACTION_VIEW);
	                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	                installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
	                
	                updatePendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installIntent, 0);               
	                //updateNotification.defaults = Notification.DEFAULT_SOUND;//铃声提醒 
	                updateNotification.flags = Notification.FLAG_AUTO_CANCEL;
	                updateNotification.setLatestEventInfo(UpdateService.this, "SogouNovel更新", "下载完成，点击安装", updatePendingIntent);
	                updateNotification.tickerText = "下载完成";
	                updateNotificationManager.notify(0, updateNotification);
	                //updateNotificationManager.cancel(0);
	                //startActivity(installIntent); 
	                //停止服务
	                stopService(updateIntent);
	                break;
	            case DOWNLOAD_FAIL:
	                //下载失败
	            	 if(control == CONTROL_DEFAULT_VALUE){
		            	updateNotification.tickerText = "下载失败";
		                updateNotification.setLatestEventInfo(UpdateService.this, "SogouNovel更新", "下载失败", updatePendingIntent);
//		                if(woWenWenYYConfig.getBoolean(Values.OUT_APPLICATION)){
//		                	updateNotificationManager.notify(0, updateNotification);
//		                }else{
//		    		    	updateNotificationManager.cancel(0);
//		    		    }
		                
		                updateNotificationManager.notify(0, updateNotification);
		                
		                stopService(updateIntent);
	            	 }
	                break;
	            case DOWNLOAD_SPACE_NOT_ENOUGH:
	                //空间不足
	            	 if(control == CONTROL_DEFAULT_VALUE){
	                    updateNotification.tickerText = "空间不足";
	                    updateNotification.setLatestEventInfo(UpdateService.this, "SogouNovel更新", "空间不足", updatePendingIntent);
//	                    if(woWenWenYYConfig.getBoolean(Values.OUT_APPLICATION)){
//	                    	updateNotificationManager.notify(0, updateNotification);
//	                    }else{
//	        		    	updateNotificationManager.cancel(0);
//	        		    }
	                    updateNotificationManager.notify(0, updateNotification);
	                    
	                    stopService(updateIntent);
	            	 }
                    break;
	            default:
	                stopService(updateIntent);
	                break;
	        }
	    }
	};
	
	public class updateRunnable extends Observable implements Runnable {
        Message message = updateHandler.obtainMessage();
        int downloadStatus = -1;
        long startTime=0;
        
        int downloadCount = 0;
        int downLoadPercent = 0;
        int currentSize = 0;
        int totalSize = 0;
        int updateTotalSize = 0;
        
        boolean isPaused = false;
        
        public void stopService(){
        	UpdateService.this.stopService(updateIntent);
        }
        
        public String getFilePath(){
        	return UpdateService.this.fileAbsolutePath;
        }
        
        public int getDownloadStatus() {
			return downloadStatus;
		}

		public void setDownloadStatus(int downloadStatus) {
			this.downloadStatus = downloadStatus;
		}

		public int getDownLoadPercent() {
			return downLoadPercent;
		}

		public void setDownLoadPercent(int downLoadPercent) {
			this.downLoadPercent = downLoadPercent;
		}

		public int getCurrentSize() {
			return currentSize;
		}

		public void setCurrentSize(int currentSize) {
			this.currentSize = currentSize;
		}

		public long getTotalSize() {
			return totalSize;
		}

		public void setTotalSize(int totalSize) {
			this.totalSize = totalSize;
		}

		public int getUpdateTotalSize() {
			return updateTotalSize;
		}

		public void setUpdateTotalSize(int updateTotalSize) {
			this.updateTotalSize = updateTotalSize;
		}
		
	    public void resume() {
	        downloadStatus = DOWNLOAD_ING;
	        stateChanged();
	        download();
	    }
	    
	    private void download() {
	        Thread thread = new Thread(this);
//	        if(VersionController.getVersionDownloadItem() != null){
//	        	this.addObserver(VersionController.getVersionDownloadItem());
//	        }
	        thread.start();
	    }

		public void run() {
            message.what = DOWNLOAD_COMPLETE;
            try{
                //下载函数，以QQ为例子
                //增加权限<uses-permission android:name="android.permission.INTERNET">;
                //long downloadSize = downloadUpdateFile("http://softfile.3g.qq.com:8080/msoft/179/1105/10753/MobileQQ1.0(Android)_Build0198.apk", updateFileOs);
                Boolean isDownloadSucc = downloadUpdateFile(url);
                if(isDownloadSucc){
                    //下载成功
                    updateHandler.sendMessage(message);
                }
            }catch(Exception ex){
                ex.printStackTrace();
                if(ex.getMessage() != null){
	                if(ex.getMessage().equals(SPACE_NOT_ENOUGH)){
	                    message.what = DOWNLOAD_SPACE_NOT_ENOUGH;
	                    //空间不足
	                    updateHandler.sendMessage(message);
	                }else{
	                    message.what = DOWNLOAD_FAIL;
	                    //下载失败
	                    updateHandler.sendMessage(message);
	                }
                }
            }
        }
        
        // Get this download's speed
        public float getSpeed() {
        	float s= (float) ((totalSize - currentSize)/(1024*1.0));
        	Calendar cal = Calendar.getInstance();
        	long elapsed = cal.getTimeInMillis();
        	elapsed = elapsed-startTime;
        	Log.i("s", String.valueOf(s));
        	Log.i("elapsed", String.valueOf(elapsed));
        	s= (float) (s / (elapsed/(1000*1.0)));
        	Log.i("speed", s + "k/s");
        	return s;
        }
        
    	public Boolean downloadUpdateFile(String downloadUrl) throws Exception {
            //这样的下载代码很多，我就不做过多的说明
            
            Boolean isDownloadSucc = false;
             
//            HttpURLConnection httpConnection = null;
            HttpClient client = null;
            InputStream is = null;
                     
            try {
            	
            	//client
            	BasicHttpParams httpParams = new BasicHttpParams();  
            	HttpConnectionParams.setConnectionTimeout(httpParams, 7000);  
            	HttpConnectionParams.setSoTimeout(httpParams, 7000);  
            	httpParams.setParameter("User-Agent", "SogouNovel");
            	
            	HttpGet httpGet = new HttpGet(downloadUrl);
    			client = new DefaultHttpClient(httpParams);
    			HttpResponse response = client.execute(httpGet);
    			StatusLine statusLine = response.getStatusLine();
    			int statusCode = statusLine.getStatusCode();
    			
    			HttpEntity entity = response.getEntity();
    			updateTotalSize += (int) entity.getContentLength();
    			
    			//connection
//                URL url = new URL(downloadUrl);
//                httpConnection = (HttpURLConnection)url.openConnection();
//                
//                httpConnection.setRequestProperty("User-Agent", "SogouNovel");
//                if(currentSize > 0) {
//                    httpConnection.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
//                    updateTotalSize = currentSize;
//                }
//                httpConnection.setConnectTimeout(7000);
//                httpConnection.setReadTimeout(7000);
//                httpConnection.connect();
//                System.out.println(httpConnection.getContentLength());
//                
                
                
//                updateTotalSize += httpConnection.getContentLength();
//                System.out.println("size is ="+updateTotalSize);

                
                /*Set<String> set = httpConnection.getHeaderFields().keySet();
                Iterator<String> iter = set.iterator();
                while(iter.hasNext()){
                	String key = iter.next();
                	Log.d("what the fuck", key + " !@#!@# " + httpConnection.getHeaderField(key) );
                }*/
                try{
                	File file = new File(fileAbsolutePath);
	                if(file.exists()){
	                	if(file.length() == updateTotalSize){
	                		downloadStatus = DOWNLOAD_COMPLETE;
	                		stateChanged();
	                        isDownloadSucc = true;
	                        Message message = updateHandler.obtainMessage();
	                        message.what = DOWNLOAD_COMPLETE;
	                        updateHandler.sendMessage(message);
	                        return isDownloadSucc;
	                	}else{ 
	                		if(currentSize == 0){
		                		if(file.length() > 0 && file.length() < updateTotalSize)
		                			file.delete();
	                		}
	                		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
	                			updateFileOs = new FileOutputStream(fileAbsolutePath, true);
	                		}else{
	                			updateFileOs = UpdateService.this.openFileOutput(mFileName, Context.MODE_APPEND);
	                		}
	                	}
	                }else{
                		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                			updateFileOs = new FileOutputStream(fileAbsolutePath, true);
                		}else{
                			updateFileOs = UpdateService.this.openFileOutput(mFileName, Context.MODE_APPEND);
                		}
	                }
                }catch(Exception e){
                	e.printStackTrace();
                }
                
                if(!FileUtil.isEnoughForDownload(Long.valueOf(updateTotalSize))){
                	downloadStatus = DOWNLOAD_FAIL;
                	stateChanged();
                    throw new Exception(SPACE_NOT_ENOUGH);
                }
                if (statusCode == 404) {
                	downloadStatus = DOWNLOAD_FAIL;
                	stateChanged();
                    throw new Exception("fail!");
                }
                if (startTime==0)
                    startTime=Calendar.getInstance().getTimeInMillis();
                
//                is = httpConnection.getInputStream();
                
                is = entity.getContent();
                
                byte buffer[] = new byte[4096];
                int readsize = 0;
                while((downloadStatus == DOWNLOAD_ING) && ((readsize = is.read(buffer)) > 0)){
                	updateFileOs.write(buffer, 0, readsize);
                    totalSize += readsize;
                    if(control == CONTROL_DEFAULT_VALUE){
                    	
                    	if(N_FLAG && updateTotalSize == -1){
                    		N_FLAG = false;
                    		updateNotification.setLatestEventInfo(UpdateService.this, "正在下载", "请稍等", updatePendingIntent);
                    		updateNotificationManager.notify(0, updateNotification);
                    	}
                    	
                        //为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
                        downLoadPercent = (int) (totalSize * 100 / updateTotalSize );
                        if((downloadCount == 0)||downLoadPercent > (downloadCount - 1)){
                        	downloadCount += 5;
                            if(downLoadPercent < 100){
                            	downloadStatus = DOWNLOAD_ING;
                            	if(totalSize > currentSize){
                            		stateChanged();
                            	}
                            	if(updateTotalSize != -1){
                            		updateNotification.setLatestEventInfo(UpdateService.this, "正在下载", String.valueOf(downLoadPercent) + "%", updatePendingIntent);
//                                  if(woWenWenYYConfig.getBoolean(Values.OUT_APPLICATION)){
//                                  	updateNotificationManager.notify(0, updateNotification);
//                                  }else{
//                      		    	updateNotificationManager.cancel(0);
//                      		    }
                            	}else{
                            		updateNotification.setLatestEventInfo(UpdateService.this, "正在下载", "请稍等", updatePendingIntent);
                            	}
                                
                                updateNotificationManager.notify(0, updateNotification);
                                
                            }else{
                            	downloadStatus = DOWNLOAD_COMPLETE;
                            	stateChanged();
                                isDownloadSucc = true;
                                //updateNotification.setLatestEventInfo(UpdateService.this, "下载完成", String.valueOf(downLoadPercent) + "%", updatePendingIntent);
                                //updateNotificationManager.notify(0, updateNotification);
                                Message message = updateHandler.obtainMessage();
                                message.what = DOWNLOAD_COMPLETE;
                                updateHandler.sendMessage(message);
                            }
                        }
                    }else{
                    	if(totalSize == updateTotalSize){
                    		downloadStatus = DOWNLOAD_COMPLETE;
                    		stateChanged();
                            isDownloadSucc = true;
                            //updateNotification.setLatestEventInfo(UpdateService.this, "下载完成", String.valueOf(downLoadPercent) + "%", updatePendingIntent);
                            //updateNotificationManager.notify(0, updateNotification);
                            Message message = updateHandler.obtainMessage();
                            message.what = DOWNLOAD_COMPLETE;
                            updateHandler.sendMessage(message);
                    	}
                    }
                    if(downloadStatus == DOWNLOAD_PAUSE){
                    	Log.i("UpdateService", "download pause");
                    	currentSize = totalSize;
                    	startTime = 0;
                    	isPaused = true;
                    	break;
                    }
                }
                if(updateTotalSize == -1){
                	downloadStatus = DOWNLOAD_COMPLETE;
                	stateChanged();
                    isDownloadSucc = true;
                    //updateNotification.setLatestEventInfo(UpdateService.this, "下载完成", String.valueOf(downLoadPercent) + "%", updatePendingIntent);
                    //updateNotificationManager.notify(0, updateNotification);
                    Message message = updateHandler.obtainMessage();
                    message.what = DOWNLOAD_COMPLETE;
                    updateHandler.sendMessage(message);
                }
            }catch(Exception e){
            	downloadStatus = DOWNLOAD_FAIL;
            	stateChanged();
            	e.printStackTrace();
            	throw new Exception("fail!");
            }finally {
                if(is != null) {
                    is.close();
                }
                if(updateFileOs != null) {
                	updateFileOs.close();
                }
//                if(httpConnection != null) {
//                    httpConnection.disconnect();
//                }
                if(client != null){
                	client.getConnectionManager().shutdown();
                }
    			

            }
            return isDownloadSucc;
        }
    	
        // Notify observers that this download's status has changed.
        private void stateChanged() {
            setChanged();
            notifyObservers();
        }
    }

	private void openFile(String fileName) {
		File file = new File(fileName);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivity(intent);
	}
}
