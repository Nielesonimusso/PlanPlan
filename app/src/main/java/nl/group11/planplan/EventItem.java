package nl.group11.planplan;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import org.json.JSONException;
import org.json.simple.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by s140442 on 07/03/2016.
 */
public class EventItem extends Item {

    EventfulEvent event;

    public EventItem (Context c, EventfulEvent event) {
        super(c);
        this.event = event;
    }

    public EventItem(JSONObject json, Context c) {
        //only for testing
        super(json, c);
        this.event = EventfulEvent.fromJSON(json);
    }

    @Override
    public void update() {
        //TODO implement method
    }

    @Override
    public String getPrice() {
        return event.getPrice();
    }

    public Date getStartTime() {
        return event.getStartTime();
    }

    @Override
    public Date getEndTime() {
        return event.getStopTime();
    }

    @Override
    public String getImage() {
        return event.getImage();
    }

    @Override
    public String getID() {
        return event.getID();
    }

    @Override
    public Enum getType() {
        return Type.EVENT;
    }

    @Override
    public String getTitle() {
        return event.getTitle();
    }

    @Override
    public String getDescription() {
        return event.getDescription();
    }

    @Override
    public String getAddress() {
        return event.getAddress();
    }

    @Override
    public boolean hasPassed() {
        Calendar calendar = Calendar.getInstance(); //calendar gives current system time.
        return event.getStartTime().before(calendar.getTime());
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", getID());
        json.put("title", getTitle());
        json.put("type", getType().toString());
        json.put("price", getPrice());
        json.put("start_time", getStartTime().getTime());
        json.put("stop_time", getEndTime().getTime());
        json.put("user_start_time", getUserStartTime());
        json.put("user_end_time", getUserEndTime());
        json.put("image", event.getImagesObject());
        json.put("description", getDescription());
        json.put("venue_address",getAddress());
        return json;
    }
}
