package com.example.nathalie.endapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by minor on 30/01/2018.
 */

public class ShowGroupsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Group> mGroupslist;

    public ShowGroupsAdapter(Context mContext, List<Group> mGroupslist) {
        this.mContext = mContext;
        this.mGroupslist = mGroupslist;
    }


    @Override
    public int getCount() {
        return mGroupslist.size();
    }

    @Override
    public Object getItem(int position) {
        return mGroupslist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.item_group_list, null);
        TextView groupName = (TextView)v.findViewById(R.id.groupname_tv);
        TextView tasksAmount = (TextView)v.findViewById(R.id.tasks_amount_tv);
        TextView usersAmount = (TextView)v.findViewById(R.id.members_amount_tv);

        Log.d("hallo CLASS COLOR", mGroupslist.get(i).color);

        StringCompare(mGroupslist.get(i).color);



        String xxx = String.valueOf(mGroupslist.get(i).color);
        String m = "magenta";

        Log.d("hallo STRING", xxx);
        if (m.equals("magenta")) {
            Log.d("hallo magenta", "");
        }
        if (xxx.matches("cyan")) {
            Log.d("hallo cyan", "");
        }

        groupColor c = groupColor.valueOf(mGroupslist.get(i).color);

        switch (c) {
            case magenta:
                Log.d("hallo magenta", "");
                break;
            case cyan:
                Log.d("hallo cyan", "");
                break;
            case yellow:
                Log.d("hallo yellow", "");
                break;
            case blue:
                Log.d("hallo blue", "");
                break;
            case red:
                Log.d("hallo red", "");
                break;
        }

        // Change textviews to according values
        groupName.setText(mGroupslist.get(i).groupname);
        tasksAmount.setText(mGroupslist.get(i).tasksAmount);
        usersAmount.setText(mGroupslist.get(i).usersAmount);

        v.setTag(mGroupslist.get(i).groupname);
        return v;
    }

    private void StringCompare (String color) {

        switch (color) {
            case "magenta":
                Log.d("hallo magenta", "");
                break;
            case "cyan":
                Log.d("hallo cyan", "");
                break;
            case "yellow":
                Log.d("hallo yellow", "");
                break;
            case "blue":
                Log.d("hallo blue", "");
                break;
            case "red":
                Log.d("hallo red", "");
                break;
        }


    }


    private enum groupColor {
        magenta, cyan, yellow, blue, red;
    }
}
