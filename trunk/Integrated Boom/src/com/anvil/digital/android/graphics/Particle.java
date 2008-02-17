package com.anvil.digital.android.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Particle {
	public float x;
	public float y;
	public float vx, vy;
	private float radius;
	public GLColor color;
	public int randomness = 0;
	public int lifetime = -1;
	public float decay = 0.0f;
	private int numSegments;
	private int stepSize;

	private float points[];
	private FloatBuffer fb;
    private ByteBuffer bb;
	
	protected ParticleEmitter emitter;

	public Particle(ParticleEmitter emitter, int numSegments) {
		this.emitter = emitter;
		this.numSegments = numSegments;
		this.color = new GLColor(0.0f, 0.0f, 0.0f);
        
		this.stepSize = (int)(SinTable.TABLE_SIZE/numSegments);
		this.points = new float[numSegments*3];	
    	bb = ByteBuffer.allocateDirect(points.length*4);
    	bb.order(ByteOrder.nativeOrder());
    	fb = bb.asFloatBuffer();	
        setRadius(this.radius);
	}
	
	public float getRadius() {
		return radius;
	}
	
	public void setRadius(float rad) {    	
    	radius = rad;
		for(int i = 0; i < numSegments; i++) {
    		points[i*3] = rad*CosTable.value[i*stepSize];
    		points[i*3+1] = rad*SinTable.value[i*stepSize];
    		points[i*3+2] = 0;
        }
    	fb.put(points);
    	fb.position(0);
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

	public void draw(GL10 gl) {
        gl.glLoadIdentity();
        gl.glTranslatef(x, y, 0);
        gl.glColor4f(this.color.red, this.color.blue, this.color.green, this.color.alpha);
        //gl.glColor4f(0.7f, 0.7f, 0.7f, 1.0f);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, fb);
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, numSegments); 
	}
}