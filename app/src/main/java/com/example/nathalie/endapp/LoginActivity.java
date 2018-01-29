package com.example.nathalie.endapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog mProgress;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private boolean login_visible = true;
    private String email, password, getEmail, getPassword;

    private EditText email_input, password_input, name_input;
    private Button register_button, login_button, signup_button, back_login_button;
    private View login_container;
    private ImageView app_logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Initialize Firebase instance
        mAuth = FirebaseAuth.getInstance();


        // Check if user is already logged in
        if (mAuth.getCurrentUser() != null) {
            // start app
        }

        // Initialize progressdialog
        mProgress = new ProgressDialog(this);

        // Initialize views from XML
        email_input = (EditText) findViewById(R.id.input_email);
        password_input = (EditText) findViewById(R.id.input_password);
        name_input = (EditText) findViewById(R.id.input_name);
        login_button = (Button) findViewById(R.id.login_button);
        register_button = (Button) findViewById(R.id.register_button);
        signup_button = (Button) findViewById(R.id.signup_button);
        back_login_button = (Button) findViewById(R.id.back_to_login_button);
        login_container = (View) findViewById(R.id.login_container);

        app_logo = (ImageView) findViewById(R.id.app_logo);

        // Get the text from the input fields
        email = email_input.getText().toString().trim();
        password = password_input.getText().toString().trim();

        // Set on click listeners
        back_login_button.setOnClickListener(this);
        login_button.setOnClickListener(this);
        register_button.setOnClickListener(this);
        signup_button.setOnClickListener(this);
        app_logo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.app_logo:
                goToMain();
                break;
            case R.id.login_button:
                loginUser();
                break;
            case R.id.register_button:
                registerUser();
                break;
            case R.id.signup_button:
                signupUser();
                break;
            case R.id.back_to_login_button:
                signupUser();
        }
    }

    public void loginUser () {
        if (!checkIfEmpty()) {

            // Make sure user cannot use app when no internet connection is active
            if(!checkInternetConnection()) {
                showAlert("Please connect to the internet to use CORVEE");
                return;
            }

            mProgress.setMessage("Logging in ...");
            mProgress.show();

            // Sign user in
            mAuth.signInWithEmailAndPassword(getEmail, getPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgress.dismiss();
                            // Re-directs to main activity if logging in is successful
                            if(task.isSuccessful()) {

                                // start app
                                goToMain();

                            } else {
                                FirebaseAuthException e = (FirebaseAuthException )task.getException();
                                Toast.makeText(LoginActivity.this, "Failed logging in", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });

        } else {        // display a progress dialog if email and password are not empty
            Toast.makeText(LoginActivity.this, "Failed logging in", Toast.LENGTH_SHORT).show();
        }
    }
    public void registerUser() {
        if (checkNameRequirements(name_input.getText().toString())) {

            // Get e-mail and password from user
            getEmail = email_input.getText().toString().trim();
            getPassword = password_input.getText().toString().trim();

            // Make sure user cannot use app when no internet connection is active
            if(!checkInternetConnection()) {
                showAlert("Please connect to the internet to use CORVEE");
                return;
            }

            mProgress.setMessage("Registering ...");
            mProgress.show();


            // Sign user in
            mAuth.createUserWithEmailAndPassword(getEmail, getPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgress.dismiss();

                            // Re-directs to main activity if registering is successful
                            if(task.isSuccessful()) {
                                // Add user to Firebase with own name
                                writeNewUser(name_input.getText().toString(), getEmail );

                                mProgress.dismiss();

                                // start app
                                goToMain();

                            } else {
                                FirebaseAuthException e = (FirebaseAuthException )task.getException();
                                Log.w("hallo_i", "onCancelled: " + e.getMessage());
                                Toast.makeText(LoginActivity.this, "Failed registering: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    });

        } else {
            Toast.makeText(LoginActivity.this, "Please enter valid username", Toast.LENGTH_SHORT).show();
        }

    }

    // Add new user to database
    private void writeNewUser(String name, String email) {
        // Get unique userID from Firebase
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        User user = new User(name, email);
        mDatabase.child("users").child(userID).setValue(user);
    }

    // Changes view visibility in order for unregistered users to sign up
    public void signupUser () {

        // Checks if login details are visible and adjusts visibility
        if (login_visible) {
            login_button.setVisibility(View.INVISIBLE);
            login_container.setVisibility(View.INVISIBLE);

            register_button.setVisibility(View.VISIBLE);
            back_login_button.setVisibility(View.VISIBLE);
            name_input.setVisibility(View.VISIBLE);
            name_input.setHint("Name");

            // Set login details to invisible
            login_visible = !login_visible;

        } else {
            login_button.setVisibility(View.VISIBLE);
            login_container.setVisibility(View.VISIBLE);

            register_button.setVisibility(View.INVISIBLE);
            back_login_button.setVisibility(View.INVISIBLE);
            name_input.setVisibility(View.INVISIBLE);
            name_input.setHint("");

            // Set login details to visible
            login_visible = true;
        }
    }

    // Checks if input fields are filled in
    public boolean checkIfEmpty() {

        getEmail = email_input.getText().toString().trim();
        getPassword = password_input.getText().toString().trim();

        if (TextUtils.isEmpty(getEmail)) {
            Toast.makeText(this, "Please enter an e-mail address", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (TextUtils.isEmpty(getPassword)) {
            Toast.makeText(this, "Please enter an e-mail password", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    // Re-direct to main activity
    public void goToMain (){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("hallo null??", "");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    // Returns true if phone is connected to the internet
    public boolean checkInternetConnection (){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        return false;
    }

    // Checks if user fills in a username and does not user invalid characters
    public boolean checkNameRequirements (String name) {
        Log.d("hallo x", "in boolean: " + name);

        // Make sure user does not enter characters Firebase can't handle
        if(name.contains(".") || name.contains("#") || name.contains("$") || name.contains("[") || name.contains("]")) {
            showAlert("Username can't contain '.', '#', '$', '[', or ']'");
            return false;
        }
        // Checks does not enter a blank group name
        if (name.equals("") || name == null || name.matches("")) {
            showAlert("Please fill in a username");
            return false;
        }
        return true;
    }

    public void showAlert (String alert) {
        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setTitle(alert);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    // Make sure user can't acces app by pressing back after logging out
    @Override
    public void onBackPressed()
    {
    }
}
