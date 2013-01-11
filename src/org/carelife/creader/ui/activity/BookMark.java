package org.carelife.creader.ui.activity;

import java.io.IOException;
import org.carelife.creader.R;
import java.util.ArrayList;
import java.util.List;

import org.carelife.creader.bean.BookMarkChildBean;
import org.carelife.creader.bean.BookMarkGroupBean;
import org.carelife.creader.db.DatabaseHelper;
import org.carelife.creader.ui.adapter.BookmarkAdapter;
import org.carelife.creader.util.FileUtil;



import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;


public class BookMark extends Activity{
	
	private ExpandableListView bookmark_list;
	private BookmarkAdapter adapter;
	private DatabaseHelper dbHelper;
	public SQLiteDatabase db;
	private String sql;
	private SharedPreferences sp;
	private Editor edit;
	FileUtil fm;
	
	List<BookMarkGroupBean> book_mark_group = new ArrayList<BookMarkGroupBean>();
	List<List<BookMarkChildBean>> book_mark_child = new ArrayList<List<BookMarkChildBean>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		System.out.println("onCreate!");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookmark);
		fm = new FileUtil();
		dbinit();
	}
	
	@Override
	protected void onDestroy() {
//		System.out.println("onDestroy!");
		super.onDestroy();
		db.close();
		
	}
	
	@Override
	protected void onStop() {
//		System.out.println("onStop!");
		
		super.onStop();
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		bookmark_list = (ExpandableListView) findViewById(R.id.bookmark_list);
		sp = getSharedPreferences("sogounovel", MODE_PRIVATE);
		edit = sp.edit();
		book_mark_group = group_data_init();
		book_mark_child = child_data_init();
		adapter = new BookmarkAdapter(this ,book_mark_group ,book_mark_child);
		adapter.changedata(this, book_mark_group, book_mark_child);
		bookmark_list.setAdapter(adapter);
		bookmark_list.setOnChildClickListener(new OnChildClickListener() {
			
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				int chapter_maxnum = 0;
				try {
					chapter_maxnum = fm.get_chapter_maxnum_fromname(book_mark_group.get(groupPosition).book_name);
				} catch (IOException e) {
					e.printStackTrace();
				}
				edit.putInt("chapter_maxnum", chapter_maxnum);
				edit.putInt("chapterindex_now_reading", book_mark_child.get(groupPosition).get(childPosition).chapter_num);
				edit.putString("book_now_reading", book_mark_group.get(groupPosition).book_name);
				edit.putInt("book_mark", book_mark_child.get(groupPosition).get(childPosition).begin_buf);
				edit.commit();
				Intent intent = new Intent(BookMark.this,SogouNovelActivity.class);
				startActivity(intent);
				return false;
			}
		});
		if(book_mark_group.size() != 0){
			bookmark_list.expandGroup(0);
		}
		
		
	}
	
	
	public void dbinit(){
		dbHelper = new DatabaseHelper(BookMark.this,"Novel_db");
		db = dbHelper.getWritableDatabase();
		sql = "CREATE TABLE IF NOT EXISTS `book_mark` (" +
				"`id` INTEGER PRIMARY KEY," +
				"`book_name` VARCHAR(255) DEFAULT ''," +
				"`chapter_num` INT(11) DEFAULT 0," +
				"`begin_buf` INT(11) DEFAULT 0," +
				"`update_time` TIMESTAMP NOT NULL DEFAULT '0000-00-00 00:00:00'," +
				"`percent` VARCHAR(255) DEFAULT ''," +
				"`type` INT(11) DEFAULT 0" +
				")";
		db.execSQL(sql);
	}
	
	public List<BookMarkGroupBean> group_data_init(){
		//groupdata
		Cursor cursor = db.query("book_mark", new String[]{"book_name"},
				null, null, "book_name", null, "update_time");
		List<BookMarkGroupBean> book_mark_group_temp = new ArrayList<BookMarkGroupBean>();
		
		cursor.moveToLast();
		while(cursor.getCount() > 0){
			String temp_str = cursor.getString(cursor.getColumnIndex("book_name"));
			book_mark_group_temp.add(new BookMarkGroupBean(temp_str,fm.getpic_frombookname(temp_str)));
			if(!cursor.moveToPrevious()){
				break;
			}
		}
		cursor.close();
		return book_mark_group_temp;
	}
	
	public List<List<BookMarkChildBean>> child_data_init(){
		//childdata
		Cursor cursor;
		List<List<BookMarkChildBean>> book_mark_child_temp = new ArrayList<List<BookMarkChildBean>>();
		for (int i = 0 ; i < book_mark_group.size() ; i++){
			List<BookMarkChildBean> book_mark_clild_data_temp = new ArrayList<BookMarkChildBean>();
			cursor = db.query("book_mark", new String[]{"id","book_name","chapter_num","begin_buf","update_time","percent","type"},
					"book_name=?", new String[]{(String) book_mark_group.get(i).book_name}, null, null, "update_time");
			//tpye=1 是人工添加的书签  type=2是自动记录的最后阅读位置
			cursor.moveToLast();
			while(cursor.getCount() > 0){
				cursor.getShort(cursor.getColumnIndex("id"));
				cursor.getString(cursor.getColumnIndex("book_name"));
				
				book_mark_clild_data_temp.add(new BookMarkChildBean(cursor.getShort(cursor.getColumnIndex("id")) ,
						cursor.getString(cursor.getColumnIndex("book_name")) ,
						cursor.getShort(cursor.getColumnIndex("chapter_num")) ,
						cursor.getShort(cursor.getColumnIndex("begin_buf")) ,
						cursor.getString(cursor.getColumnIndex("update_time")) ,
						cursor.getString(cursor.getColumnIndex("percent")),
						cursor.getShort(cursor.getColumnIndex("type")) ));
				if(!cursor.moveToPrevious()){
					break;
				}
			}
			book_mark_child_temp.add(book_mark_clild_data_temp);
			cursor.close();
		}
		
		return book_mark_child_temp;
		
	}
	
	
	
	
}
