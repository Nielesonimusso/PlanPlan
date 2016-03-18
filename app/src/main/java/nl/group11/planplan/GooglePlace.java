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
        Object name = data.get("name");
        if (name == null) {
            return "No title available";
        }
        return name.toString();
    }

    public String getID() {
        Object id = data.get("place_id");
        if(id == null) {
            return "Dit kan helemaal niet";
        }
        return id.toString();
    }

    public int getPriceLevel() {
        if (data.get("price_level") != null) {
            if (data.get("price_level").toString() != "") {
                return Integer.parseInt(data.get("price_level").toString());
            }
        }
        return -1;
    }

    public String getAddress() {
        Object address = data.get("vicinity");
        if (address == null) {
            return "No address available";
        }
        return address.toString();
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
        Object image = data.get("icon");
        if (image == null) {
            return null;
        }
        return image.toString();
    }

    public Date getUserStartTime() {
        Object userStartTime = data.get("user_start_time");
        if (userStartTime == null) {
            return new Date();
        }
        return new Date(Long.parseLong(userStartTime.toString()));
    }

    public Date getUserStopTime() {
        Object userStopTime = data.get("user_start_time");
        if (userStopTime == null) {
            return new Date();
        }
        return new Date(Long.parseLong(userStopTime.toString()));
    }

}
