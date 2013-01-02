package com.sogou.data;

public class bookmark_chlid_data {
	public int id;
	public String book_name;
	public int chapter_num;
	public int begin_buf;
	public String update_time;
	public String percent;
	public int type;
	
	public bookmark_chlid_data(int id ,String book_name ,int chapter_num ,int begin_num ,String update_time ,String percent,int type){
		this.id = id;
		this.book_name = book_name;
		this.chapter_num = chapter_num;
		this.begin_buf = begin_num;
		this.update_time = update_time;
		this.percent = percent;
		this.type = type;
	}

}
