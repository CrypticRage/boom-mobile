package com.anvil.android.particles;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

public abstract class ParticleEmitter2D {
	protected ArrayList<Particle2D> deadPool = null;
	protected ArrayList<Particle2D> livePool = null;
	
	public int numParticles = 0;
	protected int liveCount = 0;
	protected int deadCount = 0;
	
	public float x = 0.0f;
	public float y = 0.0f;
	public int angle = 0;	

	public int lifetime = 0;
	public int age = 0;
	public int decay = 0;
	public int currentDecay = 0;
	public int rate = 0;
	public int rateLife = 0;
	
	public float scatter = 0.0f;
	public float hscatter = 0.0f;
	public float vscatter = 0.0f;
	
	public float spread;
	public float width;
	public float height;
	
	public Paint paint = null;

	
	public ParticleEmitter2D(int numParticles, float x, float y) {
		this.numParticles = numParticles;
		this.livePool = new ArrayList<Particle2D>(numParticles);
		this.deadPool = new ArrayList<Particle2D>(numParticles);
		this.x = x;
		this.y = y;
		this.paint = new Paint();
	}

	public ParticleEmitter2D() {
		this.numParticles = 0;
		this.livePool = new ArrayList<Particle2D>();
		this.deadPool = new ArrayList<Particle2D>();
		this.x = 0.0f;
		this.y = 0.0f;
		this.paint = new Paint();
	}
	
	abstract void newParticle(Particle2D particle);
	
	// this updates the status of all particles based on the time
	// elapsed since the last update (in microseconds)
	abstract void update(long time);
	
	abstract void draw(Canvas canvas);
}