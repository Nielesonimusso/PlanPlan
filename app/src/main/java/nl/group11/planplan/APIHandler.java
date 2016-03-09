package nl.group11.planplan;

import android.os.AsyncTask;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by s132054 on 7-3-2016.
 */
public class APIHandler {

    private static final String EVENTFULKEY = "KMFgPh35QqPkMgXw";
    private static final String GOOGLEPLACESKEY = "AIzaSyDNrounxGt7hKmWqQftfYUniyd3BmXEmZA";

    public static void queryEventful(final String location, final int radius, final ListCallback<EventfulEvent> callback, final int page) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                final List<EventfulEvent> events = new ArrayList<>();
                try {
                    URL requestUrl = new URL("http://api.eventful.com/json/events/search?" +
                            "units=km" +
                            "&within=" + radius +
                            "&location=" + location +
                            "&date=Future" +
                            "&app_key=" + EVENTFULKEY +
                            "&page_number=" + page);

                    JSONHTTPRequest(requestUrl, new JSONCallback() {
                        @Override
                        public void onJSON(JSONObject result) {
                            //System.out.println(result.toJSONString());
                            JSONArray resultItems = (JSONArray) ((JSONObject) result.get("events")).get("event");
                            for (Object event : resultItems) {
                                JSONObject eventjson = (JSONObject) event;
                                //System.out.println(eventjson.toJSONString());
                                events.add(EventfulEvent.fromJSON(eventjson));
                            }
                            callback.onList(events);
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static void queryGooglePlaces(String location, int radius, final ListCallback<GooglePlace> callback) {

    }

    private static void JSONHTTPRequest(URL url, final JSONCallback callback) {
        new AsyncTask<URL, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(URL... params) {
                URL url = params[0];
                try {
                    HttpURLConnection http = (HttpURLConnection) url.openConnection();
                    JSONObject json = (JSONObject) new JSONParser().parse(new BufferedReader(new InputStreamReader(http.getInputStream())));
                    return json;
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                    return new JSONObject();
                }
            }

            @Override
            protected void onPostExecute(JSONObject jsonObject) {
                super.onPostExecute(jsonObject);
                callback.onJSON(jsonObject);
            }
        }.execute(url);
    }

    interface ListCallback<T> {
        void onList(List<T> results);
    }

    interface JSONCallback {
        void onJSON(JSONObject result);
    }
}

class GooglePlace {

    private JSONObject data;

    public static GooglePlace fromJSON(JSONObject json) {
        GooglePlace googlePlace = new GooglePlace();
        googlePlace.data = (JSONObject) json.clone();
        return googlePlace;
    }

    //TODO: create getter/setter methods to access data
}

class EventfulEvent {

    private JSONObject data;

    public static EventfulEvent fromJSON(JSONObject json) {
        EventfulEvent eventfulEvent = new EventfulEvent();
        eventfulEvent.data = (JSONObject) json.clone();
        return eventfulEvent;
    }

    //TODO: create getter/setter methods to access data

    public String getTitle() {
        return data.get("title").toString();
    }
}