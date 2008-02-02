package org.soadl.TotoroBoom;

import android.graphics.Canvas;
import android.graphics.Paint;

public class GameMissile extends GameObject {
	
	int mExplosionRadius;
	
	public GameMissile()
	{
		mExplosionRadius = 0;
	}
	
	public GameMissile (int radius)
	{
		mExplosionRadius = radius;
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
				canvas.drawCircle(getCurrentPos().x - (missleRadius / 2), 
						          getCurrentPos().y - (missleRadius / 2),
						          missleRadius, bluePaint);
			}
			break;
			
			case GameObject.STATE_DYING:
			{
				Paint redPaint = new Paint();
				redPaint.setAntiAlias(true);
				redPaint.setARGB(255, 255, 0, 0);
				canvas.drawCircle(getCurrentPos().x - (mExplosionRadius / 2), 
						          getCurrentPos().y - (mExplosionRadius / 2),
						          mExplosionRadius, redPaint);
				
				this.setState (GameObject.STATE_DEAD);
			}	
			break;
			
			case GameObject.STATE_DEAD:
				break;
				
			default:
				break;
		}
	}

}
