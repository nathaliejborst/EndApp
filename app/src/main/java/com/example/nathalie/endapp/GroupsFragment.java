package com.example.nathalie.endapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class GroupsFragment extends Fragment implements View.OnClickListener {
    private Button addGroup;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groups, container, false);

        // Get instance and referance from Firebase
        mAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        getFromDB ();


        addGroup = (Button) view.findViewById(R.id.add_group_button);
        addGroup.setOnClickListener(this);



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

    public void getFromDB () {

        ValueEventListener postListener = new ValueEventListener() {

            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot childDataSnapshot : dataSnapshot.child("users").getChildren()) {

                    childDataSnapshot.getKey();

                    Log.d("hallo_fragment", String.valueOf(childDataSnapshot.getKey()));

//                    String name = String.valueOf(childDataSnapshot.child("score").getValue());
//
//                    String score = String.valueOf(childDataSnapshot.child("username").getValue());


                }

            }



            @Override

            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addValueEventListener(postListener);
    }


}
