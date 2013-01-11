package org.carelife.creader.dao;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchData implements Parcelable{
	String book_name;
	String author_name;
	String type;
	int status;
	String desc;
	String pic_url;
	int loc;
	String url;
	String chapter_last;
	String chapter_url;
	String chapter_md5;
	String chapter_cid;
	String site;
	String update_time;
	
	public SearchData() {
		
	}

	public void setbookname(String bn){
		book_name = bn;
	}
	public String getbookname(){
		return book_name;
	}
	
	public void setauthor_name(String an){
		author_name = an;
	}
	public String getauthor_name(){
		return author_name;
	}
	
	public void settype(String ty){
		type = ty;
	}
	public String gettype(){
		return type;
	}
	
	public void setstatus(int s){
		status = s;
	}
	public int getstatus(){
		return status;
	}
	
	public void setdesc(String ds){
		desc = ds;
	}
	public String getdesc(){
		return desc;
	}
	
	public void setpicurl(String pu){
		pic_url = pu;
	}
	public String getpicurl(){
		return pic_url;
	}
	
	public void setloc(int l){
		loc = l;
	}
	public int getloc(){
		return loc;
	}
	
	public void seturl(String u){
		url = u;
	}
	public String geturl(){
		return url;
	}
	
	public void setchapterlast(String cl){
		chapter_last = cl;
	}
	public String getchapterlast(){
		return chapter_last;
	}
	
	public void setchapterurl(String cu){
		chapter_url = cu;
	}
	public String getchapterurl(){
		return chapter_url;
	}
	
	public String getChapter_md5() {
		return chapter_md5;
	}

	public void setChapter_md5(String chapter_md5) {
		this.chapter_md5 = chapter_md5;
	}

	public String getChapter_cid() {
		return chapter_cid;
	}

	public void setChapter_cid(String chapter_cid) {
		this.chapter_cid = chapter_cid;
	}

	public void setsite(String s){
		site = s;
	}
	public String getsite(){
		return site;
	}
	
	public void setupdatetime(String ut){
		update_time = ut;
	}
	public String getupdatetime(){
		return update_time;
	}
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(book_name);
		dest.writeString(author_name);
		dest.writeString(type);
		dest.writeInt(status);
		dest.writeString(desc);
		dest.writeString(pic_url);
		dest.writeInt(loc);
		dest.writeString(url);
		dest.writeString(chapter_last);
		dest.writeString(chapter_url);
		dest.writeString(chapter_md5);
		dest.writeString(chapter_cid);
		dest.writeString(site);
		dest.writeString(update_time);
	}
	
	public static final Parcelable.Creator<SearchData> CREATOR = new Parcelable.Creator<SearchData>() {  
        public SearchData createFromParcel(Parcel in) {  
            return new SearchData(in);  
        }  
  
        public SearchData[] newArray(int size) {  
            return new SearchData[size];  
        }  
    }; 
    
    private SearchData(Parcel in) {  
    	book_name = in.readString();  
    	author_name = in.readString();
    	type = in.readString();
    	status = in.readInt();
    	desc = in.readString();
    	pic_url = in.readString();
    	loc = in.readInt();
    	url = in.readString();
    	chapter_last = in.readString();
    	chapter_url = in.readString();
    	chapter_md5 = in.readString();
    	chapter_cid = in.readString();
    	site = in.readString();
    	update_time = in.readString();
    }  
}
