package nl.group11.planplan;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Date;

/**
 * Created by s132054 on 16-3-2016.
 */
class GooglePlace {

    private JSONObject data;

    public static GooglePlace fromJSON(JSONObject json) {
        GooglePlace googlePlace = new GooglePlace();
        googlePlace.data = (JSONObject) json.clone();
        return googlePlace;
    }

    //TODO: create getter/setter methods to access data

    public String getName() {
        return data.get("name").toString();
    }

    public String getID() {
        return data.get("place_id").toString();
    }

    public int getPriceLevel() {
        if (data.containsKey("price_level")) {
            return Integer.parseInt(data.get("price_level").toString());
        } else {
            return -1;
        }
    }

    public String getAddress() {
        return data.get("vicinity").toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    public Enum getType() {
        JSONArray types = (JSONArray) data.get("types");
        for(Object type: types) {
            String s = (String) type;
            if (s.equals("restaurant") || s.equals("food")) {
                return Type.RESTAURANT;
            }
        }
        return Type.OTHER;
    }

    public String getImage() {
        return data.get("icon").toString();
    }

    public Date getUserStartTime() {
        return new Date(Long.parseLong(data.get("user_start_time").toString()));
    }

    public Date getUserStopTime() {
        return new Date(Long.parseLong(data.get("user_stop_time").toString()));
    }

}
