package nl.group11.planplan;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.view.View;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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
    public Item(){
    //Only for testing
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

    public void addFavorite() {

        //get account
        String id = getAccount();

        //create a new reference at the location of the new item entry
        Firebase eventRef = firebase.child(id).child("favorites").child(getID());

        //set new data
        addGeneric(eventRef);
    }

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

    public boolean checkItemInFavorites() {
        return checkInGeneric("favorites");

    }

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
        final boolean[] inGeneric = {false};

        //create a reference to the location of this item in the favorites(if it exists in the database)
        Firebase ref = firebase.child(id).child(location).child(getID());

        //Listen for this reference once, then remove the listener
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check whether the item exists
                inGeneric[0] = dataSnapshot.child(Data.ID.toString()).exists();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                //Do nothing
            }
        });
        return inGeneric[0];
    }

    public void setUserStartTime(Date d) {
        this.userStartTime = d;
    }

    public void setUserEndTime(Date d) {
        this.userEndTime = d;
    }
}
