package com.example.batere3a.joggingpartner;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by adity on 2/19/2018.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    /**
     * Creates the time picker dialog with the current time from Calendar.
     * @param savedInstanceState    Saved instance
     * @return TimePickerDialog     The time picker dialog
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker.
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        return dialog;
        // Create a new instance of TimePickerDialog and return it.
        //return new TimePickerDialog(getActivity(), this, hour, minute,
        //        DateFormat.is24HourFormat(getActivity()));
    }

    /**
     * Grabs the time and converts it to a string to pass
     * to the Main Activity in order to show it with processTimePickerResult().
     * @param view          The time picker view
     * @param hourOfDay     The hour chosen
     * @param minute        The minute chosen
     */
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Set the activity to the Main Activity.
        MakeOrderActivity activity = (MakeOrderActivity) getActivity();
        // Invoke Main Activity's processTimePickerResult() method.
        activity.processTimePickerResult(hourOfDay, minute);
    }
}
