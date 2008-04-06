package com.anvil.android.boom.logic;

import com.anvil.android.boom.GlobalData;
import com.anvil.android.boom.particles.SmokeEmitter2D;
import com.anvil.android.boom.particles.SpriteInstance;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.Paint;
import android.graphics.PointF;


public abstract class GameMissile extends GameObject {
	
	public static final int DEFAULT_NUM_SMOKE_PARTICLES = 30;
	public static final float DEFAULT_PROXIMITY_RADIUS = 5;
	public static final float DEFAULT_FRIENDLY_MISSILE_VELOCITY = 50;
	
	public static final int DEFAULT_SCORE_VALUE = 100;
	
	protected int mExplosionDamage;		//Damage the explosion deals
	
	protected float mExplosionRadius;	//Radius of explosion
	protected float mProximityRadius;	//If a non-friendly is detected within
									//this radius, trigger explosion
	
	protected SmokeEmitter2D mSmokeEmitter;
	
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
	
	public GameMissile (float radius, float startingX, float startingY, boolean friendly)
	{
		mExplosionRadius = radius;
		mProximityRadius = DEFAULT_PROXIMITY_RADIUS;
		
		mStartingPos = new PointF (startingX, startingY);
		mCurrentPos = new PointF (startingX, startingY);
		
		if (friendly)
		{
			mSprite = new SpriteInstance(GlobalData.sprites[1]);
		}
		else
		{
			mSprite = new SpriteInstance(GlobalData.sprites[4]);
		}
		
		mSprite.setScale(0.20f);
		mSprite.setRadius (12);
		
		//TODO: Need to change so we're not accessing a specific index in the sprite array
		mSmokeEmitter = new SmokeEmitter2D (DEFAULT_NUM_SMOKE_PARTICLES, startingX, startingY, GlobalData.sprites[0]);
		
		mExplosion = null;
		
		mPointsAward = DEFAULT_SCORE_VALUE;
	}

	
	@Override
	//TODO: Replace with real drawing code
	public void draw (Canvas canvas, Paint paint)
	{
		switch (this.getState ())
		{
			case GameObject.STATE_ALIVE:
			{
				RectF tempBox = mSprite.getDrawBox();
				
				canvas.save();
				canvas.translate(mCurrentPos.x, mCurrentPos.y);
				canvas.rotate(drawAngle);				
		    	canvas.drawBitmap(mSprite.sprite, null, tempBox, paint);
				canvas.restore();
			}
			break;
			
			case GameObject.STATE_DYING:
				break;
			
			case GameObject.STATE_DEAD:
				break;
				
			default:
				break;
		}
	}
	
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
		
		mSmokeEmitter.x = currentPos.x;
		mSmokeEmitter.y = currentPos.y;
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
