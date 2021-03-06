package nl.group11.planplan;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Created by s132054 on 14-3-2016.
 *
 * ItemAdapter for EventItems (see {@link ItemAdapter})
 */
public class EventfulAdapter extends ItemAdapter<EventViewHolder> implements DynamicSearch.SearchUpdateListener {

    //dynamic source of EventEvents
    EventfulDynamicSearch searchSource;

    EventfulAdapter(Context context, EventfulDynamicSearch search) {
        super(context);
        searchSource = search;
        searchSource.addListener(this);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new EventViewHolder(cardView);
    }

    @Override
    public int posOfID(String ID) {
        int result = searchSource.posOfID(ID);
        System.out.println("Request for pos of ID " + ID + " with pos " + result);
        return result;
    }

    @Override
    public void onBindViewHolder(final EventViewHolder holder, int position) {
        EventfulEvent event = searchSource.get(position);
        EventItem item = new EventItem(context, event);
        item.buildView(holder, imageCache, false);
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
}
