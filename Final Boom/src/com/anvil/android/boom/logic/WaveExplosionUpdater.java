package com.anvil.android.boom.logic;

public class WaveExplosionUpdater extends ExplosionUpdater {

	@Override
	public void updateExplosion (Explosion explosion, int timeElapsed)
	{
		WaveExplosion wave = (WaveExplosion) explosion;
		float velocityPerUSecond = (wave.mExplosionVelocity / MotionSolver.MICROSECONDS_PER_SECOND) * timeElapsed;
		
		wave.mCurrentRadius += velocityPerUSecond;
		
		//Check to see if the explosion has reached its
		//max radius
		if (wave.mCurrentRadius >= wave.mExplosionRadius)
		{
			(wave.mParent).setState (GameObject.STATE_DEAD);
		}
	}
}
