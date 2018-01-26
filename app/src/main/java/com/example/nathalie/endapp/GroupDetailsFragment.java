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
    String groupName, groupID;
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





        tasksList.add("Dit is een taak");
        tasksList.add("Dit ook");

        fillSimpleListView(tasksList, tasksLV);

        accesDB();

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
                if (addTask.getText().toString().equals("")) {
                    showAlert("Please fill in a taskname first");
                } else {
                    pickStartDate();
                }
            }
        });

        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T = new Task();
                Log.d("hallo taak?", "date: " + T.startdate + " chosen freq: " + T.frequency);

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

    public void addTask () {

//            // Add group child to Firebase
//            mDatabase.child("groups").child(groupID).child("tasks").setValue(groupName);
//
//            // Add group to currently logged in user
//            mDatabase.child("users").child(U.currentUserID).child("personal groups").child(groupID).child("groupname").setValue(groupName);
//            mDatabase.child("groups").child(groupID).child("users").child(currentUserID).setValue(U.currentUsername);
//
//            // Add group to every user in Firebase
//            for (int i = 0; i < addUserDetailsList.size(); i++) {
//                Log.d("hallo _ user: ", "" + addUserDetailsList.get(i).id);
//                mDatabase.child("users").child(addUserDetailsList.get(i).id).child("personal groups").child(groupID).child("groupname").setValue(groupName);
//                mDatabase.child("groups").child(groupID).child("users").child(addUserDetailsList.get(i).id).setValue(addUserDetailsList.get(i).username);
//            }
    }

    public void pickStartDate () {

        AddTaskFragment dialogFragment = new AddTaskFragment ();
        dialogFragment.show(getActivity().getFragmentManager(),"Simple Dialog");


//
//        Calendar c = Calendar.getInstance();
//        int day = c.get(Calendar.DAY_OF_MONTH);
//        int month = c.get(Calendar.MONTH);
//        int year = c.get(Calendar.YEAR);
//
//        DatePickerDialog datePicker = new DatePickerDialog(getContext(),
//                android.R.style.Theme_DeviceDefault_Dialog,
//                mDateSetListener,year,month,day);
//        datePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        datePicker.show();
//
//        pickDate.setVisibility(View.INVISIBLE);
//        submitTask.setVisibility(View.VISIBLE);
//
//
//        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                Log.d("hallo datum", "" + year + ", " + month + ", " + dayOfMonth);
//            }
//        };
    }

    public void fillSimpleListView (final ArrayList list, ListView lv) {
        ListAdapter theAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

        // Set the adapter
        lv.setAdapter(theAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

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


}
