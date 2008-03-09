package com.anvil.digital.android;

import com.anvil.digital.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class BoomActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        // remove window title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NO_STATUS_BAR,
        		WindowManager.LayoutParams.FLAG_NO_STATUS_BAR); 
        
        setContentView(R.layout.main);
        
//        BoomView myView = (BoomView) findViewById(R.id.boom);
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
}