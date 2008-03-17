package com.anvil.android.boom.util;

public class FP {
	private static int Q_VALUE = 16;
	private static float Q_POWER = (float)Math.pow(2.0, (double)Q_VALUE);
	
	public static int ONE = 1 << Q_VALUE;
	
	/* Multiply two fixed-point numbers */
	 static public int mul (int x, int y) {
		long z = (long)x * (long)y;
		return ((int)(z >> Q_VALUE));
    }

	/* Convert an int to a fixed point value */ 
	 static public int intToFP (int x) {
		return x << Q_VALUE;
	}
	
	/* Convert a float to a fixed point value */ 
	static public int floatToFP (float x) {
		return (int)(x * Q_POWER);
	}
}
