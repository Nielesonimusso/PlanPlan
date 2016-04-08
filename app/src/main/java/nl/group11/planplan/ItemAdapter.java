package nl.group11.planplan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

/**
 * Created by s132054 on 25-3-2016.
 *
 * generic extenstion of RecyclerView.Adapter, adding some features for viewing Items
 */
public abstract class ItemAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    ImageCache imageCache;
    Context context;

    public ItemAdapter(Context context) {
        imageCache = ImageCache.getInstance();
        this.context = context;
    }

    /**
     * get position of item in adapter, based in Item ID
     * @param ID ID to query
     * @return position of item with id {@code ID} in adapter
     */
    abstract int posOfID(String ID);

}
