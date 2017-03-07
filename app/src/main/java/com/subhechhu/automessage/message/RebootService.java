package com.subhechhu.automessage.message;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.subhechhu.automessage.AppController;
import com.subhechhu.automessage.Details;

import java.util.Calendar;

/**
 * Created by subhechhu on 3/7/2017.
 */

public class RebootService extends Service {
    DBhelper dbHelper;
    Details details;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("subhechhu", "onStartCommand");
        String longTime; //id, name, number, date, time, message, medium,
        long timeLong;


        final Calendar currentCalenderInstance = Calendar.getInstance(),
                savedCalenderInstance = Calendar.getInstance();

        if (dbHelper == null) {
            dbHelper = new DBhelper(AppController.getContext());
        }
        Cursor cursor = dbHelper.getAllRemainders();
        if (cursor.moveToLast()) {
            do {
                details = new Details();
                details.setId(cursor.getString(0));
                details.setName(cursor.getString(1));
                details.setNumber(cursor.getString(2));
                details.setDate(cursor.getString(3));
                details.setTime(cursor.getString(4));
                details.setMessage(cursor.getString(5));
                details.setMediumSelected(cursor.getString(6));
                details.setTimelong(cursor.getString(7));
                longTime = cursor.getString(7);
                timeLong = Long.parseLong(longTime);
                savedCalenderInstance.setTimeInMillis(timeLong);

                if (savedCalenderInstance.after(currentCalenderInstance)) {
                    SetRemainder(details);
                }
            } while (cursor.moveToPrevious());
            Log.e("devsubhechhu", "Before Killing");
            stopSelf();
        }
        return START_STICKY;
    }

    private void SetRemainder(Details details) {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("id", details.getId());
        alarmIntent.putExtra("name", details.getName());
        alarmIntent.putExtra("number", details.getNumber());
        alarmIntent.putExtra("date", details.getDate());
        alarmIntent.putExtra("time", details.getTime());
        alarmIntent.putExtra("message", details.getMessage());
        alarmIntent.putExtra("longDate", details.getTimelong());
        alarmIntent.putExtra("medium", details.getMediumSelected());
        long remainderTimeMills = Long.parseLong(details.getTimelong());

        Log.e("devsubhechhu", "Alarm set for id: " + details.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(AppController.getContext(), Integer.parseInt(details.getId()), alarmIntent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, remainderTimeMills, pendingIntent);
    }
}
