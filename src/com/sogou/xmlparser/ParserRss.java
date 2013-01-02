package com.sogou.xmlparser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.sogou.xmldata.RssData;

public class ParserRss {

	public static List<RssData> getRssResultByXml(InputStream is) {
		// Log.i("pull parser","begin");
		List<RssData> Rlist = null;
		RssData Rss_data = new RssData();
		String currentTag = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(is, "utf8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				String typeName = parser.getName();
				if (type == XmlPullParser.START_TAG) {
					// Log.v("TAG", "typeName=" + typeName);
					if ("channel".equals(typeName)) {
						Rlist = new ArrayList<RssData>();
					} else if ("item".equals(typeName)) {
						Rss_data = new RssData();
					} else if ("description".equals(typeName)) {
						currentTag = "description";
					} else if ("id".equals(typeName)) {
						currentTag = "id";
					} else if ("title".equals(typeName)) {
						currentTag = "title";
					} else if ("source".equals(typeName)) {
						currentTag = "source";
					} else if ("link".equals(typeName)) {
						currentTag = "link";
					} else if ("author".equals(typeName)) {
						currentTag = "author";
					} else if ("guid".equals(typeName)) {
						currentTag = "guid";
					} else if ("pubDate".equals(typeName)) {
						currentTag = "pubDate";
					} else if ("comments".equals(typeName)) {
						currentTag = "comments";
					} else if ("enclosure".equals(typeName)) {
						currentTag = "enclosure";
					}
				} else if (type == XmlPullParser.END_TAG) {
					if ("item".equals(typeName)) {
						Rlist.add(Rss_data);
						Rss_data = null;
					} else if ("channel".equals(typeName)) {
						break;
					}
				} else if (type == XmlPullParser.TEXT) {
					String s = parser.getText();
					if ("count".equals(currentTag)) {
						currentTag = null;
					} else if ("id".equals(currentTag)) {
						Rss_data.setId(s);
						currentTag = null;
					} else if ("title".equals(currentTag)) {
						Rss_data.setTitle(s);
						currentTag = null;
					} else if ("source".equals(currentTag)) {
						Rss_data.setSource(s);
						currentTag = null;
					} else if ("link".equals(currentTag)) {
						Rss_data.setLink(s);
						currentTag = null;
					} else if ("author".equals(currentTag)) {
						Rss_data.setAuthor(s);
						currentTag = null;
					} else if ("guid".equals(currentTag)) {
						Rss_data.setGuid(s);
						currentTag = null;
					} else if ("comments".equals(currentTag)) {
						Rss_data.setComments(s);
						currentTag = null;
					} else if ("description".equals(currentTag)) {
						Rss_data.setDescription(s);
						currentTag = null;
					} else if ("enclosure".equals(currentTag)) {
						Rss_data.setUrl(s);
						currentTag = null;
					}
				}
				type = parser.next();
			}
			// Log.v("TAG", "size=" + list.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Rlist;
	}
}
