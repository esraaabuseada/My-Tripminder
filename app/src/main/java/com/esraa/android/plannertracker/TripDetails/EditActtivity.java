package com.esraa.android.plannertracker.TripDetails;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.esraa.android.plannertracker.BroadCastRecievers.AlarmReciever;
import com.esraa.android.plannertracker.BroadCastRecievers.NotificationReciever;
import com.esraa.android.plannertracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.PlaceAutocomplete;
import com.mapbox.mapboxsdk.plugins.places.autocomplete.model.PlaceOptions;

import java.util.Calendar;

public class EditActtivity extends AppCompatActivity {
    EditText startPosition, endPosition, tripName;
    RadioGroup radioGroup;
    Calendar c;
    RadioButton radioButton, single, round;
    DatabaseReference databaseReference;
    Button date, time, update;
    TextView dateText, timeText;
    int hour, minute, mYear, mMonth, mDay;
    TimePickerDialog mTimePicker;
    DatePickerDialog datePickerDialog;
    TrackerInformation trackerInformation;
    String tripNameChosen, startLocation, endLocation, dateChosen, timeChosen, IDTaken, TripType;
    private static final String token = "sk.eyJ1IjoibWlsa3lyYW5nZXIiLCJhIjoiY2pzOTBzOXlxMTZ6ZDN6czhiNTJjY2JrdCJ9.TVE3NN-juPXRMYr14hRBFA";
    public static final String PREFS_NAME = "MyPrefsFile";
    String start , destination;
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        c = Calendar.getInstance();
        initialize();
        getExtras();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate();
            }
        });
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseTime();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateToDatabase();
                setAlarm(false);
                finish();
            }
        });


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


    }

    public void showStartPlaces() {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(token)
                .placeOptions(PlaceOptions.builder().build(PlaceOptions.MODE_CARDS))
                .build(EditActtivity.this);
        startActivityForResult(intent, 1);


    }


    public void showEndPlaces() {
        Intent intent = new PlaceAutocomplete.IntentBuilder()
                .accessToken(token)
                .placeOptions(PlaceOptions.builder().build(PlaceOptions.MODE_CARDS))
                .build(EditActtivity.this);
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



    private void getExtras() {
        tripNameChosen = getIntent().getStringExtra("tripName");
        startLocation = getIntent().getStringExtra("start");
        endLocation = getIntent().getStringExtra("end");
        dateChosen = getIntent().getStringExtra("date");
        timeChosen = getIntent().getStringExtra("time");
        IDTaken = getIntent().getStringExtra("id");
        TripType = getIntent().getStringExtra("tripType");

        tripName.setText(tripNameChosen);
        startPosition.setText(startLocation);
        endPosition.setText(endLocation);
        dateText.setText(dateChosen);
        timeText.setText(timeChosen);
    }

    private void updateToDatabase() {
        String Note = "";
        int tripTypeChooser = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(tripTypeChooser);
        String TripType = radioButton.getText().toString();
        String theTripName = tripName.getText().toString();
        String start = startPosition.getText().toString();
        String destination = endPosition.getText().toString();
        String date = dateText.getText().toString();
        String time = timeText.getText().toString();

        String ID = IDTaken;


        mAuth = FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        uid=mUser.getUid();

        Log.i("trace", "updateToDatabase:  " + ID);
        if (!TextUtils.isEmpty(start) && !TextUtils.isEmpty(date) &&
                !TextUtils.isEmpty(time) && !TextUtils.isEmpty(theTripName)
                && !TextUtils.isEmpty(TripType)) {
            TrackerInformation trackerInformation = new TrackerInformation(ID, start,
                    destination, theTripName, time, date, TripType);
            databaseReference.child(uid).child("Trip Data").child(ID).setValue(trackerInformation);
            Toast.makeText(this, "Done Updated", Toast.LENGTH_SHORT).show();

            tripName.setText("");
            startPosition.setText("");
            endPosition.setText("");
            dateText.setText("");
            timeText.setText("");
        } else {
            Toast.makeText(this, "Enter Valid bodies", Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseTime() {
        Calendar mcurrentTime = Calendar.getInstance();
        hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        minute = mcurrentTime.get(Calendar.MINUTE);
        mTimePicker = new TimePickerDialog(EditActtivity.this, new TimePickerDialog.OnTimeSetListener() {
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
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR); // current year
        mMonth = c.get(Calendar.MONTH); // current month
        mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        datePickerDialog = new DatePickerDialog(EditActtivity.this, new DatePickerDialog.OnDateSetListener() {
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

        radioGroup = findViewById(R.id.radioGroupUpdated);
        single = findViewById(R.id.single);
        round = findViewById(R.id.round);

        trackerInformation = new TrackerInformation();
        tripName = findViewById(R.id.TripNameEdit);
        startPosition = findViewById(R.id.beginUpdate);
        endPosition = findViewById(R.id.destinationUpdate);
        update = findViewById(R.id.updateInDatabase);
        date = findViewById(R.id.chooseDateUpdate);
        time = findViewById(R.id.chooseTimeUpdate);
        timeText = findViewById(R.id.timeTextUpdate);
        dateText = findViewById(R.id.dateTextUpdate);
        //databaseReference = FirebaseDatabase.getInstance().getReference("Trip Data");
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

    }

    private void setAlarm(boolean isNotification) {
        AlarmManager manager = (AlarmManager) getSystemService(this.ALARM_SERVICE);
        Intent intent;
        PendingIntent pendingIntent;
        int x = (int) System.currentTimeMillis();
        if (isNotification) {
            intent = new Intent(this, NotificationReciever.class);
            pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        } else {
            intent = new Intent(this, AlarmReciever.class);
            pendingIntent = PendingIntent.getBroadcast(this, x, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }

        c.set(mYear, mMonth, mDay, hour, minute, 0);
        manager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),
                pendingIntent);
        Log.i("time Update", String.valueOf(c));
        Log.i("time Update", String.valueOf(c.getTimeInMillis()));
        Log.i("time Update", String.valueOf(c.getTimeZone()));
        Log.i("time Update", String.valueOf(c.getTime()));
    }


}
