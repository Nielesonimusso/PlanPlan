package nl.group11.planplan;

import android.view.View;

import com.google.android.gms.location.places.Place;

/**
 * Created by s140442 on 07/03/2016.
 */
public class GooglePlacesItem extends Item {

    Place place;

    @Override
    public void update() {

    }

    @Override
    public Float extractPrice() {
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
