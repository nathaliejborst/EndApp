package com.example.nathalie.endapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ProfileFragment extends Fragment implements View.OnClickListener {
    private DatabaseReference mDatabase;

    private Button logOutButton, tasksButton;
    private View lineTasks;
    private ListView tasksList;
    private ArrayList<String> usersGroupsIDList= new ArrayList<String>();
    private ArrayList<Task> mTasksList= new ArrayList<Task>();

    private boolean taskvisible;

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
        lineTasks = (View) view.findViewById(R.id.tasks_line);
        tasksList = (ListView) view.findViewById(R.id.upcoming_tasks_lv);

        // Set tasks invisible by default
        taskvisible = false;

        // Get user's tasks from Firebase
        getTasks();

        // Set on click listeners
        logOutButton.setOnClickListener(this);
        tasksButton.setOnClickListener(this);

        return view;
    }

    // Get user's tasks from Firebase and fill listview
    public void getTasks () {
        // Lookup in Firebase current user
        mDatabase.child("users").child(U.id).child("personal groups")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Loop over every group of user
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        // Add group id to list
                        String groupID = String.valueOf(childDataSnapshot.getKey());

                        // Loop over every task of the group
                        for (DataSnapshot taskDataSnapshot : childDataSnapshot.child("tasks").getChildren()) {
                            // Get task from Firebase and ad groupID and task to lists
                            Task T = taskDataSnapshot.getValue(Task.class);
                            usersGroupsIDList.add(groupID);
                            mTasksList.add(T);
                        }
                    }
                    // Fill custom listviews with tasks
                    fillTasksListview();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                }
            });
    }

    // Fills custom listview with tasks of user
    public void fillTasksListview () {
    // Set adapter for listview
    CalendarTaskAdapter cAdapter= new CalendarTaskAdapter(getContext(), mTasksList);
    tasksList.setAdapter(cAdapter);

    // Re-directs to group details on list item click
    tasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            showGroupDetails(String.valueOf(view.getTag()), usersGroupsIDList.get(i));
        }
    });
    }

    // Re-directs to the show group details fragment which shows the details of selected group
    public void showGroupDetails (String groupName, String groupID) {
        // Create bundle to transfer groupname to next fragment
        Bundle bundle = new Bundle();
        bundle.putString("Group name", groupName);
        bundle.putString("GroupID", groupID);

        // Initiliaze fragment and set arguments
        GroupDetailsFragment groupDetailsFragment = new GroupDetailsFragment();
        groupDetailsFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace fragment
        transaction.replace(R.id.frame, groupDetailsFragment);

        // Commit the transaction
        transaction.addToBackStack(null).commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.log_out_button:
                // Sign user out and re-direct to the login screen
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.tasks_button:
                // Shows/Hides tasks and change buttoncolor on click
                if(!taskvisible){
                    tasksButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    lineTasks.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    tasksList.setVisibility(View.VISIBLE);

                    // Set boolean to true
                    taskvisible = true;
                } else {
                    tasksButton.setTextColor(getResources().getColor(R.color.mainOrange));
                    lineTasks.setBackgroundColor(getResources().getColor(R.color.mainOrange));
                    tasksList.setVisibility(View.INVISIBLE);

                    // Set boolean to false
                    taskvisible = false;
                }

                break;
        }
    }

}
