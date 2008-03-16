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

package com.anvil.android.CanvasTest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

// ----------------------------------------------------------------------

public class CanvasTestActivity extends Activity {    
	private TextView fpsView;
	private CanvasView mPreview;
    
	// Need handler for callbacks to the UI thread
	final private Handler mHandler = new Handler();

    // Create runnable for posting
    final private Runnable mFPSUpdate = new Runnable() {
        public void run() {
        	updateFPS();
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
        
        setContentView(R.layout.boom);
        mPreview = (CanvasView)this.findViewById(R.id.maincanvas);
        fpsView = (TextView)this.findViewById(R.id.fpsmeter);
        updateFPS();
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
    
    private void updateFPS() {
    	//fpsView.setText((1000000.0f / GlobalData.frameTimeMicro) + " fps");
    	fpsView.setText((1000000.0f / GlobalData.frameTimeMicro) + " fps\n" + 
    			GlobalData.overTimeMicro + " us \n" +
    			"overCount: " + GlobalData.overCount);    	
    	mHandler.postDelayed(mFPSUpdate, 2000);
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