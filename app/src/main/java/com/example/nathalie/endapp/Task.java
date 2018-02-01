package com.example.nathalie.endapp;

import java.util.ArrayList;

/**
 * Created by Nathalie Borst on 26/01/2018.
 * Class contains a taskname, startdate, groupid, groupname, frequency groupcolor and schedule.
 *
 */

public class Task {
    public String taskname;
    public String startdate;
    public String groupid;
    public String groupname;
    public int frequency;
    public String groupcolor;
    public ArrayList<String> schedule;

    public Task() {
        this.taskname = taskname;
        this.groupid = groupid;
        this.groupname = groupname;
        this.startdate = startdate;
        this.frequency = frequency;
        this.schedule = schedule;
        this.groupcolor = groupcolor;
    }

}
