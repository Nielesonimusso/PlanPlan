package nl.group11.planplan;

import android.content.Context;

import com.google.android.gms.location.places.Place;

/**
 * Created by s124021 on 9-3-2016.
 */
public class OtherItem extends GooglePlacesItem {

    public OtherItem(Context c, GooglePlace place) {
        super(c, place);
    }

    @Override
    public String getImage() {
        return "https://cdn4.iconfinder.com/data/icons/tralel-and-recreation/32/vacation_26-512.png";
    }

    @Override
    public Enum getType() {
        return Type.OTHER;
    }
}
