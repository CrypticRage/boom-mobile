package com.anvil.android.boom.logic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;


public class GameMissileMIRV extends GameMissile {

	public GameMissileMIRV()
	{
		mExplosionRadius = 0;
		mProximityRadius = 1;
	}
	
	public GameMissileMIRV (int radius)
	{
		mExplosionRadius = radius;
		mProximityRadius = 2;
	}
	
	@Override
	public void createExplosion ()
	{
	}
	
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
} //End of class GameMissileMIRV
