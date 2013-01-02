package com.sogou.stat;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.sogou.constdata.ConstData;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
//import android.util.Log;

public class DataSendUtil {
	
//	220.181.125.89:8080/LogServer/
//	http://app.m.sogou.com/app/uploadserver.jsp
//	private final static String uploadUrl="http://app.m.sogou.com/app/uploadserver_novel.jsp";//bug test http://220.181.124.78/app/uploadserver.jsp
	
	
//	private final static String uploadUrl="http://10.12.9.232/app/uploadserver_novel.jsp";
	
//	http://10.129.32.37:8080/SogouSearchServer/uploadserver.jsp
	private final static String uploadFile="/mnt/sdcard/SogouSearch/ini/";
//	private final static String uploadZip = "/mnt/sdcard/SogouSearch/ini/searchlog.zip";
//	private final static String uploadZip = "/mnt/sdcard/SogouSearch/ini/";
	private static String filename;
	private static SharedPreferences mPerferences;
	public static Context contexts;
	private static Editor edit;
	public synchronized static void sendData(final Context context){
		System.out.println("first activate");
		mPerferences = context.getSharedPreferences("sogou",Context.MODE_PRIVATE);
		edit = mPerferences.edit();
		{
			String networktype="";
			networktype = getNetType(context);
			if(null == networktype)
				networktype = "exception";
			TelephonyManager telphone = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			if(telphone != null){
				filename = telphone.getDeviceId()+";activate";
			}
			else{
				long imei = System.currentTimeMillis();
				filename = imei+";activate.log";
				edit.putLong("imei", imei);
				edit.commit();
			}
//			System.out.println("file name"+filename);
			if(filename.contains("null"))
				filename = Installation.id(context)+";activate";
			if(filename == null || (filename!=null && filename.equals(""))){
				long imei = System.currentTimeMillis();
				if(mPerferences.getLong("imei", -1) == -1){
					edit.putLong("imei", imei);
					edit.commit();
				}
				filename = imei+"";
			}
//			System.out.println(filename);
			OutputStream out = null;
//			if(!log.exists())
				try {
					out = context.openFileOutput(filename, Context.MODE_PRIVATE);
					String appVer = context.getPackageManager()
					.getPackageInfo(
							"com.sogou.activity.src",
							0).versionName;
					out.write(appVer.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.BRAND.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.MODEL.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.DEVICE.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.PRODUCT.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.VERSION.SDK.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.VERSION.RELEASE.getBytes());
					out.write(";".getBytes());
//					out.write(android.os.Build.BRAND.getBytes());
//					out.write(";".getBytes());
					if(filename != null && !filename.equals("searchlog.log"))
						out.write(filename.getBytes());
					out.write(";".getBytes());
					out.write(networktype.getBytes());
//					out.write(";".getBytes());
//					out.write(";0".getBytes());   //标示 渠道号 yingyonghui
					ApplicationInfo appInfo = null;
					try{
					appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
					String eid = ""+appInfo.metaData.getInt("CHANNEL");
					out.write((";"+eid).getBytes());
//					Log.i("DataUpload eid",eid);
//					System.out.println("eid:"+eid);
					}catch(NameNotFoundException err){
					err.printStackTrace();
					}
					//yingyonghui  androidmarket jifeng sohu anzhimarket 

					out.write("\r\n".getBytes());
					new Thread(){
						public void run(){
							String boundary = "*****";
							try{
								URL url = new URL(ConstData.uploadUrl);
								HttpURLConnection connection = (HttpURLConnection) url.openConnection();
								connection.setDoInput(true); 
								connection.setDoOutput(true);
								connection.setUseCaches(false);
								connection.setRequestMethod("POST");
								connection.setRequestProperty("Connection", "Kepp-Alive");
								connection.setRequestProperty("Charset", "UTF-8");
								connection.setRequestProperty("filename",filename);
								connection.setRequestProperty("Content-type", "multipart/form-data;boundary="+boundary);
								DataOutputStream out = new DataOutputStream(connection.getOutputStream());
//								if(!log.exists())
//									return;
//								FileInputStream in = new FileInputStream(log);
								FileInputStream in = context.openFileInput(filename);
								byte[] buffer = new byte[4096];
								int length = -1;
								while( (length = in.read(buffer)) != -1){
									out.write(buffer,0,length);
								}
								in.close();
								out.flush();
								//获取respond 内容
								InputStream is = connection.getInputStream();
								int ch;
								StringBuffer sb = new StringBuffer();
								while((ch =is.read())!=-1){
									sb.append((char)ch);
								}
								if(sb.toString().trim().contains("success")){
//									File uploadFile = new File("/mnt/sdcard/SogouSearch/ini/"+filename);
//									if(uploadFile.exists())
//									{
//										uploadFile.delete();
//										uploadFile.deleteOnExit();
									if(checkPrivateExist(context, filename))
										context.deleteFile(filename);//删除私有文件
//									}
								}else{
									//不成功就删除，成功才不发
								}
							}catch(Exception e){
								e.printStackTrace();
							}
						}
					}.start();
				} catch (IOException e) {
					e.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					try {
						if(null != out)
							out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
		}
	}
	
	public synchronized static void sendData(final Context context,String parentId,String content){
//		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
//		{
		mPerferences = context.getSharedPreferences("sogounovel",Context.MODE_PRIVATE);
		edit = mPerferences.edit();
			String networktype="";
//			File file = new File("/mnt/sdcard/SogouSearch/ini/");
//			if(!file.exists()){
//				System.out.println("创建文件");
//				file.mkdirs();
//			}
			networktype = getNetType(context);
			if(null == networktype)
				networktype = "exception";
			TelephonyManager telphone = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			if(telphone != null){
				filename = telphone.getDeviceId();
//				networktype = "networktype:"+telphone.getNetworkType();
			}
			else{
				long imei = System.currentTimeMillis();
				if(mPerferences.getLong("imei", -1) == -1){
					edit.putLong("imei", imei);
					edit.commit();
				}
				filename = imei+"";
			}
			if(filename == null || filename.contains("null"))
				filename = Installation.id(context);
			if(filename == null || (filename!=null && filename.equals(""))){
				long imei = System.currentTimeMillis();
				if(mPerferences.getLong("imei", -1) == -1){
					edit.putLong("imei", imei);
					edit.commit();
				}
				filename = imei+"";
			}
//			File log = new File(uploadFile+filename+".log");
			OutputStream out = null;
//			if(!log.exists())
			if(!checkPrivateExist(context,filename))
				try {
//					log.createNewFile();
//					out = new FileOutputStream(log);
					
					out = context.openFileOutput(filename,Context.MODE_PRIVATE);
					String appVer = context.getPackageManager()
					.getPackageInfo(
							"com.sogou",
							0).versionName;
//					System.out.println("appVer =" + appVer);
//					System.out.println("BRAND =" + android.os.Build.BRAND);
//					System.out.println("MODEL =" + android.os.Build.MODEL);
//					System.out.println("filename is =" + filename);
//					String eid = ""+context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA).metaData.getInt("CHANNEL");
//					System.out.println("eid is ="+ eid);
					
					
					
					out.write(appVer.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.BRAND.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.MODEL.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.DEVICE.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.PRODUCT.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.VERSION.SDK.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.VERSION.RELEASE.getBytes());
					out.write(";".getBytes());
					out.write(android.os.Build.BRAND.getBytes());
					out.write(";".getBytes());
	//				TelephonyManager telphone = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	//				if(telphone != null){
	//				out.write(telphone.getDeviceId().getBytes());
//					if(filename != null && !filename.equals("searchlog.log"))
						out.write(filename.getBytes());
					out.write(";".getBytes());
					out.write(networktype.getBytes());
//					out.write(";0".getBytes());   //标示 渠道号 yingyonghui
					ApplicationInfo appInfo = null;
					try{
					appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),PackageManager.GET_META_DATA);
					String eid = ""+appInfo.metaData.getInt("CHANNEL");
					out.write((";"+eid).getBytes());
//					System.out.println("eid:"+eid);
//					Log.i("DataUpload eid",eid);
					}catch(NameNotFoundException err){
					err.printStackTrace();
					}
					//yingyonghui  androidmarket jifeng sohu anzhimarket 
					out.write("\r\n".getBytes());
					
					out.write(parentId.getBytes());
					out.write(";".getBytes());
					out.write(content.getBytes());
					out.write(";".getBytes());
					out.write(String.valueOf(System.currentTimeMillis()).getBytes());
					out.write("\r\n".getBytes());
				} catch (IOException e) {
					// TODO Auto-generated catch block
//					System.out.println("exception from datasend");
					e.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					try {
						if(null != out)
							out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
//						System.out.println("exception from datasend");
						e.printStackTrace();
					}
				}
				else{
					//文件大于50KB不保存
					/*
					if(log.length()>512000){
						new Thread(){
							public void run(){
								uploadLog(context);
							}
						}.start();
						return;
					}
					*/
					try {
//						out = new FileOutputStream(log,true);
						out = context.openFileOutput(filename, Context.MODE_APPEND);//追加
						if(parentId !=null && content!=null){
							out.write(parentId.getBytes());
							out.write(";".getBytes());
							out.write(content.getBytes());
							out.write(";".getBytes());
							out.write(String.valueOf(System.currentTimeMillis()).getBytes());
							out.write("\r\n".getBytes());
						}
						if(getPrivateFileLength(context,filename) > 512000){
							new Thread(){
								public void run(){
									if(contexts == null)
										contexts = context;
									uploadLog();
								}
							}.start();
							return;
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
//						System.out.println("exception from datasend");
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
//						System.out.println("exception from datasend");
						e.printStackTrace();
					}finally{
						try {
							if(null != out)
							out.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
//							System.out.println("exception from datasend");
						}
					}
				}
			//文件大于50KB上传
				/*
			if(log.length()>51200)
			{
				new Thread(){
					public void run(){
						uploadLog(context);
					}
				}.start();
			}
			*/
//		}
	}
	
	
	/**
     * 判断私有文件是否存在
     * @return
     */
    public static boolean checkPrivateExist(Context context,String fileName){
//    	System.out.println("检测是否存在");
            String [] fileNameArray = context.fileList();
            for(int i = 0; i< fileNameArray.length; i++){
                    if(fileNameArray[i].equals(fileName)){
//                    	System.out.println("存在");
                            return true;
                    }
            }
//        	System.out.println("不存在");
            return false;
    }
    
    public long getPrivateLength(Context context,String fileName){
    	 String [] fileNameArray = context.fileList();

         for(int i = 0; i< fileNameArray.length; i++){
                 if(fileNameArray[i].equals(fileName)){
                 }
         }
         return 0;
    }
    
    
    public static void zipPrivateFile(Context context,String fileName) throws Exception {  
    	try{
//        File f = new File(filePath);   
//        if(!f.exists())
//        	return;
    		if(!checkPrivateExist(context, fileName))
    			return;
    		
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmssSSS");
	    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String time_str = formatter.format(curDate);
        FileInputStream fis = context.openFileInput(fileName);   
        BufferedInputStream bis = new BufferedInputStream(fis);   
        byte[] buf = new byte[1024];   
        int len;   
        FileOutputStream fos = context.openFileOutput(fileName+".zip", Context.MODE_PRIVATE); 
        BufferedOutputStream bos = new BufferedOutputStream(fos);   
        ZipOutputStream zos = new ZipOutputStream(bos);//压缩包   
        ZipEntry ze = new ZipEntry(fileName+"_"+time_str+".log");//这是压缩包名里的文件名 ,linux下unzip后可唯一 
        zos.putNextEntry(ze);// 写入新的ZIP文件条目并将流定位到条目数据的开始处   
        while ((len = bis.read(buf)) != -1) {   
            zos.write(buf, 0, len);   
            zos.flush();   
        }   
        bis.close();   
        zos.close();   
    	}catch(Exception err){
    		err.printStackTrace();
    	}
    }   
    
	/**   
     * 压缩单一个文件   
     * @param zipPath   生成的zip文件路径   
     * @param filePath  需要压缩的文件路径   
     * @throws Exception   
     */  
    public static void zipFile(String zipPath, String filePath) throws Exception {  
    	try{
    		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmssSSS");
    	    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
    		String time_str = formatter.format(curDate);
	        File f = new File(filePath);   
	        if(!f.exists())
	        	return;
	        FileInputStream fis = new FileInputStream(f);   
	        BufferedInputStream bis = new BufferedInputStream(fis);   
	        byte[] buf = new byte[1024];   
	        int len;   
	        FileOutputStream fos = new FileOutputStream(zipPath);   
	        BufferedOutputStream bos = new BufferedOutputStream(fos);   
	        ZipOutputStream zos = new ZipOutputStream(bos);//压缩包   
	        ZipEntry ze = new ZipEntry(f.getName()+"_"+time_str);//这是压缩包名里的文件名 ,linux下unzip后可唯一 
	        System.out.println("entry name is ="+f.getName()+"_"+time_str+".log");
	        zos.putNextEntry(ze);// 写入新的ZIP文件条目并将流定位到条目数据的开始处   
	  
	        while ((len = bis.read(buf)) != -1) {   
	            zos.write(buf, 0, len);   
	            zos.flush();   
	        }   
	        bis.close();   
	        zos.close();   
    	}catch(Exception err){
    		err.printStackTrace();
    	}
    }   
    /**
     * post 至服务器
     */
    
    /**
     * 获取私有文件大小
     * @param context
     * @param fileName
     * @return
     */
    public static long getPrivateFileLength(Context context,String fileName){
    	if(checkPrivateExist(context,fileName)){
    		return new File(context.getFilesDir().getAbsolutePath()+"/"+fileName).length();
    	}
    	return -1;
    }
	public static void uploadLog(){
		try{
		TelephonyManager telphone = (TelephonyManager)contexts.getSystemService(Context.TELEPHONY_SERVICE);
		if(filename == null){
			if(telphone != null){
				filename = telphone.getDeviceId();
			}
			else{
				if(contexts != null){
				mPerferences = contexts.getSharedPreferences("sogou",Context.MODE_PRIVATE);
				edit = mPerferences.edit();
				long imei = System.currentTimeMillis();
				if(mPerferences.getLong("imei", -1) == -1){
					edit.putLong("imei", imei);
				}
				filename = imei+"";
				}else
					filename = "searchlog";
			}
		}
		if(checkPrivateExist(contexts, filename))
			doUpload(contexts, filename);
//		else
//			System.out.println("bu cun zai");
			
		/*
		File log = new File(uploadFile+filename+".log");
		if(log.exists() && log.length() >0){
			doUpload(contexts,filename);
//			System.out.println("日志文件存在，准备上传");
		}else{
//			System.out.println("不上传日志文件");
		}
		*/
		}catch(Exception err){
			err.printStackTrace();
		}
		
	}
	 /**
     * post 至服务器
     */
	public static void uploadLog(Context context){
		
		try{
		TelephonyManager telphone = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		if(filename == null){
			if(telphone != null){
				filename = telphone.getDeviceId();
			}
			else{
				if(context != null){
				mPerferences = context.getSharedPreferences("sogou",Context.MODE_PRIVATE);
				edit = mPerferences.edit();
				long imei = System.currentTimeMillis();
				if(mPerferences.getLong("imei", -1) == -1){
					edit.putLong("imei", imei);
				}
				filename = imei+"";
				}else
					filename = "searchlog";
			}
		}
		if(checkPrivateExist(context, filename))
			doUpload(context, filename);
//		else
//			System.out.println("bu cun zai");
			
		/*
		File log = new File(uploadFile+filename+".log");
		if(log.exists() && log.length() >0){
			doUpload(contexts,filename);
//			System.out.println("日志文件存在，准备上传");
		}else{
//			System.out.println("不上传日志文件");
		}
		*/
		}catch(Exception err){
			err.printStackTrace();
		}
		
	
	}
	public synchronized static void doUpload(Context context,String name){
//		System.out.println("上传");
		try{
//		TelephonyManager telphone = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
//		if(telphone != null){
//			filename = telphone.getDeviceId()+".log";
//		}
//		else{
//			filename = "searchlog.log";
//		}
		try {
//			zipFile(uploadZip+name+".zip",uploadFile+name+".log");
			zipPrivateFile(context,name);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
//			System.out.println("exception from datasend");
			e1.printStackTrace();
		}
//		System.out.println("开始上传");
//		String end = "\r\n";
//		String twoHyphens = "--";
		String boundary = "*****";
		try{
			URL url = new URL(ConstData.uploadUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true); 
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Connection", "Kepp-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("filename",name);
			connection.setRequestProperty("Content-type", "multipart/form-data;boundary="+boundary);
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());
//			File file = new File(uploadZip+name+".zip");
//			if(!file.exists())
//				return;
//			FileInputStream in = new FileInputStream(file);
			if(!checkPrivateExist(context, name+".zip")){
//				System.out.println("压缩包不存在");
				return;
			}
				
			FileInputStream in = context.openFileInput(name+".zip");
			byte[] buffer = new byte[4096];
			int length = -1;
			while( (length = in.read(buffer)) != -1){
				out.write(buffer,0,length);
			}
			in.close();
			out.flush();
			
			//获取respond 内容
			InputStream is = connection.getInputStream();
			int ch;
			StringBuffer sb = new StringBuffer();
			while((ch =is.read())!=-1){
				sb.append((char)ch);
			}
			if(sb.toString().trim().contains("success")){
//				System.out.println("success and delete!");
				//应该是返回成功再删除
				context.deleteFile(name);
				context.deleteFile(name+".zip");
				/*
				File uploadZipFile = new File(uploadZip+filename+".zip");
				File uploadLogFile = new File(uploadFile+filename+".log"); 
				if(uploadZipFile.exists())
				{
					uploadZipFile.delete();
					uploadZipFile.deleteOnExit();
				}
				if(uploadLogFile.exists()){
					uploadLogFile.delete();
					uploadLogFile.deleteOnExit();
				}
				*/
			}
//			else
//				System.out.println("fail upload");
			
		}catch(Exception e){
//			System.out.println("exception from datasend");
			e.printStackTrace();
		}finally{
		}
		}catch(Exception err){
			err.printStackTrace();
		}
	}
	public void deleteLog(){
		
	}
	
	/**
	 * get current connected network type
	 * @return
	 */
	public static String getNetType(Context context){
		String type=null;
		 ConnectivityManager conMan = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		 NetworkInfo info = conMan.getActiveNetworkInfo();
		 if(info !=null ) //TYPE_MOBILE
		 {
			 switch(info.getType()){
			 case ConnectivityManager.TYPE_MOBILE:
				 switch(info.getSubtype()){
				 case  TelephonyManager.NETWORK_TYPE_EDGE:
					 type = "EDGE";
					 break;
				 case  TelephonyManager.NETWORK_TYPE_CDMA:
					 type = "CDMA";
					 break;
				 case  TelephonyManager.NETWORK_TYPE_GPRS:
					 type = "GPRS";
					 break;
				 case  TelephonyManager.NETWORK_TYPE_EVDO_0:
					 type = "EVDO_0";
					 break;
				 case  TelephonyManager.NETWORK_TYPE_UNKNOWN:
					 type = "UNKOWN";
					 break;
				 }
				 break;
			 case ConnectivityManager.TYPE_WIFI:
				 type = "wifi";
				 break;
			 }
		 }else
			 type = "outofnetwork";
		return type;
	}
}

