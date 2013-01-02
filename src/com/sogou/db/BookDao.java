package com.sogou.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sogou.constdata.ConstData;
import com.sogou.data.book_basic;
import com.sogou.data.chapter_basic;
import com.sogou.db.DatabaseHelper;
import com.sogou.util.XmlUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.util.Pair;

public class BookDao {

	private SQLiteDatabase db;
	private SQLiteDatabase insert_chapter_db;
	private SQLiteDatabase add_book_db;
	private SQLiteDatabase update_db;
	private SQLiteDatabase book_mark_db;
	private final Context context;

	private static BookDao instance;
	private DatabaseHelper dbHelper;
	private String sql;
	SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
    Date curDate;
    String time_str;
    
	
	private static final String TAG = "DBAdapter";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE_HISTORY = "CREATE TABLE IF NOT EXISTS search_history_table ( book_name text PRIMARY KEY DEFAULT '' )";
	private static final String DATABASE_CREATE_HOTWORD = "CREATE TABLE IF NOT EXISTS search_hot_table ( book_name text PRIMARY KEY DEFAULT '', update_time TIMESTAMP NOT NULL DEFAULT '0000-00-00' )";
	private static final String DATABASE_TABLE_HISTORY = "search_history_table";
	private static final String DATABASE_TABLE_HOTWORD = "search_hot_table";
	private static final String TABLE_SEARCH_KEY = "book_name";
	private static final String HOTWORD_VALUE = "update_time";

	public static BookDao getInstance(Context c) {
		if (instance == null) {
			instance = new BookDao(c);
		}
		return instance;
	}
    
	private BookDao(Context c) {
		this.context = c;
		this.dbHelper = new DatabaseHelper(this.context,"SogouNovel_db");
	}

	public void close() {
		db.close();
	}

	public void open() throws SQLiteException {
		try {
			db = dbHelper.getWritableDatabase();
			db.execSQL(DATABASE_CREATE_HISTORY);
			db.execSQL(DATABASE_CREATE_HOTWORD);

//			sql = "CREATE TABLE IF NOT EXISTS book_mark(" +
//					"id INTEGER PRIMARY KEY," +
//					"book_name VARCHAR(255) DEFAULT ''," +
//					"author_name VARCHAR(255) DEFAULT ''," +
//					"chapter_num INT(11) DEFAULT 0," +
//					"begin_buf INT(11) DEFAULT 0," +
//					"update_time TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00'," +
//					"percent VARCHAR(255) DEFAULT ''," +
//					"type INT(11) DEFAULT 0" +
//					")";
//			db.execSQL(sql);
			
			

			sql = "CREATE TABLE IF NOT EXISTS `book_table` (" +
					"`book_name` varchar(255)  DEFAULT '' ," +
					"`author_name` VARCHAR(255) DEFAULT ''," +
					"`create_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' ," +
					"`pic_path` varchar(255) DEFAULT '' ," +
					"`is_loc` int(11) DEFAULT 0 ," +
					"`is_update` int(11) DEFAULT 0 ," +
					"`update_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00'," +
					"`chapter_md5` text DEFAULT ''," +
					"`chapter_index` INT(11) DEFAULT 0," +
					"`begin_buf` INT(11) DEFAULT 0," +
					"`has_chapterlist` INT(11) DEFAULT 0," +
					"`book_md5` varchar(255) DEFAULT '' ," +
					"`need_post` INT(11) DEFAULT 0 ," +
					"`max_md5` varchar(255) DEFAULT '' ," +
					" PRIMARY KEY(book_name,author_name) )";

			db.execSQL(sql);
			

			sql = "CREATE TABLE IF NOT EXISTS `chapter_table` (" +
					"`book_name` varchar(255) DEFAULT '' ," +
					"`author_name` VARCHAR(255) DEFAULT '' ," +
					"`chapter_md5` VARCHAR(255) DEFAULT '' ," +
					"`create_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00' ," +
					"`chapter_index` int(11) DEFAULT 0 ," +
					"`chapter_name` varchar(255) DEFAULT '' ," +
					"`update_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00'," +
					"`is_download` int(11) DEFAULT 0 ," +
					" PRIMARY KEY(book_name,author_name,chapter_md5,chapter_index) )";

			db.execSQL(sql);

			
			sql = "CREATE TABLE IF NOT EXISTS `user_force_book` (" +
					"`book_name` varchar(255) DEFAULT '' ," +
					"`author_name` VARCHAR(255) DEFAULT '' ," +
					" PRIMARY KEY(book_name,author_name) )";

			db.execSQL(sql);
			
//			sql = "CREATE TABLE IF NOT EXISTS `book_mark` (" +
//					"`id` INTEGER PRIMARY KEY," +
//					"`book_name` VARCHAR(255) DEFAULT ''," +
//					"`author_name` VARCHAR(255) DEFAULT '' ," +
//					"`chapter_md5` VARCHAR(255) DEFAULT '' ," +
//					"`chapter_index` int(11) DEFAULT 0 ," +
//					"`begin_buf` INT(11) DEFAULT 0," +
//					"`update_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00'," +
//					"`percent` VARCHAR(255) DEFAULT ''," +
//					"`type` INT(11) DEFAULT 0" +
//					")";
//			db.execSQL(sql);
			
			
		} catch (SQLiteException ex) {
			db = dbHelper.getReadableDatabase();
		}
	}


	
	
	//book_table things
	
