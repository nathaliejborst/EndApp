package com.example.nathalie.endapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;


public class CreateGroupFragment extends Fragment implements View.OnClickListener {
    private Button addGroup, findUsers, createGroup;
    private boolean searchItem = false;
    private ListView resultsListView, usersListview;
    private TextView usersToAdd;
    EditText searchUser;
    String newGroupName = null;
    String foundEmail, currentUserUsername, currentUserEmail, groupName;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    User aUser;
    private ArrayList<String> usernameList = new ArrayList<String>();
    private ArrayList<String> groupEmailsList= new ArrayList<String>();

    ArrayList<String> resultUsersList= new ArrayList<String>();
    private ArrayList<User> resultDetailsList= new ArrayList<User>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_group, container, false);


        // Get groupname from previous fragment
        groupName  = getArguments().getString("Group name");
        if (this.getArguments() != null) {
            String myString = getArguments().getString("Group name", "Supercalifragilisticexpialidocious");
        }

        // Get instance and referance from Firebase
        mAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

//        getFromDB ();



        // Get views from XML
        resultsListView = (ListView) view.findViewById(R.id.result_list);
        usersListview = (ListView) view.findViewById(R.id.users_list);
        addGroup = (Button) view.findViewById(R.id.add_group_button);
        createGroup = (Button) view.findViewById(R.id.submit_group_button);
        usersToAdd = (TextView) view.findViewById(R.id.users_to_add);
//        searchUser = (EditText) view.findViewById(R.id.input_find_by_email);

        findUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = String.valueOf(searchUser.getText());
//                searchByName(search);

                poging2(search);

                searchUser.setText("");
                hideKeyboard(getContext(), view);
                createGroup.setVisibility(View.VISIBLE);

            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Add group to selected users in Firebase
                for (int i=0; i < groupEmailsList.size(); i++){
                    addGroupToUsers(groupEmailsList.get(i));
                }
                // Add group to current logged in user
//                addGroupToUsers(currentUserEmail);

            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        //do what you want to do when button is clicked
        switch (view.getId()) {
            case R.id.add_group_button:
                break;
        }
    }

    private void getFromDB () {

        ValueEventListener postListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Get user details from database
                User aUser = dataSnapshot.child("users").child(String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid())).getValue(User.class);

                for (DataSnapshot childDataSnapshot : dataSnapshot.child("users").getChildren()) {

                    childDataSnapshot.getKey();

                    // Get username and email from every user in Firebase
                    currentUserUsername = aUser.username;
                    currentUserEmail = aUser.email;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Database errror", "onCancelled: " + databaseError.getMessage());
            }
        };
        mDatabase.addValueEventListener(postListener);
    }

//    public void poging3 () {
//
//        ValueEventListener postListener = new ValueEventListener() {
//
//            @Override
//
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                // get names and email from all users
//                for (DataSnapshot childDataSnapshot : dataSnapshot.child("users").getChildren()) {
//
//                    childDataSnapshot.getKey();
//                    String email = String.valueOf(childDataSnapshot.child("email").getValue());
//
//                    if (email.startsWith(searched)) {
//                        String username = String.valueOf(childDataSnapshot.child("username").getValue());
//                        groupUsersList.put("username", username);
//                        groupUsersList.put("email", email);
//                    }
//
//                }
//
//            }
//
//
//
//            @Override
//
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//
//        };
//
//        mDatabase.addValueEventListener(postListener);
//
//
//    }

    public void poging2 (String search) {

        mDatabase.child("users").orderByChild("email").startAt(search).endAt(search + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                            // Get details from every user that matches the search criteria
                            aUser = childDataSnapshot.getValue(User.class);
                            aUser.id = childDataSnapshot.getKey();

                            Log.d(" hallo class data 1", " " + aUser.username);
                            Log.d(" hallo class data 2", " " + aUser.email);
                            Log.d(" hallo class data 3", " " + aUser.id);

                            // Add usernames to list to show in results
                            resultUsersList.add(aUser.username);
                            // Save user details in list in order to not request the database multiple times
                            resultDetailsList.add(aUser);

                        }
                        // Show found users in listview
                        searchItem = true;
                        fillSimpleList(resultUsersList, resultsListView);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
                });


    }

