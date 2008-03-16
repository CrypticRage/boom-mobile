package com.anvil.android.util;

public class CosTable {
	public static final int ANGLE_DIVISOR = 3;
	public static final int TABLE_SIZE = ANGLE_DIVISOR * 360;
	
	public static float[] value;
	static {
		value = new float[TABLE_SIZE];
		
		for (int i = 0; i < TABLE_SIZE; i++) {
			double angle = 2*Math.PI*i/TABLE_SIZE;
			value[i] = (float)(Math.cos(angle));
		}
	}
}