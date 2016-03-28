package nl.group11.planplan;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
                    final String requestUrl = "http://api.eventful.com/json/events/search?" +
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
                            System.out.println(requestUrl);
                            if (!result.containsKey("no_connection")) {
                                miscCallback.onItem(result);
                                Object resultItemsObj = ((JSONObject) result.get("events")).get("event");
                                if (resultItemsObj instanceof JSONArray) {
                                    JSONArray resultItems = (JSONArray) resultItemsObj;
                                    for (Object event : resultItems) {
                                        JSONObject eventjson = (JSONObject) event;
                                        //System.out.println(eventjson.toJSONString());
                                        events.add(EventfulEvent.fromJSON(eventjson));
                                    }
                                } else if (resultItemsObj instanceof JSONObject) {
                                    JSONObject resultItem = (JSONObject) resultItemsObj;
                                    events.add(EventfulEvent.fromJSON(resultItem));
                                }
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
                            if (results != null) {
                                for (Object item : results) {
                                    places.add(GooglePlace.fromJSON((JSONObject) item));
                                }
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
                    JSONObject noConnection = new JSONObject();
                    noConnection.put("no_connection", "no internet connection");
                    return noConnection;
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
                try {
                    JSONHTTPRequest("http://api.eventful.com/json/venues/search?" +
                            "location=" + URLEncoder.encode(location, "UTF-8") +
                            "&app_key=" + EVENTFULKEY, new Callback<JSONObject>() {
                        @Override
                        public void onItem(JSONObject result) {
                            if (result.get("venues") != null) {
                                JSONArray locations = (JSONArray) ((JSONObject) result.get("venues")).get("venue");
                                String lng, lat;
                                Object location = locations.get(0);
                                JSONObject locationjson = (JSONObject) location;
                                lng = locationjson.get("longitude").toString();
                                lat = locationjson.get("latitude").toString();
                                Location locationObject = new Location("Eventful");
                                locationObject.setLongitude(Double.valueOf(lng));
                                locationObject.setLatitude(Double.valueOf(lat));
                                callback.onItem(locationObject);
                                return;
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

    public static String locationToLatLngString(Location l) {
        return l.getLatitude() + "," + l.getLongitude();
    }

    public static Object altIfNull(Object check, Object alt) {
        return check != null ? check : alt;
    }

    public static String getAccount(Context context) {
        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
        return accounts[0].name.replace('.', '*');//replace dots with stars to prevent dots in firebase key
    }

    public static void getDatabaseFavorites(Enum type, Firebase firebase, Context context, APIHandler.Callback<ArrayList<Item>> callback) {
        Firebase ref;
        String account;

        account = APIHandler.getAccount(context);
        ref = firebase.child(account).child("favorites").child(type.toString());
        getDatabase(ref, context, callback);
    }

    public static void getDatabasePlanning(Firebase firebase, final Context context, final APIHandler.Callback<ArrayList<Item>> callback) {
        Firebase ref;
        String account;

        account = APIHandler.getAccount(context);
        ref = firebase.child(account).child("planning");
        getDatabase(ref, context, callback);
    }

    private static void getDatabase(Firebase ref, final Context context, final APIHandler.Callback<ArrayList<Item>> callback) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Item> itemList = new ArrayList<Item>();

                for (DataSnapshot key : dataSnapshot.getChildren()) {
                    Item item = snapshotToItem(key, context);
                    if (item.hasPassed()) {
                        key.getRef().removeValue();
                    } else {
                        itemList.add(item);
                    }
                }
                callback.onItem(itemList);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                firebaseError.toException().printStackTrace();
            }
        });
    }

    public static Item snapshotToItem(DataSnapshot snapshot, Context context) {
        GenericTypeIndicator<Map<String, Object>> t = new GenericTypeIndicator<Map<String, Object>>() {};
        JSONObject json = new JSONObject(snapshot.getValue(t));
        /*
        String type = (String) snapshot.child(Data.TYPE.toString()).getValue();
        json.put("id", (String) snapshot.child(Data.ID.toString()).getValue());
        json.put("title", (String) snapshot.child(Data.TITLE.toString()).getValue());
        json.put("type", type);
        json.put("price", (String) snapshot.child(Data.PRICE.toString()).getValue());
        json.put("start_time", String.valueOf(snapshot.child(Data.STARTTIME.toString()).getValue()));
        json.put("stop_time", String.valueOf(snapshot.child(Data.ENDTIME.toString()).getValue()));
        json.put("user_start_time", (String) snapshot.child(Data.USERSTARTTIME.toString()).getValue());
        json.put("user_end_time", (String) snapshot.child(Data.USERENDTIME.toString()).getValue());
        json.put("image", (String) snapshot.child(Data.IMAGE.toString()).getValue());
        json.put("description", (String) snapshot.child(Data.DESCRIPTION.toString()).getValue());
        json.put("venue_address", (String) snapshot.child(Data.ADDRESS.toString()).getValue());
        */
        System.out.println(json.toJSONString());

        Item item;
        String type = json.get("type").toString();
        if (type.equals(Type.EVENT.toString())) {
            item = new EventItem(json, context);
        } else if (type.equals(Type.RESTAURANT.toString())) {
            item = new RestaurantItem(json, context);
        } else {
            item = new OtherItem(json, context);
        }
        return item;
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
                if (results.size() > 0) {
                    for (int i = 0; i < results.size(); i++) {
                        searchCache.put(page * APIHandler.EVENTFUL_PAGE_SIZE + i, results.get(i));
                    }
                }
                /* remove items out of range */
                if (results.size() < APIHandler.EVENTFUL_PAGE_SIZE) {
                    for (Iterator<Integer> iterator = searchCache.keySet().iterator(); iterator.hasNext();) {
                        if (iterator.next() >= results.size() + page * APIHandler.EVENTFUL_PAGE_SIZE) {
                            iterator.remove();
                        }
                    }
                }
                System.out.println(size());
                //*/
                notifyListeners(page * APIHandler.EVENTFUL_PAGE_SIZE, results.size());
            }
        }, new APIHandler.Callback<JSONObject>() {
            @Override
            public void onItem(JSONObject result) {
                int pagesize = Integer.valueOf(result.get("page_count").toString());
                int itemsize = Integer.valueOf(result.get("total_items").toString());
                EventfulDynamicSearch.this.maxsize = itemsize > pagesize * APIHandler.EVENTFUL_PAGE_SIZE ? pagesize * APIHandler.EVENTFUL_PAGE_SIZE : itemsize;
                //System.out.println("result: " + result.toJSONString());
            }
        });
    }

    public int posOfID(String id) {
        for (Map.Entry<Integer, EventfulEvent> event : searchCache.entrySet()) {
            if (event.getValue().getID().equals(id)) {
                return event.getKey();
            }
        }
        return -1;
    }
}