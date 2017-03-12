package com.subhechhu.automessage;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by User on 28-Sep-16.
 */

public class DialogClass extends AppCompatActivity {
    TextView textView, textViewNumber;
    Button proceed;
    String sourceString, sourceStringNumber;

    String id;
    String mediumText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_remainder);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        textView = (TextView) findViewById(R.id.text_notify);
        textViewNumber = (TextView) findViewById(R.id.text_notify_number);
        proceed = (Button) findViewById(R.id.button_proceed);

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        final String message = intent.getStringExtra("message");
        final String medium = intent.getStringExtra("medium");

        id=intent.getStringExtra("id");

        if(id.equals("No network")){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml("Message Sending Failed", Html.FROM_HTML_MODE_LEGACY));
                textViewNumber.setText(Html.fromHtml("Message could not be sent due to issue in the Network. Please check the network and try again", Html.FROM_HTML_MODE_LEGACY));
            } else {
                textView.setText(Html.fromHtml("Message Sending Failed"));
                textViewNumber.setText(Html.fromHtml("Message could not be sent due to issue in the Network. Please check the network and try again"));
            }
            proceed.setText("Ok");
            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

        }else{
            try {
                sourceString = medium + " Messsage Remainder" + "<br><br><b>" + name + "</b>";
                sourceStringNumber = "\n<b>Message: </b> \n" + message;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    textView.setText(Html.fromHtml(sourceString, Html.FROM_HTML_MODE_LEGACY));
                    textViewNumber.setText(Html.fromHtml(sourceStringNumber, Html.FROM_HTML_MODE_LEGACY));
                } else {
                    textView.setText(Html.fromHtml(sourceString));
                    textViewNumber.setText(Html.fromHtml(sourceStringNumber));
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }

            proceed.setText(R.string.proceed);
            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(medium.equalsIgnoreCase("Whatsapp")){
                        whatsappIntent(message);
                    }else if(medium.equalsIgnoreCase("Viber")) {
                        viberIntent(message);
                    }
                }
            });
        }
    }

    private void viberIntent(String message) {
        Intent vIntent = new Intent(Intent.ACTION_SEND);
        vIntent.setPackage("com.viber.voip");
        vIntent.setType("text/plain");
        vIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(vIntent);
        finish();

    }

    private void whatsappIntent(String message) {
        PackageManager pm = getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            waIntent.setPackage("com.whatsapp");
            waIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(waIntent, "Share with"));
            finish();
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
        }
    }
}
