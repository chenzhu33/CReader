package org.carelife.creader.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.carelife.creader.bean.BookBasicBean;
import org.carelife.creader.bean.ChapterBasicBean;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.db.BookDao;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;


public class DownloadUtil {

	protected BookDao bookDao;
	public static final int REQUEST_TIMEOUT = 50 * 1000; // 50s
	public static final int SO_TIMEOUT = 120 * 1000; // 120s

	public DownloadUtil() {
	}

	public List<ChapterBasicBean> getChapterList_For_Beginread(String book_name,
			String author_name, Context c) throws Exception {
		String url = UrlHelper.chapter_url;
		if (null == book_name && null == author_name) {
			return null;
		}

		try {
			url = url + "b.n=" + URLEncoder.encode(book_name, "utf-8")
					+ "&b.a=" + URLEncoder.encode(author_name, "utf-8")
					+ "&cc=" + 0;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
//		System.out.println(url);
		bookDao = BookDao.getInstance(c);
		String book_md5 = null;
		List<ChapterBasicBean> temp_list = new ArrayList<ChapterBasicBean>();
		StringBuilder builder = new StringBuilder();
		HttpGet httpGet = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();

		HttpResponse response = client.execute(httpGet);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {

			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					content, "GBK"));
			String line;
			int temp_index = 0;
			while ((line = reader.readLine()) != null) {
				if (temp_index != 0) {
					builder.append(line);
				} else {
					book_md5 = line;
					// System.out.println("md5 is "+line);
				}
				temp_index++;
			}
			// System.out.println(builder.toString());

			JSONObject j_obj = new JSONObject(builder.toString());
			content.close();
			if (!"suc".equals(j_obj.getString("status"))) {
				return null;
			}

			JSONArray jsonObjs = j_obj.getJSONArray("chapter");
			// String s = "";
			int j = 1;
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
				String name = jsonObj.getString("name");
				String md5_temp = jsonObj.getString("md5");
				// System.out.println("Index:"+ j + " 章节名：" + name + ",md5：" +
				// md5_temp+ "\n");
				// s += "Index:"+ j + " 章节名：" + name + ",md5：" + md5_temp+ "\n"
				// ;
				ChapterBasicBean temp_c = new ChapterBasicBean(book_name,
						author_name, name, md5_temp, j, 0);
				temp_list.add(temp_c);
				j++;
			}

			// System.out.println(s);

