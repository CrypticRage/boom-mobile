package org.soadl.TotoroBoom;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;

public abstract class GameObject {
	// Game object states are done using a constant rather than an Enum.
	// See http://code.google.com/android/toolbox/performance.html#avoid_enums for details.
	public static final int STATE_ALIVE  = 1; // 'Normal' state
	public static final int STATE_LARVAL = 2; // Object in process, not yet fully created.
	public static final int STATE_DYING  = 3; // Object performing death cycle
	public static final int STATE_DEAD   = 4; // Object life complete.  May be GC'd
	
	// Physics objects - where are we, where are we going, how fast are we getting there
	private PointF mStartingPos;
	private PointF mCurrentPos;
	private PointF mTargetPos;
	private int mVelocity;
	
	public static final int VELOCITY_MAX = 100;
	public static final int VELOCITY_MIN = 0;
	
	// Game rule objects
	private int mPointsAward; // How many points for killing this thing.  Negative penalties apply
	private int mHitPoints;   // How hard to kill this thing.  Should be >0.

	// The state of this object
	private int mState;
	
	// Parent/child relationships.
	// For example, a shield may have a missile as its child, indicating the dependency between
	// the two.  Physics updates are pushed from parent to children.
	
	private GameObject mParent;
	// TODO find some list type to use.
	// TODO if a MIRV exists as children in a container, how do the child missiles get
	//      promoted to the main list when the container 'opens?'  Does this even need to happen
	
	private Drawable mDrawable; // The graphic asset associated with this object.
	
	private MotionSolver mMotionSolver; // The motion solver for this object
	
	public GameObject()
	{
		mState = STATE_LARVAL;
	}

	public PointF getStartingPos ()
	{
		return mStartingPos;
	}
	
	public void setStartingPos (PointF startingPosition)
	{
		mStartingPos = startingPosition;
	}
	
	public PointF getCurrentPos() {
		return mCurrentPos;
	}

	public void setCurrentPos(PointF currentPos) {
		mCurrentPos = currentPos;
	}

	public PointF getTargetPos() {
		return mTargetPos;
	}

	public void setTargetPos(PointF targetPos) {
		mTargetPos = targetPos;
	}

	public int getVelocity() {
		return mVelocity;
	}

	public void setVelocity(int velocity) {
		if (velocity > VELOCITY_MIN && velocity < VELOCITY_MAX)
			mVelocity = velocity;
		else if (velocity > VELOCITY_MAX)
			mVelocity = VELOCITY_MAX;
		else 
			mVelocity = VELOCITY_MIN;
	}

	public int getPointsAwarded() {
		return mPointsAward;
	}

	public void setPointsAwarded(int pointsAwarded) {
		mPointsAward = pointsAwarded;
	}

	public int getHitPoints() {
		return mHitPoints;
	}

	public void setHitPoints(int hitPoints) {
		mHitPoints = hitPoints;
	}

	public int getState() {
		return mState;
	}

	public void setState(int state) {
		mState = state;
	}

	public GameObject getParent() {
		return mParent;
	}

	public void setParent(GameObject parent) {
		mParent = parent;
	}

	public Drawable getDrawable() {
		return mDrawable;
	}

	public void setDrawable(Drawable drawable) {
		mDrawable = drawable;
	}

	abstract public void draw(Canvas canvas);

	public MotionSolver getMotionSolver() {
		return mMotionSolver;
	}

	public void setMotionSolver(MotionSolver motionSolver) {
		mMotionSolver = motionSolver;
	}

	
}
