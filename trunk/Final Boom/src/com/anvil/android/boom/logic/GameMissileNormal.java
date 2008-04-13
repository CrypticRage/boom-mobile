package com.anvil.android.boom.logic;

import com.anvil.android.boom.graphics.SpriteData;
import com.anvil.android.boom.particles.SmokeEmitter2D;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class GameMissileNormal extends GameMissile {
	
	public static final int DEFAULT_NORMAL_MISSILE_DAMAGE = 100;
	
	public GameMissileNormal()
	{
		super ();
		
		mExplosionDamage = DEFAULT_NORMAL_MISSILE_DAMAGE;
	}
	
	public GameMissileNormal (float explosionRadius, float startingX, float startingY, boolean friendly, float endingX, float endingY)
	{
		super (explosionRadius, startingX, startingY, friendly, endingX, endingY);
		
		//TODO: Need to change so we're not accessing a specific index in the sprite array
		mSmokeEmitter = new SmokeEmitter2D (DEFAULT_NUM_SMOKE_PARTICLES,
				startingX, startingY, SpriteData.sprites[SpriteData.SMOKE_CLOUD]);
		
		mExplosionDamage = DEFAULT_NORMAL_MISSILE_DAMAGE;
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
	
	@Override
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
				
				mSmokeEmitter.update (timeElapsed);
				mSmokeEmitter.draw (canvas);
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
} //End of class GameMissileNormal
