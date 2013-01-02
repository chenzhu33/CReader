package com.sogou.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.sogou.constdata.ConstData;
import com.sogou.data.book_basic;
import com.sogou.db.BookDao;

public class UpdateUtil {
	
	
	
	public static List<String> cheakupdate(Context c){
		
		BookDao bd = BookDao.getInstance(c);
		boolean notify_flag = false;
		
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(ConstData.updateUrl); 
			//添加http头信息
			httppost.addHeader("Content-TYPE","application/x-www-form-urlencoded");
			
//			httppost.addHeader("Authorization", "your token"); //认证token
//			httppost.addHeader("Content-Type", "application/json");
//			httppost.addHeader("User-Agent", "SogouSearch Android");
//			httppost.addHeader()
			//http post的json数据格式：  {"name": "your name","parentId": "id_of_parent"}
//			JSONObject obj = new JSONObject();
//			obj.put("feedback_msg", msg);
//			obj.put("feedback_userinfo", userinfo);
//			httppost.setEntity(new StringEntity(obj.toString()));
//			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));  
//			Log.i("msg",msg);
//			Log.i("user",userinfo);
			JSONObject j_mainobj = new JSONObject();
			JSONArray json_array = new JSONArray();
			
			List<String> book_updatelist = bd.get_book_needupdate();
			if(null == book_updatelist || book_updatelist.size() == 0){
				return null;
			}
			
			for(int i = 0 ; i < book_updatelist.size() ; i++){
				JSONObject j_obj = new JSONObject();
				json_array.put(j_obj.put("name", book_updatelist.get(i)));
			}
			
//			JSONObject j_obj1 = new JSONObject();
//			json_array.put(j_obj1.put("name", "冒牌大将军"));
			
			j_mainobj.put("booklist", json_array);
			
			System.out.println("request json:"+j_mainobj.toString());
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
	        nameValuePair.add(new BasicNameValuePair("booklist",j_mainobj.toString()));
	        
	        
//			 HttpPost httpPost = new HttpPost("http://192.168.1.103/webservice/index.php");  
            /*设置请求的数据*/  
//	        Log.i("",new UrlEncodedFormEntity(nameValuePair).getContent().toString());
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePair,HTTP.UTF_8));  
//	        httppost.getParams().setParameter("feedback_msg", msg);
//			httppost.getParams().setParameter("feedback_userinfo", userinfo);
			HttpResponse response;
			response = httpclient.execute(httppost);
			//检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						content, "GBK"));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				System.out.println("response json:"+builder.toString());
				JSONObject j_obj = new JSONObject(builder.toString());
				JSONArray j_array = j_obj.getJSONArray("booklist");
				List<String> notifylist = new ArrayList<String>();
				for(int i = 0 ; i < j_array.length() ; i++ ){
					JSONObject temp_obj = (JSONObject) j_array.opt(i);
					book_basic temp_b = new book_basic();
					if(temp_obj.getString("loc") != null){
						int loc = Integer.valueOf(temp_obj.getString("loc"));
						if(loc == 1){
							temp_b.setBook_name(temp_obj.getString("name"));
							temp_b.setMax_md5(temp_obj.getString("md5"));
							temp_b.setIs_loc(loc);
							if(bd.set_book_update(temp_b)){
								notify_flag = true;
								notifylist.add(temp_b.getBook_name());
							}
							
						}else{
							temp_b.setBook_name(temp_obj.getString("name"));
							temp_b.setMax_md5(temp_obj.getString("chapterCode"));
							temp_b.setIs_loc(loc);
							if(bd.set_book_update(temp_b)){
								notify_flag = true;
								notifylist.add(temp_b.getBook_name());
							}
						}
					}
						
				}
				if(content != null){
					content.close();
				}
				if(httpclient != null){
					httpclient.getConnectionManager().shutdown();
				}
				
				if(notify_flag){
					return notifylist;
				}
			}
				
//				String rev = EntityUtils.toString(response.getEntity());//返回json格式： {"id": "27JpL~j4vsL0LX00E00005","version": "abc"}		
//				obj = new JSONObject(rev);
//				String id = obj.getString("id");
//				String version = obj.getString("version");
				
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e1) {	
			e1.printStackTrace();
			return null;
		} catch (Exception e2) {	
			e2.printStackTrace();
			return null;
		}
			
		return null;
		
	}
	
	public static String cheak_maxchaptercode(Context c,String book_name){
		
		String chapterCode = null;
		
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(ConstData.updateUrl); 
			//添加http头信息
			httppost.addHeader("Content-TYPE","application/x-www-form-urlencoded");
			
//			httppost.addHeader("Authorization", "your token"); //认证token
//			httppost.addHeader("Content-Type", "application/json");
//			httppost.addHeader("User-Agent", "SogouSearch Android");
//			httppost.addHeader()
			//http post的json数据格式：  {"name": "your name","parentId": "id_of_parent"}
//			JSONObject obj = new JSONObject();
//			obj.put("feedback_msg", msg);
//			obj.put("feedback_userinfo", userinfo);
//			httppost.setEntity(new StringEntity(obj.toString()));
//			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));  
//			Log.i("msg",msg);
//			Log.i("user",userinfo);
			JSONObject j_mainobj = new JSONObject();
			JSONArray json_array = new JSONArray();
			
			JSONObject j_obj1 = new JSONObject();
			json_array.put(j_obj1.put("name", book_name));
			
			j_mainobj.put("booklist", json_array);
			
//			System.out.println("request json:"+j_mainobj.toString());
			
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
	        nameValuePair.add(new BasicNameValuePair("booklist",j_mainobj.toString()));
	        
	        
//			 HttpPost httpPost = new HttpPost("http://192.168.1.103/webservice/index.php");  
            /*设置请求的数据*/  
//	        Log.i("",new UrlEncodedFormEntity(nameValuePair).getContent().toString());
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePair,HTTP.UTF_8));  
//	        httppost.getParams().setParameter("feedback_msg", msg);
//			httppost.getParams().setParameter("feedback_userinfo", userinfo);
			HttpResponse response;
			response = httpclient.execute(httppost);
			//检验状态码，如果成功接收数据
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						content, "GBK"));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
//				System.out.println("response json:"+builder.toString());
				JSONObject j_obj = new JSONObject(builder.toString());
				JSONArray j_array = j_obj.getJSONArray("booklist");
				for(int i = 0 ; i < j_array.length() ; i++ ){
					JSONObject temp_obj = (JSONObject) j_array.opt(i);
					if(temp_obj.getString("loc") != null){
						int loc = Integer.valueOf(temp_obj.getString("loc"));
						if(loc != 1){
							chapterCode = temp_obj.getString("chapterCode");
						}
					}
						
				}
				if(content != null){
					content.close();
				}
				if(httpclient != null){
					httpclient.getConnectionManager().shutdown();
				}
				
				return chapterCode;
			}
				
//				String rev = EntityUtils.toString(response.getEntity());//返回json格式： {"id": "27JpL~j4vsL0LX00E00005","version": "abc"}		
//				obj = new JSONObject(rev);
//				String id = obj.getString("id");
//				String version = obj.getString("version");
				
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e1) {	
			e1.printStackTrace();
			return null;
		} catch (Exception e2) {	
			e2.printStackTrace();
			return null;
		}
			
		return null;
		
	}

}
