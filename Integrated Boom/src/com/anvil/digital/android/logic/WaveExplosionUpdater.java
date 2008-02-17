package com.anvil.digital.android.logic;

public class WaveExplosionUpdater extends ExplosionUpdater {

	@Override
	public void updateExplosion (Explosion explosion)
	{
		WaveExplosion wave = (WaveExplosion) explosion;
		
		wave.mCurrentRadius += wave.mExplosionVelocity;
		
		//Check to see if the explosion has reached its
		//max radius
		if (wave.mCurrentRadius >= wave.mExplosionRadius)
		{
			(wave.mParent).setState (GameObject.STATE_DEAD);
		}
	}
}
