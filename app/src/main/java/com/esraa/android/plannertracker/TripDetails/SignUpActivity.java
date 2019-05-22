package com.esraa.android.plannertracker.TripDetails;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.esraa.android.plannertracker.LoginActivity;
import com.esraa.android.plannertracker.MainActivity;
import com.esraa.android.plannertracker.NewPlan;
import com.esraa.android.plannertracker.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText  email, password;
    Button mRegisterbtn;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    String Email, Password;
    ProgressDialog mDialog;
    FirebaseUser fu;
    FirebaseDatabase database;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title


        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        //if user already exist
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        }


        databaseReference = FirebaseDatabase.getInstance().getReference();



        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        mRegisterbtn = (Button) findViewById(R.id.signupbtn);
        mDialog = new ProgressDialog(this);


        mRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                    //Toast.makeText(SignUpActivity.this, "You are connected to Internet", Toast.LENGTH_SHORT).show();
                    UserRegister();


                } else {
                    Toast.makeText(SignUpActivity.this, "You are not connected to Internet", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }



    private void UserRegister() {

        Email = email.getText().toString().trim();
        Password = password.getText().toString().trim();


        if (TextUtils.isEmpty(Email)) {
            email.setError("email is required");
            email.requestFocus();
            return;
        } else if (TextUtils.isEmpty(Password)) {
            password.setError("password is required");
            password.requestFocus();

            return;
        } else if (Password.length() < 6) {
            password.setError("password is required");
            password.requestFocus();

            return;
        }

        mDialog.setMessage("Creating User please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        mAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Email = email.getText().toString().trim();
                    Password = password.getText().toString().trim();

                    // sendAnewUser();
                    createAnewUser(Email, Password);
                    //sendAnewUser();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);

                    finish();

                    mDialog.dismiss();
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();


                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }


    private void sendAnewUser () {

        Email = email.getText().toString().trim();
        Password = password.getText().toString();
        User u = new User();
        u.setUserEmail(Email);

        u.setUserPassword(Password);

        Intent intent = new Intent(SignUpActivity.this, NewPlan.class);

        intent.putExtra("SEND_TEXT2", Email);
        intent.putExtra("SEND_TEXT3", Password);


        startActivity(intent);
        finish();

    }


//check network connection

    protected boolean isOnline () {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    private void createAnewUser (String Email, String Password){

        fu = mAuth.getCurrentUser();
        String id = fu.getUid();

        User user = new User(id, Email, Password);


        Toast.makeText(SignUpActivity.this, user.getUserId(), Toast.LENGTH_SHORT).show();

        databaseReference.child("users").child(id).setValue(user);

    }
}

