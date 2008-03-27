package com.anvil.android.boom;

import java.util.Map;

import com.anvil.android.boom.GlobalData;

import android.graphics.PointF;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.os.Handler;
import android.os.Message;

class BoomView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mHolder;
    private CanvasThread mPreviewThread;
    private boolean mHasSurface;
    
    protected StatusEventHandler mStatusEventHandler;
	
	private class StatusEventHandler extends Handler
	{
		public StatusEventHandler ()
		{
			super ();
		}
		
		public void handleMessage (Message msg)
		{
			//TODO: Extract StatusUpdate message type
			//and update the score or w/e
			
			msg.recycle ();
		}
	}
    
    public BoomView(Context context, AttributeSet attrs, Map inflateParams) {
        super(context, attrs, inflateParams);
        init();
    }
    
    public BoomView(Context context) {
        super(context);
        init();
    }
    
    private void init() {    
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        setFocusable(true);
    	mHolder = getHolder();
        mHolder.addCallback(this);
        mHasSurface = false;
        
        // In this example, we hardcode the size of the preview. In a real
        // application this should be more dynamic. This guarantees that
        // the underlying surface will never change size.
        mHolder.setFixedSize(480, 320);
    }

    public void resume() {
        // We do the actual acquisition in a separate thread. Create it now.
        if (mPreviewThread == null) {
            mPreviewThread = new CanvasThread(mHolder);
            // If we already have a surface, just start the thread now too.
            if (mHasSurface == true) {
                mPreviewThread.start();
            }
        }
    }
    
    public void pause() {
        // Stop Preview.
        if (mPreviewThread != null) {
            mPreviewThread.requestExitAndWait();
            mPreviewThread = null;
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, start our main acquisition thread.
        mHasSurface = true;
        if (mPreviewThread != null) {
            mPreviewThread.start();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return. Stop the preview.
        mHasSurface = false;
        pause();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Surface size or format has changed. This should not happen in this
        // example.
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent event) {
    	Handler handler = GlobalData.canvasThreadHandler;
    	PointF tempPoint = new PointF(event.getX(), event.getY());
    	Message msg = handler.obtainMessage (GlobalData.MOTION_EVENT_TYPE, tempPoint);
    	//Message msg = handler.obtainMessage (GlobalData.MOTION_EVENT_TYPE, event);
        msg.target = handler;
        
        handler.sendMessageAtFrontOfQueue(msg);
        //handler.sendMessage (msg);
        
        return true;
    }
}