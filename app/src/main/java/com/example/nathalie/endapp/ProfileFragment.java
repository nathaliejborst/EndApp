package com.example.nathalie.endapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.roomorama.caldroid.CaldroidFragment;

import java.util.Calendar;
import java.util.Date;


public class ProfileFragment extends Fragment {
    private Button logOutButton, tasksButton;
    TextView lineTasks;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Get views from XML
        logOutButton = (Button) view.findViewById(R.id.log_out_button);
        tasksButton = (Button) view.findViewById(R.id.tasks_button);
        lineTasks = (TextView) view.findViewById(R.id.tasks_line);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            }
        });

        tasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            tasksButton.setTextColor(Color.parseColor("#66B2FF"));
            lineTasks.setTextColor(Color.parseColor("#66B2FF"));
            }
        });



        return view;

        }

}