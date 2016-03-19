package nl.group11.planplan;

import android.content.Context;
import android.graphics.Bitmap;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s132054 on 16-3-2016.
 */
public class GooglePlacesAdapter extends RecyclerView.Adapter<GooglePlacesAdapter.ViewHolder> implements APIHandler.Callback<List<GooglePlace>>, SearchAdapter {

    List<GooglePlace> places;
    ImageCache imageCache;
    String type;

    GooglePlacesAdapter(Context context, String location, int radius, String type) {
        imageCache = new ImageCache(context);
        places = new ArrayList<>();
        this.type = type;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        GooglePlace place = position < places.size() ? places.get(position) : null;
        if (place == null) {
            holder.title.setText("Loading...");
            holder.description.setText("");
            holder.price.setText("");
            holder.image.setImageResource(R.drawable.imgnotfound);
            holder.buttons.setVisibility(View.GONE);
        } else {
            final GooglePlacesItem item;
            if (place.getType().equals(Type.RESTAURANT)) {
                item = new RestaurantItem(holder.cardView.getContext(), place);
            } else {
                item = new OtherItem(holder.cardView.getContext(), place);
            }
            holder.buttons.setVisibility(View.VISIBLE);
            holder.title.setText(item.getTitle());
            holder.description.setText(Html.fromHtml(item.getDescription()));
            holder.description.setMovementMethod(LinkMovementMethod.getInstance());
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
