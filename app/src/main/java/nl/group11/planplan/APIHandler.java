package nl.group11.planplan;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by s132054 on 7-3-2016.
 */
public class APIHandler {

    private static final String EVENTFULKEY = "KMFgPh35QqPkMgXw";
    static final int EVENTFUL_PAGE_SIZE = 10;

    private static final String GOOGLEPLACESKEY = "AIzaSyDNrounxGt7hKmWqQftfYUniyd3BmXEmZA";


    public static void queryEventful(final String location, final int radius, final int page, final ListCallback<EventfulEvent> callback) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                final List<EventfulEvent> events = new ArrayList<>();
                try {
                    URL requestUrl = new URL("http://api.eventful.com/json/events/search?" +
                            "units=km" +
                            "&page_size=" + EVENTFUL_PAGE_SIZE +
                            "&date=Future" +
                            "&within=" + radius +
                            "&location=" + location +
                            "&app_key=" + EVENTFULKEY +
                            "&page_number=" + page +
                            "&sort_order=date");

                    JSONHTTPRequest(requestUrl, new JSONCallback() {
                        @Override
                        public void onJSON(JSONObject result) {
                            //System.out.println(result.get("total_items").toString());
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
    public String getID() {
        return data.get("id").toString();
    }

    @Override
    public String toString() {
        return getTitle();
    }
}

abstract class DynamicSearch<T> {

    Map<Integer, T> searchCache;
    List<SearchUpdateListener> listeners;

    DynamicSearch() {
        searchCache = new HashMap<>();
        listeners = new ArrayList<>();
    }

    @Nullable
    public T get(int i) {
        if (searchCache.containsKey(i)) {
            //return item from cache
            return searchCache.get(i);
        } else {
            //perform query and return null to indicate result is not yet available
            query(i);
            return null;
        }
    }

    abstract void query(int i);

    public void addListener(SearchUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SearchUpdateListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(int start, int end) {
        for(SearchUpdateListener listener : listeners) {
            listener.onUpdate(this, start, end);
        }
    }

    interface SearchUpdateListener {
        void onUpdate(DynamicSearch self, int start, int end);
    }
}

class EventfulDynamicSearch extends DynamicSearch<EventfulEvent> {

    String location;
    int radius;

    EventfulDynamicSearch(String location, int radius) {
        this.location = location;
        this.radius = radius;
    }

    @Override
    void query(int i) {
        final int page = i / APIHandler.EVENTFUL_PAGE_SIZE;
        APIHandler.queryEventful(location, radius, page + 1, new APIHandler.ListCallback<EventfulEvent>() {
            @Override
            public void onList(List<EventfulEvent> results) {
                if (results.size() == 0) {
                    //somehow notify callee that there are no results
                } else {
                    for (int i = 0; i < results.size(); i++) {
                        searchCache.put(page * APIHandler.EVENTFUL_PAGE_SIZE + i, results.get(i));
                    }
                    notifyListeners(page * APIHandler.EVENTFUL_PAGE_SIZE,
                            page * APIHandler.EVENTFUL_PAGE_SIZE + results.size() - 1);
                }
            }
        });
    }
}