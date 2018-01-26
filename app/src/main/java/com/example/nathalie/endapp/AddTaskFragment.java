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

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddTaskFragment extends DialogFragment {
    CompactCalendarView compactCalendar;
    TextView currentMonth;
    Spinner frequencyS;
    LinearLayout taskFrag;
    Button confirmDate;
    String yearClicked, monthClicked, dayClicked, selectedDate;
    Task T;

    private long epoch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_task, container, false);
        getDialog().setTitle("Simple Dialog");
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
        currentMonth.append(" " + new SimpleDateFormat("YYYY").format(cal.getTime()));

        // Initialze task instance
        T = new Task();


        confirmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedDateToLong();

                // Get chosen frequency
                frequencyS.getSelectedItem().toString();
                int freq = frequencyS.getSelectedItemPosition();

                // Set chosen values
                T.setStartDate(epoch, freq);

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
                dayClicked = String.valueOf(dateClicked).substring(0, 3);
                monthClicked = String.valueOf(dateClicked).substring(4, 7);
                yearClicked = String.valueOf(dateClicked).substring(24, 28);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // Change title to visible month
                String dateSelected = String.valueOf(firstDayOfNewMonth);
                String month = dateSelected.substring(4, 7);
                String year = dateSelected.substring(24, 28);
                currentMonth.setText(month);
                currentMonth.append(" " + year);
            }
        });

        return view;
    }

    public void clickedDateToLong () {
        // Change month format to integer
        Date datemonth = null;
        try {
            Log.d("hallo month", "" + monthClicked);
            datemonth = new SimpleDateFormat("MMM").parse(monthClicked);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(datemonth);
        int month = cal.get(Calendar.MONTH);

        // Change dateformat to epoch format
        String dateString = dayClicked + "/" + month + "/" + yearClicked;
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        Log.d(" hallo date", "" + f);
        Date date = null;
        try {
            date = f.parse("01/01/2018");
            epoch = date.getTime();

            Toast.makeText(getActivity(), "you picked " + String.valueOf(epoch),
                    Toast.LENGTH_SHORT).show();

            Log.d(" hallo date", "" + epoch);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }





}
