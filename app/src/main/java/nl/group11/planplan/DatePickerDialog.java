package nl.group11.planplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Anne on 22/03/2016.
 */
public class DatePickerDialog extends DialogFragment {

    RelativeLayout datePickerLayout;
    DatePicker datePicker;
    AddDialog addDialog;
    int day;
    int month;
    int year;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater li = getActivity().getLayoutInflater();;
        datePickerLayout = (RelativeLayout) li.inflate(R.layout.dialog_datepicker, null);
        builder.setTitle("Pick a date...")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    // When the user clicks add, add item to planning
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: pass this time back to AddDialog
                        day = datePicker.getDayOfMonth();
                        month = datePicker.getMonth() + 1;
                        year = datePicker.getYear();
                        addDialog.setDate(day, month, year);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // When the user clicks cancel, close dialog
                        dialog.cancel();
                    }
                })
                .setView(datePickerLayout);
        referenceUIElements();
        addDatePickerListener();
        return builder.create();
    }

    /* Sets references to UI elements. */
    public void referenceUIElements() {
        datePicker = (DatePicker) (datePickerLayout.getChildAt(0));
    }

    /* Sets a listener for the date picker. */
    public void addDatePickerListener() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int newYear, int newMonth,
                                              int newDay) {
                        day = newDay;
                        month = newMonth;
                        year = newYear;
                        System.out.println(day + "/" + month + "/" + year + "hoi ik ben hier");
                    }
                });
    }

    /* Sets corresponding add dialog. */
    public void setAddDialog(AddDialog dialog) {
        addDialog = dialog;
    }
}
