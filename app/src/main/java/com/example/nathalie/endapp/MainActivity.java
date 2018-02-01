package com.example.nathalie.endapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DatabaseReference mDatabase;

    private Button top_button, header;
    private BottomNavigationView bottomNavigationView;

    private String currentUserID = null;
    private String currentUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user is logged in, re-direct to login screen if not
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // Get instance and referance from Firebase and access corresponding data
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Access
        accessDB();

        // Get view from XML
        top_button = (Button) findViewById(R.id.top_button);

        // Set on click listener for top button
        top_button.setOnClickListener(this);

        // Initialize bottom navigation bar
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigation();

        // Re-direct to user's profile by default
        onProfileItemClicked();
    }

    // Handles bottom navigation items
    public void bottomNavigation () {
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.groups_item:
                            // Clear backstack before opening fragment using the bottom navigation
                            getSupportFragmentManager().popBackStackImmediate();

                            onGroupsItemClicked();
                            break;
                        case R.id.calendar_item:
                            // Clear backstack before opening fragment using the bottom navigation
                            getSupportFragmentManager().popBackStackImmediate();

                            onCalendarItemClicked();
                            break;
                        case R.id.profile_item:
                            // Clear backstack before opening fragment using the bottom navigation
                            getSupportFragmentManager().popBackStackImmediate();

                            onProfileItemClicked();
                            break;
                    }
                    return false;
                }
            });
    }


    // Re-directs to show groups fragment
    public void onGroupsItemClicked () {
        // Opens groupsfragment and adds to backstack
        ShowGroupsFragment showGroupsFragment = new ShowGroupsFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.frame, showGroupsFragment).addToBackStack(null).commit();
    }

    // Re-directs to calendar fragment
    public void onCalendarItemClicked () {
        // Send user ID to next activity
        Bundle bundle = new Bundle();
        bundle.putString("userID", currentUserID);

        // Open calendar fragment and adds to backstack
        CalendarFragment calendarFragment = new CalendarFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.frame, calendarFragment).addToBackStack(null).commit();
    }

    // Re-directs to profile fragment
    public void onProfileItemClicked () {
        // Opens profile fragment and adds to backstack
        ProfileFragment profileFragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.frame, profileFragment).addToBackStack(null).commit();
    }

    // Access Firebase and get username from current user
    public void accessDB () {
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

    // Show username of currently logged in user on top of screen
    public void showCurrentUsername (DataSnapshot dataSnapshot) {
        currentUser = String.valueOf(dataSnapshot.child(currentUserID).child("username").getValue());
        top_button.setText("Welcome "  + currentUser);
    }

    @Override
    public void onClick(View view) {
        // Re-directs to user's profile on top button clicked
        switch(view.getId()) {
            case R.id.top_button:
                onProfileItemClicked();
                break;
        }
    }
}
