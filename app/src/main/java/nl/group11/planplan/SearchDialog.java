package nl.group11.planplan;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by s140442 on 09/03/2016.
 */
public class SearchDialog extends DialogFragment {


    EditText searchBar;
    Spinner optionsSpinner;
    CheckBox optionsCheckbox;
    GPSTracker gps;
    LinearLayout layout;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater li = getActivity().getLayoutInflater();;
        layout = (LinearLayout) li.inflate(R.layout.dialog_search, null);
        builder.setTitle("Search")
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    Location location;
                    int range;
                    String locationString;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        range = Integer.parseInt(optionsSpinner.getSelectedItem().toString().replace(" km", ""));
                        if (optionsCheckbox.isChecked()) {
                            location = gps.getLocation();
                            callAPIs();
                        } else {
                            locationString = searchBar.getText().toString();
                            APIHandler.stringToLocation(locationString, new APIHandler.Callback<Location>() {
                                @Override
                                public void onItem(Location result) {
                                    location = result;
                                    callAPIs();
                                }
                            });
                        }
                    }

                    public void callAPIs() {
                        //TODO api calls
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setView(layout);
        referenceUIElements();
        addOptionsView();
        return builder.create();
    }

    public void referenceUIElements() {
        //TODO needs testing
        /*searchBar = (EditText) getActivity().findViewById(R.id.searchbar);
        optionsSpinner = (Spinner) getActivity().findViewById(R.id.rangeSpinner);
        optionsCheckbox = (CheckBox) getActivity().findViewById(R.id.searchCheckbox);*/

        searchBar = (EditText)((LinearLayout) layout.getChildAt(0)).getChildAt(1);
        optionsSpinner = (Spinner)((LinearLayout) layout.getChildAt(1)).getChildAt(1);
        optionsCheckbox = (CheckBox)((LinearLayout) layout.getChildAt(1)).getChildAt(3);
    }

    private void addOptionsView() {
        //list with range options
        List list = new ArrayList<String>();
        list.add("5 km");
        list.add("10 km");
        list.add("20 km");
        list.add("50 km");

        ArrayAdapter<String> spinnerOptions = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
        spinnerOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        optionsSpinner.setAdapter(spinnerOptions);
    }
}
