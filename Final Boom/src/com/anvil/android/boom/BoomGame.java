package com.anvil.android.boom;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.anvil.android.boom.logic.Explosion;
import com.anvil.android.boom.logic.ExplosionUpdater;
import com.anvil.android.boom.logic.GameBase;
import com.anvil.android.boom.logic.GameMissile;
import com.anvil.android.boom.logic.GameMissileNormal;
import com.anvil.android.boom.logic.GameMissileBaseKiller;
import com.anvil.android.boom.logic.GameMissileSmart;
import com.anvil.android.boom.logic.GameMissileShrapnel;
import com.anvil.android.boom.logic.GameObject;
import com.anvil.android.boom.logic.LineSolver;
import com.anvil.android.boom.logic.MotionSolver;
import com.anvil.android.boom.logic.Physics;
import com.anvil.android.boom.logic.WaveExplosion;
import com.anvil.android.boom.logic.scoring.ScoreCalculator;
import com.anvil.android.boom.logic.scoring.StatusUpdateMessage;
import com.anvil.android.boom.particles.SmokeEmitter2D;

public class BoomGame
{	
	public static final int MISSILE_RELOAD_TIME = 500;
	
	private static final int GAME_DIFFICULTY_EASY = 1;
	private static final int GAME_DIFFICULTY_MEDIUM = 2;
	private static final int GAME_DIFFICULTY_HARD = 3;
	private static final int GAME_DIFFICULTY_INSANE = 4;
	
	private Semaphore mSem;					//Semaphore to protect game object array lists
	private ArrayList<GameObject> mFriendlyMissiles; //Friendly GameMissile objects
	private ArrayList<GameObject> mEnemyMissiles; //Enemy GameMissile objects
	private ArrayList<GameBase> mBases; //Friendly GameBase objects
	
	private long mGameStartTime;
	
	public boolean mDone;
	
	private boolean mMissileReloadDone;
	
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
					
				case GlobalData.FRIENDLY_MISSILE_RELOAD:
					mMissileReloadDone = true;
					break;
					
