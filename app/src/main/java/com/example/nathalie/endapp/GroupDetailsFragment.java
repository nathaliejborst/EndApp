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
    String groupName, groupID, taskName;
    ListView membersLV, tasksLV;
    Button addTask, addMember, submitTask;
    EditText addTaskET;
    ImageButton pickDate;

    User U;
    Task T;

    private ArrayList<String> membersList= new ArrayList<String>();
    private ArrayList<String> tasksList= new ArrayList<String>();


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
        Log.d("halloooo groupID", groupID);

        // Get tasks from database and show in listview
        getTasksFromDatabase();

        accesDB();

        // Show groupname in title
        groupNameTV.setText(groupName);

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        mDatabase.child("groups").child(groupID).child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("hallo group usernames1", String.valueOf(dataSnapshot.getChildrenCount()));
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            Log.d("hallo group usernames2", String.valueOf(childDataSnapshot.getValue()));
                            membersList.add(String.valueOf(childDataSnapshot.getValue()));

                        }
                        fillSimpleListView(membersList, membersLV);
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
        bundle.putString("taskname", taskName);
        bundle.putString("groupname", groupName);

        // Open fragment to choose startdate en frequency
        AddTaskFragment dialogFragment = new AddTaskFragment ();
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getActivity().getFragmentManager(),"Simple Dialog");
    }

    public void fillSimpleListView (final ArrayList list, final ListView lv) {
        ListAdapter theAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

        // Set the adapter
        lv.setAdapter(theAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                lv.invalidateViews();

            }
        });
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

    public void getTasksFromDatabase () {
        mDatabase.child("groups").child(groupID).child("tasks")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("hallo task count", String.valueOf(dataSnapshot.getChildrenCount()));
                        tasksList.clear();
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                            Log.d("hallo task?", String.valueOf(childDataSnapshot.getKey()));
                            tasksList.add(String.valueOf(childDataSnapshot.getKey()));
                        }
                        fillSimpleListView(tasksList, tasksLV);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
                });

    }

    public boolean checkTaskDuplicate () {
        for (int i = 0; i < tasksList.size(); i++) {
            if (taskName.equalsIgnoreCase(tasksList.get(0))) {
                Log.d("hallo taskduplicate", "lijst: " + tasksList.get(0) + "   taskname: " + taskName);
                return true;
            }
        }
        return false;
    }


}
