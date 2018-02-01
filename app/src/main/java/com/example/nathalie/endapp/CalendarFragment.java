package com.example.nathalie.endapp;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {
    private DatabaseReference mDatabase;

    private CompactCalendarView compactCalendar;
    private ListView showTasks;
    private TextView currentMonth;
    private ArrayList<String> dayEvents = new ArrayList<String>();
    private ArrayList<String> tasksList = new ArrayList<String>();
    private ArrayList<Task> mTasksList = new ArrayList<Task>();

    User U;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Get views from XML
        showTasks = (ListView) view.findViewById(R.id.usertasks_lv);
        currentMonth = (TextView) view.findViewById(R.id.month_tv);
        compactCalendar = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);

        // Get instance of database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize user details of current user;
        U = new User();
        U.setCurrentuser();

        // Set title of calendar to current month
        Calendar cal = Calendar.getInstance();
//        Date date = new Date();
        currentMonth.setText(new SimpleDateFormat("MMM").format(cal.getTime()));
        currentMonth.append(" " + new SimpleDateFormat("yyyy").format(cal.getTime()));

        // Initialize compact calendar
        compactCalendar.setShouldDrawDaysHeader(true);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        // Connect with database
        getTasksFromDatabase();

        // Set listeners on calendar
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                // Clear list otherwise tasks will be shown multiple times if date is clicked more
                // than once
                dayEvents.clear();
                mTasksList.clear();

                // Get events from selected date and add to list
                List<Event> events = compactCalendar.getEvents(dateClicked);
                for (int i = 0; i < events.size(); i++) {
                    dayEvents.add(String.valueOf(events.get(i).getData()));
                    Object O = events.get(i).getData();
                    Task T = (Task) O;

                    // Add every task on selected day to list
                    mTasksList.add(T);
                }

                // Show tasks of clicked day in custom listview
                fillTasksListview();
            }
            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // Get month and year from selected month
                String dateSelected = String.valueOf(firstDayOfNewMonth);
                String month = dateSelected.substring(4, 7);
                String year;

                // Get timezone from device
                String tz = TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);

                // String value of date goes out of bound when timezone is EST
                if (tz.equals("EST")) {
                    year = dateSelected.substring(24, 28);

                } else {
                    year = dateSelected.substring(30, 34);
                }

                // Change title of calendar to currently visible month and year
                currentMonth.setText(month);
                currentMonth.append(" " + year);
            }
        });
        return view;


    }
//
//    public void fillSimpleListView(final ArrayList list) {
//        ListAdapter theAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
//
//        // Set the adapter
//        showTasks.setAdapter(theAdapter);
//        showTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
//                String eventData = showTasks.getItemAtPosition(i).toString();
//
//                String groupName = eventData.substring(eventData.indexOf("(")+1, eventData.indexOf(")"));
//                Log.d("hallo grouname clicked", "" + groupName);
//
////                showTaskDescription("bla bla bla bla bla beschrijving bla bla bla bla");
//            }
//        });
//    }

    // Get user's tasks from Firebase and create events on calendar
    public void getTasksFromDatabase () {
        // Acess Firebase
        mDatabase.child("users").child(U.id).child("personal groups")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Clear list before adding tasks
                    tasksList.clear();

                    // Get details for every group of user
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        //Loop over every task of group
                        for (DataSnapshot taskDataSnapshot : childDataSnapshot.child("tasks").getChildren()) {
                            // Retreive task from Firebase
                            Task T = taskDataSnapshot.getValue(Task.class);
                            // Add events and create schedule using startdate and frequency of
                            // task
                            createSchedule(T);
//                                long l = Long.parseLong(T.startdate);

//                                ArrayList<String> schedule = T.schedule;
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                }
            });
    }

    // Create a schedule for every task given the frequency, startdate and amount of groupmembers
    public void createSchedule (Task T) {
        // By default a task gets repeated a 100 times per user
        int repetition = 100;
        int members = T.schedule.size();
        int position = 0;

        // Check user's position in schedule
        for (int i = 0; i < members; i++) {
            if(T.schedule.get(i).equals(U.id)) {
                position = i;
            }
        }

        // Convert startdate back to long and set calendar to that date
        long l = Long.parseLong(T.startdate);
        Date date = new Date(l);
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        // Check frequency and get startdate of task for user
        switch (T.frequency) {
            // Daily task
            case 0:
                // Get date for first task and add to calendar
                c.add(Calendar.DAY_OF_MONTH, + position);
                addEvent(c, T);

                // Add task according to schedule to calendar
                for (int i = 0; i < repetition; i++) {
                    c.add(Calendar.DAY_OF_MONTH, + members);
                    addEvent(c, T);
                }
                break;
            // Weekly task
            case 1:
                // Get date for first task and add to calendar
                c.add(Calendar.WEEK_OF_MONTH, + position);
                addEvent(c, T);

                // Add task according to schedule to calendar
                for (int i = 0; i < repetition; i++) {
                    c.add(Calendar.WEEK_OF_MONTH, + members);
                    addEvent(c, T);
                }
                break;
            // Monthly task
            case 2:
                // Get date for first task and add to calendar
                c.add(Calendar.MONTH, + position);
                addEvent(c, T);

                // Add task according to schedule to calendar
                for (int i = 0; i < repetition; i++) {
                    c.add(Calendar.MONTH, + members);
                    addEvent(c, T);
                }
                break;
        }
    }

    // Create an event for every task and add to calendar
    public void addEvent (Calendar c, Task T) {
        Long taskDate = c.getTimeInMillis();

        // Add task to calendar with the the groupcolor as marker color
        Event event = new Event(Color.rgb(255, 255, 255), taskDate, T);
        switch(T.groupcolor) {
            case "magenta":
                event = new Event(Color.rgb(186, 85, 211), taskDate, T);
                break;
            case "cyan":
                event = new Event(Color.rgb(144, 238, 144), taskDate, T);
                break;
            case "yellow":
                event = new Event(Color.rgb(255, 215, 0), taskDate, T);
                break;
            case "blue":
                event = new Event(Color.rgb(65, 105, 225), taskDate, T);
                break;
            case "red":
                event = new Event(Color.rgb(255, 69, 0), taskDate, T);
                break;
        }
        // Add event to calendar
        compactCalendar.addEvent(event);
    }

    // Fill listview with tasks of selected day
    public void fillTasksListview () {
        // Set adapter for listview
        CalendarTaskAdapter cAdapter= new CalendarTaskAdapter(getContext(), mTasksList);
        showTasks.setAdapter(cAdapter);

        showTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Show group name on click
                LoginActivity.showAlert(view.getTag().toString(), getActivity());
            }
        });
    }
}
