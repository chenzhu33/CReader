package com.sogou.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;
import android.util.Log;
import com.sogou.db.BookDao;
import com.sogou.db.DatabaseHelper;

public class FileUtil {
	File skRoot = null;
	File file = null;
	public static String book_dir = Environment.getExternalStorageDirectory()
			+ "/book/";
	public static String new_dir = Environment.getExternalStorageDirectory()
			+ "/sogounovel/book/";
	public static String book_temp_dir = Environment
			.getExternalStorageDirectory() + "/sogounovel/book_temp/";
	public static String news_dir = Environment.getExternalStorageDirectory()
			+ "/sogounovel/news/";
	public static String news_temp_dir = Environment
			.getExternalStorageDirectory() + "/sogounovel/news_temp/";

	int chapter_max_num;
	int book_num;

	DatabaseHelper dbHelper;
	SQLiteDatabase db;
	String sql;
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	Date curDate;
	String time_str;
	int flag_sql;

	public FileUtil() {
	}

	public Bitmap getpic_frombookname(String book_name) {
		Bitmap bm = null;
		File f = new File(book_dir + book_name + "/book_pic.jpg");
		if (f.exists()) {
			bm = BitmapFactory.decodeFile(f.getPath());
		}
		return bm;
	}

	public boolean file_is_exists(String path) {
		File f = new File(path);
		if (f.exists()) {
			return true;
		}
		return false;
	}

