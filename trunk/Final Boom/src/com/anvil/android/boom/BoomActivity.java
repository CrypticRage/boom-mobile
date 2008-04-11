package com.anvil.android.boom;

import com.anvil.android.boom.BoomView;
import com.anvil.android.boom.GlobalData;
import com.anvil.android.boom.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Button;
import android.view.View;

public class BoomActivity extends Activity {
	private TextView topLeftTextView, topMiddleTextView, topRightTextView;
	private BoomView mPreview;
	private ImageButton[] buttons;
    
	// Need handler for callbacks to the UI thread
	private volatile Handler mHandler = new Handler();

    // Create runnable for posting
    final private Runnable mTopMiddleUpdate = new Runnable() {
        public void run() {
        	updateTopMiddleText();
        }
    };
    
    // Create runnable for posting
    final private Runnable mTopRightUpdate = new Runnable() {
        public void run() {
        	updateTopRightText();
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
    
        buttons = new ImageButton[2];
        
        setContentView(R.layout.boom);
        mPreview = (BoomView)this.findViewById(R.id.main_canvas);
        topMiddleTextView = (TextView)this.findViewById(R.id.top_middle);
        topRightTextView = (TextView)this.findViewById(R.id.top_right);
        topLeftTextView = (TextView)this.findViewById(R.id.top_left);
        
        createButtons();
        loadSprites();
        //loadFonts();     

        updateTopRightText();
        updateTopMiddleText();
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
          
    private void updateTopRightText() {
    	//fpsView.setText((1000000.0f / GlobalData.frameTimeMicro) + " fps");
    	topRightTextView.setText((1000000.0f / GlobalData.frameTimeMicro) + " fps\n" + 
    			GlobalData.overTimeMicro + " us \n" +
    			"overCount: " + GlobalData.overCount);
    	mHandler.postDelayed(mTopMiddleUpdate, 1000);
    }
     
    private void updateTopMiddleText() {
    	topMiddleTextView.setText(GlobalData.bottomStatusText);
    	mHandler.postDelayed(mTopRightUpdate, 2000);
    }   
    
    private void createButtons() {
        buttons[0] = (ImageButton)this.findViewById(R.id.button1);
        buttons[1] = (ImageButton)this.findViewById(R.id.button2);
        
        buttons[0].setBackgroundColor(Color.GRAY);
        buttons[1].setBackgroundColor(Color.TRANSPARENT);
        topLeftTextView.setText("Standard Missile");
        
        buttons[0].setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                GlobalData.AMMO_TYPE = GlobalData.STANDARD_MISSILE;
                topLeftTextView.setText("Standard Missile");
                v.setClickable(false);
                v.setBackgroundColor(Color.GRAY);
                buttons[1].setClickable(true);
                buttons[1].setBackgroundColor(Color.TRANSPARENT);
            }
        });
        
        buttons[1].setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                GlobalData.AMMO_TYPE = GlobalData.SMART_BOMB;
                topLeftTextView.setText("Smart Bomb");
                v.setClickable(false);
                v.setBackgroundColor(Color.GRAY);
                buttons[0].setClickable(true);
                buttons[0].setBackgroundColor(Color.TRANSPARENT);
            }
        }); 
    }
    
    private void loadSprites() {
    	GlobalData.sprites = new Bitmap[5];
    	GlobalData.sprites[0] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.cloud_tiny);
    	GlobalData.sprites[1] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.friendly_missile); 	
    	GlobalData.sprites[2] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.blast); 
    	GlobalData.sprites[3] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.base);
    	GlobalData.sprites[4] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.enemy_missile); 
    	
    	GlobalData.background = BitmapFactory.decodeResource(getResources(),
    			R.drawable.sundown);
    }
  
    private void loadFonts() {
    	Typeface tempType = Typeface.createFromAsset(getAssets(),
        	"fonts/sample_font.ttf");
    	topMiddleTextView.setTypeface(tempType);
    }
}