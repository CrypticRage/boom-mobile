package com.anvil.android.boom.particles;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class SpriteInstance {
	public final Bitmap sprite;
	private RectF drawBox;
	public final float width;
	public final float height;
	private float scale;
	
	public SpriteInstance(Bitmap sprite) {
		this.sprite = sprite;
		this.width = (float)sprite.getWidth();
		this.height = (float)sprite.getHeight();
		this.drawBox = new RectF(-(width/2), -(height/2), width/2, height/2);
		this.scale = 1.0f;
	}

	public void setScale(float scale) {
		this.scale = scale;
		this.drawBox.right *= scale;
		this.drawBox.left *= scale;
		this.drawBox.top *= scale;
		this.drawBox.bottom *= scale;
	}
	
	public float getScale() {
		return scale;
	}

	public RectF getDrawBox() {
		return drawBox;
	}
}