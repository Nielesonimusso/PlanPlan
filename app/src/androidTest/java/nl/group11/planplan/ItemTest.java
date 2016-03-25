package nl.group11.planplan;

import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.firebase.client.Firebase;

import org.json.simple.JSONObject;

import java.util.Date;

import dalvik.annotation.TestTarget;

/**
 * Created by s140442 on 25/03/2016.
 */
public class ItemTest extends InstrumentationTestCase {

    EventfulEvent event1,event2,event3;
    public ItemTest() {
        super();
    }
    public JSONObject buildJSON(String id, String title, String price, Date startTime, Date endTime
                , Date userStartTime, Date userEndTime, String image, String description, String address) {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("title", title);
        json.put("type", Type.EVENT.toString());
        json.put("price", price);
        json.put("start_time", startTime.getTime());
        json.put("stop_time", endTime.getTime());
        json.put("user_start_time", userStartTime.getTime());
        json.put("user_end_time", userEndTime.getTime());
        json.put("image", image);
        json.put("description", description);
        json.put("venue_address", address);
        return json;
    }

    public void eventfulItemtoJSONTest(Item item, JSONObject json) {
        assertEquals(item.toJSON().toString(), json.toString());
    }

    @SmallTest
    public void testEventfulItemToJSON1 () {
        JSONObject json = buildJSON("0","a","10,-",new Date(0L),new Date(1L), new Date(0L), new Date(1L), null,"mooi","hier");
        EventItem item = new EventItem(json, getInstrumentation().getContext());
        eventfulItemtoJSONTest(item,json);
    }
}
