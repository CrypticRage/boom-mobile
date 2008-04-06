package com.anvil.android.boom.logic;

public abstract class MotionSolver {
	public static final float MICROSECONDS_PER_SECOND = 1000000.0f;
	public static final float INV_MICROSECONDS_PER_SECOND = 1/MICROSECONDS_PER_SECOND;
	
	public abstract void solveMotion(GameObject object, int timeElapsed);
}
