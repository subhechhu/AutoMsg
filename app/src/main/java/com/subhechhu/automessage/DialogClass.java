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

        try {
            sourceString = "Whatsapp Messsage Remainder" + "<br><br><b>" + name + "</b>";
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
                whatsappIntent(message);
            }
        });
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
