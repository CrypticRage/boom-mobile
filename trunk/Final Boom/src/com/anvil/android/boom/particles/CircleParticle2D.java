package com.anvil.android.boom.particles;

import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.ShapeDrawable;

public class CircleParticle2D extends Particle2D {
	public OvalShape shape;
	public ShapeDrawable shapeDraw;
	public int alpha;
	public float radius;
	
	public CircleParticle2D(float radius) {
		super();
		init(radius);
	}
	
	public CircleParticle2D(float x, float y, float radius) {
		super(x, y);
		init(radius);
	}
		
	private void init(float radius) {
		this.radius = radius;
		this.shape = new OvalShape();
		this.shapeDraw = new ShapeDrawable(shape);
		this.alpha = 255;
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