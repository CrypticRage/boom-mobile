package com.anvil.android.boom.logic;

import android.graphics.Canvas;
import android.graphics.Paint;


public class GameMissileShrapnel extends GameMissile {

	public GameMissileShrapnel()
	{
		super ();
	}
	
	public GameMissileShrapnel (int explosionRadius, float startingX, float startingY, boolean friendly, float endingX, float endingY)
	{
		super (explosionRadius, startingX, startingY, friendly, endingX, endingY);
	}
	
	@Override
	public void createExplosion ()
	{
		mState = GameObject.STATE_DEAD;
		
		mExplosion = new ShrapnelExplosion (mCurrentPos.x,
											mCurrentPos.y,
											mExplosionRadius,
											ShrapnelExplosion.DEFAULT_SHRAPNEL_EXPLOSION_VELOCITY,
											this);
		
		//Shrapnel explosions don't have their own explosion updater, because 
		//their movement updates are called by the updateProjectiles method 
		mExplosionUpdater = null;
	}
	
	@Override
	public void draw (Canvas canvas, Paint paint, int timeElapsed)
	{
		switch (this.getState ())
		{
			case GameObject.STATE_ALIVE:
			{

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
} //End of class GameMissileShrapnel
