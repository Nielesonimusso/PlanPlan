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
import java.util.Collections;
import java.util.List;

/**
 * Created by s132054 on 25-3-2016.
 */
public class MixedFirebaseAdapter extends ItemAdapter<RecyclerView.ViewHolder> implements APIHandler.Callback<ArrayList<Item>>, ValueEventListener {

    List<Item> cache;
    ImageCache imageCache;
    Firebase endpoint;
    Context context;

    MixedFirebaseAdapter(Context context) {
        cache = new ArrayList<>();
        this.context = context;
        imageCache = ImageCache.getInstance();
        endpoint = new Firebase("https://planplan.firebaseio.com/");
        endpoint.child(APIHandler.getAccount(context)).child("planning").addValueEventListener(this);
        APIHandler.getDatabasePlanning(endpoint, context, this);
    }

    @Override
    public int getItemViewType(int position) {
        Type t;
        if (position >= cache.size()) {
            t = Type.EVENT;
        } else {
            Item item = cache.get(position);
            t = (Type) item.getType();
        }
        return t.ordinal();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView;
        if (viewType == Type.EVENT.ordinal()) {
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
            item = new EventItem(context, null);
        } else {
            item = cache.get(position);
        }
        item.buildView(holder, imageCache, true);
    }

    @Override
    public int getItemCount() {
        return cache.size();
    }

    @Override
    public void onItem(ArrayList<Item> result) {
        Collections.sort(result, new ItemComparator());
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
        Collections.sort(templist, new ItemComparator());
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
