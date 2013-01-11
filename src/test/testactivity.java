package test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.carelife.creader.db.BookDao;
import org.carelife.creader.xmlparser.RankParser;
import org.carelife.creader.xmlparser.SearchParser;
import org.xml.sax.SAXException;

import org.carelife.creader.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class testactivity extends Activity{

	Button st,sp,add_data;
	Intent it;
	BookDao bd;
	int i =1;
	HashMap<String,Object> test_data = new HashMap<String,Object>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.servicetest);
		bd = BookDao.getInstance(this);
		
		
		it = new Intent("com.sogou.testservice");
		st = (Button) findViewById(R.id.service_start);
		sp = (Button) findViewById(R.id.service_stop);
		add_data = (Button) findViewById(R.id.sql_add_data);
		
		add_data.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bd.open();
				String book_name = "";
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
				NotificationManager manager = (NotificationManager) getSystemService(
						testactivity.this.NOTIFICATION_SERVICE); 
			     //构建一个通知对象
//				Notification notification = new Notification(R.drawable.bight_set, 
//			                "来自sogou阅读的"+book_name+"更新消息", System.currentTimeMillis());
//				PendingIntent pendingIntent = PendingIntent.getActivity( 
//					 testactivity.this, 
//		       		 0, 
//		             new Intent(testactivity.this,BookShelf.class),
//		             0
//		        );
//				
//				notification.setLatestEventInfo(getApplicationContext(),
//						book_name+"更新", 
//			             "快来看"+book_name+"吧", 
//			             pendingIntent);
//				
//				notification.flags|=Notification.FLAG_AUTO_CANCEL; //自动终止
//			    notification.defaults |= Notification.DEFAULT_SOUND; //默认声音
//			    manager.notify(0, notification);
				
			}
		});
		
		st.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startService(it);
			}
		});
		
		sp.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopService(it);
			}
		});
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			test_data = getXML();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(test_data);
	}
	
	
	public HashMap<String,Object> getXML() throws UnsupportedEncodingException{
		//System.out.println("begin get version from stream");
		//System.out.println("begin get version using pull");
		HashMap<String,Object> result = null;
		String testUrl = "http://wap.sogou.com/book/sgapp_search.jsp?p=1&keyword=";
		String query_word = "遮天";
		String query = URLEncoder.encode(query_word, "utf-8");
		testUrl = testUrl + query;
		System.out.println(testUrl);
		try{
			URL urlStr = new URL(testUrl);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) urlStr
					.openConnection();
			InputStream input = httpUrlConnection.getInputStream();
			// 开始解析文件
			result =  SearchParser.getSearchResultByXml(input);
			if(null != httpUrlConnection)
				httpUrlConnection.disconnect();
		}catch(Exception err){
			err.printStackTrace();
		}
		return result;
	}
	
	private Bitmap getBitmapFromUrl(String imgUrl) {
        URL url;
        Bitmap bitmap = null;
        try {
                url = new URL(imgUrl);
                InputStream is = url.openConnection().getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bitmap = BitmapFactory.decodeStream(bis);
                bis.close();
        } catch (MalformedURLException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return bitmap;
	}
	
}
