package nl.group11.planplan;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s132054 on 25-3-2016.
 *
 * ItemAdapter for items from Firebase (either EventItem or GooglePlaceItem) (see {@link ItemAdapter})
 */
public class FirebaseAdapter extends ItemAdapter<RecyclerView.ViewHolder> implements APIHandler.Callback<ArrayList<Item>>, ValueEventListener {

    List<Item> cache;
    Firebase endpoint;
    Type type;

    FirebaseAdapter(Context context, Type type) {
        super(context);
        cache = new ArrayList<>();
        endpoint = new Firebase("https://planplan.firebaseio.com/");
        endpoint.child(APIHandler.getAccount(context)).child("favorites").child(type.toString()).addValueEventListener(this);
        APIHandler.getDatabaseFavorites(type, endpoint, context, this);
        this.type = type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView;
        if (type == Type.EVENT) {
            cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
            return new EventViewHolder(cardView);
        } else {
            cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
            return new PlaceViewHolder(cardView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Item item;
        if (position >= cache.size()) {
            switch (type) { //match type just in case
                case EVENT:
                    item = new EventItem(context, null);
                    break;
                case RESTAURANT:
                    item = new RestaurantItem(context, null);
                    break;
                default:
                    item = new OtherItem(context, null);
                    break;
            }
        } else {
            item = cache.get(position);
        }
        System.out.println("Adapter type: " + type + " | Item type: " + item.getType());
        item.buildView(holder, imageCache, false);
    }

    @Override
    public int getItemCount() {
        return cache.size();
    }

    @Override
    public void onItem(ArrayList<Item> result) {
        cache = result;
        notifyDataSetChanged();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        ArrayList<Item> templist = new ArrayList<>();
        for (DataSnapshot entry : dataSnapshot.getChildren()) {
            Item item = APIHandler.snapshotToItem(entry, context);
            templist.add(item);
        }
        cache = templist;
        notifyDataSetChanged();
    }

    @Override
    public void onCancelled(FirebaseError firebaseError) {

    }

    @Override
    int posOfID(String ID) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getID().equals(ID)) {
                System.out.println("Request for update on ID " + ID + " with pos " + i);
                return i;
            }
        }
        return -1;
    }
}
