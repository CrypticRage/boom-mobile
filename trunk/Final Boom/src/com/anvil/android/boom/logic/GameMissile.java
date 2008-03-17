package com.anvil.android.boom.logic;

import com.anvil.android.boom.GlobalData;
import com.anvil.android.boom.particles.SmokeEmitter2D;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;


public abstract class GameMissile extends GameObject {
	
	public static final int DEFAULT_NUM_SMOKE_PARTICLES = 30;
	public static final int DEFAULT_PROXIMITY_RADIUS = 1;
	
	protected int mExplosionRadius;	//Radius of explosion
	protected int mProximityRadius;	//If a non-friendly is detected within
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
	}
	
	public GameMissile (int radius, float startingX, float startingY)
	{
		mExplosionRadius = radius;
		mProximityRadius = DEFAULT_PROXIMITY_RADIUS;
		
		mStartingPos = new PointF (startingX, startingY);
		mCurrentPos = new PointF (startingX, startingY);
		
		//TODO: Need to change so we're not accessing a specific index in the sprite array
		mSmokeEmitter = new SmokeEmitter2D (DEFAULT_NUM_SMOKE_PARTICLES, startingX, startingY, GlobalData.sprites[0]);
		
		mExplosion = null;
	}

	
	@Override
	//TODO: Replace with real drawing code
	public void draw (Canvas canvas, Paint paint)
	{
		switch (this.getState ())
		{
			case GameObject.STATE_LARVAL:
			case GameObject.STATE_ALIVE:
			{
				Bitmap temp = GlobalData.sprites[1];

		    	canvas.save();
				canvas.scale(0.5f, 0.5f);
				
				//TODO: Need to figure out the correct rotation for each missile based on
				//trajectory and flight path type
				canvas.rotate (180.0f + 45.0f,
							   mCurrentPos.x + (temp.getWidth () / 2) + 15.0f,
							   mCurrentPos.y + (temp.getHeight() / 2) + 15.0f);
		    	canvas.drawBitmap(temp, mCurrentPos.x, mCurrentPos.y, paint);
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
		
		mSmokeEmitter.x = currentPos.x;
		mSmokeEmitter.y = currentPos.y;
	}
	
	public SmokeEmitter2D getSmokeEmitter ()
	{
		return mSmokeEmitter;
	}
} //End of class GameMissile
