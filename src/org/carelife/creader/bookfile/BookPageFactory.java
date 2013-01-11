package org.carelife.creader.bookfile;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.carelife.creader.bean.BookLineBean;
import org.carelife.creader.dao.UrlHelper;
import org.carelife.creader.util.DesUtil;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;

public class BookPageFactory {

	private File book_file = null;
	private MappedByteBuffer m_mbBuf = null;
	private int m_mbBufLen = 0;
	private int m_mbBufBegin = 0;
	private int m_mbBufEnd = 0;
	private String m_strCharsetName = "GBK";
	private int mWidth;
	private int mHeight;
	
	private Bitmap m_book_bg_day = null;
	private Bitmap m_book_bg_night = null;
	private int m_textColor_day = Color.BLACK;
	private int m_textColor_night = 0xffd5c9bb;
//	private int m_textColor_night = 0xffE6E6FA;
	private boolean flag_day = true;
	
	private Vector<BookLineBean> m_lines = new Vector<BookLineBean>();

	private int m_fontSize = 24; //默认字号 
	private int m_backColor = 0xffff9e85; 
	private int marginWidth = 15;
	private int drawmarginWidth = 17;
	private int marginHeight = 20; //顶部空隙
	private int marginHeight2 = 35; //底部空隙

	private int mLineCount; 
	private float mVisibleHeight;
	private float mVisibleWidth; 
	private boolean m_isfirstPage,m_islastPage;
	
	private Matrix matrix;
	
	private boolean isopenfile = false;
	private String book_name ="";
	private float level = 1;
	
	private int banner_size = 24;
	private int batter_width = 2;
	private int batter_c = 27;
	private int batter_k = 18;
	private int batter_nc = 21;
	private int batter_nk = 12;
	private int batter_b = 3;
	private int batter_tb = 4;
	private int batter_tc = 5;
	private int batter_tk = 10;
	
	
	private int m_nLineSpaceing = 0;

	private Paint mPaint;
	SimpleDateFormat formatter = new SimpleDateFormat ("HH:mm");
	
	private static BookPageFactory instance;
	
	public static BookPageFactory get_Instance(){
		if (instance == null){
			instance = new BookPageFactory();
		}
		return instance;
	}
	
	public void setScreen(int w, int h){
		mWidth = w;
		mHeight = h;
		int wl = h > w ? w : h;
		mVisibleWidth = mWidth - marginWidth * 2;
		mVisibleHeight = mHeight - marginHeight - (int) (marginHeight2*((float)wl*0.9/480));
		mLineCount = (int) (mVisibleHeight / (m_fontSize+m_nLineSpaceing));
		banner_size = (int) (24*((float)wl*0.8/480));
		batter_width = (int) (2*((float)wl*0.8/480));
		batter_c = (int) (27*((float)wl*0.8/480));
		batter_k = (int) (18*((float)wl*0.8/480));
		batter_nc = (int) (21*((float)wl*0.8/480));
		batter_nk = (int) (12*((float)wl*0.8/480));
		batter_b = (int) (3*((float)wl*0.8/480));
		batter_tb = (int) (4*((float)wl*0.8/480));
		batter_tc = (int) (5*((float)wl*0.8/480));
		batter_tk = (int) (10*((float)wl*0.8/480));
	}

