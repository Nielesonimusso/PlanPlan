package nl.group11.planplan;

import android.support.v7.widget.RecyclerView;

/**
 * Created by s132054 on 25-3-2016.
 */
public abstract class ItemAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    abstract int posOfID(String ID);

}
