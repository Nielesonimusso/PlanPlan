package nl.group11.planplan;

import android.content.Context;

import org.json.simple.JSONObject;

/**
 * Created by s140442 on 09/03/2016.
 */
public class RestaurantItem extends GooglePlacesItem {

    public RestaurantItem(Context c, GooglePlace place) {
        super(c, place);
    }

    public RestaurantItem(JSONObject json, Context c) {
        super(json, c);
    }

    @Override
    public Enum getType() {
        return Type.RESTAURANT;
    }
}
