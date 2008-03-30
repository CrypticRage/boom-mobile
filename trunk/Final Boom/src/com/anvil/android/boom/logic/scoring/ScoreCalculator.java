package com.anvil.android.boom.logic.scoring;

import android.util.Log;

public abstract class ScoreCalculator
{
	public static final int BASE_SCORE_MULTIPLIER_VALUE = 1;
	public static final int HALF_MAX_RADIUS_MULTIPLIER_VALUE = 2;
	public static final int PROXIMITY_MULTIPLIER_VALUE = 3;
	
	public static StatusUpdateMessage calculateMissileScore (int points, float multiplier)
	{
		StatusUpdateMessage msg = new StatusUpdateMessage ();
		msg.mScoreChange = (int) (points * multiplier);
		
//		Log.i ("calculateMissileScore: ", "Score: " + msg.mScoreChange);
		
		return msg;
	}
}