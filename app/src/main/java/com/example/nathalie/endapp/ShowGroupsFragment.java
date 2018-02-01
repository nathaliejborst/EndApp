package com.example.nathalie.endapp;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.nathalie.endapp.Group;
import com.example.nathalie.endapp.GroupDetailsFragment;
import com.example.nathalie.endapp.GroupnameFragment;
import com.example.nathalie.endapp.R;
import com.example.nathalie.endapp.ShowGroupsAdapter;
import com.example.nathalie.endapp.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowGroupsFragment extends Fragment implements View.OnClickListener {
    private DatabaseReference mDatabase;

    private Button newGroupButton, showGroupsButton;
    private TextView lineAddGroup, lineShowGroups;
    private ListView showUsersGroups;

    private ArrayList<String> usersGroupsIDList= new ArrayList<String>();
    private ArrayList<Group> mGroupsList= new ArrayList<Group>();
    private boolean showGroups;
    User U;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_groups, container, false);

        // Initialize user details of current user
        U = new User();
        U.setCurrentuser();

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Don't show groups by default
        showGroups = false;

        // Get views from XML
        newGroupButton = (Button) view.findViewById(R.id.new_group_button);
        showGroupsButton = (Button) view.findViewById(R.id.show_groups_button);
        showUsersGroups = (ListView) view.findViewById(R.id.show_groups_lv);
        lineAddGroup = (TextView) view.findViewById(R.id.add_group_line);
        lineShowGroups = (TextView) view.findViewById(R.id.show_groups_line);

        // Set on click listeners
        newGroupButton.setOnClickListener(this);
        showGroupsButton.setOnClickListener(this);

        return view;
    }

    // Gets all groups where current user is a member of from Firebase
    public void getUsersGroups () {
        // Lookup in Firebase current user
        mDatabase.child("users").child(U.id).child("personal groups")
            .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Clear groups ID list before filling it with data from Firebase
                usersGroupsIDList.clear();

                // Loop over all groups to get name and ID
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    // Add group ID's to list
                    usersGroupsIDList.add(String.valueOf(childDataSnapshot.getKey()));
                }

                // Get task and member amount per group
                getTaskAndMemberCount();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
            }
        });
    }

    // Re-directs to group details fragment
    public void showGroupDetails (String groupName, String groupID) {
        // Create bundle to transfer groupname and -ID to next fragment
        Bundle bundle = new Bundle();
        bundle.putString("Group name", groupName);
        bundle.putString("GroupID", groupID);

        // Initialize group details fragment and set arguments
        GroupDetailsFragment groupDetailsFragment = new GroupDetailsFragment();
        groupDetailsFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace fragment
        transaction.replace(R.id.frame, groupDetailsFragment, "groupdetails");

        // Commit the transaction
        transaction.addToBackStack(null).commit();
    }

    public void getTaskAndMemberCount () {
        // Loop over every group to get amount of tasks and members per group
        for (int i = 0; i < usersGroupsIDList.size(); i++) {
            // Clear list because otherwise groups will be shown twice when clicked multiple times
            mGroupsList.clear();

            // Get task and member count from Firebase
            mDatabase.child("groups").child(usersGroupsIDList.get(i)).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get group details for group
                        Group G = new Group();
                        G = dataSnapshot.getValue(Group.class);

                        // Get tasks and users per group
                        G.tasksAmount = String.valueOf(dataSnapshot.child("tasks").getChildrenCount());
                        G.usersAmount = String.valueOf(dataSnapshot.child("users").getChildrenCount());

                        // Add group to groupslist
                        mGroupsList.add(G);

                        // Fill listview with groups
                        fillGroupsListview();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
            });
        }
    }

    // Fills custom listview with user's groups
    public void fillGroupsListview () {
        // Set adapter for listview
        ShowGroupsAdapter cAdapter= new ShowGroupsAdapter(getContext(), R.layout.item_group_list, mGroupsList);
        showUsersGroups.setAdapter(cAdapter);

        // Re-direct to selected group details
        showUsersGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showGroupDetails(mGroupsList.get(i).groupname, usersGroupsIDList.get(i));
            }
        });
    }

    // Changes color of show groups button and shows/hides listview with groups
    public void groupsButtonClicked () {
        // Shows groups and turns views blue when button clicked and not yet visible
        if(!showGroups) {
            showGroupsButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            lineShowGroups.setTextColor(getResources().getColor(R.color.colorPrimary));
            showUsersGroups.setVisibility(View.VISIBLE);

            showGroups = true;
        // Hides groups and turns views back to orange when button clicked and already visible
        } else {
            showGroupsButton.setTextColor(getResources().getColor(R.color.mainOrange));
            lineShowGroups.setTextColor(getResources().getColor(R.color.mainOrange));
            showUsersGroups.setVisibility(View.INVISIBLE);

            showGroups = false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.new_group_button:
                // Initialize group fragment to re-direct to groupname fragment
                GroupnameFragment groupnameFragment = new GroupnameFragment();;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace fragment
                transaction.replace(R.id.frame, groupnameFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

                break;
            case R.id.show_groups_button:
                // Get user's groups from Firebase and fill listview
                getUsersGroups();

                // Handles button click
                groupsButtonClicked();

                break;
        }
    }
}
