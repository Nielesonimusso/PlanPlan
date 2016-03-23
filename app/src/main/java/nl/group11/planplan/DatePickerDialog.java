package nl.group11.planplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Anne on 22/03/2016.
 */
public class DatePickerDialog extends DialogFragment {

    RelativeLayout datePickerLayout;
    DatePicker datePicker;
    AddDialog addDialog;
    TextView textView;
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
                        // Pass this time back to the AddDialog
                        day = datePicker.getDayOfMonth();
                        month = datePicker.getMonth() + 1;
                        year = datePicker.getYear();
                        addDialog.setDate(textView, day, month, year);
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
        datePickerSetUp();
        return builder.create();
    }

    /* Sets references to UI elements. */
    public void referenceUIElements() {
        datePicker = (DatePicker) (datePickerLayout.getChildAt(0));
    }

    /* Sets a listener for the date picker. */
    public void datePickerSetUp() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
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
