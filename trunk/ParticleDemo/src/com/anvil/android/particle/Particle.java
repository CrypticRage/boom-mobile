package com.anvil.android.particle;

class Particle {
	public float x, y;
	public float vx, vy;
	public float size = 0.0f;
	public int color = 0;
	public int randomness = 0;
	public int lifetime = -1;
	public int decay = 1;
	
	protected ParticleEmitter emitter;

	public Particle(ParticleEmitter emitter) {
		this.emitter = emitter;
	}
	
	public void move(float width, float height) {
		if (randomness != 0) {
			vx += (Math.random() * randomness)-randomness/2;
			vy += (Math.random() * randomness)-randomness/2;
		}
		x += vx;
		y += vy;
		//vy += emitter.gravity;
		lifetime--;
	}
}