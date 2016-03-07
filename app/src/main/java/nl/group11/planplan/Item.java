package nl.group11.planplan;

import android.content.Context;
import android.view.View;

import com.firebase.client.Firebase;

import java.util.Date;

/**
 * Created by s140442 on 07/03/2016.
 */
abstract public class Item implements View.OnClickListener{
    Date userStartTime, userEndTime;
    Firebase firebase;
    Context context;

    /**
     * Extracts data from the API and saves it in this object
     *
     * extracts:
     * type
     */
    public Item(Context c) {
        context = c;
    }

    /**
     * updates the information in this item with the current information found in the respective API.
     */
    abstract public void update();

    /**
     * retrieves the price of the item.
     *
     * @return price of the item
     */
    abstract public String getPrice();

    /**
     *
     * @return Date object with start date of the item
     */
    abstract public Date getStartTime();

        /**
     *
     * @return Date object with start date of the item set by the user
     */
    abstract public Date getUserStartTime();

    /**
     *
     * @return Date object with stop date of the item
     */
    abstract public Date getEndTime();

    /**
     *
     * @return Date object with end date of the item set by the user
     */
    abstract public Date getUserEndTime();


    /**
     *
     * @return image of the item
     */
    abstract public String getImage();

    /**
     * @return id of item
     */
    abstract public String getID();

    /**
     * @return type of the item
     */
    abstract public Enum getType();

    /**
     * @return title of the item
     */
    abstract public String getTitle();

    /**
     * @return description of the item
     */
    abstract public String getDescription();

    /**
     * @return address of the item
     */
    abstract public String getAddress();

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

    public void setUserStartTime(Date d) {
        this.userStartTime = d;
    }

    public void setUserEndTime(Date d) {
        this.userEndTime = d;
    }
}
