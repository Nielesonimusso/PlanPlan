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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(cardView);
    }

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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        GooglePlace place = position < places.size() ? places.get(position) : null;
        buildView(holder, place);
    }

    private void buildView(final ViewHolder holder, GooglePlace place) {
        if (place == null) {
            holder.title.setText("Loading...");
            holder.description.setText("");
            holder.price.setText("");
            holder.image.setImageResource(R.drawable.imgnotfound);
            holder.buttons.setVisibility(View.GONE);
        } else {
            final GooglePlacesItem item;
            if (place.getType().equals(Type.RESTAURANT)) {
                item = new RestaurantItem(context, place);
            } else {
                item = new OtherItem(context, place);
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
            item.checkInPlanning(new APIHandler.Callback<Boolean>() {
                @Override
                public void onItem(Boolean result) {
                    if (result) {
                        ((TextView) holder.planningButton.getChildAt(0)).setText("Remove from planning");
                    } else {
                        ((TextView) holder.planningButton.getChildAt(0)).setText("Add to planning");
                    }
                }
            });
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
            favoritesButton = (CardView) buttons.getChildAt(0);
            favoritesButtonLabel = (TextView) favoritesButton.getChildAt(0);
            planningButton = (CardView) buttons.getChildAt(2);
            planningButtonLabel = (TextView) planningButton.getChildAt(0);
        }
    }
}
