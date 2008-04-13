package com.anvil.android.boom.logic;

import com.anvil.android.boom.GlobalData;
import com.anvil.android.boom.particles.SmokeEmitter2D;
import com.anvil.android.boom.graphics.SpriteInstance;
import com.anvil.android.boom.graphics.SpriteData;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.PointF;


public abstract class GameMissile extends GameObject {
	
	public static final int DEFAULT_NUM_SMOKE_PARTICLES = 30;
	public static final float DEFAULT_PROXIMITY_RADIUS = 5;
	public static final float DEFAULT_FRIENDLY_MISSILE_VELOCITY = 125;
	
	public static final int DEFAULT_SCORE_VALUE = 100;
	
	protected int mExplosionDamage;		//Damage the explosion deals
	
	protected float mExplosionRadius;	//Radius of explosion
	protected float mProximityRadius;	//If a non-friendly is detected within
									//this radius, trigger explosion
	
	protected SmokeEmitter2D mSmokeEmitter;
	protected PointF smokeOffset = new PointF(0.0f, 0.0f);
	//protected PointF smokeOffset;
	
	// TODO find some list type to use.
	// TODO if a MIRV exists as children in a container, how do the child missiles get
	//      promoted to the main list when the container 'opens?'  Does this even need to happen
	
	
	public GameMissile()
	{
		mExplosionRadius = 0;
		mProximityRadius = DEFAULT_PROXIMITY_RADIUS;
		
		mExplosion = null;
		
		mPointsAward = DEFAULT_SCORE_VALUE;
	}
	
	public GameMissile (float radius, float startingX, float startingY, float endingX, float endingY)
	{
		mExplosionRadius = radius;
		mProximityRadius = DEFAULT_PROXIMITY_RADIUS;
		
		mStartingPos = new PointF (startingX, startingY);
		mCurrentPos = new PointF (startingX, startingY);
		setTargetPos (new PointF (endingX, endingY));
		
		mExplosion = null;
		
		mPointsAward = DEFAULT_SCORE_VALUE;
	}
	
	@Override
	//TODO: Replace with real drawing code
	public abstract void draw (Canvas canvas, Paint paint, int timeElapsed);
	
	public float getProximityRadius ()
	{
		return mProximityRadius;
	}
	
	public float getExplosionRadius ()
	{
		return mExplosionRadius;
	}
	
	@Override
	public void setCurrentPos(PointF currentPos)
	{
		mCurrentPos = currentPos;

		//mSmokeEmitter.x = currentPos.x;
		//mSmokeEmitter.y = currentPos.y;
		mSmokeEmitter.x = currentPos.x + smokeOffset.x;
		mSmokeEmitter.y = currentPos.y + smokeOffset.y;
	}
	
	public SmokeEmitter2D getSmokeEmitter ()
	{
		return mSmokeEmitter;
	}
	
	public int getScoreValue ()
	{
		return mPointsAward;
	}
	
	public int getExplosionDamage ()
	{
		return mExplosionDamage;
	}
} //End of class GameMissile
