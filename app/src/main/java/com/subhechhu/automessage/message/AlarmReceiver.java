package com.subhechhu.automessage.message;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

import com.subhechhu.automessage.AppController;
import com.subhechhu.automessage.DialogClass;
import com.subhechhu.automessage.R;
import com.subhechhu.automessage.SharedPrefUtil;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by subhechhu on 3/5/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    SharedPrefUtil sharedPrefUtil;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(getClass().getSimpleName(), "alarm receiver");

        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            Intent pushIntent = new Intent(context, TransparentActivity.class);
            context.startActivity(pushIntent);
        } else {
            String name = intent.getStringExtra("name");
            String number = intent.getStringExtra("number");
            String date = intent.getStringExtra("date");
            String time = intent.getStringExtra("time");
            String message = intent.getStringExtra("message");
            String id = intent.getStringExtra("id");
            String medium = intent.getStringExtra("medium");

            if (medium.equals("Messenger")) {
                MessengerAction(context, name, message, id, number);
            } else if (medium.equals("Whatsapp")) {
                WhatsappAction(context, name, message, id, medium);
            } else if (medium.equals("Viber")) {
                ViberAction(context, name, message, id, medium);
            }
        }
    }

    private void ViberAction(Context context, String name, String message, String id, String medium) {
        Intent intent1 = new Intent(context, DialogClass.class);
        intent1.putExtra("id", id);
        intent1.putExtra("name", name);
        intent1.putExtra("message", message);
        intent1.putExtra("medium", medium);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }

    private void WhatsappAction(Context context, String name, String message, String id, String medium) {
        Intent intent1 = new Intent(context, DialogClass.class);
        intent1.putExtra("id", id);
        intent1.putExtra("name", name);
        intent1.putExtra("message", message);
        intent1.putExtra("medium", medium);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }

    private void MessengerAction(Context context, String name, String message, String id, String number) {
        int notificationCount;
        sharedPrefUtil = new SharedPrefUtil();
        String messageTitle, messageText;

        notificationCount = sharedPrefUtil.getSharedPreferenceInt(AppController.getContext(), "notificationCount", 0);
        notificationCount = notificationCount + 1;

        sharedPrefUtil.setSharedPreferenceInt(AppController.getContext(), "notificationCount", notificationCount);

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);

        Intent intent2 = new Intent(context, MainListActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, Integer.parseInt(id), intent2, 0);

        if (notificationCount > 1) {
            messageTitle = notificationCount + " New Messages";
            messageText = "Last Message sent to " + name;
        } else {
            messageTitle = "Message sent to " + name;
            messageText = message;
        }

        Notification notification = new Notification.Builder(context)
                .setContentTitle(messageTitle)
                .setStyle(new Notification.BigTextStyle().bigText(messageText))
                .setSmallIcon(R.drawable.app_logo)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
