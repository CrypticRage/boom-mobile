package com.anvil.android.boom;

import android.graphics.Typeface;
import android.os.Handler;

public class GlobalData {
	public static volatile float fps = 0.0f;
	public static volatile long frameTimeMicro = 0;
	public static volatile int overCount = 0;
	public static volatile long overTimeMicro = 0;
	
	public static volatile Handler uiThreadHandler;
	public static volatile Handler canvasThreadHandler;
	
	public static volatile Typeface textFont;
	
	public static volatile int gameScore = 0; 
	public static volatile String bottomStatusText;
	
	public static final int MOTION_EVENT_TYPE = 1;
	public static final int STATUS_UPDATE_EVENT_TYPE = 2;
	public static final int ENEMY_MISSILE_GENERATION = 3;
	public static final int FRIENDLY_MISSILE_RELOAD = 4;
	public static final int BASE_KILLER_RESPAWN = 5;
		
	public static final int STANDARD_MISSILE = 0;
	public static final int SMART_BOMB = 1;	
	public static volatile int AMMO_TYPE = STANDARD_MISSILE;
}