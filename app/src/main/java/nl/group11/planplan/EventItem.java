package nl.group11.planplan;

import android.view.View;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by s140442 on 07/03/2016.
 */
public class EventItem extends Item {

    com.evdb.javaapi.data.Event event;

    @Override
    public void update() {

    }

    @Override
    public String getPrice() {
        return event.getPrice();
    }

    public Date getStartTime() {
        return event.getStartTime();
    }

    @Override
    public Date getStopTime() {
        return null;
    }

    @Override
    public String getImage() {
        return null;
    }

    @Override
    public String getID() {
        return null;
    }

    @Override
    public Enum getType() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean hasPassed() {
        Calendar calendar = Calendar.getInstance(); //calendar gives current system time.
        return event.getStopTime().before(calendar.getTime());
    }

    @Override
    public void addFavorite() {

    }

    @Override
    public void addPlanning() {

    }

    @Override
    public boolean checkItemInFavorites() {
        return false;
    }

    @Override
    public boolean checkItemInPlanning() {
        return false;
    }
}
