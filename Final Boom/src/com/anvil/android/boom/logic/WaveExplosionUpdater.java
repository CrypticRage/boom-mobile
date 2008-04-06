package com.anvil.android.boom.logic;

public class WaveExplosionUpdater extends ExplosionUpdater {

	@Override
	public void updateExplosion (Explosion explosion, int timeElapsed)
	{
		WaveExplosion wave = (WaveExplosion) explosion;
		float velocityPerUSecond = (wave.mExplosionVelocity / MotionSolver.MICROSECONDS_PER_SECOND) * timeElapsed;
		float currentRadius = wave.mCurrentRadius;
		float maxExplosionRadius = wave.mExplosionRadius;
		
		wave.setPreviousRadius (currentRadius);
		currentRadius += velocityPerUSecond;
		
		wave.setCurrentRadius (currentRadius);
		
		//Check to see if the explosion has reached its
		//max radius
		if (currentRadius >= maxExplosionRadius)
		{
			(wave.mParent).setState (GameObject.STATE_DEAD);
		}
	}
}
