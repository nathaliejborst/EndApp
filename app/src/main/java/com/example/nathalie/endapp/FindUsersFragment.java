package com.example.nathalie.endapp;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindUsersFragment extends Fragment implements View.OnClickListener {
    private DatabaseReference mDatabase;

    private TextView receivedGroupName, showUsersToAdd, line;
    private EditText searchUser;
    private ListView resultsList, usersToAddList;
    private Button findUsers, createGroup;

    private String groupName, groupID;
    private String groupColor = "red";
    private ArrayList<String> resultUsersList= new ArrayList<String>();
    private ArrayList<User> resultDetailsList= new ArrayList<User>();
    private ArrayList<String> addUsersList= new ArrayList<String>();
    private ArrayList<User> addUserDetailsList= new ArrayList<User>();

    User U;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_users, container, false);

        // Initialize Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // get e-mail of current user from Firebase
        FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser != null) {
                U.email =  firebaseUser.getEmail();
            }
            }
        });

        // Get groupname and -color from previous fragment
        groupName  = getArguments().getString("Group name");
        groupColor = getArguments().getString("Group color");

        // Initialize user details of current user;
        U = new User();
        U.setCurrentuser();

        // Get views from XML for left frame
        receivedGroupName = (TextView) view.findViewById(R.id.received_group_name_tv);
        line = (TextView) view.findViewById(R.id.line_tv);
        searchUser = (EditText) view.findViewById(R.id.input_find_by_email);
        findUsers = (Button) view.findViewById(R.id.find_button);
        resultsList = (ListView) view.findViewById(R.id.results_lv);

        // Get views from XML for right frame
        showUsersToAdd = (TextView) view.findViewById(R.id.users_to_add_tv);
        usersToAddList = (ListView) view.findViewById(R.id.users_to_add_lv);
        createGroup = (Button) view.findViewById(R.id.create_group_button);

        // Show groupname on top of fragment
        receivedGroupName.setText("  " + groupName);

        // Set on click listeners
        findUsers.setOnClickListener(this);
        createGroup.setOnClickListener(this);

        return view;
    }

    // Fills a list of all registered users' e-mail that match the search input
    public void searchUser (String search) {
        // Clear previous result lists
        resultUsersList.clear();
        resultDetailsList.clear();

        // Lookup in Firebase users that match e-mail addresses with entered search item
        mDatabase.child("users").orderByChild("email").startAt(search).endAt(search + "\uf8ff")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Loop trough every user that match the search criteria
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        // Get details from every user that matches the search criteria
                        User aUser = childDataSnapshot.getValue(User.class);
                        aUser.id = childDataSnapshot.getKey();

                        // Show results but exclude logged in user from list
                        if (!aUser.id.equals(U.id)) {
                            // Add user to list
                            resultDetailsList.add(aUser);
                        }
                    }
                    // Show found users in listview
                    fillUsersListview();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                }
            });
    }

    // adds user to provisory list of group members
    public void addUserToGroup (int positionClicked) {
        // Check if user is already added
        for (int i = 0; i < addUsersList.size(); i++) {
            if (addUsersList.get(i).equals(resultDetailsList.get(positionClicked).username)) {
                LoginActivity.showAlert("You already added this user", getActivity());
                return;
            }
        }

        // Add username to list
        addUsersList.add(resultDetailsList.get(positionClicked).username);
        // Add user to list
        addUserDetailsList.add(resultDetailsList.get(positionClicked));

        // Fill listview on right side of screen
        fillSimpleListView(addUsersList, usersToAddList);
    }

    // Adds group with corresponding users and details to Firebase
    public void createGroup () {
        // Add group child with values groupname and -color to Firebase
        mDatabase.child("groups").child(groupID).child("groupname").setValue(groupName);
        mDatabase.child("groups").child(groupID).child("color").setValue(groupColor);

        // Add group to currently logged in user
        mDatabase.child("users").child(U.id).child("personal groups").child(groupID).child("groupname").setValue(groupName);
        mDatabase.child("groups").child(groupID).child("users").child(U.id).setValue(U);

        // Add group to every groupmember in Firebase
        for (int i = 0; i < addUserDetailsList.size(); i++) {
            Log.d("hallo _ user: ", "" + addUserDetailsList.get(i).id);
            mDatabase.child("users").child(addUserDetailsList.get(i).id).child("personal groups").child(groupID).child("groupname").setValue(groupName);
            mDatabase.child("groups").child(groupID).child("users").child(addUserDetailsList.get(i).id).setValue(addUserDetailsList.get(i));
        }
    }

    // Fills a simple listview on the right side of the screen
    public void fillSimpleListView (final ArrayList list, final ListView lv) {
        // Initialize list adapter
        ListAdapter theAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

        // Set the adapter
        lv.setAdapter(theAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                // Removes selected user from users to add list on click
                addUsersList.remove(i);
                usersToAddList.invalidateViews();
            }
        });
    }

    public void fillUsersListview () {
        // Set adapter for listview
        GroupMembersAdapter cAdapter= new GroupMembersAdapter(getContext(), resultDetailsList);
        resultsList.setAdapter(cAdapter);

        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addUserToGroup(i);

                // Change color of button and line when one user is added to list
                line.setTextColor(Color.parseColor("#66B2FF"));
                createGroup.setTextColor(Color.parseColor("#66B2FF"));
            }
        });

    }

    // Hides keyboard from screen
    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    // Re-directs user to details of selected group
    public void showGroupDetails () {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_button:
                // Get the entered search input from user
                String search = String.valueOf(searchUser.getText());
                searchUser(search);

                // Clear text and hide keyboard
                searchUser.setText("");
                hideKeyboard(getContext(), getView());
                break;
            case R.id.create_group_button:
                // Generate random group ID in case of identical group names
                groupID = Long.toHexString(Double.doubleToLongBits(Math.random()));

                // Add group to every user's node in Firebase
                createGroup();

                // Re-direct to just created group details
                showGroupDetails();
                break;
        }
    }
}
