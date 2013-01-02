package test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sogou.R;

import android.app.TabActivity;   
import android.content.Intent;
import android.os.Bundle;   
import android.util.DisplayMetrics;   
import android.util.Log;   
import android.widget.Button;   
import android.widget.TabHost;   
import android.widget.TabWidget;   
  
public class rollmenu extends TabActivity {   
    TabHost m_TabHost;   
    Button upButton;   
    Button nextButton;   
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.testrollmenu);   
        // Init      
        initTabHost();   
  

        
        
        getJosnData();
        
        
    }   
  
    private void initTabHost() {   
    	m_TabHost = getTabHost();   
        m_TabHost.addTab(m_TabHost.newTabSpec(0 + "").setIndicator("A  eoe")   
                .setContent(R.id.textview01));   
        m_TabHost.addTab(m_TabHost.newTabSpec(1 + "").setIndicator("B  eoe")   
                .setContent(R.id.textview01));   
        m_TabHost.addTab(m_TabHost.newTabSpec(2 + "").setIndicator("C  eoe")   
                .setContent(R.id.textview01));   
        m_TabHost.addTab(m_TabHost.newTabSpec(3 + "").setIndicator("D  eoe")   
                .setContent(R.id.textview01));   
        m_TabHost.addTab(m_TabHost.newTabSpec(4 + "").setIndicator("E  eoe")   
                .setContent(R.id.textview01));   
        m_TabHost.addTab(m_TabHost.newTabSpec(5 + "").setIndicator("F  eoe")   
                .setContent(R.id.textview01));
        Intent intent = new Intent().setClass(this, testrolltabhost.class);
        m_TabHost.addTab(m_TabHost.newTabSpec(6 + "").setIndicator("G  eoe")   
                .setContent(intent));
        
        // Get scream width   
        DisplayMetrics dm = new DisplayMetrics();   
        getWindowManager().getDefaultDisplay().getMetrics(dm);   
        int screenWidth = dm.widthPixels;   
        Log.i("test", "screenWidth=" + screenWidth);   
           
        // Get tab counts   
        TabWidget tabWidget = m_TabHost.getTabWidget();   
        int count = tabWidget.getChildCount();   
        if (count > 3) {   
            for (int i = 0; i < count; i++) {   
                tabWidget.getChildTabViewAt(i).setMinimumWidth((screenWidth) / 3);   
            }   
        } 
    } 
    
    
    private void getJosnData(){
    	
    	final String dataUrl = "http://10.14.135.43/novelapi/novellist.jsp?b.n=%E6%AD%A6%E5%8A%A8%E4%B9%BE%E5%9D%A4&b.a=%E5%A4%A9%E8%9A%95%E5%9C%9F%E8%B1%86&cc=500";
    	
    	
    		StringBuilder builder = new StringBuilder();
    		HttpGet httpGet = new HttpGet(dataUrl);
    		HttpClient client = new DefaultHttpClient();
    		try {
    			HttpResponse response = client.execute(httpGet);
    			StatusLine statusLine = response.getStatusLine();
    			int statusCode = statusLine.getStatusCode();
    			if (statusCode == 200) {
    				HttpEntity entity = response.getEntity();
    				InputStream content = entity.getContent();
    				BufferedReader reader = new BufferedReader(new InputStreamReader(content,"GBK"));
    				String line;
    				while ((line = reader.readLine()) != null) {
    					builder.append(line);
    				}
//    				System.out.println(builder.toString());

    				JSONArray jsonObjs = new JSONObject(builder.toString()).getJSONArray("new");
    				String s = "";   
    				for(int i = 0; i < jsonObjs.length() ; i++){   
	    				JSONObject jsonObj = ((JSONObject)jsonObjs.opt(i));     
	    				String name = jsonObj.getString("name");   
	    				String url = jsonObj.getString("url");   
	    				s +=  " ÕÂ½ÚÃû£º" + name + ",URL£º" + url+ "\n" ;   

    				 }  

    				System.out.println(s);
    				
//    				System.out.println("third url2:"+jsonObject.getString("url2"));
//    				System.out.println("third url:"+jsonObject.getString("url"));
    			} else {
//    				System.out.println("http error");
    			}
    		} catch (Exception err) {
    			err.printStackTrace();
    		}
    		return ;
    	

    	
    }
    
}  
