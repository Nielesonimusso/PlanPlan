package nl.group11.planplan;

import android.app.DownloadManager;
import android.os.AsyncTask;

import com.evdb.javaapi.APIConfiguration;
import com.evdb.javaapi.EVDBAPIException;
import com.evdb.javaapi.EVDBRuntimeException;
import com.evdb.javaapi.data.SearchResult;
import com.evdb.javaapi.data.request.EventSearchRequest;
import com.evdb.javaapi.operations.EventOperations;
import com.evdb.javaapi.data.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s132054 on 7-3-2016.
 */
public class APIHandler {

    private static final String EVENTFULKEY = "KMFgPh35QqPkMgXw";

    public static void queryEventful(String location, int radius, final EventCallback callback) {

        new AsyncTask<QueryParams, Void, List<Event>>() {

            @Override
            protected List<Event> doInBackground(QueryParams... params) {
                String location = params[0].location;
                int radius = params[0].radius;
                try {
                    APIConfiguration.getApiKey();
                } catch (EVDBRuntimeException ex) {
                    APIConfiguration.setApiKey(EVENTFULKEY);
                }

                EventOperations eo = new EventOperations();
                EventSearchRequest esr = new EventSearchRequest();

                esr.setLocation(location);
                esr.setLocationRadius(radius);
                esr.setPageSize(20);
                esr.setPageNumber(1);
                SearchResult sr = null;
                try {
                    sr = eo.search(esr);
                    return sr.getEvents();
                } catch(EVDBRuntimeException | EVDBAPIException var) {
                    System.err.println("Error while performing Eventful query");
                }
                return new ArrayList<Event>();
            }

            @Override
            protected void onPostExecute(List<Event> events) {
                super.onPostExecute(events);
                callback.callback(events);
            }
        }.execute(new QueryParams(location, radius));
    }

    interface EventCallback {
        void callback(List<Event> events);
    }
}

class QueryParams {

    String location;
    int radius;

    QueryParams(String location, int radius) {
        this.location = location;
        this.radius = radius;
    }

}