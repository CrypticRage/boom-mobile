package org.soadl.TotoroBoom;

import org.soadl.TotoroBoom.TotoroBoomView;
import org.soadl.TotoroBoom.R;

import android.app.Activity;
import android.os.Bundle;

public class TotoroBoomActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);
        TotoroBoomView myView = (TotoroBoomView) findViewById(R.id.totoroboom);
        
    }
}