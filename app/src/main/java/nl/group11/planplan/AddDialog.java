package nl.group11.planplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Anne on 22/03/2016.
 */
public class AddDialog extends DialogFragment {

    TextView startDate;
    public static TextView startTime;
    TextView endDate;
    public static TextView endTime;
    TextView duration;
    Button startDateButton;
    Button endDateButton;
    Button startTimeButton;
    Button endTimeButton;
    RelativeLayout addLayout;
    final AddDialog addDialog = this;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater li = getActivity().getLayoutInflater();;
        addLayout = (RelativeLayout) li.inflate(R.layout.dialog_add, null);
        builder.setTitle("Add to planning (test)")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    // When the user clicks add, add item to planning
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: add item to planning
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // When the user clicks cancel, close dialog
                        dialog.cancel();
                    }
                })
                .setView(addLayout);
        referenceUIElements();
        setDatesAndTimes();
        addButtonListeners();
        return builder.create();
    }

    /* Sets references to UI elements. */
    public void referenceUIElements() {
        startDate = (TextView)(addLayout.getChildAt(1));
        startDateButton = (Button) (addLayout.getChildAt(2));
        startTime = (TextView)(addLayout.getChildAt(4));
        startTimeButton = (Button) (addLayout.getChildAt(5));
        endDate = (TextView)(addLayout.getChildAt(7));
        endDateButton = (Button) (addLayout.getChildAt(8));
        endTime = (TextView)(addLayout.getChildAt(10));
        endTimeButton = (Button) (addLayout.getChildAt(11));
        duration = (TextView)(addLayout.getChildAt(13));
    }

    /* Sets the initial dates and times. */
    private void setDatesAndTimes() {
        // TODO: get dates and times from Item and display them
        // Currently the present day is shown and times are hard-coded in XML
        final Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        // Set current date into text view
        startDate.setText(new StringBuilder()
                .append(d).append("-").append(m).append("-")
                .append(y));
        endDate.setText(new StringBuilder()
                .append(d).append("-").append(m).append("-")
                .append(y));
    }

    /* Adds listeners for Change buttons. */
    private void addButtonListeners() {
        startDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open start date picker
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                DatePickerDialog startDatePicker = new DatePickerDialog();
                startDatePicker.setAddDialog(addDialog);
                startDatePicker.setTextView(startDate);
                startDatePicker.show(trans, "Start date picker");
            }
        });
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open start time picker
                //setting the boolean to true as an indication that we are changing the start time
                TimePickerDialog.changeStartTime = Boolean.TRUE;
                Intent intent = new Intent(getActivity().getApplicationContext(), TimePickerDialog.class);
                startActivity(intent);
            }
        });
        endDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open end date picker
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                DatePickerDialog endDatePicker = new DatePickerDialog();
                endDatePicker.setAddDialog(addDialog);
                endDatePicker.setTextView(endDate);
                endDatePicker.show(trans, "End date picker");
            }
        });
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open end time picker
                //setting the boolean to false as an indication that we are not changing the start time but the stop time
                TimePickerDialog.changeStartTime = Boolean.FALSE;
                Intent intent = new Intent(getActivity().getApplicationContext(), TimePickerDialog.class);
                startActivity(intent);
            }
        });
    }

    public void setDate(TextView textView, int day, int month, int year) {
        textView.setText("" + Integer.toString(day) + "-" + Integer.toString(month) + "-" +
                Integer.toString(year));
        // TODO: update duration text view
    }

}
