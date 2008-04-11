package com.anvil.android.boom.graphics;

import android.graphics.Camera;
import android.graphics.RectF;
import android.graphics.PointF;
//import android.util.Log;

import com.anvil.android.boom.logic.Physics;

public class Camera2D extends Camera {
	private RectF view;
	
	private float aspectRatio = 1.0f;	
	private float viewWidth = 0.0f;
	private float viewLength = 0.0f;
	private float cameraHeight = 576.0f;
		
	private final float tanTheta = 0.27777777f;
	private final float invTanTheta = 1/tanTheta;
	
	public Camera2D(float length, float width) {
		super();
		viewWidth = width;
		viewLength = length;
		if (width > length) {
			aspectRatio = width/length;			
		}
		else if (width < length) {
			aspectRatio = length/width;			
		}

    	resetView();
		translate(-(viewLength/2), (viewWidth/2), 0.0f);
    	save();
	}
	
	public void reset() {
    	resetView();
		restore();
	}
	
	private void resetView() {
		view = new RectF();
    	view.left = 0.0f;
    	view.right = viewLength;
    	view.top = 0.0f;
    	view.bottom = viewWidth;		
	}
	
	public void move(float x, float y) {
		view.left += x;
		view.right += x;
		view.top += y;
		view.bottom += y;
		translate(-x, y, 0.0f);
	}
	
	public void moveTo(float x, float y) {
		float dispX = x-view.centerX();
		float dispY = y-view.centerY();
		view.left += dispX;
		view.right += dispX;
		view.top += dispY;
		view.bottom += dispY;
		translate(-dispX, dispY, 0.0f);
	}
	
	public void zoom(float radius) {
		float z = radius*invTanTheta;
		float y = 0.0f;
		float x = 0.0f;
		float cx = view.centerX();
		float cy = view.centerY();
		float distance = 0.0f;
		
		if (cameraHeight > z) {
			distance = cameraHeight - z;
		}
		else if (z > cameraHeight) {
			distance = z - cameraHeight;
		}
		
		//Log.i("Camera", "X: " + cx + " Y:" + cy);
		cameraHeight = z;
		y = tanTheta*cameraHeight;
		x = aspectRatio*y;
		view.left = cx-x;
		view.right = cx+x;
		view.top = cy-y;
		view.bottom = cy+y;		
		translate(0.0f, 0.0f, -distance);
	}

	public RectF getView() {
		return view;
	}
	
	public float getCenterX() {
		return view.centerX();
	}

	public float getCenterY() {
		return view.centerY();
	}

	public PointF getCenter() {
		return new PointF (view.centerX(), view.centerY());
	}
	
	public float getRadius() {
		float rad = Physics.calculateDistance(getCenter(), new PointF(view.centerX(), view.top));
		return rad;
	}
}