package com.anvil.android.boom.logic;



public class GameMissileNormal extends GameMissile {

	public GameMissileNormal()
	{
		super ();
	}
	
	public GameMissileNormal (int explosionRadius, float startingX, float startingY)
	{
		super (explosionRadius, startingX, startingY);
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
