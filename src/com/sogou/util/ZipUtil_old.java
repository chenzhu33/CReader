package com.sogou.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import android.util.Log;

public class ZipUtil_old {
	
	
	/**
     * 解压缩功能.
     * 将zipFile文件解压到folderPath目录下.
     * @throws Exception
 */
     public int upZipFile(File zipFile, String folderPath)throws ZipException,IOException {
     //public static void upZipFile() throws Exception{
         ZipFile zfile=new ZipFile(zipFile,"GBK");
         Enumeration zList=zfile.getEntries();
         ZipEntry ze=null;
         byte[] buf=new byte[1024];
         while(zList.hasMoreElements()){
             ze=(ZipEntry)zList.nextElement();    
             if(ze.isDirectory()){
                 Log.d("upZipFile", "ze.getName() = "+ze.getName());
                 String dirstr = folderPath + ze.getName();
                 //dirstr.trim();
//                 dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                 Log.d("upZipFile", "str = "+dirstr);
                 File f=new File(dirstr);
                 f.mkdir();
                 continue;
             }
             Log.d("upZipFile", "ze.getName() = "+ze.getName());
             OutputStream os= new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
             InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
             int readLen=0;
             while ((readLen=is.read(buf, 0, 1024))!=-1) {
                 os.write(buf, 0, readLen);
             }
             is.close();
             os.close();    
         }
         zfile.close();
         Log.d("upZipFile", "finishssssssssssssssssssss");
         return 0;
     }
 
     /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     * @param baseDir 指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
 */
     public static File getRealFileName(String baseDir, String absFileName) throws UnsupportedEncodingException{
         String[] dirs=absFileName.split("/");
         File ret=new File(baseDir);
         String substr = null;
         if(dirs.length>1){
             for (int i = 0; i < dirs.length-1;i++) {
                 substr = dirs[i];
                 ret=new File(ret, substr);
                 
             }
             Log.d("upZipFile", "1ret = "+ret);
             if(!ret.exists())
                 ret.mkdirs();
             substr = dirs[dirs.length-1];
             //substr.trim();
//                 substr = new String(substr.getBytes("8859_1"), "GB2312");
			 Log.d("upZipFile", "substr = "+substr);
             
             ret=new File(ret, substr);
             Log.d("upZipFile", "2ret = "+ret);
             return ret;
         }
         return ret;
     }
	

}
