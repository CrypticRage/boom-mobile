package org.soadl.TotoroBoom;

import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

public class TotoroBoomView extends View {

	private Vector<GameMissile> mEnemyMissiles; //Enemy GameMissile objects
	private Vector<GameMissile> mFriendsList; //Friendly GameMissile objects
	
	private double mLastDrawTime;
	private int mFramesDrawn;
	private String mFpsString;
	
	public TotoroBoomView(Context context, AttributeSet attrs, Map inflateParams) {
        super(context, attrs, inflateParams);
        
        GameMissile m1 = new GameMissileNormal(30);
        m1.setCurrentPos(new PointF(80, 60));
        m1.setStartingPos (new PointF (80, 60));
        m1.setVelocity (5);
        m1.setTargetPos (new PointF (0, 120));
        m1.setState (GameObject.STATE_ALIVE);
       
        GameMissile m2 = new GameMissileNormal(30);
        m2.setCurrentPos(new PointF(20,80));
        m2.setStartingPos (new PointF(20,80));
        m2.setVelocity (10);
        m2.setTargetPos (new PointF (200, 200));
        m2.setState (GameObject.STATE_ALIVE);
        
        MotionSolver ms1 = new LineSolver ();
        
        m1.setMotionSolver(ms1);
        m2.setMotionSolver(ms1);
        
        mEnemyMissiles = new Vector<GameMissile> ();
        mFriendsList = new Vector<GameMissile> ();
        
        mEnemyMissiles.add(m1);
        mEnemyMissiles.add(m2);
        mLastDrawTime = System.currentTimeMillis();
        mFramesDrawn = 0;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		
		GameBase gb1 = new GameBase(210, 150, 240, 180);
		gb1.draw(canvas);
		
		GameBase m1Target = new GameBase (59, 29, 61, 31);
		m1Target.draw (canvas);

        Paint fpsPaint = new Paint();
        fpsPaint.setAntiAlias(true);
        
        updateProjectiles();
        drawEnemies(canvas);
        
        double now = System.currentTimeMillis();
        
        if((mFramesDrawn % 1) == 0)
        {
        	double fps = 1000/(now - mLastDrawTime);
        	mFpsString = String.format("%3.3f fps %d frames", new Double(fps), new Integer(mFramesDrawn));

            fpsPaint.setARGB(255, 0, 0, 0);
            
        }
        canvas.drawText(mFpsString, 10, 10, fpsPaint);
        mLastDrawTime = now;
		mFramesDrawn++;    
        invalidate();
        
	}
	
	private void updateProjectiles()
	{
		for (int i = 0; i < mEnemyMissiles.size (); i++)
		{
			GameObject o = mEnemyMissiles.get (i);
			MotionSolver ms = o.getMotionSolver ();
			ExplosionUpdater eu = o.getExplosionUpdater ();
			
			//Update positions and explosions
			switch (o.mState)
			{
				case GameObject.STATE_LARVAL:
				case GameObject.STATE_ALIVE:
					if (ms != null)
					{
						ms.solveMotion (o);
					}
					break;
					
				case GameObject.STATE_DYING:
					if (eu != null)
					{
						eu.updateExplosion (o.mExplosion);
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
	
	private void drawEnemies(Canvas canvas)
	{
		for(GameObject o : mEnemyMissiles)
		{
			o.draw (canvas);
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
} //End of class TotoroBoomView
