package nl.group11.planplan;

import android.content.Context;

import com.google.android.gms.location.places.Place;

/**
 * Created by s140442 on 09/03/2016.
 */
public class RestaurantItem extends GooglePlacesItem {

    public RestaurantItem(Context c, GooglePlace place) {
        super(c, place);
    }

    @Override
    public Enum getType() {
        return Type.RESTAURANT;
    }
}
