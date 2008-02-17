package org.soadl.TotoroBoom;


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
} //End of class GameMissileShrapnel
