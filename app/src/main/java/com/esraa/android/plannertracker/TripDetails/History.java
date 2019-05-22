package com.esraa.android.plannertracker.TripDetails;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.esraa.android.plannertracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class History extends AppCompatActivity {
    RecyclerView recyclerView;
    HistoryRecyclerAdapter historyRecyclerAdapter;
    ArrayList<HistoryList> historyLists;
    HistoryList historyList;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    String uId ;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initialize();
    }

    public void initialize() {
        recyclerView = findViewById(R.id.recycler);
        historyLists = new ArrayList<>();
        mAuth =FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        uId = firebaseUser.getUid();

    }

    @Override
    public void onStart() {
        super.onStart();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        Log.i("trace", "INSIDE ON START");

        databaseReference.child(uId).child("Trip History").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i("trace", dataSnapshot.getKey());
                historyLists.clear();
                for (DataSnapshot trackInfo : dataSnapshot.getChildren()) {
                    getTrackDetails(trackInfo);
                    historyLists.add(historyList);

                }

                recyclerView.setLayoutManager(new LinearLayoutManager(History.this));
                recyclerView.setAdapter(new HistoryRecyclerAdapter(History.this, historyLists));



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getTrackDetails(DataSnapshot trackInfo) {
        //    getTrackDetails(trackInfo);

        HistoryList values = trackInfo.getValue(HistoryList.class);
        historyList = new HistoryList();
        String tripName = values.getTripName();
        String start = values.getStartPlace();
        String end = values.getEndPlace();
        String id = values.getId(); /// newly added
//        Log.v("zzzzz", id);

        historyList.setTripName(tripName);
        historyList.setStartPlace(start);
        historyList.setEndPlace(end);
        historyList.setId(id);

        Log.v("hello", tripName);
        Log.v("helloo", start);
        Log.v("hellooo", end);


    }


}
