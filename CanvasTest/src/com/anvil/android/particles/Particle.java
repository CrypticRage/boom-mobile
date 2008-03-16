package com.anvil.android.particles;

public interface Particle {
	// Constant Declarations

	// Method Signatures

	// defines a method for updating the position
	// of a particle
	public void move(float width, float height);
	
	// defines how the particle will be drawn
	public void draw(Object surface);
}