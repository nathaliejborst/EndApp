package com.example.nathalie.endapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;
import com.roughike.bottombar.OnTabClickListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button open_calendar;
    BottomNavigationView bottomNavigationView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get instance and referance from Firebase
        mAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        open_calendar = (Button) findViewById(R.id.calendar_button);
        open_calendar.setOnClickListener(this);

        // Initialize bottom navigation bar
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNavigation();

        // Get user's name from Firebase
//        getFromDB();


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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.calendar_button:
                Intent intent = new Intent(getApplicationContext(), CalendarActivity.class);
                startActivity(intent);
                break;

        }
    }

    public void onGroupsItemClicked () {
        Toast.makeText(MainActivity.this, "Groups_bottom_bar", Toast.LENGTH_SHORT).show();

        GroupsFragment groupsFragment = new GroupsFragment();
        getSupportFragmentManager().beginTransaction().
                replace(R.id.activity_main, groupsFragment).commit();

    }

    public void getFromDB () {

        ValueEventListener postListener = new ValueEventListener() {

            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                User aUser = dataSnapshot.child("users").child(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid())).getValue(User.class);
                String username = String.valueOf(aUser.username);
            }



            @Override

            public void onCancelled(DatabaseError databaseError) {


            }
        };
        mDatabase.addValueEventListener(postListener);
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
                            case R.id.tasks_item2:
                                Toast.makeText(MainActivity.this, "Tasks_bottom_bar", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });

    }




}
