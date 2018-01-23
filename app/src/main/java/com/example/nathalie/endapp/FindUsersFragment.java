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


/**
 * A simple {@link Fragment} subclass.
 */
public class FindUsersFragment extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    TextView receivedGroupName;
    EditText searchUser;
    ListView resultsList;
    Button findUsers;
    ImageButton infoButton;

    private String groupName;
    private ArrayList<String> resultUsersList= new ArrayList<String>();
    private ArrayList<User> resultDetailsList= new ArrayList<User>();

    private ArrayList<String> nextFragmentID= new ArrayList<String>();
    private ArrayList<String> nextFragmentName= new ArrayList<String>();

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

        // Get views from XML
        receivedGroupName = (TextView) view.findViewById(R.id.received_group_name_tv);
        searchUser = (EditText) view.findViewById(R.id.input_find_by_email);
        findUsers = (Button) view.findViewById(R.id.find_button);
        resultsList = (ListView) view.findViewById(R.id.results_lv);
        infoButton = (ImageButton) view.findViewById(R.id.infoButton);

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
        // Handle on click for the search button
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                infoDialog();
            }
        });

        return view;
    }

    public void searchUser (String search) {
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
                        fillSimpleListView(resultUsersList);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
                });
    }

    public void addUserToGroup (int positionClicked) {
        // Create bundle to transfer groupname to next fragment
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("ID list", nextFragmentID);
        bundle.putStringArrayList("User list", nextFragmentName);
        bundle.putString("User ID", resultDetailsList.get(positionClicked).id);
        bundle.putString("User name", resultDetailsList.get(positionClicked).username);
        bundle.putString("User email", resultDetailsList.get(positionClicked).email);
        bundle.putString("Group name", groupName);

        addGroupFragment addFragment = new addGroupFragment();
        addFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace fragment
        transaction.replace(R.id.frame2, addFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void addUserToList (int i) {
        nextFragmentID.add(resultDetailsList.get(i).id);
        Log.d(" hallo f", " " + nextFragmentID);
        nextFragmentName.add(resultDetailsList.get(i).username);
        Log.d(" hallo f", " " + nextFragmentName);
    }

    public void fillSimpleListView (ArrayList list) {
        // Initialize list adapter
        ListAdapter theAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

        // Set the adapter
        resultsList.setAdapter(theAdapter);
        resultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                // Add user to group on click
                addUserToList(i);
                addUserToGroup(i);
                Toast.makeText(getActivity(), String.valueOf(resultsList.getItemAtPosition(i)), Toast.LENGTH_SHORT).show();
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
