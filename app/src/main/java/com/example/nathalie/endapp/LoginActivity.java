package com.example.nathalie.endapp;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = database.getReference();
    private FirebaseAuth mAuth;

    private ProgressDialog mProgress;
    private EditText email_input, password_input, name_input;
    private Button register_button, login_button, signup_button, back_login_button;
    private View login_container;
    private ImageView app_logo;

    private boolean login_visible = true;
    private String email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase instance
        mAuth = FirebaseAuth.getInstance();

        // Check if user is already logged in if so, re-direct to main activity
        if (mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        // Initialize progressdialog
        mProgress = new ProgressDialog(this);

        // Get views from XML
        email_input = (EditText) findViewById(R.id.input_email);
        password_input = (EditText) findViewById(R.id.input_password);
        name_input = (EditText) findViewById(R.id.input_name);
        login_button = (Button) findViewById(R.id.login_button);
        register_button = (Button) findViewById(R.id.register_button);
        signup_button = (Button) findViewById(R.id.signup_button);
        back_login_button = (Button) findViewById(R.id.back_to_login_button);
        login_container = (View) findViewById(R.id.login_container);
        app_logo = (ImageView) findViewById(R.id.app_logo);

        // Set on click listeners
        back_login_button.setOnClickListener(this);
        login_button.setOnClickListener(this);
        register_button.setOnClickListener(this);
        signup_button.setOnClickListener(this);
        app_logo.setOnClickListener(this);
    }

    // Logs user in using Firebase
    public void loginUser () {
        // Make sure user cannot use app when no internet connection is active
        if(!checkInternetConnection()) {
            showAlert("Please connect to the internet to use CORVEE", LoginActivity.this);
            return;
        }

        // Get text from the input fields
        email = email_input.getText().toString().trim();
        password = password_input.getText().toString().trim();

        // Make sure does not leave any fields blank
        if (checkIfBlank(email,  getApplicationContext()) || checkIfBlank(password,
                getApplicationContext())) {
            return;
        }

        mProgress.setMessage("Logging in ...");
        mProgress.show();

        // Sign user in
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    mProgress.dismiss();

                    // Re-directs to main activity if logging in is successful
                    if(task.isSuccessful()) {
                        // start app
                        goToMain();
                    } else {
                        // Catch Firebase exception in case logging in did not work
                        try {
                            throw task.getException();
                        } catch(FirebaseAuthException e) {
                            Toast.makeText(LoginActivity.this, "Failed logging in: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        } catch(FirebaseNetworkException e) {
                            Toast.makeText(LoginActivity.this, "Failed logging in: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Failed logging in: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        return;
                    }
                }
        });
    }

    // Registers user using Firebase
    public void registerUser() {
        // Make sure user cannot use app when no internet connection is active
        if(!checkInternetConnection()) {
            showAlert("Please connect to the internet to use CORVEE", getApplicationContext());
            return;
        }

        // Get text from the input fields
        email = email_input.getText().toString().trim();
        password = password_input.getText().toString().trim();

        // Make sure user can't log in when the username does not meet the requirements
        if (!checkInputRequirements(name_input.getText().toString(), getApplicationContext())) {
            return;
        }

        // Make sure does not leave any fields blank
        if (checkIfBlank(name_input.getText().toString(), getApplicationContext()) ||
                checkIfBlank(email, getApplicationContext()) || checkIfBlank(password,
                getApplicationContext())) {
            return;
        }

        mProgress.setMessage("Registering ...");
        mProgress.show();

        // Sign user in
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                mProgress.dismiss();

                // Re-directs to main activity if registering is successful
                if(task.isSuccessful()) {
                    // Add user to Firebase with chosen username
                    writeNewUser(name_input.getText().toString(), email);

                    mProgress.dismiss();

                    // start app
                    goToMain();
                } else {
                    // Catch Firebase exception in case registering in did not work
                    try {
                        throw task.getException();
                    } catch(FirebaseAuthException e) {
                        Toast.makeText(LoginActivity.this, "Failed registering: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    } catch(FirebaseNetworkException e) {
                        Toast.makeText(LoginActivity.this, "Failed registering: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this, "Failed registering: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                }
        });
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
    static boolean checkIfBlank(String input, Context context) {
        // Checks does not leave a input blank
        if (input.equals("") || input == null || input.matches("")) {
            showAlert("Please fill in blank fields", context);
            return true;
        }
        return false;
    }

    // Re-direct to main activity
    public void goToMain (){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
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

    // Returns false if user does not meet input requirements
    static boolean checkInputRequirements (String name, Context context) {
        // Make sure user does not enter characters Firebase can't handle
        if(name.contains(".") || name.contains("#") || name.contains("$") || name.contains("[") || name.contains("]")) {
            showAlert("Characters '.', '#', '$', '[', or ']' are not allowed", context);
            return false;
        }
        return true;
    }

    // Shows an alert with given alert to user
    static void showAlert (String alert, Context context) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(alert);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.app_logo:
                // Re-direct to easter egg when clicking on the app logo
                Intent intent = new Intent(LoginActivity.this, EasterActivity.class);
                startActivity(intent);
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

    // Make sure user can't acces app by pressing back after logging out
    @Override
    public void onBackPressed() {
    }
}
