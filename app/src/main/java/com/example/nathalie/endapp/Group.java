package com.example.nathalie.endapp;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by minor on 23/01/2018.
 */

public class Group {
    public String color;
    public String groupname;
    public String tasksAmount;
    public String usersAmount;

    public Group() {
        this.color = color;
        this.groupname = groupname;
        this.tasksAmount = tasksAmount;
        this.usersAmount = usersAmount;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getGroupname() {
        return groupname;
    }

    public void setGroupname(String groupname) {
        this.groupname = groupname;
    }

    public String getTasksAmount() {
        return tasksAmount;
    }

    public void setTasksAmount(String tasksAmount) {
        this.tasksAmount = tasksAmount;
    }

    public String getUsersAmount() {
        return usersAmount;
    }

    public void setUsersAmount(String usersAmount) {
        this.usersAmount = usersAmount;
    }
}
