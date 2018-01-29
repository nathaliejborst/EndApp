package com.example.nathalie.endapp;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
                if (checkNameRequirements(enteredGroupName.getText().toString())) {
                    // Create bundle to transfer groupname to next fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("Group name", enteredGroupName.getText().toString());

                    FindUsersFragment findUsersFragment = new FindUsersFragment();
                    findUsersFragment.setArguments(bundle);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    // Replace fragment
                    transaction.replace(R.id.frame, findUsersFragment);

                    // Commit the transaction
                    transaction.addToBackStack(null).commit();
                }
            }
        });
        return view;
    }

    public boolean checkNameRequirements (String name) {
        // Make sure user does not enter characters Firebase can't handle
        if(name.contains(".") || name.contains("#") || name.contains("$") || name.contains("[") || name.contains("]")) {
            showAlert("Groupname can't contain '.', '#', '$', '[', or ']'");
            return false;
        }
        // Checks does not enter a blank group name
        if (name.equals("") || name == null || name.matches("")) {
            showAlert("Please fill in a group name");
            return false;
        }
        return true;
    }

    // Hides keyboard from screen
    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showAlert (String alert) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle(alert);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
