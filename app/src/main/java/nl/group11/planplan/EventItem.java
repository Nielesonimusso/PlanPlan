package nl.group11.planplan;

import android.content.Context;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by s140442 on 07/03/2016.
 */
public class EventItem extends Item {

    com.evdb.javaapi.data.Event event;

    public EventItem (Context c, com.evdb.javaapi.data.Event event) {
        super(c);
        this.event = event;
    }

    public EventItem(com.evdb.javaapi.data.Event event) {
        //only for testing
        super();
        this.event = event;
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
        return event.getImages().get(0).getUrl();
    }

    @Override
    public String getID() {
        return event.getSeid();
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
        return getDescription();
    }

    @Override
    public String getAddress() {
        return event.getVenueAddress();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean hasPassed() {
        Calendar calendar = Calendar.getInstance(); //calendar gives current system time.
        return event.getStopTime().before(calendar.getTime());
    }
}
