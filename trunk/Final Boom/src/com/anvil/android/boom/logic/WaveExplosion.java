package com.anvil.android.boom.logic;

import android.graphics.Canvas;
import com.anvil.android.boom.GlobalData;
import com.anvil.android.boom.particles.BlastEmitter2D;



public class WaveExplosion extends Explosion {

	protected int mCurrentRadius;
	
	BlastEmitter2D mBlastEmitter;
	

	
	public WaveExplosion (GameMissile parent)
	{
		super (parent);
		
		mCurrentRadius = 0;
		
		mBlastEmitter = null;
	}
	
	public WaveExplosion (float x, float y, int radius, int velocity, GameMissile parent)
	{
		super (x, y, radius, velocity, parent);
		
		mCurrentRadius = 0;
		
		//TODO: Need to change so we're not accessing a specific index in the sprite array
		mBlastEmitter = new BlastEmitter2D(x, y, GlobalData.sprites[2]);
	}
	
	public int getCurrentRadius ()
	{
		return mCurrentRadius;
	}
	
	public void setCurrentRadius (int radius)
	{
		mCurrentRadius = radius;
	}
	
	@Override
	public void drawExplosion (Canvas canvas, int timeElapsed)
	{
		switch (mBlastEmitter.status)
		{
			case BlastEmitter2D.ALIVE:
				mBlastEmitter.update (timeElapsed);
				mBlastEmitter.draw (canvas);
				break;
				
			case BlastEmitter2D.DEAD:
				break;
		}
	}
} //End of class WaveExplosion
