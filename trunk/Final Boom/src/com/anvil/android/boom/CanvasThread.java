package com.anvil.android.boom;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.SurfaceHolder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
import com.anvil.android.boom.logic.scoring.StatusUpdateMessage;
import com.anvil.android.boom.logic.scoring.ScoreCalculator;
import com.anvil.android.boom.particles.SmokeEmitter2D;
import com.anvil.android.boom.util.StopWatch;

import java.util.Random;

class CanvasThread extends Thread {
	private Semaphore mSem;					//Semaphore to protect game object array lists
	private ArrayList<GameObject> mFriendlyMissiles; //Friendly GameMissile objects
	private ArrayList<GameObject> mEnemyMissiles; //Enemy GameMissile objects
	private ArrayList<GameBase> mBases; //Friendly GameBase objects
	
    SurfaceHolder mHolder;
	private boolean mDone;
    private Paint mPaint;
    
    /* StopWatch Variables */
    private int frameCount;
    private StopWatch watch;
	private int elapsedTime;        
    
	/* Camera Variables */
	private float screenHeight = 0.0f;
	private float screenWidth = 0.0f;
	
	protected MotionEventHandler mMotionEventHandler;
	
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
					
					createFriendlyMissile (tempPoint.x, tempPoint.y);
					break;
					
				case GlobalData.ENEMY_MISSILE_GENERATION:
					createEnemyMissile ();
					break;
					