				default:
					msg.recycle ();
					break;
			} //End of switch
		} //End of handleMessage
	} //End of MotionEventHandler class
	
	public BoomGame ()
	{
        //Initialize game objects
		mFriendlyMissiles = new ArrayList<GameObject> ();
		mEnemyMissiles = new ArrayList<GameObject> ();
		mBases = new ArrayList<GameBase> ();
		
		//Create base(s)
		GameBase base = new GameBase (240, 320,
									  GameBase.DEFAULT_BASE_RADIUS,
									  GameBase.DEFAULT_BASE_HIT_POINTS);
		base.setState (GameObject.STATE_ALIVE);
		mBases.add (base);
		
		//Create a new binary semaphore
		mSem = new Semaphore (1, true);
		
        mMotionEventHandler = new MotionEventHandler ();
        GlobalData.canvasThreadHandler = mMotionEventHandler;
        
        mDone = false;
        
        mGameStartTime = System.currentTimeMillis ();
        mMissileReloadDone = true;
	}
	
	public void createFriendlyMissile (float xCoord, float yCoord)
    {
		GameMissile m1;
		
		if (mMissileReloadDone)
		{
			mMissileReloadDone = false;
			
			//TODO: Check global munitions type
			if (true)
			{
				//Start off from the center base
				m1 = new GameMissileNormal (WaveExplosion.DEFAULT_FRIENDLY_WAVE_EXPLOSION_RADIUS,
														240, 320, true);
//				GameMissile m1 = new GameMissileNormal (WaveExplosion.DEFAULT_FRIENDLY_WAVE_EXPLOSION_RADIUS,
//														xCoord, yCoord);
				m1.setVelocity (GameMissile.DEFAULT_FRIENDLY_MISSILE_VELOCITY);
				m1.setTargetPos (new PointF (xCoord, yCoord));
				m1.setState (GameObject.STATE_ALIVE);
				
				//TODO: Do we just want to have some sort of general LineSolver
				//instead of creating a new Solver for each missile?
				MotionSolver ms1 = new LineSolver ();
				m1.setMotionSolver(ms1);
			}
			else
			{
				//Start off from the center base
				m1 = new GameMissileSmart (GameMissileSmart.DEFAULT_SMART_MISSILE_EXPLOSION_RADIUS,
														240, 320, true);
//				GameMissile m1 = new GameMissileSmart (WaveExplosion.DEFAULT_FRIENDLY_WAVE_EXPLOSION_RADIUS,
//														xCoord, yCoord);
				m1.setVelocity (GameMissileSmart.DEFAULT_SMART_MISSILE_VELOCITY);
				m1.setTargetPos (new PointF (xCoord, yCoord));
				m1.setState (GameObject.STATE_ALIVE);
				
				//TODO: Do we just want to have some sort of general LineSolver
				//instead of creating a new Solver for each missile?
				MotionSolver ms1 = new LineSolver ();
				m1.setMotionSolver(ms1);
			}
			
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
			
			Message msg = mMotionEventHandler.obtainMessage (GlobalData.FRIENDLY_MISSILE_RELOAD);
	        msg.target = mMotionEventHandler;
	        mMotionEventHandler.sendMessageDelayed (msg, MISSILE_RELOAD_TIME);
		}
    }
    
    public void createEnemyMissile ()
    {
    	//Determine a random X value to begin from
    	Random generator = new Random (System.currentTimeMillis ());
    	float startingX = generator.nextInt (480);
    	long currentTime = System.currentTimeMillis ();
    	GameMissile m1;
    	int gameDifficulty;
    	long runningTime = currentTime - mGameStartTime;
    	long runningTimeSeconds = runningTime / Physics.MILLISECONDS_PER_SECOND;
    	int missileGenerationTime, velocityAdditive;
    	
    	if (runningTimeSeconds < 30)
    	{
    		gameDifficulty = GAME_DIFFICULTY_EASY;
    		missileGenerationTime = 2000;
    		velocityAdditive = 0;
    	}
    	else if (runningTimeSeconds < 60)
    	{
    		gameDifficulty = GAME_DIFFICULTY_MEDIUM;
    		missileGenerationTime = 1500;
    		velocityAdditive = 5;
    	}
    	else if (runningTimeSeconds < 90)
    	{
    		gameDifficulty = GAME_DIFFICULTY_HARD;
    		missileGenerationTime = 1000;
    		velocityAdditive = 10;
    	}
    	else
    	{
    		gameDifficulty =  GAME_DIFFICULTY_INSANE;
    		missileGenerationTime = 500;
    		velocityAdditive = 15;
    	}
    	
    	//TODO: What should be the odds of a BaseKiller being generated?
    	if (generator.nextInt (10) == 0)
    	{
    		m1 = new GameMissileBaseKiller (WaveExplosion.DEFAULT_ENEMY_PAYLOAD_WAVE_EXPLOSION_RADIUS,
													startingX, 0, false);
    		m1.setVelocity (45 + velocityAdditive);
    		m1.setTargetPos (new PointF (240, 320));
    	}
    	else
    	{
    	    float endingX = generator.nextInt (480);
	    	int missileVelocity = generator.nextInt (25) + 20 + velocityAdditive;
			m1 = new GameMissileNormal (WaveExplosion.DEFAULT_ENEMY_PAYLOAD_WAVE_EXPLOSION_RADIUS,
													startingX, 0, false);
			m1.setVelocity (missileVelocity);
			m1.setTargetPos (new PointF (endingX, 320));
    	}

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
        mMotionEventHandler.sendMessageDelayed (msg, missileGenerationTime);
    }
    
    public void updateFriendlyProjectiles (int timeElapsed)
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
							if (otherMissile instanceof GameMissileBaseKiller)
							{
								//Only Smart missiles can take out BaseKillers
								if (m instanceof GameMissileSmart)
								{
									//Don't want to re-activate a dead object
									if (otherMissile.getState () == GameObject.STATE_ALIVE)
									{
										m.setState (GameObject.STATE_DYING);
										otherMissile.setState (GameObject.STATE_DYING);
										
										sendScoreUpdate (m.getScoreValue (),
										 		 ScoreCalculator.PROXIMITY_MULTIPLIER_VALUE *
										 		 	GameMissileSmart.SMART_MISSILE_SCORE_MULTIPLIER);
									}
								}
								//Everything else dies
								else
								{
									m.setState (GameObject.STATE_DYING);
								}
							}
							else
							{
								//Don't want to re-activate a dead object
								if (otherMissile.getState () == GameObject.STATE_ALIVE)
								{
									m.setState (GameObject.STATE_DYING);
									otherMissile.setState (GameObject.STATE_DYING);
									
//									Log.i ("Missile collision:", "" + m + " into " + otherMissile + " at " + mCurrentPos.x + "," + mCurrentPos.y);
									
									sendScoreUpdate (m.getScoreValue (), ScoreCalculator.PROXIMITY_MULTIPLIER_VALUE);
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
								if (otherMissile instanceof GameMissileBaseKiller)
								{
									//Only Smart missiles can take out BaseKillers
									if (m instanceof GameMissileSmart)
									{
										//Don't want to re-activate a dead object
										if (otherMissile.getState () == GameObject.STATE_ALIVE)
										{
											otherMissile.setState (GameObject.STATE_DYING);
											
											//Calculate the score for this missile
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
											
											sendScoreUpdate (m.getScoreValue (),
															 multiplier * GameMissileSmart.SMART_MISSILE_SCORE_MULTIPLIER);
										}
									}
								}
								else
								{
									//Don't want to re-activate a dead object
									if (otherMissile.getState () == GameObject.STATE_ALIVE)
									{
										otherMissile.setState (GameObject.STATE_DYING);
										
										//Calculate the score for this missile
//										Log.i ("Explosion collision:", "" + e + " into " + otherMissile + " at " + mCurrentPos.x + "," + mCurrentPos.y);
										
										//Calculate the score for this missile
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
										
										sendScoreUpdate (m.getScoreValue (), multiplier);
									}
								} //End of if !GameMissileBaseKiller
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
    
    public void updateEnemyProjectiles (int timeElapsed)
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
    
    public void updateBases (int timeElapsed)
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

    public void updateProjectiles(int timeElapsed)
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
    
    public void drawProjectiles (Canvas canvas, Paint paint, int timeElapsed)
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
	                    m.draw(canvas, paint, timeElapsed);
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
	                    m.draw(canvas, paint, timeElapsed);
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
    
    public void drawBases (Canvas canvas, Paint paint, int timeElapsed)
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
	                    base.draw(canvas, paint, timeElapsed);
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
    
    public void sendScoreUpdate (int baseScore, float multiplier)
    {
    	StatusUpdateMessage scoreMsg = ScoreCalculator.calculateMissileScore (baseScore,
				  multiplier);
		Handler uiThreadHandler = GlobalData.uiThreadHandler;
		Message msg = uiThreadHandler.obtainMessage (GlobalData.STATUS_UPDATE_EVENT_TYPE, scoreMsg);
		msg.target = uiThreadHandler;
		uiThreadHandler.sendMessage (msg);
    }
} //End of BoomGame class