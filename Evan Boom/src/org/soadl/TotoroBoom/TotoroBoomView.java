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

	private Vector<GameMissile> mEnemiesList;
	private double mLastDrawTime;
	private int mFramesDrawn;
	private String mFpsString;
	
	public TotoroBoomView(Context context, AttributeSet attrs, Map inflateParams) {
        super(context, attrs, inflateParams);
        
        GameMissile m1 = new GameMissile(6);
        m1.setCurrentPos(new PointF(80, 60));
        m1.setStartingPos (new PointF (80, 60));
        m1.setVelocity (5);
        m1.setTargetPos (new PointF (0, 120));
        m1.setState (GameObject.STATE_ALIVE);
       
        GameMissile m2 = new GameMissile(6);
        m2.setCurrentPos(new PointF(20,80));
        m2.setStartingPos (new PointF(20,80));
        m2.setVelocity (10);
        m2.setTargetPos (new PointF (200, 200));
        m2.setState (GameObject.STATE_ALIVE);
        
        MotionSolver ms1 = new LineSolver ();
        
        m1.setMotionSolver(ms1);
        m2.setMotionSolver(ms1);
        
        mEnemiesList = new Vector<GameMissile>();
        mEnemiesList.add(m1);
        mEnemiesList.add(m2);
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
        
        updateEnemies();
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
	
	protected void updateEnemies()
	{
		for(GameObject o: mEnemiesList)
		{
			o.getMotionSolver().solveMotion(o);
		}
	}
	protected void drawEnemies(Canvas canvas)
	{
		for(GameObject o : mEnemiesList)
		{
			o.draw (canvas);
		}	
	}
}
