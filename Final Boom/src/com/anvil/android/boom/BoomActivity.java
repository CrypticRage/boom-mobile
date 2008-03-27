package com.anvil.android.boom;

import com.anvil.android.boom.BoomView;
import com.anvil.android.boom.GlobalData;
import com.anvil.android.boom.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class BoomActivity extends Activity {
	private TextView topStatusView, bottomStatusView;
	private BoomView mPreview;
    
	// Need handler for callbacks to the UI thread
	private volatile Handler mHandler = new Handler();

    // Create runnable for posting
    final private Runnable mTopUpdate = new Runnable() {
        public void run() {
        	updateTopStatus();
        }
    };
    
    // Create runnable for posting
    final private Runnable mBottomUpdate = new Runnable() {
        public void run() {
        	updateBottomStatus();
        }
    };
    
    @Override
	protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        // remove window title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NO_STATUS_BAR,
        		WindowManager.LayoutParams.FLAG_NO_STATUS_BAR); 
    
        loadSprites();
        
        setContentView(R.layout.canvas);
        mPreview = (BoomView)this.findViewById(R.id.main_canvas);
        topStatusView = (TextView)this.findViewById(R.id.top_status_bar);
        bottomStatusView = (TextView)this.findViewById(R.id.bottom_status_bar);
        updateTopStatus();
        updateBottomStatus();
    }
    
    @Override
	protected void onResume() {
        // Because the CameraDevice object is not a shared resource,
        // it's very important to release it when the activity is paused.
        super.onResume();
        mPreview.resume();
    }

    @Override
	protected void onPause() {
        // Start Preview again when we resume.
        super.onPause();
        mPreview.pause();
    }
          
    private void updateTopStatus() {
    	//fpsView.setText((1000000.0f / GlobalData.frameTimeMicro) + " fps");
    	topStatusView.setText((1000000.0f / GlobalData.frameTimeMicro) + " fps\n" + 
    			GlobalData.overTimeMicro + " us \n" +
    			"overCount: " + GlobalData.overCount);
    	mHandler.postDelayed(mTopUpdate, 1000);
    }
     
    private void updateBottomStatus() {
    	bottomStatusView.setText(GlobalData.bottomStatusText);
    	mHandler.postDelayed(mBottomUpdate, 1000);
    }   
    
    private void loadSprites() {
    	GlobalData.sprites = new Bitmap[3];
    	GlobalData.sprites[0] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.cloud_tiny);
    	GlobalData.sprites[1] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.green_rocket); 	
    	GlobalData.sprites[2] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.blast); 
    	
    	GlobalData.background = BitmapFactory.decodeResource(getResources(),
    			R.drawable.sundown);
    }
}