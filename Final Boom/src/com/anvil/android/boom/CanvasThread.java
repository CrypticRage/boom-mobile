package com.anvil.android.boom;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.anvil.android.boom.util.StopWatch;
import com.anvil.android.boom.graphics.Camera2D;
import com.anvil.android.boom.graphics.CameraMotion2D;


class CanvasThread extends Thread {

	Handler motionEventHandler;
	BoomGame mGame;
	
    SurfaceHolder mHolder;
    private Paint mPaint;
    
    /* StopWatch Variables */
    private int frameCount;
    private StopWatch watch;
	private int elapsedTime;        
    
	/* Camera */
	private Camera2D camera;
	private CameraMotion2D smartBombMotion;
	
	/* Screen Dimensions */
	private float screenHeight = 0.0f;
	private float screenWidth = 0.0f;
	   
    CanvasThread(SurfaceHolder mHolder) {
        super();
        this.mHolder = mHolder;       

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);       
        
    	frameCount = 0;
    	watch = new StopWatch();
        
        elapsedTime = 0;
        
        mGame = new BoomGame ();
		
    	motionEventHandler = new Handler() {
    		public void handleMessage (Message msg)
    		{
    			switch (msg.what)
    			{
    				case GlobalData.MOTION_EVENT_TYPE:
    					PointF tempPoint = (PointF) msg.obj;
    					
    					mGame.createFriendlyMissile (tempPoint.x, tempPoint.y);
    					break;
    					
    				case GlobalData.ENEMY_MISSILE_GENERATION:
    					mGame.createEnemyMissile ();
    					break;
    					
    				case GlobalData.FRIENDLY_MISSILE_RELOAD:
    					mGame.reloadMissile ();
    					break;
    					
    				default:
//    					msg.recycle ();
    					break;
    			} //End of switch
    		} //End of handleMessage
    	};
    	GlobalData.canvasThreadHandler = motionEventHandler; 
   	
        screenWidth = (mHolder.getSurfaceFrame()).width();
		screenHeight = (mHolder.getSurfaceFrame()).height();
    }

    @Override
	public void run() {
        // This is our main acquisition thread's loop, we go until
        // asked to quit.
        watch.start();
    	SurfaceHolder holder = mHolder;
    	
    	mGame.createEnemyMissile ();
    	
    	//TODO: Hack to get the score up
    	mGame.sendScoreUpdate (0, 1);
        
        while (!mGame.mDone) {       	          	
        	// Lock the surface, this returns a Canvas that can
            // be used to render into.
            Canvas canvas = holder.lockCanvas();

            canvas.drawColor(Color.BLACK);
            
            mGame.updateProjectiles(elapsedTime);
            
            mGame.drawProjectiles (canvas, mPaint, elapsedTime);
            mGame.drawBases (canvas, mPaint, elapsedTime);

            frameCount++; 	        
	                   
            /* Stopwatch */
            watch.stop();
            elapsedTime = watch.getElapsedTimeMicro();
        	if (elapsedTime > 200000) {
        		GlobalData.overTimeMicro = elapsedTime;
        		GlobalData.overCount++;
        	}
        
            if (frameCount == 60) {
	        	GlobalData.frameTimeMicro = elapsedTime;
	        	frameCount = 0;
            }
	        
	        watch.reset();
            watch.start();
            
            // And finally unlock and post the surface.
            holder.unlockCanvasAndPost(canvas);
        }
    }
    
    public void requestExitAndWait() {
        // don't call this from PreviewThread thread or it a guaranteed
        // deadlock!
        mGame.mDone = true;
        
        try {
            join();
        } catch (InterruptedException ex) { }
    }
} //End of CanvasThread class