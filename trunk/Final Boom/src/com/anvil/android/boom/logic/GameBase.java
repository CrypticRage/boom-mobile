package com.anvil.android.boom.logic;

import com.anvil.android.boom.GlobalData;
import com.anvil.android.boom.particles.SpriteInstance;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

public class GameBase extends GameObject {
	
	public static final int DEFAULT_BASE_HIT_POINTS = 1000;
	
	public static final float DEFAULT_BASE_RADIUS = 30;
	
	protected float mBaseRadius;
		
	public GameBase (float startingX, float startingY, float radius, int numHitPoints)
	{
		super ();
		
		mCurrentPos = new PointF (startingX, startingY);
		mStartingPos = new PointF (startingX, startingY);
		
		mSprite = new SpriteInstance(GlobalData.sprites[3]);
		mSprite.setRadius(radius);
		
		mHitPoints = numHitPoints;
		
		mBaseRadius = radius;
	}
	
	public float getBaseRadius ()
	{
		return mBaseRadius;
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint) {
		RectF tempBox = mSprite.getDrawBox();
		
		canvas.save();
		canvas.translate(mCurrentPos.x, mCurrentPos.y);
		//canvas.rotate(drawAngle);				
    	canvas.drawBitmap(mSprite.sprite, null, tempBox, paint);
		canvas.restore();
	}

	public void createExplosion ()
	{
	}
}