	public book_basic getBook(String book_name , String author_name){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		book_basic book = new book_basic();
		Cursor cursor = db.query("book_table", new String[]{"book_name","author_name","create_time","pic_path","is_loc","is_update",
				"update_time","chapter_md5","chapter_index","begin_buf","book_md5","has_chapterlist","need_post","max_md5"},
				"book_name=? and author_name=?", new String[]{book_name,author_name}, null, null, null);
		if(cursor == null) {
			db.close();
			return null;
		}
		if (cursor.getCount() != 0) {
			while(cursor.moveToNext()){
				book.setBook_name((String) cursor.getString(cursor.getColumnIndex("book_name")));
				book.setAuthor_name((String) cursor.getString(cursor.getColumnIndex("author_name")));
				book.setCreate_time((String) cursor.getString(cursor.getColumnIndex("create_time")));
				book.setPic_path((String) cursor.getString(cursor.getColumnIndex("pic_path")));
				book.setUpdate_time((String) cursor.getString(cursor.getColumnIndex("update_time")));
				book.setChapter_md5((String) cursor.getString(cursor.getColumnIndex("chapter_md5")));
				book.setBook_md5((String) cursor.getString(cursor.getColumnIndex("book_md5")));
				book.setIs_loc((int)cursor.getInt(cursor.getColumnIndex("is_loc")));
				book.setIs_update((int)cursor.getInt(cursor.getColumnIndex("is_update")));
				book.setChapter_index((int)cursor.getInt(cursor.getColumnIndex("chapter_index")));
				book.setBegin_buf((int)cursor.getInt(cursor.getColumnIndex("begin_buf")));
				book.setHas_chapterlist((int)cursor.getInt(cursor.getColumnIndex("has_chapterlist")));
				book.setNeed_post((int)cursor.getInt(cursor.getColumnIndex("need_post")));
				book.setMax_md5((String) cursor.getString(cursor.getColumnIndex("max_md5")));
			}
		}else{
			cursor.close();
			db.close();
			return null;
		}
		cursor.close();
		db.close();
		
		return book;
	}
	
	public List<book_basic> getBook_list(){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		ArrayList<book_basic> book_list = new ArrayList<book_basic>();
		Cursor cursor = db.query("book_table", new String[]{"book_name","author_name","create_time","pic_path","is_loc","is_update","update_time","chapter_md5","chapter_index","begin_buf","book_md5","has_chapterlist","need_post","max_md5"},null, null, null, null, "update_time");
		if(cursor == null) {
			db.close();
			return null;
		}
		if (cursor.getCount() != 0) {
			cursor.moveToLast();
			do{
				book_basic book = new book_basic();
				book.setBook_name((String) cursor.getString(cursor.getColumnIndex("book_name")));
				book.setAuthor_name((String) cursor.getString(cursor.getColumnIndex("author_name")));
				book.setCreate_time((String) cursor.getString(cursor.getColumnIndex("create_time")));
				book.setPic_path((String) cursor.getString(cursor.getColumnIndex("pic_path")));
				book.setUpdate_time((String) cursor.getString(cursor.getColumnIndex("update_time")));
				book.setChapter_md5((String) cursor.getString(cursor.getColumnIndex("chapter_md5")));
				book.setBook_md5((String) cursor.getString(cursor.getColumnIndex("book_md5")));
				book.setIs_loc((int)cursor.getInt(cursor.getColumnIndex("is_loc")));
				book.setIs_update((int)cursor.getInt(cursor.getColumnIndex("is_update")));
				book.setChapter_index((int)cursor.getInt(cursor.getColumnIndex("chapter_index")));
				book.setBegin_buf((int)cursor.getInt(cursor.getColumnIndex("begin_buf")));
				book.setHas_chapterlist((int)cursor.getInt(cursor.getColumnIndex("has_chapterlist")));
				book.setNeed_post((int)cursor.getInt(cursor.getColumnIndex("need_post")));
				book.setMax_md5((String) cursor.getString(cursor.getColumnIndex("max_md5")));
				book_list.add(book);
			}while(cursor.moveToPrevious());
//			while(cursor.moveToNext()){
//				book_basic book = new book_basic();
//				book.setBook_name((String) cursor.getString(cursor.getColumnIndex("book_name")));
//				book.setAuthor_name((String) cursor.getString(cursor.getColumnIndex("author_name")));
//				book.setCreate_time((String) cursor.getString(cursor.getColumnIndex("create_time")));
//				book.setPic_path((String) cursor.getString(cursor.getColumnIndex("pic_path")));
//				book.setUpdate_time((String) cursor.getString(cursor.getColumnIndex("update_time")));
//				book.setChapter_md5((String) cursor.getString(cursor.getColumnIndex("chapter_md5")));
//				book.setBook_md5((String) cursor.getString(cursor.getColumnIndex("book_md5")));
//				book.setIs_loc((int)cursor.getInt(cursor.getColumnIndex("is_loc")));
//				book.setIs_update((int)cursor.getInt(cursor.getColumnIndex("is_update")));
//				book.setChapter_index((int)cursor.getInt(cursor.getColumnIndex("chapter_index")));
//				book.setBegin_buf((int)cursor.getInt(cursor.getColumnIndex("begin_buf")));
//				book.setHas_chapterlist((int)cursor.getInt(cursor.getColumnIndex("has_chapterlist")));
//				book.setNeed_post((int)cursor.getInt(cursor.getColumnIndex("need_post")));
//				book.setMax_md5((String) cursor.getString(cursor.getColumnIndex("max_md5")));
//				book_list.add(book);
//			}
		}else{
			return null;
		}
		cursor.close();
		db.close();
		
		return book_list;
	}
	
	
	public String get_Bookmd5(String book_name,String author_name){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		String book_md5 = null;
		Cursor cursor = db.query("book_table", new String[]{"book_md5"},
				"book_name=? and author_name=?", new String[]{book_name,author_name}, null, null, null);
		if (null == cursor){
			db.close();
			return null;
		}
		if (cursor.getCount() != 0){
			while(cursor.moveToNext()){
				book_md5 =(String) cursor.getString(cursor.getColumnIndex("book_md5"));
			}
		}
		
		cursor.close();
		db.close();
		return book_md5;
	}
	
	
	
