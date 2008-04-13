package com.anvil.android.boom.logic;

import android.graphics.Canvas;
import com.anvil.android.boom.graphics.SpriteData;
import com.anvil.android.boom.particles.BlastEmitter2D;

public class WaveExplosion extends Explosion {
	
	//TODO: Need to figure out if the velocity range 
	//[0, 100] is appropriate
	public static float DEFAULT_WAVE_EXPLOSION_VELOCITY = 120; 
	
	public static final float DEFAULT_FRIENDLY_WAVE_EXPLOSION_RADIUS = 45;
	public static final float DEFAULT_ENEMY_PAYLOAD_WAVE_EXPLOSION_RADIUS = 30;
	public static final float DEFAULT_ENEMY_INTERCEPTED_WAVE_EXPLOSION_RADIUS = 10;

	public static final float ENEMY_MAX_DAMAGE_RADIUS = 10; //If a base gets hit within this radius, it takes max damage
	
	protected float mCurrentRadius;
	protected float mPreviousRadius;
	
	BlastEmitter2D mBlastEmitter;

	
	public WaveExplosion (GameObject parent)
	{
		super (parent);
		
		mCurrentRadius = 0;
		mPreviousRadius = 0;
		
		mBlastEmitter = null;
	}
	
	public WaveExplosion (float x, float y, float radius, float velocity, GameObject parent)
	{
		super (x, y, radius, velocity, parent);
		
		mCurrentRadius = 0;
		mPreviousRadius = 0;
		
		//TODO: Need to change so we're not accessing a specific index in the sprite array
		mBlastEmitter = new BlastEmitter2D(	x,
											y,
											velocity,
											radius,
											SpriteData.sprites[SpriteData.BLAST]
		);
	}
	
	public float getCurrentRadius ()
	{
		return mCurrentRadius;
	}
	
	public void setCurrentRadius (float radius)
	{
		mCurrentRadius = radius;
	}
	
	public float getPreviousRadius ()
	{
		return mPreviousRadius;
	}
	
	public void setPreviousRadius (float radius)
	{
		mPreviousRadius = radius;
	}
	
	@Override
	public void drawExplosion (Canvas canvas, int timeElapsed)
	{
		switch (mBlastEmitter.status)
		{
			case BlastEmitter2D.ALIVE:
				mBlastEmitter.update (timeElapsed);
				mBlastEmitter.draw (canvas);
				break;
				
			case BlastEmitter2D.DEAD:
				break;
		}
	}
} //End of class WaveExplosion
