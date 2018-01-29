package com.example.nathalie.endapp;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by minor on 26/01/2018.
 */

public class Task {
    public String taskname;
    public String startdate;
    public String groupid;
    public String groupname;
    public int frequency;
    public ArrayList<String> schedule;

    public Task() {
        this.taskname = taskname;
        this.groupid = groupid;
        this.groupname = groupname;
        this.startdate = startdate;
        this.frequency = frequency;
        this.schedule = schedule;
    }

    public void createSchedule () {




    }


}
