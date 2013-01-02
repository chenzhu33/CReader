package com.sogou.xmlparser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.sogou.xmldata.VersionData;


public class ParserVersion 
{   
  
    public static List<VersionData> getPersonsByParseXml(InputStream is)   
    {   
//    	Log.i("pull parser","begin");
        List<VersionData> list = null;   
        VersionData r = null;   
        String currentTag = null;   
        try{
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();   
        XmlPullParser parser = factory.newPullParser();   
        parser.setInput(is, "utf-8");   
        int type = parser.getEventType();   
        while (type != XmlPullParser.END_DOCUMENT)   
        {   
            String typeName = parser.getName();   
            if (type == XmlPullParser.START_TAG)   
            {   
//                Log.v("TAG", "typeName=" + typeName);   
                if ("version".equals(typeName))   
                {   
                   list = new ArrayList<VersionData>();   
                   r = new VersionData();  
                } else if ("versioncode".equals(typeName))   
                {   
                    currentTag="versioncode";
                } else if ("content".equals(typeName))   
                {   
                    currentTag = "content";   
                } else if ("updateurl".equals(typeName))   
                {   
                    currentTag = "updateurl";   
                } else if("control".equals(typeName))
                {
                	currentTag = "control";
                }
            } else if (type == XmlPullParser.END_TAG)   
            {   
                if ("version".equals(typeName))   
                {   
                    list.add(r);   
                    r = null;   
                }   
            } else if (type == XmlPullParser.TEXT)   
            {   
                String s = parser.getText();   
//                Log.i("text",s);
                if ("versioncode".equals(currentTag))   
                {   
                    r.setVersionCode(s);
                    currentTag = null;   
                } else if ("content".equals(currentTag))   
                {   
                    r.setContent(s);  
                    currentTag = null;   
                } else if("updateurl".equals(currentTag)){
                	r.setUpdate_url(s);
                	currentTag = null;  
                } else if("control".equals(currentTag)){
                	r.setControl(Integer.parseInt(s.trim()));
                	currentTag = null;
                }
            }   
            type = parser.next();   
        }   
//        Log.v("TAG", "size=" + list.size());   
        }catch(Exception e){
        	e.printStackTrace();
        }
        return list;   
    }   
}


