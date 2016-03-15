package nl.group11.planplan;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by s132054 on 14-3-2016.
 */
public class EventfulAdapter extends RecyclerView.Adapter<EventfulAdapter.ViewHolder> implements DynamicSearch.SearchUpdateListener{

    EventfulDynamicSearch searchSource;
    ImageCache imageCache;

    EventfulAdapter(EventfulDynamicSearch search) {
        searchSource = search;
        searchSource.addListener(this);

        imageCache = new ImageCache();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.event, parent, false);
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
        } else {
            holder.title.setText(item.getTitle());
            holder.description.setText(Html.fromHtml(item.getDescription()));
            holder.price.setText(Html.fromHtml(item.getPrice()));
            holder.imgUrl = item.getImage();
            holder.image.setImageBitmap(imageCache.setImageFromURL(holder.image.getContext(), item.getImage(), new APIHandler.Callback<Bitmap>() {
                @Override
                public void onItem(Bitmap result) {
                    if (holder.imgUrl.equals(item.getImage())) {
                        holder.image.setImageBitmap(result);
                    }
                }
            }));
            holder.cardView.setOnClickListener(item);
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
        TextView title, description, price;
        ImageView image;
        String imgUrl;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
            LinearLayout texts = (LinearLayout) cardView.getChildAt(0);
            image = (ImageView) texts.getChildAt(0);
            title = (TextView) texts.getChildAt(1);
            price = (TextView) texts.getChildAt(2);
            description = (TextView) texts.getChildAt(3);
        }
    }
}
