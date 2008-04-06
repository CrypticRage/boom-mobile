package com.anvil.android.boom.logic;



public class GameMissileNormal extends GameMissile {
	
	public static final int DEFAULT_NORMAL_MISSILE_DAMAGE = 100;
	
	public GameMissileNormal()
	{
		super ();
		
		mExplosionDamage = DEFAULT_NORMAL_MISSILE_DAMAGE;
	}
	
	public GameMissileNormal (float explosionRadius, float startingX, float startingY)
	{
		super (explosionRadius, startingX, startingY);
		
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
} //End of class GameMissileNormal
