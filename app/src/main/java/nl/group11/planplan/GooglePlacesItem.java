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
public class GooglePlacesItem extends Item {

    public GooglePlacesItem(Context c, com.google.android.gms.location.places.Place place;) {
        super(c);
    }
    com.google.android.gms.location.places.Place place;

    @Override
    public void update() {

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
    public Date getUserStartTime() {
        return null;
    }

    @Override
    public Date getEndTime() {
        return null;
    }

    @Override
    public Date getUserEndTime() {
        return null;
    }

    @Override
    public String getImage() {
        return null;
    }

    @Override
    public String getID() {
        return place.getId();
    }

    @Override
    public Enum getType() {
        return null;
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

    }

    @Override
    public boolean hasPassed() {
        return false;
    }

    @Override
    public void addFavorite() {

    }

    @Override
    public void addPlanning() {

    }

    private void addGeneric(Firebase eventRef) {
        eventRef.child(Data.ID.toString()).setValue(getID());
        eventRef.child(Data.TITLE.toString()).setValue(getTitle());
        eventRef.child(Data.TYPE.toString()).setValue(getType());
        eventRef.child(Data.ADDRESS.toString()).setValue(getAddress());
        eventRef.child(Data.DESCRIPTION.toString()).setValue(getDescription());
        eventRef.child(Data.STARTTIME.toString()).setValue(getStartTime());
        eventRef.child(Data.ENDTIME.toString()).setValue(getEndTime());
        eventRef.child(Data.IMAGE.toString()).setValue(getImage());
        eventRef.child(Data.PRICE.toString()).setValue(getPrice());
    }

    private String getAccount() {
        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
        return accounts[0].name;
    }

    @Override
    public boolean checkItemInFavorites() {
        return false;
    }

    @Override
    public boolean checkItemInPlanning() {
        return false;
    }
}