	public Boolean UnZipBook(String ZipName, String book_name,
			String author_name) {
		book_name = FileUtil.cheak_string(book_name);
		author_name = FileUtil.cheak_string(author_name);

		File Zipf = new File(book_temp_dir + book_name + "_" + author_name
				+ "/" + ZipName + ".zip");

		if (!Zipf.exists()) {
			return false;
		}

		File Bookdir = new File(new_dir + book_name + '_' + author_name);
		if (!Bookdir.exists()) {
			Bookdir.mkdirs();
		}

		try {
			ZipUtil.unZip(Zipf, new_dir + book_name + '_' + author_name);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// DB update chapter path

		return true;
	}
	
	
	public Boolean UnZipBook_refresh(String ZipName, String book_name,
			String author_name) {
		book_name = FileUtil.cheak_string(book_name);
		author_name = FileUtil.cheak_string(author_name);

		File Zipf = new File(book_temp_dir + book_name + "_" + author_name
				+ "/" + ZipName + ".zip");

		if (!Zipf.exists()) {
			return false;
		}

		File Bookdir = new File(new_dir + book_name + "_" + author_name + "/refresh");
		if (!Bookdir.exists()) {
			Bookdir.mkdirs();
		}

		try {
			ZipUtil.unZip(Zipf, new_dir + book_name + "_" + author_name + "/refresh");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// DB update chapter path

		return true;
	}

	// url =
	// "http://10.14.135.43/novelapi/novelDetailServlet?url=http%3A%2F%2Fwww.xiaoshuozhe.com%2Ffiles%2Farticle%2Fbooks%2F20%2F20432%2F6975307.html&b.n=%E6%AD%A6%E5%8A%A8%E4%B9%BE%E5%9D%A4&b.a=%E5%A4%A9%E8%9A%95%E5%9C%9F%E8%B1%86&count=5";

	public String get_chapterpath(String book_name, String author_name,
			int index) {
		String file_path = new_dir + book_name + author_name + File.separator
				+ index + ".txt";
		File book_file = new File(file_path);
		if (book_file.exists()) {
			return file_path;
		}

		return "";
	}

	public boolean create_book(File f) {
		return f.mkdir();
	}

	public Bitmap getbook_pic(File f) {
		Bitmap bm = null;
		if (f.exists()) {
			bm = BitmapFactory.decodeFile(f.getPath());
		}
		return bm;
	}

	public int getbook_num(File f) {
		if (!f.exists()) {
			return 0;
		}
		File[] temp = f.listFiles();
		int j = 0;
		for (int i = temp.length - 1; i >= 0; i--) {
			if (temp[i].isDirectory()) {
				j++;

			}
		}

		return j;
	}

	public List<HashMap<String, Object>> getbook_list() throws IOException {
		FileInputStream fr = null;
		String path = null;
		String book_name = null;
		File f = new File(book_dir);
		List<HashMap<String, Object>> b_list = new ArrayList<HashMap<String, Object>>();
		if (!f.exists()) {
			return null;
		}
		File[] temp = f.listFiles();
		int j = 0;
		for (int i = temp.length - 1; i >= 0; i--) {
			if (temp[i].isDirectory()) {
				book_name = temp[i].getName();
				path = temp[i].getPath();
				HashMap<String, Object> book = new HashMap<String, Object>();
				book.put("book_name", book_name);
				File temp_infofile = new File(path + "/book_info.txt");
				if (temp_infofile.exists()) {
					fr = new FileInputStream(temp_infofile);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(fr, "GBK"));
					StringBuffer sb = new StringBuffer();
					while (br.ready()) {
						String line = br.readLine();
						if (line == null)
							break;
						sb.append(line + "\n");
					}
					book.put("info", sb.toString());
					br.close();
					fr.close();
				}
				File temp_picfile = new File(path + "/book_pic.jpg");
				if (temp_picfile.exists()) {
					book.put("book_pic", getbook_pic(new File(path
							+ "/book_pic.jpg")));
				}
				b_list.add(book);
				j++;

			}
		}

		book_num = j;

		return b_list;

	}

	public List<HashMap<String, Object>> getchapter_list(String book_name)
			throws IOException {
		FileInputStream fr = null;
		File f = new File(book_dir + book_name + "/book_list.txt");
		fr = new FileInputStream(f);
		List<HashMap<String, Object>> c_list = new ArrayList<HashMap<String, Object>>();
		int i = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(fr, "GBK"));
		String[] temp = null;
		while (br.ready()) {
			String line = br.readLine();
			if (line == null)
				break;

			HashMap<String, Object> chapter = new HashMap<String, Object>();
			temp = line.split(":");
			if (temp.length == 2) {
				chapter.put("chapter_name", temp[1]);
				chapter.put("chapter_path", f.getParent() + "/" + temp[0]
						+ ".txt");
				// chapter.put("chapter_pic", getbook_pic(new
				// File(f.getParent()+"/book_pic.jpg")));
				c_list.add(chapter);
				i++;
			}
		}
		// System.out.println("c_list is = "+c_list);
		// System.out.println("chapter len is = "+i);
		fr.close();
		br.close();
		chapter_max_num = i;
		return c_list;

	}

