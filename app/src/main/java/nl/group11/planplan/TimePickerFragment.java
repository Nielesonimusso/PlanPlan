package nl.group11.planplan;

/**
 * Created by s145576 on 28-3-2016.
 */

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;


public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    AddDialog addDialog;
    TextView textView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), R.style.Theme_Dialog, this, hour, min, DateFormat.is24HourFormat(getActivity()));
        return dialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        addDialog.setTime(textView, hourOfDay, minute);
    }

    /* Sets corresponding add dialog. */
    public void setAddDialog(AddDialog dialog) {
        addDialog = dialog;
    }

    /* Sets the text view to update in the add dialog. */
    public void setTextView(TextView textView) {
        this.textView = textView;
    }
}