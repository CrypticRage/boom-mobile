package com.anvil.android.boom.logic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import java.util.ArrayList;

import com.anvil.android.boom.particles.SpriteInstance;

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
	protected PointF mVelocityVector;	// Velocity unit vector (direction)
	protected float mVelocityScalar;	// Velocity scalar (magnitude)
	
	// Game rule objects
	protected int mPointsAward; // How many points for killing this thing.  Negative penalties apply
	protected int mHitPoints;   // How hard to kill this thing.  Should be >0.

	// The state of this object
	protected int mState;
	
	// Parent/child relationships.
	// For example, a shield may have a missile as its child, indicating the dependency between
	// the two.  Physics updates are pushed from parent to children.
	protected GameObject mParent;
	protected ArrayList<GameObject> mChildren;
		
	protected SpriteInstance mSprite; // The graphic asset associated with this object.
	protected float drawAngle;
	
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
		calcDrawAngle();
		calcInitVelocity();
	}

	private void calcDrawAngle() {
		float hyp = Physics.calculateDistance(mStartingPos, mTargetPos);
		float opp = mStartingPos.y - mTargetPos.y;
		double tempAngle = Math.asin(opp/hyp);
		tempAngle = Math.toDegrees(tempAngle);
		if (mTargetPos.x > mStartingPos.x) {
			drawAngle = 180.0f - (float)tempAngle;
		}
		else if (mTargetPos.x < mStartingPos.x) {
			drawAngle = (float)tempAngle;
		}
		else if (mTargetPos.x == mStartingPos.x) {
			drawAngle = 90.0f;
		}		
	}
	
	private void calcInitVelocity() {
		mVelocityVector = PointF.difference(mTargetPos, mStartingPos);
		mVelocityVector = Physics.normalize(mVelocityVector);
	}
	
	public float getVelocity() {
		return mVelocityScalar;
	}
	
	public void setVelocity(float velocity) {
		if (velocity > Physics.VELOCITY_MIN && velocity < Physics.VELOCITY_MAX)
			mVelocityScalar = velocity;
		else if (velocity > Physics.VELOCITY_MAX)
			mVelocityScalar = Physics.VELOCITY_MAX;
		else 
			mVelocityScalar = Physics.VELOCITY_MIN;
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
			mChildren = new ArrayList<GameObject> ();
		}
		
		mChildren.add (child);
		child.setParent (this);
	}
	
	public ArrayList<GameObject> getChildren ()
	{
		return mChildren;
	}

	public SpriteInstance getSprite() {
		return mSprite;
	}

	public void setSprite(SpriteInstance sprite) {
		mSprite = sprite;
	}

	abstract public void draw(Canvas canvas, Paint paint);
	
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
	
	public abstract void createExplosion ();
	
	public Explosion getExplosion ()
	{
		return mExplosion;
	}
} //End of class GameObject
