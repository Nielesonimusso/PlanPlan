package nl.group11.planplan;

import org.joda.time.DateTime;
import org.json.simple.JSONObject;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by s132054 on 16-3-2016.
 */
class EventfulEvent {

    private JSONObject data;

    public static EventfulEvent fromJSON(JSONObject json) {
        EventfulEvent eventfulEvent = new EventfulEvent();
        eventfulEvent.data = (JSONObject) json.clone();
        return eventfulEvent;
    }

    public String JSON() {
        return data.toJSONString();
    }

    public String getTitle() {
        return data.get("title").toString();
    }
    public String getID() {
        return data.get("id").toString();
    }

    public String getDescription() {
        Object desc = (String) data.get("description");
        return (String) APIHandler.altIfNull(desc, "No description available");
    }
    public String getPrice() {
        Object price = data.get("price");
        return (String) APIHandler.altIfNull(price, "No price information available");
    }

    private Date makeDate(String dateString) {
        int year = Integer.parseInt(dateString.substring(0,4));
        int month = Integer.parseInt(dateString.substring(5,7));
        int day = Integer.parseInt(dateString.substring(8,10));
        int hour = Integer.parseInt(dateString.substring(11,13));
        int minute = Integer.parseInt(dateString.substring(14,16));
        int second = Integer.parseInt(dateString.substring(17, 19));
        return new DateTime(year,month,day,hour,minute,second).toDate();
    }
    public Date getStartTime() {
        if (data.get("start_time").toString().contains("-")) {
            Date newStart = makeDate(data.get("start_time").toString());
            data.put("start_time", newStart.getTime());
        }
        return new Date(Long.parseLong(data.get("start_time").toString()));
    }

    public Date getUserStartTime() {
        return new Date(Long.parseLong(data.get("user_start_time").toString()));
    }

    public Date getStopTime() {
        if (data.get("stop_time").toString().contains("-")) {
            Date newStop = makeDate(data.get("stop_time").toString());
            data.put("stop_time", newStop.getTime());
        }
        return new Date(Long.parseLong(data.get("stop_time").toString()));
    }

    public Date getUserStopTime() {
        return new Date(Long.parseLong(data.get("user_stop_time").toString()));
    }

    public String getImage() {
        JSONObject imgJSON = (JSONObject) data.get("image");
        if (imgJSON != null) {
            System.out.println("getting image url for event " + getTitle());
            System.out.println(imgJSON.toJSONString());
            String biggestUrl = "";
            int biggestDim = 0;
            System.out.println("-- checking keys --");
            for (Object o : imgJSON.entrySet()) {
                Map.Entry<String, Object> entry = (Map.Entry<String, Object>) o;
                System.out.println(entry.getKey().toString());
                if (entry.getKey().equals("url")) {
                    biggestUrl = Integer.valueOf(imgJSON.get("width").toString()) > biggestDim ? entry.getValue().toString() : biggestUrl;
                    biggestDim = Math.max(biggestDim, Integer.valueOf(imgJSON.get("width").toString()));
                } else if (entry.getValue() instanceof JSONObject) {
                    JSONObject imgRef = (JSONObject) entry.getValue();
                    biggestUrl = Integer.valueOf(imgRef.get("width").toString()) > biggestDim ? imgRef.get("url").toString() : biggestUrl;
                    biggestDim = Math.max(biggestDim, Integer.valueOf(imgRef.get("width").toString()));
                }
            }
            return biggestUrl;
        } else {
            return null;
        }
    }

    public JSONObject getImagesObject() {
        return (JSONObject) data.get("image");
    }

    public String getAddress() {
        Object address = data.get("venue_address");
        if (address != null) {
            return data.get("venue_address").toString();
        } else {
            return "No address available";
        }
    }

    public String getType() {
        return data.get("type").toString();
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
