package org.soadl.TotoroBoom;

import android.graphics.PointF;

public class LineSolver extends MotionSolver {

	@Override
	public void solveMotion(GameObject object) {
		PointF curPos = object.getCurrentPos();
		PointF startingPos = object.getStartingPos ();
		PointF targetPos = object.getTargetPos();
		PointF nextPos = new PointF (curPos.x, curPos.y);
		double targetDistance = 0, distanceTraveled = 0;
		double vertDistTraveled = 0, horizDistTraveled = 0;
		double expVertDistTravel = 0, expHorizDistTravel = 0;
		double angle = 0;
		double forceX = 0, forceY = 0;
		
		if (object.getState () == GameObject.STATE_LARVAL || object.getState () == GameObject.STATE_ALIVE)
		{
			//If two points are not horizontal or vertical
			if ((targetPos.y != startingPos.y) && (targetPos.x != startingPos.x))
			{
				angle = (float) Math.atan ((targetPos.y - startingPos.y) / (targetPos.x - startingPos.x));
				forceX = Math.cos (angle) * object.getVelocity ();
				forceY = Math.sin (angle) * object.getVelocity ();
				
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
					nextPos.y = curPos.y + object.getVelocity ();
				}
				else
				{
					nextPos.x = curPos.x + object.getVelocity ();
				}
			}
	
			//Calculate expected distance to travel
			expVertDistTravel = targetPos.y - startingPos.y;
			expHorizDistTravel = targetPos.x - startingPos.x;
			targetDistance = Math.sqrt (Math.pow (expVertDistTravel, 2) + Math.pow (expHorizDistTravel, 2));
			
			//Calculate distance traveled so far
			vertDistTraveled = nextPos.y - startingPos.y;
			horizDistTraveled = nextPos.x - startingPos.x;
			distanceTraveled = Math.sqrt (Math.pow(vertDistTraveled, 2) + Math.pow (horizDistTraveled, 2));
			
			object.setCurrentPos(nextPos);
			
			//If the object has reached its destination or passed it
			if (distanceTraveled >= targetDistance)
			{
				object.setState (GameObject.STATE_DYING);
			}
		}
	}
}
