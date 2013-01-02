package com.sogou.xmldata;

public class RankData {
	String book_name;
	String author_name;
	String type;
	int status;
	String desc;
	String pic_url;

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
	
}