//    public void searchByName (String search) {
//
//        // Search in database for user same as searched e-mail item
//        mDatabase.child("users").orderByChild("email").equalTo(search)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//
//                        // Clear list otherwise last search is still in list
//
//
//
//                        // Check if search command matches emails of users in Firebase
//                        if (dataSnapshot.getValue() != null) {
//
//                            // Convert object from Firebase to String
//                            String returnObject = dataSnapshot.getValue().toString();
//
//                            // Strip String to e-mail and username of selected user
//                            foundEmail = returnObject.substring(returnObject.indexOf("email=") + 6, returnObject.lastIndexOf(",") - 0);
//
//                            String foundUsername = returnObject.substring(returnObject.indexOf("username=") + 9, returnObject.lastIndexOf("}") - 1);
//
//
//                            // Add username to list to show to user
////                            groupUsersList.add(foundUsername);
//                            searchItem = true;
//                            fillSimpleList(groupUsersList, resultsListView);
//
//                        } else {    // Show message if user is not found in Firebase
////                            groupUsersList.add("User does not exist. Please try again");
//                            searchItem = false;
//                            fillSimpleList(groupUsersList, resultsListView);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w("Database errror", "onCancelled: " + databaseError.getMessage());
//                    }
//                });
//    }



//    public void addGroupAlert () {
//        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
//        final EditText edittext = new EditText(getContext());
//        alert.setMessage("Enter group name");
//        alert.setTitle("T I T L E");
//
//        // Show edittext to let user fill in username
//        alert.setView(edittext);
//
//        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//
//                // Get desired group name from user
//                final String enteredGroupName = edittext.getText().toString();
//                newGroupName = enteredGroupName.toString();
//
//                // Make sure user fills in a group name
//                if(enteredGroupName.equals("")) {
//                    Toast.makeText(getActivity(), "Please fill in a group name", Toast.LENGTH_SHORT).show();
//                } else {
//                    // Show entered group name in app and set button invisible
//                    groupName.setText(enteredGroupName);
//                    groupName.setVisibility(View.VISIBLE);
//                    addGroup.setVisibility(View.INVISIBLE);
//                }
//                }
//            });
//
//            alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//
//                }
//            });
//            alert.show();
//    }

    public void fillSimpleList (ArrayList list, final ListView listView) {

        // Initialize list adapter
        ListAdapter theAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

        // Set the adapter
        listView.setAdapter(theAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                if (searchItem) {
                    addUserToList();
                    searchItem = !searchItem;
                }

                Log.d("hallo_click position", String.valueOf(i));
                Toast.makeText(getActivity(), String.valueOf(listView.getItemAtPosition(i)), Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void addUserToList () {

        groupEmailsList.add(foundEmail);
        fillSimpleList(groupEmailsList, usersListview);
    }

    public void addGroupToUsers (String selectedUser) {

        Log.d(" hallo_selected user: ", selectedUser);

        // Search for user in Firebase
        mDatabase.child("users").orderByChild("email").equalTo(selectedUser)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Log.d("hallo_", "in loop" + " " + dataSnapshot.getValue());
                        String snapshot = dataSnapshot.getValue().toString();

                        String userID = snapshot.substring(snapshot.indexOf("{") + 1, snapshot.lastIndexOf("={") - 0);

                        // Some users already have groups in their account so make sure string is solely the user ID
                        if (userID.length() != 28) {
                            int length = userID.length() - 28;
                            userID = userID.substring(0, userID.length() - length);
                        }

                        if (newGroupName == null) {
                            Toast.makeText(getActivity(), "Please fill in a group name first", Toast.LENGTH_SHORT).show();
                        } else {

                            Log.d("hallo_", " " + userID);
                            Log.d("hallo_length", " " + userID.length());


                            // Create new child for created grup
                        mDatabase.child("users").child(userID).child("groups").push().setValue(newGroupName);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("Database error", "onCancelled: " + databaseError.getMessage());
                    }
                });
    }

    public void addUsersToGroup () {

    }


    // Hides keyboard from screen
    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    }



