package nl.group11.planplan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by s132054 on 14-3-2016.
 */
public class EventfulAdapter extends RecyclerView.Adapter<EventfulAdapter.ViewHolder> implements DynamicSearch.SearchUpdateListener, SearchAdapter {

    EventfulDynamicSearch searchSource;
    ImageCache imageCache;
    SimpleDateFormat df;
    Context context;

    EventfulAdapter(Context context, EventfulDynamicSearch search) {
        searchSource = search;
        searchSource.addListener(this);
        this.context = context;
        imageCache = new ImageCache(context);
        df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(cardView);
    }

    public int posOfID(String ID) {
        return searchSource.posOfID(ID);
    }

    protected void buildView(final ViewHolder holder, EventfulEvent event) {
        final EventItem item = new EventItem(context, event);
        if (event == null) {
            holder.title.setText("Loading...");
            holder.description.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.price.setVisibility(View.GONE);
            holder.image.setImageResource(R.drawable.imgnotfound);
            holder.buttons.setVisibility(View.GONE);
        } else {
            holder.buttons.setVisibility(View.VISIBLE);
            holder.title.setText(item.getTitle());
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(Html.fromHtml(item.getDescription()));
            holder.time.setVisibility(View.VISIBLE);
            if (item.getStartTime() == null || item.getEndTime() == null) {
                holder.time.setVisibility(View.GONE);
            } else if (item.getStartTime().equals(item.getEndTime())) {
                holder.time.setText(df.format(item.getStartTime()));
            } else {
                holder.time.setText(df.format(item.getStartTime()) + " till " + df.format(item.getEndTime()));
            }
            holder.price.setVisibility(View.VISIBLE);
            holder.price.setText(Html.fromHtml(item.getPrice()));
            holder.imgUrl = item.getImage();
            holder.image.setImageBitmap(imageCache.setImageFromURL(item.getImage(), new APIHandler.Callback<Bitmap>() {
                @Override
                public void onItem(Bitmap result) {
                    if (holder.imgUrl != null && holder.imgUrl.equals(item.getImage())) {
                        holder.image.setImageBitmap(result);
                    }
                }
            }));
            holder.cardView.setOnClickListener(item);
            if(item.hasPassed()) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.colorGrayedOut));
                holder.planningButton.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.colorGrayedOut));
                holder.favoritesButton.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.colorGrayedOut));
                holder.planningButton.setOnClickListener(null);
                holder.favoritesButton.setOnClickListener(null);
            } else {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.cardview_light_background));
                holder.planningButton.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.cardview_light_background));
                holder.favoritesButton.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.cardview_light_background));
                holder.planningButton.setOnClickListener(item);
                holder.favoritesButton.setOnClickListener(item);
            }
            item.checkInFavorites(new APIHandler.Callback<Boolean>() {
                @Override
                public void onItem(Boolean result) {
                    if (result) {
                        ((TextView) holder.favoritesButton.getChildAt(0)).setText("Remove from favorites");
                    } else {
                        ((TextView) holder.favoritesButton.getChildAt(0)).setText("Add to favorites");
                    }
                }
            });
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        EventfulEvent event = searchSource.get(position);
        buildView(holder, event);
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

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView, planningButton, favoritesButton;
        TextView title, description, price, planningButtonLabel, favoritesButtonLabel, time;
        ImageView image;
        String imgUrl;
        LinearLayout buttons;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
            LinearLayout texts = (LinearLayout) cardView.getChildAt(0);
            image = (ImageView) texts.getChildAt(0);
            title = (TextView) texts.getChildAt(1);
            time = (TextView) texts.getChildAt(2);
            price = (TextView) texts.getChildAt(3);
            description = (TextView) texts.getChildAt(4);

            buttons = (LinearLayout) texts.getChildAt(5);
            favoritesButton = (CardView) buttons.getChildAt(0);
            favoritesButtonLabel = (TextView) favoritesButton.getChildAt(0);
            planningButton = (CardView) buttons.getChildAt(2);
            planningButtonLabel = (TextView) planningButton.getChildAt(0);
        }
    }
}
