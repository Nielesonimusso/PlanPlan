package nl.group11.planplan;

import android.content.Context;

import com.google.android.gms.location.places.Place;

/**
 * Created by s140442 on 09/03/2016.
 */
public class RestaurantItem extends GooglePlacesItem {

    public RestaurantItem(Context c, Place place) {
        super(c, place);
    }

    @Override
    public String getImage() {
        return "http://i.imgur.com/ZPRu8He.png";
    }

    @Override
    public Enum getType() {
        return Type.RESTAURANT;
    }
}
