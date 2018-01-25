package com.example.nathalie.endapp;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Nathalie on 12-1-2018.
 */

public class User {
    public String username;
    public String email;
    public String id;
    public String currentUsername;
    public String currentUserID;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.id = id;
    }

    public void setCurrentuser () {
        Log.d("hallo user class", " " + FirebaseAuth.getInstance().getCurrentUser().getUid());
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("hallo user class", " " + dataSnapshot.child(currentUserID).child("username").getValue());
                        currentUsername = String.valueOf(dataSnapshot.child(currentUserID).child("username").getValue());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                    }
                });
    }
}
