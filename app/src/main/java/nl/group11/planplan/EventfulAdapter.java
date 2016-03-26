package nl.group11.planplan;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by s132054 on 14-3-2016.
 */
public class EventfulAdapter extends ItemAdapter<EventViewHolder> implements DynamicSearch.SearchUpdateListener, SearchAdapter {

    EventfulDynamicSearch searchSource;
    ImageCache imageCache;
    Context context;

    EventfulAdapter(Context context, EventfulDynamicSearch search) {
        searchSource = search;
        searchSource.addListener(this);
        this.context = context;
        imageCache = new ImageCache(context);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new EventViewHolder(cardView);
    }

    @Override
    public int posOfID(String ID) {
        int result = searchSource.posOfID(ID);
        System.out.println("Request for update on ID " + ID + " with pos " + result);
        return result;
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        EventfulEvent event = searchSource.get(position);
        EventItem item = new EventItem(context, event);
        item.buildView(holder, imageCache);
    }

    @Override
    public int getItemCount() {
        return searchSource.size();
    }

    @Override
    public void onUpdate(DynamicSearch self, int start, int count) {
        /*
        notifyItemChanged(start);
        notifyItemRangeInserted(start + 1, count - 1);
        */
        notifyDataSetChanged();
    }

    @Override
    public void updateSearch(String location, int radius) {
        searchSource.location = location;
        searchSource.radius = radius;
        searchSource.reset();
    }
}
