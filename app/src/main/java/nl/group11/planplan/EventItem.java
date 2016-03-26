package nl.group11.planplan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by s140442 on 07/03/2016.
 */
public class EventItem extends Item {

    EventfulEvent event;

    /**
     * Constructs an Item from an event.
     * @param c context in which item is created.
     * @param event an EventfulEvent.
     */
    public EventItem (Context c, EventfulEvent event) {
        super(c);
        this.event = event;
    }

    /**
     * Constructs an item from a jsonObject.
     * @param json a JSONOBJECT containing all information needed to construct an event.
     * @param c context from which item is created.
     */
    public EventItem(JSONObject json, Context c) {
        //only for testing
        super(c);
        this.event = EventfulEvent.fromJSON(json);
        userStartTime = event.getUserStartTime();
        userEndTime = event.getUserStopTime();
    }

    @Override
    public void update() {
        //TODO implement method
    }

    @Override
    public String getPrice() {
        return event.getPrice();
    }

    public Date getStartTime() {
        return event.getStartTime();
    }

    @Override
    public Date getEndTime() {
        return event.getStopTime();
    }

    @Override
    public String getImage() {
        return event.getImage();
    }

    @Override
    public String getID() {
        return event.getID();
    }

    @Override
    public Enum getType() {
        return Type.EVENT;
    }

    @Override
    public String getTitle() {
        return event.getTitle();
    }

    @Override
    public String getDescription() {
        return event.getDescription();
    }

    @Override
    public String getAddress() {
        return event.getAddress();
    }

    /**
     * @return whether the stoptime of this item has passed.
     */
    @Override
    public boolean hasPassed() {
        Calendar calendar = Calendar.getInstance(); //calendar gives current system time.
        return event.getStopTime().before(calendar.getTime());
    }

    /**
     * @return JSONObject containing all neccesary information to construct an event
     */
    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("id", getID());
        json.put("title", getTitle());
        json.put("type", getType().toString());
        json.put("price", getPrice());
        json.put("start_time", getStartTime().getTime());
        json.put("stop_time", getEndTime().getTime());
        if (getUserStartTime() !=null) {
            json.put("user_start_time", getUserStartTime().getTime());
        }
        if (getUserEndTime() != null) {
            json.put("user_end_time", getUserEndTime().getTime());
        }
        json.put("image", event.getImagesObject());
        json.put("description", getDescription());
        json.put("venue_address",getAddress());
        return json;
    }

    @Override
    public void buildView(final RecyclerView.ViewHolder holder, ImageCache imageCache) {
        buildView((EventViewHolder) holder, imageCache);
    }

    public void buildView(final EventViewHolder holder, ImageCache imageCache) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        if (event == null) {
            holder.title.setText("Loading...");
            holder.description.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.price.setVisibility(View.GONE);
            holder.image.setImageResource(R.drawable.imgnotfound);
            holder.buttons.setVisibility(View.GONE);
        } else {
            holder.buttons.setVisibility(View.VISIBLE);
            holder.title.setText(this.getTitle());
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(Html.fromHtml(this.getDescription()));
            holder.time.setVisibility(View.VISIBLE);
            if (this.getStartTime() == null || this.getEndTime() == null) {
                holder.time.setVisibility(View.GONE);
            } else if (this.getStartTime().equals(this.getEndTime())) {
                holder.time.setText(df.format(this.getStartTime()));
            } else {
                holder.time.setText(df.format(this.getStartTime()) + " till " + df.format(this.getEndTime()));
            }
            holder.price.setVisibility(View.VISIBLE);
            holder.price.setText(Html.fromHtml(this.getPrice()));
            holder.imgUrl = this.getImage();
            holder.image.setImageBitmap(imageCache.setImageFromURL(this.getImage(), new APIHandler.Callback<Bitmap>() {
                @Override
                public void onItem(Bitmap result) {
                    if (holder.imgUrl != null && holder.imgUrl.equals(EventItem.this.getImage())) {
                        holder.image.setImageBitmap(result);
                    }
                }
            }));
            holder.cardView.setOnClickListener(this);
            if(this.hasPassed()) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.colorGrayedOut));
                holder.planningButton.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.colorGrayedOut));
                holder.favoritesButton.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.colorGrayedOut));
                holder.planningButton.setOnClickListener(null);
                holder.favoritesButton.setOnClickListener(null);
            } else {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.cardview_light_background));
                holder.planningButton.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.cardview_light_background));
                holder.favoritesButton.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.getContext(), R.color.cardview_light_background));
                holder.planningButton.setOnClickListener(this);
                holder.favoritesButton.setOnClickListener(this);
            }
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

class EventViewHolder extends RecyclerView.ViewHolder {

    CardView cardView, planningButton, favoritesButton;
    TextView title, description, price, planningButtonLabel, favoritesButtonLabel, time;
    ImageView image;
    String imgUrl;
    LinearLayout buttons;

    public EventViewHolder(CardView cardView) {
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
