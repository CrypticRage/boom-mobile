package com.anvil.android.particle;

public class GlobalData {
	public static final int ANGLE_DIVISOR = 3;
	public static final int TABLE_SIZE = ANGLE_DIVISOR * 360;
	public static float[] sinTable = new float[TABLE_SIZE];
	public static float[] cosTable = new float[TABLE_SIZE];
	
	for (int i = 0; i < TABLE_SIZE; i++) {
		double angle = 2*Math.PI*i/360;
		sinTable[i] = (float)Math.sin(angle);
		cosTable[i] = (float)Math.cos(angle);
	}
}