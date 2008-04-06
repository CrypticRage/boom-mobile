package com.anvil.android.boom.particles;

import android.graphics.Canvas;
import android.graphics.Bitmap;

import com.anvil.android.boom.util.*;

public class SmokeEmitter2D extends ParticleEmitter2D {
	private Bitmap smokeCloud;
	
	public SmokeEmitter2D(int numParticles, float x, float y, Bitmap sprite) {
		super(numParticles, x, y);
		smokeCloud = sprite;
		
		for (int i = 0; i < numParticles; i++) {
			SpriteParticle2D p = new SpriteParticle2D(x, y, smokeCloud);
			p.sprite.setScale(0.25f);
			deadPool.add(p);
		}
		
		this.deadCount = numParticles;
		this.scatter = 1.0f;
		this.hscatter = 3.0f;
		this.vscatter = 3.0f;
		this.rate = 250000;
        
		paint.setAntiAlias(true);
	}

	public void newParticle(Particle2D particle) {
		SpriteParticle2D p = (SpriteParticle2D)particle;
		double rand = Math.random();
		float tempX = this.x;
		float tempY = this.y;
		
		p.lifetime = 1500000;
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
		//p.sprite.setScale(0.15f);
	}
	
	public void update(int time) {		
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
        for (int i = 0; i < liveCount; i++) {
    		canvas.save();
        	SpriteParticle2D p = (SpriteParticle2D)livePool.get(i);
			SpriteInstance tempSprite = p.sprite;
			paint.setAlpha(p.alpha);
			canvas.translate(p.x, p.y);
			//canvas.rotate(0, this.x, this.y);
			canvas.drawBitmap(tempSprite.sprite, null, tempSprite.getDrawBox(), this.paint);
			canvas.restore();
        }
	}
}