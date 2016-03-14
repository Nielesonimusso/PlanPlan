package nl.group11.planplan;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evdb.javaapi.data.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s132054 on 14-3-2016.
 */
public class EventfulAdapter extends RecyclerView.Adapter<EventfulAdapter.ViewHolder> implements DynamicSearch.SearchUpdateListener{

    EventfulDynamicSearch searchSource;

    EventfulAdapter(EventfulDynamicSearch search) {
        searchSource = search;
        searchSource.addListener(this);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.event, parent, false);
        TextView text = (TextView) cardView.getChildAt(0);
        return new ViewHolder(cardView, text);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EventfulEvent event = searchSource.get(position);
        if (event == null) {
            holder.title.setText("Loading...");
        } else {
            holder.title.setText(event.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return searchSource.size();
    }

    @Override
    public void onUpdate(DynamicSearch self, int start, int count) {
        notifyItemRangeChanged(start, count);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;

        public ViewHolder(CardView cardView, TextView title) {
            super(cardView);
            this.cardView = cardView;
            this.title = title;
        }
    }
}
