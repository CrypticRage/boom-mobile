package com.anvil.android.boom.logic;

import android.graphics.PointF;

public class LineSolver extends MotionSolver {

	@Override
	public void solveMotion(GameObject object, int timeElapsed) {
		PointF curPos = object.mCurrentPos;
		PointF startingPos = object.mStartingPos;
		PointF targetPos = object.mTargetPos;
		PointF velocityVec = object.mVelocityVector;
		PointF nextPos = new PointF (curPos.x, curPos.y);
		float scalar = object.mVelocityScalar * timeElapsed * INV_MICROSECONDS_PER_SECOND;
		int objectState = object.mState;
		
		if (objectState == GameObject.STATE_LARVAL ||
			objectState == GameObject.STATE_ALIVE) {
				
				nextPos.x += scalar * velocityVec.x;
				nextPos.y += scalar * velocityVec.y;
				
				//Check if the object has reached its destination or passed it
				boolean targetReached = Physics.checkCrossing(startingPos, targetPos, nextPos);		
				
				if (targetReached)
				{
					object.setCurrentPos (targetPos);
					object.setState (GameObject.STATE_DYING);
				}
				else
				{
					object.setCurrentPos (nextPos);
				}	
			}
	}
	
/*
	@Override
	public void solveMotion(GameObject object, int timeElapsed) {
		PointF curPos = object.mCurrentPos;
		PointF startingPos = object.mStartingPos;
		PointF targetPos = object.mTargetPos;
		PointF nextPos = new PointF (curPos.x, curPos.y);
		double targetDistance = 0, distanceTraveled = 0;
		double angle = 0;
		double forceX = 0, forceY = 0;
		int objectState = object.mState;
		
		if (objectState == GameObject.STATE_LARVAL || 
			objectState == GameObject.STATE_ALIVE)
		{
			float objectVelocity = object.getVelocity ();
			float velocityPerUSecond = (objectVelocity / MotionSolver.MICROSECONDS_PER_SECOND) * timeElapsed;
			
			//If two points are not horizontal or vertical
			if ((targetPos.y != startingPos.y) && (targetPos.x != startingPos.x))
			{
				angle = Math.atan ((targetPos.y - startingPos.y) / (targetPos.x - startingPos.x));
				forceX = Math.cos (angle) * velocityPerUSecond;
				forceY = Math.sin (angle) * velocityPerUSecond;
				
				if (targetPos.x > curPos.x)
				{
					nextPos.x = curPos.x + (float) forceX;
					nextPos.y = curPos.y + (float) forceY;
				}
				else
				{
					nextPos.x = curPos.x - (float) forceX;
					nextPos.y = curPos.y - (float) forceY;
				}
			}
			//Else we're dealing with a horizontal or vertical case
			else
			{
				if (targetPos.x == startingPos.x)
				{
					if (targetPos.y > startingPos.y)
					{
						nextPos.y = curPos.y + velocityPerUSecond;
					}
					else
					{
						nextPos.y = curPos.y - velocityPerUSecond;
					}
				}
				else
				{
					if (targetPos.x > startingPos.x)
					{
						nextPos.x = curPos.x + velocityPerUSecond;
					}
					else
					{
						nextPos.x = curPos.x - velocityPerUSecond;
					}
				}
			}
	
			//Calculate expected distance to travel
			targetDistance = Physics.calculateDistance (targetPos, startingPos);
			
			//Calculate distance traveled so far
			distanceTraveled = Physics.calculateDistance (nextPos, startingPos);
			
			//If the object has reached its destination or passed it
			if (distanceTraveled >= targetDistance)
			{
				object.setCurrentPos (targetPos);
				object.setState (GameObject.STATE_DYING);
			}
			else
			{
				object.setCurrentPos (nextPos);
			}
		}
	} //End of method solveMotion
*/
}//End of class LineSolver
