package com.esraa.android.plannertracker.TripDetails;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.esraa.android.plannertracker.R;

public class DetailActivity extends AppCompatActivity {
    TextView mtrip,mstart,mend,mdate,mtime,mtype;
    Button ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initialize();
        getExtras();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getExtras() {
        String tripNameChosen = getIntent().getStringExtra("tripName");
        String startLocation = getIntent().getStringExtra("start");
        String endLocation = getIntent().getStringExtra("end");
        String dateChosen = getIntent().getStringExtra("date");
        String timeChosen = getIntent().getStringExtra("time");
        String typeTrip = getIntent().getStringExtra("tripType");
        String IDTaken = getIntent().getStringExtra("id");
        mtrip.setText(tripNameChosen);
        mstart.setText(startLocation);
        mend.setText(endLocation);
        mdate.setText(dateChosen);
        mtime.setText(timeChosen);
        mtype.setText(typeTrip);
    }

    private void initialize() {
        mtime = findViewById(R.id.time);
        mtrip=findViewById(R.id.theTrip);
        mtype = findViewById(R.id.typeTrip);
        mstart = findViewById(R.id.startPoint);
        mend = findViewById(R.id.destinationPoint);
        mdate = findViewById(R.id.date);
        ok = findViewById(R.id.okBtn);
    }
}
