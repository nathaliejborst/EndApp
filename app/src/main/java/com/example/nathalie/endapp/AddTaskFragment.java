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
    String yearClicked, monthClicked, dayClicked, selectedDate, groupID, taskName, epochString, groupName;
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
        getDialog().setTitle("Simple Dialog");

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

        // Initialze task instance
        T = new Task();

        // Get chosen date and frequency and add task to Firebase
        confirmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                clickedDateToLong();

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
                dayClicked = String.valueOf(dateClicked).substring(8, 10);
                monthClicked = String.valueOf(dateClicked).substring(4, 7);
                yearClicked = String.valueOf(dateClicked).substring(30, 34);

                Log.d("hallo clicked", "" + dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // Change title to visible month
                String dateSelected = String.valueOf(firstDayOfNewMonth);
                Log.d("hallo scroll", "" + dateSelected);
                String month = dateSelected.substring(4, 7);
                String year = dateSelected.substring(30, 34);
                Log.d("hallo year", "" + year);
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
            String monthNumber;

            // Plus 1 because compact calendar handles January as the 0'th month
            if (monthInt < 10) {
                monthNumber = "0" + String.valueOf(monthInt + 1);
            } else {
                monthNumber = String.valueOf(monthInt + 1);
            }

            Log.d("hallo month int?", "" + monthInt);

            // Create String from selected date
            String convertDate = dayClicked + "/" + monthNumber + "/" + yearClicked;
            // Convert String to epoch miliseconds format
            epoch = new SimpleDateFormat("dd/MM/yyyy").parse(convertDate).getTime();


            Log.d("hallo month string?", "" + convertDate);
            Log.d("hallo month epoch?", "" + epoch);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void addTaskToDatabase () {
        // Get chosen frequency
        frequencyS.getSelectedItem().toString();
        freq = frequencyS.getSelectedItemPosition();

        Log.d("hallo task", "freq: " + T.frequency + " epoch: " + T.startdate + " groupid: " + T.groupid);

        // Get groupmembers from Firebase
        mDatabase.child("groups").child(groupID).child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            // Add groupmembers to list
                            memberIDList.add(String.valueOf(childDataSnapshot.getKey()));
                        }
                        createTask();
                        createSchedule();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
                });






    }

    public void createSchedule () {

        Date epochDate = new Date( epoch );
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(epochDate);
        calendar.add(Calendar.MONTH, +1);
        Date result = calendar.getTime();
        Log.d("hallo result?", "" + result);

    }

    public void createTask () {

        Log.d("hallo unshuf", "" + String.valueOf(memberIDList));



        // classify members randomly in schedule
        Collections.shuffle(memberIDList);

        Log.d("hallo shuf????", "" + String.valueOf(memberIDList));

        // Create task
        T.taskname = taskName;
        T.frequency = freq;
        T.startdate = String.valueOf(epoch);
        T.groupid = groupID;
        T.schedule = memberIDList;
        T.groupname = groupName;

        Log.d("hallo T", "" + String.valueOf(T.startdate) + "   name: " + String.valueOf(taskName));


        // Add task to group and user in Firebase
        mDatabase.child("groups").child(groupID).child("tasks").child(T.taskname).setValue(T);
        mDatabase.child("users").child(U.currentUserID).child("personal groups").child(groupID).child("tasks").child(T.taskname).setValue(T);

    }






}
