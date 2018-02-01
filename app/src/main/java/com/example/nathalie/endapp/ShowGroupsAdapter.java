package com.example.nathalie.endapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by minor on 30/01/2018.
 */

public class ShowGroupsAdapter extends ArrayAdapter {
    private Context mContext;
    private List<Group> mGroupslist;

//    public ShowGroupsAdapter(Context mContext, List<Group> mGroupslist) {
//        this.mContext = mContext;
//        this.mGroupslist = mGroupslist;
//    }


    public ShowGroupsAdapter(@NonNull Context context, int resource, @NonNull List<Group> mGroupslist) {
        super(context, resource, mGroupslist);

        this.mContext = context;
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

        // Get views from XML
        TextView groupName = (TextView)v.findViewById(R.id.groupname_tv);
        TextView tasksAmount = (TextView)v.findViewById(R.id.tasks_amount_tv);
        TextView tasks = (TextView)v.findViewById(R.id.textView9);
        TextView usersAmount = (TextView)v.findViewById(R.id.members_amount_tv);
        TextView colorMarker = (TextView)v.findViewById(R.id.color_marker_tv);


        // Get groupcolor to string
        String color = mGroupslist.get(i).getColor();

        // Change textview text to groupview
        groupName.setText(mGroupslist.get(i).groupname);

        // Change groupname textview to group color (if statements because switch case does somehow
        // not work))
        if(color.equals("magenta"))
            colorMarker.setTextColor(mContext.getResources().getColor(R.color.colorMagenta));
        else if(color.contains("cyan"))
            colorMarker.setTextColor(mContext.getResources().getColor(R.color.colorCyan));
        else if(color.contains("yellow"))
            colorMarker.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
        else if(color.contains("blue"))
            colorMarker.setTextColor(mContext.getResources().getColor(R.color.colorBlue));
        else if(color.contains("red"))
            colorMarker.setTextColor(mContext.getResources().getColor(R.color.colorRed));

        // Change textview to task if group has only one task
        if (Integer.valueOf(mGroupslist.get(i).tasksAmount) == 1) {
            tasks.setText("task");
        }

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
