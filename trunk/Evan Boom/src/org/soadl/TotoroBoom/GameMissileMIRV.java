package org.soadl.TotoroBoom;


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
} //End of class GameMissileMIRV
