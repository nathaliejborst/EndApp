package com.example.nathalie.endapp;


import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupDetailsFragment extends Fragment implements View.OnClickListener {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private TextView groupNameTV, addTaskTV, taskLine, colorOverlay;
    private String groupName, groupID, taskName, groupColor;
    private ListView membersLV, tasksLV;
    private Button addTask, addMember, submitTask;
    private EditText addTaskET;
    private ImageButton pickDate;

    private ArrayList<User> mMembersList= new ArrayList<User>();
    private ArrayList<Task> mTasksList= new ArrayList<Task>();

    User U;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_details, container, false);

        // Get instance and referance from Firebase
        mAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize user details of current user;
        U = new User();
        U.setCurrentuser();

        // Get views from XML
        groupNameTV = (TextView) view.findViewById(R.id.group_name_tv);
        addTaskTV = (TextView) view.findViewById(R.id.tasks_tv);
        taskLine = (TextView) view.findViewById(R.id.tasks_line);
        colorOverlay = (TextView) view.findViewById(R.id.color_overlay_tv);
        membersLV = (ListView) view.findViewById(R.id.members_lv);
        tasksLV = (ListView) view.findViewById(R.id.tasks_lv);
        addTask = (Button) view.findViewById(R.id.add_task_button) ;
        submitTask = (Button) view.findViewById(R.id.submit_task_button) ;
        addTaskET = (EditText) view.findViewById(R.id.add_task_et);
        pickDate = (ImageButton) view.findViewById(R.id.pick_date_button);

        // Get selected group name and ID from previous fragment
        groupName  = getArguments().getString("Group name");
        groupID  = getArguments().getString("GroupID");

        // Get tasks from database, show in right listview and image color overlay
        getTasksFromDatabase();

        // Get tasks from database, show in left listview
        getGroupMembers();

        // Update listviews
        tasksLV.invalidateViews();
        membersLV.invalidateViews();

        // Show groupname in title
        groupNameTV.setText(groupName);

        addTask.setOnClickListener(this);
        pickDate.setOnClickListener(this);

//        addTask.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Set hint
//                addTaskET.setHint("Enter taskname");
//
//                // Change visibility of views
//                addTaskET.setVisibility(View.VISIBLE);
//                pickDate.setVisibility(View.VISIBLE);
//
//                addTaskTV.setVisibility(View.INVISIBLE);
//                addTask.setVisibility(View.INVISIBLE);
//                taskLine.setVisibility(View.INVISIBLE);
//
//            }
//        });
//
//        pickDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            // Get taskname from view
//            taskName = addTaskET.getText().toString();
//
//            // Check if group already has a task with the same name
//            if (checkTaskDuplicate()) {
//                LoginActivity.showAlert("This task already exists", getActivity());
//                return;
//            }
//            // Check if user filled in a taskname
//            if (addTaskET.getText().toString().matches("")) {
//                LoginActivity.showAlert("Please fill in a taskname first", getActivity());
//                return;
//            }
//
//            // Open datepicker dialog to add task
//            pickStartDate();
//
//            // Change visibilities of views back to default
//            addTaskET.setText("");
//            addTaskET.setVisibility(View.INVISIBLE);
//            pickDate.setVisibility(View.INVISIBLE);
//
//            addTaskTV.setVisibility(View.VISIBLE);
//            addTask.setVisibility(View.VISIBLE);
//            taskLine.setVisibility(View.VISIBLE);
//            }
//        });
        return view;
    }

    // Get tasks for group from Firebase
    public void getTasksFromDatabase () {
        // Access Firebase
        mDatabase.child("groups").child(groupID).child("tasks")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Clear tasklist before filling it
                    mTasksList.clear();

                    // Loop over every task
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        // Get task and add to list
                        Task T = new Task();
                        T = childDataSnapshot.getValue(Task.class);
                        mTasksList.add(T);
                    }
                    // Fill listview with tasks
                    fillTasksListview();

                    // Add a semi-transparant groupcolor overlay over the image
                    setOverlayColor();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                }
            });
    }

    // Get members of group from Firebase
    public void getGroupMembers () {
        // Access Firebase
        mDatabase.child("groups").child(groupID)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Clear member list before filling it
                    mMembersList.clear();

                    // Loop over groupmembrs
                    for (DataSnapshot childDataSnapshot : dataSnapshot.child("users").getChildren()) {
                        // Get group member and add to list
                        User member = childDataSnapshot.getValue(User.class);
                        mMembersList.add(member);
                    }

                    // Show members in listview
                    fillMembersListview();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                }
            });
    }

    // Fills left listview with members of group
    public void fillMembersListview () {
        // Set adapter for listview
        GroupMembersAdapter cAdapter= new GroupMembersAdapter(getContext(), mMembersList);
        membersLV.setAdapter(cAdapter);

        //
//        membersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                String clickedItem = String.valueOf(view.getTag());
//                showAlert(clickedItem);
//                tasksLV.invalidateViews();
//            }
//        });
    }

    // Fills right listview with tasks of group
    public void fillTasksListview () {
        // Set adapter for listview
        CalendarTaskAdapter cAdapter= new CalendarTaskAdapter(getContext(), mTasksList);
        tasksLV.setAdapter(cAdapter);

        // Show task schedule on click
        tasksLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LoginActivity.showAlert(mTasksList.get(i).schedule.toString(), getActivity());
                tasksLV.invalidateViews();
            }
        });
    }

    // Returns true if entered taskname already exists within group
    public boolean checkTaskDuplicate () {
        for (int i = 0; i < mTasksList.size(); i++) {
            if (taskName.equalsIgnoreCase(mTasksList.get(0).taskname)) {
                return true;
            }
        }
        return false;
    }

    // Re-directs to next fragment to choose the startdate and frequency of task
    public void pickStartDate () {
        // Create bundle to transfer groupID, groupname and taskname to next fragment
        Bundle bundle = new Bundle();
        bundle.putString("GroupID", groupID);
        bundle.putString("taskname", taskName);
        bundle.putString("groupname", groupName);

        // Open fragment to choose startdate en frequency
        AddTaskFragment dialogFragment = new AddTaskFragment ();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getActivity().getFragmentManager(),"Simple Dialog");
    }

