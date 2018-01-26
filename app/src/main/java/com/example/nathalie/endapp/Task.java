package com.example.nathalie.endapp;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by minor on 26/01/2018.
 */

public class Task {
    public String taskname;
    public Long startdate;
    public String taskid;
    public int frequency;

    public Task() {
        this.taskname = taskname;
        this.startdate = startdate;
        this.taskid = taskid;
        this.frequency = frequency;
    }


    public void setStartDate(Long startDate, int freq) {

        startdate = startDate;
        frequency = freq;

    }
}