	public boolean Update_Bookmd5(book_basic book){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("book_table", new String[]{"book_name","author_name","create_time","pic_path","is_loc","is_update",
				"update_time","chapter_md5","chapter_index","begin_buf","book_md5","has_chapterlist","need_post","max_md5"},
				"book_name=? and author_name=?", new String[]{book.getBook_name(),book.getAuthor_name()}, null, null, null);
		if (null == cursor){
			db.close();
			return false;
		}
		if (cursor.getCount() != 0){
			sql ="UPDATE book_table SET book_md5 = '"+book.getBook_md5()+"' WHERE book_name = '"+book.getBook_name()+"' and author_name = '"+book.getAuthor_name()+"'";
			db.execSQL(sql);
		}else{
			curDate = new Date(System.currentTimeMillis());//获取当前时间
			time_str = formatter.format(curDate);
			sql ="insert into book_table (book_name,author_name,create_time,pic_path,is_loc,is_update,update_time,chapter_md5,chapter_index," +
					"begin_buf,book_md5,has_chapterlist,need_post,max_md5)values('"+book.getBook_name()+"','"+book.getAuthor_name()+"','"+time_str+"','"+book.getPic_path()+"'" +
							",'"+book.getIs_loc()+"','"+book.getIs_update()+"','"+time_str+"','"+book.getChapter_md5()+"','"+book.getChapter_index()+"'" +
									",'"+book.getBegin_buf()+"','"+book.getBook_md5()+"','"+book.getHas_chapterlist()+"','"+book.getNeed_post()+"','"+book.getMax_md5()+"')";
			db.execSQL(sql);
		}
		
		cursor.close();
		db.close();
		return true;
	}
	
	
	public boolean Update_Bookmd5_Noinsert(book_basic book){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("book_table", new String[]{"book_name","author_name","create_time","pic_path","is_loc","is_update",
				"update_time","chapter_md5","chapter_index","begin_buf","book_md5","has_chapterlist","need_post","max_md5"},
				"book_name=? and author_name=?", new String[]{book.getBook_name(),book.getAuthor_name()}, null, null, null);
		if (null == cursor){
			db.close();
			return false;
		}
		if (cursor.getCount() != 0){
			sql ="UPDATE book_table SET book_md5 = '"+book.getBook_md5()+"' WHERE book_name = '"+book.getBook_name()+"' and author_name = '"+book.getAuthor_name()+"'";
			db.execSQL(sql);
		}
		
		cursor.close();
		db.close();
		return true;
	}
	
	
	public boolean insert_book_mark(book_basic b){
		book_mark_db = dbHelper.getWritableDatabase();
		Cursor cursor = book_mark_db.query("book_table", new String[]{"book_name","author_name"},
				"book_name=? and author_name=?", new String[]{b.getBook_name(),b.getAuthor_name()}, null, null, null);
		if(cursor == null) {
			book_mark_db.close();
			return false;
		}
		curDate = new Date(System.currentTimeMillis());//获取当前时间
		time_str = formatter.format(curDate);
		if (cursor.getCount() != 0) {
			sql="update book_table set chapter_md5 = '"+b.getChapter_md5()+"'" +
					",chapter_index ='"+b.getChapter_index()+"',begin_buf = '"+b.getBegin_buf()+"',update_time ='"+time_str+"' " +
							" where book_name='"+b.getBook_name()+"' and author_name='"+b.getAuthor_name()+"'";
		}else{
			sql ="insert into book_table (book_name,author_name,pic_path,is_loc,create_time,is_update,update_time,chapter_md5,chapter_index,begin_buf,need_post,max_md5)" +
					"values('"+b.getBook_name()+"','"+b.getAuthor_name()+"','"+b.getPic_path()+"','"+b.getIs_loc()+"','"+time_str+"','"+b.getIs_update()+"','"+time_str+"'," +
							"'"+b.getChapter_md5()+"','"+b.getChapter_index()+"','"+b.getBegin_buf()+"','"+b.getNeed_post()+"','"+b.getMax_md5()+"')";
			
		}
		book_mark_db.execSQL(sql);
		cursor.close();
		book_mark_db.close();
		return true;
	}
	
	
	public boolean insert_book_mark_web_view(String book_name,String author_name,String url){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("book_table", new String[]{"book_name","author_name"},
				"book_name=? and author_name=?", new String[]{book_name,author_name}, null, null, null);
		if(cursor == null) {
			db.close();
			return false;
		}
		curDate = new Date(System.currentTimeMillis());//获取当前时间
		time_str = formatter.format(curDate);
		if (cursor.getCount() != 0) {
			sql="update book_table set chapter_md5 = '"+url+"'" +
					",update_time ='"+time_str+"' " +
							" where book_name='"+book_name+"' and author_name='"+author_name+"'";
		}else{
			sql ="insert into book_table (book_name,author_name,is_loc,create_time,update_time,chapter_md5)" +
					"values('"+book_name+"','"+author_name+"','0','"+time_str+"','"+time_str+"'," +
							"'"+url+"')";
			
		}
		db.execSQL(sql);
		cursor.close();
		db.close();
		return true;
	}
	
	
	public boolean insert_maxmd5(book_basic book){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("book_table", new String[]{"book_name"},
				"book_name=? and author_name=?", new String[]{book.getBook_name(),book.getAuthor_name()}, null, null, null);
		if (null == cursor){
			db.close();
			return false;
		}
		if (cursor.getCount() != 0){
			sql="update book_table set max_md5 = '"+book.getMax_md5()+"'" +
					" where book_name='"+book.getBook_name()+"' and author_name='"+book.getAuthor_name()+"'";
			db.execSQL(sql);
		}
		cursor.close();
		db.close();
		return true;
	}
	
	
	public List<chapter_basic> getChapter_list(String book_name , String author_name){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		ArrayList<chapter_basic> chapter_list = new ArrayList<chapter_basic>();
		Cursor cursor = db.query("chapter_table", new String[]{"book_name","author_name","create_time","chapter_index","chapter_name",
				"update_time","chapter_md5","is_download"},
				"book_name=? and author_name=?", new String[]{book_name,author_name}, null, null, "chapter_index");
		if(cursor == null) {
			db.close();
			return null;
		}
		if (cursor.getCount() != 0) {
			while(cursor.moveToNext()){
				chapter_basic chapter = new chapter_basic();
				chapter.setBook_name((String) cursor.getString(cursor.getColumnIndex("book_name")));
				chapter.setAuthor_name((String) cursor.getString(cursor.getColumnIndex("author_name")));
				chapter.setCreate_time((String) cursor.getString(cursor.getColumnIndex("create_time")));
				chapter.setUpdate_time((String) cursor.getString(cursor.getColumnIndex("update_time")));
				chapter.setChapter_md5((String) cursor.getString(cursor.getColumnIndex("chapter_md5")));
				chapter.setChapter_name((String) cursor.getString(cursor.getColumnIndex("chapter_name")));
				chapter.setChapter_index((int)cursor.getInt(cursor.getColumnIndex("chapter_index")));
				chapter.setIs_download((int)cursor.getInt(cursor.getColumnIndex("is_download")));
				chapter_list.add(chapter);
			}
		}else{
			return null;
		}
		cursor.close();
		db.close();
		
		return chapter_list;
	}
	
