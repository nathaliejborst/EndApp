package com.example.nathalie.endapp;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static java.util.Calendar.MILLISECOND;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {
    private DatabaseReference mDatabase;



    CompactCalendarView compactCalendar;
    private ListView showTasks;
    private TextView currentMonth;
    private Toolbar toolbar;
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

        // Get instance of database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize user details of current user;
        U = new User();
        U.setCurrentuser();

        // Set title to current month
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        currentMonth.setText(new SimpleDateFormat("MMM").format(cal.getTime()));
        currentMonth.append(" " + new SimpleDateFormat("yyyy").format(cal.getTime()));


        compactCalendar = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);
        compactCalendar.setShouldDrawDaysHeader(true);

        compactCalendar.setUseThreeLetterAbbreviation(true);

        // Connect with database
        getTasksFromDatabase();

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                // Clear list otherwise tasks will be shown multiple times if date is clicked more than once
                dayEvents.clear();

                // Get events on selected date and add to String list
                List<Event> events = compactCalendar.getEvents(dateClicked);
                for (int i = 0; i < events.size(); i++) {
                    dayEvents.add(String.valueOf(events.get(i).getData()));
                }
                fillSimpleListView(dayEvents);
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

    public void fillSimpleListView(final ArrayList list) {
        ListAdapter theAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

        // Set the adapter
        showTasks.setAdapter(theAdapter);
        showTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                showTaskDescription("bla bla bla bla bla beschrijving bla bla bla bla");
            }
        });
    }

    public void getTasksFromDatabase () {
        mDatabase.child("users").child(U.id).child("personal groups")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Clear list before adding tasks
                        tasksList.clear();

                        // Get details for every group of user
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            // Get task data from group
                            for (DataSnapshot taskDataSnapshot : childDataSnapshot.child("tasks").getChildren()) {
                                // Retreive task from Firebase
                                Task T = taskDataSnapshot.getValue(Task.class);
                                long l = Long.parseLong(T.startdate);


                                ArrayList<String> schedule = T.schedule;

                                createSchedule(T);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
                });

    }

    public void createSchedule (Task T) {
        // By default a task gets repeated a 100 times per user
        int repetition = 100;
        int members = T.schedule.size();
        int position = 0;

        // Check user's position in schedule
        for (int i = 0; i < members; i++) {
            if(T.schedule.get(i).equals(U.id)) {
                position = i;
                Log.d("hallo, POSITION: ", "" + position);
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

    public void addEvent (Calendar c, Task T) {
        Date result = c.getTime();
        Long taskDate = c.getTimeInMillis();
        String color = checkColor(T);

        // Add task to calendar
        Event event = new Event(Color.parseColor("#90EE90"), taskDate, T.taskname +"     (" + T.groupname + ")");
        compactCalendar.addEvent(event);
    }


    public void showTaskDescription (String alert) {
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

    public String checkColor (Task T) {
        String color = "#FFFFFF";
        switch(T.groupcolor) {
            case "magenta":
                color = "#BA55D3";
                Log.d("hallo magenta", "");
                break;
            case "cyan":
                color = "#90EE90";
                Log.d("hallo cyan", "");
                break;
            case "yellow":
                color = "#FFD700";
                Log.d("hallo yellow", "");
                break;
            case "blue":
                color = "#4169E1";
                Log.d("hallo blue", "");
                break;
            case "red":
                color = "#FF4500";
                Log.d("hallo red", "");
                break;
        }
        Log.d("hallo TASK COLOR", "" + color + "    from T: " + T.groupcolor + ", " + T.groupname);




//        String color = T.groupcolor;
//        switch(color) {
//            case "magenta":
//                event = new Event(Color.parseColor("#BA55D3"), taskDate, T.taskname +"     (" + T.groupname + ")");
////                color = "#BA55D3";
//                Log.d("hallo magenta", "");
//                break;
//            case "cyan":
//                event = new Event(Color.parseColor("#90EE90"), taskDate, T.taskname +"     (" + T.groupname + ")");
//
////                color = "#90EE90";
//                Log.d("hallo cyan", "");
//                break;
//            case "yellow":
//                event = new Event(Color.parseColor("#FFD700"), taskDate, T.taskname +"     (" + T.groupname + ")");
//
////                color = "#FFD700";
//                Log.d("hallo yellow", "");
//                break;
//            case "blue":
//                event = new Event(Color.parseColor("#4169E1"), taskDate, T.taskname +"     (" + T.groupname + ")");
//
////                color = "#4169E1";
//                Log.d("hallo blue", "");
//                break;
//            case "red":
//                event = new Event(Color.parseColor("#FF4500"), taskDate, T.taskname +"     (" + T.groupname + ")");
//
//                Log.d("hallo red", "");
//                break;
//        }
        return color;
    }

}
