package test;

import android.app.Activity;   
import android.os.Bundle;   
import android.util.DisplayMetrics;
import android.view.View;   
import android.content.Context;   
import android.graphics.Canvas;   
import android.graphics.Color;   
import android.graphics.Paint;   
import android.graphics.RectF;   
import android.graphics.Path;   
import android.graphics.Shader;   
import android.graphics.LinearGradient;   
//???????§Þ????Android.view.View?? MyView????§Õ MyView??onDraw??????????   
//??????????§Ý???????????onDraw????????Paint????????¦Ë?????Canvas???   
//?? paint.setColor() ????????????? paint.setStyle()??????????????????   
//?????????????????????????¦²??????????Canvas§Õ???????????Paint??   
//??????????????????§Ó??????????? 
public class drawTest extends Activity { 
	int w = 300;
	int h = 500;
    /** Called when the activity is first created. */  
    @Override  
    public void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);   
        /*????ContentView???????MyVieW*/  
        MyView myView=new MyView(this);   
        setContentView(myView);
//        DisplayMetrics dm = new DisplayMetrics();  
//		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		w = dm.widthPixels; 
//		h = dm.heightPixels;
    }   
        
    /* ???????View ??MyView*/  
    private class MyView extends View {   
         public MyView(Context context){   
            super(context) ;   
         }   
         
         
         /*??§ÕonDraw????*/  
         @Override  
         protected void onDraw(Canvas canvas)   
         {      
              super.onDraw(canvas);   
              /*???????????*/  
              canvas.drawColor(Color.WHITE);   
                  
              Paint paint=new Paint();   
              /*????*/  
              paint.setAntiAlias(true);   
              /*????paint?????*/  
              paint.setColor(Color.RED);   
              /*????paint?? style ?STROKE??????*/  
              paint.setStyle(Paint.Style.STROKE);   
              /*????paint???????*/  
              paint.setStrokeWidth(3);   
                  
              /*????????????*/  
              canvas.drawCircle(40, 40, 30, paint);   
              /*???????????????*/  
              canvas.drawRect(10, 90, 70, 150, paint);   
              /*??????????????*/  
              canvas.drawRect(10, 170, 70,200, paint);   
              /*??????????????*/  
              canvas.drawOval(new RectF(10,220,70,250), paint);   
              /*???????????????*/  
              Path path=new Path();   
              path.moveTo(10, 330);   
              path.lineTo(70,330);   
              path.lineTo(40,270);   
              path.close();   
              canvas.drawPath(path, paint);   
              /*?????????????*/  
              Path path1=new Path();   
              path1.moveTo(10, 410);   
              path1.lineTo(70,410);   
              path1.lineTo(55,350);   
              path1.lineTo(25, 350);   
              path1.close();   
              canvas.drawPath(path1, paint);   
                  
              /*????paint?????*/  
              paint.setColor(Color.BLUE);   
              /*????paint ??style? FILL?????*/  
              paint.setStyle(Paint.Style.FILL);   
              /*?????????*/  
              canvas.drawCircle(120,40,30, paint);   
              /*??????????????*/  
              canvas.drawRect(90, 90, 150, 150, paint);   
              /*?????????????*/  
              canvas.drawRect(90, 170, 150,200, paint);   
              /*???????????*/  
              RectF re2=new RectF(90,220,150,250);   
              canvas.drawOval(re2, paint);   
              /*??????????????*/  
              Path path2=new Path();   
              path2.moveTo(90, 330);   
              path2.lineTo(150,330);   
              path2.lineTo(120,270);   
              path2.close();   
              canvas.drawPath(path2, paint);   
              /*????????????*/  
              Path path3=new Path();   
              path3.moveTo(90, 410);   
              path3.lineTo(150,410);   
              path3.lineTo(135,350);   
              path3.lineTo(105, 350);   
              path3.close();   
              canvas.drawPath(path3, paint);   
                  
              /*???y????*/  
              Shader mShader=new LinearGradient(0,0,100,100,   
                      new int[]{Color.RED,Color.GREEN,Color.BLUE,Color.YELLOW},   
                      null,Shader.TileMode.REPEAT);   
              //Shader.TileMode??????   
              //REPEAT:???????????????   
              //CLAMP:?????????????¦¶???????????????????   
              //MIRROR:??REPEAT???????????????????????????   
              paint.setShader(mShader);//??Shader?§Ø??ŽG??????????   
               
              /*???????????*/  
              canvas.drawCircle(200,40,30, paint);   
              /*????????????????*/  
              canvas.drawRect(170, 90, 230, 150, paint);   
              /*????????????????*/  
              canvas.drawRect(170, 170, 230,200, paint);   
              /*?????????????*/  
              RectF re3=new RectF(170,220,230,250);   
              canvas.drawOval(re3, paint);   
              /*??????????123???*/  
              Path path4=new Path();   
              path4.moveTo(170,330);   
              path4.lineTo(230,330);   
              path4.lineTo(200,270);   
              path4.close();   
              canvas.drawPath(path4, paint);   
              /*??????????????*/  
              Path path5=new Path();   
              path5.moveTo(170, 410);   
              path5.lineTo(230,410);   
              path5.lineTo(215,350);   
              path5.lineTo(185, 350);   
              path5.close();   
              canvas.drawPath(path5, paint);   
                  
              /*§Õ??*/  
              paint.setTextSize(24);   
              canvas.drawText("???", 240, 50, paint);   
              canvas.drawText("??????", 240, 120, paint);   
              canvas.drawText("??????", 240, 190, paint);   
              canvas.drawText("?????", 240, 250, paint);   
              canvas.drawText("??????", 240, 320, paint);   
              canvas.drawText("????", 240, 390, paint);
              
              
              //?????
              
              
              Paint paint2 = new Paint();   
              /*????*/  
              paint2.setAntiAlias(true);   
              /*????paint?????*/  
              paint2.setColor(Color.BLACK);   
              /*????paint?? style ?STROKE??????*/  
              paint2.setStyle(Paint.Style.STROKE);   
              /*????paint???????*/  
              paint2.setStrokeWidth(2);
              
//              Path path6=new Path();
//              path6.moveTo(8, h-6);
//              path6.lineTo(8, h-30);
//              path6.lineTo(44, h-30);
//              path6.lineTo(44, h-6);
//              path6.close();
//              canvas.drawPath(path6, paint2); 
              canvas.drawRect(8,h-26,38,h-6,paint2);
              
              paint2.setStyle(Paint.Style.FILL);
              
//              Path path7=new Path();
//              path7.moveTo(44, h-25);
//              path7.lineTo(52, h-25);
//              path7.lineTo(52, h-11);
//              path7.lineTo(44, h-11);
//              path7.close();
              canvas.drawRect(38,h-22,44,h-10,paint2);
//              canvas.drawPath(path7, paint2);
              
//              Path path8=new Path();
//              path8.moveTo(11, h-9);
//              path8.lineTo(11, h-27);
//              path8.lineTo(41, h-27);
//              path8.lineTo(41, h-9);
//              path8.close();
//              canvas.drawPath(path8, paint2);
              canvas.drawRect(11,h-23,35,h-9,paint2);
              
         }   
    }   
}