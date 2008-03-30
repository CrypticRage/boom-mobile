package com.anvil.android.boom.logic.scoring;

public class StatusUpdateMessage
{
	public int mScoreChange;
	
	public StatusUpdateMessage ()
	{
		mScoreChange = 0;
	}
	
	public StatusUpdateMessage (int points)
	{
		mScoreChange = points;
	}
}