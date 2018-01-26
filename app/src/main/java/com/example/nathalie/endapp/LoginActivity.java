package com.example.nathalie.endapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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

        // Hide bottom navigation bar
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

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
        if (!checkIfEmpty()) {

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
                                Toast.makeText(LoginActivity.this, "Failed registering", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });

        } else {        // display a progress dialog if email and password are not empty
            Toast.makeText(LoginActivity.this, "Failed registering", Toast.LENGTH_SHORT).show();
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

    // Re-direct to main activity and remove from backstack
    public void goToMain (){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("hallo null??", "");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
}