	public List<chapter_basic> getDownload_list(String book_name , String author_name){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		ArrayList<chapter_basic> chapter_list = new ArrayList<chapter_basic>();
		Cursor cursor = db.query("chapter_table", new String[]{"book_name","author_name","create_time","chapter_index","chapter_name",
				"update_time","chapter_md5","is_download"},
				"book_name=? and author_name=? and is_download=?", new String[]{book_name,author_name,"1"}, null, null, null);
		if(cursor == null) {
			db.close();
			return null;
		}
		if (cursor.getCount() != 0) {
			while(cursor.moveToNext()){
				chapter_basic chapter = new chapter_basic();
				chapter.setBook_name((String) cursor.getString(cursor.getColumnIndex("book_name")));
				chapter.setAuthor_name((String) cursor.getString(cursor.getColumnIndex("author_name")));
				chapter.setCreate_time((String) cursor.getString(cursor.getColumnIndex("create_time")));
				chapter.setUpdate_time((String) cursor.getString(cursor.getColumnIndex("update_time")));
				chapter.setChapter_md5((String) cursor.getString(cursor.getColumnIndex("chapter_md5")));
				chapter.setChapter_name((String) cursor.getString(cursor.getColumnIndex("chapter_name")));
				chapter.setChapter_index((int)cursor.getInt(cursor.getColumnIndex("chapter_index")));
				chapter.setIs_download((int)cursor.getInt(cursor.getColumnIndex("is_download")));
				chapter_list.add(chapter);
			}
		}else{
			cursor.close();
			db.close();
			return null;
		}
		cursor.close();
		db.close();
		
		return chapter_list;
	}
	
	
	public boolean Update_Download(List<chapter_basic> chapter_list){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		for (int i = 0 ; i < chapter_list.size() ; i++){
			
			sql = "update chapter_table set is_download = 1 where book_name='"+chapter_list.get(i).getBook_name()+"' and author_name='"+chapter_list.get(i).getAuthor_name()+"'";
			db.execSQL(sql);
		}
		db.close();
		return true;
	}
	
	
	public boolean insert_chapter(List<chapter_basic> chapter_list){
		
		if(null == chapter_list ||chapter_list.size() <= 0){
			return false;
		}
		
		insert_chapter_db = dbHelper.getWritableDatabase();
		
		String book_name = chapter_list.get(0).getBook_name();
		String author_name = chapter_list.get(0).getAuthor_name();
		
		
//		ArrayList<chapter_basic> old_chapter_list = new ArrayList<chapter_basic>();
//		old_chapter_list = (ArrayList<chapter_basic>) getDownload_list(book_name,author_name);
		
		sql="delete from chapter_table where book_name='"+book_name+"' and author_name='"+author_name+"'";
		insert_chapter_db.execSQL(sql);

		//开启事务,提高效率
		insert_chapter_db.beginTransaction();
		for (int i = 0; i < chapter_list.size() ; i++){
			chapter_basic c = chapter_list.get(i);
			try {
				curDate = new Date(System.currentTimeMillis());//获取当前时间
				time_str = formatter.format(curDate);
				sql ="insert into chapter_table (book_name,author_name,chapter_md5,chapter_name,chapter_index,is_download,create_time,update_time)" +
						"values('"+c.getBook_name()+"','"+c.getAuthor_name()+"','"+c.getChapter_md5()+"','"+c.getChapter_name()+"','"+c.getChapter_index()+"'," +
								"'"+c.getIs_download()+"','"+time_str+"','"+time_str+"')";
//				System.out.println(sql);
				insert_chapter_db.execSQL(sql);
			} catch (Exception e) {
				// TODO: handle exception
				insert_chapter_db.setTransactionSuccessful();
				insert_chapter_db.endTransaction();
				System.out.println("insert chapter failed!");
				insert_chapter_db.close();
				return false;
			}
			
		}
		insert_chapter_db.setTransactionSuccessful();
		insert_chapter_db.endTransaction();
//		if(null != old_chapter_list){
//			Update_Download(old_chapter_list);
//		}
		insert_chapter_db.close();
		return true;
	}
	
	
	public chapter_basic getChapter_from_index(String book_name , String author_name ,int index){
	
		//TODO
		SQLiteDatabase db_temp = dbHelper.getWritableDatabase();
		chapter_basic chapter = new chapter_basic();
		
		Cursor cursor = db_temp.query("chapter_table", new String[]{"book_name","author_name","create_time","chapter_index","chapter_name",
				"update_time","chapter_md5","is_download"},
				"book_name=? and author_name=? and chapter_index=?", new String[]{book_name,author_name,""+index}, null, null, null);
		if(cursor == null) {
			db_temp.close();
			return null;
		}
		if (cursor.getCount() != 0) {
			while(cursor.moveToNext()){
				chapter.setBook_name((String) cursor.getString(cursor.getColumnIndex("book_name")));
				chapter.setAuthor_name((String) cursor.getString(cursor.getColumnIndex("author_name")));
				chapter.setCreate_time((String) cursor.getString(cursor.getColumnIndex("create_time")));
				chapter.setUpdate_time((String) cursor.getString(cursor.getColumnIndex("update_time")));
				chapter.setChapter_md5((String) cursor.getString(cursor.getColumnIndex("chapter_md5")));
				chapter.setChapter_name((String) cursor.getString(cursor.getColumnIndex("chapter_name")));
				chapter.setChapter_index((int)cursor.getInt(cursor.getColumnIndex("chapter_index")));
				chapter.setIs_download((int)cursor.getInt(cursor.getColumnIndex("is_download")));
			}
		}else{
			cursor.close();
			db_temp.close();
			return null;
		}
		cursor.close();
		db_temp.close();
		
		return chapter;
	}
	
	
	public chapter_basic cheak_chapter(String book_name , String author_name ,int index ,String md5){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("chapter_table", new String[]{"book_name"},
				"book_name=? and author_name=? and chapter_index=? and chapter_md5=?", new String[]{book_name,author_name,""+index,md5}, null, null, null);
		if(cursor != null && cursor.getCount() != 0){
			cursor.close();
			db.close();
			return null;
		}
		chapter_basic chapter = new chapter_basic();
		cursor = db.query("chapter_table", new String[]{"book_name","author_name","create_time","chapter_index","chapter_name",
				"update_time","chapter_md5","is_download"},
				"book_name=? and author_name=? and chapter_md5=?", new String[]{book_name,author_name,md5}, null, null, null);
		if(cursor != null && cursor.getCount() != 0) {
			while(cursor.moveToNext()){
				chapter.setBook_name((String) cursor.getString(cursor.getColumnIndex("book_name")));
				chapter.setAuthor_name((String) cursor.getString(cursor.getColumnIndex("author_name")));
				chapter.setCreate_time((String) cursor.getString(cursor.getColumnIndex("create_time")));
				chapter.setUpdate_time((String) cursor.getString(cursor.getColumnIndex("update_time")));
				chapter.setChapter_md5((String) cursor.getString(cursor.getColumnIndex("chapter_md5")));
				chapter.setChapter_name((String) cursor.getString(cursor.getColumnIndex("chapter_name")));
				chapter.setChapter_index((int)cursor.getInt(cursor.getColumnIndex("chapter_index")));
				chapter.setIs_download((int)cursor.getInt(cursor.getColumnIndex("is_download")));
			}
		}else{
			cursor = db.query("chapter_table", new String[]{"book_name","author_name","create_time","chapter_index","chapter_name",
					"update_time","chapter_md5","is_download"},
					"book_name=? and author_name=? and chapter_index=?", new String[]{book_name,author_name,""+index}, null, null, null);
			if(cursor != null && cursor.getCount() != 0) {
				while(cursor.moveToNext()){
					chapter.setBook_name((String) cursor.getString(cursor.getColumnIndex("book_name")));
					chapter.setAuthor_name((String) cursor.getString(cursor.getColumnIndex("author_name")));
					chapter.setCreate_time((String) cursor.getString(cursor.getColumnIndex("create_time")));
					chapter.setUpdate_time((String) cursor.getString(cursor.getColumnIndex("update_time")));
					chapter.setChapter_md5((String) cursor.getString(cursor.getColumnIndex("chapter_md5")));
					chapter.setChapter_name((String) cursor.getString(cursor.getColumnIndex("chapter_name")));
					chapter.setChapter_index((int)cursor.getInt(cursor.getColumnIndex("chapter_index")));
					chapter.setIs_download((int)cursor.getInt(cursor.getColumnIndex("is_download")));
				}
				//else return null chapter
			}
		}
		
		cursor.close();
		db.close();
		return chapter;
	}
	
