package nl.group11.planplan;

import android.content.Context;
import android.view.View;

import com.google.android.gms.location.places.Place;

import java.util.Date;

/**
 * Created by s140442 on 07/03/2016.
 */
public class GooglePlacesItem extends Item {

    public GooglePlacesItem(Context c) {
        super(c);
    }
    Place place;

    @Override
    public void update() {

    }

    @Override
    public String getPrice() {
        return null;
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
        return null;
    }

    @Override
    public Enum getType() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getAddress() {
        return null;
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

    @Override
    public boolean checkItemInFavorites() {
        return false;
    }

    @Override
    public boolean checkItemInPlanning() {
        return false;
    }
}
