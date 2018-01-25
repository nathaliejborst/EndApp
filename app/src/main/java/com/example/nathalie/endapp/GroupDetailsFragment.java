package com.example.nathalie.endapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupDetailsFragment extends Fragment {
    TextView groupNameTV;
    String groupName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_details, container, false);

        // Get views from XML
        groupNameTV = (TextView) view.findViewById(R.id.group_name_tv);

        // Get selected group name from previous fragment
        groupName  = getArguments().getString("Group name");

        Log.d("hallo group name bundle", "" + groupName);

        groupNameTV.setText(groupName);

        return view;
    }

}
