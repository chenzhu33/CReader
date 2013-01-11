package org.carelife.creader.dao;

import org.carelife.creader.R;

public class UrlHelper {
	public static String[] book_cate = { "玄幻", "言情", "都市", "仙侠", "校园", "耽美",
			"游戏", "悬疑", "科幻", "军史", "同人", "其他" };

	public static String[] goto_data = { "xuanhuan", "yanqing", "dushi",
			"xianxia", "xiaoyuan", "danmei", "youxi", "xuanyi", "kehuan",
			"junshi", "tongren", "qita" };

	public static String[] book_rank = { "本日排行", "本月排行", "本周排行", "人气排行",
			"新书排行", "连载排行", "全本排行" };

	public static String[] goto_data_rank = { "benri", "benyue", "benzhou",
			"renqi", "xinshu", "lianzai", "quanben" };

	public static String goto_data_search = "resou";

	public static String[] newsGroupName = { "nation", "social", "internation",
			"weapon", "sport", "fastnews" };
	
	public static int[] backgroundColor = { R.drawable.list_bg_white,
			R.drawable.list_bg_gray };
	
	public static int[] backgroundViewSelector = { R.drawable.listview_white_selector,
		R.drawable.listview_gray_selector };
	
	public static String cover_string = "cache.ch";
	public static String key_string = "sogounovel";
	public static boolean dbg = true;
	
	public static String rankurl = "http://wap.sogou.com/book/sgapp_ranking.jsp?rank=";
	public static String searchurl = "http://wap.sogou.com/book/sgapp_search.jsp?keyword=";
	public static String tc_url = "http://wap.sogou.com/pr=a/tc?url=";
	public static String chapter_url = "http://readerapi.wap.sogou.com/novelDirServlet?";
	public static String context_url = "http://readerapi.wap.sogou.com/novelDetailServlet?";
	public static String version_url = "http://app.m.sogou.com/sogou_novel_update.xml";
	public static String feedback_url = "http://app.m.sogou.com/app/dofeed_app_novel.jsp";
	public static String uploadUrl="http://app.m.sogou.com/app/uploadserver_novel.jsp";
	public static String updateUrl = "http://wap.sogou.com/book/sgapp_query_json.jsp";

	public static String[][] newsurl = {
		{ 
				"http://m.sohu.com/api/rss/fragment/?ids=1444&count=6",
				"http://m.sohu.com/api/rss/fragment/?ids=1801&count=10",
				"http://m.sohu.com/api/rss/fragment/?ids=804&count=10" },
		{
				"http://m.sohu.com/api/rss/fragment/?ids=1154&count=6",
				"http://m.sohu.com/api/rss/channel/60/?count=15&content_type=1",
				"http://m.sohu.com/api/rss/channel/64/?count=15&content_type=1" },
		{
				"http://m.sohu.com/api/rss/fragment/?ids=1150&count=6",
				"http://m.sohu.com/api/rss/channel/499/?count=15&content_type=2",
				"http://m.sohu.com/api/rss/channel/199/?count=15&content_type=2" },
		{
				"http://m.sohu.com/api/rss/fragment/?ids=1802&count=10",
				"http://m.sohu.com/api/rss/channel/178/?count=15&content_type=1",
				"http://m.sohu.com/api/rss/channel/749/?count=15&content_type=2" },
		{
				"http://m.sohu.com/api/rss/fragment/?ids=1638&count=3",
				"http://m.sohu.com/api/rss/channel/538/?count=15&content_type=2",
				"http://m.sohu.com/api/rss/fragment/?ids=1137&count=16" },
		{ 		
				"http://m.sohu.com/api/rss/fragment/?ids=1601&count=10",
				"http://m.sohu.com/api/rss/fragment/?ids=1146&count=6",
				"http://m.sohu.com/api/rss/fragment/?ids=286&count=20" },
		{ 
				"http://m.sohu.com/api/rss/fragment/?ids=168&count=17",
				"http://m.sohu.com/api/rss/fragment/?ids=440&count=8",
				"http://m.sohu.com/api/rss/channel/262/?count=15&content_type=2" },
		{
				"http://m.sohu.com/api/rss/fragment/?ids=138&count=8",
				"http://m.sohu.com/api/rss/channel/223/?count=15&content_type=1",
				"http://m.sohu.com/api/rss/channel/221/?count=15&content_type=1" },
		{
				"http://m.sohu.com/api/rss/fragment/?ids=156&count=6",
				"http://m.sohu.com/api/rss/channel/451/?count=15&content_type=1",
				"http://m.sohu.com/api/rss/channel/532/?count=15&content_type=2" },
		{
				"http://m.sohu.com/api/rss/fragment/?ids=1669&count=14",
				"http://m.sohu.com/api/rss/channel/403/?count=15&content_type=1",
				"http://m.sohu.com/api/rss/channel/408/?count=15&content_type=2" },
		{
				"http://m.sohu.com/api/rss/channel/610/?count=15&content_type=1",
				"http://m.sohu.com/api/rss/channel/125/?count=15&content_type=1",
				"http://m.sohu.com/api/rss/fragment/?ids=1840&count=7" },
		{
				"http://m.sohu.com/api/rss/channel/116/?count=15&content_type=1",
				"http://m.sohu.com/api/rss/channel/156/?count=15&content_type=1",
				"http://m.sohu.com/api/rss/channel/111/?count=15&content_type=1" } };
	
}