package com.subhechhu.automessage.Info;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.pixelcan.inkpageindicator.InkPageIndicator;
import com.subhechhu.automessage.AppController;
import com.subhechhu.automessage.R;
import com.subhechhu.automessage.SharedPrefUtil;
import com.subhechhu.automessage.login.LoginActivity;
import com.subhechhu.automessage.message.MainListActivity;

import java.util.ArrayList;
import java.util.List;

public class InfoActivity extends AppCompatActivity implements InfoFragment.OnSubmitListener {

    String TAG = getClass().getSimpleName();
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 999;
    private static final int SEND_SMS_PERMISSION_REQUEST = 888;

    ViewPagerAdapter pageAdapter;
    ViewPager viewPager;
    InkPageIndicator inkPageIndicator;
    View mainLayout;

    boolean contactPermission = false, smsPermission = false;
    SharedPrefUtil sharedPrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_info);
        mainLayout = findViewById(R.id.activity_info);

        sharedPrefUtil = new SharedPrefUtil();

        List<Fragment> fragments = getFragments(); //List of swipable fragments
        pageAdapter = new ViewPagerAdapter(getSupportFragmentManager(), fragments); //CustomPageAdapter to display the fragments
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(pageAdapter);

        inkPageIndicator = (InkPageIndicator) findViewById(R.id.indicator); // ViewPager Indicator object
        inkPageIndicator.setViewPager(viewPager);
    }

    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<>();
        fList.add(InfoFragment.newInstance(0, getString(R.string.firstPageText), R.drawable.splash1));
        fList.add(InfoFragment.newInstance(1, getString(R.string.secondPageText), R.drawable.splash2));
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            fList.add(InfoFragment.newInstance(2, getString(R.string.thirdPageText), R.drawable.splash3));
        } else {
            fList.add(InfoFragment.newInstance(2, getString(R.string.thirdPageTextSecondary), R.drawable.splash3));
        }
        return fList;
    }

    private void GetSMSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                    SEND_SMS_PERMISSION_REQUEST);
        } else {
            smsPermission = true;
            Toast.makeText(InfoActivity.this, "SMS Permission Already Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void SMSSnackBar(boolean isPermitted) {
        if (isPermitted) {
            Snackbar.make(mainLayout, getText(R.string.permission_granted), Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(mainLayout, getText(R.string.permission_request_sms), Snackbar.LENGTH_LONG)
                    .setAction(getText(R.string.permit), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GetSMSPermission();
                        }
                    }).show();
        }
    }

    private void GetContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        } else {
            contactPermission = true;
            Toast.makeText(InfoActivity.this, "Contact Permission Already Granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void ContactSnackBar(boolean isPermitted) {
        if (isPermitted) {
            Snackbar.make(mainLayout, getText(R.string.permission_granted), Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(mainLayout, getText(R.string.permission_request_contact), Snackbar.LENGTH_LONG)
                    .setAction(getText(R.string.permit), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GetContactPermission();
                        }
                    }).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                contactPermission = true;
                ContactSnackBar(true);
            } else {
                contactPermission = false;
                ContactSnackBar(false);
            }
        } else if (requestCode == SEND_SMS_PERMISSION_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                smsPermission = true;
                SMSSnackBar(true);
            } else {
                smsPermission = false;
                SMSSnackBar(false);
            }
        }
    }

    @Override
    public void setOnSubmitListener(String which) {
        switch (which) {
            case "contact":
                GetContactPermission();
                break;
            case "sms":
                GetSMSPermission();
                break;
            case "proceed":
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    sharedPrefUtil.setSharedPreferenceBoolean(AppController.getContext(), "newApp", false);
                    startActivity(new Intent(InfoActivity.this, MainListActivity.class));
                    finish();
                } else {
                    if (contactPermission && smsPermission) {
                        sharedPrefUtil.setSharedPreferenceBoolean(AppController.getContext(), "newApp", false);
                        startActivity(new Intent(InfoActivity.this, MainListActivity.class));
                        finish();
                    } else if (!contactPermission && !smsPermission) {
                        Toast.makeText(InfoActivity.this, "Please Provide Permissions To Continue", Toast.LENGTH_SHORT).show();
                    } else if (!smsPermission) {
                        Toast.makeText(InfoActivity.this, "Please Provide SMS Permission To Continue", Toast.LENGTH_SHORT).show();
                    } else if (!contactPermission) {
                        Toast.makeText(InfoActivity.this, "Please Provide Contact Permission To Continue", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
