package nl.group11.planplan;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * Created by s140442 on 09/03/2016.
 */
public class SearchDialog extends AlertDialog {

    public SearchDialog(Context context, int themeResId) {
        super(context, themeResId);

        //main layout
        LinearLayout layout = new LinearLayout(context);
        //layout for the search bar
        LinearLayout searchLayout = new LinearLayout(context);
        SearchView searchBar = new SearchView(context);
        TextView searchText = new TextView(context);
        //layout for the options
        LinearLayout optionsLayout = new LinearLayout(context);
        TextView optionsSpinnerText = new TextView(context);
        TextView optionsCheckboxText = new TextView(context);
        Spinner optionsSpinner = new Spinner(context);
        CheckBox optionsCheckbox = new CheckBox(context);

        //configure search bar layout
        searchText.setText("Location: ");
        searchLayout.addView(searchText, 0);
        searchLayout.addView(searchBar, 1);

        //configure options bar layout
        optionsSpinnerText.setText("Range: ");
        optionsCheckboxText.setText("Use current location");
        optionsLayout.addView(optionsSpinnerText,0);
        optionsLayout.addView(optionsSpinner,1);
        optionsLayout.addView(optionsCheckboxText,2);
        optionsLayout.addView(optionsCheckboxText,3);

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(searchLayout,0);

        setView(layout);
    }
}
