package org.soadl.TotoroBoom;

import android.graphics.Canvas;
import android.graphics.Paint;



public class WaveExplosion extends Explosion {

	protected int mCurrentRadius;

	
	public WaveExplosion (GameMissile parent)
	{
		super (parent);
		
		mCurrentRadius = 0;
	}
	
	public WaveExplosion (float x, float y, int radius, int velocity, GameMissile parent)
	{
		super (x, y, radius, velocity, parent);
		
		mCurrentRadius = 0;
	}
	
	public int getCurrentRadius ()
	{
		return mCurrentRadius;
	}
	
	public void setCurrentRadius (int radius)
	{
		mCurrentRadius = radius;
	}
	
	@Override
	public void drawExplosion (Canvas canvas)
	{
		Paint redPaint = new Paint();
		
		redPaint.setAntiAlias(true);
		redPaint.setARGB(255, 255, 0, 0);
		canvas.drawCircle(mStartingPos.x, 
						  mStartingPos.y,
						  mCurrentRadius, redPaint);
	}
} //End of class WaveExplosion
