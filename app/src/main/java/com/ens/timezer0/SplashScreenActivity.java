package com.ens.timezer0;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.ens.timezer0.utils.SharedPref;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int time = 1000;
    ProgressBar mProgressBar ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mProgressBar = (ProgressBar) findViewById(R.id.splashprogrssbar);
        new BackgroundSplashTask().execute();
    }


        private class BackgroundSplashTask extends AsyncTask {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                mProgressBar.setVisibility(View.GONE);
                CheckSession();
                finish();
            }


        }

        private void CheckSession () {
            Boolean CheckSessionb = Boolean.valueOf(SharedPref.readSharedSetting(SplashScreenActivity.this,"userconnect","False"));

            Intent intoLogin = new Intent(SplashScreenActivity.this,LoginActivity.class);
            Intent intoHome = new Intent (SplashScreenActivity.this,NavigActivity.class);

            if(CheckSessionb){
                startActivity(intoHome);
                finish();
            }
            else
            {
                startActivity(intoLogin);
                finish();
            }

        }

    }

