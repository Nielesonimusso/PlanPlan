package nl.group11.planplan;

import android.support.v7.widget.RecyclerView;

/**
 * Created by s132054 on 30-3-2016.
 *
 * Interface implemented by all fragments which contain a RecyclerView displaying Item instances
 *
 */
public interface AdapterFragment {
    /**
     * Getter-method
     *
     * @return the ItemAdapter which is used to display the Item instances in the implementing fragments
     */
    ItemAdapter getAdapter();
}
