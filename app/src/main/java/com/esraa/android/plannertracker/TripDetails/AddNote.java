package com.esraa.android.plannertracker.TripDetails;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.esraa.android.plannertracker.ChatHeadService;
import com.esraa.android.plannertracker.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AddNote extends AppCompatActivity {
    public static final String CLICKED_ITEM_POSITION = "ClickedItemPoisiton";
    EditText addNote;
    int position;
    ListView listView;
    ArrayAdapter mArrayAdapter;
    DatabaseReference databaseReference;
    String IDTaken;
    List<String> myNotesList;
    FloatingActionButton fab;
    String uId;
    FirebaseAuth mAuth;
    FirebaseUser mUser;


    @Override
    protected void onStart() {
        super.onStart();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myNotesList.clear();
                for (DataSnapshot myNotes : dataSnapshot.getChildren()) {

                    for (DataSnapshot notesInfo : myNotes.getChildren()) {
                    NoteClass note = notesInfo.getValue(NoteClass.class);
                    Log.i("notes", "note  = " + note.getMyNotes());
                    myNotesList.add(note.getMyNotes());
                }
                }
                mArrayAdapter = new ArrayAdapter(AddNote.this, android.R.layout.simple_list_item_1, myNotesList);
                listView.setAdapter(mArrayAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        getId();
        initialize();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //List<String> NoteAdded = new ArrayList<>();
                //NoteAdded.add(note);
                String note = addNote.getText().toString();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(AddNote.this)) {
                    //If the draw over permission is not available open the settings screen
                    //to grant the permission.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 1);
                }else{
                    String IDClicked = getIntent().getStringExtra("id");
                    String key = databaseReference.child("note").push().getKey();
                    NoteClass noteClass = new NoteClass(key, note);
                    databaseReference.child("note").child(key).setValue(noteClass);
                    Toast.makeText(AddNote.this, "Done added", Toast.LENGTH_SHORT).show();
                    addNote.setText(" ");
                    goToChatHead();
                }
//                String IDClicked = getIntent().getStringExtra("id");
//                String key = databaseReference.child("note").push().getKey();
//                NoteClass noteClass = new NoteClass(key, note);
//                databaseReference.child("notes").child(key).setValue(noteClass);
//                Toast.makeText(AddNote.this, "Done added", Toast.LENGTH_SHORT).show();
//                addNote.setText(" ");
//                //open Chat head
//                goToChatHead();
                finish();
            }
        });

    }

    private void goToChatHead() {
        Intent intent = new Intent(AddNote.this,ChatHeadService.class);
        intent.putExtra("id",IDTaken);
        startService(intent);
    }


    private void getId() {
        Intent intent = getIntent();
        IDTaken = intent.getStringExtra("id");
    }


    private void initialize() {
        listView = findViewById(R.id.theList);
        mAuth = FirebaseAuth.getInstance();
        myNotesList = new ArrayList<>();
        mUser=mAuth.getCurrentUser();
        uId=mUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(uId).child("Trip Data").child(IDTaken);
        addNote = findViewById(R.id.editNote);
        fab = findViewById(R.id.fab);



    }
}
