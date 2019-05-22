package com.esraa.android.plannertracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.esraa.android.plannertracker.TripDetails.SignUpActivity;
import com.esraa.android.plannertracker.TripDetails.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.esraa.android.plannertracker.R;

public class LoginActivity extends AppCompatActivity {
    EditText emailEdittext, passwordEdittext;
    TextView creatAccount;
    Button LogInButton;
    SignInButton GLogin;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListner;
    FirebaseUser mUser;
    String email, password;
    ProgressDialog dialog;
    GoogleSignInClient gsi;
    DatabaseReference databaseReference;
    public static final String TAG="LOGIN";
    private CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_login);
        LogInButton = (Button) findViewById(R.id.loginbtn);
        GLogin = (SignInButton) findViewById(R.id.sign_in_button);
        creatAccount = findViewById(R.id.creatAccount);

        emailEdittext = (EditText) findViewById(R.id.email);
        passwordEdittext= (EditText) findViewById(R.id.password);
        dialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        saveLoginCheckBox = (CheckBox)findViewById(R.id.saveLoginCheckBox);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            emailEdittext.setText(loginPreferences.getString("email", ""));
            passwordEdittext.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }

        if(mAuth.getCurrentUser()!=null){

            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        }

        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Calling EditText is empty or no method.
                if (isOnline()) {
                    userSign();
                } else {
                    Toast.makeText(LoginActivity.this, "You are not connected to Internet", Toast.LENGTH_SHORT).show();
                }



            }


        });

        GLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("55076937471-ssqvvisso1pj8ju9hrmurgjrjgec36ac.apps.googleusercontent.com")

                .requestEmail()
                .build();
        gsi = GoogleSignIn.getClient(this, gso);


        creatAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });


    }


    public void doSomethingElse () {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        LoginActivity.this.finish();
    }


    private void signIn () {
        if (isOnline()) {

            Intent signInIntent = gsi.getSignInIntent();
            startActivityForResult(signInIntent, 1);
        } else {
            Toast.makeText(LoginActivity.this, "You are not connected to Internet", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public void onActivityResult ( int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("google sighn in", "Google sign in failed", e);
                // ...
            }
        }
    }


    @Override
    protected void onStart () {
        super.onStart();
        //removeAuthSateListner is used  in onStart function just for checking purposes,it helps in logging you out.
        // mAuth.removeAuthStateListener(mAuthListner);

    }

    @Override
    protected void onStop () {
        super.onStop();
        if (mAuthListner != null) {
            mAuth.removeAuthStateListener(mAuthListner);
        }

    }

    @Override
    public void onBackPressed () {
        LoginActivity.super.finish();
    }


    private void userSign () {
        email = emailEdittext.getText().toString().trim();
        password = passwordEdittext.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {

            emailEdittext.setError("email wrong");
            emailEdittext.requestFocus();
            return;


        } else if (TextUtils.isEmpty(password)) {
            passwordEdittext.setError("Enter the correct password");
            passwordEdittext.requestFocus();
            return;

        }

        dialog.setMessage("Loging in please wait...");
        dialog.setIndeterminate(true);
        dialog.show();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    dialog.dismiss();

                    Toast.makeText(LoginActivity.this, "Login not successfull", Toast.LENGTH_SHORT).show();

                } else {
                    //dialog.dismiss();
                    mUser=mAuth.getCurrentUser();
                    String id=mUser.getUid();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                    //Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    Toast.makeText(LoginActivity.this, "Login  successfull", Toast.LENGTH_SHORT).show();


                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(emailEdittext.getWindowToken(), 0);

                    email = emailEdittext.getText().toString();
                    password = passwordEdittext.getText().toString();
                    if (saveLoginCheckBox.isChecked()) {
                        loginPrefsEditor.putBoolean("saveLogin", true);
                        loginPrefsEditor.putString("email", email);
                        loginPrefsEditor.putString("password", password);
                        loginPrefsEditor.commit();
                    } else {
                        loginPrefsEditor.clear();
                        loginPrefsEditor.commit();
                    }

                    doSomethingElse();

                    startActivity(intent);
                    finish();



                }
            }
        });

    }


    private void firebaseAuthWithGoogle (GoogleSignInAccount acct){


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            email=emailEdittext.getText().toString();
                            password=passwordEdittext.getText().toString();



                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(emailEdittext.getWindowToken(), 0);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);



                            if (saveLoginCheckBox.isChecked()) {
                                loginPrefsEditor.putBoolean("saveLogin", true);
                                loginPrefsEditor.putString("email", email);
                                loginPrefsEditor.putString("password", password);
                                loginPrefsEditor.commit();
                            } else {
                                loginPrefsEditor.clear();
                                loginPrefsEditor.commit();
                            }

                            doSomethingElse();

                            startActivity(intent);
                            finish();




                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(LoginActivity.this, "login failed", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    //check connection
    protected boolean isOnline () {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }


    private void createAnewUser(String Email,String Password) {
        mUser=mAuth.getCurrentUser();
        String id=mUser.getUid();
        //  User user=new User(id,Name,Email,Password);
        User user=new User(id,Email,Password);


        databaseReference.child("users").child(id).setValue(user);

        //return id;
    }



}
