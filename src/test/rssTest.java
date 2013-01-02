package test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.PrintStreamPrinter;

import com.sogou.constdata.ConstData;
import com.sogou.xmldata.RssData;
import com.sogou.xmlparser.ParserRss;

public class rssTest {

	static String url="http://m.sohu.com/api/rss/fragment/?ids=804&count=10";
	static String org = Environment.getExternalStorageDirectory()+"";
	
	public static List<RssData> getXML(){
		List<RssData> result = null;
		String Url = url;
		try{
//			HttpGet httpGet = new HttpGet(Url);
//			HttpClient client = new DefaultHttpClient();
//			
//			HttpResponse response = client.execute(httpGet);
//			
//			StatusLine statusLine = response.getStatusLine();
//			int statusCode = statusLine.getStatusCode();
//			if (statusCode == 200) {
//				HttpEntity entity = response.getEntity();
//				InputStream content = entity.getContent();
//				int len = (int) entity.getContentLength();
//				
//				byte[] b = new byte[len];
//				
//				content.read(b);
//				String str=new String(b,"utf8");
//				System.out.println(str);
//			}
			
			URL urlStr = new URL(Url);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) urlStr
					.openConnection();
			InputStream input =  httpUrlConnection.getInputStream();
			
			
	
			
//			int total = httpUrlConnection.getInputStream().available();
//	        byte[] b = new byte[total];
	        
//	        byte buffer[] = new byte[1024];
//	        int len = -1;
//	        while ((len = input.read(buffer)) != -1) {
//	        	System.out.println(new String(buffer,"utf8"));
//	        }
//	        

	        
//			input.read(b);
//			System.out.println(total);
//			String str=new String(b,"utf8");
//			System.out.println(str);
//			
//			
//			// 开始解析文件
			result =  ParserRss.getRssResultByXml(input);
//			if(null != httpUrlConnection)
//				httpUrlConnection.disconnect();
			
//			System.out.println("download ");
//			File apk = new File(org+"apk.txt");
//			if(!apk.exists())
//				apk.createNewFile();
//			URL urlStr = new URL(url);
//			HttpURLConnection httpUrlConnection = (HttpURLConnection) urlStr
//					.openConnection();
//			InputStream input = httpUrlConnection
//					.getInputStream();
//			FileOutputStream outputstream = new FileOutputStream(apk);
//			byte buffer[] = new byte[1024];
//			int len = -1;
//			// 将输入流中的内容先输入到buffer中缓存，然后用输出流写到文件中
//			while ((len = input.read(buffer)) != -1) {
////				outputstream.write(buffer, 0, len);
//				System.out.println(new String(buffer,0,len));
//			}
//			input.close();

			
			
		}catch(Exception err){
			err.printStackTrace();
		}
		return result;
	}
	
	
	
	
	
	public static Boolean testupdate(){
		
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://10.11.200.226/book/query_json.jsp"); 
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
			for(int i = 0 ; i < 3 ; i++){
				JSONObject j_obj = new JSONObject();
				json_array.put(j_obj.put("name", "斗破苍穹"));
			}
			JSONObject j_obj1 = new JSONObject();
			json_array.put(j_obj1.put("name", "杀神"));
			
			j_mainobj.put("booklist", json_array);
			
			System.out.println(j_mainobj.toString());
			
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
			System.out.println("code is ="+code);
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
				System.out.println(builder.toString());
//				String rev = EntityUtils.toString(response.getEntity());//返回json格式： {"id": "27JpL~j4vsL0LX00E00005","version": "abc"}		
//				obj = new JSONObject(rev);
//				String id = obj.getString("id");
//				String version = obj.getString("version");
				return true;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e1) {	
			e1.printStackTrace();
			return false;
		} catch (Exception e2) {	
			e2.printStackTrace();
			return false;
		}
		return false;
		
	}
	
	
	
}




