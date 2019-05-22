package com.esraa.android.plannertracker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.esraa.android.plannertracker.BroadCastRecievers.AlarmReciever;
import com.esraa.android.plannertracker.BroadCastRecievers.NotificationReciever;
import com.esraa.android.plannertracker.SqltieDatabase.DbContract;
import com.esraa.android.plannertracker.SqltieDatabase.DbHelper;
import com.esraa.android.plannertracker.TripDetails.TrackerInformation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.util.Calendar;


public class NewPlan extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 2;
    public int ALARM_TRIP_REQUEST = 0;
    public boolean flag = false;
    private static final String token = "sk.eyJ1IjoibWlsa3lyYW5nZXIiLCJhIjoiY2pzOTBzOXlxMTZ6ZDN6czhiNTJjY2JrdCJ9.TVE3NN-juPXRMYr14hRBFA";
    public static final String PREFS_NAME = "MyPrefsFile";
    Calendar c, calendarRound;
    String TripType, TripName, start, destination, time, date;
    LinearLayout roundChooserDate, roundChooserTime;
    SQLiteDatabase sqLiteDatabase;
    TimePickerDialog mTimePicker, mTimePickerRound;
    DatePickerDialog datePickerDialog, mDatePickerRound;
    RadioGroup radioGroup;
    RadioButton radioButton, mround, msingle;
    int hour, minute, mYear, mMonth, mDay;
    int roundHour, roundMinute, roundYear, roundMonth, roundDAy;
    EditText startPosition, endPosition, tripName;
    DatabaseReference databaseReference;
    Button dateBtn, timeBtn, save;
    TextView dateText, timeText, roundDateText, roundTimeText;
    ImageView roundDateIcon, roundTimeIcon;
    TextInputEditText editTextTripRound;
    String uid;
    int x, y;
    MainActivity mainActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_plan);
        ALARM_TRIP_REQUEST++;
        c = Calendar.getInstance();
        calendarRound = Calendar.getInstance();
        initialize();
        startPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStartPlaces();

            }
        });
        endPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndPlaces();

            }
        });
        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate();
            }
        });
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseTime();
            }
        });
        roundDateIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseRoundDate();
            }
        });
        roundTimeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseRoundTime();
            }
        });
        mround.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setVisibilty(b);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveToDatabase();
                if (flag) {
                    setAlarm(false);
                }
                Log.i("Alarm", String.valueOf(x));
                if (mround.isChecked()) {
                    saveRoundTripToDatabase();
                    setAlarmRoundTrip(false);
                    Log.i("Alarm2", String.valueOf(y));
                }

            }

        });

    }

    private void setAlarmRoundTrip(boolean isNotification) {

        AlarmManager manager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        Intent intent;
        PendingIntent pendingIntent;
        y = (int) System.currentTimeMillis();
        if (isNotification) {
            intent = new Intent(this, NotificationReciever.class);
            pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        } else {
            intent = new Intent(this, AlarmReciever.class);
            pendingIntent = PendingIntent.getBroadcast(this, y, intent, 0);

        }

        calendarRound.set(roundYear, roundMonth, roundDAy, roundHour, roundMinute, 0);
        manager.set(AlarmManager.RTC_WAKEUP, calendarRound.getTimeInMillis(),
                pendingIntent);

        Log.i("timeSaveRound", String.valueOf(calendarRound));
        Log.i("timeSaveRound", String.valueOf(calendarRound.getTimeInMillis()));
        Log.i("timeSaveRound", String.valueOf(calendarRound.getTimeZone()));
        Log.i("timeSaveRound", String.valueOf(calendarRound.getTime()));
    }


    private void setVisibilty(boolean b) {
        if (b) {
            roundChooserDate.setVisibility(View.VISIBLE);
            roundChooserTime.setVisibility(View.VISIBLE);
            editTextTripRound.setVisibility(View.VISIBLE);
        } else {
            roundChooserDate.setVisibility(View.GONE);
            roundChooserTime.setVisibility(View.GONE);
            editTextTripRound.setVisibility(View.GONE);
        }
    }

    private void chooseRoundDate() {
        roundYear = calendarRound.get(Calendar.YEAR); // current year
        roundMonth = calendarRound.get(Calendar.MONTH); // current month
        roundDAy = calendarRound.get(Calendar.DAY_OF_MONTH); // current day
        mDatePickerRound = new DatePickerDialog(NewPlan.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                roundYear = year;
                roundMonth = month;
                roundDAy = day;
                roundDateText.setText(day + "/" + (month + 1) + "/" + year);
            }
        }, roundYear, roundMonth, roundDAy);
        mDatePickerRound.show();

    }

    private void chooseRoundTime() {
        roundHour = calendarRound.get(Calendar.HOUR_OF_DAY);
        roundMinute = calendarRound.get(Calendar.MINUTE);
        mTimePickerRound = new TimePickerDialog(NewPlan.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String status = "AM";
                roundHour = selectedHour;
                roundMinute = selectedMinute;
                if (selectedHour > 11) {
                    // If the hour is greater than or equal to 12
                    // Then the current AM PM status is PM
                    status = "PM";
                }
                int hour_of_12_hour_format;

                if (selectedHour > 11) {
                    hour_of_12_hour_format = selectedHour - 12;
                } else {
                    hour_of_12_hour_format = selectedHour;
                }

                roundTimeText.setText(hour_of_12_hour_format + " : " + selectedMinute + " :" + status);
            }
        }, roundHour, roundMinute, false);//Yes 24 hour time
        mTimePickerRound.setTitle("Select Time");
        mTimePickerRound.show();
    }

    private void saveTointernal() {
        DbHelper dbHelper = new DbHelper(getBaseContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DbContract.DbTripDetails.COLUMN_TRIP_NAME, TripName);
        contentValues.put(DbContract.DbTripDetails.COLUMN_START_POINT, start);
        contentValues.put(DbContract.DbTripDetails.COLUMN_END_POINT, destination);
        contentValues.put(DbContract.DbTripDetails.COLUMN_DATE_TRIP, date);
        contentValues.put(DbContract.DbTripDetails.COLUMN_TIME_TRIP, time);

        long newRowId = sqLiteDatabase.insert(DbContract.DbTripDetails.TABLE_NAME, null, contentValues);
        Toast.makeText(this, "Added internally succeffully", Toast.LENGTH_SHORT).show();
    }

    public void showStartPlaces() {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(token)
                .placeOptions(PlaceOptions.builder().build(PlaceOptions.MODE_CARDS))
                .build(NewPlan.this);
        startActivityForResult(intent, 1);


    }


    public void showEndPlaces() {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(token)
                .placeOptions(PlaceOptions.builder().build(PlaceOptions.MODE_CARDS))
                .build(NewPlan.this);
        startActivityForResult(intent, 2);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            Toast.makeText(this, feature.text(), Toast.LENGTH_LONG).show();
            start = feature.text();
            startPosition.setText(start);
            // adding shared pref
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String startP = feature.text();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("start", startP);
            editor.commit();


        } else if (resultCode == Activity.RESULT_OK && requestCode == 2) {
            CarmenFeature feature = PlaceAutocomplete.getPlace(data);
            Toast.makeText(this, feature.text(), Toast.LENGTH_LONG).show();
            destination = feature.text();
            endPosition.setText(destination);
            // adding shared pref
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String endP = feature.text();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("end", endP);
            editor.commit();


        }
    }


    private void saveRoundTripToDatabase() {
        String roundTripName = editTextTripRound.getText().toString();
        String roundDate = roundDateText.getText().toString();
        String roundTime = roundTimeText.getText().toString();
        String startRoundTrip = endPosition.getText().toString();
        String destinationRoundTrip = startPosition.getText().toString();

        String ID = databaseReference.push().getKey();
        TrackerInformation roundTracks = new TrackerInformation(ID, destination, start,
                roundTripName, roundTime, roundDate, "Single");
        databaseReference.child(uid).child("Trip Data").child(ID).setValue(roundTracks);
        Toast.makeText(this, "Done added Round trip", Toast.LENGTH_SHORT).show();
        editTextTripRound.setText("");
        roundDateText.setText("");
        roundTimeText.setText("");

    }

    private void saveToDatabase() {
        Log.i("trace", radioButton.getText().toString());
        TripType = radioButton.getText().toString();
        TripName = tripName.getText().toString();
        start = startPosition.getText().toString();
        destination = endPosition.getText().toString();
        date = dateText.getText().toString();
        time = timeText.getText().toString();

        if (!TextUtils.isEmpty(start) && !TextUtils.isEmpty(date) &&
                !TextUtils.isEmpty(time) && !TextUtils.isEmpty(TripName)
                && !TextUtils.isEmpty(TripType))
        {
            String id = databaseReference.push().getKey();
            Intent intent = getIntent();
            uid = intent.getStringExtra("SEND_ID");
            TrackerInformation trackerInformation = new TrackerInformation(id, start,
                    destination, TripName, time, date, TripType);
            //    databaseReference.child(id).setValue(trackerInformation);
            databaseReference.child(uid).child("Trip Data").child(id).setValue(trackerInformation);
            Toast.makeText(this, "Done Added", Toast.LENGTH_SHORT).show();
            flag = true;
            finish();

        } else {
            Toast.makeText(this, "Enter Valid bodies", Toast.LENGTH_SHORT).show();

        }


//        SharedPreferences settings = getSharedPreferences(PREFS_NAME , 0);
//        SharedPreferences.Editor editor = settings.edit();
//        editor.putString("tripName",TripName);
//        editor.commit();
    }

    private void chooseTime() {
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        mTimePicker = new TimePickerDialog(NewPlan.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String status = "AM";
                hour = selectedHour;
                minute = selectedMinute;
                if (selectedHour > 11) {
                    // If the hour is greater than or equal to 12
                    // Then the current AM PM status is PM
                    status = "PM";
                }
                int hour_of_12_hour_format;

                if (selectedHour > 11) {
                    hour_of_12_hour_format = selectedHour - 12;
                } else {
                    hour_of_12_hour_format = selectedHour;
                }

                timeText.setText(hour_of_12_hour_format + " : " + selectedMinute + " :" + status);
            }
        }, hour, minute, false);//Yes 24 hour time

        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void chooseDate() {
        mYear = c.get(Calendar.YEAR); // current year
        mMonth = c.get(Calendar.MONTH); // current month
        mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        datePickerDialog = new DatePickerDialog(NewPlan.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mYear = year;
                mMonth = month;
                mDay = day;
                dateText.setText(day + "/" + (month + 1) + "/" + year);
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();

    }

    private void initialize() {
        editTextTripRound = findViewById(R.id.roundTripName);
        roundChooserDate = findViewById(R.id.RoundChooserDate);
        roundChooserTime = findViewById(R.id.RoundChooserTime);
        roundDateIcon = findViewById(R.id.roundDateImage);
        roundTimeIcon = findViewById(R.id.roundTimeImage);
        roundDateText = findViewById(R.id.roundDate);
        roundTimeText = findViewById(R.id.roundTime);
        mround = findViewById(R.id.roundBtn);

        radioGroup = findViewById(R.id.radioGrp);
        tripName = findViewById(R.id.TripNameNew);
        startPosition = findViewById(R.id.startPosition);
        endPosition = findViewById(R.id.destination);
        save = findViewById(R.id.saveToDatabase);
        dateBtn = findViewById(R.id.chooseDate);
        timeBtn = findViewById(R.id.chooseTime);
        timeText = findViewById(R.id.timeText);
        dateText = findViewById(R.id.dateText);
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        //  databaseReference = FirebaseDatabase.getInstance().getReference("Trip Data");
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

    private void setAlarm(boolean isNotification) {
        AlarmManager manager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        Intent intent;
        PendingIntent pendingIntent;
        x = (int) System.currentTimeMillis();

        if (isNotification) {
            intent = new Intent(this, NotificationReciever.class);
            pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        } else {
            intent = new Intent(this, AlarmReciever.class);
            pendingIntent = PendingIntent.getBroadcast(this, x, intent, 0);

        }

        c.set(mYear, mMonth, mDay, hour, minute, 0);
        manager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                pendingIntent);
        Log.i("timeSave", String.valueOf(c));
        Log.i("timeSave", String.valueOf(c.getTimeInMillis()));
        Log.i("timeSave", String.valueOf(c.getTimeZone()));
        Log.i("timeSave", String.valueOf(c.getTime()));
    }
}
