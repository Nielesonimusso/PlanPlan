package nl.group11.planplan;

import java.util.Comparator;

/**
 * Created by s140442 on 22/03/2016.
 */
public class ItemComparator implements Comparator<Item> {
    @Override
    public int compare(Item lhs, Item rhs) {
        return (lhs).compareTo(rhs);
    }
}
