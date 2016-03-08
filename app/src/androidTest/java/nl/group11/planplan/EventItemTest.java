package nl.group11.planplan;

import com.evdb.javaapi.data.Event;
import com.evdb.javaapi.data.Image;

import junit.framework.TestCase;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by s140442 on 08/03/2016.
 */
public class EventItemTest extends TestCase {

    Item testItem;

    public void buildEvent() {
        Event testEvent = new Event();
        testEvent.setPrice("heel veel €99999,50");
        testEvent.setFree(false);
        testEvent.setStartTime(new Date());
        testEvent.setStopTime(new Date());
        Image img = new Image();
        img.setUrl("insert mooi plaatje.nl");
        List<Image> list = new LinkedList<>();
        list.add(img);
        testEvent.setImages(list);
        testEvent.setDescription("mooi event");
        testEvent.setTitle("wereldkampioenschap events verzinnen");
        testEvent.setSeid("125446sSDF");
        testEvent.setVenueAddress("hier");

        testItem = new EventItem(testEvent);
    };


    public void testUpdate() throws Exception {

    }

    public void testGetPrice() throws Exception {

    }

    public void testGetStartTime() throws Exception {

    }

    public void testGetUserStartTime() throws Exception {

    }

    public void testGetEndTime() throws Exception {

    }

    public void testGetUserEndTime() throws Exception {

    }

    public void testGetImage() throws Exception {

    }

    public void testGetID() throws Exception {

    }

    public void testGetType() throws Exception {

    }

    public void testGetTitle() throws Exception {

    }

    public void testGetDescription() throws Exception {

    }

    public void testGetAddress() throws Exception {

    }

    public void testOnClick() throws Exception {

    }

    public void testHasPassed() throws Exception {

    }

    public void testAddFavorite() throws Exception {

    }

    public void testAddPlanning() throws Exception {

    }

    public void testCheckItemInFavorites() throws Exception {

    }

    public void testCheckItemInPlanning() throws Exception {

    }
}