package feature.smartseed.example.empirical.constructor;

import java.util.Comparator;

public class ComparableComparator<E extends Comparable> implements Comparator<E>{

    public int compare(E o1, E o2) {
        return o1.compareTo(o2);
    }
}
