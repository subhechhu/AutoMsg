package com.subhechhu.automessage.message;

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
import android.view.View;
import android.widget.TextView;

import com.subhechhu.automessage.AppController;
import com.subhechhu.automessage.Details;
import com.subhechhu.automessage.R;

import java.util.ArrayList;
import java.util.List;

public class MainListActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();
    final int NEW_MESSAGE = 1;

    TextView textView_noMessage;
    List<Details> detailOfRemainder;
    RecyclerView recyclerView;

    CustomAdapter customAdapter;
    DBhelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textView_noMessage = (TextView) findViewById(R.id.textView_noRemainderFound);
        detailOfRemainder = new ArrayList<>();

        if (dbHelper == null) {
            dbHelper = new DBhelper(AppController.getContext());
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_main);
        FetchAllMessage();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseMedium();
            }
        });
    }

    private void FetchAllMessage() {
        try {
            Cursor cursor = dbHelper.getAllRemainders();
            Log.d(TAG, "detailOfRemainder: " + cursor.getCount());

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
                    customAdapter = new CustomAdapter(detailOfRemainder);
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
                    customAdapter = new CustomAdapter(detailOfRemainder);
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
}