	public int get_chapter_count(String book_name , String author_name){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("chapter_table", new String[]{"book_name"},
				"book_name=? and author_name=?", new String[]{book_name,author_name}, null, null, null);
		if(cursor == null){
			db.close();
			return 0;
		}
		
		return cursor.getCount();
	}
	
	
	public List<String> get_book_needupdate(){
		
		update_db = dbHelper.getWritableDatabase();
		Cursor cursor;
		List<String> temp_bookupdate = new ArrayList<String>();
		
		cursor = update_db.query("book_table", new String[]{"book_name"},
				"need_post=?", new String[]{"1"}, null, null, null);
		if(null != cursor && cursor.getCount() != 0){
			while(cursor.moveToNext()){
				String book_name = (String) cursor.getString(cursor.getColumnIndex("book_name"));
				temp_bookupdate.add(book_name);
			}
			
		}
		cursor.close();
		update_db.close();
		
		return temp_bookupdate;
	}
	
	
	public List<String> get_book_update(){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor;
		List<String> temp_bookupdate = new ArrayList<String>();
		
		cursor = db.query("book_table", new String[]{"book_name"},
				"is_update=?", new String[]{"1"}, null, null, null);
		if(null != cursor && cursor.getCount() != 0){
			while(cursor.moveToNext()){
				String book_name = (String) cursor.getString(cursor.getColumnIndex("book_name"));
				temp_bookupdate.add(book_name);
			}
			cursor.close();
		}else if (cursor.getCount() == 0){
			cursor.close();
		}
		
		db.close();
		
		return temp_bookupdate;
	}
	
