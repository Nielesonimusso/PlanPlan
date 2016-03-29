package nl.group11.planplan;

/**
 * Created by s145576 on 28-3-2016.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
        tp.setIs24HourView(true);

        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        final Button cancelButton = (Button) findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        finish();
                    }
                });

        // We listen if the setTimeButton is clicked, if it is clicked we save the entered time.
        final Button setTimeButton = (Button) findViewById(R.id.setTimeButton);
        setTimeButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        hour = tp.getCurrentHour();
                        min = tp.getCurrentMinute();
                        //TextView time = (TextView) findViewById(R.id.startTime);//niet textview2 maar iets op dialog_add
                        if (changeStartTime) {
                            AddDialog.startTime.setText(new StringBuilder().append(hour).append(":").append(min)); //niet mainview maar dialog_add
                        } else {
                            AddDialog.endTime.setText(new StringBuilder().append(hour).append(":").append(min));
                        }
                        finish();
                    }
                });
    }

}