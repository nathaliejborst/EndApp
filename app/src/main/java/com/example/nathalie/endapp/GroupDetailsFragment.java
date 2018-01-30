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
public class GroupDetailsFragment extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    TextView groupNameTV, addTaskTV, taskLine;
    String groupName, groupID, taskName, groupColor;
    ListView membersLV, tasksLV;
    Button addTask, addMember, submitTask;
    EditText addTaskET;
    ImageButton pickDate;

    User U;
    Task T;

    private ArrayList<User> mMembersList= new ArrayList<User>();
    private ArrayList<Task> tasksList= new ArrayList<Task>();
    private ArrayList<Task> mTasksList= new ArrayList<Task>();



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
        membersLV = (ListView) view.findViewById(R.id.members_lv);
        tasksLV = (ListView) view.findViewById(R.id.tasks_lv);
        addTask = (Button) view.findViewById(R.id.add_task_button) ;
        submitTask = (Button) view.findViewById(R.id.submit_task_button) ;
        addMember = (Button) view.findViewById(R.id.add_member_button);
        addTaskET = (EditText) view.findViewById(R.id.add_task_et);
        pickDate = (ImageButton) view.findViewById(R.id.pick_date_button);

        // Get selected group name from previous fragment
        groupName  = getArguments().getString("Group name");
        groupID  = getArguments().getString("GroupID");

        // Get tasks from database and show in listview
        getTasksFromDatabase();

        accesDB();

        tasksLV.invalidateViews();
        membersLV.invalidateViews();

        // Show groupname in title
        groupNameTV.setText(groupName);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set hint
                addTaskET.setHint("Enter taskname");

                // Change visibility of views
                addTaskET.setVisibility(View.VISIBLE);
                pickDate.setVisibility(View.VISIBLE);

                addTaskTV.setVisibility(View.INVISIBLE);
                addTask.setVisibility(View.INVISIBLE);
                taskLine.setVisibility(View.INVISIBLE);

            }
        });

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get taskname from view
                taskName = addTaskET.getText().toString();

                // Check if group already has a task with the same name
                if (checkTaskDuplicate()) {
                    showAlert("This task already exists");
                    return;
                }
                // Check if user filled in a taskname
                if (addTaskET.getText().toString().matches("")) {
                    showAlert("Please fill in a taskname first");
                    return;
                }
                    // Open datepicker dialog to add task
                    pickStartDate();

                    // Change visibilities of views back to before adding the task and delete entry
                    addTaskET.setText("");
                    addTaskET.setVisibility(View.INVISIBLE);
                    pickDate.setVisibility(View.INVISIBLE);

                    addTaskTV.setVisibility(View.VISIBLE);
                    addTask.setVisibility(View.VISIBLE);
                    taskLine.setVisibility(View.VISIBLE);

            }
        });

        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    public void accesDB () {
        mDatabase.child("groups").child(groupID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get group color and name from Firebase
//                        groupColor = String.valueOf(dataSnapshot.child("color").getValue());
//                        String.valueOf(dataSnapshot.child("groupname").getValue());

//                        Log.d("hallo group COLOR?", String.valueOf(dataSnapshot.child("color").getValue()));
                        for (DataSnapshot childDataSnapshot : dataSnapshot.child("users").getChildren()) {

                            // Get group members from Firebase
                            User member = childDataSnapshot.getValue(User.class);
                            mMembersList.add(member);

                            Log.d("hallo group usernames2", "" + member.username);

                        }
                        fillMembersListview();
//                        fillSimpleListView(membersList, membersLV);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
                });
    }

    public void pickStartDate () {
        // Create bundle to transfer group- and taskname to next fragment
        Bundle bundle = new Bundle();
        bundle.putString("GroupID", groupID);
        bundle.putString("Group color", groupColor);
        bundle.putString("taskname", taskName);
        bundle.putString("groupname", groupName);

        // Open fragment to choose startdate en frequency
        AddTaskFragment dialogFragment = new AddTaskFragment ();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getActivity().getFragmentManager(),"Simple Dialog");
    }

    public void getTasksFromDatabase () {
        mDatabase.child("groups").child(groupID).child("tasks")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mTasksList.clear();
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            Task T = new Task();
                            T = childDataSnapshot.getValue(Task.class);
                            Log.d("hallo class", String.valueOf(T.groupname) + " frequency: " + T.frequency);

                            // Add task to list
                            mTasksList.add(T);
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
        tasksLV.setAdapter(cAdapter);

        tasksLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedItem = String.valueOf(view.getTag());
                showAlert(clickedItem);
                tasksLV.invalidateViews();
            }
        });

    }

    public void fillMembersListview () {
        // Set adapter for listview
        GroupMembersAdapter cAdapter= new GroupMembersAdapter(getContext(), mMembersList);
        membersLV.setAdapter(cAdapter);

        membersLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickedItem = String.valueOf(view.getTag());
                showAlert(clickedItem);
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

    public void showAlert (String alert) {
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
