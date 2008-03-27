package com.anvil.android.boom;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.SurfaceHolder;
import android.view.MotionEvent;
import android.os.Handler;
import android.os.Message;


import com.anvil.android.boom.logic.Explosion;
import com.anvil.android.boom.logic.ExplosionUpdater;
import com.anvil.android.boom.logic.GameBase;
import com.anvil.android.boom.logic.GameMissile;
import com.anvil.android.boom.logic.GameMissileNormal;
import com.anvil.android.boom.logic.GameMissileShrapnel;
import com.anvil.android.boom.logic.GameObject;
import com.anvil.android.boom.logic.LineSolver;
import com.anvil.android.boom.logic.MotionSolver;
import com.anvil.android.boom.logic.Physics;
import com.anvil.android.boom.logic.WaveExplosion;
import com.anvil.android.boom.particles.SmokeEmitter2D;
import com.anvil.android.boom.util.StopWatch;

class CanvasThread extends Thread {
	private Semaphore mSem;					//Semaphore to protect game object array lists
	private ArrayList<GameObject> mMissiles; //GameMissile objects
	private ArrayList<GameBase> mBases; //Friendly GameBase objects
	
    SurfaceHolder mHolder;
	private boolean mDone;
    private Paint mPaint;
    
    /* StopWatch Variables */
    private int frameCount;
    private StopWatch watch;
	private int elapsedTime;        
    
	/* Camera Variables */
	
	protected volatile MotionEventHandler mMotionEventHandler;
	
	private class MotionEventHandler extends Handler
	{
		public MotionEventHandler ()
		{
			super ();
		}
		
		public void handleMessage (Message msg)
		{
			switch (msg.what)
			{
				case GlobalData.MOTION_EVENT_TYPE:
					PointF tempPoint = (PointF) msg.obj;
					float xCoord = 0, yCoord = 0;
					
					xCoord = tempPoint.x;
					yCoord = tempPoint.y;
					
					GameMissile m1 = new GameMissileNormal (30, 240, 320);
					m1.setVelocity (50);
					m1.setTargetPos (new PointF (xCoord, yCoord));
					m1.setState (GameObject.STATE_ALIVE);
					
					//TODO: Do we just want to have some sort of general LineSolver
					//instead of creating a new Solver for each missile?
					MotionSolver ms1 = new LineSolver ();
					m1.setMotionSolver(ms1);
		
					try
					{
						mSem.acquire ();
						mMissiles.add (m1);
						mSem.release ();
					}
					catch (InterruptedException e)
					{
						System.err.println ("InterruptedException in CanvasThread MotionEventHandler: " + e.getMessage ());
					}
					finally
					{
						msg.recycle ();
					}
					break;
					
				default:
					break;
			}
		}
	}
	   
    CanvasThread(SurfaceHolder mHolder) {
        super();
        this.mHolder = mHolder;       
        mDone = false;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);       
        
    	frameCount = 0;
    	watch = new StopWatch();
        
        elapsedTime = 0;
        
        mMotionEventHandler = new MotionEventHandler ();
        GlobalData.canvasThreadHandler = mMotionEventHandler;
        
        //Initialize game objects
		mMissiles = new ArrayList<GameObject> ();
		mBases = new ArrayList<GameBase> ();
		
		mSem = new Semaphore (1, true);
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

    private void updateProjectiles(int timeElapsed)
	{
    	try
    	{
    		mSem.acquire ();
    		
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
    					
//    					for (int j = 0; j < mMissiles.size (); j++)
//    					{
//    						GameMissile otherMissile = (GameMissile) mMissiles.get (j);
//    						
//    						//If we're not dealing with the same object
//    						if (m != otherMissile)
//    						{
//    							//Calculate the distance between the current missile
//    							//and the second one
//    							PointF mCurrentPos = m.getCurrentPos ();
//    							PointF otherCurrentPos = otherMissile.getCurrentPos ();
//    							double distance = Physics.calculateDistance (mCurrentPos, otherCurrentPos);
//    							
//    							//We just smacked into something
//    							if (distance <= m.getProximityRadius ())
//    							{
//    								m.setState (GameObject.STATE_DYING);
//    								
//    								//Don't want to re-activate a dead object
//    								if (otherMissile.getState () != GameObject.STATE_DEAD)
//    								{
//    									otherMissile.setState (GameObject.STATE_DYING);
//    								}
//    							}
//    						}
//    					}
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
    					
//    					for (int j = 0; j < mMissiles.size (); j++)
//    					{
//    						GameObject otherMissile = mMissiles.get (j);
    //
//    						//If we're not dealing with the same object
//    						if (m != otherMissile)
//    						{
//    							//Calculate the distance between the current missile
//    							//and the second one
//    							PointF mCurrentPos = m.getCurrentPos ();
//    							PointF otherCurrentPos = otherMissile.getCurrentPos ();
//    							double coreDistance = Physics.calculateDistance (mCurrentPos, otherCurrentPos);
//    							Explosion e = m.getExplosion ();
//    							
//    							if (e instanceof WaveExplosion)
//    							{
//    								WaveExplosion wave = (WaveExplosion) e;
    //
//    								//Our wave explosion hit something
//    								if (coreDistance <= wave.getCurrentRadius ())
//    								{
//    									//Don't want to re-activate a dead object
//    									if (otherMissile.getState () != GameObject.STATE_DEAD)
//    									{
//    										otherMissile.setState (GameObject.STATE_DYING);
//    									}
//    								}
//    							}
//    							
//    							//TODO: Add code for shrapnel type explosion
//    						}
//    					}
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
    		
    		mSem.release ();
    	} //End of try
    	catch (InterruptedException e)
    	{
    		System.err.println ("InterruptedException in updateProjectiles: " + e.getMessage ());
    	}
		
	} //End of updateProjectiles
    
    private void drawProjectiles (Canvas canvas, Paint paint, int timeElapsed)
    {
    	try
    	{
    		mSem.acquire ();
    	
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
	    	
	    	mSem.release ();
	    } //End of try
		catch (InterruptedException e)
		{
			System.err.println ("InterruptedException in updateProjectiles: " + e.getMessage ());
		}
    } //End of drawProjectiles
} //End of CanvasThread class