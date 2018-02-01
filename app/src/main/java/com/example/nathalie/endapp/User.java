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
 * User contains an id, email and id. Class is able to get id and username of current user from
 * Firebase
 */

public class User {
    public String username;
    public String email;
    public String id;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.id = id;
    }

    // Get current user ID and -name for Firebase
    public void setCurrentuser () {
        // Get current user's ID
        id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Access Firebase
        FirebaseDatabase.getInstance().getReference().child("users")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    username = String.valueOf(dataSnapshot.child(id).child("username").getValue());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("hallo_i", "onCancelled: " + databaseError.getMessage());
                }
            });
    }
}
