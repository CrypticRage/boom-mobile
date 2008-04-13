package com.anvil.android.boom.logic;

import com.anvil.android.boom.graphics.SpriteData;
import com.anvil.android.boom.graphics.SpriteInstance;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;



public class GameMissileSmart extends GameMissileNormal {
	
	public static final int DEFAULT_SMART_MISSILE_DAMAGE = 100;
	
	public static final int DEFAULT_SMART_MISSILE_VELOCITY = 250;
	public static final float DEFAULT_SMART_MISSILE_EXPLOSION_RADIUS = 15;
	
	public static final int SMART_MISSILE_SCORE_MULTIPLIER = 2;
	
	public GameMissileSmart()
	{
		super ();
		
		mExplosionDamage = DEFAULT_SMART_MISSILE_DAMAGE;
	}
	
	public GameMissileSmart (float explosionRadius, float startingX, float startingY, float endingX, float endingY)
	{
		super (explosionRadius, startingX, startingY, endingX, endingY);
		
		mExplosionDamage = DEFAULT_SMART_MISSILE_DAMAGE;
		
		mSprite = new SpriteInstance(SpriteData.sprites[SpriteData.SMART_BOMB]);
		
		mSprite.setScale(0.20f);
		mSprite.setRadius (12);
	}
	
	@Override
	public void createExplosion ()
	{
		mExplosion = new WaveExplosion (mCurrentPos.x, mCurrentPos.y,
				   mExplosionRadius, 
				   WaveExplosion.DEFAULT_WAVE_EXPLOSION_VELOCITY,
				   this);
		
		mExplosionUpdater = new WaveExplosionUpdater ();
	}
	
	public void draw (Canvas canvas, Paint paint, int timeElapsed)
	{
		switch (this.getState ())
		{
			case GameObject.STATE_ALIVE:
			{
				RectF tempBox = mSprite.getDrawBox();
				
				canvas.save();
				canvas.translate(mCurrentPos.x, mCurrentPos.y);
				canvas.rotate(drawAngle);				
		    	canvas.drawBitmap(mSprite.sprite, null, tempBox, paint);
				canvas.restore();
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
} //End of class GameMissileSmart
