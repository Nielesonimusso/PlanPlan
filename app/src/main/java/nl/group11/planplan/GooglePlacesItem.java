package nl.group11.planplan;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import com.google.android.gms.location.places.Place;

import org.json.simple.JSONObject;

import java.util.Date;

/**
 * Created by s140442 on 07/03/2016.
 */
abstract public class GooglePlacesItem extends Item {

    GooglePlace place;

    public GooglePlacesItem(Context c, GooglePlace place) {
        super(c);
        this.place = place;
    }
    public GooglePlacesItem(JSONObject json, Context c) {
        //only for testing
        super(json, c);
        this.place = GooglePlace.fromJSON(json);
    }

    @Override
    public void update() {
        //TODO implement method
    }

    /**
     * get price of item
     * @return String that states the price level of the item
     */
    @Override
    public String getPrice() {
        int pricelevel = place.getPriceLevel();
        String price = null;
        switch (pricelevel){
            case -1: price ="No price information available";
                break;
            case 0: price = "Free";
                break;
            case 1: price = "Inexpensive";
                break;
            case 2: price = "Moderate";
                break;
            case 3: price = "Expensive";
                break;
            case 4: price = "Very Expensive";
                break;
        }
        return price;
    }

    @Override
    /**
     * get item starttime.
     * @return returns null, since Google places items don't have start times
     */
    public Date getStartTime() {
        return null;
    }

    /**
     * get item stoptime.
     * @return returns null, since Google places items don't have stop times
     */
    @Override
    public Date getEndTime() {
        return null;
    }

    /**
     * get item ID
     * @return returns item ID
     */
    @Override
    public String getID() {
        return place.getID();
    }

    /**
     * get item title
     * @return returns item title
     */
    @Override
    public String getTitle() {
        return place.getName();
    }

    /**
     * get item description
     * @return returns empty string, since Google places items don't have descriptions
     */
    @Override
    public String getDescription() {
        return "";
    }

    /**
     * get item address
     * @return returns string containing human-readable address of the item
     */
    @Override
    public String getAddress() {
        return place.getAddress();
    }

    /**
     * get item image
     * @return returns the image belonging to the item
     */
    public String getImage() {
        return place.getImage();
    }

    /**
     * determine whether the event has passed
     * @return returns boolean, in this case always false because Google places items don't have start/stop times
     */
    @Override
    public boolean hasPassed() {
        return false;
    }

    /**
     * item to JSONObject to store in database
     * @return JSONObject containing the item information.
     */
    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("place_id", getID());
        json.put("name", getTitle());
        json.put("type",getType().toString());
        json.put("price_level", place.getPriceLevel());
        json.put("user_start_time", getUserStartTime());
        json.put("user_end_time", getUserEndTime());
        json.put("icon", getImage());
        json.put("vicinity",getAddress());
        return json;
    }

}
