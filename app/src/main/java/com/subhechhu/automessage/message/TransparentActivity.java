package com.subhechhu.automessage.message;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.subhechhu.automessage.R;
import com.subhechhu.automessage.message.DBhelper;

public class TransparentActivity extends Activity {

    DBhelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);
        Log.e("subhechhu", "transparent activity");
        startService(new Intent(this, RebootService.class));
        finish();
    }
}
