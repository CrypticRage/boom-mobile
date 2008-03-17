package com.anvil.android.boom;

import android.graphics.Bitmap;

public class GlobalData {
	public static volatile float fps = 0.0f;
	public static volatile int frameTimeMicro = 0;
	public static volatile int overCount = 0;
	public static volatile int overTimeMicro = 0;
	
	public static volatile Bitmap[] sprites;
	public static volatile Bitmap background;
}