package com.anvil.android.boom.particles;

import android.graphics.Canvas;
import android.graphics.Bitmap;

//import com.anvil.android.util.*;

public class BlastEmitter2D extends ParticleEmitter2D {
	public int status;
	public static final int DEAD = 0;
	public static final int ALIVE = 1;
	
	public BlastEmitter2D(float x, float y, Bitmap sprite) {
		super(1, x, y);

		SpriteParticle2D p = new SpriteParticle2D(x, y, sprite);
		newParticle(p);
		livePool.add(p);
		liveCount = 1;
		status = ALIVE;
		
		paint.setAntiAlias(true);
	}

	public void newParticle(Particle2D particle) {
		SpriteParticle2D p = (SpriteParticle2D)particle;
		p.lifetime = 750000;
		p.age = 0;
		p.decay = p.lifetime/256;
		p.alpha = 255;
		
	}
	
	public void update(int time) {		
		if (status == ALIVE) {
			SpriteParticle2D p = (SpriteParticle2D)livePool.get(0);
			
			p.currentDecay += time;
			if (p.currentDecay >= p.decay && (p.age >= p.lifetime/2)) {
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
				deadPool.add(livePool.remove(0)); 
				deadCount++;
				liveCount--;
				status = DEAD;
			}				
		}
	}
	
	public void draw(Canvas canvas) {
		if (status == ALIVE) {
			canvas.save();
			canvas.scale(0.5f, 0.5f);
			SpriteParticle2D p = (SpriteParticle2D)livePool.get(0);
			paint.setAlpha(p.alpha);
			canvas.rotate(0, this.x, this.y);
			//canvas.drawBitmap(p.sprite, null, p.spriteBox, this.paint);
			canvas.drawBitmap(p.sprite, p.x, p.y, this.paint);
			canvas.restore();			
		}
	}
}