package com.anvil.android.boom.particles;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class SpriteParticle2D extends Particle2D {
	public Bitmap sprite;
	public Rect spriteBox;
	public int alpha;
	public float angle;
	
	public SpriteParticle2D(Bitmap sprite) {
		super();
		init(sprite);
	}
	
	public SpriteParticle2D(float x, float y, Bitmap sprite) {
		super(x, y);
		init(sprite);
	}
		
	private void init(Bitmap sprite) {
		this.sprite = sprite;
		this.spriteBox = new Rect(0, sprite.getHeight(), sprite.getWidth(), 0);
		this.alpha = 255;
		this.angle = 0.0f;
	}
	
	public void move(float width, float height) {
		if (randomness != 0) {
			vx += (Math.random() * randomness)-randomness/2;
			vy += (Math.random() * randomness)-randomness/2;
		}
		x += vx;
		y += vy;
	}
}