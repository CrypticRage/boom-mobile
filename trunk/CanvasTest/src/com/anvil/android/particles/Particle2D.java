package com.anvil.android.particles;

public abstract class Particle2D {
	public float x = 0.0f;
	public float y = 0.0f;
	public float vx = 0.0f;
	public float vy = 0.0f;
	public int randomness = 0;
	public int lifetime = 0;
	public int age = 0;
	public int decay = 0;
	public int currentDecay = 0;

	// protected ParticleEmitter2D emitter;

	public Particle2D() {
	}
	
	public Particle2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setPos(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public abstract void move(float width, float height);
}