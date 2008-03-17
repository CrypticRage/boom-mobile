package com.anvil.digital.android.logic;

import android.graphics.Canvas;
import java.util.ArrayList;
import android.graphics.PointF;
import java.util.Random;


public class ShrapnelExplosion extends Explosion {

	public static final int DEFAULT_SHRAPNEL_EXPLOSION_PIECES = 5;
	public static final int DEFAULT_PIECE_EXPLOSION_RADIUS = 50;
	public static final int DEFAULT_SHRAPNEL_EXPLOSION_VELOCITY = 500;
	
	
	public ShrapnelExplosion (GameMissile parent)
	{
		super (parent);
	}
	
	public ShrapnelExplosion (float x, float y, int radius, int velocity, GameMissile parent)
	{
		super (x, y, radius, velocity, parent);
		
		MotionSolver ms = new LineSolver ();
		parent.mChildren = new ArrayList <GameObject> ();
		
		Random rand = new Random (System.currentTimeMillis ());
		
		for (int i = 0; i < DEFAULT_SHRAPNEL_EXPLOSION_PIECES; i++)
		{
			double angle = rand.nextDouble () * (2 * Math.PI);
			PointF targetPos = new PointF (x, y);
			int parentProximityRadius = parent.getProximityRadius ();
			GameMissileNormal m = new GameMissileNormal (DEFAULT_PIECE_EXPLOSION_RADIUS,
														 x + parentProximityRadius,
														 y + parentProximityRadius);

			targetPos.x = (float) (x + (radius * Math.sin (angle)));
			targetPos.y = (float) (y + (radius * Math.cos (angle)));
			
			m.setVelocity (velocity);
			m.setTargetPos (targetPos);
			m.setState (GameObject.STATE_ALIVE);
			m.setMotionSolver (ms);
			
			parent.mChildren.add (m);
		}
	}
	
	@Override
	public void drawExplosion (Canvas canvas)
	{
		
	}
	
} //End of class ShrapnelExplosion