	public boolean set_book_needupdate(book_basic b){
		
		if(null == b){
			return false;
		}
		
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		
		Pair<String, String> user_force = new Pair<String, String>(b.getBook_name(), b.getAuthor_name());
		
		if(cheak_user_force(user_force)){
			return true;
		}
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		String sql;
		sql ="UPDATE book_table SET need_post = 1 WHERE book_name = '"+b.getBook_name()+"' and author_name = '"+b.getAuthor_name()+"'";
		db.execSQL(sql);
		
		db.close();
		
		return true;
	}
	
	public boolean set_book_needupdate_force(book_basic b){
		
		if(null == b){
			return false;
		}
		
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		
		Pair<String, String> user_fource = new Pair<String, String>(b.getBook_name(), b.getAuthor_name());
		
		insert_user_force(user_fource);
		
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		
		String sql;
		sql ="UPDATE book_table SET need_post = 1 WHERE book_name = '"+b.getBook_name()+"' and author_name = '"+b.getAuthor_name()+"'";
		db.execSQL(sql);
		
		db.close();
		
		return true;
	}
	
	
	
	public boolean set_book_update(book_basic book){
		
		if(null == book){
			return false;
		}
		
		update_db = dbHelper.getWritableDatabase();
		String sql;
		
		Cursor cursor = update_db.query("book_table", new String[]{"book_name","author_name","is_loc","is_update",
				"need_post","max_md5"},
				"book_name=?", new String[]{book.getBook_name()}, null, null, null);
		if(cursor == null) {
			update_db.close();
			return false;
		}
		int temp_index = 0;
		int max_index = 0;
		if (cursor.getCount() != 0) {
			
			while(cursor.moveToNext()){
				max_index++;
				//不是一本书
				int is_loc  = (int)cursor.getInt(cursor.getColumnIndex("is_loc"));
				if(book.getIs_loc() != is_loc){
					temp_index++;
					continue;
				}
				//已经更新过不再提醒
				if(1 == (int)cursor.getInt(cursor.getColumnIndex("is_update"))){
					if(book.getMax_md5().equals((String) cursor.getString(cursor.getColumnIndex("max_md5")))){
						cursor.close();
						update_db.close();
						return false;
					}else{
						cursor = update_db.query("chapter_table", new String[]{"book_name"},
								"chapter_md5=? and book_name=?", new String[]{book.getMax_md5(),book.getBook_name()}, null, null, null);
						if (cursor.getCount() != 0){
							sql ="UPDATE book_table SET max_md5 = '"+book.getMax_md5()+"' WHERE book_name = '"+book.getBook_name()+"' and is_loc = "+book.getIs_loc()+"";
							update_db.execSQL(sql);
							cursor.close();
							update_db.close();
							return false;
						}
						sql ="UPDATE book_table SET is_update = 1,max_md5 = '"+book.getMax_md5()+"' WHERE book_name = '"+book.getBook_name()+"' and is_loc = "+book.getIs_loc()+"";
						update_db.execSQL(sql);
					}
				}else{
					if(book.getMax_md5().equals((String) cursor.getString(cursor.getColumnIndex("max_md5")))){
						cursor.close();
						update_db.close();
						return false;
					}else{
						cursor = update_db.query("chapter_table", new String[]{"book_name"},
								"chapter_md5=? and book_name=?", new String[]{book.getMax_md5(),book.getBook_name()}, null, null, null);
						if (cursor.getCount() != 0){
							sql ="UPDATE book_table SET max_md5 = '"+book.getMax_md5()+"'  WHERE book_name = '"+book.getBook_name()+"' and is_loc = "+book.getIs_loc()+"";
							update_db.execSQL(sql);
							cursor.close();
							update_db.close();
							return false;
						}
						sql ="UPDATE book_table SET is_update = 1,max_md5 = '"+book.getMax_md5()+"'  WHERE book_name = '"+book.getBook_name()+"' and is_loc = "+book.getIs_loc()+"";
						update_db.execSQL(sql);
					}
				}
				
				
			}
			
			
		}else{
			cursor.close();
			update_db.close();
			return false;
		}
		cursor.close();
		update_db.close();
		if(temp_index == max_index){
			return false;
		}
		return true;
	}
	
