package nl.group11.planplan;

import android.content.Context;

import com.google.android.gms.location.places.Place;

import org.json.simple.JSONObject;

/**
 * Created by s140442 on 09/03/2016.
 */
public class RestaurantItem extends GooglePlacesItem {

    public RestaurantItem(Context c, GooglePlace place) {
        super(c, place);
    }

    public RestaurantItem(JSONObject json) {
        super(json);
    }

    @Override
    public Enum getType() {
        return Type.RESTAURANT;
    }
}
