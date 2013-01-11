package org.carelife.creader.xmlparser;

import java.util.ArrayList;
import java.util.List;

import org.carelife.creader.dao.RssData;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class RssSXParser extends DefaultHandler {
	

	private String tagName; // 当前解析的元素标签
	private List<RssData> Rss_list;
	private RssData rss  = new RssData();
	private String url = null;
	
	public List<RssData> getRss() {
		return Rss_list;
	}

	public void setversion(List<RssData> Rss_list) {
		this.Rss_list = Rss_list;
	}
	public void startDocument() throws SAXException {
//		System.out.println("begin to parse xml file!");
		Rss_list = new ArrayList<RssData>();
	}

	public void endDocment() throws SAXException {
//		System.out.println("end of pase!");
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attr) throws SAXException {
		if (localName.equals("item")) {
			rss = new RssData();
		}else if(localName.equals("enclosure")){
			url = null;
			url = attr.getValue("url");
			rss.setUrl(url);
		}
		this.tagName = localName;
	}
	
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		tagName = "";
		if (localName.equals("item")) {
			Rss_list.add(rss);
			rss = null;
		}
		this.tagName = null;
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String data = null; 
		if(tagName != null){
			data = new String(ch,start,length);
//			System.out.println("tagName"+tagName);
			if(tagName.equals("title")){	
				if(data != null && !data.trim().equals(""))
				this.rss.setTitle(data);
			}else if(tagName.equals("id")){
				if(data != null && !data.trim().equals(""))
				this.rss.setId(data);
			}else if(tagName.equals("source")){
				if(data != null && !data.trim().equals(""))
				this.rss.setSource(data);
			}else if(tagName.equals("link")){
				if(data != null && !data.trim().equals(""))
				this.rss.setLink(data);
			}else if(tagName.equals("author")){
				if(data != null && !data.trim().equals(""))
				this.rss.setAuthor(data);
			}else if(tagName.equals("guid")){
				if(data != null && !data.trim().equals(""))
				this.rss.setGuid(data);
			}else if(tagName.equals("pubDate")){
				if(data != null && !data.trim().equals(""))
				this.rss.setPubDate(data);
			}else if(tagName.equals("comments")){
				if(data != null && !data.trim().equals(""))
				this.rss.setComments(data);
			}else if(tagName.equals("description")){
				if(data != null && !data.trim().equals(""))
				this.rss.setDescription(data);
			}
			
			
		}
	}
}