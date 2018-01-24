package com.example.nathalie.endapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupnameFragment extends Fragment {
    private Button addGroup;
    private EditText enteredGroupName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groupname, container, false);

        // Get views from XML
        addGroup = (Button) view.findViewById(R.id.add_group_button);
        enteredGroupName = (EditText) view.findViewById(R.id.groupname_editText);

        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                // Make sure fills in a group name
                if (enteredGroupName.getText().toString().equals("")) {
                    hideKeyboard(getContext(), v);
                    Toast.makeText(getActivity(), "Please fill in a group name", Toast.LENGTH_SHORT).show();
                } else {

                    // Create bundle to transfer groupname to next fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("Group name", enteredGroupName.getText().toString());


                    FindUsersFragment findUsersFragment = new FindUsersFragment();
                    findUsersFragment.setArguments(bundle);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    // Replace fragment
                    transaction.replace(R.id.frame, findUsersFragment);
                    transaction.addToBackStack(null);


                    killFrag();

                    // Commit the transaction
                    transaction.commit();
                }
            }
        });
        return view;
    }

    // Hides keyboard from screen
    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void killFrag() {
        addGroup.setVisibility(View.GONE);
        enteredGroupName.setVisibility(View.GONE);
        hideKeyboard(getContext(), getView());
    }


}
