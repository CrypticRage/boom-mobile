package com.anvil.android.boom.graphics;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class SpriteInstance {
	public final Bitmap sprite;
	private RectF drawBox;
	private RectF originalBox;
	public final float width;
	public final float height;
	private float scale = 1.0f;
	private float aspectRatio = 1.0f;
	
	public SpriteInstance(Bitmap sprite) {
		this.sprite = sprite;
		width = (float)sprite.getWidth();
		height = (float)sprite.getHeight();
		drawBox = new RectF(-(width/2), -(height/2), width/2, height/2);
		originalBox = this.drawBox;

		if (width > height) {
			aspectRatio = width/height;
		}
		else if (width < height) {
			aspectRatio = height/width;
		}
	}

	public void setScale(float scale) {
		this.scale = scale;
		drawBox = originalBox;
		drawBox.right *= scale;
		drawBox.left *= scale;
		drawBox.top *= scale;
		drawBox.bottom *= scale;
	}
	
	public void setRadius(float radius) {
		if (width > height) {
			drawBox.left = -radius;
			drawBox.right = radius;
			drawBox.top = -(radius/aspectRatio);
			drawBox.bottom = radius/aspectRatio;
		}
		else if (width < height) {
			drawBox.top = -radius;
			drawBox.bottom = radius;
			drawBox.left = -(radius/aspectRatio);
			drawBox.right = radius/aspectRatio;
		}
		else {
			drawBox.top = -radius;
			drawBox.bottom = radius;
			drawBox.left = -radius;
			drawBox.right = radius;			
		}
	}
	
	public float getScale() {
		return scale;
	}

	public RectF getDrawBox() {
		return drawBox;
	}
}