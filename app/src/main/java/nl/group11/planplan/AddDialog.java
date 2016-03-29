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
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Anne on 22/03/2016.
 */
public class AddDialog extends DialogFragment {

    TextView startDate;
    public TextView startTime;
    TextView endDate;
    public TextView endTime;
    TextView duration;
    Button startDateButton;
    Button endDateButton;
    Button startTimeButton;
    Button endTimeButton;
    RelativeLayout addLayout;
    final AddDialog addDialog = this;
    Dialog dialog;

    DialogResult dialogResult; // callback

    Date userStart; // the user-specified start date
    Date userEnd; // the user-specified end date
    boolean add; // whether or not to add the item to planning
    Calendar c; // the current date
    Calendar startCal; // the start date of the event
    Calendar endCal; // the end date of the event

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        //LayoutInflater li = getActivity().getLayoutInflater();;
        addLayout = (RelativeLayout) inflater.inflate(R.layout.dialog_add, null);
        builder.setTitle("Add to planning (test)")
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    // When the user clicks add, validate input and add item to planning
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String userStartDateString = startDate.getText().toString();
                        String userEndDateString = endDate.getText().toString();
                        String userStartTimeString = startTime.getText().toString();
                        String userEndTimeString = endTime.getText().toString();
                        String userStartString = userStartDateString + " " + userStartTimeString;
                        String userEndString = userEndDateString + " " + userEndTimeString;

                        userStart = formatDate(userStartString);
                        userEnd = formatDate(userEndString);

                        if (userEnd.before(userStart)) {
                            // The end date is after the start date; invalid, do not add to planning
                            add = false;
                            CharSequence warning = "Not added to planning. The end date and time " +
                                    "must be after the start date and time.";
                            Toast.makeText(getActivity(), warning, Toast.LENGTH_LONG).show();
                            // This closes the fragment. Tried to fix this by adding a custom
                            // button listener and leaving this blank, but getButton(int)
                            // cannot be resolved... I don't know why. TODO don't close fragment.
                            dialog.cancel();
                        } else {
                            // Valid dates. Add to planning
                            add = true;
                        }
                        // TODO add item to planning. This callback stuff isn't working
                        if (dialogResult != null) {
                            dialogResult.finish(userStart, userEnd, add);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do not add item to planning
                        add = false;
                        // TODO don't add item to planning. This callback stuff isn't working
                        if (dialogResult != null) {
                            dialogResult.finish(userStart, userEnd, add);
                        }
                        dialog.cancel();
                    }
                })
                .setView(addLayout);
        referenceUIElements();
        displayDatesAndTimes();
        addButtonListeners();
        dialog = builder.create();
        return dialog;
    }

    /** Sets references to UI elements. */
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

    /** Sets the dates and times to contain the item's dates and times. */
    public void setDatesAndTimes(Date start, Date end) {
        if (start != null) {
            startCal = dateToCalendar(start);
        }
        if (end != null) {
            endCal = dateToCalendar(end);
        }
    }

    /** Sets the title to contain the item name. */
    public void setTitle(String title) {
        // TODO: implement setTitle
        // I don't know how to do this. This is a null object reference:
        // this.getDialog().setTitle("Add " + title + " to planning");
        // And this too:
        // getDialog().setTitle("Add " + title + " to planning");
        // And this gives OutOfMemoryError:
        // this.setTitle("Add " + title + " to planning");
    }

    /** Displays the initial dates and times. */
    private void displayDatesAndTimes() {
        c = Calendar.getInstance();

        if (startCal != null) {
            // Show item start date in start date/time text view
            putDate(startDate, startTime, startCal);
        } else {
            // Show current date in start date/time text view
            putDate(startDate, startTime, c);
        }

        if (endCal != null) {
            // Show item end date in end date/time text view
            putDate(endDate, endTime, endCal);
        } else {
            // Show current date in end date/time text view
            putDate(endDate, endTime, c);
        }
    }

    /** Puts a Calendar's dates and times in a TextView. */
    private void putDate(TextView dateText, TextView timeText, Calendar c) {
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);
        int hr = c.get(Calendar.HOUR_OF_DAY);
        String hour = Integer.toString(hr);
        int min = c.get(Calendar.MINUTE);
        String minute = Integer.toString(min);
        if (min < 10) {
            // Add leading 0
            minute = "0" + minute;
        }
        if (hr < 10) {
            // Add leading 0
            hour = "0" + hour;
        }

        dateText.setText(new StringBuilder()
                .append(d).append("-").append(m).append("-")
                .append(y));
        timeText.setText(new StringBuilder()
                .append(hour).append(":").append(minute));
    }

    /** Adds listeners for Change buttons. */
    private void addButtonListeners() {
        startDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open start date picker
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                DatePickerFragment startDatePicker = new DatePickerFragment();
                startDatePicker.setAddDialog(addDialog);
                startDatePicker.setTextView(startDate);
                startDatePicker.show(trans, "Start date picker");
            }
        });
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open start time picker
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                TimePickerFragment startTimePicker = new TimePickerFragment();
                startTimePicker.setAddDialog(addDialog);
                startTimePicker.setTextView(startTime);
                startTimePicker.show(trans, "Start time picker");
            }
        });
        endDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open end date picker
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                DatePickerFragment endDatePicker = new DatePickerFragment();
                endDatePicker.setAddDialog(addDialog);
                endDatePicker.setTextView(endDate);
                endDatePicker.show(trans, "End date picker");
            }
        });
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Open end time picker
                FragmentTransaction trans = getFragmentManager().beginTransaction();
                TimePickerFragment startTimePicker = new TimePickerFragment();
                startTimePicker.setAddDialog(addDialog);
                startTimePicker.setTextView(endTime);
                startTimePicker.show(trans, "End time picker");
            }
        });
    }

    /** Displays date picked on date picker in a text view. */
    public void setDate(TextView textView, int day, int month, int year) {
        textView.setText("" + Integer.toString(day) + "-" + Integer.toString(month) + "-" +
                Integer.toString(year));
        updateDuration();
    }

    public void setTime(TextView textView, int hour, int min) {
        String hourStr = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minStr = min < 10 ? "0" + min : String.valueOf(min);
        textView.setText(hourStr + ":" + minStr);
    }

    /** Sets duration text view to end - start. */
    public void updateDuration() {
        // TODO: update duration text view
    }

    /** Converts string in dd-M-yyyy hh:mm format to be a Date object. */
    // TODO: update to incorporate time as well
    private Date formatDate(String string) {
        DateFormat format = new SimpleDateFormat("dd-M-yyyy HH:mm", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(string);
            System.out.println("The parsed date is " + date);
        } catch (ParseException e) {
            System.out.println("Parsing date exception" + e);
        }
        return date;
    }

    /** Changes date to calendar (date format is deprecated). */
    public static Calendar dateToCalendar(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /* Iets met een callback dat niet werkt :) Zie ook Item class setDialogResult */
    public void setDialogResult(DialogResult dr) {
        dialogResult = dr;
    }
    public interface DialogResult {
        void finish(Date startResult, Date endResult, boolean toAdd);
    }

}
