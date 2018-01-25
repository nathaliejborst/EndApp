package com.example.nathalie.endapp;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {


    CompactCalendarView compactCalendar;
    private ListView showTasks;
    private ArrayList<String> dateEvents = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // Get views from XML
        showTasks = (ListView) view.findViewById(R.id.usertasks_lv);


        compactCalendar = (CompactCalendarView) view.findViewById(R.id.compactcalendar_view);

        Event ev1 = new Event(Color.GREEN, 1433701251000L, "Some extra data that I want to store.");
        Event ev2 = new Event(Color.GREEN, 1516889708000L, "Dit is een taak");
        Event ev3 = new Event(Color.GREEN, 1516889708000L, "Dit ook");

        compactCalendar.addEvent(ev1);
        compactCalendar.addEvent(ev2);
        compactCalendar.addEvent(ev3);

        List<Event> events = compactCalendar.getEvents(1516889708000L);

        long currentEpoch = System.currentTimeMillis()/1000;


        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getContext();

                List<Event> events = compactCalendar.getEvents(dateClicked);

                for (int i = 0; i < events.size(); i++) {

                    Log.d("hallo Day was clicked: ", "" + dateClicked + " with events " + events.get(i).getData());
                    dateEvents.add(String.valueOf(events.get(i).getData()));
                }


                Log.d("hallo dateclicked", "" + dateClicked);

                fillSimpleListView(dateEvents);

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

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
            }
        });

    }
}
