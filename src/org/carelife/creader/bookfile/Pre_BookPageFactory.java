package org.carelife.creader.bookfile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.util.DesUtil;


public class Pre_BookPageFactory {
	
	private File book_file = null;
	private int m_mbBufLen = 0;
	private MappedByteBuffer m_mbBuf = null;
	private boolean isopenfile = false;
	
	public Pre_BookPageFactory() {
		// TODO Auto-generated constructor stub
	}
	
	
	public void openbook(String strFilePath) throws Exception {
		DesUtil ds = new DesUtil(UrlHelper.key_string);
		File temp_f = new File(strFilePath);
		File temp_dir = new File(temp_f.getParent()+"/temp/");
		if(!temp_dir.exists()){
			temp_dir.mkdirs();
		}
        ds.decryptFile(strFilePath, temp_dir.getPath()+"/"+temp_f.getName()+".ds");
		book_file = new File(temp_dir.getPath()+"/"+temp_f.getName()+".ds");
		long lLen = book_file.length();
		m_mbBufLen = (int) lLen;
		m_mbBuf = new RandomAccessFile(book_file, "r").getChannel().map(
				FileChannel.MapMode.READ_ONLY, 0, lLen);
//		book_file.delete();
		isopenfile = true;
	}
	
	public boolean get_isopenfile(){
		return isopenfile;
	}
	
	public void set_null(){
		if(m_mbBuf != null){
			m_mbBuf.clear();
		}
		isopenfile = false;
		return;
	}
	
	public MappedByteBuffer get_m_mbBuf(){
		return m_mbBuf;
	}
	
	public int get_m_mbBufLen(){
		return m_mbBufLen;
	}
	
	public File getBookFile(){
		return book_file;
	}
	
	public void chapter_copy(BookPageFactory book){
//		if(m_mbBuf != null){
//			m_mbBuf.clear();
//		}
		m_mbBuf = book.get_m_mbBuf();
		m_mbBufLen = book.get_m_mbBufLen();
		if(book_file != null){
			book_file.delete();
		}
		book_file = book.getBookFile();
		isopenfile = true;
		return;
	}
	
	
	public void set_m_mbBuf(MappedByteBuffer b){
		if(m_mbBuf != null){
			m_mbBuf.clear();
		}
		m_mbBuf = b;
		return;
	}
	
	public void set_m_mbBufLen(int l){
		m_mbBufLen = l;
		return ;
	}

	public void Destory(){
		if(m_mbBuf != null){
			m_mbBuf.clear();
			m_mbBuf = null;
		}
		m_mbBufLen = 0;
		if(book_file != null){
			book_file.delete();
			book_file = null;
		}
	}
	
}
