package com.example.nathalie.endapp;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupnameFragment extends Fragment implements View.OnClickListener {
    private Button addGroup;
    private EditText enteredGroupName;
    private TextView magentaTV, cyanTV, yellowTV, blueTV, redTV
            ,selectMagenta, selectCyan, selectYellow, selectBlue, selectRed;

    private String groupColor = "magenta";

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.color_magenta_tv:
                // Show that clicked color is selected
                allColorSelectorsInvisible();
                selectMagenta.setVisibility(View.VISIBLE);

                // Set group color to chosen color
                groupColor = "magenta";
                hideKeyboard(getContext(), v);
                break;
            case R.id.color_cyan_tv:
                // Show that clicked color is selected
                allColorSelectorsInvisible();
                selectCyan.setVisibility(View.VISIBLE);

                // Set group color to chosen color
                groupColor = "cyan";
                hideKeyboard(getContext(), v);
                break;
            case R.id.color_yellow_tv:
                // Show that clicked color is selected
                allColorSelectorsInvisible();
                selectYellow.setVisibility(View.VISIBLE);

                // Set group color to chosen color
                groupColor = "yellow";
                hideKeyboard(getContext(), v);
                break;
            case R.id.color_blue_tv:
                // Show that clicked color is selected
                allColorSelectorsInvisible();
                selectBlue.setVisibility(View.VISIBLE);

                // Set group color to chosen color
                groupColor = "blue";
                hideKeyboard(getContext(), v);
                break;
            case R.id.color_red_tv:
                // Show that clicked color is selected
                allColorSelectorsInvisible();
                selectRed.setVisibility(View.VISIBLE);

                // Set group color to chosen color
                groupColor = "red";
                hideKeyboard(getContext(), v);
                break;
            case R.id.add_group_button:
                // Make sure user fills in a groupname and meets name requirements
                if (!LoginActivity.checkInputRequirements(enteredGroupName.getText().toString(),
                        getActivity()) || LoginActivity.checkIfBlank(enteredGroupName.getText()
                                .toString(), getActivity())) {
                    return;
                }

                // Re-directs to fragment to add members to newly created group
                sendToAddGroup();
                }
    }

    // Sets all color selectors textviews to invisible
    public void allColorSelectorsInvisible () {
        selectMagenta.setVisibility(View.INVISIBLE);
        selectCyan.setVisibility(View.INVISIBLE);
        selectYellow.setVisibility(View.INVISIBLE);
        selectBlue.setVisibility(View.INVISIBLE);
        selectRed.setVisibility(View.INVISIBLE);
    }

    // Re-directs to find users fragment so user can add group members
    public void sendToAddGroup () {
        // Create bundle to transfer groupname and -color to next fragment
        Bundle bundle = new Bundle();
        bundle.putString("Group name", enteredGroupName.getText().toString());
        bundle.putString("Group color", groupColor);

        // Initialize group details fragment and set arguments
        FindUsersFragment findUsersFragment = new FindUsersFragment();
        findUsersFragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace fragment
        transaction.replace(R.id.frame, findUsersFragment);

        // Commit the transaction
        transaction.addToBackStack(null).commit();
    }

    // Hides keyboard from screen
    public void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
