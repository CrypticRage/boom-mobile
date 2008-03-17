package com.anvil.android.boom;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.graphics.*;
import com.anvil.android.boom.util.StopWatch;
import com.anvil.android.boom.particles.*;
import com.anvil.android.boom.logic.*;

class BoomView extends SurfaceView implements SurfaceHolder.Callback {
	private ArrayList<GameObject> mMissiles; //GameMissile objects
	private ArrayList<GameBase> mBases; //Friendly GameBase objects
	
    SurfaceHolder mHolder;
    private CanvasThread mPreviewThread;
    private boolean mHasSurface;
    
    public BoomView(Context context, AttributeSet attrs, Map inflateParams) {
        super(context, attrs, inflateParams);
        init();
    }
    
    public BoomView(Context context) {
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
//    	private boolean watchEn;
        
        /* Camera Variables */ 
        //private float zoom;
    	//private boolean zoomingIn;
    	//private float zoomRate;
    	
    	int elapsedTime;
    	    
        CanvasThread() {
            super();
            mDone = false;
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            //radGrad = new RadialGradient(25.0f, 25.0f, 25.0f,
            //		Color.RED, Color.BLACK, Shader.TileMode.MIRROR);
            //radShape = new ShapeDrawable(new OvalShape());            
            
        	frameCount = 0;
        	watch = new StopWatch();
//        	watchEn = false;
        	
            //zoom = 0.25f;
            //zoomRate = .01f;
            //zoomingIn = true;
            
            elapsedTime = 0;
        }
    
        @Override
		public void run() {
            // This is our main acquisition thread's loop, we go until
            // asked to quit.
            watch.start();
        	SurfaceHolder holder = mHolder;
        	
			GameMissile m1 = new GameMissileNormal (30, 80, 60);
			m1.setVelocity (50);
			m1.setTargetPos (new PointF (480, 320));
			m1.setState (GameObject.STATE_ALIVE);
			
			MotionSolver ms1 = new LineSolver ();
			m1.setMotionSolver(ms1);
			 
			mMissiles = new ArrayList<GameObject> ();
			mBases = new ArrayList<GameBase> ();

			mMissiles.add (m1);
            
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
                
                updateProjectiles(elapsedTime);
                
                drawProjectiles (canvas, mPaint, elapsedTime);
                
                
                
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
            mDone = true;
            try {
                join();
            } catch (InterruptedException ex) { }
        }
    } //End of CanvasThread class
    
    private void updateProjectiles(int timeElapsed)
	{
		for (int i = 0; i < mMissiles.size (); i++)
		{
			GameMissile m = (GameMissile) mMissiles.get (i);
			MotionSolver ms = m.getMotionSolver ();
			ExplosionUpdater eu = m.getExplosionUpdater ();
			
			//Update positions and explosions
			switch (m.getState ())
			{
				case GameObject.STATE_LARVAL:
				case GameObject.STATE_ALIVE:
					if (ms != null)
					{
						ms.solveMotion (m, timeElapsed);
					}
					
					for (int j = 0; j < mMissiles.size (); j++)
					{
						GameMissile otherMissile = (GameMissile) mMissiles.get (j);
						
						//If we're not dealing with the same object
						if (m != otherMissile)
						{
							//Calculate the distance between the current missile
							//and the second one
							PointF mCurrentPos = m.getCurrentPos ();
							PointF otherCurrentPos = otherMissile.getCurrentPos ();
							double distance = Physics.calculateDistance (mCurrentPos, otherCurrentPos);
							
							//We just smacked into something
							if (distance <= m.getProximityRadius ())
							{
								m.setState (GameObject.STATE_DYING);
								
								//Don't want to re-activate a dead object
								if (otherMissile.getState () != GameObject.STATE_DEAD)
								{
									otherMissile.setState (GameObject.STATE_DYING);
								}
							}
						}
					}
					break;
					
				case GameObject.STATE_DYING:
					Explosion exp = m.getExplosion ();
					
					//Important to only have one explosion creation point
					//since we're setting the state to DYING in multiple places
					if (exp == null)
					{
						m.createExplosion ();
						eu = m.getExplosionUpdater ();
						
						//If this is a shrapnel-type missile, 
						if (m instanceof GameMissileShrapnel)
						{
							ArrayList <GameObject> shrapnelChildren = m.getChildren ();
							
							mMissiles.addAll (shrapnelChildren);
						}
					}
					//Don't want to update an explosion if we just created it
					else
					{
						if (eu != null)
						{
							eu.updateExplosion (exp);
						}
					}
					
					for (int j = 0; j < mMissiles.size (); j++)
					{
						GameObject otherMissile = mMissiles.get (j);

						//If we're not dealing with the same object
						if (m != otherMissile)
						{
							//Calculate the distance between the current missile
							//and the second one
							PointF mCurrentPos = m.getCurrentPos ();
							PointF otherCurrentPos = otherMissile.getCurrentPos ();
							double coreDistance = Physics.calculateDistance (mCurrentPos, otherCurrentPos);
							Explosion e = m.getExplosion ();
							
							if (e instanceof WaveExplosion)
							{
								WaveExplosion wave = (WaveExplosion) e;

								//Our wave explosion hit something
								if (coreDistance <= wave.getCurrentRadius ())
								{
									//Don't want to re-activate a dead object
									if (otherMissile.getState () != GameObject.STATE_DEAD)
									{
										otherMissile.setState (GameObject.STATE_DYING);
									}
								}
							}
							
							//TODO: Add code for shrapnel type explosion
						}
					}
					break;
					
				case GameObject.STATE_DEAD:
					//Verify each child is dead as well
					ArrayList<GameObject> children = m.getChildren ();
					
					//If an object has no children, just remove it
					if (children != null)
					{
						boolean cleanUp = true;
						
						for (int j = 0; j < children.size (); j++)
						{
							GameObject child = children.get (j);
							
							if (child.getState () == GameObject.STATE_DEAD)
							{
								children.remove (child);
								j--;
							}
							else
							{
								cleanUp = false;
							}
						}
						
						if (cleanUp)
						{
							mMissiles.remove (m);
							i--;
						}
					}
					else
					{
						mMissiles.remove (m);
						i--;
					}
					break;
			} //End of switch			
		} //End of for loop
	} //End of updateProjectiles
    
    private void drawProjectiles (Canvas canvas, Paint paint, int timeElapsed)
    {
    	for (int i = 0; i < mMissiles.size (); i++)
		{
			GameMissile m = (GameMissile) mMissiles.get (i);
			
			//Draw positions and explosions
			switch (m.getState ())
			{
				case GameObject.STATE_LARVAL:
				case GameObject.STATE_ALIVE:
					SmokeEmitter2D smokeEmitter = m.getSmokeEmitter ();
					
					smokeEmitter.update(timeElapsed);
					smokeEmitter.draw(canvas);
                    
                    m.draw(canvas, paint);
					break;
					
				case GameObject.STATE_DYING:
					Explosion ex = m.getExplosion ();
					
					if (ex != null)
					{
						ex.drawExplosion (canvas, timeElapsed);
					}
					break;
					
				case GameObject.STATE_DEAD:
					break;
			}
		} //End of for loop
    } //End of drawProjectiles
}