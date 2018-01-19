package com.example.nathalie.endapp;

/**
 * Created by Nathalie on 12-1-2018.
 */

public class User {
    public String username;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username) {
        this.username = username;
    }

}
