package com.kw.sharaf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {
    private  static  final int  Delayed_time = 2150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

         new Handler().postDelayed(() -> {
             Intent intent = new Intent(Splash.this, MainActivity.class);
                     startActivity(intent);
                     finish();
         },Delayed_time
         );

    }
}