package com.subhechhu.automessage.message;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.subhechhu.automessage.AbtActivity;
import com.subhechhu.automessage.AppController;
import com.subhechhu.automessage.Details;
import com.subhechhu.automessage.R;
import com.subhechhu.automessage.SharedPrefUtil;

import java.util.ArrayList;
import java.util.List;

public class MainListActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();
    final int NEW_MESSAGE = 1;

    TextView textView_noMessage;
    List<Details> detailOfRemainder;
    RecyclerView recyclerView;
    FloatingActionButton fab;

    CustomAdapter customAdapter;
    DBhelper dbHelper;

    SharedPrefUtil sharedPrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView_noMessage = (TextView) findViewById(R.id.textView_noRemainderFound);
        fab = (FloatingActionButton) findViewById(R.id.fab_add);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_main);

        detailOfRemainder = new ArrayList<>();

        sharedPrefUtil = new SharedPrefUtil();
        sharedPrefUtil.setSharedPreferenceInt(AppController.getContext(), "notificationCount", 0);

        if (dbHelper == null) {
            dbHelper = new DBhelper(AppController.getContext());
        }
        FetchAllMessage();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseMedium();
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fab.isShown()) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }

    private void FetchAllMessage() {
        try {
            Cursor cursor = dbHelper.getAllRemainders();

            Log.e("devsubhechhu","all remainder: "+cursor.getCount());
//            for (int i = 0; i < cursor.getColumnCount(); i++) {
//                Log.e(TAG, "buhahahaha " + cursor.getColumnName(i));
//                Log.e(TAG, "buhahahaha name: " + cursor.getString(i));
//            }
            if (cursor.moveToLast()) {
                do {
                    Details mydetail = new Details();
                    mydetail.setId(cursor.getString(0));
                    mydetail.setName(cursor.getString(1));
                    mydetail.setNumber(cursor.getString(2));
                    mydetail.setDate(cursor.getString(3));
                    mydetail.setTime(cursor.getString(4));
                    mydetail.setMessage(cursor.getString(5));
                    mydetail.setMediumSelected(cursor.getString(6));
                    detailOfRemainder.add(mydetail);
                } while (cursor.moveToPrevious());
                textView_noMessage.setVisibility(View.GONE);

                if (customAdapter == null) {
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainListActivity.this);
                    customAdapter = new CustomAdapter(MainListActivity.this, detailOfRemainder);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();
                } else {
                    customAdapter.notifyDataSetChanged();
                }
            } else {
                textView_noMessage.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ChooseMedium() {
        Intent intent = new Intent(MainListActivity.this, MessageActivity.class);
        startActivityForResult(intent, NEW_MESSAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == NEW_MESSAGE) {
                String name = data.getStringExtra("name");
                String number = data.getStringExtra("number");
                String date = data.getStringExtra("dateTV");
                String time = data.getStringExtra("timeTV");
                String message = data.getStringExtra("messageTV");
                String id = data.getStringExtra("id");
                String medium = data.getStringExtra("medium");
                String longTime = data.getStringExtra("longTime");

                Details details = new Details();
                details.setId(id);
                details.setName(name);
                details.setNumber(number);
                details.setDate(date);
                details.setTime(time);
                details.setMessage(message);
                details.setMediumSelected(medium);
                details.setTimelong(longTime);

                if (textView_noMessage != null) {
                    textView_noMessage.setVisibility(View.GONE);
                }
                detailOfRemainder.add(0, details);

                if (customAdapter == null) {
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainListActivity.this);
                    customAdapter = new CustomAdapter(MainListActivity.this, detailOfRemainder);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(customAdapter);
                    customAdapter.notifyDataSetChanged();
                } else {
                    customAdapter.notifyDataSetChanged();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void DeleteRow(String id, int position) {
        Log.e(TAG, "delete id: " + id);
        Log.e(TAG, "delete position: " + position);

        detailOfRemainder.remove(position);
        customAdapter.notifyDataSetChanged();

        Toast.makeText(MainListActivity.this, "Remainder Deleted", Toast.LENGTH_SHORT).show();

        if (detailOfRemainder.size() == 0) {
            if (textView_noMessage != null) {
                textView_noMessage.setVisibility(View.VISIBLE);
            }
        }

        UnsetRemainder(id, DeleteRemainder(id));
    }

    private void UnsetRemainder(String id, int delId) {
        if (delId == 1) {
            Intent intent = new Intent(MainListActivity.this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AppController.getContext(), Integer.parseInt(id), intent, 0);
            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
            pendingIntent.cancel();
        } else {
            Toast.makeText(MainListActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private int DeleteRemainder(String id) {
        return dbHelper.deleteRemainder(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent notificationActivity = new Intent(MainListActivity.this, AbtActivity.class);
            startActivity(notificationActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
