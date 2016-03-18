package nl.group11.planplan;

import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by s132054 on 7-3-2016.
 */
public class APIHandler {

    private static final String EVENTFULKEY = "KMFgPh35QqPkMgXw";
    static final int EVENTFUL_PAGE_SIZE = 10;

    private static final String GOOGLEPLACESKEY = "AIzaSyDNrounxGt7hKmWqQftfYUniyd3BmXEmZA";
    static final int GOOGLEPLACES_PAGE_SIZE = 20;

    public static void queryEventful(final String location, final int radius, final int page, final Callback<List<EventfulEvent>> callback) {
        queryEventful(location, radius, page, callback, new Callback<JSONObject>() {
            @Override
            public void onItem(JSONObject result) {

            }
        });
    }

    public static void queryEventful(final String location, final int radius, final int page, final Callback<List<EventfulEvent>> callback, final Callback<JSONObject> miscCallback) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                final List<EventfulEvent> events = new ArrayList<>();
                try {
                    String requestUrl = "http://api.eventful.com/json/events/search?" +
                            "units=km" +
                            "&page_size=" + EVENTFUL_PAGE_SIZE +
                            "&date=Future" +
                            "&within=" + radius +
                            "&location=" + URLEncoder.encode(location, "UTF-8") +
                            "&app_key=" + EVENTFULKEY +
                            "&page_number=" + page +
                            "&sort_order=date";

                    JSONHTTPRequest(requestUrl, new Callback<JSONObject>() {
                        @Override
                        public void onItem(JSONObject result) {
                            //System.out.println(result.get("total_items").toString());
                            miscCallback.onItem(result);
                            JSONArray resultItems = (JSONArray) ((JSONObject) result.get("events"))
                                    .get("event");
                            for (Object event : resultItems) {
                                JSONObject eventjson = (JSONObject) event;
                                //System.out.println(eventjson.toJSONString());
                                events.add(EventfulEvent.fromJSON(eventjson));
                            }
                            callback.onItem(events);
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public static void queryGooglePlaces(final String location, final int radius, final String type, final Callback<List<GooglePlace>> callback) {
        queryGooglePlaces(location, radius, type, callback, "");
    }

    public static void queryGooglePlaces(final String location, final int radius, final String type,
                                         final Callback<List<GooglePlace>> callback, final String pagetoken) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final List<GooglePlace> places = new ArrayList<>();
                    String requestUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
                    if (pagetoken.isEmpty()) {
                        requestUrl += "location=" + URLEncoder.encode(location, "UTF-8") +
                                "&radius=" + radius +
                                "&type=" + URLEncoder.encode(type, "UTF-8");
                    } else {
                        requestUrl += "pagetoken=" + pagetoken;
                    }
                    requestUrl += "&key=" + GOOGLEPLACESKEY;

                    System.out.println(requestUrl);

                    JSONHTTPRequest(requestUrl, new Callback<JSONObject>() {
                        @Override
                        public void onItem(final JSONObject result) {
                            JSONArray results = (JSONArray) result.get("results");
                            for (Object item : results) {
                                places.add(GooglePlace.fromJSON((JSONObject) item));
                            }
                            callback.onItem(places);
                            //query next page when there is one, otherwise return
                            if (result.containsKey("next_page_token")) {
                                System.out.println("GooglePlaces next page: " + result.get("next_page_token").toString());
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        queryGooglePlaces(location, radius, type, callback, result.get("next_page_token").toString());
                                    }
                                }, 2000);
                            } else {
                                System.out.println("GooglePlaces no next page");
                            }
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private static void JSONHTTPRequest(final String url, final Callback<JSONObject> callback) {
        new AsyncTask<Void, Void, JSONObject>() {

            @Override
            protected JSONObject doInBackground(Void... params) {
                try {
                    URL usableURL = new URL(url);
                    HttpURLConnection http = (HttpURLConnection) usableURL.openConnection();
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
                callback.onItem(jsonObject);
            }
        }.execute();
    }

    interface Callback<T> {
        void onItem(T result);
    }

    public static void stringToLocation(final String location, final Callback<Location> callback) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                JSONHTTPRequest("http://api.eventful.com/json/venues/search?" +
                        "location=" + location +
                        "&app_key=" + EVENTFULKEY, new Callback<JSONObject>() {
                    @Override
                    public void onItem(JSONObject result) {
                        JSONArray locations = (JSONArray) ((JSONObject) result.get("venues")).get("venue");
                        String lng, lat;
                        for (Object location : locations) {
                            JSONObject locationjson = (JSONObject) location;
                            if (locationjson.get("geocode_type").toString().equals("City Based GeoCodes")) {
                                lng = locationjson.get("longitude").toString();
                                lat = locationjson.get("latitude").toString();
                                Location locationObject = new Location("Eventful");
                                locationObject.setLongitude(Double.valueOf(lng));
                                locationObject.setLatitude(Double.valueOf(lat));
                                callback.onItem(locationObject);
                                return;
                            }
                        }
                    }
                });
                return null;
            }
        }.execute();
    }

    public static String locationToLatLngString(Location l) {
        return l.getLatitude() + "," + l.getLongitude();
    }

    public static Object altIfNull(Object check, Object alt) {
        return check != null ? check : alt;
    }
}

abstract class DynamicSearch<T> {

    Map<Integer, T> searchCache;
    List<SearchUpdateListener> listeners;
    int maxsize = 1;
    int pagesize;

    DynamicSearch() {
        searchCache = new HashMap<>();
        listeners = new ArrayList<>();
    }

    @Nullable
    public synchronized T get(int i) {
        if (searchCache.containsKey(i)) {
            //return item from cache
            return searchCache.get(i);
        } else {
            System.out.println("get called " + i);
            //set map to null to prevent unnecessary requests
            int page = i / pagesize;
            for (int x = page * pagesize; x < page * pagesize + pagesize; x++) {
                searchCache.put(x, null);
            }
            //perform query and return null to indicate result is not yet available
            query(i);
            return null;
        }
    }

    public int size() {
        return Math.min(searchCache.size() + 1, maxsize);
    }

    abstract void query(int i);

    public void addListener(SearchUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SearchUpdateListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(int start, int count) {
        System.out.println("notifying listeners " + start + " for " + count);
        for(SearchUpdateListener listener : listeners) {
            listener.onUpdate(this, start, count);
        }
    }

    public void reset() {
        int oldSize = size();
        searchCache = new HashMap<>();
        notifyListeners(0, oldSize);
    }

    interface SearchUpdateListener {
        void onUpdate(DynamicSearch self, int start, int count);
    }
}

class EventfulDynamicSearch extends DynamicSearch<EventfulEvent> {

    String location;
    int radius;

    EventfulDynamicSearch(String location, int radius) {
        this.location = location;
        this.radius = radius;
        pagesize = APIHandler.EVENTFUL_PAGE_SIZE;
    }

    @Override
    void query(int i) {
        final int page = i / APIHandler.EVENTFUL_PAGE_SIZE;
        APIHandler.queryEventful(location, radius, page + 1, new APIHandler.Callback<List<EventfulEvent>>() {
            @Override
            public void onItem(List<EventfulEvent> results) {
                if (results.size() == 0) {
                    //somehow notify callee that there are no results
                } else {
                    for (int i = 0; i < results.size(); i++) {
                        searchCache.put(page * APIHandler.EVENTFUL_PAGE_SIZE + i, results.get(i));
                    }
                    notifyListeners(page * APIHandler.EVENTFUL_PAGE_SIZE, APIHandler.EVENTFUL_PAGE_SIZE);
                }
            }
        }, new APIHandler.Callback<JSONObject>() {
            @Override
            public void onItem(JSONObject result) {
                EventfulDynamicSearch.this.maxsize = Integer.valueOf(result.get("page_count").toString()) * APIHandler.EVENTFUL_PAGE_SIZE;
                //System.out.println("result: " + result.toJSONString());
            }
        });
    }
}