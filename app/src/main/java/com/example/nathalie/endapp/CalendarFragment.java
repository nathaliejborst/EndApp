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
    private ArrayList<String> dateEvents = new ArrayList<String>();
    private ArrayList<String> tasksList = new ArrayList<String>();

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

        Long y = 1519772400000L;


        Event ev1 = new Event(Color.WHITE, 1433701251000L, "Some extra data that I want to store.");
        Event ev2 = new Event(Color.WHITE, 1516889708000L, "Dit is een taak");
        Event ev3 = new Event(Color.WHITE, 1516889708000L, "Dit ook");
        Event ev4 = new Event(Color.WHITE, y, "Nog een");

        compactCalendar.addEvent(ev1);
        compactCalendar.addEvent(ev2);
        compactCalendar.addEvent(ev3);

        List<Event> events = compactCalendar.getEvents(1516889708000L);

        final long currentEpoch = System.currentTimeMillis()/1000;


        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getContext();

                // Clear list otherwise tasks will be shown multiple times if date is clicked more than once
                dateEvents.clear();

                Log.d("hallo wekrt het?", "" + dateClicked.getMonth());

                List<Event> events = compactCalendar.getEvents(dateClicked);

                for (int i = 0; i < events.size(); i++) {

                    Log.d("hallo Day was clicked: ", "" + dateClicked.getMonth() + " with events " + events.get(i).getData());
                    dateEvents.add(String.valueOf(events.get(i).getData()));
                }
                fillSimpleListView(dateEvents);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // Change
                String dateSelected = String.valueOf(firstDayOfNewMonth);
                String month = dateSelected.substring(4, 7);
                String year = dateSelected.substring(30, 34);
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

    public void maandlater (long z, String groupname, String taskname) {

        Date xx = new Date(z);
        Calendar c = Calendar.getInstance();
        c.setTime(xx);
        c.add(Calendar.MONTH, +1);
        Date result = c.getTime();
        Long l = c.getTimeInMillis();

        Log.d("hallo long millis: ", "" + l + "  actual date: " + result + "   size:    " + dateEvents.size());


//        Date epochDate = new Date(z);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(epochDate);
//        calendar.add(Calendar.MONTH, +1);
//        Date result = calendar.getTime();
//        Log.d("hallo result?", "" + result);

            Event x = new Event(Color.BLUE, l, groupname +"2:   " + taskname + "2");

            compactCalendar.addEvent(x);
    }


    public void getTasksFromDatabase () {
        mDatabase.child("users").child(U.currentUserID).child("personal groups")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Clear list before adding tasks
                        tasksList.clear();

                        // Get details for every group of user
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                            Log.d("hallo group name??? ", "" + String.valueOf(childDataSnapshot.getKey()));
                            Log.d("hallo group tasks???? ", "" + String.valueOf(childDataSnapshot.child("tasks")));

                            // child1: value = groupname, child2: task.class
                            for (DataSnapshot IDDataSnapshot : childDataSnapshot.child("tasks").getChildren()) {
                                Log.d("hallo group NAME ", "" + String.valueOf(IDDataSnapshot.getKey()));
                                Log.d("hallo group COUNT ", "" + String.valueOf(IDDataSnapshot.getChildrenCount()));

                                Task t = IDDataSnapshot.getValue(Task.class);
                                Log.d("halloXXXXX ", "" + String.valueOf(t.startdate) + "freq:  " + String.valueOf(t.frequency));


                                long l = Long.parseLong(t.startdate);

                                Event ev4 = new Event(Color.GREEN, l, t.groupname +":   " + t.taskname);

                                compactCalendar.addEvent(ev4);

                                maandlater(l, t.groupname, t.taskname);




                            }


                            // Add task names to list
//                            tasksList.add(String.valueOf(childDataSnapshot.getKey()));
//                            Log.d("hallo TASKS ", "" + String.valueOf(childDataSnapshot.child("tasks").child("1")));
//                            Log.d("hallo SCHED ", "" + String.valueOf(childDataSnapshot.child("tasks").child("schedule")));
//                            Log.d("hallo COUNT ", "" + String.valueOf(childDataSnapshot.child("tasks").getChildrenCount()));
//                            Log.d("hallo CHILDREN ", "" + String.valueOf(childDataSnapshot.child("tasks").getChildren()));
//                            Log.d("hallo NAME ", "" + String.valueOf(childDataSnapshot.child("tasks").getValue()));



                            // Get details for every task of group
                            for (DataSnapshot taskDataSnapshot : childDataSnapshot.getChildren()) {
//                                Log.d("hallo group name??? ", "" + String.valueOf(taskDataSnapshot.child("groupname").getValue()));
//
//                                Log.d("hallo group name??? ", "" + String.valueOf(taskDataSnapshot.child("tasks")));



                            }





                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
                });

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

}
