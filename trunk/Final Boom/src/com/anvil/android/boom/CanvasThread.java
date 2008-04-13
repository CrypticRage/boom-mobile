package com.anvil.android.boom;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.anvil.android.boom.GlobalData;
import com.anvil.android.boom.util.StopWatch;
import com.anvil.android.boom.graphics.Camera2D;
import com.anvil.android.boom.graphics.CameraMotion2D;
import com.anvil.android.boom.graphics.SpriteData;

class CanvasThread extends Thread {

	Handler motionEventHandler;
	BoomGame mGame;
	
    SurfaceHolder mHolder;
    private Paint mPaint;
    
    /* Background */
    private Bitmap ground = SpriteData.bgSprites[SpriteData.BG_GROUND];
    private Bitmap mountains = SpriteData.bgSprites[SpriteData.BG_MOUNTAINS];
    
    /* StopWatch Variables */
    private int frameCount;
    private StopWatch watch;
	private int elapsedTime;        
    
	/* Camera */
	private Camera2D camera;
	private CameraMotion2D cameraMotion;
	private float minRadius;
	private float maxRadius;
	
	/* Screen Dimensions */
	private float screenHeight = 0.0f;
	private float screenWidth = 0.0f;
	   
    CanvasThread(SurfaceHolder mHolder) {
        super();
        this.mHolder = mHolder;       

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);       
        
    	camera = new Camera2D(480.0f, 320.0f);
        cameraMotion = new CameraMotion2D(camera);
        minRadius = 60.0f;
        maxRadius = 160.0f;
        
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

    					if (cameraMotion.getState() == CameraMotion2D.IDLE &&
    						GlobalData.AMMO_TYPE == GlobalData.STANDARD_MISSILE ) {
        					mGame.createFriendlyMissile (tempPoint.x, tempPoint.y);
    					}
    					
    					else if (	(cameraMotion.getState() == CameraMotion2D.IDLE) &&
        							(GlobalData.AMMO_TYPE == GlobalData.SMART_BOMB) &&
        							(camera.getRadius() > minRadius)
    					) {
    						tempPoint.x = (camera.getView()).left + (tempPoint.x/480.0f)*camera.getView().width();
    						tempPoint.y = (camera.getView()).top + (tempPoint.y/320.0f)*camera.getView().height();   	
    				    	if (tempPoint.y > (320.0f-minRadius)) {
    				    		tempPoint.y = 320.0f-minRadius;
    				    	}
    						cameraMotion.setMotion(tempPoint.x, tempPoint.y, minRadius);
    				    	cameraMotion.setVelocityScalar(20.0f);
    				    	cameraMotion.setZoomScalar(80.0f);
    				    	cameraMotion.startMotion(); 						
        				}
    					
    					else if (	( !(cameraMotion.getState() == CameraMotion2D.IDLE) &&
										(GlobalData.AMMO_TYPE == GlobalData.SMART_BOMB) )
										||
									( (cameraMotion.getState() == CameraMotion2D.IDLE) &&
										(GlobalData.AMMO_TYPE == GlobalData.SMART_BOMB) &&
										(camera.getRadius() <= minRadius) )
    					) {
    						tempPoint.x = (camera.getView()).left + (tempPoint.x/480.0f)*camera.getView().width();
    						tempPoint.y = (camera.getView()).top + (tempPoint.y/320.0f)*camera.getView().height();
        					cameraMotion.stopMotion(); 						
        					mGame.createFriendlyMissile (tempPoint.x, tempPoint.y);
        					cameraMotion.setMotion(240.0f, 160.0f, maxRadius);
    				    	cameraMotion.setVelocityScalar(20.0f);
    				    	cameraMotion.setZoomScalar(90.0f);
    				    	cameraMotion.startMotion();
    					}
    					break;
    					
    				case GlobalData.ENEMY_MISSILE_GENERATION:
    					mGame.createEnemyMissile ();
    					break;
    					
    				case GlobalData.FRIENDLY_MISSILE_RELOAD:
    					mGame.reloadMissile ();
    					break;
    					
    				case GlobalData.BASE_KILLER_RESPAWN:
    					mGame.reloadBaseKiller ();
    					
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
    	
    	int bgColor = 0xff8da7b3;
    	
    	mGame.createEnemyMissile ();
    	
    	//TODO: Hack to get the score up
    	mGame.sendScoreUpdate (0, 1);
        
        while (!mGame.mDone) {       	          	
        	// Lock the surface, this returns a Canvas that can
            // be used to render into.
            Canvas canvas = holder.lockCanvas();

            canvas.drawColor(Color.BLACK);
            //canvas.drawColor(bgColor);

            canvas.translate(240.0f, 160.0f);
            camera.applyToCanvas(canvas);

            canvas.drawBitmap(
            		ground,
            		0.0f,
            		320.0f-ground.getHeight(),
            		mPaint);
           
            canvas.drawBitmap(
            		mountains,
            		-((mountains.getWidth() - 480.0f)*0.5f),
            		320.0f-ground.getHeight()-mountains.getHeight(),
            		mPaint);            

            mGame.updateProjectiles(elapsedTime);
            
            mGame.drawProjectiles (canvas, mPaint, elapsedTime);
            mGame.drawBases (canvas, mPaint, elapsedTime);

            frameCount++; 	                 
                  
            /* Stopwatch */
            watch.stop();
            elapsedTime = watch.getElapsedTimeMicro();
        	
            if (!(cameraMotion.getState() == CameraMotion2D.IDLE)) {
            	cameraMotion.updateMotion((int)elapsedTime);
            }
            
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