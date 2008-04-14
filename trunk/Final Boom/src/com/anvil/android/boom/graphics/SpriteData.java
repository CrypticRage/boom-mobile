package com.anvil.android.boom.graphics;

import android.graphics.Bitmap;

public class SpriteData {
	public static volatile Bitmap[] bgSprites;		
	public static final int bgSpriteCount = 2;
	public static final int BG_GROUND = 0;
	public static final int BG_MOUNTAINS = 1;
	
	public static volatile Bitmap[] sprites;
	public static final int spriteCount = 8;
	public static final int SMOKE_CLOUD = 0;
	public static final int STD_MISSILE = 1;
	public static final int SMART_BOMB = 2;
	public static final int GOLD_MISSILE = 3;
	public static final int DOME_BASE = 4;
	public static final int RED_CROSS = 5;
	public static final int BLAST = 6;
	public static final int INTRO_SCREEN = 7;	
}