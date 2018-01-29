package com.example.nathalie.endapp;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTaskFragment extends DialogFragment {
    private DatabaseReference mDatabase;

    CompactCalendarView compactCalendar;
    TextView currentMonth;
    Spinner frequencyS;
    LinearLayout taskFrag;
    Button confirmDate;
    String dayClicked, monthClicked, yearClicked, groupID, taskName, epochString, groupName, groupColor;
    int freq;

    private ArrayList<String> memberIDList = new ArrayList<String>();
    Task T;
    User U;

    private long epoch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_task, container, false);
//        getDialog().setTitle("Simple Dialog");

        // Get group ID from previous fragment
        groupID  = getArguments().getString("GroupID");
        taskName = getArguments().getString("taskname");
        groupName = getArguments().getString("groupname");

        // Initialize user details of current user;
        U = new User();
        U.setCurrentuser();

        // Get instance of database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get views from XML
        compactCalendar = (CompactCalendarView) view.findViewById(R.id.compactcalendar_dialog);
        currentMonth = (TextView) view.findViewById(R.id.month_datepicker_tv);
        confirmDate = (Button) view.findViewById(R.id.OK_button);
        taskFrag = (LinearLayout) view.findViewById(R.id.taskfrag);
        frequencyS = (Spinner) view.findViewById(R.id.spinner);

        // Set title to current month
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        currentMonth.setText(new SimpleDateFormat("MMM").format(cal.getTime()));
        currentMonth.append(" " + new SimpleDateFormat("yyyy").format(cal.getTime()));

        // Set date clicked to null
        dayClicked = monthClicked = yearClicked = null;

        // Initialze task instance
        T = new Task();

        // Get chosen date and frequency and add task to Firebase
        confirmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check is user selected a start date
                if (dayClicked == null || monthClicked == null || yearClicked == null) {
                    showAlert("Please choose a start date");
                    return;
                }

                // Convert selected date to long for compactcalendar format
                dateToLong();

                addTaskToDatabase();

                // 'Close' fragment
                taskFrag.setVisibility(View.GONE);
            }
        });

        // Initialize calendar
        compactCalendar.setShouldDrawDaysHeader(true);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                // Convert selected date to Strings
                dayClicked = String.valueOf(dateClicked).substring(8, 10);
                monthClicked = String.valueOf(dateClicked).substring(4, 7);
                yearClicked = String.valueOf(dateClicked).substring(30, 34);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // Change title to visible month
                String dateSelected = String.valueOf(firstDayOfNewMonth);
                String month = dateSelected.substring(4, 7);
                String year = dateSelected.substring(30, 34);
                currentMonth.setText(month);
                currentMonth.append(" " + year);
            }
        });
        return view;
    }

    public void dateToLong () {
        // Convert month from string to int
        try {
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

            // Create String from selected date
            String convertDate = dayClicked + "/" + monthClicked + "/" + yearClicked;

            // Convert String to epoch miliseconds format
            epoch = new SimpleDateFormat("dd/MM/yyyy").parse(convertDate).getTime();
        } catch (ParseException e) {
            Log.d("hallo_i", "" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addTaskToDatabase () {
        // Get chosen frequency
        frequencyS.getSelectedItem().toString();
        freq = frequencyS.getSelectedItemPosition();

        // Get groupmembers from Firebase
        mDatabase.child("groups").child(groupID).child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                            // Add groupmembers to list
                            memberIDList.add(String.valueOf(childDataSnapshot.getKey()));
                            User member = childDataSnapshot.getValue(User.class);
                        }

                        createTask();
//                        createSchedule();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
                });
    }

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

        // Add task to group and user in Firebase
        mDatabase.child("groups").child(groupID).child("tasks").child(T.taskname).setValue(T);
//        mDatabase.child("users").child(U.id).child("personal groups").child(groupID).child("tasks").child(T.taskname).setValue(T);

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
}
