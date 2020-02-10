package com.kushal.qrparking;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kushal.qrparking.activities.LoginRegisterActivity;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class LoadingActivity extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_loading);

        Global.setTimeOut(SPLASH_DISPLAY_LENGTH, new Global.callbackRun() {
            @Override
            public void toRun() {
                Intent mainIntent = new Intent(getApplicationContext(), LoginRegisterActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }
}
