package com.anvil.android.boom.logic;

import android.graphics.PointF;

public abstract class Physics {

	public static final float VELOCITY_MAX = 1000;
	public static final float VELOCITY_MIN = 0;
	
	public static double calculateDistance (PointF p1, PointF p2)
	{
		double distanceX = 0, distanceY = 0;
		double distance = 0;
		
		distanceX = p1.x - p2.x;
		distanceY = p1.y - p2.y;
		
		distance = Math.sqrt (distanceY*distanceY + distanceX*distanceX);
		
		return distance;
	}
}