//    public void showAlert (String alert) {
//        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
//        alertDialog.setTitle(alert);
//        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        alertDialog.show();
//    }

    // Sets color of the header to the corresponding groupcolor
    public void setOverlayColor () {
        // Prevent error is retrieving data from Firebase is slow
        if (mTasksList.size() < 1) {
            return;
        }
        // Get string color from task
        String color = mTasksList.get(0).groupcolor;

        // Change overlay color according to groupcolor
        if(color.equals("magenta")) {
            colorOverlay.setBackgroundColor(getResources().getColor(R.color.colorMagenta));
        }
        else if(color.contains("cyan")) {
            colorOverlay.setBackgroundColor(getResources().getColor(R.color.colorCyan));
        }
        else if(color.contains("yellow")) {
            colorOverlay.setBackgroundColor(getResources().getColor(R.color.colorYellow));
        }
        else if(color.contains("blue")) {
            colorOverlay.setBackgroundColor(getResources().getColor(R.color.colorBlue));
        }
        else if(color.contains("red")) {
            colorOverlay.setBackgroundColor(getResources().getColor(R.color.colorRed));
        }

        // Add overlaycolor with opacity of 50%
        colorOverlay.getBackground().setAlpha(128);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tasks_tv:
                // Set hint
                addTaskET.setHint("Enter taskname");

                // Change visibility of views
                addTaskET.setVisibility(View.VISIBLE);
                pickDate.setVisibility(View.VISIBLE);

                addTaskTV.setVisibility(View.INVISIBLE);
                addTask.setVisibility(View.INVISIBLE);
                taskLine.setVisibility(View.INVISIBLE);
                break;
            case R.id.pick_date_button:
                // Get taskname from edittext
                taskName = addTaskET.getText().toString();

                // Check if group already has a task with the same name
                if (checkTaskDuplicate()) {
                    LoginActivity.showAlert("This task already exists", getActivity());
                    return;
                }
                // Check if user filled in a taskname
                if (LoginActivity.checkIfBlank(addTaskET.getText().toString(), getActivity())) {
                    return;
                }

                // Open datepicker dialog to add task
                pickStartDate();

                // Change visibilities of views back to default
                addTaskET.setText("");
                addTaskET.setVisibility(View.INVISIBLE);
                pickDate.setVisibility(View.INVISIBLE);

                addTaskTV.setVisibility(View.VISIBLE);
                addTask.setVisibility(View.VISIBLE);
                taskLine.setVisibility(View.VISIBLE);

                break;
        }

    }
}
