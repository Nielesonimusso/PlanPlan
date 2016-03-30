package nl.group11.planplan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    AddDialog addDialog;
    TextView textView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), R.style.Theme_Dialog, this, year, month, day);
        return dialog;
    }

    /* Sets corresponding add dialog. */
    public void setAddDialog(AddDialog dialog) {
        addDialog = dialog;
    }

    /* Sets the text view to update in the add dialog. */
    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        addDialog.setDate(textView, dayOfMonth, monthOfYear + 1, year);
    }
}
