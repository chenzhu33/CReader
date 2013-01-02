package com.sogou.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.sogou.util.FileUtil;

public class chapter_basic implements Parcelable{
	private String book_name;
	private String author_name;
	private String create_time;
	private String update_time;
	private String chapter_name;
	private String chapter_md5;
	private int is_download;
	private int chapter_index;
	
	public chapter_basic(){
//		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
//		String time_str = formatter.format(curDate);
//		create_time = time_str;
//		update_time = time_str;
	}
	
	public chapter_basic(String book_name ,String author_name ,String chapter_name ,String chapter_md5 ,int chapter_index,int is_download) {
		// TODO Auto-generated constructor stub
		this.book_name = book_name;
		this.author_name = author_name;
		this.chapter_name = chapter_name;
		this.chapter_md5 = chapter_md5;
		this.chapter_index = chapter_index;
		this.is_download = is_download;
		
//		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
//		String time_str = formatter.format(curDate);
//		create_time = time_str;
//		update_time = time_str;
	}
	
	public String getBook_name() {
		return book_name;
	}
	public void setBook_name(String book_name) {
		this.book_name = book_name;
	}
	public String getAuthor_name() {
		return author_name;
	}
	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getChapter_name() {
		return chapter_name;
	}
	public void setChapter_name(String chapter_name) {
		this.chapter_name = chapter_name;
	}
	public int getChapter_index() {
		return chapter_index;
	}
	public void setChapter_index(int chapter_index) {
		this.chapter_index = chapter_index;
	}
	public int getIs_download() {
		return is_download;
	}
	public void setIs_download(int is_download) {
		this.is_download = is_download;
	}
	public String getChapter_md5() {
		return chapter_md5;
	}
	public void setChapter_md5(String chapter_md5) {
		this.chapter_md5 = chapter_md5;
	}
	
	
	public String getChapter_path(){
		
		return FileUtil.new_dir +  FileUtil.cheak_string(book_name) + "_" +  FileUtil.cheak_string(author_name) + "/" + chapter_md5 + ".txt";
		
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(book_name);
		dest.writeString(author_name);
		dest.writeString(create_time);
		dest.writeString(chapter_name);
		dest.writeInt(is_download);
		dest.writeString(update_time);
		dest.writeString(chapter_md5);
		dest.writeInt(chapter_index);
		
	}
	
	
	public static final Parcelable.Creator<chapter_basic> CREATOR = new Parcelable.Creator<chapter_basic>() {  
        public chapter_basic createFromParcel(Parcel in) {  
            return new chapter_basic(in);  
        }  
  
        public chapter_basic[] newArray(int size) {  
            return new chapter_basic[size];  
        }  
    }; 
    
    private chapter_basic(Parcel in) {  
    	book_name = in.readString();  
    	author_name = in.readString();
    	create_time = in.readString();
    	chapter_name = in.readString();
    	is_download = in.readInt();
    	update_time = in.readString();
    	chapter_md5 = in.readString();
    	chapter_index = in.readInt();
    }
	
	
}
