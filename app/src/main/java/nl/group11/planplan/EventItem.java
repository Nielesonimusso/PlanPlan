package nl.group11.planplan;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by s140442 on 07/03/2016.
 */
public class EventItem extends Item {

    com.evdb.javaapi.data.Event event;

    public EventItem (Context c, com.evdb.javaapi.data.Event event) {
        super(c);
        this.event = event;
    }

    public EventItem(com.evdb.javaapi.data.Event event) {
        //only for testing
        super();
        this.event = event;
    }

    @Override
    public void update() {
        //TODO implement method
    }

    @Override
    public String getPrice() {
        return event.getPrice();
    }

    public Date getStartTime() {
        return event.getStartTime();
    }

    @Override
    public Date getUserStartTime() {
        return userStartTime;
    }

    @Override
    public Date getEndTime() {
        return event.getStopTime();
    }

    @Override
    public Date getUserEndTime() {
        return userEndTime;
    }

    @Override
    public String getImage() {
        return event.getImages().get(0).getUrl();
    }

    @Override
    public String getID() {
        return event.getSeid();
    }

    @Override
    public Enum getType() {
        return Type.EVENT;
    }

    @Override
    public String getTitle() {
        return event.getTitle();
    }

    @Override
    public String getDescription() {
        return getDescription();
    }

    @Override
    public String getAddress() {
        return event.getVenueAddress();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean hasPassed() {
        Calendar calendar = Calendar.getInstance(); //calendar gives current system time.
        return event.getStopTime().before(calendar.getTime());
    }

    @Override
    public void addFavorite() {

        //get account
        String id = getAccount();

        //create a new reference at the location of the new item entry
        Firebase eventRef = firebase.child(id).child("favorites").child(getID());

        //set new data
        addGeneric(eventRef);
    }

    @Override
    public void addPlanning() {
        //get account
        String id = getAccount();

        //create a new reference at the location of the new item entry
        Firebase eventRef = firebase.child(id).child("planning").child(getID());

        //set new data
        addGeneric(eventRef);
        eventRef.child(Data.USERSTARTTIME.toString()).setValue(getUserStartTime());
        eventRef.child(Data.USERENDTIME.toString()).setValue(getUserEndTime());
    }

    /**
     * Sets data to firebase needed by both the favorites and the planning.
     * @param eventRef reference to the location in the firebase database
     *                 where the data has to be added.
     */
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
        return checkInGeneric("favorites");

    }

    @Override
    public boolean checkItemInPlanning() {
        return checkInGeneric("planning");
    }

    /**
     * @param location location of item (either "favorites" or "planning")
     * @return whether the item exists at the given location
     */
    private boolean checkInGeneric(String location) {
        //get user account
        String id = getAccount();

        //whether it is in the favorites (this syntax is used because we have to set it inside onDataChange)
        final boolean[] inFavorites = {false};

        //create a reference to the location of this item in the favorites(if it exists in the database)
        Firebase ref = firebase.child(id).child(location).child(getID());

        //Listen for this reference once, then remove the listener
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check whether the item exists
                inFavorites[0] = dataSnapshot.child(Data.ID.toString()).exists();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //Do nothing
            }
        });
        return inFavorites[0];
    }
}