			// System.out.println("third url2:"+jsonObject.getString("url2"));
			// System.out.println("third url:"+jsonObject.getString("url"));
			// bookDao = BookDao.getInstance(c);
			BookBasicBean book = new BookBasicBean(book_name,
					author_name, book_md5);
			book.setMax_md5(temp_list.get(j - 2).getChapter_md5());
			bookDao.Update_Bookmd5(book);
			bookDao.insert_maxmd5(book);
			bookDao.insert_chapter(temp_list);
		}
		
		return temp_list;
	}

	public HashMap<String, Object> getChapterList(String book_name,
			String author_name, Context c, boolean force) throws Exception {
		String url = UrlHelper.chapter_url;
		if (null == book_name && null == author_name) {
			return null;
		}

		try {
			url = url + "b.n=" + URLEncoder.encode(book_name, "utf-8")
					+ "&b.a=" + URLEncoder.encode(author_name, "utf-8")
					+ "&cc=" + 0;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
//		System.out.println(url);
		bookDao = BookDao.getInstance(c);
		HashMap<String, Object> result = new HashMap<String, Object>();
		List<ChapterBasicBean> temp_list = new ArrayList<ChapterBasicBean>();
		String book_md5 = bookDao.get_Bookmd5(book_name, author_name);
		StringBuilder builder = new StringBuilder();
		HttpGet httpGet = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();

		HttpResponse response = client.execute(httpGet);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {

			HttpEntity entity = response.getEntity();
			InputStream content = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					content, "GBK"));
			String line;
			int temp_index = 0;
			while ((line = reader.readLine()) != null) {
				if (temp_index != 0) {
					builder.append(line);
				} else {
					if (!force && line.equals(book_md5)) {
						// System.out.println("读取缓存目录");
						temp_list = bookDao.getChapter_list(book_name,
								author_name);
						result.put("book_md5", book_md5);
						result.put("chapter_list", temp_list);
						content.close();
						return result;
					}
					book_md5 = line;
					// System.out.println("md5 is "+line);
				}
				temp_index++;
			}
			// System.out.println(builder.toString());
			content.close();
			JSONObject j_obj = new JSONObject(builder.toString());

			if (!"suc".equals(j_obj.getString("status"))) {
				return null;
			}

			JSONArray jsonObjs = j_obj.getJSONArray("chapter");
			// String s = "";
			int j = 1;
			for (int i = 0; i < jsonObjs.length(); i++) {
				JSONObject jsonObj = ((JSONObject) jsonObjs.opt(i));
				String name = jsonObj.getString("name");
				String md5_temp = jsonObj.getString("md5");
//				 System.out.println("Index:"+ j + " 章节名：" + name + ",md5："
//				 + md5_temp+ "\n");
				// s += "Index:"+ j + " 章节名：" + name + ",md5：" + md5_temp+
				// "\n" ;
				ChapterBasicBean temp_c = new ChapterBasicBean(book_name,
						author_name, name, md5_temp, j, 0);
				temp_list.add(temp_c);
				j++;
			}

			// System.out.println(s);

			// System.out.println("third url2:"+jsonObject.getString("url2"));
			// System.out.println("third url:"+jsonObject.getString("url"));
			// bookDao = BookDao.getInstance(c);
			BookBasicBean book = new BookBasicBean(book_name,
					author_name, book_md5);
			book.setMax_md5(temp_list.get(j - 2).getChapter_md5());
			bookDao.Update_Bookmd5_Noinsert(book);
			bookDao.insert_maxmd5(book);
			bookDao.insert_chapter(temp_list);
		} else {
			temp_list = bookDao.getChapter_list(book_name, author_name);
			result.put("book_md5", book_md5);
			result.put("chapter_list", temp_list);
			return result;

		}

		result.put("book_md5", book_md5);
		result.put("chapter_list", temp_list);

		return result;
	}

	public boolean Begin_Read(List<ChapterBasicBean> c_list, BookBasicBean book,
			Context c) {

		bookDao = BookDao.getInstance(c);

		bookDao.insert_chapter(c_list);

		bookDao.Update_Bookmd5(book);

		return true;
	}

	public boolean get_Bookcontext(String book_name, String author_name,
			String md5, int num, Context c) throws Exception {

		String url = UrlHelper.context_url;
		// url="http://10.14.135.43/novelapi/novelDetailServlet?b.n=%E9%81%AE%E5%A4%A9&b.a=%E8%BE%B0%E4%B8%9C&md5=96def07ad858190f&count=10";
		if (null == book_name && null == author_name) {
			return false;
		}

		try {
			url = url + "b.n=" + URLEncoder.encode(book_name, "utf-8")
					+ "&b.a=" + URLEncoder.encode(author_name, "utf-8")
					+ "&md5=" + URLEncoder.encode(md5, "utf-8") + "&count="
					+ num;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
//		System.out.println(url);


		File temp_d = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
				+ FileUtil.cheak_string(author_name) + "/");
		if (!temp_d.exists()) {
			temp_d.mkdirs();
		}

		File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
				+ FileUtil.cheak_string(author_name) + "/" + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
				+ md5 + "_" + num + ".zip");
		if (temp_f.exists()) {
			temp_f.delete();
			// FileUtil fu = new FileUtil();
			// fu.UnZipBook(book_name+"_"+author_name+"_"+md5+"_"+num,
			// book_name, author_name);
			// return true;
		}
		temp_f.createNewFile();

		FileOutputStream output = new FileOutputStream(temp_f);
		HttpGet httpGet = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(httpGet);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {

			HttpEntity entity = response.getEntity();

			//Header[] headers = response.getAllHeaders();

			// for(int i = 0;i < headers.length ; i++){
			// System.out.println("name = "+headers[i].getName()
			// +" , values is ="+headers[i].getValue());
			// }
			//int total_len = (int) entity.getContentLength();
			//DecimalFormat df = new DecimalFormat("#0");
			// System.out.println("len is ="+total_len);

			InputStream content = entity.getContent();
			byte buf[] = new byte[1024];
			//int downLoadFilePosition = 0;
			int numread;
			while ((numread = content.read(buf)) != -1) {

				output.write(buf, 0, numread);
				//downLoadFilePosition += numread;
				// System.out.println(df.format(downLoadFilePosition*
				// 100/total_len ) + "%");
			}
			output.close();
			content.close();
			if (temp_f.exists()) {
				FileUtil fu = new FileUtil();
				fu.UnZipBook(FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
						+ md5 + "_" + num , book_name, author_name);
				return true;
			}
		}
		

		return true;
	}
	
	
	public boolean get_Bookcontext_refresh(String book_name, String author_name,
			String md5, int num, Context c) throws Exception {

		String url = UrlHelper.context_url;
		// url="http://10.14.135.43/novelapi/novelDetailServlet?b.n=%E9%81%AE%E5%A4%A9&b.a=%E8%BE%B0%E4%B8%9C&md5=96def07ad858190f&count=10";
		if (null == book_name && null == author_name) {
			return false;
		}

		try {
			url = url + "b.n=" + URLEncoder.encode(book_name, "utf-8")
					+ "&b.a=" + URLEncoder.encode(author_name, "utf-8")
					+ "&md5=" + URLEncoder.encode(md5, "utf-8") + "&count="
					+ num;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
//		System.out.println(url);


		File temp_d = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
				+ FileUtil.cheak_string(author_name) + "/");
		if (!temp_d.exists()) {
			temp_d.mkdirs();
		}

		File temp_f = new File(FileUtil.book_temp_dir + FileUtil.cheak_string(book_name) + "_"
				+ FileUtil.cheak_string(author_name) + "/" + FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
				+ md5 + "_" + num + ".zip");
		if (temp_f.exists()) {
			temp_f.delete();
			// FileUtil fu = new FileUtil();
			// fu.UnZipBook(book_name+"_"+author_name+"_"+md5+"_"+num,
			// book_name, author_name);
			// return true;
		}
		temp_f.createNewFile();

		FileOutputStream output = new FileOutputStream(temp_f);
		HttpGet httpGet = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(httpGet);
		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {

			HttpEntity entity = response.getEntity();

			//Header[] headers = response.getAllHeaders();

			// for(int i = 0;i < headers.length ; i++){
			// System.out.println("name = "+headers[i].getName()
			// +" , values is ="+headers[i].getValue());
			// }
			//int total_len = (int) entity.getContentLength();
			//DecimalFormat df = new DecimalFormat("#0");
			// System.out.println("len is ="+total_len);

			InputStream content = entity.getContent();
			byte buf[] = new byte[1024];
			//int downLoadFilePosition = 0;
			int numread;
			while ((numread = content.read(buf)) != -1) {

				output.write(buf, 0, numread);
				//downLoadFilePosition += numread;
				// System.out.println(df.format(downLoadFilePosition*
				// 100/total_len ) + "%");
			}
			output.close();
			content.close();
			if (temp_f.exists()) {
				FileUtil fu = new FileUtil();
				fu.UnZipBook_refresh(FileUtil.cheak_string(book_name) + "_" + FileUtil.cheak_string(author_name) + "_"
						+ md5 + "_" + num , book_name, author_name);
				return true;
			}
		}
		

		return true;
	}

}
