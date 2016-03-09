package nl.group11.planplan;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import com.google.android.gms.location.places.Place;

import java.util.Date;

/**
 * Created by s140442 on 07/03/2016.
 */
abstract public class GooglePlacesItem extends Item {

    public GooglePlacesItem(Context c, com.google.android.gms.location.places.Place place) {
        super(c);
    }
    com.google.android.gms.location.places.Place place;

    @Override
    public void update() {
        //TODO implement method
    }

    @Override
    public String getPrice() {
        int pricelevel = place.getPriceLevel();
        String price = null;
        switch (pricelevel){
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
    public Date getStartTime() {
        return null;
    }

    @Override
    public Date getEndTime() {
        return null;
    }

    @Override
    public String getID() {
        return place.getId();
    }

    @Override
    public String getTitle() {
        return (String) place.getName();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getAddress() {
        return (String) place.getAddress();
    }

    @Override
    public void onClick(View v) {
        //TODO implement method
    }

    @Override
    public boolean hasPassed() {
        return false;
    }
}
