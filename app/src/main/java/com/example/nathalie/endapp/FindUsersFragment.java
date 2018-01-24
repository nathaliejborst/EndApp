package com.example.nathalie.endapp;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.ListIterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class FindUsersFragment extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    TextView receivedGroupName, showUsersToAdd;
    EditText searchUser;
    ListView resultsList, usersToAddList;
    Button findUsers, createGroup;

    private String groupName, groupID;
    private ArrayList<String> resultUsersList= new ArrayList<String>();
    private ArrayList<User> resultDetailsList= new ArrayList<User>();

    private ArrayList<String> addUsersList= new ArrayList<String>();
    private ArrayList<User> addUserDetailsList= new ArrayList<User>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_users, container, false);

        // Initialize Firebase
        mAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get groupname from previous fragment
        groupName  = getArguments().getString("Group name");
        if (this.getArguments() != null) {
            String myString = getArguments().getString("Group name", "Supercalifragilisticexpialidocious");
        }

        // Get views from XML for left frame
        receivedGroupName = (TextView) view.findViewById(R.id.received_group_name_tv);
        searchUser = (EditText) view.findViewById(R.id.input_find_by_email);
        findUsers = (Button) view.findViewById(R.id.find_button);
        resultsList = (ListView) view.findViewById(R.id.results_lv);

        // Get views from XML for right frame
        showUsersToAdd = (TextView) view.findViewById(R.id.users_to_add_tv);
        usersToAddList = (ListView) view.findViewById(R.id.users_to_add_lv);
        createGroup = (Button) view.findViewById(R.id.create_group_button);


        // Show groupname on top of fragment
        receivedGroupName.setText(groupName);

        // Handle on click for the search button
        findUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = String.valueOf(searchUser.getText());
                searchUser(search);

                // Clear text and hide keyboard
                searchUser.setText("");
                hideKeyboard(getContext(), getView());
            }
        });

        // Handle on click for the create group button
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Generate random group ID in case of identical group names
                groupID = Long.toHexString(Double.doubleToLongBits(Math.random()));

                // Add group to every user's node in Firebase
                createGroup();


            }
        });
        return view;
    }

    public void searchUser (String search) {
        // Clear previous result lists
        resultUsersList.clear();
        resultDetailsList.clear();

        // Lookup in Firebase users that match e-mail addresses with entered search item
        mDatabase.child("users").orderByChild("email").startAt(search).endAt(search + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get users details for every user in search result
                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                            // Get details from every user that matches the search criteria
                            User aUser = childDataSnapshot.getValue(User.class);
                            aUser.id = childDataSnapshot.getKey();

                            // Add usernames to list to show in results
                            resultUsersList.add(aUser.username);
                            // Save user details in list in order to not request the database multiple times
                            resultDetailsList.add(aUser);
                        }
                        // Show found users in listview
                        fillSimpleListView(resultUsersList, resultsList);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
                });
    }

    public void addUserToGroup (int positionClicked) {
        // Add username to list to show in the listview
        addUsersList.add(resultDetailsList.get(positionClicked).username);
        // Add user details in list in order to add right user to Firebase
        addUserDetailsList.add(resultDetailsList.get(positionClicked));

        // Fill listview on right side of screen
        fillSimpleListView(addUsersList, usersToAddList);
    }

    public void addGroupToUsers (final User selectedUser) {
        mDatabase.child("users").child(selectedUser.id).child("groups").child(groupID).child("groupname").setValue(groupName);
    }

    public void createGroup () {

        mDatabase.child("groups").child(groupID).child("groupname").setValue(groupName);

        // Add group to every user in Firebase
        for (int i = 0; i < addUserDetailsList.size(); i++) {
            Log.d("hallo _ user: ", "" + addUserDetailsList.get(i).id);
            mDatabase.child("users").child(addUserDetailsList.get(i).id).child("personal groups").child(groupID).child("groupname").setValue(groupName);
            mDatabase.child("groups").child(groupID).child("users").child(addUserDetailsList.get(i).id).setValue(addUserDetailsList.get(i).username);
        }
    }

    public void fillSimpleListView (final ArrayList list, final ListView lv) {
        // Initialize list adapter
        ListAdapter theAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

        // Set the adapter
        lv.setAdapter(theAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                // Make sure on click handles the right listview
                int listViewID = lv.getId();

                Log.d("hallo listview:", "" + listViewID);

                // Left listview
                if (listViewID == 2131230884) {
                    // Show clicked user in listview on right side of screen
                    addUserToGroup(i);
                }
                // Right listview
                if (listViewID == 2131230961) {
                    // Remove selected user from users to add list
                    addUsersList.remove(i);
                    usersToAddList.invalidateViews();
                }
            }
        });
    }

    public void infoDialog () {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Information");
        alertDialog.setMessage("Search by e-mail address to find a user. The app shows you the usernames found for your search. Click on a user to add the person to your group.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    // Hides keyboard from screen
    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
