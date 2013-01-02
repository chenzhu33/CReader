package com.sogou.util;
import java.io.FileNotFoundException;   
import java.io.FileOutputStream;   
import java.io.IOException;   
        
import android.app.Activity;   
import android.graphics.Bitmap;   
import android.graphics.Rect;   
import android.view.View;   
        
public class ScreenShotUtil {   
    // ��ȡָ��Activity�Ľ��������浽png�ļ�   
    private static Bitmap takeScreenShot(Activity activity){   
        //View������Ҫ��ͼ��View   
        View view = activity.getWindow().getDecorView();   
        view.setDrawingCacheEnabled(true);   
        view.buildDrawingCache();   
        Bitmap b1 = view.getDrawingCache();   
            
        //��ȡ״̬���߶�   
        Rect frame = new Rect();     
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);     
        int statusBarHeight = frame.top;     
        System.out.println(statusBarHeight);   


//        tv.setText(new Calculater()); 
//        class Calculater implements Runnable { 
//            public void run(){ 
//                Rect rect = new Rect(); 
//                getWindow().getDecorView().getWindowVisibleDisplayFrame(rect); 
//                int statusBarHeight = rect.top; 
//                System.out.println("StatusBarHeight:" + statusBarHeight); 
//            } 
//        } 

        
        
        //��ȡ��Ļ���͸�   
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();     
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();     
        //ȥ��������   
        //Bitmap b = Bitmap.createBitmap(b1, 0, 25, 320, 455);   
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);   
        view.destroyDrawingCache();   
        return b;   
    }   
        
    //���浽sdcard   
    private static void savePic(Bitmap b,String strFileName){   
        FileOutputStream fos = null;   
        try {   
            fos = new FileOutputStream(strFileName);   
            if (null != fos)   
            {   
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);   
                fos.flush();   
                fos.close();   
            }   
        } catch (FileNotFoundException e) {   
            e.printStackTrace();   
        } catch (IOException e) {   
            e.printStackTrace();   
        }   
    }   
        
    //�������   
    public static void shoot(Activity a){   
        ScreenShotUtil.savePic(ScreenShotUtil.takeScreenShot(a), "sdcard/xx.png");   
    }   
}