				default:
					msg.recycle ();
					break;
			} //End of switch
		} //End of handleMessage
	} //End of MotionEventHandler class
	   
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
		mFriendlyMissiles = new ArrayList<GameObject> ();
		mEnemyMissiles = new ArrayList<GameObject> ();
		mBases = new ArrayList<GameBase> ();
		
		screenWidth = (mHolder.getSurfaceFrame()).width();
		screenHeight = (mHolder.getSurfaceFrame()).height();
		
		//Create base(s)
		GameBase base = new GameBase (240, 320,
									  GameBase.DEFAULT_BASE_RADIUS,
									  GameBase.DEFAULT_BASE_HIT_POINTS);
		base.setState (GameObject.STATE_ALIVE);
		mBases.add (base);
		
		//Create a new binary semaphore
		mSem = new Semaphore (1, true);
    }

    @Override
	public void run() {
        // This is our main acquisition thread's loop, we go until
        // asked to quit.
        watch.start();
    	SurfaceHolder holder = mHolder;
    	
    	createEnemyMissile ();
    	
    	//TODO: Hack to get the score up
    	sendScoreUpdate (0, 1);
        
        while (!mDone) {       	          	
        	// Lock the surface, this returns a Canvas that can
            // be used to render into.
            Canvas canvas = holder.lockCanvas();

            canvas.drawColor(Color.BLACK);
            
            updateProjectiles(elapsedTime);
            
            drawProjectiles (canvas, mPaint, elapsedTime);
            drawBases (canvas, mPaint, elapsedTime);

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
    
    private void createFriendlyMissile (float xCoord, float yCoord)
    {
		//Start off from the center base
		GameMissile m1 = new GameMissileNormal (WaveExplosion.DEFAULT_FRIENDLY_WAVE_EXPLOSION_RADIUS,
												240, 320, true);
//		GameMissile m1 = new GameMissileNormal (WaveExplosion.DEFAULT_FRIENDLY_WAVE_EXPLOSION_RADIUS,
//												xCoord, yCoord);
		m1.setVelocity (GameMissile.DEFAULT_FRIENDLY_MISSILE_VELOCITY);
		m1.setTargetPos (new PointF (xCoord, yCoord));
		m1.setState (GameObject.STATE_ALIVE);
		
		//TODO: Do we just want to have some sort of general LineSolver
		//instead of creating a new Solver for each missile?
		MotionSolver ms1 = new LineSolver ();
		m1.setMotionSolver(ms1);
		
		try
		{
			mSem.acquire ();
			
			boolean stillAlive = false;
			
			//Make sure at least one base is still alive before launching
			for (int i = 0; i < mBases.size (); i++)
			{
				GameBase base = (GameBase) mBases.get (i);
				
				if (base.getState () == GameObject.STATE_ALIVE)
				{
					stillAlive = true;
				}
			}
			
			if (stillAlive)
			{
				mFriendlyMissiles.add (m1);
			}
			
			mSem.release ();
		}
		catch (InterruptedException e)
		{
			System.err.println ("InterruptedException in CanvasThread MotionEventHandler: " + e.getMessage ());
		}
    }
    
    private void createEnemyMissile ()
    {
    	//Determine a random X value to begin from
    	Random generator = new Random (System.currentTimeMillis ());
    	float startingX = generator.nextInt (480);
    	float endingX = generator.nextInt (480);
    	int missileVelocity = generator.nextInt (25) + 20;
    	
		GameMissile m1 = new GameMissileNormal (WaveExplosion.DEFAULT_ENEMY_PAYLOAD_WAVE_EXPLOSION_RADIUS,
												startingX, 0, false);
		m1.setVelocity (missileVelocity);
		m1.setTargetPos (new PointF (endingX, 320));
		m1.setState (GameObject.STATE_ALIVE);
		
		//TODO: Do we just want to have some sort of general LineSolver
		//instead of creating a new Solver for each missile?
		MotionSolver ms1 = new LineSolver ();
		m1.setMotionSolver(ms1);
		
		try
		{
			mSem.acquire ();
			mEnemyMissiles.add (m1);
			mSem.release ();
		}
		catch (InterruptedException e)
		{
			System.err.println ("InterruptedException in CanvasThread createEnemyMissile: " + e.getMessage ());
		}
		
		Message msg = mMotionEventHandler.obtainMessage (GlobalData.ENEMY_MISSILE_GENERATION);
        msg.target = mMotionEventHandler;
        mMotionEventHandler.sendMessageDelayed (msg, 1000);
    }
    
    private void updateFriendlyProjectiles (int timeElapsed)
    {
    	for (int i = 0; i < mFriendlyMissiles.size (); i++)
		{
			GameMissile m = (GameMissile) mFriendlyMissiles.get (i);
			MotionSolver ms = m.getMotionSolver ();
			ExplosionUpdater eu = m.getExplosionUpdater ();
			
			//Update positions and explosions
			switch (m.getState ())
			{
				case GameObject.STATE_ALIVE:
					if (ms != null)
					{
						ms.solveMotion (m, timeElapsed);
					}
					
					for (int j = 0; j < mEnemyMissiles.size (); j++)
					{
						GameMissile otherMissile = (GameMissile) mEnemyMissiles.get (j);
						
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
							if (otherMissile.getState () == GameObject.STATE_ALIVE)
							{
								otherMissile.setState (GameObject.STATE_DYING);
								
//								Log.i ("Missile collision:", "" + m + " into " + otherMissile + " at " + mCurrentPos.x + "," + mCurrentPos.y);

								//Calculate the score for this missile
								sendScoreUpdate (m.getScoreValue (), ScoreCalculator.PROXIMITY_MULTIPLIER_VALUE);
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
							
							mFriendlyMissiles.addAll (shrapnelChildren);
						}
					}
					//Don't want to update an explosion if we just created it
					else
					{
						if (eu != null)
						{
							eu.updateExplosion (exp, timeElapsed);
						}
					}
					
					for (int j = 0; j < mEnemyMissiles.size (); j++)
					{
						GameObject otherMissile = mEnemyMissiles.get (j);

						//Calculate the distance between the current missile
						//and the second one
						PointF otherCurrentPos = otherMissile.getCurrentPos ();
						Explosion e = m.getExplosion ();
						PointF mCurrentPos = e.getStartingPosition ();
						double coreDistance = Physics.calculateDistance (mCurrentPos, otherCurrentPos);
						
						if (e instanceof WaveExplosion)
						{
							WaveExplosion wave = (WaveExplosion) e;
							float waveRadius = wave.getCurrentRadius ();

							//Our wave explosion hit something
							if (coreDistance <= waveRadius)
							{
								//Don't want to re-activate a dead object
								if (otherMissile.getState () == GameObject.STATE_ALIVE)
								{
									otherMissile.setState (GameObject.STATE_DYING);
									
									//Calculate the score for this missile
//									Log.i ("Explosion collision:", "" + e + " into " + otherMissile + " at " + mCurrentPos.x + "," + mCurrentPos.y);
									
									//Calculate the score for this missile
									int baseScore = m.getScoreValue ();
									
									float maxRadius = wave.getExplosionRadius ();
									float halfMaxRadius = maxRadius / 2;
									float multiplier = 0;
									
									if (waveRadius < halfMaxRadius)
									{
										float radiusFraction = waveRadius / halfMaxRadius;
										float reciprocol = 1 - radiusFraction;
										
										multiplier = ScoreCalculator.HALF_MAX_RADIUS_MULTIPLIER_VALUE + reciprocol;
									}
									else
									{
										float radiusFraction = waveRadius / maxRadius;
										float reciprocol = 1 - radiusFraction;
										
										multiplier = ScoreCalculator.BASE_SCORE_MULTIPLIER_VALUE + reciprocol;
									}
									
									sendScoreUpdate (baseScore, multiplier);
								}
							}
						}
						
						//TODO: Add code for shrapnel type explosion
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
//							Log.i ("Removing missile:", "" + m);
							mFriendlyMissiles.remove (m);
							i--;
						}
					}
					else
					{
//						Log.i ("Removing missile:", "" + m);
						mFriendlyMissiles.remove (m);
						i--;
					}
					break;
			} //End of switch			
		} //End of for loop
    } //End of updateFriendlyProjectiles
    
    private void updateEnemyProjectiles (int timeElapsed)
    {
    	for (int i = 0; i < mEnemyMissiles.size (); i++)
		{
			GameMissile m = (GameMissile) mEnemyMissiles.get (i);
			MotionSolver ms = m.getMotionSolver ();
			ExplosionUpdater eu = m.getExplosionUpdater ();
			
			//Update positions and explosions
			switch (m.getState ())
			{
				case GameObject.STATE_ALIVE:
					if (ms != null)
					{
						ms.solveMotion (m, timeElapsed);
					}
					
					for (int j = 0; j < mBases.size (); j++)
					{
						GameBase base = (GameBase) mBases.get (j);
						
						//Calculate the distance between the current missile
						//and the base
						PointF mCurrentPos = m.getCurrentPos ();
						PointF otherCurrentPos = base.getCurrentPos ();
						double distance = Physics.calculateDistance (mCurrentPos, otherCurrentPos);
						
						distance -= base.getBaseRadius ();
						
						//We just smacked into something
						if (distance <= m.getProximityRadius ())
						{
							m.setState (GameObject.STATE_DYING);
							
							//TODO: Should the base take damage from the actual impact?
//							if (base.getState () == GameObject.STATE_ALIVE)
//							{
//								int baseHP = base.getHitPoints ();
//								int missileDamage = m.getExplosionDamage ();
//								
//								baseHP -= missileDamage;
//								base.setHitPoints (baseHP);
//								
//								//If the base just died
//								if (baseHP <= 0)
//								{
//									base.setState (GameObject.STATE_DEAD);
//								}
//							}
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
							
							mFriendlyMissiles.addAll (shrapnelChildren);
						}
					}
					//Don't want to update an explosion if we just created it
					else
					{
						if (eu != null)
						{
							eu.updateExplosion (exp, timeElapsed);
						}
					}
					
					for (int j = 0; j < mBases.size (); j++)
					{
						GameBase base = (GameBase) mBases.get (j);
						
						//Calculate the distance between the current missile
						//and the base
						PointF mCurrentPos = m.getCurrentPos ();
						PointF otherCurrentPos = base.getCurrentPos ();
						double coreDistance = Physics.calculateDistance (mCurrentPos, otherCurrentPos);
						Explosion e = m.getExplosion ();
						float baseRadius = base.getBaseRadius (); 
						
						if (e instanceof WaveExplosion)
						{
							WaveExplosion wave = (WaveExplosion) e;
							float previousRadius = wave.getPreviousRadius ();
							float currentRadius = wave.getCurrentRadius ();

							//Subtract the base radius
							coreDistance -= baseRadius;
							
							//Our wave explosion hit something
							if (coreDistance <= currentRadius &&
								coreDistance > previousRadius)
							{
								if (base.getState () == GameObject.STATE_ALIVE)
								{
									int baseHP = base.getHitPoints ();
									int missileDamage = m.getExplosionDamage ();
									
									Log.i ("updateEnemyProjectiles: ", "Base took damage");
									
									baseHP -= missileDamage;
									base.setHitPoints (baseHP);
									
									//If the base just died
									if (baseHP <= 0)
									{
										Log.i ("updateEnemyProjectiles: ", "Base dying");
										base.setState (GameObject.STATE_DYING);
									}
								}
							}
						}
						
						//TODO: Add code for shrapnel type explosion
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
//							Log.i ("Removing missile:", "" + m);
							mEnemyMissiles.remove (m);
							i--;
						}
					}
					else
					{
//						Log.i ("Removing missile:", "" + m);
						mEnemyMissiles.remove (m);
						i--;
					}
					break;
			} //End of switch			
		} //End of for loop
    } //End of updateEnemyProjectiles
    
    private void updateBases (int timeElapsed)
    {
    	for (int i = 0; i < mBases.size (); i++)
		{
			GameBase base = (GameBase) mBases.get (i);
			ExplosionUpdater eu = base.getExplosionUpdater ();
			
			//Update positions and explosions
			switch (base.getState ())
			{
				case GameObject.STATE_ALIVE:
					break;
					
				case GameObject.STATE_DYING:
					Explosion exp = base.getExplosion ();
					
					//Important to only have one explosion creation point
					//since we're setting the state to DYING in multiple places
					if (exp == null)
					{
						base.createExplosion ();
						eu = base.getExplosionUpdater ();
					}
					//Don't want to update an explosion if we just created it
					else
					{
						if (eu != null)
						{
							eu.updateExplosion (exp, timeElapsed);
						}
					}
					break;
					
				case GameObject.STATE_DEAD:
					//Verify each child is dead as well
					ArrayList<GameObject> children = base.getChildren ();
					
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
//							Log.i ("Removing missile:", "" + m);
							mBases.remove (base);
							i--;
						}
					}
					else
					{
//						Log.i ("Removing missile:", "" + m);
						mBases.remove (base);
						i--;
					}
					break;
			} //End of switch			
		} //End of for loop
    	
    	if (mBases.size () == 0)
    	{
    		//TODO: Game Over
//    		Log.i ("updateBases: ", "Game Over");
    		mDone = true;
    		
    		
    	}
    } //End of updateBases

    private void updateProjectiles(int timeElapsed)
	{
    	try
    	{
    		mSem.acquire ();
    		
    		updateFriendlyProjectiles (timeElapsed);
    		updateEnemyProjectiles (timeElapsed);
    		updateBases (timeElapsed);
    		
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
    	
	    	for (int i = 0; i < mFriendlyMissiles.size (); i++)
			{
				GameMissile m = (GameMissile) mFriendlyMissiles.get (i);
				
				//Draw positions and explosions
				switch (m.getState ())
				{
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
	    	
	    	for (int i = 0; i < mEnemyMissiles.size (); i++)
			{
				GameMissile m = (GameMissile) mEnemyMissiles.get (i);
				
				//Draw positions and explosions
				switch (m.getState ())
				{
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
    
    private void drawBases (Canvas canvas, Paint paint, int timeElapsed)
    {
    	try
    	{
    		mSem.acquire ();
    	
	    	for (int i = 0; i < mBases.size (); i++)
			{
				GameBase base = (GameBase) mBases.get (i);
				
				//Draw positions and explosions
				switch (base.getState ())
				{
					case GameObject.STATE_ALIVE:
	                    base.draw(canvas, paint);
						break;
						
					case GameObject.STATE_DYING:
						Explosion ex = base.getExplosion ();
						
						if (ex != null)
						{
							ex.drawExplosion (canvas, timeElapsed);
						}
						break;
						
					default:
						break;
				}
			} //End of for loop
	    	
	    	mSem.release ();
	    } //End of try
		catch (InterruptedException e)
		{
			System.err.println ("InterruptedException in updateBases: " + e.getMessage ());
		}
    } //End of drawBases
    
    void sendScoreUpdate (int baseScore, float multiplier)
    {
    	StatusUpdateMessage scoreMsg = ScoreCalculator.calculateMissileScore (baseScore,
				  multiplier);
		Handler uiThreadHandler = GlobalData.uiThreadHandler;
		Message msg = uiThreadHandler.obtainMessage (GlobalData.STATUS_UPDATE_EVENT_TYPE, scoreMsg);
		msg.target = uiThreadHandler;
		uiThreadHandler.sendMessage (msg);
    }
} //End of CanvasThread class