package com.anvil.android.boom.logic;

public abstract class MotionSolver {
	public static final float MICROSECONDS_PER_SECOND = 1000000;
	
	public abstract void solveMotion(GameObject object, int timeElapsed);
}
