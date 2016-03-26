package nl.group11.planplan;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Date;

/**
 * Created by s132054 on 16-3-2016.
 */
class GooglePlace {

    private JSONObject data; //Contains query data

    /**
     * @param json JSONObject containing information to construct a GooglePlace
     * @return GooglePlace constructed from json
     */
    public static GooglePlace fromJSON(JSONObject json) {
        GooglePlace googlePlace = new GooglePlace();
        googlePlace.data = (JSONObject) json.clone();
        return googlePlace;
    }

    public String getName() {
        Object name = data.get("name");
        if (name == null) {
            name = data.get("title");
            if (name == null) { //HOTFIX for proper saving to Firebase
                return "No title available";
            }
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
        return -1;//If no price level available return an integer google would never return.
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
            image = data.get("image");
            if (image == null) { //HOTFIX for proper saving to Firebase
                return null;
            }
        }
        return image.toString();
    }

    public Date getUserStartTime() {
        Object userStartTime = data.get("user_start_time");
        if (userStartTime == null) {
            return new Date(); //if no userstarttime available, return current time.
        }
        return new Date(Long.parseLong(userStartTime.toString()));
    }

    public Date getUserStopTime() {
        Object userStopTime = data.get("user_start_time");
        if (userStopTime == null) {
            return new Date(); //if no userstoptime available, return current time.
        }
        return new Date(Long.parseLong(userStopTime.toString()));
    }

}