	public void test_sd(){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor = db.query("chapter_table", new String[]{"book_name"},
				"chapter_md5=? and book_name=?", new String[]{"973e5991337130b4","吞噬星空"}, null, null, null);
		System.out.println(cursor.getCount());
		if (cursor.getCount() != 0){
			System.out.println("hi");
		}
		cursor.close();
		db.close();
	}
	
	public int del_update(book_basic b){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		sql ="UPDATE book_table SET is_update = 0 WHERE book_name = '"+b.getBook_name()+"' and author_name = '"+b.getAuthor_name()+"'";
		int flag = 0;
		try {
			db.execSQL(sql);
		} catch (Exception e) {
			// TODO: handle exception
			flag = -1;
		}
		db.close();
		
		return flag;
	}
	
	public int del_needupdate(book_basic b){
		if(b == null){
			return -1;
		}
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		sql ="UPDATE book_table SET need_post = 0 WHERE book_name = '"+b.getBook_name()+"' and author_name = '"+b.getAuthor_name()+"'";
		int flag = 0;
		try {
			db.execSQL(sql);
		} catch (Exception e) {
			// TODO: handle exception
			flag = -1;
		}
		db.close();
		
		return flag;
	}
	
	
	public List<Pair<String,String>> get_user_force(){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor;
		List<Pair<String,String>> temp_bookforce = new ArrayList<Pair<String,String>>();
		
		cursor = db.query("user_force_book", new String[]{"book_name","author_name"},
				null, null, null, null, null);
		if(null != cursor && cursor.getCount() != 0){
			while(cursor.moveToNext()){
				String book_name = (String) cursor.getString(cursor.getColumnIndex("book_name"));
				String author_name = (String) cursor.getString(cursor.getColumnIndex("author_name"));
				temp_bookforce.add(new Pair<String, String>(book_name, author_name));
			}
			cursor.close();
		}
		db.close();
		
		return temp_bookforce;
	}
	
	public boolean cheak_user_force(Pair<String, String> book_pair){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor;
		
		cursor = db.query("user_force_book", new String[]{"book_name","author_name"},
				"book_name=? and author_name=?", new String[]{book_pair.first,book_pair.second}, null, null, null);
		if(cursor == null) {
			db.close();
			return false;
		}
		if (cursor.getCount() != 0) {
			db.close();
			cursor.close();
			return true;
		}else{
			db.close();
			cursor.close();
			return false;
		}
		
	}
	
	public boolean insert_user_force(Pair<String, String> book_pair){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		Cursor cursor;
		
		cursor = db.query("user_force_book", new String[]{"book_name","author_name"},
				"book_name=? and author_name=?", new String[]{book_pair.first,book_pair.second}, null, null, null);
		if(cursor == null) {
			db.close();
			return false;
		}
		if (cursor.getCount() != 0) {
			db.close();
			cursor.close();
			return true;
		}else{
			
			String sql = "insert into user_force_book(book_name,author_name)values('"+book_pair.first+"','"+book_pair.second+"')";
			db.execSQL(sql);
			
			db.close();
			cursor.close();
			return true;
		}
		
	}
	
	
	public boolean add_book(book_basic b){
		add_book_db = dbHelper.getWritableDatabase();
		book_basic book = new book_basic();
		curDate = new Date(System.currentTimeMillis());//获取当前时间
		time_str = formatter.format(curDate);
		Cursor cursor = add_book_db.query("book_table", new String[]{"book_name"},
				"book_name=? and author_name=?", new String[]{b.getBook_name(),b.getAuthor_name()}, null, null, null);
		if(cursor == null) {
			add_book_db.close();
			return false;
		}
		if (cursor.getCount() != 0) {
			sql="update book_table set chapter_md5 = '"+b.getChapter_md5()+"'" +
					",chapter_index ='"+b.getChapter_index()+"',is_loc = '"+b.getIs_loc()+"'" +
					",pic_path ='"+b.getPic_path()+"',begin_buf = '"+b.getBegin_buf()+"',update_time ='"+time_str+"' " +
							" where book_name='"+b.getBook_name()+"' and author_name='"+b.getAuthor_name()+"' and max_md5 ='"+b.getMax_md5()+"'";
			cursor.close();
			add_book_db.execSQL(sql);
			add_book_db.close();
			return true;
			
		}else{
			try {
				
				sql ="insert into book_table (book_name,author_name,pic_path,is_loc,create_time,is_update,update_time,chapter_md5,chapter_index,begin_buf,need_post,max_md5)" +
						"values('"+b.getBook_name()+"','"+b.getAuthor_name()+"','"+b.getPic_path()+"','"+b.getIs_loc()+"','"+time_str+"','"+b.getIs_update()+"','"+time_str+"'," +
								"'"+b.getChapter_md5()+"','"+b.getChapter_index()+"','"+b.getBegin_buf()+"','"+b.getNeed_post()+"','"+b.getMax_md5()+"')";
				add_book_db.execSQL(sql);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("data exist!");
			}
			
		}
		cursor.close();
		add_book_db.close();
		
		return true;
	}
	