	private BookPageFactory() {
		// TODO Auto-generated constructor stub
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Align.LEFT);
		mPaint.setTextSize(m_fontSize);
		mPaint.setColor(m_textColor_day);
	}
	
	public boolean changefront(int fs){
		m_fontSize = fs;
		mPaint.setTextSize(m_fontSize);
		mLineCount = (int) (mVisibleHeight / (m_fontSize+m_nLineSpaceing));
		return true;
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
	
	protected byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		byte b0, b1;
		if (m_strCharsetName.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}

		} else if (m_strCharsetName.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				b1 = m_mbBuf.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else {
			i = nEnd - 1;
			while (i > 0) {
				b0 = m_mbBuf.get(i);
				//0x0a是"\n" 0x0d是"\r"
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
			//fix when i = 0 m_mbBuf.get(0)='\n' make page_up replace fault;
//			if(i == 0 && m_mbBuf.get(0) == 0x0a){
//				i = 1;
//			}
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = m_mbBuf.get(i + j);
		}
		return buf;
	}


	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		if (m_strCharsetName.equals("UTF-16LE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (m_strCharsetName.equals("UTF-16BE")) {
			while (i < m_mbBufLen - 1) {
				b0 = m_mbBuf.get(i++);
				b1 = m_mbBuf.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (i < m_mbBufLen) {
				b0 = m_mbBuf.get(i++);
				if (b0 == 0x0a) {
					break;
				}
			}
		}
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			buf[i] = m_mbBuf.get(nFromPos + i);
		}
		return buf;
	}

	protected Vector<BookLineBean> pageDown() {
//		System.out.println("down ---begin ="+m_mbBufBegin);
		String strParagraph = "";
		Vector<BookLineBean> lines = new Vector<BookLineBean>();
		while (lines.size() < mLineCount && m_mbBufEnd < m_mbBufLen) {
			byte[] paraBuf = readParagraphForward(m_mbBufEnd); // 取下一段
			m_mbBufEnd += paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strReturn = "";
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}
			
			//填文章中的空行
			if (strParagraph.length() == 0) {
				lines.add(new BookLineBean(strParagraph,0,0));
			}
			while (strParagraph.length() > 0) {
				strParagraph = strParagraph.replaceAll("\t", " ");
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				int usedWidth = (int) mPaint.measureText(strParagraph.substring(0, nSize));
				lines.add(new BookLineBean(strParagraph.substring(0, nSize),nSize,usedWidth));
				strParagraph = strParagraph.substring(nSize);
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			if (strParagraph.length() != 0) {
				try {
					m_mbBufEnd -= (strParagraph + strReturn)
							.getBytes(m_strCharsetName).length;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
//		System.out.println("down ---end ="+m_mbBufEnd);
		return lines;
	}

	protected void pageUp() {
		if (m_mbBufBegin < 0)
			m_mbBufBegin = 0;
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && m_mbBufBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readParagraphBack(m_mbBufBegin);
			//fix i = 0 bug
			if(paraBuf.length == 0){
				break;
			}
			m_mbBufBegin -= paraBuf.length;
			try {
				strParagraph = new String(paraBuf, m_strCharsetName);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");

			if (strParagraph.length() == 0) {
				//空行
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}
		while (lines.size() > mLineCount) {
			try {
				m_mbBufBegin += lines.get(0).getBytes(m_strCharsetName).length;
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		m_mbBufEnd = m_mbBufBegin;
		return;
	}

	public boolean cheakpage_begin(){
		if (m_mbBufBegin <= 0){
			return true;
		}
		return false;
	}
	public boolean cheakpage_end(){
		if (m_mbBufEnd >= m_mbBufLen){
			return true;
		}
		return false;
	}
	
	public void set_book_name(String n){
		book_name = n;
		return;
	}
	public String get_book_name(){
		return book_name;
	}
	
	
	public void prePage() throws IOException {
		//fix i=0 bug
		if (m_mbBufBegin <= 0) {
			m_mbBufBegin = 0;
			m_isfirstPage=true;
			return;
		}else m_isfirstPage=false;
		m_lines.clear();
		pageUp();
		m_lines = pageDown();
	}

	public void nextPage() throws IOException {
		if (m_mbBufEnd >= m_mbBufLen) {
			m_islastPage=true;
			return;
		}else m_islastPage=false;
		m_lines.clear();
		m_mbBufBegin = m_mbBufEnd;
		m_lines = pageDown();
	}
	
	public void refreshpage(){
		
		m_mbBufEnd = m_mbBufBegin;
		m_lines.clear();
		m_lines = pageDown();
	
	}
	
	public File getBookFile(){
		return book_file;
	}
	
	public void delBookFile(){
		if(book_file != null){
			book_file.delete();
		}
		return;
	}
	
	public void refreshchapter(){
		
		m_mbBufEnd = 0;
		m_mbBufBegin = 0;
		m_lines.clear();
		m_lines = pageDown();
	
	}
	
	public void Destory(){
		if(m_mbBuf != null){
			m_mbBuf.clear();
			m_mbBuf = null;
		}
		if(book_file != null){
			book_file.delete();
			book_file = null;
		}
		m_mbBufLen = 0;
		m_mbBufBegin = 0;
		m_mbBufEnd = 0;
		m_lines.clear();
	}

	public void onDraw(Canvas c) {
		if (m_lines.size() == 0)
			m_lines = pageDown();
		if (m_lines.size() > 0) {
			if(flag_day){
				if (m_book_bg_day == null)
					c.drawColor(m_backColor);
				matrix = new Matrix();
				matrix.postScale(((float)mWidth/m_book_bg_day.getWidth()), ((float)mHeight/m_book_bg_day.getHeight()));
				c.drawBitmap(m_book_bg_day, matrix, null);
				mPaint.setColor(m_textColor_day);
			}else{
				if (m_book_bg_night == null)
					c.drawColor(m_backColor);
				matrix = new Matrix();
				matrix.postScale(((float)mWidth/m_book_bg_night.getWidth()), ((float)mHeight/m_book_bg_night.getHeight()));
				c.drawBitmap(m_book_bg_night, matrix, null);
				mPaint.setColor(m_textColor_night);
				
			}
			
			int y = marginHeight;
			for (int i = 0 ;i < m_lines.size() ; i++){
				y += m_fontSize+m_nLineSpaceing;
				int usedwidth = m_lines.get(i).width;
				String str = m_lines.get(i).str;
				if(usedwidth < mVisibleWidth - m_fontSize||mVisibleWidth == usedwidth){
					c.drawText(str, marginWidth, y, mPaint);
					continue;
				}
				float block_size = ((float)mVisibleWidth - (float)usedwidth)/(float)(m_lines.get(i).size - 1);
				

				float x = marginWidth;
				for(int j = 0; j < str.length() ; j++){
					String temp_str = str.subSequence(j, j+1).toString();
					c.drawText(temp_str, x, y, mPaint);
					x += block_size + mPaint.measureText(temp_str);
				}
				
			}
		}else{
			
			if(flag_day){
				if (m_book_bg_day == null)
					c.drawColor(m_backColor);
				matrix = new Matrix();
				matrix.postScale(((float)mWidth/m_book_bg_day.getWidth()), ((float)mHeight/m_book_bg_day.getHeight()));
				c.drawBitmap(m_book_bg_day, matrix, null);
				mPaint.setColor(m_textColor_day);
			}else{
				if (m_book_bg_night == null)
					c.drawColor(m_backColor);
				matrix = new Matrix();
				matrix.postScale(((float)mWidth/m_book_bg_night.getWidth()), ((float)mHeight/m_book_bg_night.getHeight()));
				c.drawBitmap(m_book_bg_night, matrix, null);
				mPaint.setColor(m_textColor_night);
				
			}
			
		}
		mPaint.setTextSize(banner_size);
		float fPercent = (float) (m_mbBufBegin * 1.0 / (m_mbBufLen == 0 ? 1 : m_mbBufLen));
		DecimalFormat df = new DecimalFormat("#0");
		String strPercent = df.format(fPercent * 100) + "%";
		int nPercentWidth = (int) mPaint.measureText("999.9%") + 1;
		c.drawText(book_name, mWidth/2-(book_name.length()*banner_size/2), mHeight - 6, mPaint);
		c.drawText(strPercent, mWidth - nPercentWidth, mHeight - 6, mPaint);
		//画电池
		mPaint.setStyle(Paint.Style.STROKE);//空心
		mPaint.setStrokeWidth(batter_width);
		c.drawRect(drawmarginWidth ,mHeight-6-batter_k,drawmarginWidth +batter_c,mHeight-6,mPaint);
		
		mPaint.setStyle(Paint.Style.FILL); //实心
		c.drawRect(drawmarginWidth +batter_c,mHeight-6-batter_tb-batter_tk,drawmarginWidth +batter_c+batter_tc,mHeight-6-batter_tb,mPaint);
		c.drawRect(drawmarginWidth +batter_b,mHeight-6-batter_b-batter_nk,drawmarginWidth +batter_b+batter_nc*level,mHeight-6-batter_b,mPaint);
		
		//画时间
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String time_str = formatter.format(curDate);
		c.drawText(time_str, drawmarginWidth +batter_c+batter_tc + 5, mHeight - 6, mPaint);
		mPaint.setTextSize(m_fontSize);
	}
	
	private String String(char c) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int get_percent_num(){
		float fPercent = (float) (m_mbBufBegin * 1.0 / (m_mbBufLen == 0 ? 1 : m_mbBufLen));
		DecimalFormat df = new DecimalFormat("#0");
		return Integer.valueOf(df.format(fPercent * 100));
	}
	
	public String get_percent(){
		float fPercent = (float) (m_mbBufBegin * 1.0 / (m_mbBufLen == 0 ? 1 : m_mbBufLen));
		DecimalFormat df = new DecimalFormat("#0");
		String strPercent = df.format(fPercent * 100) + "%";
		return strPercent;
	}
	
	public void set_m_mbBufBegin_bypercent(int percent){
		m_lines.clear();
		int s_m_mbBufBegin = m_mbBufLen * percent / 100;
		m_mbBufBegin = s_m_mbBufBegin;
		byte[] paraBuf = readParagraphBack(m_mbBufBegin);
		m_mbBufBegin -= paraBuf.length;
		m_mbBufEnd = m_mbBufBegin;
		//给出大概位置
		paraBuf = readParagraphForward(m_mbBufEnd); // 取下一段
		m_mbBufEnd += paraBuf.length;
		int temp_percent = (s_m_mbBufBegin - m_mbBufBegin) * 100 /( m_mbBufEnd - m_mbBufBegin);
		String temp_str = null;
		try {
			temp_str = new String(paraBuf, m_strCharsetName);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int index = temp_str.length() * temp_percent / 100;
		temp_str = temp_str.substring(index);
		if (temp_str.length() != 0) {
			try {
				m_mbBufEnd -= temp_str
						.getBytes(m_strCharsetName).length;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		m_mbBufBegin = m_mbBufEnd;
	}
	
	public void set_m_mbBufBegin(int s_m_mbBufBegin){
		m_lines.clear();
		m_mbBufBegin = s_m_mbBufBegin;
		m_mbBufEnd = s_m_mbBufBegin;
	}
	
	public int get_m_mbBufBegin(){
		return m_mbBufBegin;
	}

	public void setBgBitmap_day(Bitmap BG) {
		m_book_bg_day = BG;
	}
	public void setBgBitmap_night(Bitmap BG) {
		m_book_bg_night = BG;
	}
	public void changeMode(){
		if (flag_day){
			flag_day = false;
		}else{
			flag_day = true;
		}
		return;
	}
	
	public boolean getMode(){
		return flag_day;
	}
	public void setMode(boolean flagd){
		flag_day = flagd;
		return;
	}
	
	
	public boolean isfirstPage() {
		return m_isfirstPage;
	}
	public boolean islastPage() {
		return m_islastPage;
	}
	
	public boolean get_isopenfile(){
		return isopenfile;
	}
	
	public void set_null(){
		isopenfile = false;
		return;
	}
	
	public MappedByteBuffer get_m_mbBuf(){
		return m_mbBuf;
	}
	
	public int get_m_mbBufLen(){
		return m_mbBufLen;
	}
	//翻页用，下一章下一章用
	public void chapter_down_copy(Pre_BookPageFactory book){
//		if(m_mbBuf != null){
//			m_mbBuf.clear();
//		}
		m_mbBuf = book.get_m_mbBuf();
		m_mbBufLen = book.get_m_mbBufLen();
		book_file = book.getBookFile();
		clear_chapter();
		return;
	}
	//向前翻页用
	public void chapter_up_copy(Pre_BookPageFactory book) throws IOException{
//		if(m_mbBuf != null){
//			m_mbBuf.clear();
//		}
		m_mbBuf = book.get_m_mbBuf();
		m_mbBufLen = book.get_m_mbBufLen();
		book_file = book.getBookFile();
		clear_chapter();
		tolastpage();
		return;
	}
	
	public void clear_chapter(){
		m_mbBufEnd = 0;
		m_mbBufBegin = 0;
		m_lines.clear();
		m_isfirstPage = true;
		m_islastPage = false;
		return;
	}
	
	public void tolastpage() throws IOException{
		m_mbBufEnd = 0;
		m_mbBufBegin = 0;
		while(true){
			if(islastPage()){
				break;
			}
			nextPage();
		}
		return;
	}
	
	public void setlevel(float l_temp){
		level = l_temp;
		return;
	}
	
	public void set_fontcolor_day(int color){
		this.m_textColor_day = color;
		mPaint.setColor(m_textColor_day);
//		System.out.println(m_textColor_day);
		return;
	}
	
	public void setm_nLineSpaceing(int lineblock){
		m_nLineSpaceing = lineblock;
		changefront(m_fontSize);
		return;
	}
	
}
