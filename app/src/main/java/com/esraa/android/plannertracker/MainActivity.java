package com.esraa.android.plannertracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.esraa.android.plannertracker.TripDetails.ArrayAdapter;
import com.esraa.android.plannertracker.TripDetails.History;
import com.esraa.android.plannertracker.TripDetails.NoteClass;
import com.esraa.android.plannertracker.TripDetails.TrackerInformation;
import com.esraa.android.plannertracker.TripDetails.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TRIP_DATA = "Trip Data";
    String id;
     Context context;

    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ImageView favourit;
    TextView amazingTrips;
    TrackerInformation trackerInformation;
    NoteClass noteClass;
    ArrayList<TrackerInformation> trackerInformationList;
    boolean flagRound;
    FirebaseUser fu ;
    FirebaseAuth mAuth;
    String userId;
    User user;
    int flag;


    @Override
    protected void onStart() {
        super.onStart();


        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.keepSynced(true);
        databaseReference.child(userId).child("Trip Data").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("trace", dataSnapshot.getKey());
                trackerInformationList.clear();
                for (DataSnapshot trackInfo : dataSnapshot.getChildren()) {
                    getTrackDetails(trackInfo);
                    trackerInformationList.add(trackerInformation);
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(new ArrayAdapter(MainActivity.this, trackerInformationList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void getTrackDetails(DataSnapshot trackInfo) {
        TrackerInformation values = trackInfo.getValue(TrackerInformation.class);
        trackerInformation = new TrackerInformation();
        String nameOfTrip = values.getTripName();
        String start = values.getStartPosition();
        String end = values.getDestination();
        String date = values.getDate();
        String time = values.getTime();
        String tripType = values.getTripType();
        id = values.getId();
        trackerInformation.setTripName(nameOfTrip);
        trackerInformation.setStartPosition(start);
        trackerInformation.setDestination(end);
        trackerInformation.setDate(date);
        trackerInformation.setTime(time);
        trackerInformation.setId(id);
        trackerInformation.setTripType(tripType);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        databaseReference =  FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();
        fu = mAuth.getCurrentUser();
        userId = fu.getUid();
        intialize();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        androidStaff(toolbar);



        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this,NewPlan.class);
                intent.putExtra("SEND_ID",userId);
                startActivity(intent);
            }
        });
    }

    private void intialize() {
        recyclerView = findViewById(R.id.recycler);

        trackerInformationList = new ArrayList<>();
        favourit=findViewById(R.id.favourit);
        amazingTrips=findViewById(R.id.trip);


    }

    private void androidStaff(Toolbar toolbar)
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.emailuser);
        String um= fu.getEmail();


        navUsername.setText(um);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);


        } else {

            super.onBackPressed();

        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.history)
        {
            startActivity(new Intent(MainActivity.this, History.class));


        }
        else if (id == R.id.logOut) {
            signOut();
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public  void signOut()
    {
        mAuth.signOut();
    }

}
