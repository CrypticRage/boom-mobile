package com.anvil.android.particle;

import com.anvil.android.particle.R;

import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.app.Activity;
import android.os.Bundle;
import android.content.Context;

import java.util.Random;
import java.lang.String;

public class ParticleDemo extends Activity {
	
    private ImageView mImageView;
    private SurfaceView mSurView;
    private SampleView mSampView;
    
    private TextView mTextView;
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        // No Title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);

        //mImageView = new ImageView(this);
        //mImageView.setImageResource(R.drawable.sunset_backdrop);

        //mSurView = new SurfaceView(this);
        //mSurView.setBackground(R.drawable.sunset_backdrop);
        
        mSampView = new SampleView(this);
        mSampView.setBackground(R.drawable.sunset_backdrop);
        
        mTextView = new TextView(this);
        mTextView.setText("Testing.");
        
        setContentView(mSampView);
    }

	private static class SampleView extends View {
	    private Paint mPaint;
	    private ParticleEmitter[] emitters;
	    private StopWatch watch;
	    private boolean watchEn = false;
	    private int frame;	    

	    private int testAlpha;
	    private int alphaCount;
	    
	    public SampleView(Context context) {
	        super(context);

	        watch = new StopWatch();
	        mPaint = new Paint();
	        Random rand = new Random();
	        frame = 0;
	        emitters = new ParticleEmitter[5];
	        for (int i = 0; i < emitters.length; i++)
	        {
	        	emitters[i] = new ParticleEmitter(
	        			60,
	        			rand.nextFloat() * 480,
	        			rand.nextFloat() * 100,
	        			10.0f,
	        			10.0f
	        	);
	        }       
	    
	        testAlpha = 0;
	    }
	    
	    @Override protected void onDraw(Canvas canvas) {
	    	super.onDraw(canvas);
	    	Paint paint = mPaint;
	
	        //canvas.drawColor(Color.WHITE);    
	        paint.setAntiAlias(false);
	        paint.setStrokeCap(Paint.Cap.ROUND);	
	        // ROUND cap + width > 0 ... Squares (pretty slow)
	        //paint.setStrokeWidth(3);
	        //canvas.drawPoints(mPts, paint);
	        //canvas.save();
	        
	        /*
	        for (int i = 0; i < emitters.length; i++)
	        {
	        	for (int j = 0; j < emitters[i].particles.length; j++)
	        	{
	        		paint.setColor(emitters[i].particles[j].color);
	        		canvas.drawCircle(
	        				emitters[i].particles[j].x,
	        				emitters[i].particles[j].y,
	        				emitters[i].particles[j].size,
	        				paint);
	        	}
	        	emitters[i].update();

	        	paint.setColor(Color.GREEN);
	        	canvas.drawCircle(emitters[i].x, emitters[i].y, 1.5f, paint);
	        	emitters[i].x += .6f;
	        	emitters[i].y += .6f;
	        }
	        */
	        
	        paint.setColor(Color.RED - (testAlpha << 24));        
	        canvas.drawCircle(155.0f, 155.0f, 5.5f, paint);
	        if (testAlpha < 255){
	        	testAlpha++;
	        }
	        else {
	        	testAlpha = 0;
	        	alphaCount++;       	
	        }
        	
	        paint.setColor(Color.RED);
	        String testString = "" + alphaCount;
	        canvas.drawText(testString, 10.0f, 25.0f, paint);
	     
	        if (!watchEn) {
	        	watch.start();
	        	watchEn = true;
	        }
	        else {
	        	watch.stop();
	        	watchEn = false;
		        testString = "" + watch.getElapsedTime();
		        paint.setColor(Color.BLUE);
		        canvas.drawText(testString, 450.0f, 20.0f, paint);
	        }
	        
	        invalidate();
	        //watch.stop();
	        //canvas.restore();	        
	        
	        // SQUARE cap + width > 0 ... Circles (very slow)
	        /*
	        paint.setColor(Color.GREEN);
	        paint.setStrokeWidth(6);
	        paint.setStrokeCap(Paint.Cap.ROUND);
	        canvas.drawPoints(mPts, paint);
			*/
	
	        // antialias + width == 0 ... blurry pixels (pretty fast)
	        /*
	        paint.setColor(Color.RED);
	        paint.setStrokeWidth(0);
	        canvas.drawPoints(mPts, paint);
	         */
	    
	        // no-antialias + width == 0 ... single pixels (very fast)
	        /*
	        paint.setColor(Color.BLACK);
	        paint.setAntiAlias(false);
	        canvas.drawPoints(mPts, paint);
			*/
	    }
	}
}


