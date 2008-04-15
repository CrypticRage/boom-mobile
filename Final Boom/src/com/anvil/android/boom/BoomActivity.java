package com.anvil.android.boom;

import com.anvil.android.boom.BoomView;
import com.anvil.android.boom.GlobalData;
import com.anvil.android.boom.graphics.SpriteData;
import com.anvil.android.boom.R;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    // Create runnable for posting the score
    final private Runnable mTopMiddleUpdate = new Runnable() {
        public void run() {
        	updateTopMiddleText();
        }
    };
    
    // Create runnable for posting the base health
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
        loadFont();
        
        updateTopRightText();
        updateTopMiddleText();
    }
    
    @Override
	protected void onResume() {
        GlobalData.gameScore = 0;
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
    	/* Debug Text */
    	/*
    	topRightTextView.setText((1000000.0f / GlobalData.frameTimeMicro) + " fps\n" + 
    			GlobalData.overTimeMicro + " us \n" +
    			"overCount: " + GlobalData.overCount);
    	*/
    	if (GlobalData.baseHealth <= 0)
    		topRightTextView.setText("Base Destroyed!");
    	else {
    		topRightTextView.setText("Base Health: " + GlobalData.baseHealth);
    	}
    	mHandler.postDelayed(mTopRightUpdate, 1000);
    	
    }
    
    private void updateTopMiddleText() {
    	topMiddleTextView.setText(GlobalData.scoreText);
    	mHandler.postDelayed(mTopMiddleUpdate, 2000);
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
    	SpriteData.sprites = new Bitmap[SpriteData.spriteCount];
    	SpriteData.sprites[SpriteData.SMOKE_CLOUD] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.smoke_cloud);
    	SpriteData.sprites[SpriteData.STD_MISSILE] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.std_missile);
    	SpriteData.sprites[SpriteData.SMART_BOMB] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.smart_bomb);
    	SpriteData.sprites[SpriteData.GOLD_MISSILE] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.gold_missile);
    	SpriteData.sprites[SpriteData.DOME_BASE] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.dome_base);
    	SpriteData.sprites[SpriteData.RED_CROSS] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.red_cross);
    	SpriteData.sprites[SpriteData.BLAST] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.blast);
    	SpriteData.sprites[SpriteData.INTRO_SCREEN] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.intro);
    	
    	SpriteData.bgSprites = new Bitmap[SpriteData.bgSpriteCount];
    	SpriteData.bgSprites[SpriteData.BG_GROUND] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.ground);
    	SpriteData.bgSprites[SpriteData.BG_MOUNTAINS] = BitmapFactory.decodeResource(getResources(),
    			R.drawable.mountains);
    }

    private void loadFont() {
    	GlobalData.textFont = Typeface.createFromAsset(getAssets(),
        	"fonts/blade_runner.TTF");
    }
}