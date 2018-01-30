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
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupnameFragment extends Fragment implements View.OnClickListener {
    private Button addGroup;
    private EditText enteredGroupName;
    private String groupColor;
    private TextView magentaTV, cyanTV, yellowTV, blueTV, redTV
            ,selectMagenta, selectCyan, selectYellow, selectBlue, selectRed;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_groupname, container, false);

        // Get views from XML
        addGroup = (Button) view.findViewById(R.id.add_group_button);
        enteredGroupName = (EditText) view.findViewById(R.id.groupname_editText);
        magentaTV = (TextView)view.findViewById(R.id.color_magenta_tv);
        cyanTV = (TextView)view.findViewById(R.id.color_cyan_tv);
        yellowTV = (TextView)view.findViewById(R.id.color_yellow_tv);
        blueTV = (TextView)view.findViewById(R.id.color_blue_tv);
        redTV = (TextView)view.findViewById(R.id.color_red_tv);
        selectMagenta = (TextView)view.findViewById(R.id.background_m);
        selectCyan = (TextView)view.findViewById(R.id.background_c);
        selectYellow = (TextView)view.findViewById(R.id.background_y);
        selectBlue = (TextView)view.findViewById(R.id.background_b);
        selectRed = (TextView)view.findViewById(R.id.background_r);

        // Set on click listeners
        magentaTV.setOnClickListener(this);
        cyanTV.setOnClickListener(this);
        yellowTV.setOnClickListener(this);
        blueTV.setOnClickListener(this);
        redTV.setOnClickListener(this);
        addGroup.setOnClickListener(this);

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

    // Shows entered String as alert dialog
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.color_magenta_tv:
                allColorSelectorsInvisible();
                selectMagenta.setVisibility(View.VISIBLE);
                groupColor = "magenta";
                break;
            case R.id.color_cyan_tv:
                allColorSelectorsInvisible();
                selectCyan.setVisibility(View.VISIBLE);
                groupColor = "cyan";
                break;
            case R.id.color_yellow_tv:
                allColorSelectorsInvisible();
                selectYellow.setVisibility(View.VISIBLE);
                groupColor = "yellow";
                break;
            case R.id.color_blue_tv:
                allColorSelectorsInvisible();
                selectBlue.setVisibility(View.VISIBLE);
                groupColor = "blue";
                break;
            case R.id.color_red_tv:
                allColorSelectorsInvisible();
                selectRed.setVisibility(View.VISIBLE);
                groupColor = "red";
                break;
            case R.id.add_group_button:
                // Make sure fills in a group name
                if (checkNameRequirements(enteredGroupName.getText().toString())) {
                    // Create bundle to transfer groupname to next fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("Group name", enteredGroupName.getText().toString());
                    bundle.putString("Group color", groupColor);


                    FindUsersFragment findUsersFragment = new FindUsersFragment();
                    findUsersFragment.setArguments(bundle);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    // Replace fragment
                    transaction.replace(R.id.frame, findUsersFragment);

                    // Commit the transaction
                    transaction.addToBackStack(null).commit();
                }
        }
    }

    public void allColorSelectorsInvisible () {
        selectMagenta.setVisibility(View.INVISIBLE);
        selectCyan.setVisibility(View.INVISIBLE);
        selectYellow.setVisibility(View.INVISIBLE);
        selectBlue.setVisibility(View.INVISIBLE);
        selectRed.setVisibility(View.INVISIBLE);

    }
}
