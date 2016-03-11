package nl.group11.planplan;

import android.app.AlertDialog;
import android.content.Context;
import android.location.Location;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by s140442 on 09/03/2016.
 */
public class SearchDialog extends AlertDialog {

    //main layout
    LinearLayout layout;
    //layout for the search bar
    LinearLayout searchLayout;
    SearchView searchBar;
    TextView searchText;
    //layout for the options
    LinearLayout optionsLayout;
    TextView optionsSpinnerText;
    TextView optionsCheckboxText;
    Spinner optionsSpinner;
    CheckBox optionsCheckbox;
    ImageButton searchButton;
    GPSTracker gps;

    public SearchDialog(Context context, int themeResId, GPSTracker gps) {
        super(context, themeResId);

        createUIElements(context); //initialize ui elements
        addSearchView(); //configure search bar layout
        addOptionsView(context); //configure search options

        setView(layout);
    }

    public void createUIElements(Context context) {
        //main layout
        layout = new LinearLayout(context);
        //layout for the search bar
        searchLayout = new LinearLayout(context);
        searchBar = new SearchView(context);
        searchText = new TextView(context);
        //layout for the options
        optionsLayout = new LinearLayout(context);
        optionsSpinnerText = new TextView(context);
        optionsCheckboxText = new TextView(context);
        optionsSpinner = new Spinner(context);
        optionsCheckbox = new CheckBox(context);
        searchButton = new ImageButton(context);
        this.gps = gps;

        layout.setOrientation(LinearLayout.VERTICAL);
    }

    public void addSearchView() {
        searchText.setText("Location: ");
        //TODO searchButton.setImage
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int range;
                Location location;
                if(optionsCheckbox.isChecked()) {
                    location = gps.getLocation();
                } else {
                   String locString = (String) searchBar.getQuery();
                    //TODO get Location with eventful
                }
                range = Integer.parseInt(optionsSpinner.getSelectedItem().toString().replace(" km",""));

                //TODO make search request to API
            }
        });

        searchLayout.addView(searchText, 0);
        searchLayout.addView(searchBar, 1);
        searchLayout.addView(searchButton,2);

        layout.addView(searchLayout, 0);
    }

    public void addOptionsView(Context context) {
        //configure options bar layout
        optionsSpinnerText.setText("Range: ");
        optionsCheckboxText.setText("Use current location");

        //list with range options
        List list = new ArrayList<String>();
        list.add("5 km");
        list.add("10 km");
        list.add("20 km");
        list.add("50 km");

        ArrayAdapter<String> spinnerOptions = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        spinnerOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        optionsSpinner.setAdapter(spinnerOptions);
        optionsLayout.addView(optionsSpinnerText,0);
        optionsLayout.addView(optionsSpinner,1);
        optionsLayout.addView(optionsCheckboxText,2);
        optionsLayout.addView(optionsCheckboxText, 3);
    }
}
