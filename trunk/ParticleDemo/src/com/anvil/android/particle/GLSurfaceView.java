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

import android.content.Context;
import android.graphics.OpenGLContext;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.Map;
import java.util.Random;
import javax.microedition.khronos.opengles.GL10;

/**
 * An implementation of SurfaceView that uses the dedicated surface for
 * displaying an OpenGL animation.  This allows the animation to run in
 * a separate thread, without requiring that it be driven by the update
 * mechanism of the view hierarchy.
 */
class GLSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    private GLThread mGLThread;

    GLSurfaceView(Context context) {
        super(context);
        init();
    }

    public GLSurfaceView(Context context, AttributeSet attrs, Map inflateParams) {
        super(context, attrs, inflateParams);
        init();
    }
    
    private void init() {
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed 
        mHolder = getHolder();
        mHolder.addCallback(this);
    }
    
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, start our drawing thread.
        mGLThread = new GLThread();
        mGLThread.start();
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

    class GLThread extends Thread {
    	private TextView fpsView;
    	private boolean mDone;
        private int mWidth;
        private int mHeight;
 
        private StopWatch watch;
    	private boolean watchEn; 
    	Random rand;
    	
        private int numEmitters;
        private ParticleEmitter[] emitters;
        
        GLThread() {
        	super();
        	mDone = false;
        	mWidth = 0;
        	mHeight = 0;

        	fpsView = (TextView)findViewById(R.id.fpsmeter);
        	
        	rand = new Random();
        	numEmitters = 1;
        	emitters = new ParticleEmitter[numEmitters];
	        for (int i = 0; i < numEmitters; i++)
	        {
	        	emitters[i] = new ParticleEmitter(
	        			30,
	        			480.0f*0.1f,
	        			320.0f*0.1f
	        	);
	        }
	        
        	watch = new StopWatch();
        	watchEn = false;	
        }
          
        @Override
        public void run() {
            /* 
             * Create an OpenGL|ES context. This must be done only once, an
             * OpenGL context is a somewhat heavy object.
             */
            OpenGLContext glc = new OpenGLContext( OpenGLContext.DEPTH_BUFFER );
            
            /*
             * Before we can issue GL commands, we need to make sure 
             * the context is current and bound to a surface.
             */
            SurfaceHolder holder = mHolder;
            glc.makeCurrent(holder);
            
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
             gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);


            // This is our main acquisition thread's loop, we go until
            // asked to quit.
            while (!mDone) {
                // Update the asynchronous state (window size, key events)
                int w, h;
                synchronized(this) {
                    w = mWidth;
                    h = mHeight;
                }

                /* draw a frame here */
                drawFrame(gl, w, h);

                /*
                 * Once we're done with GL, we need to call post()
                 */
                glc.post();
            }
            
            glc.makeCurrent((SurfaceHolder)null);
        }
        
        private void drawFrame(GL10 gl, int w, int h) {
            gl.glViewport(0, 0, w, h);
        
            /*
             * Set our projection matrix. This doesn't have to be done
             * each time we draw, but usualy a new projection needs to be set
             * when the viewport is resized.
             */

            //float ratio = (float)w / h;
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
        	gl.glOrthof(0.0f, 480.0f, 0.0f, 320.0f, -1.0f, 1.0f);
            //gl.glFrustumf(-ratio, ratio, -1, 1, 2, 12);

            /*
             * By default, OpenGL enables features that improve quality
             * but reduce performance. One might want to tweak that
             * especially on software renderer.
             */
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
        	gl.glDisable(GL10.GL_DITHER);
            gl.glActiveTexture(GL10.GL_TEXTURE0);
            gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

            /*
             * Usually, the first thing one might want to do is to clear
             * the screen. The most efficient way of doing this is to use
             * glClear(). However we must make sure to set the scissor
             * correctly first. The scissor is always specified in window
             * coordinates:
             */
            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            
            
            /* Draw Particle Emitters */
	        for (int i = 0; i < emitters.length; i++)
	        { 
	        	emitters[i].update();
	        	emitters[i].draw(gl);

	        	emitters[i].x += 0.5f;
	        	emitters[i].y += 0.5f;
	        }
            
	        /* Stopwatch */
	        if (!watchEn) {
	        	watch.start();
	        	watchEn = true;
	        }
	        else {
	        	watch.stop();
	        	float fps = 1.0f / (float)(watch.getElapsedTime());
	        	//fpsView.setText("Test");
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

