package nl.group11.planplan;

import android.view.View;

import com.firebase.client.Firebase;

import java.util.Date;

/**
 * Created by s140442 on 07/03/2016.
 */
abstract public class Item {

    Enum type;
    Date userStartTime, userEndTime;
    Firebase firebase;


    /**
     * Extracts data from the API and saves it in this object
     */
    public Item() {
    }

    /**
     * updates the information in this item with the current information found in the respective API.
     */
    abstract public void update();

    /**
     * extracts the price of the item if available.
     * price is formatted in numerous ways when retrieved from the API.
     * the String from the API must be converted to a float value containing just the price.
     *
     * @return price of the item if available, else -1.
     */
    abstract public Float extractPrice();

    /**
     * opens the details activity for this item
     *
     * @param v View
     */
    abstract public void onClick(View v);

    /**
     * Checks whether the item has already passed
     *
     * @return false if the item has no specific time, else whether the item has passed
     */
    abstract public boolean hasPassed();

    /**
     * Adds this item to the user's favorites list in the Firebase database.
     */
    abstract public void addFavorite();
    /**
     * Adds this item to the user's planning list in the Firebase database.
     */
    abstract public void addPlanning();

    /**
     * Checks whether this item is already in the user's favorites list in the Firebase database
     *
     * @return whether the item is in the favorites list
     */
    abstract public boolean checkItemInFavorites();

    /**
     * Checks whether this item is already in the user's planning list in the Firebase database
     *
     * @return whether the item is in the planning list
     */
    abstract public boolean checkItemInPlanning();
}