	public List<HashMap<String, Object>> getchapter_list_fortab(String book_name)
			throws IOException {
		FileInputStream fr = null;
		File f = new File(book_dir + book_name + "/book_list.txt");
		fr = new FileInputStream(f);
		List<HashMap<String, Object>> c_list = new ArrayList<HashMap<String, Object>>();
		int i = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(fr, "GBK"));
		while (br.ready()) {
			String line = br.readLine();
			if (line == null)
				break;
			HashMap<String, Object> chapter = new HashMap<String, Object>();
			String[] temp = line.split(":");
			if (temp.length == 2) {
				chapter.put("chapter_name", temp[1]);
				chapter.put("chapter_path", f.getParent() + "/" + temp[0]
						+ ".txt");
				c_list.add(chapter);
				i++;
			}

		}
		// System.out.println("c_list is = "+c_list);
		// System.out.println("chapter len is = "+i);
		fr.close();
		br.close();
		chapter_max_num = i;
		return c_list;

	}

	public int get_chapter_from_path(String path) {
		if (path == null)
			return 0;
		String[] temp = path.split("/");
		if (temp.length != 0) {
			return Integer.parseInt(temp[temp.length - 1].split("\\.")[0]);
		}

		return 0;
	}

	public String get_book_name(String path) {

		File tempfile = new File(path);
		return tempfile.getParent().replace(book_dir, "");

	}

	public int get_chapter_maxnum_fromname(String book_name) throws IOException {
		File f = new File(book_dir + book_name + "/book_list.txt");
		FileInputStream fr = null;
		fr = new FileInputStream(f);
		int i = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(fr, "GBK"));
		while (br.ready()) {
			String line = br.readLine();
			if (line == null)
				break;
			String[] temp = line.split(":");
			if (temp.length == 2) {
				i++;
			}

		}
		fr.close();
		br.close();
		chapter_max_num = i;

		return chapter_max_num;
	}

	public int get_chapter_maxnum() {
		return chapter_max_num;
	}

	public String get_chapter_name_fornum(String book_name, int Chapter_num)
			throws IOException {
		String chapter_name = "";
		File f = new File(book_dir + book_name + "/book_list.txt");
		FileInputStream fr = null;
		fr = new FileInputStream(f);
		int i = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(fr, "GBK"));
		while (br.ready()) {
			String line = br.readLine();
			if (line == null)
				break;
			String[] temp = line.split(":");
			if (temp.length == 2) {
				i++;
			}
			if (i == Chapter_num) {
				chapter_name = temp[1];
				fr.close();
				br.close();
				return chapter_name;
			}

		}
		fr.close();
		br.close();

		return chapter_name;
	}

	public int get_book_num() {
		return book_num;
	}

	public void findbook(File f) {

		File[] temp = file.listFiles();
		for (int i = 0; i < temp.length; i++) {
			if (temp[i].isDirectory()) {
				System.out.println(temp[i].getName());
				System.out.println(temp[i].getPath());

			}
		}

		return;
	}

	public void listFile(File f) {
		if (f.isDirectory()) {
			System.out.println("directory:" + f.getPath() + "--parent:"
					+ f.getParent());
			File[] t = f.listFiles();
			for (int i = 0; i < t.length; i++) {
				listFile(t[i]);
			}
		} else {
			System.out.println("fileName:" + f.getAbsolutePath() + "--parent:"
					+ f.getParent());
		}
		return;
	}

	public boolean checkSDcardAvailable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}

	/*-------------------------- new file manger -------------------------------------------*/

	public List<HashMap<String, Object>> getchapter_list_locdata(
			String book_name) throws IOException {
		FileInputStream fr = null;
		File f = new File(book_dir + book_name + "/book_list.txt");
		fr = new FileInputStream(f);
		List<HashMap<String, Object>> c_list = new ArrayList<HashMap<String, Object>>();
		int i = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(fr, "GBK"));
		String[] temp = null;
		while (br.ready()) {
			String line = br.readLine();
			if (line == null)
				break;

			HashMap<String, Object> chapter = new HashMap<String, Object>();
			temp = line.split(":");
			if (temp.length == 3) {
				// 章节中文名
				chapter.put("chapter_name", temp[1]);
				// 章节地址
				chapter.put("chapter_path", f.getParent() + "/" + temp[2]
						+ ".txt");
				// 章节编号
				chapter.put("chapter_num", temp[0]);

				// chapter.put("chapter_pic", getbook_pic(new
				// File(f.getParent()+"/book_pic.jpg")));
				c_list.add(chapter);
				i++;
			}
		}
		// System.out.println("c_list is = "+c_list);
		// System.out.println("chapter len is = "+i);
		fr.close();
		br.close();
		chapter_max_num = i;
		return c_list;

	}

	public int get_chapter_from_path_locdata(String path) {
		if (path == null)
			return 0;
		String[] temp = path.split("/");
		if (temp.length != 0) {
			return Integer.parseInt(temp[temp.length - 1].split("\\.")[0]);
		}

		return 0;
	}

	public static void delete_book(String book_name, String author_name,
			Context c) {
		BookDao bd;
		bd = BookDao.getInstance(c);

		bd.delete_book(book_name, author_name);

		delAllFile(book_temp_dir + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "/");
		delAllFile(new_dir + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "/");

	}

	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}

		file.delete();
		return flag;
	}

	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public long getFileSizes(File f) throws Exception {// 取得文件大小
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
			fis.close();
		} else {
			f.createNewFile();
			System.out.println("文件不存在");
		}
		return s;
	}

	// 递归
	public long getFileSize(File f) throws Exception// 取得文件夹大小
	{
		if (!f.exists()) {
			return 0;
		}
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}

	public String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS == 0) {
			return "0B";
		}
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public String BookSize(String book_name, String author_name) {
		String rt = null;
		File book_f = new File(new_dir + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name));
		File book_temp_f = new File(book_temp_dir + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name));

		try {
			long size = getFileSize(book_f) + getFileSize(book_temp_f);

			rt = FormetFileSize(size);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rt;
	}

	public static String cheak_string(String s){
		if( null == s) {
			return "";
		}
		String regEx="[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";   
		Pattern p = Pattern.compile(regEx);      
		Matcher m = p.matcher(s);
		String final_string = m.replaceAll("").trim();
//		System.out.println("old string is ="+final_string);
		String return_string = Base64Util.encode(final_string.getBytes()).replace("/", "$");
//		System.out.println("new string is ="+return_string);
//		System.out.println("after decode string is ="+new String(Base64Util.decode(return_string.replace("$", "/"))));
		return return_string;
	}
	
	
	/**
	 * 判断是否有足够的空间供下载
	 * 
	 * @param downloadSize
	 * @return
	 */
	public static boolean isEnoughForDownload(long downloadSize) {
		StatFs statFs = new StatFs(Environment.getExternalStorageDirectory()
				.getAbsolutePath());
		// sd卡分区数
		int blockCounts = statFs.getBlockCount();
		Log.e("ray", "blockCounts" + blockCounts);
		// sd卡可用分区数
		int avCounts = statFs.getAvailableBlocks();
		Log.e("ray", "avCounts" + avCounts);
		// 一个分区数的大小
		long blockSize = statFs.getBlockSize();
		Log.e("ray", "blockSize" + blockSize);
		// sd卡可用空间
		long spaceLeft = avCounts * blockSize;
		Log.e("ray", "spaceLeft" + spaceLeft);
		Log.e("ray", "downloadSize" + downloadSize);
		if (spaceLeft < downloadSize) {
			return false;
		}
		return true;
	}
	
	public static boolean isEmail(String strEmail) { 
	      String strPattern = "^[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?";
	      Pattern p = Pattern.compile(strPattern); 
	      Matcher m = p.matcher(strEmail); 
	      return m.matches(); 
	} 

	/**  
     *  复制单个文件  
     *  @param  oldPath  String  原文件路径  如：c:/fqf.txt  
     *  @param  newPath  String  复制后路径  如：f:/fqf.txt  
     *  @return  boolean  
     */
	public  static void  copyFile(String  oldPath,  String  newPath)  {  
	       try  {  
	           int  bytesum  =  0;  
	           int  byteread  =  0;  
	           File  oldfile  =  new  File(oldPath);  
	           if  (oldfile.exists())  {  //文件存在时  
	               InputStream  inStream  =  new  FileInputStream(oldPath);  //读入原文件  
	               FileOutputStream  fs  =  new  FileOutputStream(newPath);  
	               byte[]  buffer  =  new  byte[1444];  
	               while  (  (byteread  =  inStream.read(buffer))  !=  -1)  {  
	                   bytesum  +=  byteread;  //字节数  文件大小  
	                   fs.write(buffer,  0,  byteread);  
	               }  
	               inStream.close();
	               fs.close();
	           }  
	       }  
	       catch  (Exception  e)  {  
//	           System.out.println("复制单个文件操作出错");  
	           e.printStackTrace();  
	 
	       }  
	 
	   }

	//
	// if(!file.exists()){
	// System.out.println("ERR!");
	// file.mkdirs();
	// }
	//
	//

}
