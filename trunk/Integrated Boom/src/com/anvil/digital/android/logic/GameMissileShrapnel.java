package com.anvil.digital.android.logic;


public class GameMissileShrapnel extends GameMissile {

	public GameMissileShrapnel()
	{
		mExplosionRadius = 0;
		mProximityRadius = 1;
	}
	
	public GameMissileShrapnel (int radius)
	{
		mExplosionRadius = radius;
		mProximityRadius = 2;
	}
	
	@Override
	public void createExplosion ()
	{
	}
} //End of class GameMissileShrapnel
