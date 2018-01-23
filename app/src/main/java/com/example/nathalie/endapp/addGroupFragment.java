package com.example.nathalie.endapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class addGroupFragment extends Fragment {
    private String username, userID, email, groupName;
    private ArrayList<String> usersToAdd = new ArrayList<String>();
    private ArrayList<String> usersIDList = new ArrayList<String>();

    ListView toAddList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_group, container, false);

        // Get groupname from previous fragment
        groupName = getArguments().getString("Group name");
        usersToAdd = getArguments().getStringArrayList("User list");
        usersIDList = getArguments().getStringArrayList("ID list");
        email = getArguments().getString("User email");

        // Get views from XML
        toAddList = (ListView) view.findViewById(R.id.users_to_add_lv);

        // Add user details to lists
        usersToAdd.add(username);
        usersIDList.add(userID);

        fillSimpleListView(usersToAdd);

        TextView addg = (TextView) view.findViewById(R.id.adddddd);
        addg.setText(groupName + " "  + email);


        return view;
    }

    public void fillSimpleListView (ArrayList list) {
        // Initialize list adapter
        ListAdapter theAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);

        // Set the adapter
        toAddList.setAdapter(theAdapter);
        toAddList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                Toast.makeText(getActivity(), String.valueOf(toAddList.getItemAtPosition(i)), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
