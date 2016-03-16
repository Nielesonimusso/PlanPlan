package nl.group11.planplan;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by s132054 on 14-3-2016.
 */
public class EventfulAdapter extends RecyclerView.Adapter<EventfulAdapter.ViewHolder> implements DynamicSearch.SearchUpdateListener, SearchAdapter {

    EventfulDynamicSearch searchSource;
    ImageCache imageCache;

    EventfulAdapter(Context context, EventfulDynamicSearch search) {
        searchSource = search;
        searchSource.addListener(this);

        imageCache = new ImageCache(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        EventfulEvent event = searchSource.get(position);
        final EventItem item = new EventItem(holder.cardView.getContext(), event);
        if (event == null) {
            holder.title.setText("Loading...");
            holder.description.setText("");
            holder.price.setText("");
            holder.image.setImageResource(R.drawable.imgnotfound);
            holder.buttons.setVisibility(View.GONE);
        } else {
            holder.buttons.setVisibility(View.VISIBLE);
            holder.title.setText(item.getTitle());
            holder.description.setText(Html.fromHtml(item.getDescription()));
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
            holder.planningButton.setOnClickListener(item);
            holder.favoritesButton.setOnClickListener(item);
            /*
            if (item.checkItemInFavorites()) {
                holder.favoritesButtonLabel.setText("Remove from favorites");
            }
            */
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

    @Override
    public void updateSearch(String location, int radius) {
        searchSource.location = location;
        searchSource.radius = radius;
        searchSource.reset();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView, planningButton, favoritesButton;
        TextView title, description, price, planningButtonLabel, favoritesButtonLabel;
        ImageView image;
        String imgUrl;
        LinearLayout buttons;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
            LinearLayout texts = (LinearLayout) cardView.getChildAt(0);
            image = (ImageView) texts.getChildAt(0);
            title = (TextView) texts.getChildAt(1);
            price = (TextView) texts.getChildAt(2);
            description = (TextView) texts.getChildAt(3);

            buttons = (LinearLayout) texts.getChildAt(4);
            planningButton = (CardView) buttons.getChildAt(0);
            planningButtonLabel = (TextView) planningButton.getChildAt(0);
            favoritesButton = (CardView) buttons.getChildAt(2);
            favoritesButtonLabel = (TextView) favoritesButton.getChildAt(0);
        }
    }
}
