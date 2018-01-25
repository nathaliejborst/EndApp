package com.example.nathalie.endapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.app.DatePickerDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Button top_button;
    BottomNavigationView bottomNavigationView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String currentUserID = null;
    private String currentUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user is logged in, re-direct to login screen if not
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.d("hallo null??", "");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // Get instance and referance from Firebase and access corresponding data
        mAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        accesDB();

        // Get views from XML
        top_button = (Button) findViewById(R.id.top_button);

        // Initialize bottom navigation bar
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigation();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_nav_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.groups_item:
                Toast.makeText(this, "Groups", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.tasks_item:
                Toast.makeText(this, "Tasks", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void bottomNavigation () {
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.groups_item2:
                                onGroupsItemClicked();
                                break;
                            case R.id.calendar_item2:
                                onCalendarItemClicked();
                                break;
                            case R.id.profile_item2:
                                onProfileItemClicked();
                                break;
                        }
                        return false;
                    }
                });
    }


    // Open groups fragment
    public void onGroupsItemClicked () {

        // Send user ID to next activity
        Bundle bundle = new Bundle();
        bundle.putString("userID", currentUserID);

        GroupnameFragment groupnameFragment = new GroupnameFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.frame, groupnameFragment).commit();
    }

    public void onCalendarItemClicked () {

        DialogFragment datePicker = new CalenderDialog();
        datePicker.show(getSupportFragmentManager(), "date picker");

        // Send user ID to next activity
        Bundle bundle = new Bundle();
        bundle.putString("userID", currentUserID);

        CalendarFragment calendarFragment = new CalendarFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.frame, calendarFragment).commit();



        // Start new activity to open calendar
//        Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
//        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        Toast.makeText(MainActivity.this, currentDate, Toast.LENGTH_SHORT).show();

    }

    public void onProfileItemClicked () {
        ProfileFragment profileFragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.frame, profileFragment).commit();

    }

    public void accesDB () {
        mDatabase.child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        showCurrentUsername(dataSnapshot);

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
                });
    }

    public void showCurrentUsername (DataSnapshot dataSnapshot) {
//        currentUserID = dataSnapshot.child("users").child(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid())).getKey();

        currentUser = String.valueOf(dataSnapshot.child(currentUserID).child("username").getValue());
        Log.w("hallo_i", "name " + dataSnapshot.child(currentUserID).child("username").getValue());
        Log.w("hallo_i", "id " + currentUserID);
        top_button.setText("Welcome "  + currentUser);
    }

    public void getGroups (DataSnapshot dataSnapshot) {

        // Check if user already is in at least one group
        if (dataSnapshot.child(currentUserID).getChildrenCount() == 3) {
//                            ShowGroupsFragment showGroupFragment = new ShowGroupsFragment();
//                            getSupportFragmentManager().beginTransaction().
//                                    replace(R.id.frame, showGroupFragment).commit();

        } else {
            GroupnameFragment groupnameFragment = new GroupnameFragment();
            getSupportFragmentManager().beginTransaction().
                    replace(R.id.frame, groupnameFragment).commit();
        }

    }

}
