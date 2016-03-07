package nl.group11.planplan;

import android.view.View;

/**
 * Created by s140442 on 07/03/2016.
 */
public class EventItem extends Item {

    com.evdb.javaapi.data.Event event;

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
