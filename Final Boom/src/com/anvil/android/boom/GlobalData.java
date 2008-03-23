package com.anvil.android.boom;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class GlobalData {
	public static volatile float fps = 0.0f;
	public static volatile long frameTimeMicro = 0;
	public static volatile int overCount = 0;
	public static volatile long overTimeMicro = 0;
	
	public static volatile Bitmap[] sprites;
	public static volatile Bitmap background;
	
	public static volatile float x, y;
	public static volatile Rect clipBox;
	
	public static final int ZOOMING = 0;
	public static final int MIN_ZOOM = 1;
	public static final int MAX_ZOOM = 1;
	public static volatile int gameState = MIN_ZOOM;
	
	public static volatile String bottomStatusText;
}