package com.leibun.SensorTest3;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;


public  class SensorTest3 extends Activity implements SensorEventListener {
	private final String tag = SensorTest3.class.getSimpleName();
	private SensorManager sensorManager;
	private MySurfaceView view;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MySurfaceView(this);
        setContentView(view);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        	       
    }
	@Override
	protected void onResume() {
		super.onResume();
		List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensors.size() > 0) {
			sensorManager.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {		
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
		view.onValueChanged(event.values);
	}    
	
	//サーフェイスビューの利用
    public class MySurfaceView extends SurfaceView 
        implements SurfaceHolder.Callback {

        private Bitmap image;//イメージ
        private float    px=0; //X座標
        private float    py=0; //Y座標
        private float    vx=0;//X速度
        private float    vy=0;//Y速度
        private int	   offsetX=0;
        private int    offsetY=0;
        private boolean	  drawflg=false;
        private boolean	  swingflg=false;
        private float    samplingTime =0.050f; //[sec]
        private float    rate=3000f;//320pixel:0.045m=px:(accele*samplingTime+vx)*samplingTime
        //コンストラクタ
        public MySurfaceView(Context context) {
            super(context);

            //画像の読み込み
            Resources r=getResources();
            image=BitmapFactory.decodeResource(r,android.R.drawable.ic_input_add);  
			offsetY = image.getHeight()/2;
			offsetX = image.getWidth()/2;
			initialize();
                    
        }
        public void initialize(){
			py = getHeight()/2;
			px = getWidth()/2;
			Log.i("SensorText","### px="+px);
			vy = 0.0f;
			vx = 0.0f;
        }
        //サーフェイスの生成
        public void surfaceCreated(SurfaceHolder holder) {
        	drawflg=false;        
        }

        //サーフェイスの終了
        public void surfaceDestroyed(SurfaceHolder holder) {
        	drawflg=false;        
        }

        //サーフェイスの変更
        public void surfaceChanged(SurfaceHolder holder,
            int format,int w,int h) {
        	drawflg=false;        
        }   

		void onValueChanged(float[] values) {
			if(drawflg==false){
				drawflg=true;
				initialize();
			}else{
				vx += -values[0]*samplingTime;
				px += rate*vx*samplingTime;
				vy += values[1]*samplingTime;
				py += rate*vy*samplingTime;
				
	            if (px<image.getWidth()/2){
	            	px = image.getWidth()/2;
	            	vx =-vx/2;
	            }else if(getWidth()-image.getWidth()/2<px){
	            	px = getWidth()-image.getWidth()/2;
	            	vx =-vx/2;
	            }
	            Log.i("accex","###px="+px+" vx="+getWidth());
	            if (py<image.getHeight()/2){
	            	py = image.getHeight()/2;
	            	vy =-vy/2;
	            }else if(getHeight()-image.getHeight()/2<py){
	            	py = getHeight()-image.getHeight()/2;
	            	vy = -vy/2;
	            }
				Canvas canvas = getHolder().lockCanvas();
				if (canvas != null) {				
					//描画
					canvas.drawColor(Color.BLACK);
//					Rect src = new Rect((int)px,0,(int)(px+getWidth()/10),getHeight());
//					Rect dst = new Rect(0,0,getWidth()/10,getHeight());
//					canvas.drawBitmap(image, src, dst, null);
					canvas.drawBitmap(image,px-offsetX,py-offsetY,null);                    
					getHolder().unlockCanvasAndPost(canvas);
				}
			}
		}
    }

}