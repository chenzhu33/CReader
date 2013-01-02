package com.sogou.adapter;


import java.io.IOException;
import com.sogou.R;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sogou.data.*;
import com.sogou.db.DatabaseHelper;
import com.sogou.sogounovel.BookMark;
import com.sogou.util.FileUtil;

public class BookmarkAdapter extends BaseExpandableListAdapter {
	private List<List<bookmark_chlid_data>> child_data;
	private List<bookmark_group_data> group_data;
	Context context;
	GroupViewHolder groupholder;
	ChildViewHolder childholder;
	BookMark Bookmark;
	private DatabaseHelper dbHelper;
	public SQLiteDatabase db;
	private String sql;
	private SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
	private Date curDate;
	private String time_str;
	private int flag_sql;
	FileUtil fm;
	
	public BookmarkAdapter(Context context ,List<bookmark_group_data> group_data, List<List<bookmark_chlid_data>> child_data ){
		this.context = context;
		this.child_data = child_data;
		this.group_data = group_data;
		fm = new FileUtil();
	}
	
	private void initdb(){
		dbHelper = new DatabaseHelper(context,"Novel_db");
		db = dbHelper.getWritableDatabase();
	}

	private void closedb(){
		db.close();
	}
	
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}


	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return (child_data!= null) ? child_data.get(groupPosition).size() : 0;
	}

	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	public int getGroupCount() {
		// TODO Auto-generated method stub
		return (group_data!= null) ? group_data.size() : 0;
	}

	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null ){
			convertView = View.inflate(context, R.layout.bookmarkgroup, null);
			groupholder = new GroupViewHolder();
			groupholder.book_name = (TextView)convertView.findViewById(R.id.bookmark_bookname);
			groupholder.delbook = (Button)convertView.findViewById(R.id.book_mark_deletebook);
			groupholder.book_pic = (ImageView) convertView.findViewById(R.id.bookmark_bookpic);
			convertView.setTag(groupholder);
		}else{
			groupholder = (GroupViewHolder)convertView.getTag();
		}
		final String book_name = group_data.get(groupPosition).book_name;
		groupholder.book_name.setText(book_name);
		groupholder.book_pic.setImageBitmap(group_data.get(groupPosition).bm);
		groupholder.delbook.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sql = "delete from book_mark where book_name = '"+book_name+"'";
				flag_sql = 1;
				initdb();
				try {
					db.execSQL(sql);
				} catch (Exception e) {
					// TODO: handle exception
					flag_sql = 0;
					System.out.println("del book fail!");
				}
				if(flag_sql == 1){
					group_data.remove(groupPosition);
					BookmarkAdapter.this.notifyDataSetChanged();
				}
				closedb();
			}
		});
		return convertView;
	}
	
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null ){
			convertView = View.inflate(context, R.layout.bookmarkchild, null);
			childholder = new ChildViewHolder();
			childholder.chapter_name = (TextView)convertView.findViewById(R.id.bookmark_chaptername);
			childholder.update_time = (TextView)convertView.findViewById(R.id.bookmark_updatetime);
			childholder.delchapter = (Button)convertView.findViewById(R.id.book_mark_deletechapter);
			childholder.type_pic = (ImageView) convertView.findViewById(R.id.bookmark_type);
			convertView.setTag(childholder);
		}else{
			childholder = (ChildViewHolder)convertView.getTag();
		}
			//待修改，读文件写成中文名
			String chaptername = ""+child_data.get(groupPosition).get(childPosition).chapter_num;
			try {
				chaptername = fm.get_chapter_name_fornum(group_data.get(groupPosition).book_name, 
						child_data.get(groupPosition).get(childPosition).chapter_num);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (chaptername == "0"){
				return convertView;
			}
			int temp_type = child_data.get(groupPosition).get(childPosition).type;
			String type_str = "";
			switch (temp_type) {
				case 2:
					type_str = "(上次阅读)";
					break;
				case 1:
					type_str = "(书签)";
					break;
				default:
					break;
			}
			if(temp_type == 2){
				childholder.type_pic.setImageResource(R.drawable.auto_mark);
			}else{
				childholder.type_pic.setImageResource(R.drawable.ic_action_search);
			}
			String percent = child_data.get(groupPosition).get(childPosition).percent;
			if (percent.length() < 3){
				percent = "  "+percent;
			}
			childholder.chapter_name.setText(type_str + chaptername );
			childholder.update_time.setText("["+percent+"] "+child_data.get(groupPosition).get(childPosition).update_time);
			childholder.delchapter.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					int id = child_data.get(groupPosition).get(childPosition).id;
					sql = "delete from book_mark where id ="+id;
					flag_sql = 1;
					initdb();
					try {
						db.execSQL(sql);
					} catch (Exception e) {
						// TODO: handle exception
						flag_sql = 0;
						System.out.println("del chapter fail!");
					}
					if(flag_sql == 1){
						child_data.get(groupPosition).remove(childPosition);
						BookmarkAdapter.this.notifyDataSetChanged();
					}
					closedb();
				}
			});
		return convertView;
	}
	
	public void changedata(Context context ,List<bookmark_group_data> group_data, List<List<bookmark_chlid_data>> child_data ){
		this.context = context;
		this.child_data = child_data;
		this.group_data = group_data;
		BookmarkAdapter.this.notifyDataSetChanged();
	}
	
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	public class GroupViewHolder
	{
		private TextView book_name;
		private ImageView book_pic;
		private Button delbook;
	}
	public class ChildViewHolder
	{
		private ImageView type_pic;
		private TextView chapter_name;
		private TextView update_time;
		private Button delchapter;
	}
	
}
