package nl.group11.planplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anne on 22/03/2016.
 */
public class AddDialog extends DialogFragment {

    TextView date;
    TextView startTime;
    TextView duration;
    TextView endTime;
    Button dateButton;
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
        addButtonListeners();
        return builder.create();
    }

    /* Sets references to UI elements. */
    public void referenceUIElements() {
        date = (TextView)(addLayout.getChildAt(1));
        dateButton = (Button) (addLayout.getChildAt(2));
        startTime = (TextView)(addLayout.getChildAt(4));
        startTimeButton = (Button) (addLayout.getChildAt(5));
        endTime = (TextView)(addLayout.getChildAt(7));
        endTimeButton = (Button) (addLayout.getChildAt(8));
        duration = (TextView)(addLayout.getChildAt(11));
    }

    /* Adds listeners for Change buttons. */
    private void addButtonListeners() {
        dateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open date picker
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                DatePickerDialog datePicker = new DatePickerDialog();
                datePicker.setAddDialog(addDialog);
                datePicker.show(trans, "datepicker");
            }
        });
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open start time picker
            }
        });
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open end time picker
            }
        });
    }

    public void setDate(int day, int month, int year) {
        date.setText("" + Integer.toString(day) + "-" + Integer.toString(month) + "-" +
                Integer.toString(year));
    }

}
