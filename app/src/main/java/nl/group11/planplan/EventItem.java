package nl.group11.planplan;

import android.content.Context;
import android.view.View;

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

    public EventItem(EventfulEvent event) {
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
    public void onClick(View v) {

    }

    @Override
    public boolean hasPassed() {
        Calendar calendar = Calendar.getInstance(); //calendar gives current system time.
        return event.getStopTime().before(calendar.getTime());
    }
}
