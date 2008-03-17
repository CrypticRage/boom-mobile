package com.anvil.digital.android.logic;

import android.graphics.Canvas;
import android.graphics.PointF;



public abstract class Explosion {
	
	//TODO: Need to figure out if the velocity range 
	//[0, 100] is appropriate
	public static int DEFAULT_WAVE_EXPLOSION_VELOCITY = 5; 

	protected PointF mStartingPos;		//The starting position of the explosion
	protected int mExplosionRadius;		//The maximum radius of the explosion

	protected int mExplosionVelocity;		//Velocity explosion travels outward
	
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
	
	public Explosion (float x, float y, int radius, int velocity, GameMissile parent)
	{
		mStartingPos = new PointF (x, y);
		mExplosionRadius = radius;
		mExplosionVelocity = velocity;
		
		mParent = parent;
	}
	
	public int getExplosionVelocity ()
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
	
	public GameMissile getParent ()
	{
		return mParent;
	}
	
	public abstract void drawExplosion (Canvas canvas);		//Renders the Explosion on the screen
} //End of class Explosion
