package com.anvil.digital.android.logic;

import android.graphics.Canvas;
import android.graphics.Paint;


public class GameMissileNormal extends GameMissile {

	public GameMissileNormal()
	{
		mExplosionRadius = 0;
		mProximityRadius = 1;
		
		mExplosionUpdater = new WaveExplosionUpdater ();
	}
	
	public GameMissileNormal (int radius)
	{
		mExplosionRadius = radius;
		mProximityRadius = 2;
		
		mExplosionUpdater = new WaveExplosionUpdater ();
	}
	
	@Override
	public void createExplosion ()
	{
		mExplosion = new WaveExplosion (mCurrentPos.x, mCurrentPos.y,
				   mExplosionRadius, 
				   WaveExplosion.DEFAULT_WAVE_EXPLOSION_VELOCITY,
				   this);
	}
	
	@Override
	public void draw(Canvas canvas) {
		switch (this.getState ())
		{
			case GameObject.STATE_LARVAL:
			case GameObject.STATE_ALIVE:
			{
				int missleRadius = 6;
				Paint bluePaint = new Paint();
				
				bluePaint.setAntiAlias(true);
		        bluePaint.setARGB(255, 0, 0, 255);
				canvas.drawCircle(mCurrentPos.x - (missleRadius / 2), 
								  mCurrentPos.y - (missleRadius / 2),
						          missleRadius, bluePaint);
			}
			break;
			
			case GameObject.STATE_DYING:
			{			
				mExplosion.drawExplosion (canvas);
			}
			break;
			
			case GameObject.STATE_DEAD:
				break;
				
			default:
				break;
		}
	}
} //End of class GameMissileNormal
