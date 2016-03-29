package nl.group11.planplan;

/**
 * Created by s145576 on 28-3-2016.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class TimePickerDialog extends Activity {

    public int hour;
    public int min;
    public static boolean changeStartTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.timePickerStyle);
        setContentView(R.layout.dialog_timepicker);

        final TimePicker tp = (TimePicker) findViewById(R.id.timePicker1);
        // Setting the 24 hour view for the timePicker
        tp.setIs24HourView(true);

        // When the cancel button is clicked the dialog should close
        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                });

        // We listen if the setTimeButton is clicked. if it is clicked we save the entered time.
        final Button setTimeButton = (Button) findViewById(R.id.setTimeButton);
        setTimeButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // Two variables that correspond to the device time in hours and minutes
                        hour = tp.getCurrentHour();
                        min = tp.getCurrentMinute();
                        // If we change the start time we want to add it to the start time textField
                        if(changeStartTime) {
                            // A bunch of if statements to ensure the right time format is used
                            if (hour < 10 && min < 10) {
                                AddDialog.startTime.setText(new StringBuilder().append("0" + hour).append(":").append("0" + min));
                            } else if (hour < 10 && min >= 10) {
                                AddDialog.startTime.setText(new StringBuilder().append("0" + hour).append(":").append(min));
                            } else if (hour >= 10 && min < 10) {
                                AddDialog.startTime.setText(new StringBuilder().append(hour).append(":").append("0" + min));
                            } else {
                                AddDialog.startTime.setText(new StringBuilder().append(hour).append(":").append(min));
                            }
                        } else {
                            // A bunch of if statements to ensure the right time format is used
                            if (hour < 10 && min < 10) {
                                AddDialog.endTime.setText(new StringBuilder().append("0" + hour).append(":").append("0" + min));
                            } else if (hour < 10 && min >= 10) {
                                AddDialog.endTime.setText(new StringBuilder().append("0" + hour).append(":").append(min));
                            } else if (hour >= 10 && min < 10) {
                                AddDialog.endTime.setText(new StringBuilder().append(hour).append(":").append("0" + min));
                            } else {
                                AddDialog.endTime.setText(new StringBuilder().append(hour).append(":").append(min));
                            }
                        }
                        finish();
                    }
                });
    }
}