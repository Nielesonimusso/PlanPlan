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
        return (lhs).compareTo(rhs);
    }
}
