package com.example.nathalie.endapp;


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
public class ShowGroupsFragment extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private Button newGroupButton, showGroupsButton;
    private ListView showUsersGroups;
    private ArrayList<String> usersGroupsList= new ArrayList<String>();
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
        mAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get views from XML
        newGroupButton = (Button) view.findViewById(R.id.new_group_button);
        showGroupsButton = (Button) view.findViewById(R.id.show_groups_button);
        showUsersGroups = (ListView) view.findViewById(R.id.show_groups_lv);

        // Fill listview with groups from user
        getUsersGroups();

        // Re-direct to fragment to add a new group
        newGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupnameFragment groupnameFragment = new GroupnameFragment();;
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace fragment
                transaction.replace(R.id.frame, groupnameFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        showGroupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show found users in listview
                fillSimpleListView(usersGroupsList);
            }
        });
        return view;

    }

    public void getUsersGroups () {
        // Lookup in Firebase current user
        mDatabase.child("users").child(U.currentUserID).child("personal groups")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get users details for every user in search result
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                            Log.w("hallo_GROUPS", "groups? " + childDataSnapshot.child("groupname").getValue());
                            usersGroupsList.add(String.valueOf(childDataSnapshot.child("groupname").getValue()));

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
                });
    }

    public void fillSimpleListView (final ArrayList list) {
        ListAdapter theAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

        // Set the adapter
        showUsersGroups.setAdapter(theAdapter);
        showUsersGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
            }
        });
    }



}
