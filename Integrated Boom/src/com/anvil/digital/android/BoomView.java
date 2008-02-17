package com.anvil.digital.android;

import java.util.Map;
import java.util.Random;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import com.anvil.digital.android.logic.*;
import com.anvil.digital.android.graphics.*;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.OpenGLContext;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class BoomView extends SurfaceView implements SurfaceHolder.Callback {

	private Vector<GameMissile> mEnemyMissiles; //Enemy GameMissile objects
	private Vector<GameMissile> mFriendsList; //Friendly GameMissile objects

	
    private GLThread mGLThread;
    SurfaceHolder mHolder;
	
	
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

//        	fpsView = (TextView)findViewById(R.id.fpsmeter);
        	
        	rand = new Random();
        	numEmitters = 1;
        	emitters = new ParticleEmitter[numEmitters];
	        
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
             
			GameMissile m1 = new GameMissileNormal(30);
			m1.setCurrentPos(new PointF(80, 60));
			m1.setStartingPos (new PointF (80, 60));
			m1.setVelocity (5);
			m1.setTargetPos (new PointF (240, 160));
			m1.setState (GameObject.STATE_ALIVE);
			
        	emitters[0] = new ParticleEmitter(
        			30,
        			80f,
        			60f
        	);
			
			MotionSolver ms1 = new LineSolver ();
			 
			m1.setMotionSolver(ms1);
			 
			mEnemyMissiles = new Vector<GameMissile> ();
			mFriendsList = new Vector<GameMissile> ();
			 
			mEnemyMissiles.add(m1);


            // This is our main acquisition thread's loop, we go until
            // asked to quit.
            while (!mDone) {
                // Update the asynchronous state (window size, key events)
                int w, h;
                synchronized(this) {
                    w = mWidth;
                    h = mHeight;
                }

                updateProjectiles(emitters);
                
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
    } //End of GLThread class
    
    public BoomView (Context context) {
        super(context);
        init();
    }

    public BoomView (Context context, AttributeSet attrs, Map inflateParams) {
        super(context, attrs, inflateParams);
        init();
    }

//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//		
//		
//		GameBase gb1 = new GameBase(210, 150, 240, 180);
//		gb1.draw(canvas);
//		
//		GameBase m1Target = new GameBase (59, 29, 61, 31);
//		m1Target.draw (canvas);
//
//        Paint fpsPaint = new Paint();
//        fpsPaint.setAntiAlias(true);
//        
//        invalidate();
//	}
	
	private void updateProjectiles(ParticleEmitter[] emitters)
	{
		for (int i = 0; i < mEnemyMissiles.size (); i++)
		{
			GameObject o = mEnemyMissiles.get (i);
			MotionSolver ms = o.getMotionSolver ();
			ExplosionUpdater eu = o.getExplosionUpdater ();
			ParticleEmitter emitter = emitters [i];
			
			//Update positions and explosions
			switch (o.getState ())
			{
				case GameObject.STATE_LARVAL:
				case GameObject.STATE_ALIVE:
					if (ms != null)
					{
						ms.solveMotion (o);
						PointF currentPos = o.getCurrentPos ();
						
						emitter.x = currentPos.x;
						emitter.y = currentPos.y;
					}
					
					if (o.getState () == GameObject.STATE_DYING)
					{
						o.createExplosion ();
						
						//Remove emitter from emitters
					}
					break;
					
				case GameObject.STATE_DYING:
					if (eu != null)
					{
						eu.updateExplosion (o.getExplosion ());
					}
					break;
					
				case GameObject.STATE_DEAD:
					//Verify each child is dead as well
					Vector<GameObject> children = o.getChildren ();
					
					//If an object has no children, just remove it
					if (children != null)
					{
						boolean cleanUp = true;
						
						for (int j = 0; j < children.size (); j++)
						{
							GameObject child = children.get (j);
							
							if (child.getState () == GameObject.STATE_DEAD)
							{
								children.remove (child);
								j--;
							}
							else
							{
								cleanUp = false;
							}
						}
						
						if (cleanUp)
						{
							mEnemyMissiles.remove (o);
							i--;
						}
					}
					else
					{
						mEnemyMissiles.remove (o);
						i--;
					}
					break;
			}
			
			//Collision detection
			
		}
	}
	
	//TODO: Need to figure out all objects affected...
	//maybe missiles and GameBase types separately?
//	private Vector<GameMissile> getMissilesInRange (GameMissile explodingMissile,
//												    Vector<GameMissile> missileList)
//	{
//	}
//	
//	private Vector<GameBase> getBasesInRange (GameMissile explodingMissile,
//											  Vector<GameBase> baseList)
//	{
//	}
	
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
} //End of class TotoroBoomView
