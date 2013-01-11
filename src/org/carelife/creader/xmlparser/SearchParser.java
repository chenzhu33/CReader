package org.carelife.creader.xmlparser;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.carelife.creader.dao.SearchData;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;



public class SearchParser {
	
	public static HashMap<String,Object> getSearchResultByXml(InputStream is){   
//	    	Log.i("pull parser","begin");
		HashMap<String,Object> result = null;
        List<SearchData> Slist = null;   
        SearchData Search_data = null;   
        String currentTag = null;
        String maxpage = null;
        String pagesize = null;
        String pagenum = null;
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
//	                Log.v("TAG", "typeName=" + typeName);   
                if ("DOCUMENT".equals(typeName)){
                	result = new HashMap<String,Object>();
                	Slist = new ArrayList<SearchData>();
                }else if ("pagetotal".equals(typeName)){
                	currentTag = "pagetotal";
                }else if ("pagesize".equals(typeName)){
                	currentTag = "pagesize";
                }else if ("pagenum".equals(typeName)){
                	currentTag = "pagenum";
                }else if ("item".equals(typeName)){
                	Search_data = new SearchData();
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
                }else if ("loc".equals(typeName)){
                	currentTag = "loc";
                }else if ("url".equals(typeName)){
                	currentTag = "url";
                }else if ("chapter".equals(typeName)){
                	currentTag = "chapter";
                }else if ("chapterurl".equals(typeName)){
                	currentTag = "chapterurl";
                }else if ("chaptermd5".equals(typeName)){
                	currentTag = "chaptermd5";
                }else if ("chaptercid".equals(typeName)){
                	currentTag = "chaptercid";
                }else if ("site".equals(typeName)){
                	currentTag = "site";
                }else if ("date".equals(typeName)){
                	currentTag = "date";
                }
                
            }else if (type == XmlPullParser.END_TAG){   
                if ("item".equals(typeName))   
                {   
                    Slist.add(Search_data);   
                    Search_data = null;   
                }else if("DOCUMENT".equals(typeName)){
                	result.put("maxpage", maxpage);
                	result.put("pagesize", pagesize);
                	result.put("pagenum", pagenum);
                	result.put("searchitem", Slist);
                }
            }else if (type == XmlPullParser.TEXT){   
                String s = parser.getText();   
//	                Log.i("text",s);
                if ("pagetotal".equals(currentTag)){   
                	maxpage = s;
                    currentTag = null;   
                }else if ("pagesize".equals(currentTag)){   
                	pagesize = s;  
                    currentTag = null;   
                }else if ("pagenum".equals(currentTag)){   
                	pagenum = s; 
                    currentTag = null;   
                }else if ("book".equals(currentTag)){   
                    Search_data.setbookname(s);  
                    currentTag = null;   
                }else if ("author".equals(currentTag)){   
                    Search_data.setauthor_name(s);  
                    currentTag = null;   
                }else if ("type".equals(currentTag)){   
                    Search_data.settype(s);  
                    currentTag = null;   
                }else if ("status".equals(currentTag)){   
                    Search_data.setstatus(Integer.parseInt(s));  
                    currentTag = null;   
                }else if ("desc".equals(currentTag)){   
                    Search_data.setdesc(s);  
                    currentTag = null;   
                }else if ("loc".equals(currentTag)){   
                    Search_data.setloc(Integer.parseInt(s));  
                    currentTag = null;   
                }else if ("picurl".equals(currentTag)){   
                    Search_data.setpicurl(s);  
                    currentTag = null;   
                }else if ("url".equals(currentTag)){   
                    Search_data.seturl(s);  
                    currentTag = null;   
                }else if ("chapter".equals(currentTag)){   
                    Search_data.setchapterlast(s);  
                    currentTag = null;   
                }else if ("chapterurl".equals(currentTag)){   
                    Search_data.setchapterurl(s);  
                    currentTag = null;   
                }else if ("chaptermd5".equals(currentTag)){   
                    Search_data.setChapter_md5(s);  
                    currentTag = null;   
                }else if ("chaptercid".equals(currentTag)){   
                    Search_data.setChapter_cid(s);  
                    currentTag = null;   
                }else if ("site".equals(currentTag)){   
                    Search_data.setsite(s);  
                    currentTag = null;   
                }else if ("date".equals(currentTag)){   
                    Search_data.setupdatetime(s);  
                    currentTag = null;   
                }
            }   
            type = parser.next();   
        }   
//	        Log.v("TAG", "size=" + list.size());   
        }catch(Exception e){
        	e.printStackTrace();
        }
        return result;   
    }
}
