package com.anvil.android.boom.logic;

import com.anvil.android.boom.GlobalData;
import com.anvil.android.boom.graphics.SpriteInstance;
import com.anvil.android.boom.graphics.SpriteData;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.anvil.android.boom.logic.WaveExplosion;

public class GameBase extends GameObject {
	
	public static final int DEFAULT_BASE_HIT_POINTS = 1000;
	
	public static final float DEFAULT_BASE_RADIUS = 30;
	public static final float DEFAULT_BASE_EXPLOSION_RADIUS = 100;
	
	protected float mBaseRadius;
		
	public GameBase (float startingX, float startingY, float radius, int numHitPoints)
	{
		super ();
		
		mCurrentPos = new PointF (startingX, startingY);
		mStartingPos = new PointF (startingX, startingY);
		
		mSprite = new SpriteInstance(SpriteData.sprites[SpriteData.DOME_BASE]);
		mSprite.setRadius(radius);
		
		mHitPoints = numHitPoints;
		
		mBaseRadius = radius;
	}
	
	public float getBaseRadius ()
	{
		return mBaseRadius;
	}
	
	@Override
	public void draw(Canvas canvas, Paint paint, int timeElapsed)
	{	
		switch (mState)
		{
			case GameObject.STATE_ALIVE:
				RectF tempBox = mSprite.getDrawBox();
				
				canvas.save();
				canvas.translate(mCurrentPos.x, mCurrentPos.y);		
		    	canvas.drawBitmap(mSprite.sprite, null, tempBox, paint);
				canvas.restore();
				break;
				
			default:
				break;
		}
	}

	public void createExplosion ()
	{
		mExplosion = new WaveExplosion (mCurrentPos.x, mCurrentPos.y,
										DEFAULT_BASE_EXPLOSION_RADIUS, 
										WaveExplosion.DEFAULT_WAVE_EXPLOSION_VELOCITY,
										this);
		
		mExplosionUpdater = new WaveExplosionUpdater ();
	}
}
