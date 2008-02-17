package com.anvil.digital.android.logic;

import android.graphics.Canvas;
import android.graphics.Paint;


public abstract class GameMissile extends GameObject {
	
	protected int mExplosionRadius;	//Radius of explosion
	protected int mProximityRadius;	//If a non-friendly is detected within
									//this radius, trigger explosion
	
	// TODO find some list type to use.
	// TODO if a MIRV exists as children in a container, how do the child missiles get
	//      promoted to the main list when the container 'opens?'  Does this even need to happen
	
	
	public GameMissile()
	{
		mExplosionRadius = 0;
		mProximityRadius = 1;
		
		mExplosion = null;
	}
	
	public GameMissile (int radius)
	{
		mExplosionRadius = radius;
		mProximityRadius = 2;
		
		mExplosion = null;
	}

	
	@Override
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
	
} //End of class GameMissile
