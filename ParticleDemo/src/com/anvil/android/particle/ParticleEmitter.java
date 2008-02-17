package com.anvil.android.particle;

import javax.microedition.khronos.opengles.GL10;

class ParticleEmitter {
	public Particle[] particles;
	
	public float rate = 100;
	public int angle = 0;	// 0 degrees is north
	public float spread = 90;
	//public float gravity = (1 << 8);
	public int lifetime = 100;
	public float scatter = 4.5f;
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
	public GLColor color;
	public int numParticles;
	
	public ParticleEmitter(int numParticles, float x, float y) {
		this.numParticles = numParticles;
		this.x = x;
		this.y = y;
		this.color = new GLColor(1.0f, 0.0f, 0.0f);
		
		particles = new Particle[numParticles];
		for (int i = 0; i < numParticles; i++) {
			particles[i] = new Particle(this, 10);
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
		particle.color.red = 0.7f;
		particle.color.green = 0.7f;
		particle.color.blue = 0.7f;
		particle.color.alpha = 1.0f;
		//particle.lifetime = this.lifetime;
		particle.lifetime = (int)(Math.random()*this.lifetime) + 1;
		particle.decay = 1.0f / (float)particle.lifetime;
		particle.x = x;
		particle.y = y;
		particle.setRadius(3.0f);
		//particle.randomness = randomness;
		//particle.size = size;
		
		if (scatter != 0) {
			int a = ((int)(Math.random() * SinTable.TABLE_SIZE)) % SinTable.TABLE_SIZE;
			double distance = scatter * Math.random();
			particle.x += (CosTable.value[a] * distance);
			particle.y += (-SinTable.value[a] * distance);
		}
		if (hscatter != 0)
			particle.x += (hscatter * (Math.random()-0.5f));
		if (vscatter != 0)
			particle.y += (vscatter * (Math.random()-0.5f));

		//particle.vx = ((cosTable[a] * s) >> 8);
		//particle.vy = -((sinTable[a] * s) >> 8);
	}
	
	public void update() {
		for (int i = 0; i < numParticles; i++) {
			Particle p = particles[i];
			float rad = p.getRadius();
			
			if (p.lifetime < 0) {
				newParticle(p);
			}
			p.move(width, height);		
			p.color.alpha -= p.decay;
			if (rad < 5.0f) {
				p.setRadius(rad + .02f);
			}
			if (p.color.alpha <= 0.0f)
				p.color.alpha = 0.0f;
		}
	}
	
	public void draw(GL10 gl) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        //gl.glLoadIdentity();
        //gl.glTranslatef(x, y, 0);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        
        for (int i = 0; i < numParticles; i++) {
        	particles[i].draw(gl);
        }
	}
	
	public String toString() {
		return "Particles";
	}
}