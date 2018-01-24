package com.example.nathalie.endapp;

import java.util.ArrayList;

/**
 * Created by Nathalie on 12-1-2018.
 */

public class User {
    public String username;
    public String email;
    public String id;
//    public ArrayList<String> groups;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.id = id;
//        this.groups = groups;
    }

}
