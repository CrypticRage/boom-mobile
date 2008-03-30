package com.anvil.android.boom.logic;

import android.graphics.Canvas;
import android.graphics.PointF;



public abstract class Explosion {
	
	protected PointF mStartingPos;		//The starting position of the explosion
	protected float mExplosionRadius;		//The maximum radius of the explosion

	protected float mExplosionVelocity;		//Velocity explosion travels outward
	
	protected GameMissile mParent;
	
	
	public Explosion ()
	{
	}

	public Explosion (GameMissile parent)
	{
		mStartingPos = new PointF (0, 0);
		mExplosionRadius = 0;
		mExplosionVelocity = 0;
		
		mParent = parent;
	}
	
	public Explosion (float x, float y, float radius, float velocity, GameMissile parent)
	{
		mStartingPos = new PointF (x, y);
		mExplosionRadius = radius;
		mExplosionVelocity = velocity;
		
		mParent = parent;
	}
	
	public float getExplosionVelocity ()
	{
		return mExplosionVelocity;
	}
	
	public void setExplosionVelocity (int v)
	{
		if (v > Physics.VELOCITY_MIN && v < Physics.VELOCITY_MAX)
			mExplosionVelocity = v;
		else if (v > Physics.VELOCITY_MAX)
			mExplosionVelocity = Physics.VELOCITY_MAX;
		else 
			mExplosionVelocity = Physics.VELOCITY_MIN;
	}
	
	public float getExplosionRadius ()
	{
		return mExplosionRadius;
	}
	
	public GameMissile getParent ()
	{
		return mParent;
	}
	
	public PointF getStartingPosition ()
	{
		return mStartingPos;
	}
	
	public abstract void drawExplosion (Canvas canvas, int timeElapsed);		//Renders the Explosion on the screen
} //End of class Explosion
