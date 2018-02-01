package com.example.nathalie.endapp;


import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddTaskFragment extends DialogFragment implements View.OnClickListener {
    private DatabaseReference mDatabase;

    private CompactCalendarView compactCalendar;
    private TextView currentMonth;
    private Spinner frequencyS;
    private LinearLayout taskFrag;
    private Button confirmDate;
    private String monthClicked, groupID, taskName, groupName, groupColor;
    private int freq;
    private long epoch = 0;
    private ArrayList<String> memberIDList = new ArrayList<String>();

    Task T;
    User U;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_task, container, false);

        // Get group ID, groupname and taskname from previous fragment
        groupID  = getArguments().getString("GroupID");
        taskName = getArguments().getString("taskname");
        groupName = getArguments().getString("groupname");

        // Initialize user details of current user;
        U = new User();
        U.setCurrentuser();

        // Initialze task instance
        T = new Task();

        // Get instance of database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get views from XML
        compactCalendar = (CompactCalendarView) view.findViewById(R.id.compactcalendar_dialog);
        currentMonth = (TextView) view.findViewById(R.id.month_datepicker_tv);
        confirmDate = (Button) view.findViewById(R.id.OK_button);
        taskFrag = (LinearLayout) view.findViewById(R.id.taskfrag);
        frequencyS = (Spinner) view.findViewById(R.id.spinner);

        // Set title of calendar to current month
        Calendar cal = Calendar.getInstance();
//        final Date date = new Date();
        currentMonth.setText(new SimpleDateFormat("MMM").format(cal.getTime()));
        currentMonth.append(" " + new SimpleDateFormat("yyyy").format(cal.getTime()));

        // Set on click listener
        confirmDate.setOnClickListener(this);

//        // Get chosen date and frequency and add task to Firebase
//        confirmDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Check is user selected a start date
//                if (epoch == 0) {
//                    showAlert("Please choose a start date");
//                    return;
//                }
//
//                // Convert selected date to long for compactcalendar format
//                dateToLong();
//
//                addTaskToDatabase();
//
//                // 'Close' fragment
//                taskFrag.setVisibility(View.GONE);
//            }
//        });

        // Initialize calendar
        compactCalendar.setShouldDrawDaysHeader(true);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        // Add listeners to calendar
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                // Get clicked date in milliseconds
                epoch = dateClicked.getTime();

                // Get the clicked month
                monthClicked = String.valueOf(dateClicked.getMonth());
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // Get current month and change title to visible month and year
                String dateSelected = String.valueOf(firstDayOfNewMonth);
                String month = dateSelected.substring(4, 7);
                String year = String.valueOf(firstDayOfNewMonth.getYear());
                currentMonth.setText(month);
                currentMonth.append(" " + year);
            }
        });
        return view;
    }

    // Converts month format
    public void monthFormat () {
        // Convert month from string to int
        try {
            // Convert month using the simple date format
            Date x = new SimpleDateFormat("MMM").parse(monthClicked);
            Calendar c = Calendar.getInstance();
            c.setTime(x);
            int monthInt = c.get(Calendar.MONTH);

            // Plus 1 because compact calendar handles January as the 0'th month
            if (monthInt < 10) {
                monthClicked = "0" + String.valueOf(monthInt + 1);
            } else {
                monthClicked = String.valueOf(monthInt + 1);
            }
        } catch (ParseException e) {
            Log.d("hallo_i", "" + e.getMessage());
            e.printStackTrace();
        }
    }

    // Adds task with chosen name, frequency and startdate to Firebase
    public void addTaskToDatabase () {
        // Get chosen frequency
        frequencyS.getSelectedItem().toString();
        freq = frequencyS.getSelectedItemPosition();

        // Access Firebase
        mDatabase.child("groups").child(groupID).
            addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get groupcolor from group
                    groupColor = String.valueOf(dataSnapshot.child("color").getValue());

                    // Clear member ID list before filling it
                    memberIDList.clear();

                    // Loop over members of group
                    for (DataSnapshot childDataSnapshot : dataSnapshot.child("users").getChildren()) {
                        // Get member and add to list
                        User member = childDataSnapshot.getValue(User.class);
                        memberIDList.add(member.id);
                    }

                    // Add task to Firebase
                    createTask();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                }
            });



//        // Get groupmembers from Firebase
//        mDatabase.child("groups").child(groupID).child("users")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
//
//                            // Add groupmembers to list
//                            memberIDList.add(String.valueOf(childDataSnapshot.getKey()));
//                            User member = childDataSnapshot.getValue(User.class);
//                        }
//
//                        createTask();
////                        createSchedule();
//                    }
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
//                    }
//                });
    }

    // Adds task to every user and corresponding group in Firebase
    public void createTask () {
        // classify members randomly in schedule
        Collections.shuffle(memberIDList);

        // Create task
        T.taskname = taskName;
        T.frequency = freq;
        T.startdate = String.valueOf(epoch);
        T.groupid = groupID;
        T.schedule = memberIDList;
        T.groupname = groupName;
        T.groupcolor = groupColor;

        // Add task to group in Firebase
        mDatabase.child("groups").child(groupID).child("tasks").child(T.taskname).setValue(T);

        // Add ask to every group members child in Firebase
        for (int i = 0; i < memberIDList.size(); i++) {
            mDatabase.child("users").child(memberIDList.get(i)).child("personal groups").child(groupID).child("tasks").child(T.taskname).setValue(T);
        }
    }

    public void showAlert (String alert) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
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
        // Check is user selected a start date
        if (epoch == 0) {
            LoginActivity.showAlert("Please choose a start date", getActivity());
            return;
        }

        // Convert selected month to a usable format
        monthFormat();

        // Add task to Firebase
        addTaskToDatabase();

        // 'Close' fragment
        taskFrag.setVisibility(View.GONE);
    }
}
