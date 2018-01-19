package com.example.nathalie.endapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.util.Calendar;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

/**
 * Created by Nathalie on 15-1-2018.
 */

public class CalenderDialog extends android.support.v4.app.DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        Toast.makeText(getActivity().getApplicationContext(), currentDate, Toast.LENGTH_SHORT).show();

        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }

}
