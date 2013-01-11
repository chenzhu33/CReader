package org.carelife.creader.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.carelife.creader.dao.UrlHelper;


public class ZipUtil {

     private static void createDirectory(String directory, String subDirectory) {
 	    String dir[];
 	    File fl = new File(directory);
 	    try {
 	      if (subDirectory == "" && fl.exists() != true)
 	        fl.mkdir();
 	      else if (subDirectory != "") {
 	        dir = subDirectory.replace('\\', '/').split("/");
 	        for (int i = 0; i < dir.length; i++) {
 	          File subFile = new File(directory + File.separator + dir[i]);
 	          if (subFile.exists() == false)
 	            subFile.mkdir();
 	          directory += File.separator + dir[i];
 	        }
 	      }
 	    }
 	    catch (Exception ex) {
 	      System.out.println(ex.getMessage());
 	    }
 	  }

 	public static void unZip(File zipFileName, String outputDirectory) throws Exception {
 	    try {
 	      org.apache.tools.zip.ZipFile zipFile = new org.apache.tools.zip.ZipFile(zipFileName,"GBK");
 	      java.util.Enumeration e = zipFile.getEntries();
 	      org.apache.tools.zip.ZipEntry zipEntry = null;
 	      createDirectory(outputDirectory, "");
 	      while (e.hasMoreElements()) {
 	        zipEntry = (org.apache.tools.zip.ZipEntry) e.nextElement();
// 	        System.out.println("unziping " + zipEntry.getName());
 	        if (zipEntry.isDirectory()) {
 	          String name = zipEntry.getName();
 	          name = name.substring(0, name.length() - 1);
 	          File f = new File(outputDirectory + File.separator + name);
 	          f.mkdir();
// 	          System.out.println("创建目录：" + outputDirectory + File.separator + name);
 	        }
 	        else {
 	          String fileName = zipEntry.getName();
 	          fileName = fileName.replace('\\', '/');
 	         // System.out.println("测试文件1：" +fileName);
 	          if (fileName.indexOf("/") != -1)
 	          {
 	              createDirectory(outputDirectory,
 	                              fileName.substring(0, fileName.lastIndexOf("/")));
 	              fileName=fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
 	          }

 	                   File f = new File(outputDirectory + File.separator + zipEntry.getName());
 	          //不覆盖文件
 	          if(!f.createNewFile()){
 	        	  continue;
 	          }
 	          
 	          InputStream in = zipFile.getInputStream(zipEntry);
 	          OutputStream out = new FileOutputStream(f);
 	          DesUtil ds = new DesUtil(UrlHelper.key_string);
 	          ds.encryptFile(in, out);
// 	          FileOutputStream out=new FileOutputStream(f);

// 	          byte[] by = new byte[1024];
// 	          int c;
// 	          while ( (c = in.read(by)) != -1) {
// 	            out.write(by, 0, c);
// 	          }
// 	          out.close();
// 	          in.close();
 	        }
 	      }
 	       }
 	    catch (Exception ex) {
 	      System.out.println(ex.getMessage());
 	    }
 	        
 	    }
     
 	public static void unZip_init(File zipFileName, String outputDirectory) throws Exception {
 	    try {
 	      org.apache.tools.zip.ZipFile zipFile = new org.apache.tools.zip.ZipFile(zipFileName,"GBK");
 	      java.util.Enumeration e = zipFile.getEntries();
 	      org.apache.tools.zip.ZipEntry zipEntry = null;
 	      createDirectory(outputDirectory, "");
 	      while (e.hasMoreElements()) {
 	        zipEntry = (org.apache.tools.zip.ZipEntry) e.nextElement();
// 	        System.out.println("unziping " + zipEntry.getName());
 	        if (zipEntry.isDirectory()) {
 	          String name = zipEntry.getName();
 	          name = name.substring(0, name.length() - 1);
 	          File f = new File(outputDirectory + File.separator + name);
 	          f.mkdir();
// 	          System.out.println("创建目录：" + outputDirectory + File.separator + name);
 	        }
 	        else {
 	          String fileName = zipEntry.getName();
 	          fileName = fileName.replace('\\', '/');
 	         // System.out.println("测试文件1：" +fileName);
 	          if (fileName.indexOf("/") != -1)
 	          {
 	              createDirectory(outputDirectory,
 	                              fileName.substring(0, fileName.lastIndexOf("/")));
 	              fileName=fileName.substring(fileName.lastIndexOf("/")+1,fileName.length());
 	          }

 	                   File f = new File(outputDirectory + File.separator + zipEntry.getName());
 	          //不覆盖文件
 	          if(!f.createNewFile()){
 	        	  continue;
 	          }
 	          
 	          InputStream in = zipFile.getInputStream(zipEntry);
 	          FileOutputStream out=new FileOutputStream(f);
 	          if(fileName.endsWith("c1.txt")||fileName.endsWith("c2.txt")||fileName.endsWith("c3.txt")){
 	        	  byte[] by = new byte[1024];
	 	          int c;
	 	          while ( (c = in.read(by)) != -1) {
	 	            out.write(by, 0, c);
	 	          }
	 	          out.close();
	 	          in.close();
 	          }else{
 	        	 DesUtil ds = new DesUtil(UrlHelper.key_string);
 	        	 ds.encryptFile(in, out);
 	          }
 	          

// 	          byte[] by = new byte[1024];
// 	          int c;
// 	          while ( (c = in.read(by)) != -1) {
// 	            out.write(by, 0, c);
// 	          }
// 	          out.close();
// 	          in.close();
 	        }
 	      }
 	       }
 	    catch (Exception ex) {
 	      System.out.println(ex.getMessage());
 	    }
 	        
 	    } 
 	
     
}
