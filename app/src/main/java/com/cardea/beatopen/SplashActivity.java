package com.cardea.beatopen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Thread() {
            public void run() {

                try {
                    Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                    Thread.sleep(2500);
                    startActivity(i);
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
