/* 
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.anvil.android.particle;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.OpenGLContext;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.microedition.khronos.opengles.GL10;

// ----------------------------------------------------------------------

public class GLParticleDemo extends Activity
{    
    @Override
    protected void onCreate(Bundle icicle)
    {
        super.onCreate(icicle);
        
        // Make sure to create a TRANSLUCENT window. This is recquired
        // for SurfaceView to work. Eventually this'll be done by
        // the system automatically.
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
    
        // We don't need a title either.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // Create our Preview view and set it as the content of our
        // Activity
        mGLSurfaceView = new GLSurfaceView(this);
        //mGLSurfaceView.setBackground(R.drawable.sunset_backdrop);        
        setContentView(mGLSurfaceView);
    }
    
    @Override
    protected boolean isFullscreenOpaque() {
        // Our main window is set to translucent, but we know that we will
        // fill it with opaque data. Tell the system that so it can perform
        // some important optimizations.
        return true;
    }

    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
    }

    private GLSurfaceView mGLSurfaceView;
}

// ----------------------------------------------------------------------

class GLSurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    SurfaceHolder		mHolder;
    private GLThread	mGLThread;
    private boolean		mHasSurface;
    //private Cube		mCube;
    private float		mAngle;
	
	GLSurfaceView(Context context) {
        super(context);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed 
        mHolder = getHolder();
        mHolder.setCallback(this);
    }

    public boolean surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, start our main acquisition thread.
        mGLThread = new GLThread();
        mGLThread.start();
        return true;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return
        mGLThread.requestExitAndWait();
        mGLThread = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Surface size or format has changed. This should not happen in this
        // example.
        mGLThread.onWindowResize(w, h);
    }

    // ----------------------------------------------------------------------

    class GLThread extends Thread
    {
    	StopWatch watch;
    	boolean watchEn;
        private boolean mDone;
        private int     mWidth;
        private int     mHeight;
        
        private float rad = .05f;
        private int numCircles = 100;
        private int numSegments = 20;
        private FloatBuffer circVert;
        private ByteBuffer fbb;
        private float circle[] = new float[numSegments*3];
        
    	GLThread() {
            super();
            mDone = false;
            mWidth = 0;
            mHeight = 0;
         
            watch = new StopWatch();
            watchEn = false;
 
        	for(int j=0; j < numSegments; j++) {
        		double angle = 2*Math.PI*j/numSegments;
        		circle[j*3] = rad*(float)Math.cos(angle);
        		circle[j*3+1] = rad*(float)Math.sin(angle);
        		circle[j*3+2] = 0;
        	}
        	fbb = ByteBuffer.allocateDirect(circle.length*4);
            fbb.order(ByteOrder.nativeOrder());
            circVert = fbb.asFloatBuffer();
        	circVert.put(circle);
        	circVert.position(0);
    	}
    
        @Override
        public void run() {
            /* 
             * Create an OpenGL|ES context. This must be done only once, an
             * OpenGL contex is a somewhat heavy object.
             */
            OpenGLContext glc = new OpenGLContext( OpenGLContext.DEPTH_BUFFER );

            /*
             * First, we need to get to the appropriate GL interface.
             * This is simply done by casting the GL context to either
             * GL10 or GL11.
             */
            GL10 gl = (GL10)(glc.getGL());

            /*
             * Some one-time OpenGL initialization can be made here
             * probably based on features of this particular context
             */
            gl.glHint(gl.GL_PERSPECTIVE_CORRECTION_HINT, gl.GL_FASTEST);
            
            // This is our main acquisition thread's loop, we go until
            // asked to quit.
            SurfaceHolder holder = mHolder;
            while (!mDone) {
                // Update the asynchronous state (window size, key events)
                int w, h;
                synchronized(this) {
                    w = mWidth;
                    h = mHeight;
                }

                // Lock the surface, this returns a Canvas that can
                // be used to render into.
                Canvas canvas = holder.lockCanvas();

                /*
                 * Before we can issue GL commands, we need to make sure all
                 * native drawing commands are completed. Simply call
                 * waitNative() to accomplish this. Once this is done, no native
                 * calls should be issued.
                 */
                glc.waitNative(canvas, null);


                /* draw a frame here */
                drawFrame(gl, w, h);

                /*
                 * Once we're done with GL, we need to flush all GL commands and
                 * make sure they complete before we can issue more native
                 * drawing commands. This is done by calling waitGL().
                 */
                glc.waitGL();

                // And finally unlock and post the surface.
                holder.unlockCanvasAndPost(canvas);
            }
        }
        
        private void drawFrame(GL10 gl, int w, int h) {
            gl.glViewport(0, 0, w, h);
        
            /*
             * Set our projection matrix. This doesn't have to be done
             * each time we draw, but usualy a new projection needs to be set
             * when the viewport is resized.
             */

            //float ratio = (float)w / h;
            gl.glMatrixMode(gl.GL_PROJECTION);
            gl.glLoadIdentity();
            //gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
        	gl.glOrthof(0.0f, 1.0f, 0.0f, 1.0f, -1.0f, 1.0f);
        	
            /*
             * By default, OpenGL enables features that improve quality
             * but reduce performance. One might want to tweak that
             * especially on software renderer.
             */
            gl.glDisable(gl.GL_DITHER);
            gl.glActiveTexture(gl.GL_TEXTURE0);
            gl.glBindTexture(gl.GL_TEXTURE_2D, 0);
            gl.glTexParameterx(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_S, gl.GL_CLAMP_TO_EDGE);
            gl.glTexParameterx(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_WRAP_T, gl.GL_CLAMP_TO_EDGE);
            gl.glTexParameterx(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MAG_FILTER, gl.GL_NEAREST);
            gl.glTexParameterx(gl.GL_TEXTURE_2D, gl.GL_TEXTURE_MIN_FILTER, gl.GL_NEAREST);
            gl.glTexEnvx(gl.GL_TEXTURE_ENV, gl.GL_TEXTURE_ENV_MODE, gl.GL_REPLACE);

            /*
             * Usually, the first thing one might want to do is to clear
             * the screen. The most efficient way of doing this is to use
             * glClear(). However we must make sure to set the scissor
             * correctly first. The scissor is always specified in window
             * coordinates:
             */
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
            gl.glColor4f(0.7f, 0.7f, 0.7f, 1.0f);
            gl.glVertexPointer(3, gl.GL_FLOAT, 0, circVert);
            gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
            
            gl.glTranslatef(0, .5f, 0);
            for(int i=0; i < numCircles; i++) {             
                gl.glDrawArrays(gl.GL_TRIANGLE_FAN, 0, numSegments);       
                gl.glTranslatef(.1f, 0, 0);
            }         
            
            /*
             * Now we're ready to draw some 3D object
             */
            //gl.glMatrixMode(gl.GL_MODELVIEW);
            //gl.glLoadIdentity();
            //gl.glTranslatef(0, 0, -3.0f);
            //gl.glRotatef(mAngle,        0, 1, 0);
            //gl.glRotatef(mAngle*0.25f,  1, 0, 0);
            

            //gl.glEnableClientState(gl.GL_COLOR_ARRAY);
            //gl.glEnable(gl.GL_CULL_FACE);
            //gl.glShadeModel(gl.GL_SMOOTH);
            //gl.glEnable(gl.GL_DEPTH_TEST);
            
            //mAngle += 1.2f;

	        if (!watchEn) {
	        	watch.start();
	        	watchEn = true;
	        }
	        else {
	        	watch.stop();
	        	long elapsed = watch.getElapsedTime();
	        	watchEn = false;
	        }
        }

        public void onWindowResize(int w, int h) {
            synchronized(this) {
                mWidth = w;
                mHeight = h;
            }
        }
        
        public void requestExitAndWait() {
            // don't call this from GLThread thread or it a guaranteed
            // deadlock!
            mDone = true;
            try {
                join();
            } catch (InterruptedException ex) { }
        }
        
    }

}
