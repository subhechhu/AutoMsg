package com.subhechhu.automessage;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.subhechhu.automessage.Info.InfoActivity;
import com.subhechhu.automessage.message.MainListActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    ProgressBar loadingBar;
    SharedPrefUtil sharedPrefUtil;
    boolean newApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        loadingBar = (ProgressBar) findViewById(R.id.progressBar_splash);
//        loadingBar.getIndeterminateDrawable()
//                .setColorFilter(ContextCompat.getColor(this, R.color.mainColor), PorterDuff.Mode.SRC_IN);
        loadingBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#002a41"),
                android.graphics.PorterDuff.Mode.MULTIPLY);
        sharedPrefUtil = new SharedPrefUtil(); //shared preference object to store boolean value from NewApp() function below.
        newApp = NewApp(); //check if the app is new or old inorder to redirect either to login activity or help fragments.
        Log.e("subhechhu", "newApp: " + newApp);
        if (newApp) {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, InfoActivity.class));
                    finish(); // Kills the SplashACtivity
                }
            }, 2500);
        } else {
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(MainActivity.this, MainListActivity.class));
                    finish(); // Kills the SplashACtivity
                }
            }, 2500);
        }
    }

    public boolean NewApp() {
        return sharedPrefUtil.getSharedPreferenceBoolean(AppController.getContext(), "newApp", true);
    }
}
