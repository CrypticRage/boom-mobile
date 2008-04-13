package com.anvil.android.boom.logic;



public class GameMissileBaseKiller extends GameMissileNormal {
	
	public static final int DEFAULT_BASE_KILLER_MISSILE_DAMAGE = GameBase.DEFAULT_BASE_HIT_POINTS;
	
	public GameMissileBaseKiller()
	{
		super ();
		
		mExplosionDamage = DEFAULT_BASE_KILLER_MISSILE_DAMAGE;
	}
	
	public GameMissileBaseKiller (float explosionRadius, float startingX, float startingY, boolean friendly, float endingX, float endingY)
	{
		super (explosionRadius, startingX, startingY, friendly, endingX, endingY);
		
		mExplosionDamage = DEFAULT_BASE_KILLER_MISSILE_DAMAGE;
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
} //End of class GameMissileNormal
