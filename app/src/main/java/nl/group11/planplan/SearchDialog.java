package nl.group11.planplan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by s140442 on 09/03/2016.
 */
@SuppressLint("ValidFragment")
public class SearchDialog extends DialogFragment {

    EditText searchBar;
    Spinner optionsSpinner;
    CheckBox optionsCheckbox;
    GPSTracker gps;
    LinearLayout layout;
    SearchListener listener;

    @SuppressLint("ValidFragment")
    SearchDialog(SearchListener listener) {
        this.listener = listener;
    }

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
                            if (!gps.canGetLocation() || location == null) {
                                gps.showSettingsAlert();
                            } else {
                                System.out.println("latlong: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
                                callAPIs();
                            }
                        } else {
                            locationString = searchBar.getText().toString();
                            if (!locationString.isEmpty()) {
                                APIHandler.stringToLocation(locationString, new APIHandler.Callback<Location>() {
                                    @Override
                                    public void onItem(Location result) {
                                        location = result;
                                        callAPIs();
                                    }
                                });
                            }
                        }
                    }

                    public void callAPIs() {
                        SearchDialog.this.listener.onSearch(APIHandler.locationToLatLngString(location), range);
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
        addCheckBoxListener();
        return builder.create();
    }

    /* Sets references to UI elements. */
    public void referenceUIElements() {
        searchBar = (EditText)((LinearLayout) layout.getChildAt(0)).getChildAt(0);
        optionsSpinner = (Spinner)((LinearLayout) layout.getChildAt(1)).getChildAt(1);
        optionsCheckbox = (CheckBox)((LinearLayout) layout.getChildAt(1)).getChildAt(3);
    }

    /* Adds checkbox listener for current location checkbox. */
    private void addCheckBoxListener() {
        optionsCheckbox.setOnCheckedChangeListener(new checkBoxChangeListener());
    }

    /* Adds the options for range. */
    private void addOptionsView() {
        List list = new ArrayList<String>();
        list.add("5 km");
        list.add("10 km");
        list.add("20 km");
        list.add("50 km");

        ArrayAdapter<String> spinnerOptions = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
        spinnerOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        optionsSpinner.setAdapter(spinnerOptions);

    }

    /* Listener for the checkbox.
     * Disables the location search bar if "current location" is enabled.
     */
    class checkBoxChangeListener implements CheckBox.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                searchBar.setEnabled(false);
            } else {
                searchBar.setEnabled(true);
            }
        }
    }

    public void setGPS(GPSTracker gps) {
        this.gps = gps;
    }

    interface SearchListener {
        void onSearch(String location, int radius);
    }
}
