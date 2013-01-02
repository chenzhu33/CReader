package com.sogou.xmlparser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.sogou.xmldata.RankData;


public class ParserRank {
	
	public static HashMap<String,Object> getRankResultByXml(InputStream is)   
    {   
//    	Log.i("pull parser","begin");
		HashMap<String,Object> result = null;
        List<RankData> Rlist = null;   
        RankData Rank_data = null;   
        String currentTag = null;
        String count = null;
        try{
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();   
        XmlPullParser parser = factory.newPullParser();   
        parser.setInput(is, "GBK");   
        int type = parser.getEventType();   
        while (type != XmlPullParser.END_DOCUMENT)   
        {   
            String typeName = parser.getName();   
            if (type == XmlPullParser.START_TAG)   
            {   
//                Log.v("TAG", "typeName=" + typeName);   
                if ("DOCUMENT".equals(typeName)){
                	result = new HashMap<String,Object>();
                	Rlist = new ArrayList<RankData>();
                }else if ("count".equals(typeName)){
                	currentTag = "count";
                }else if ("item".equals(typeName)){
                	Rank_data = new RankData();
                }else if ("book".equals(typeName)){
                	currentTag = "book";
                }else if ("author".equals(typeName)){
                	currentTag = "author";
                }else if ("type".equals(typeName)){
                	currentTag = "type";
                }else if ("status".equals(typeName)){
                	currentTag = "status";
                }else if ("desc".equals(typeName)){
                	currentTag = "desc";
                }else if ("picurl".equals(typeName)){
                	currentTag = "picurl";
                }
            }else if (type == XmlPullParser.END_TAG){   
                if ("item".equals(typeName))   
                {   
                    Rlist.add(Rank_data);   
                    Rank_data = null;   
                }else if("DOCUMENT".equals(typeName)){
                	result.put("count", count);
                	result.put("rankitem", Rlist);
                }
            }else if (type == XmlPullParser.TEXT){   
                String s = parser.getText();   
//                Log.i("text",s);
                if ("count".equals(currentTag))   
                {   
                	count = s;
                    currentTag = null;   
                }else if ("book".equals(currentTag)){   
                    Rank_data.setbookname(s);  
                    currentTag = null;   
                }else if ("author".equals(currentTag)){   
                    Rank_data.setauthor_name(s);  
                    currentTag = null;   
                }else if ("type".equals(currentTag)){   
                    Rank_data.settype(s);  
                    currentTag = null;   
                }else if ("status".equals(currentTag)){   
                    Rank_data.setstatus(Integer.parseInt(s));  
                    currentTag = null;   
                }else if ("desc".equals(currentTag)){   
                    Rank_data.setdesc(s);  
                    currentTag = null;   
                }else if ("picurl".equals(currentTag)){   
                    Rank_data.setpicurl(s);  
                    currentTag = null;   
                }
            }   
            type = parser.next();   
        }   
//        Log.v("TAG", "size=" + list.size());   
        }catch(Exception e){
        	e.printStackTrace();
        }
        return result;   
    }
}
