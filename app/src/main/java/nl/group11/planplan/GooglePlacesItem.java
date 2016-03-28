package nl.group11.planplan;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.simple.JSONObject;

import java.util.Date;

/**
 * Created by s140442 on 07/03/2016.
 */
abstract public class GooglePlacesItem extends Item {

    GooglePlace place;

    public GooglePlacesItem(Context c, GooglePlace place) {
        super(c);
        this.place = place;
    }
    public GooglePlacesItem(JSONObject json, Context c) {
        //only for testing
        super(c);
        this.place = GooglePlace.fromJSON(json);
        setUserStartTime(place.getUserStartTime());
        setUserEndTime(place.getUserStopTime());
    }

    @Override
    public void update() {
        //TODO implement method
    }

    /**
     * get price of item
     * @return String that states the price level of the item
     */
    @Override
    public String getPrice() {
        int pricelevel = place.getPriceLevel();
        String price = null;
        switch (pricelevel){
            case -1: price ="No price information available";
                break;
            case 0: price = "Free";
                break;
            case 1: price = "Inexpensive";
                break;
            case 2: price = "Moderate";
                break;
            case 3: price = "Expensive";
                break;
            case 4: price = "Very Expensive";
                break;
        }
        return price;
    }

    @Override
    /**
     * get item starttime.
     * @return returns null, since Google places items don't have start times
     */
    public Date getStartTime() {
        return null;
    }

    /**
     * get item stoptime.
     * @return returns null, since Google places items don't have stop times
     */
    @Override
    public Date getEndTime() {
        return null;
    }

    /**
     * get item ID
     * @return returns item ID
     */
    @Override
    public String getID() {
        return place.getID();
    }

    /**
     * get item title
     * @return returns item title
     */
    @Override
    public String getTitle() {
        return place.getName();
    }

    /**
     * get item description
     * @return returns empty string, since Google places items don't have descriptions
     */
    @Override
    public String getDescription() {
        return "";
    }

    /**
     * get item address
     * @return returns string containing human-readable address of the item
     */
    @Override
    public String getAddress() {
        return place.getAddress();
    }

    /**
     * get item image
     * @return returns the image belonging to the item
     */
    public String getImage() {
        return place.getImage();
    }

    /**
     * determine whether the event has passed
     * @return returns boolean, in this case always false because Google places items don't have start/stop times
     */
    @Override
    public boolean hasPassed() {
        return false;
    }

    /**
     * item to JSONObject to store in database
     * @return JSONObject containing the item information.
     */
    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("place_id", getID());
        json.put("name", getTitle());
        json.put("type",getType().toString());
        json.put("price_level", place.getPriceLevel());
        if (getUserStartTime() !=null) {
            json.put("user_start_time", getUserStartTime().getTime());
        }
        if (getUserEndTime() != null) {
            json.put("user_end_time", getUserEndTime().getTime());
        }
        json.put("icon", getImage());
        json.put("vicinity",getAddress());
        return json;
    }

    @Override
    public void buildView(final RecyclerView.ViewHolder holder, ImageCache imageCache) {
        buildView((PlaceViewHolder) holder, imageCache);
    }

    public void buildView(final PlaceViewHolder holder, ImageCache imageCache) {
        if (place == null) {
            holder.title.setText("Loading...");
            holder.description.setText("");
            holder.price.setText("");
            holder.image.setImageResource(R.drawable.imgnotfound);
            holder.buttons.setVisibility(View.GONE);
        } else {
            holder.buttons.setVisibility(View.VISIBLE);
            holder.title.setText(this.getTitle());
            holder.time.setText("Mo-Fri 09:00-22:00");
            holder.description.setText(Html.fromHtml(this.getDescription()));
            holder.description.setMovementMethod(LinkMovementMethod.getInstance());
            holder.price.setText(Html.fromHtml(this.getPrice()));
            holder.imgUrl = this.getImage();
            holder.image.setImageBitmap(imageCache.setImageFromURL(this.getImage(), new APIHandler.Callback<Bitmap>() {
                @Override
                public void onItem(Bitmap result) {
                    if (holder.imgUrl != null && holder.imgUrl.equals(GooglePlacesItem.this.getImage())) {
                        holder.image.setImageBitmap(result);
                    }
                }
            }));
            holder.cardView.setOnClickListener(this);
            holder.planningButton.setOnClickListener(this);
            holder.favoritesButton.setOnClickListener(this);
            this.checkInFavorites(new APIHandler.Callback<Boolean>() {
                @Override
                public void onItem(Boolean result) {
                    if (result) {
                        ((TextView) holder.favoritesButton.getChildAt(0)).setText("Remove from favorites");
                    } else {
                        ((TextView) holder.favoritesButton.getChildAt(0)).setText("Add to favorites");
                    }
                }
            });
            this.checkInPlanning(new APIHandler.Callback<Boolean>() {
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
}


class PlaceViewHolder extends RecyclerView.ViewHolder {

    CardView cardView, planningButton, favoritesButton;
    TextView title, time, description, price, planningButtonLabel, favoritesButtonLabel;
    ImageView image;
    String imgUrl;
    LinearLayout buttons;

    public PlaceViewHolder(CardView cardView) {
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
