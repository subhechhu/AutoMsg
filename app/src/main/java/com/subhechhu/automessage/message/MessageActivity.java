package com.subhechhu.automessage.message;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.subhechhu.automessage.AppController;
import com.subhechhu.automessage.Details;
import com.subhechhu.automessage.R;
import com.subhechhu.automessage.SharedPrefUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class MessageActivity extends AppCompatActivity {
    String TAG = getClass().getSimpleName();

    final int MESSENGER_POSITION = 0;
    final int WHATSAPP_POSITION = 1;
    final int VIBER_POSITION = 2;

    boolean STATE_CONTACT_ADDED, STATE_DATE_ADDED,
            STATE_TIME_ADDED, STATE_MESSAGE_ADDED, STATE_MEDIUM_SELECTED;

    TextView userNameTV, dateTV, timeTV, messageTV, descrptionTV;
    long currentTimeLong;

    private static final int GET_CONTACT = 111;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 999;
    private static final int SEND_SMS = 888;

    public static SubscriptionManager mSubscriptionManager;
    public static List<SubscriptionInfo> subInfoList;

    SpinnerAdapter spinnerAdapter;
    int[] appIcons;
    String[] appName;

    Spinner spinnerMedium;
    String mediumSelected, recipientNumber, recipientName, message, displayTime, displayDate, senderNumber;
    //    EditText editText_senderphone;
    Button saveBtn;

    RadioButton sim1, sim2;
    RadioGroup simGrp;

    int simCount;

    HashSet<String> phoneNumbers;

    Calendar selectedCalenderInstance;
    Dialog dialog;

    boolean isNewMessage, isChecked;
    String simSelected = "Sim1";
    DBhelper dbHelper;

    SharedPrefUtil sharedPrefUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPrefUtil = new SharedPrefUtil();

        spinnerMedium = (Spinner) findViewById(R.id.spinner_medium);
        simCount = sharedPrefUtil.getSharedPreferenceInt(AppController.getContext(), "simCount", 1);

        appIcons = new int[]{R.drawable.message_icon, R.drawable.whatsapp_icon, R.drawable.viber};
        appName = new String[]{"Messenger", "Whatsapp", "Viber"};

        userNameTV = (TextView) findViewById(R.id.textView_userName);
        dateTV = (TextView) findViewById(R.id.textView_date);
        timeTV = (TextView) findViewById(R.id.textView_time);
        messageTV = (TextView) findViewById(R.id.textView_message);
        descrptionTV = (TextView) findViewById(R.id.textView_description);

        isChecked = false;
        sim1 = (RadioButton) findViewById(R.id.radio_sim1);
        sim2 = (RadioButton) findViewById(R.id.radio_sim2);
        simGrp = (RadioGroup) findViewById(R.id.radioSim);

        if (simCount > 1) {
            isChecked = false;
            simGrp.setVisibility(View.VISIBLE);
        } else {
            isChecked = true;
            simGrp.setVisibility(View.GONE);
        }

        simGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isChecked = true;
                if (checkedId == R.id.radio_sim1) {
                    simSelected = "Sim1";
                } else {
                    simSelected = "Sim2";
                }
            }
        });

        saveBtn = (Button) findViewById(R.id.button_save);

        if (dbHelper == null) {
            dbHelper = new DBhelper(AppController.getContext());
        }

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (STATE_CONTACT_ADDED &&
                        STATE_DATE_ADDED &&
                        STATE_TIME_ADDED &&
                        STATE_MESSAGE_ADDED &&
                        isChecked) {
                    if (STATE_MEDIUM_SELECTED) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.SEND_SMS) !=
                                        PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS);
                        } else {
                            SendMessage(recipientNumber, recipientName, currentTimeLong, message, simCount, simSelected);
                        }
                    } else {
                        Toast.makeText(MessageActivity.this, "Selected Medium Not Found in Your Device", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MessageActivity.this, "Fields Cannot Be Left Blank or UnChecked!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        phoneNumbers = new HashSet<>();

        spinnerAdapter = new SpinnerAdapter(MessageActivity.this, appIcons, appName);
        spinnerMedium.setAdapter(spinnerAdapter);

        spinnerMedium.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case WHATSAPP_POSITION:
                        mediumSelected = "Whatsapp";
                        STATE_MEDIUM_SELECTED = CheckApp(mediumSelected);
                        setDescription(position);
                        break;
                    case MESSENGER_POSITION:
                        mediumSelected = "Messenger";
                        STATE_MEDIUM_SELECTED = true;
                        setDescription(position);
                        break;
                    case VIBER_POSITION:
                        mediumSelected = "Viber";
                        STATE_MEDIUM_SELECTED = CheckApp(mediumSelected);
                        setDescription(position);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int GetCarriorsInformation() {
//        sims = new ArrayList<Integer>();
        mSubscriptionManager = SubscriptionManager.from(MessageActivity.this);
        subInfoList = mSubscriptionManager.getActiveSubscriptionInfoList();
        return subInfoList.size();
    }

    private Boolean CheckApp(String medium) {
        String app = null;
        if (medium.equals("Whatsapp")) {
            app = "com.whatsapp";
        } else if (medium.equalsIgnoreCase("Viber")) {
            app = "com.viber.voip";
        }
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(app, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, medium + " not Found in Your Device", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void SendMessage(String recipientNumber, String recipientName,
                             long currentTimeLong, String message, int simCount, String simSelected) {
        Calendar currentCalendarInstance = Calendar.getInstance();
        if (selectedCalenderInstance.before(currentCalendarInstance)) {
            Toast.makeText(MessageActivity.this, "Invalid Date/Time detected. Please Enter the Valid Date/Time", Toast.LENGTH_SHORT).show();
        } else {
            SavedData(recipientName, recipientNumber, displayTime, displayDate,
                    currentTimeLong, message, simCount, simSelected);
        }
    }

    private void SavedData(String recipientName, String recipientNumber,
                           String displayTime, String displayDate, long currentTimeLong, String message,
                           int simCount, String simSelected) {
        Long recentId;
        Details details = new Details();
        details.setName(recipientName);
        details.setNumber(recipientNumber);
        details.setDate(displayDate);
        details.setTime(displayTime);
        details.setMessage(message);
        details.setMediumSelected(mediumSelected);
        details.setTimelong("" + currentTimeLong);
        details.setSimSelected(simSelected);
        details.setSimCount(simCount);

        recentId = dbHelper.addRemainder(details);
        details.setId(String.valueOf(recentId));

        if (recentId != -1) {
            DataInserted(details);
        } else {
            Toast.makeText(MessageActivity.this, "Something Went Wrong. Please Try Again", Toast.LENGTH_SHORT).show();
        }
    }

    void DataInserted(Details details) {
        Log.d(TAG, "inserted");

        AddRemainder(details);
        Intent intent = new Intent();
        intent.putExtra("result", "success");
        intent.putExtra("id", details.getId());
        intent.putExtra("name", details.getName());
        intent.putExtra("number", details.getNumber());
        intent.putExtra("dateTV", details.getDate());
        intent.putExtra("timeTV", details.getTime());
        intent.putExtra("messageTV", details.getMessage());
        intent.putExtra("medium", details.getMediumSelected());
        intent.putExtra("longTime", details.getTimelong());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void AddRemainder(Details details) {
        PendingIntent pendingIntent;

        Intent intent = new Intent(MessageActivity.this, AlarmReceiver.class);
        intent.putExtra("id", details.getId());
        intent.putExtra("name", details.getName());
        intent.putExtra("number", details.getNumber());
        intent.putExtra("date", details.getDate());
        intent.putExtra("time", details.getTime());
        intent.putExtra("message", details.getMessage());
        intent.putExtra("longDate", details.getTimelong());
        intent.putExtra("medium", details.getMediumSelected());
        intent.putExtra("simSelected", details.getSimSelected());
        intent.putExtra("simCount", details.getSimCount());

        long remainderTimeMills = Long.parseLong(details.getTimelong());
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss ");
        String date = format.format(new Date(remainderTimeMills));
        pendingIntent = PendingIntent.getBroadcast(AppController.getContext(), Integer.parseInt(details.getId()), intent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP, remainderTimeMills, pendingIntent);
        Toast.makeText(this, "Message Remainder is set for " + date, Toast.LENGTH_SHORT).show();
    }

    private void setDescription(int position) {
        if (position == MESSENGER_POSITION) {
            descrptionTV.setText(getString(R.string.messenger_description));
            saveBtn.setText("Send");
        } else {
            descrptionTV.setText(getString(R.string.other_medium_description));
            saveBtn.setText("Save");
        }
    }

    private void ReadContacts() {
        phoneNumbers.clear();
        Intent contactIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactIntent, GET_CONTACT);
    }

    private void ExtractContact(Intent data) {
        ContentResolver cr = getContentResolver();
        String phoneNo;
        Cursor cursor = null;

        try {
            cursor = cr.query(data.getData(), null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts._ID));
                    recipientName = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                    String hasPhone = cursor
                            .getString(cursor
                                    .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    //            Cursor emailCur = cr.query(
                    //                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    //                    null,
                    //                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    //                    new String[]{contactId}, null);
                    //            if (emailCur != null) {
                    //                emailCur.close();
                    //            }

                    if (hasPhone.equalsIgnoreCase("1"))
                        hasPhone = "true";
                    else
                        hasPhone = "false";

                    if (Boolean.parseBoolean(hasPhone)) {
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = " + contactId, null, null);
                        if (phones != null) {
                            while (phones.moveToNext()) {
                                phoneNo = phones
                                        .getString(phones
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                phoneNumbers.add(phoneNo);
                            }
                            phones.close();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                final CharSequence[] items = phoneNumbers.toArray(new CharSequence[phoneNumbers.size()]);
                if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
                    if (phoneNumbers.size() > 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Select a number");
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                recipientNumber = items[item].toString();

                                recipientNumber = recipientNumber.replaceAll("\\s+", "");
                                AddToView(recipientName, recipientNumber);

                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        recipientNumber = items[0].toString();
                        AddToView(recipientName, recipientNumber);
                    }
                }
            }
        }
    }

    private void AddToView(String recipientName, String recipientNumber) {
        String sourceString = recipientName + "\t" + "[ " + recipientNumber + " ]";
        STATE_CONTACT_ADDED = true;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            userNameTV.setText(Html.fromHtml(sourceString, Html.FROM_HTML_MODE_LEGACY));
        } else {
            userNameTV.setText(Html.fromHtml(sourceString));
        }
    }

    private void ContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        READ_CONTACTS_PERMISSIONS_REQUEST);
            }
        }
    }

    private void SelectDate() {
        final Calendar currentCalenderInstance = Calendar.getInstance();
        final int mYear = currentCalenderInstance.get(Calendar.YEAR);
        final int mMonth = currentCalenderInstance.get(Calendar.MONTH);
        final int mDay = currentCalenderInstance.get(Calendar.DAY_OF_MONTH);
        final DatePickerDialog dialog = new DatePickerDialog(MessageActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        STATE_TIME_ADDED = false;
                        timeTV.setText(getString(R.string.time));
                        SetDate(year, month, dayOfMonth, currentCalenderInstance);
                    }
                }, mYear, mMonth, mDay);
        dialog.show();
    }

    private void SetDate(int year, int month, int dayOfMonth, Calendar currentCalenderInstance) {
        selectedCalenderInstance = Calendar.getInstance();
        selectedCalenderInstance.set(Calendar.YEAR, year);
        selectedCalenderInstance.set(Calendar.MONTH, month);
        selectedCalenderInstance.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        long currentTimeLong = selectedCalenderInstance.getTimeInMillis();
        displayDate = new SimpleDateFormat("dd MMMM yyyy").format(new Date(currentTimeLong));

        if (selectedCalenderInstance.before(currentCalenderInstance)) {
            Toast.makeText(MessageActivity.this, "Invalid Date", Toast.LENGTH_SHORT).show();
        } else {
            STATE_DATE_ADDED = true;
            dateTV.setText(displayDate);
        }
    }

    private void SelectTime() {
        final Calendar currentCalenderInstance = Calendar.getInstance();
        int hour = currentCalenderInstance.get(Calendar.HOUR_OF_DAY);
        int minute = currentCalenderInstance.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(MessageActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                SetTime(selectedHour, selectedMinute, currentCalenderInstance);
            }
        }, hour, minute, false);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void SetTime(int selectedHour, int selectedMinute, Calendar currentCalendarInstance) {

        selectedCalenderInstance.set(Calendar.HOUR_OF_DAY, selectedHour);
        selectedCalenderInstance.set(Calendar.MINUTE, selectedMinute);
        selectedCalenderInstance.set(Calendar.SECOND, 0);

        currentTimeLong = selectedCalenderInstance.getTimeInMillis();
        displayTime = new SimpleDateFormat("hh:mm a").format(new Date(currentTimeLong));

        if (selectedCalenderInstance.before(currentCalendarInstance)) {
            Toast.makeText(MessageActivity.this, "Invalid Time", Toast.LENGTH_SHORT).show();
        } else {
            STATE_TIME_ADDED = true;
            timeTV.setText(displayTime);
        }
    }

    private void EnterMessage(final Boolean isNewMessage) {
        dialog = new Dialog(MessageActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_message_dialog);
        dialog.show();

        final EditText messageET = (EditText) dialog.findViewById(R.id.message_ET);
        if (isNewMessage) {
            messageET.setText(message);
        }

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        if (window != null) {
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }

        Button proceedBtn = (Button) dialog.findViewById(R.id.button_proceed);
        Button cancelBtn = (Button) dialog.findViewById(R.id.button_cancel);
        final TextView countTV = (TextView) dialog.findViewById(R.id.textView_count);

        messageET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countTV.setText(String.valueOf(s.length())+"/400");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (!messageET.getText().toString().trim().isEmpty()) {
                    STATE_MESSAGE_ADDED = true;
                    MessageActivity.this.isNewMessage = true;
                    message = messageET.getText().toString();
                    messageTV.setText(message);
                } else {
                    STATE_MESSAGE_ADDED = false;
                    MessageActivity.this.isNewMessage = false;
                    messageTV.setText("");
                    messageTV.setHint(getString(R.string.message));
                }
            }
        });
    }

    public void ViewClick(View view) {
        switch (view.getId()) {
            case R.id.relativeLayout_contact:
                if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) &&
                        (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
                                PackageManager.PERMISSION_GRANTED)) {
                    ContactPermission();
                } else {
                    ReadContacts();
                }
                break;
            case R.id.relativeLayout_date:
                SelectDate();
                break;
            case R.id.relativeLayout_time:
                if (STATE_DATE_ADDED) {
                    SelectTime();
                } else {
                    Toast.makeText(MessageActivity.this, "Date Missing", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.relativeLayout_message:
                if (messageTV.getText().toString().isEmpty()) {
                    EnterMessage(isNewMessage);
                } else {
                    EnterMessage(isNewMessage);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ReadContacts();
            } else {
                Toast.makeText(MessageActivity.this, "Contact Permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SEND_SMS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SendMessage(recipientNumber, recipientName, currentTimeLong, message, simCount, senderNumber);
            } else {
                Toast.makeText(MessageActivity.this, "Message Permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GET_CONTACT:
                if (resultCode == RESULT_OK) {
                    ExtractContact(data);
                }
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to close the activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}