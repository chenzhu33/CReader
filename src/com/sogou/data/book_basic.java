package com.sogou.data;

import com.sogou.util.FileUtil;
import android.os.Parcel;
import android.os.Parcelable;

public class book_basic implements Parcelable{

	private String book_name;
	private String author_name;
	private String create_time;
	private String pic_path;
	private int is_loc;
	private int is_update;
	private String update_time;
	private String chapter_md5;
	private int chapter_index;
	private int begin_buf;
	private String book_md5;
	private int has_chapterlist;
	private int need_post;
	private String max_md5;
	
	public book_basic(){
//		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
//		String time_str = formatter.format(curDate);
//		create_time = time_str;
//		update_time = time_str;
	}
	
	public book_basic(String book_name ,String author_name ,String book_md5) {
		// TODO Auto-generated constructor stub
		this.book_name = book_name;
		this.author_name = author_name;
		this.book_md5 = book_md5;
	}
	
	public book_basic(String book_name ,String author_name ,String chapter_md5 ,int chapter_index) {
		// TODO Auto-generated constructor stub
		this.book_name = book_name;
		this.author_name = author_name;
		this.chapter_md5 = chapter_md5;
		this.chapter_index = chapter_index;
		this.begin_buf = 0;
	}
	
	
	public String getChapter_md5() {
		return chapter_md5;
	}
	public void setChapter_md5(String chapter_md5) {
		this.chapter_md5 = chapter_md5;
	}
	public int getChapter_index() {
		return chapter_index;
	}
	public void setChapter_index(int chapter_index) {
		this.chapter_index = chapter_index;
	}
	public int getBegin_buf() {
		return begin_buf;
	}
	public void setBegin_buf(int begin_buf) {
		this.begin_buf = begin_buf;
	}
	public String getBook_md5() {
		return book_md5;
	}
	public void setBook_md5(String book_md5) {
		this.book_md5 = book_md5;
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
	public String getPic_path() {
		return pic_path;
	}
	public void setPic_path(String pic_path) {
		this.pic_path = pic_path;
	}
	public int getIs_loc() {
		return is_loc;
	}
	public void setIs_loc(int is_loc) {
		this.is_loc = is_loc;
	}
	public int getIs_update() {
		return is_update;
	}
	public void setIs_update(int is_update) {
		this.is_update = is_update;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public int getHas_chapterlist() {
		return has_chapterlist;
	}

	public void setHas_chapterlist(int has_chapterlist) {
		this.has_chapterlist = has_chapterlist;
	}  
	
	
	public int getNeed_post() {
		return need_post;
	}

	public void setNeed_post(int need_post) {
		this.need_post = need_post;
	}

	public String getMax_md5() {
		return max_md5;
	}

	public void setMax_md5(String max_md5) {
		this.max_md5 = max_md5;
	}

	public String getChapter_path(){
		
		return FileUtil.new_dir + FileUtil.cheak_string(book_name) + "_" +  FileUtil.cheak_string(author_name) + "/" + chapter_md5 + ".txt";
		
	}
	
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(book_name);
		dest.writeString(author_name);
		dest.writeString(create_time);
		dest.writeInt(is_loc);
		dest.writeString(pic_path);
		dest.writeInt(is_update);
		dest.writeString(update_time);
		dest.writeInt(chapter_index);
		dest.writeString(chapter_md5);
		dest.writeInt(begin_buf);
		dest.writeString(book_md5);
		dest.writeInt(has_chapterlist);
		dest.writeInt(need_post);
		dest.writeString(max_md5);
	}
	
	public static final Parcelable.Creator<book_basic> CREATOR = new Parcelable.Creator<book_basic>() {  
        public book_basic createFromParcel(Parcel in) {  
            return new book_basic(in);  
        }  
  
        public book_basic[] newArray(int size) {  
            return new book_basic[size];  
        }  
    }; 
    
    private book_basic(Parcel in) {  
    	book_name = in.readString();  
    	author_name = in.readString();
    	create_time = in.readString();
    	is_loc = in.readInt();
    	pic_path = in.readString();
    	is_update = in.readInt();
    	update_time = in.readString();
    	chapter_index = in.readInt();
    	chapter_md5 = in.readString();
    	begin_buf = in.readInt();
    	book_md5 = in.readString();
    	has_chapterlist = in.readInt();
    	need_post = in.readInt();
    	max_md5 = in.readString();
    }

	
	
}
