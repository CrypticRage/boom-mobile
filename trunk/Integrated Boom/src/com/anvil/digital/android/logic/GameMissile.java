package com.anvil.digital.android.logic;

import com.anvil.digital.android.graphics.ParticleEmitter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;


public abstract class GameMissile extends GameObject {
	
	public static final int DEFAULT_NUM_SMOKE_PARTICLES = 30;
	public static final int DEFAULT_PROXIMITY_RADIUS = 1;
	
	protected int mExplosionRadius;	//Radius of explosion
	protected int mProximityRadius;	//If a non-friendly is detected within
									//this radius, trigger explosion
	
	protected ParticleEmitter mEmitter;
	
	// TODO find some list type to use.
	// TODO if a MIRV exists as children in a container, how do the child missiles get
	//      promoted to the main list when the container 'opens?'  Does this even need to happen
	
	
	public GameMissile()
	{
		mExplosionRadius = 0;
		mProximityRadius = DEFAULT_PROXIMITY_RADIUS;
		
		mExplosion = null;
	}
	
	public GameMissile (int radius, float startingX, float startingY)
	{
		mExplosionRadius = radius;
		mProximityRadius = DEFAULT_PROXIMITY_RADIUS;
		
		mStartingPos = new PointF (startingX, startingY);
		mCurrentPos = new PointF (startingX, startingY);
		mEmitter = new ParticleEmitter (DEFAULT_NUM_SMOKE_PARTICLES, startingX, startingY);
		
		mExplosion = null;
	}

	
	@Override
	//TODO: Replace with real drawing code
	public void draw(Canvas canvas) {
		switch (this.getState ())
		{
			case GameObject.STATE_LARVAL:
			case GameObject.STATE_ALIVE:
			{
				int missileRadius = 6;
				Paint bluePaint = new Paint();
				
				bluePaint.setAntiAlias(true);
		        bluePaint.setARGB(255, 0, 0, 255);
				canvas.drawCircle(mCurrentPos.x - (missileRadius / 2), 
								  mCurrentPos.y - (missileRadius / 2),
						          missileRadius, bluePaint);
			}
			break;
			
			case GameObject.STATE_DYING:
			{
				Paint redPaint = new Paint();
				
				redPaint.setAntiAlias(true);
		        redPaint.setARGB(255, 0, 0, 255);
				canvas.drawCircle(mCurrentPos.x, 
								  mCurrentPos.y,
								  mExplosionRadius, redPaint);
			}
			break;
			
			case GameObject.STATE_DEAD:
				break;
				
			default:
				break;
		}
	}
	
	public int getProximityRadius ()
	{
		return mProximityRadius;
	}
	
	public int getExplosionRadius ()
	{
		return mExplosionRadius;
	}
	
	@Override
	public void setCurrentPos(PointF currentPos)
	{
		mCurrentPos = currentPos;
		
		mEmitter.x = currentPos.x;
		mEmitter.y = currentPos.y;
	}
	
	public ParticleEmitter getParticleEmitter ()
	{
		return mEmitter;
	}
} //End of class GameMissile
