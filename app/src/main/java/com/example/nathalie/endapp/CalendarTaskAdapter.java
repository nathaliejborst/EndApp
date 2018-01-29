package com.example.nathalie.endapp;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by minor on 29/01/2018.
 */

public class CalendarTaskAdapter extends BaseAdapter {
    private Context mContext;
    private List<Task> mTasklist;
    ArrayList<String> frequencies = new ArrayList<String>();

    public CalendarTaskAdapter(Context mContext, List<Task> mTasklist) {
        this.mContext = mContext;
        this.mTasklist = mTasklist;
        this.frequencies = frequencies;

        // Position in lists corresponds to the correct frequency in Firebase
        frequencies.add("daily");
        frequencies.add("weekly");
        frequencies.add("monthly");

    }


    @Override
    public int getCount() {
        return mTasklist.size();
    }

    @Override
    public Object getItem(int position) {
        return mTasklist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(mContext, R.layout.item_task_list, null);
        TextView taskName = (TextView)v.findViewById(R.id.taskname_tv);
        TextView frequency = (TextView)v.findViewById(R.id.frequency_tv);
        TextView startdate = (TextView)v.findViewById(R.id.startdate_tv);

        Log.d("hallo freq size", " " + frequencies.size());

        // Convert startdate to string data
        Long l = Long.parseLong(mTasklist.get(i).startdate);
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        String startdateString = f.format(new Date(l));

        // Change textviews to according values
        taskName.setText(mTasklist.get(i).taskname);
        frequency.setText(frequencies.get(mTasklist.get(i).frequency));
        startdate.setText(startdateString);

        v.setTag(String.valueOf(mTasklist.get(i).startdate));

        return v;
    }
}
