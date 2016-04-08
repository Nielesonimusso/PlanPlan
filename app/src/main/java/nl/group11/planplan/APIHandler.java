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
 *
 * Class containing methods and fields needed for accessing APIs used in PlanPlan
 */
public class APIHandler {

    //API key used to access Eventful Web API
    private static final String EVENTFULKEY = "KMFgPh35QqPkMgXw";
    //page size as used when retrieving events from Eventful Web API
    static final int EVENTFUL_PAGE_SIZE = 10;

    //API key used to access Google Places Web Service API
    private static final String GOOGLEPLACESKEY = "AIzaSyDNrounxGt7hKmWqQftfYUniyd3BmXEmZA";
    //page size of Google Places Web Services API (not changable by client)
    static final int GOOGLEPLACES_PAGE_SIZE = 20;

    /**
     * Alternative signature for queryEventful(), providing empty Callback instance for miscCallback
     * parameter. This method is not used anymore.
     *
     * See also {@link #queryEventful(String, int, int, Callback, Callback)}
     */
    @Deprecated
    public static void queryEventful(final String location, final int radius, final int page, final Callback<List<EventfulEvent>> callback) {
        queryEventful(location, radius, page, callback, new Callback<JSONObject>() {
            @Override
            public void onItem(JSONObject result) {

            }
        });
    }

    /**
     * Queries Eventful using settings, and providing two Callbacks for this methods to pass back
     * the query results.
     *
     * @param location location parameter of the query, in the form of a city name like "Eindhoven"
     *                 or "New York".
     * @param radius the radius parameter of the query in kilometers.
     * @param page the page number parameter of the query. This indicates which page of the total
     *             search results will be returned.
     * @param callback a Callback instance, whose onItem() method will be called with a List of
     *                 EventfulEvents, representing the resulting events from the query.
     * @param miscCallback a Callback instance, whose onItem() method will be called when the JSON
     *                     result from the query comes in. This raw JSON result will be passed to
     *                     the onItem() call. This feature can be used in places where more
     *                     information about the query needs to be used, other than the actual
     *                     events
     */
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

    /**
     * Queries Google Places Web Service API given query parameters
     *
     * @param location location parameter used in the query. Needs to be formatted as follows:
     *                 {@code latitude,longitude}
     * @param radius radius parameter of the query in meters.
     * @param type the type parameter of the query. This can have any of the values as given at
     *             {@see https://developers.google.com/places/supported_types}
     * @param callback Callback instance, whose onItem() method will be called with a List of
     *                 GooglePlaces, representing the query results, when available.
     */
    public static void queryGooglePlaces(final String location, final int radius, final String type, final Callback<List<GooglePlace>> callback) {
        queryGooglePlaces(location, radius, type, callback, "");
    }

    /**
     * Queries Google Places Web Service API, using the specified {@code pagetoken} to get the
     * next pages of an original query, when the page token is not equal to {@code ""}. When
     * {@code pagetoken} is equal to {@code ""}, a normal query is performed using the given parameters.
     *
     * @param pagetoken the page token which specifies the page to get from an original query (
     *                  as received along with the results of the original query).
     *
     * See also {@link #queryGooglePlaces(String, int, String, Callback)}
     */
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

    /**
     * Generic HTTP request method, giving the results back in the form of a JSONObject through a
     * callback method
     *
     * @param url the URL to send a request to, given as a String
     * @param callback Callback instance of which the onItem() method will be called, passing the
     *                 result from the query in the form of a JSONObject.
     */
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

    /**
     * callback interface, for creating callbacks that accept items of type {@code T}
     * @param <T> the type accepted by this callback
     */
    interface Callback<T> {
        void onItem(T result);
    }

    /**
     * Convert a city name to a {@code Location} object, passing the result to a callback
     *
     * @param location city name to convert
     * @param callback Callback instance, of which the onItem() will be called, passing the
     *                 converted city name as a Location object.
     */
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

    /**
     * Convert Location object to {@code latitude,longitude}-formatted String
     * @param l Location object to be converted
     * @return {@code latitude,longitude}-formatted String, representing {@code l}.
     */
    public static String locationToLatLngString(Location l) {
        return l.getLatitude() + "," + l.getLongitude();
    }

