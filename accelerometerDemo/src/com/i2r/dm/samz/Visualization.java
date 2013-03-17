package com.i2r.dm.samz;


import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Visualization extends View{
	
	public ArrayList<VisualizationParams> vp;
	
	public List<int[]> featureList1= new ArrayList<int[]>();
	public List<int[]> featureList2= new ArrayList<int[]>();
	public List<int[]> featureList3= new ArrayList<int[]>();
	public List<int[]> featureList4= new ArrayList<int[]>();
	public List<int[]> featureList5= new ArrayList<int[]>();
	
	public List<String> BDistance=new ArrayList<String>();
	
    private Handler mHandler = new Handler();
    private static boolean clear=false;
    

	public Visualization(Context mContext) {
        super(mContext);
        vp=new ArrayList<VisualizationParams>();
        for (int i=0;i<5;i++){
        	VisualizationParams dist= new VisualizationParams();
        	vp.add(dist);  
        	BDistance.add("0");
        }
    }
    
    @SuppressLint("ParserError")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        Paint walk = new Paint();
        Paint run = new Paint();
        Paint stand = new Paint();
        Paint sit = new Paint();
        Paint lay = new Paint();
        Paint point = new Paint();
        Paint white = new Paint();

        //Set Activity Colors
        walk.setColor(Color.parseColor("#2C8383"));
        run.setColor(Color.parseColor("#9E1D1D"));
        stand.setColor(Color.parseColor("#6A3297"));
        sit.setColor(Color.parseColor("#FF7700"));
        lay.setColor(Color.parseColor("#669A00"));
        white.setColor(Color.parseColor("#FFFFFF"));

        //Set Activities Color Style
        walk.setStyle(Paint.Style.STROKE);
        run.setStyle(Paint.Style.STROKE);
        stand.setStyle(Paint.Style.STROKE);
        sit.setStyle(Paint.Style.STROKE);
        lay.setStyle(Paint.Style.STROKE);
        white.setStyle(Paint.Style.FILL);

        //Screen Color
        canvas.drawColor(Color.BLACK);


        if(!clear){
        	
        	for(int[] data:featureList1){
        		canvas.drawPoint(data[0],data[1],walk);
        	}
        	for(int[] data:featureList2){
        		canvas.drawPoint(data[0],data[1],run);
        	}
        	for(int[] data:featureList3){
        		canvas.drawPoint(data[0],data[1],stand);
        	}
        	for(int[] data:featureList4){
        		canvas.drawPoint(data[0],data[1],sit);
        	}
        	for(int[] data:featureList5){
        		canvas.drawPoint(data[0],data[1],lay);
        	}

        	if(featureList1.size()==500){
        		featureList1.clear();
        	}
        	if(featureList2.size()==500){
        		featureList2.clear();
        	}
        	if(featureList3.size()==500){
        		featureList3.clear();
        	}
        	if(featureList4.size()==500){
        		featureList4.clear();
        	}
        	if(featureList5.size()==500){
        		featureList5.clear();
        	}

        	drawAux(canvas,0,walk);
        	drawAux(canvas,1,run);
        	drawAux(canvas,2,stand);
        	drawAux(canvas,3,sit);
        	drawAux(canvas,4,lay);

        }
        
       /* if(clear)
        	this.clear=false;*/

        
        canvas.drawText("Bhattacharyya Dist:", 580, 40, white);
        canvas.drawText(this.BDistance.get(0), 600, 70, walk);
        canvas.drawText(this.BDistance.get(1), 600, 100, run);
        canvas.drawText(this.BDistance.get(2), 600, 130, stand);
        canvas.drawText(this.BDistance.get(3), 600, 160, sit);
        canvas.drawText(this.BDistance.get(4), 600, 190, lay);

    }
     
    public void clearScreen(){
    	this.clear=true;
    	this.resetAllVisParameters();
    	this.postInvalidate();
    	this.clear=false;
    }
    
    
    public void drawAux(Canvas canvas, int index, Paint paint){
    	canvas.save();
        canvas.rotate(vp.get(index).rotation,vp.get(index).x,vp.get(index).y);     
        canvas.drawOval(this.myOval(vp.get(index).x,vp.get(index).y,vp.get(index).minor,vp.get(index).major), paint);
        canvas.restore();
    }
    
    public void redraw(final int x, final int y, final int minor, final int major, final int rotation, final int act){

    	mHandler.post(new Runnable() {
            public void run() {
            	updateVisParameters(x,y,minor,major,rotation,act);
            }
        });
    }

    
    public void drawData(final List<int[]> fL1,final List<int[]> fL2,final List<int[]> fL3,final List<int[]> fL4,final List<int[]> fL5){

    	mHandler.post(new Runnable() {
            public void run() {
            	updateData(fL1,fL2,fL3,fL4,fL5);
            }
        });
    }



    private void updateData(final List<int[]> fL1,final List<int[]> fL2,final List<int[]> fL3,final List<int[]> fL4,final List<int[]> fL5){
    	
    	this.featureList1=fL1;
    	this.featureList2=fL2;
    	this.featureList3=fL3;
    	this.featureList4=fL4;
    	this.featureList5=fL5;
    	
    	this.invalidate();
    }
    
    private void updateVisParameters(int x,int y, int minor, int major, int rotation,int index) {
    	VisualizationParams params=vp.get(index);
    	params.updateVisParameters(x,y, minor, major, rotation);
    	this.invalidate();
    }
    
    private void resetAllVisParameters(){
    	for(VisualizationParams vis:vp)
    		vis.resetVisParameters();
    }

    public RectF myOval(float x, float y, float width, float height){
        float halfW = width/2;
        float halfH = height/2;
        return new RectF(x-halfW, y-halfH, x+halfW, y+halfH);
    }
    
       
}

