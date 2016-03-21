package nl.group11.planplan;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.renderscript.Sampler;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.json.simple.JSONObject;
import org.w3c.dom.Text;

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
        firebase = new Firebase("https://planplan.firebaseio.com/");
    }
    public Item(JSONObject json){

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
    public Date getUserStartTime() {
        return userStartTime;
    }

    /**
     *
     * @return Date object with stop date of the item
     */
    abstract public Date getEndTime();

    /**
     *
     * @return Date object with end date of the item set by the user
     */
    public Date getUserEndTime() {
        return userEndTime;
    }


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
    public void onClick(View v) {
        String tag = (String) v.getTag();
        switch (tag) {
            case "details":
                System.out.println("Clicked item with title " + getTitle());
                Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                intent.putExtra("json",toJSON().toString());
                v.getContext().startActivity(intent);
                break;
            case "addPlanning":
                System.out.println("Clicked planning button of item " + getTitle());
                System.out.println("clicked " + v.getClass() + " - " + (v instanceof AppCompatTextView));

                addRemovePlanning((TextView)((ViewGroup) v).getChildAt(0));
                break;
            case "addFavorites":
                System.out.println("Clicked favorites button of item " + getTitle());
                System.out.println("clicked " + v.getClass() + " - " + (v instanceof AppCompatTextView));

                addRemoveFavorites((TextView)((ViewGroup) v).getChildAt(0));
                break;
        }
    }

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
        Firebase eventRef = firebase.child(id).child("favorites").child(getType().toString()).child(getID());

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

    public void removeFavorite() {

        String id = getAccount();
        firebase.child(id).child("favorites").child(getType().toString()).child(getID()).removeValue();
    }

    public void removePlanning() {
        String id = getAccount();
        firebase.child(id).child("planning").child(getID()).removeValue();
    }

    private String getAccount() {
        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
        return accounts[0].name.replace('.', '*');//replace dots with stars to prevent dots in firebase key
    }

    public void addRemoveFavorites(final TextView v) {
        databaseGeneric("favorites", true, new APIHandler.Callback<Boolean>() {
            @Override
            public void onItem(Boolean result) {
                if (v != null) {
                    if (result) {
                        v.setText("Add to favorites");
                    } else {
                        v.setText("Remove from favorites");
                    }
                }
            }
        });

    }

    public void addRemovePlanning(final TextView v) {
        databaseGeneric("planning", true, new APIHandler.Callback<Boolean>() {
            @Override
            public void onItem(Boolean result) {
                if (v != null) {
                    if (result) {
                        v.setText("Add to planning");
                    } else {
                        v.setText("Remove from planning");
                    }
                }
            }
        });
    }

    public void checkInFavorites(boolean updateButtons) {
        databaseGeneric("favorites", false, new APIHandler.Callback<Boolean>() {
            @Override
            public void onItem(Boolean result) {

            }
        });
    }

    public void checkInPlanning() {
        databaseGeneric("planning", false, new APIHandler.Callback<Boolean>() {
            @Override
            public void onItem(Boolean result) {

            }
        });
    }

    /**
     * @param location location of item (either "favorites" or "planning")
     * @return whether the item exists at the given location
     */
    private void databaseGeneric(final String location, final boolean add, final APIHandler.Callback<Boolean> callback) {
        //get user account
        String account = getAccount();

        //whether it is in the favorites (this syntax is used because we have to set it inside onDataChange)
        final boolean[] inGeneric = {false};
        Firebase ref;

        //create a reference to the location of this item in the favorites(if it exists in the database)
        if (location.equals("favorites")) {
            ref = firebase.child(account).child(location).child(getType().toString()).child(getID());
        } else {
            ref = firebase.child(account).child(location).child(getID());

        }

        //Listen for this reference once, then remove the listener
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check whether the item exists
                inGeneric[0] = dataSnapshot.child(Data.ID.toString()).exists();
                System.out.println("in database: getItem: " + inGeneric[0] + " - " + dataSnapshot.child(Data.ID.toString()));
                callback.onItem(inGeneric[0]);

                //add if add
                if (add) {
                    if (inGeneric[0]) {
                        if (location.equals("favorites")) {
                            removeFavorite();
                        } else {
                            removePlanning();
                        }
                    } else {
                        if (location.equals("favorites")) {
                            addFavorite();
                        } else {
                            addPlanning();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("in database: " + firebaseError.toString());
            }
        });
    }

    public void setUserStartTime(Date d) {
        this.userStartTime = d;
    }

    public void setUserEndTime(Date d) {
        this.userEndTime = d;
    }

    abstract public JSONObject toJSON();
}