    /**
     * Utility method to return alternative when {@code check == null}
     *
     * @param check
     * @param alt
     * @return check != null ? check : alt
     */
    public static Object altIfNull(Object check, Object alt) {
        return check != null ? check : alt;
    }

    /**
     * get account String
     *
     * @param context needed to get account String
     * @return account String (google-account email address)
     */
    public static String getAccount(Context context) {
        Account[] accounts = AccountManager.get(context).getAccountsByType("com.google");
        return accounts[0].name.replace('.', '*');//replace dots with stars to prevent dots in firebase key
    }

    /**
     * get favorites from database
     *
     * @param type type of favorites to query
     * @param firebase database reference
     * @param context context used for other methods (see {@link #getAccount(Context)} and
     *                {@link #getDatabase(Firebase, Context, Callback)})
     * @param callback Callback instance of which the onItem() method will be called, passing the
     *                 result of the query (favorites in database)
     */
    public static void getDatabaseFavorites(Enum type, Firebase firebase, Context context, APIHandler.Callback<ArrayList<Item>> callback) {
        Firebase ref;
        String account;

        account = APIHandler.getAccount(context);
        ref = firebase.child(account).child("favorites").child(type.toString());
        getDatabase(ref, context, callback);
    }

    /**
     * get items in planning from database
     *
     * @param firebase database reference
     * @param context context used for other methods (see {@link #getAccount(Context)} and
     *        {@link #getDatabase(Firebase, Context, Callback)})
     * @param callback Callback instance of which the onItem() method will be called, passing the
     *                 result of the query (planning items in database)
     */
    public static void getDatabasePlanning(Firebase firebase, final Context context, final APIHandler.Callback<ArrayList<Item>> callback) {
        Firebase ref;
        String account;

        account = APIHandler.getAccount(context);
        ref = firebase.child(account).child("planning");
        getDatabase(ref, context, callback);
    }

    /**
     * Generic method for retrieving items from database
     *
     * @param ref database reference
     * @param context context needed to pass to items
     * @param callback callback of which onItem() will be called, passing items requested from
     *                 database.
     */
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

    /**
     * convert a DataSnapshot representing an item to an Item object.
     *
     * @param snapshot DataSnapshot to be converted
     * @param context passed to Item object
     * @return Item object representing {@code snapshot}
     */
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

/**
 * search class, which can be used to "iterate" a the results from a query, using specified
 * parameters
 *
 * @param <T> type of items that are queried.
 */
abstract class DynamicSearch<T> {

    Map<Integer, T> searchCache;
    List<SearchUpdateListener> listeners;
    int maxsize = 1;
    int pagesize;

    DynamicSearch() {
        searchCache = new HashMap<>();
        listeners = new ArrayList<>();
    }

    /**
     * get item at index {@code i} of the search
     *
     * @param i
     * @return
     */
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

    /**
     * get the number of items in the query
     * @return
     */
    public int size() {
        return Math.min(searchCache.size() + 1, maxsize);
    }

    /**
     * placeholder for actual query
     * @param i
     */
    abstract void query(int i);

    public void addListener(SearchUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SearchUpdateListener listener) {
        listeners.remove(listener);
    }

    /**
     * notify listeners of change in cache
     * @param start starting index of the change
     * @param count number of items changed
     */
    public void notifyListeners(int start, int count) {
        System.out.println("notifying listeners " + start + " for " + count);
        for(SearchUpdateListener listener : listeners) {
            listener.onUpdate(this, start, count);
        }
    }

    /**
     * resets search cache, and notifies listeners
     */
    public void reset() {
        int oldSize = size();
        searchCache = new HashMap<>();
        notifyListeners(0, oldSize);
    }

    /**
     * interface for listeners
     */
    interface SearchUpdateListener {
        void onUpdate(DynamicSearch self, int start, int count);
    }
}

/**
 * Eventful version of DynamicSearch class
 */
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

    /**
     * get position of item with ID {@code id}.
     *
     * @param id the ID to query
     * @return position in cache of item with id {@code id}
     */
    public int posOfID(String id) {
        for (Map.Entry<Integer, EventfulEvent> event : searchCache.entrySet()) {
            if (event.getValue().getID().equals(id)) {
                return event.getKey();
            }
        }
        return -1;
    }
}