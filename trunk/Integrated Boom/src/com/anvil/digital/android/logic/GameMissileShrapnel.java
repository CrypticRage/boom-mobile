package com.anvil.digital.android.logic;


public class GameMissileShrapnel extends GameMissile {

	public GameMissileShrapnel()
	{
		super ();
	}
	
	public GameMissileShrapnel (int radius, float startingX, float startingY)
	{
		super (radius, startingX, startingY);
	}
	
	@Override
	public void createExplosion ()
	{
		mState = GameObject.STATE_DEAD;
		
		mExplosion = new ShrapnelExplosion (mCurrentPos.x,
											mCurrentPos.y,
											mExplosionRadius,
											ShrapnelExplosion.DEFAULT_SHRAPNEL_EXPLOSION_VELOCITY,
											this);
		
		//Shrapnel explosions don't have their own explosion updater, because 
		//their movement updates are called by the updateProjectiles method 
		mExplosionUpdater = null;
	}
} //End of class GameMissileShrapnel
