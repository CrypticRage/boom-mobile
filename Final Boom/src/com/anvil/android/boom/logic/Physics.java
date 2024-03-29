package com.anvil.android.boom.logic;

import android.graphics.PointF;

public class Physics {
	public static final float MICROSECONDS_PER_SECOND = 1000000.0f;
	public static final float INV_MICROSECONDS_PER_SECOND = 1/MICROSECONDS_PER_SECOND;
	
	public static final long MILLISECONDS_PER_SECOND = 1000;
	
	public static final float VELOCITY_MAX = 500.0f;
	public static final float VELOCITY_MIN = 0.0f;
	
	public static float calculateDistance (PointF p1, PointF p2)
	{
		double distanceX = 0, distanceY = 0;
		double distance = 0;
		
		distanceX = p1.x - p2.x;
		distanceY = p1.y - p2.y;
		
		distance = Math.sqrt (distanceY*distanceY + distanceX*distanceX);
		
		return (float)distance;
	}
	
	public static PointF scale (PointF p, float scale) {
		PointF newPoint = new PointF(p.x * scale, p.y * scale);
		return newPoint;
	}

	public static PointF normalize (PointF p) {
		float distance = calculateDistance(new PointF(0.0f, 0.0f), p);
		PointF newPoint = new PointF(p.x/distance, p.y/distance);
		return newPoint;
	}

	public static float magnituge (PointF p) {
		float mag = (float)Math.sqrt(p.x*p.x + p.y*p.y);
		return mag;
	}
	
	public static float dotProduct (PointF p1, PointF p2) {
		float dot = p1.x*p2.x + p1.y*p2.y;
		return dot;
	}
	
	public static boolean checkCrossing (PointF startingPos, PointF targetPos, PointF nextPos) {
		boolean targetReached = false;
		
		// Check for off-axis crossings
		if (startingPos.x > targetPos.x && startingPos.y < targetPos.y) {
			if (nextPos.x <= targetPos.x && nextPos.y >= targetPos.y) {
				targetReached = true;
			}
		}		
		if (startingPos.x < targetPos.x && startingPos.y > targetPos.y) {
			if (nextPos.x >= targetPos.x && nextPos.y <= targetPos.y) {
				targetReached = true;
			}
		}
		if (startingPos.x > targetPos.x && startingPos.y > targetPos.y) {
			if (nextPos.x <= targetPos.x && nextPos.y <= targetPos.y) {
				targetReached = true;
			}
		}		
		if (startingPos.x < targetPos.x && startingPos.y < targetPos.y) {
			if (nextPos.x >= targetPos.x && nextPos.y >= targetPos.y) {
				targetReached = true;
			}
		}
		
		// Check for on-axis crossings
		if (startingPos.x > targetPos.x && startingPos.y == targetPos.y) {
			if (nextPos.x <= targetPos.x) {
				targetReached = true;
			}
		}		
		if (startingPos.x < targetPos.x && startingPos.y == targetPos.y) {
			if (nextPos.x >= targetPos.x) {
				targetReached = true;
			}
		}
		if (startingPos.x == targetPos.x && startingPos.y > targetPos.y) {
			if (nextPos.y <= targetPos.x) {
				targetReached = true;
			}
		}
		if (startingPos.x == targetPos.x && startingPos.y < targetPos.y) {
			if (nextPos.y >= targetPos.x) {
				targetReached = true;
			}
		}
		
		// Check for rare case where starting point is equal to ending point
		if (startingPos.x == targetPos.x && startingPos.y == targetPos.y) {
			targetReached = true;
		}				
		
		return targetReached;
	}
}