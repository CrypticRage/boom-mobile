package com.anvil.android.boom.logic;


public class GameMissileMIRV extends GameMissile {

	public GameMissileMIRV()
	{
		mExplosionRadius = 0;
		mProximityRadius = 1;
	}
	
	public GameMissileMIRV (int radius)
	{
		mExplosionRadius = radius;
		mProximityRadius = 2;
	}
	
	@Override
	public void createExplosion ()
	{
	}
} //End of class GameMissileMIRV
