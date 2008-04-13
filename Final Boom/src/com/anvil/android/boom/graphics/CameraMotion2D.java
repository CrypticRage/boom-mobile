package com.anvil.android.boom.graphics;

import android.graphics.PointF;
import android.util.Log;

import com.anvil.android.boom.logic.Physics;

public class CameraMotion2D {
	private Camera2D camera;
	private float maxHeight;
	
	private PointF currentPoint;
	private float currentRadius = 1.0f;	
	private PointF startPoint;
	private float startRadius = 1.0f;
	private PointF endPoint;
	private float endRadius = 1.0f;
	
	private float zoomVelocity = 0.0f;
	private PointF velocityVector;
	private float velocityScalar = 1.0f;
	
	public static final int IDLE = 0;
	public static final int MOVING = 1;
	public static final int ZOOM_IN = 2;
	public static final int ZOOM_OUT = 3;	
	public static final int MOVE_ZOOM_IN = 4;
	public static final int MOVE_ZOOM_OUT = 5;
	public int state = IDLE;
	
	public CameraMotion2D(Camera2D camera) {
		this.camera = camera;
		startPoint = new PointF(0.0f, 0.0f);
		endPoint = new PointF(0.0f, 0.0f);		
		currentPoint = new PointF(0.0f, 0.0f);
		velocityVector = new PointF(0.0f, 0.0f);
		maxHeight = (camera.getView()).height();
	}

	public void updateMotion(int elapsed) {
		if (state == MOVING) {
			move(elapsed);
		}
		else if (state == MOVE_ZOOM_IN || state == MOVE_ZOOM_OUT) {
			move(elapsed);
			zoom(elapsed);
		}
		else if (state == ZOOM_IN || state == ZOOM_OUT) {
			zoom(elapsed);
		}	
		else if (state == IDLE) {
			Log.w("CameraMotion2D", "Attempting to update motion in IDLE state.");
		}
	}
	
	private void move(int elapsed) {
		float scalar = 0.0f;
		scalar = velocityScalar * elapsed * Physics.INV_MICROSECONDS_PER_SECOND;
		float x = scalar * velocityVector.x;
		float y = scalar * velocityVector.y;	
		
		if (Physics.checkCrossing(startPoint, endPoint, currentPoint)) {
			if (state == MOVING) {
				state = IDLE;				
			}
			else if (state == MOVE_ZOOM_IN) {
				state = ZOOM_IN;
			}
			else if (state == MOVE_ZOOM_OUT) {
				state = ZOOM_OUT;
			}
			camera.moveTo(endPoint.x, endPoint.y);
		}
		
		else {
			camera.move(x, y);
		}
	
		currentPoint.x = camera.getCenterX();
		currentPoint.y = camera.getCenterY();
	}
	
	private void zoom(int elapsed) {
		float z = 0.0f;
		z = zoomVelocity * elapsed * Physics.INV_MICROSECONDS_PER_SECOND;
		//float cameraBottom = (camera.getView()).bottom;
		
		if ((state == ZOOM_IN) || (state == MOVE_ZOOM_IN)) {
			currentRadius -= z;
			if ((state == ZOOM_IN) && (currentRadius <= endRadius)) {
				state = IDLE;
				camera.zoom(endRadius);
			}
			else if ((state == MOVE_ZOOM_IN) && (currentRadius <= endRadius)) {
				state = MOVING;
				camera.zoom(endRadius);
			}
			else {
				camera.zoom(currentRadius);
			}
		
			/*
			if (cameraBottom >= maxHeight) {
				camera.move(0.0f, -(cameraBottom-maxHeight));
			}
			*/
		}
		
		if ((state == ZOOM_OUT) || (state == MOVE_ZOOM_OUT)) {
			currentRadius += z;
			if ((state == ZOOM_OUT) && (currentRadius >= endRadius)) {
				state = IDLE;
				camera.zoom(endRadius);
			}
			else if ((state == MOVE_ZOOM_OUT) && (currentRadius >= endRadius)) {
				state = MOVING;
				camera.zoom(endRadius);
			}
			else {
				camera.zoom(currentRadius);
			}
		}	
	}
	
	public void setMotion(float x, float y, float radius) {
		startPoint = camera.getCenter();
		startRadius = camera.getRadius();
		
		endPoint.x = x;
		endPoint.y = y;
		endRadius = radius;
		
		currentPoint = new PointF(startPoint.x, startPoint.y);
		currentRadius = startRadius;
		
		velocityVector = PointF.difference(endPoint, startPoint);
		velocityVector = Physics.normalize(velocityVector);
	}

	public void startMotion() {
		if (startRadius == endRadius) {
			state = MOVING;
		}
		else if ((startRadius > endRadius) && !startPoint.equals(endPoint.x, endPoint.y)) {
			state = MOVE_ZOOM_IN;
		}
		else if ((startRadius < endRadius) && !startPoint.equals(endPoint.x, endPoint.y)) {
			Log.i("CameraMotion2D", "MOVE_ZOOM_OUT motion started.");
			state = MOVE_ZOOM_OUT;
		}
		else if ((startRadius < endRadius) && startPoint.equals(endPoint.x, endPoint.y)) {
			state = ZOOM_OUT;
		}
		else if ((startRadius > endRadius) && startPoint.equals(endPoint.x, endPoint.y)) {
			state = ZOOM_IN;
		}
	}
	
	public void stopMotion() {
		state = IDLE;
	}
	
	public void setVelocityScalar(float vel) {
		velocityScalar = vel;
		velocityVector = Physics.scale(velocityVector, velocityScalar);
	}
	
	public void setZoomScalar(float vel) {
		zoomVelocity = vel;
	}
	
	public int getState() {
		return state;
	}
}