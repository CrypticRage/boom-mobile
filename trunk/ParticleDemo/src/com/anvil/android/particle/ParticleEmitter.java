package com.anvil.android.particle;

import android.graphics.Color;

class ParticleEmitter {
	public Particle[] particles;
	public float rate = 100;
	public int angle = 0;	// 0 degrees is north
	public float spread = 90;
	//public float gravity = (1 << 8);
	public int lifetime = 85;
	public float scatter = 2.5f;
	public float hscatter = 2.6f;
	public float vscatter = 2.6f;
	public float x;
	public float y;
	public float speed = 1.0f;
	public float size = 0.5f;
	public float width;
	public float height;
	public float speedVariation = 0;
	//public int decay = 1;
	//public int randomness = (7 << 8);
	public int color;
	public float numParticles;
	
	private static float[] sinTable, cosTable;
	private Particle p;
	
	static {
		sinTable = new float[360];
		cosTable = new float[360];
		for (int i = 0; i < 360; i++) {
			double angle = 2*Math.PI*i/360;
			sinTable[i] = (float)(Math.sin(angle));
			cosTable[i] = (float)(Math.cos(angle));
		}
	}

	public ParticleEmitter(int numParticles, float x, float y, float width, float height) {
		this.numParticles = numParticles;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		particles = new Particle[numParticles];
		for (int i = 0; i < numParticles; i++) {
			particles[i] = new Particle(this);
			newParticle(particles[i]);
		}
	}

	public double gaussian() {
		double sum = 0;
		for (int i = 0; i < 12; i++) {
			sum += Math.random();
		}
		return (sum-6)/3.0;
	}
	
	public void newParticle(Particle particle) {
		particle.color = Color.GRAY;
		particle.size = size;
		particle.lifetime = (int)(Math.random()*lifetime);
		particle.decay = particle.lifetime;
		particle.x = x;
		particle.y = y;
		//particle.randomness = randomness;
		if (scatter != 0) {
			int a = ((int)(Math.random() * 360)) % 360;
			double distance = scatter * Math.random();
			particle.x += (cosTable[a] * distance);
			particle.y += (-sinTable[a] * distance);
		}
		if (hscatter != 0)
			particle.x += (hscatter * (Math.random()-0.5f));
		if (vscatter != 0)
			particle.y += (vscatter * (Math.random()-0.5f));

		//particle.vx = ((cosTable[a] * s) >> 8);
		//particle.vy = -((sinTable[a] * s) >> 8);
	}
	
	public void update() {
		for (int i = 0; i < particles.length; i++) {
			p = particles[i];
			if (p.lifetime < 0) {
				newParticle(p);
			}
			p.move(width, height);		
			p.color -= (p.decay << 24);
			if (p.size < 5.0f) {
				p.size += .12f;
			}
			if (Color.alpha(p.color) <= 0)
				p.color = Color.TRANSPARENT;
		}
	}
	
	public String toString() {
		return "Particles";
	}
}