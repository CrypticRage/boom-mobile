package com.anvil.digital.android.logic;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class GameBase extends GameObject {
	RectF baseRect;
	
	public GameBase (float left, float top, float right, float bottom)
	{
		baseRect = new RectF (left, top, right, bottom);
	}
	
	@Override
	public void draw(Canvas canvas) {
		Paint redPaint = new Paint();
        redPaint.setAntiAlias(true);
        redPaint.setARGB(255, 255, 0, 0);
        
        canvas.drawRect(baseRect, redPaint);
	}

	public void createExplosion ()
	{
	}
}
