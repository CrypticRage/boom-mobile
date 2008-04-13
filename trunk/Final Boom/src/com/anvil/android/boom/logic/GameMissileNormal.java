package com.anvil.android.boom.logic;

import com.anvil.android.boom.graphics.SpriteData;
import com.anvil.android.boom.graphics.SpriteInstance;
import com.anvil.android.boom.particles.SmokeEmitter2D;
import com.anvil.android.boom.logic.Physics;

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
	
	public GameMissileNormal (float explosionRadius, float startingX, float startingY, float endingX, float endingY)
	{
		super (explosionRadius, startingX, startingY, endingX, endingY);
		init(startingX, startingY);
	}
	
	public GameMissileNormal (float explosionRadius, float startingX, float startingY, float endingX, float endingY, boolean friendly)
	{
		super (explosionRadius, startingX, startingY, endingX, endingY);
		init(startingX, startingY);
		
		if (friendly)
		{
			mSprite = new SpriteInstance(SpriteData.sprites[SpriteData.STD_MISSILE]);
		}
		else
		{
			mSprite = new SpriteInstance(SpriteData.sprites[SpriteData.STD_MISSILE]);
		}	
		mSprite.setRadius (12.0f);
	}
	
	private void init(float startingX, float startingY) {
		mSmokeEmitter = new SmokeEmitter2D (DEFAULT_NUM_SMOKE_PARTICLES,
				startingX, startingY, SpriteData.sprites[SpriteData.SMOKE_CLOUD]);
		mSmokeEmitter.angle = drawAngle;
		smokeOffset = Physics.scale(mVelocityVector, -12.0f);
		
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
				
				mSmokeEmitter.update (timeElapsed);
				mSmokeEmitter.draw (canvas);
				
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
} //End of class GameMissileNormal
