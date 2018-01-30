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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roomorama.caldroid.CaldroidFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ProfileFragment extends Fragment {
    private DatabaseReference mDatabase;
    private Button logOutButton, tasksButton;
    private TextView lineTasks;
    private ListView tasksList;
    private ArrayList<String> usersGroupsIDList= new ArrayList<String>();
    private ArrayList<Task> mTasksList= new ArrayList<Task>();



    User U;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize user details of current user
        U = new User();
        U.setCurrentuser();

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get views from XML
        logOutButton = (Button) view.findViewById(R.id.log_out_button);
        tasksButton = (Button) view.findViewById(R.id.tasks_button);
        lineTasks = (TextView) view.findViewById(R.id.tasks_line);
        tasksList = (ListView) view.findViewById(R.id.upcoming_tasks_lv);

        getTasks();

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

        public void getTasks () {
            // Lookup in Firebase current user
            mDatabase.child("users").child(U.id).child("personal groups")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get task from every group from Firebase
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                // Add group id to list
                                String groupID = String.valueOf(childDataSnapshot.getKey());

                                for (DataSnapshot taskDataSnapshot : childDataSnapshot.child("tasks").getChildren()) {
                                    Log.d("hallo snapc", " " + taskDataSnapshot);

                                    // Get task from Firebas
                                    Task T = taskDataSnapshot.getValue(Task.class);
                                    usersGroupsIDList.add(groupID);
                                    mTasksList.add(T);
                                    Log.d("hallo taksname?", "" + T.taskname);
                                }
                            }

                            fillTasksListview();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                        }
                    });


        }

        public void fillTasksListview () {
        // Set adapter for listview
        CalendarTaskAdapter cAdapter= new CalendarTaskAdapter(getContext(), mTasksList);
        tasksList.setAdapter(cAdapter);

        tasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Re-direct to group details on list item click
                showGroupDetails(String.valueOf(view.getTag()), usersGroupsIDList.get(i));
            }
        });
    }

    public void showGroupDetails (String groupName, String groupID) {
        // Create bundle to transfer groupname to next fragment
        Bundle bundle = new Bundle();
        bundle.putString("Group name", groupName);
        bundle.putString("GroupID", groupID);

        GroupDetailsFragment groupDetailsFragment = new GroupDetailsFragment();
        groupDetailsFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace fragment
        transaction.replace(R.id.frame, groupDetailsFragment);

        // Commit the transaction
        transaction.addToBackStack(null).commit();
    }

}