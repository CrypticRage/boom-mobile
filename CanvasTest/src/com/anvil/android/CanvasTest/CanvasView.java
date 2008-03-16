package com.anvil.android.CanvasTest;

import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.*;

import com.anvil.android.util.StopWatch;
import com.anvil.android.particles.SmokeEmitter2D;
import com.anvil.android.particles.BlastEmitter2D;

class CanvasView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    private CanvasThread mPreviewThread;
    private boolean mHasSurface;
    
    public CanvasView(Context context, AttributeSet attrs, Map inflateParams) {
        super(context, attrs, inflateParams);
        init();
    }
    
    public CanvasView(Context context) {
        super(context);
        init();
    }
    
    private void init() {    
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        setFocusable(true);
    	mHolder = getHolder();
        mHolder.addCallback(this);
        mHasSurface = false;
        
        // In this example, we hardcode the size of the preview. In a real
        // application this should be more dynamic. This guarantees that
        // the underlying surface will never change size.
        mHolder.setFixedSize(480, 320);
    }

    public void resume() {
        // We do the actual acquisition in a separate thread. Create it now.
        if (mPreviewThread == null) {
            mPreviewThread = new CanvasThread();
            // If we already have a surface, just start the thread now too.
            if (mHasSurface == true) {
                mPreviewThread.start();
            }
        }
    }
    
    public void pause() {
        // Stop Preview.
        if (mPreviewThread != null) {
            mPreviewThread.requestExitAndWait();
            mPreviewThread = null;
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, start our main acquisition thread.
        mHasSurface = true;
        if (mPreviewThread != null) {
            mPreviewThread.start();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return. Stop the preview.
        mHasSurface = false;
        pause();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Surface size or format has changed. This should not happen in this
        // example.
    }

    // ----------------------------------------------------------------------

    class CanvasThread extends Thread {
        private boolean mDone;
        private Paint mPaint;
        //private RadialGradient radGrad;
        //private ShapeDrawable radShape;
        
        /* StopWatch Variables */
        private int frameCount;
        private StopWatch watch;
    	private boolean watchEn;
        
        /* Camera Variables */ 
        //private float zoom;
    	//private boolean zoomingIn;
    	//private float zoomRate;
    	
    	/* Emitter Variables */
    	SmokeEmitter2D smoke;
    	BlastEmitter2D blast;
    	long elapsedTime;
    	    
        CanvasThread() {
            super();
            mDone = false;
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            //radGrad = new RadialGradient(25.0f, 25.0f, 25.0f,
            //		Color.RED, Color.BLACK, Shader.TileMode.MIRROR);
            //radShape = new ShapeDrawable(new OvalShape());            
            
        	frameCount = 0;
        	watch = new StopWatch();
        	watchEn = false;
        	
            //zoom = 0.25f;
            //zoomRate = .01f;
            //zoomingIn = true;
            
            smoke = new SmokeEmitter2D(1, -60.0f, -60.0f, GlobalData.sprites[0]);
            blast = new BlastEmitter2D(600.0f, 600.0f, GlobalData.sprites[2]);
            elapsedTime = 0;
        }
    
        @Override
		public void run() {
            // This is our main acquisition thread's loop, we go until
            // asked to quit.
            watch.start();
        	SurfaceHolder holder = mHolder;     
            
            while (!mDone) {       	          	
            	// Lock the surface, this returns a Canvas that can
                // be used to render into.
                Canvas canvas = holder.lockCanvas();
                
               
                canvas.drawColor(Color.BLACK);
                //canvas.drawBitmap(GlobalData.background, 0.0f, 0.0f, mPaint);
                
                //radShape.setBounds(200, 200, 250, 250);
                //radShape.setShader(radGrad); 
                //radShape.draw(canvas);
                
                //RectF bounds = new RectF();
                //mPath.computeBounds(bounds, false);
                
                
                /*
				if (zoom >= 1.5f) {
    	        	zoomingIn = false;
    	        }
    	        else if (zoom <= .05f) {
    	        	zoomingIn = true;
    	        }
    	        
    	        if (zoomingIn) {
    	        	zoom += zoomRate;
    	        }
    	        else {
    	        	zoom -= zoomRate;
    	        }
				*/
                
            	smoke.update(elapsedTime);
                smoke.draw(canvas);
                
                smoke.x += 1.25f;
                smoke.y += 1.25f;
                
                drawRocket(canvas);
                
                if (	smoke.x >= (blast.x + 30.0f) &&
                		smoke.y >= (blast.y + 30.0f) &&
                		blast.status == BlastEmitter2D.ALIVE	) {
                	
                	blast.update(elapsedTime);
                	blast.draw(canvas);
                }
                
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
              
        public void drawRocket(Canvas canvas) {
    		Bitmap temp = GlobalData.sprites[1];
        	canvas.save();
    		canvas.scale(0.25f, 0.25f);
    		canvas.rotate(180.0f+45.0f, smoke.x+(temp.getWidth()/2)+15.0f, smoke.y+(temp.getHeight()/2)+15.0f);
        	canvas.drawBitmap(temp, smoke.x, smoke.y, mPaint);
    		canvas.restore();
        }
        
        public void requestExitAndWait() {
            // don't call this from PreviewThread thread or it a guaranteed
            // deadlock!
            mDone = true;
            try {
                join();
            } catch (InterruptedException ex) { }
        }
    }
}