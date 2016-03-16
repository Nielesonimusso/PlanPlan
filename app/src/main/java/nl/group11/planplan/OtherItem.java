package nl.group11.planplan;

import android.content.Context;
import org.json.simple.JSONObject;

/**
 * Created by s124021 on 9-3-2016.
 */
public class OtherItem extends GooglePlacesItem {

    public OtherItem(Context c, GooglePlace place) {
        super(c, place);
    }

    public OtherItem(JSONObject json) {
        super(json);
    }

    @Override
    public Enum getType() {
        return Type.OTHER;
    }
}
