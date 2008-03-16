package com.anvil.android.particles;

import android.graphics.Canvas;
import android.graphics.Bitmap;

import com.anvil.android.util.*;

public class SmokeEmitter2D extends ParticleEmitter2D {
	private Bitmap smokeCloud;
	
	public SmokeEmitter2D(int numParticles, float x, float y, Bitmap sprite) {
		super(numParticles, x, y);
		smokeCloud = sprite;
		
		for (int i = 0; i < numParticles; i++) {
			Particle2D p = new SpriteParticle2D(x, y, smokeCloud);
			deadPool.add(p);
		}
		
		this.deadCount = numParticles;
		this.scatter = 10.0f;
		this.hscatter = 10.0f;
		this.vscatter = 10.0f;
		this.rate = 200000;
        
		paint.setAntiAlias(true);
	}

	public void newParticle(Particle2D particle) {
		SpriteParticle2D p = (SpriteParticle2D)particle;
		double rand = Math.random();
		float tempX = this.x;
		float tempY = this.y;
		
		p.lifetime = 3000000;
		p.age = 0;
		p.decay = p.lifetime/256;
		p.alpha = 255;
		//particle.randomness = randomness;
		//particle.size = size;
		
		if (scatter != 0.0f) {
			int a = ((int)(rand * SinTable.TABLE_SIZE)) % SinTable.TABLE_SIZE;
			double distance = scatter * rand;
			tempX += (CosTable.value[a] * distance);
			tempY += (-SinTable.value[a] * distance);
		}
		if (hscatter != 0)
			tempX += (hscatter * (rand-0.5f));
		if (vscatter != 0)
			tempY += (vscatter * (rand-0.5f));

		//particle.vx = ((cosTable[a] * s) >> 8);
		//particle.vy = -((sinTable[a] * s) >> 8);
		
		p.x = tempX;
		p.y = tempY;
	}
	
	public void update(long time) {		
		this.rateLife += time;
		
		if (this.rateLife >= this.rate) {
			if (deadCount > 0) {
				Particle2D p = deadPool.remove(--deadCount);
				newParticle(p);
				livePool.add(liveCount, p);
			}
			else {
				SpriteParticle2D p = new SpriteParticle2D(x, y, smokeCloud);
				newParticle(p);
				livePool.add(p);
			}
			liveCount++;
			this.rateLife -= this.rate;
		}
		
		for (int i = 0; i < liveCount; i++) {
			SpriteParticle2D p = (SpriteParticle2D)livePool.get(i);

			p.currentDecay += time;
			if (p.currentDecay >= p.decay) {
				int decayFactor = p.currentDecay / p.decay;
				p.alpha -= decayFactor;
				if (p.alpha < 0) {
					p.alpha = 0;
					p.currentDecay = 0;
				}
				else {
					p.currentDecay -= decayFactor * p.decay;
				}
			}
					
			p.age += time;
			if (p.age >= p.lifetime) {
				deadPool.add(livePool.remove(i)); 
				deadCount++;
				liveCount--;
			}
		}		
	}
	
	public void draw(Canvas canvas) {
		canvas.save();
		canvas.scale(0.25f, 0.25f);
        for (int i = 0; i < liveCount; i++) {
			SpriteParticle2D p = (SpriteParticle2D)livePool.get(i);
			paint.setAlpha(p.alpha);
			canvas.rotate(0, this.x, this.y);
			//canvas.drawBitmap(p.sprite, null, p.spriteBox, this.paint);
			canvas.drawBitmap(p.sprite, p.x, p.y, this.paint);
		}
		canvas.restore();
	}
}