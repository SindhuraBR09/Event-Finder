package com.eventsearch.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_TIME_OUT = 2000; // After completion of 2000 ms, the next activity will get started.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            getWindow().setBackgroundDrawableResource(R.color.black);
            setContentView(R.layout.activity_main); // this will bind your MainActivity.class file with activity_main.
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                        },
                        PackageManager.PERMISSION_GRANTED);
            }
            else{
//                Log.d("Already have permission","");
//                Intent i = new Intent(MainActivity.this, HomeActivity.class);
//                startActivity(i);
//                finish();

                Handler h =new Handler() ;
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        MainActivity.this.finish();
                    }

                }, 2000);
            }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start the HomeActivity
//                getWindow().setBackgroundDrawableResource(R.color.black);
//                setContentView(R.layout.activity_main); // this will bind your MainActivity.class file with activity_main.
//                Handler h =new Handler() ;
//                h.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        MainActivity.this.finish();
//                    }
//
//                    }, 5000);

                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            } else {
                // Permission denied, do something appropriate
            }
        }
    }


}