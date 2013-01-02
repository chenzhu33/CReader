package com.sogou.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.sogou.constdata.ConstData;
import com.sogou.xmldata.RssData;
import com.sogou.xmldata.SearchData;
import com.sogou.xmldata.VersionData;
import com.sogou.xmlparser.ParserRank;
import com.sogou.xmlparser.ParserRssSX;
import com.sogou.xmlparser.ParserSearch;
import com.sogou.xmlparser.ParserVersion;

public class XmlUtil {
	public static HashMap<String, Object> getXML(String cate, int st, int len)
			throws IOException {
		HashMap<String, Object> result = null;
		String Url = ConstData.rankurl + cate + "&start=" + st + "&length="
				+ len;
		try {
			URL urlStr = new URL(Url);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) urlStr
					.openConnection();
			InputStream input = httpUrlConnection.getInputStream();
			if (input == null) {
				return null;
			}
			// 开始解析文件
			result = ParserRank.getRankResultByXml(input);
			if (null != httpUrlConnection)
				httpUrlConnection.disconnect();
		} catch (MalformedURLException err) {
			err.printStackTrace();
		}
		return result;
	}

	public static HashMap<String, Object> getXML(String url, String query,
			String additon, int type) throws IOException {
		HashMap<String, Object> result = null;
		String Url = url + query + additon;
		try {
			URL urlStr = new URL(Url);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) urlStr
					.openConnection();
			InputStream input = httpUrlConnection.getInputStream();
			if (input == null) {
				return null;
			}
			// 开始解析文件
			switch (type) {
			case 0:
				result = ParserSearch.getSearchResultByXml(input);
				break;
			case 1:
				result = ParserRank.getRankResultByXml(input);
				break;
			default:
				break;
			}
			if (null != httpUrlConnection)
				httpUrlConnection.disconnect();
		} catch (MalformedURLException err) {
			err.printStackTrace();
		}
		return result;
	}

	public static List<RssData> getNewsXML(String url) throws IOException {
		List<RssData> result = null;
		URL urlStr = null;
		try {
			urlStr = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		HttpURLConnection httpUrlConnection = null;
		httpUrlConnection = ((HttpURLConnection) urlStr.openConnection());
		InputStream input = null;
		input = httpUrlConnection.getInputStream();

		if (input == null) {
			return null;
		}
		ParserRssSX handler = new ParserRssSX();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		// 开始解析文件
		try {
			saxParser.parse(input, handler);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		result = handler.getRss();
		try {
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;

		// List<RssData> result = null;
		// String Url = url;
		// try {
		// URL urlStr = new URL(Url);
		// HttpURLConnection httpUrlConnection = (HttpURLConnection) urlStr
		// .openConnection();
		// InputStream input = httpUrlConnection.getInputStream();
		// // 开始解析文件
		// result = ParserRss.getRssResultByXml(input);
		// if (null != httpUrlConnection)
		// httpUrlConnection.disconnect();
		// } catch (Exception err) {
		// err.printStackTrace();
		// }
		// return result;
	}

	public static List<VersionData> getVersionXML(String url)
			throws IOException {
		List<VersionData> result = null;
		String Url = url;

		URL urlStr = new URL(Url);
		HttpURLConnection httpUrlConnection = (HttpURLConnection) urlStr
				.openConnection();
		InputStream input = httpUrlConnection.getInputStream();
		if (input == null) {
			return null;
		}
		// 开始解析文件
		result = ParserVersion.getPersonsByParseXml(input);
		if (null != httpUrlConnection)
			httpUrlConnection.disconnect();

		return result;
	}

	public static SearchData getSearchOneXML(String bookname_one) {
		HashMap<String, Object> result = null;

		String Url = null;
		try {
			Url = ConstData.searchurl
					+ URLEncoder.encode(bookname_one, "utf-8") + "&fixpos=0";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			URL urlStr = new URL(Url);
			HttpURLConnection httpUrlConnection = (HttpURLConnection) urlStr
					.openConnection();
			InputStream input = httpUrlConnection.getInputStream();
			if (input == null) {
				return null;
			}
			// 开始解析文件
			result = ParserSearch.getSearchResultByXml(input);
			if (null != httpUrlConnection)
				httpUrlConnection.disconnect();
		} catch (Exception err) {
			err.printStackTrace();
		}
		if (result != null) {
			List<SearchData> data_list = (List<SearchData>) result
					.get("searchitem");

			if (data_list.size() < 1) {
				return null;
			}
			return data_list.get(0);
		} else {
			return null;
		}
	}
}
