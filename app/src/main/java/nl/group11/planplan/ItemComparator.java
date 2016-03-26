package nl.group11.planplan;

import java.util.Comparator;

/**
 * Created by s140442 on 22/03/2016.
 */
public class ItemComparator implements Comparator<Item> {

    /**
     * compares two items
     *
     * @param lhs first item
     * @param rhs second item
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compare(Item lhs, Item rhs) {
        if (lhs == null && rhs == null) {
            return 0;
        } else if (lhs == null) {
            return -1;
        } else if (rhs == null) {
            return 1;
        } else {
            return (lhs).compareTo(rhs);
        }
    }
}
