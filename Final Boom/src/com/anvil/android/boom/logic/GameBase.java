package com.anvil.android.boom.logic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

public class GameBase extends GameObject {
	
	public static final int DEFAULT_BASE_HIT_POINTS = 1000;
		
	public GameBase (float startingX, float startingY, int numHitPoints)
	{
		super ();
		
		mCurrentPos = new PointF (startingX, startingY);
		mStartingPos = new PointF (startingX, startingY);
		
		mHitPoints = numHitPoints;
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint) {
	}

	public void createExplosion ()
	{
	}
}
