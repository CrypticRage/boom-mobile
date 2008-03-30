package com.anvil.android.boom.particles;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Color;

//import com.anvil.android.util.*;

public class BlastEmitter2D extends ParticleEmitter2D {
	public int status;
	public static final int DEAD = 0;
	public static final int ALIVE = 1;
	
	public int color = Color.RED;
	
	private float maxRadius = 0.0f;
	private float radius = 0.0f;
	private float radVelocity = 0.0f;
	
	public BlastEmitter2D(float x, float y, float radVel, float maxRad, Bitmap sprite) {
		super(1, x, y);

		SpriteParticle2D p = new SpriteParticle2D(x, y, sprite);
		p.sprite.setScale(0.25f);
		newParticle(p);
		livePool.add(p);
		liveCount = 1;
		status = ALIVE;
		
		maxRadius = maxRad;
		radVelocity = radVel;
		
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
			
			/*
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
			*/
			
			radius += (time*0.000001f)*radVelocity;
			if (radius >= maxRadius) {
				radius = maxRadius;
			}
		}
	}
	
	public void draw(Canvas canvas) {
		if (status == ALIVE) {
			canvas.save();
			paint.setColor(color);
			paint.setAlpha(125);
			SpriteParticle2D p = (SpriteParticle2D)livePool.get(0);
			//SpriteInstance tempSprite = p.sprite;
			//canvas.translate(p.x, p.y);
			canvas.drawCircle(p.x, p.y, radius, paint);
			//canvas.rotate(0, this.x, this.y);
			//canvas.drawBitmap(tempSprite.sprite, null, tempSprite.getDrawBox(), this.paint);
			canvas.restore();			
		}
	}
}