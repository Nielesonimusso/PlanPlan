package nl.group11.planplan;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s132054 on 16-3-2016.
 */
public class GooglePlacesAdapter extends ItemAdapter<PlaceViewHolder> implements APIHandler.Callback<List<GooglePlace>>, SearchAdapter {

    List<GooglePlace> places;
    ImageCache imageCache;
    String type;
    Context context;

    GooglePlacesAdapter(Context context, String location, int radius, String type) {
        imageCache = new ImageCache(context);
        places = new ArrayList<>();
        this.type = type;
        this.context = context;
        performQuery(location, radius);
    }

    public void performQuery(String location, int radius) {
        APIHandler.queryGooglePlaces(location, radius, type, this);
    }

    @Override
    public void updateSearch(String location, int radius) {
        places = new ArrayList<>();
        notifyDataSetChanged();
        performQuery(location, radius);
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new PlaceViewHolder(cardView);
    }

    @Override
    public int posOfID(String ID) {
        for (int i = 0; i < places.size(); i++) {
            if (places.get(i).getID().equals(ID)) {
                System.out.println("Request for update on ID " + ID + " with pos " + i);
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(final PlaceViewHolder holder, int position) {
        GooglePlace place = position < places.size() ? places.get(position) : null;
        GooglePlacesItem item;
        if (place.getType().equals(Type.RESTAURANT)) {
            item = new RestaurantItem(context, place);
        } else {
            item = new OtherItem(context, place);
        }
        item.buildView(holder, imageCache, false);
    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    @Override
    public void onItem(List<GooglePlace> result) {

        int oldSize = places.size();
        int newLength = result.size();
        places.addAll(result);
        //notifyItemRangeInserted(oldSize, newLength);
        notifyDataSetChanged();
    }
}