	public void delete_book(String book_name , String author_name){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		sql="delete from book_table where book_name = '"+book_name+"' and author_name = '"+author_name+"'";
		db.execSQL(sql);
		
		db.close();
	}
	
	
	public void update_book_time(String book_name , String author_name){
		if(db == null){
			this.open();  
		}
		curDate = new Date(System.currentTimeMillis());//获取当前时间
		time_str = formatter.format(curDate);
		db = dbHelper.getWritableDatabase();
		sql="update book_table set update_time = '"+time_str+"' where book_name = '"+book_name+"' and author_name = '"+author_name+"'";
		db.execSQL(sql);
		
		db.close();
	}
	
	
	public void test_data(){
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
//		sql = " delete from book_table";
//		execSQL(sql);
		sql =" insert into book_table (book_name,author_name,is_loc,book_md5)values('西游记','罗贯中',1,'sadas412')";
		db.execSQL(sql);
		Cursor cursor = db.query("book_table", new String[]{"book_name","author_name","create_time","pic_path","is_loc","is_update",
				"update_time","chapter_md5","chapter_index","begin_buf","book_md5","has_chapterlist","need_post","max_md5"},
				"book_name=? and author_name=?", new String[]{"西游记","罗贯中"}, null, null, null);
		if(cursor == null) {
			System.out.println("cursor null");
		}
		if (cursor.getCount() != 0) {
			while(cursor.moveToNext()){
				System.out.println((String) cursor.getString(cursor.getColumnIndex("book_name")));
			}
		}else{
			System.out.println("cursor len is 0");
			
		}
		cursor.close();
		
	}
	
	//test interface 之后要改成update
	public int update_data(String name){
		sql ="update book_table set is_update = 1 where book_name = '"+name+"'";
		db.execSQL(sql);
		return 0;
	}
	
	
	public  Cursor get_book_mark(book_basic book){
		
		return db.query("book_mark", new String[]{"chapter_num","begin_buf"},
				"book_name=? and author_name=? and type=?", new String[]{book.getBook_name(),book.getAuthor_name(),"1"}, null, null, null);
	}
	
	// operation of History TABLE
	public long insertHistory(String content) {
		if(db == null){
			this.open();
		}
		db = dbHelper.getWritableDatabase();
		
		db.delete(DATABASE_TABLE_HISTORY, "book_name=?", new String[] {content});
		ContentValues initialValues = new ContentValues();
		initialValues.put(TABLE_SEARCH_KEY, content);
		long ret = db.insert(DATABASE_TABLE_HISTORY, null, initialValues);
		db.close();
		return ret;
	}

	public List<Map<String,Object>> getHistory()  {
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		ArrayList<Map<String,Object>> books = new ArrayList<Map<String,Object>>();
		Cursor cur = db.query(DATABASE_TABLE_HISTORY, new String[] {
				TABLE_SEARCH_KEY }, null, null, null, null, null);
		if(cur == null) {
			db.close();
			return null;
		}
		if (cur.getCount() != 0) {
			cur.moveToLast();
			do {
				HashMap<String, Object> tmp = new HashMap<String, Object>();
				tmp.put("bookname", cur.getString(0));
				books.add(tmp);
			} while (cur.moveToPrevious());
		}
		cur.close();
		db.close();
		return books;
	}

	public int delHistorySearch(String bookname) {
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		int ret= db.delete(DATABASE_TABLE_HISTORY, "book_name=?", new String[] {bookname});
		db.close();
		return ret;
	}
	
	public long insertHotword(List<String> contents) {
		long ret = 0;
		if(db == null){
			this.open();
		}
		db = dbHelper.getWritableDatabase();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");    
		String date =sdf.format(new java.util.Date());    
		for(String content : contents) {
			ContentValues initialValues = new ContentValues();
			initialValues.put(TABLE_SEARCH_KEY, content);
			initialValues.put(HOTWORD_VALUE, date);
			ret = db.insert(DATABASE_TABLE_HOTWORD, null, initialValues);
		}
//		db.close();
		return ret;
	}

	public List<String> getHotwords()  {
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		
	    SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
		
		ArrayList<String> books = new ArrayList<String>();
		Cursor cur = db.query(DATABASE_TABLE_HOTWORD, new String[] {
				TABLE_SEARCH_KEY, HOTWORD_VALUE }, null, null, null, null, null);
		if(cur == null) {
			//TODO 从网上拿数据，然后塞进去
			db.close();
			return null;
		}
		if (cur.getCount() != 0) {
			cur.moveToFirst();
			String date =myFormatter.format(new java.util.Date());    
			long days = 0;
			try {
				java.util.Date dataDate = myFormatter.parse(cur.getString(1));
				java.util.Date currentDate= myFormatter.parse(date);
				days=(currentDate.getTime()-dataDate.getTime())/(24*60*60*1000);
			} catch (ParseException e) {
				e.printStackTrace();
			} 
			if(days<=7) {
				do {
					books.add(cur.getString(0));
				} while (cur.moveToNext());
			} else {
				db.execSQL("DELETE FROM "+DATABASE_TABLE_HOTWORD, new Object[]{});
				cur.close();
				return null;
			}
			cur.close();
			db.close();
			return books;
		} else {
			cur.close();
			db.close();
			return null;
		}

	}

	public void clearHotwordsTable() {
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		db.execSQL("DELETE FROM "+DATABASE_TABLE_HOTWORD);
		db.close();
	}
	
	public void testHistoryTable() {
		if(db == null){
			this.open();  
		}
		db = dbHelper.getWritableDatabase();
		Cursor cur = db.query(DATABASE_TABLE_HISTORY, new String[] {
				TABLE_SEARCH_KEY }, null, null, null, null, null);
		if(cur == null) {
			db.close();
			return;
		}
		if (cur.getCount() != 0) {
			cur.moveToFirst();
			do {
				Log.e("DB_History",cur.getString(0));
			} while (cur.moveToNext());
		}
		cur.close();
		db.close();
	}
}
