package org.soadl.TotoroBoom;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import java.util.Vector;

public abstract class GameObject {
	// Game object states are done using a constant rather than an Enum.
	// See http://code.google.com/android/toolbox/performance.html#avoid_enums for details.
	public static final int STATE_ALIVE  = 1; // 'Normal' state
	public static final int STATE_LARVAL = 2; // Object in process, not yet fully created.
	public static final int STATE_DYING  = 3; // Object performing death cycle
	public static final int STATE_DEAD   = 4; // Object life complete.  May be GC'd
	
	// Physics objects - where are we, where are we going, how fast are we getting there
	protected PointF mCurrentPos;
	protected PointF mStartingPos;
	protected PointF mTargetPos;
	protected int mVelocity;
	
	
	// Game rule objects
	protected int mPointsAward; // How many points for killing this thing.  Negative penalties apply
	protected int mHitPoints;   // How hard to kill this thing.  Should be >0.

	// The state of this object
	protected int mState;
	
	// Parent/child relationships.
	// For example, a shield may have a missile as its child, indicating the dependency between
	// the two.  Physics updates are pushed from parent to children.
	protected GameObject mParent;
	protected Vector<GameObject> mChildren;
		
	protected Drawable mDrawable; // The graphic asset associated with this object.
	
	protected MotionSolver mMotionSolver; // The motion solver for this object
	
	protected Explosion mExplosion;
	protected ExplosionUpdater mExplosionUpdater;
	
	public GameObject()
	{
		mState = STATE_LARVAL;
		mParent = null;
	}


	public PointF getCurrentPos() {
		return mCurrentPos;
	}

	public void setCurrentPos(PointF currentPos) {
		mCurrentPos = currentPos;
	}
	
	public PointF getStartingPos ()
	{
		return mStartingPos;
	}
	
	public void setStartingPos (PointF startingPosition)
	{
		mStartingPos = startingPosition;
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
		if (velocity > Physics.VELOCITY_MIN && velocity < Physics.VELOCITY_MAX)
			mVelocity = velocity;
		else if (velocity > Physics.VELOCITY_MAX)
			mVelocity = Physics.VELOCITY_MAX;
		else 
			mVelocity = Physics.VELOCITY_MIN;
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
	
	public void addChild (GameObject child)
	{
		if (mChildren == null)
		{
			mChildren = new Vector<GameObject> ();
		}
		
		mChildren.add (child);
		child.setParent (this);
	}
	
	public Vector<GameObject> getChildren ()
	{
		return mChildren;
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
	
	public ExplosionUpdater getExplosionUpdater ()
	{
		return mExplosionUpdater;
	}
	
	public void setExplosionSolver (ExplosionUpdater explosionUpdater)
	{
		mExplosionUpdater = explosionUpdater;
	}
} //End of class GameObject
