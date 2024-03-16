package cc.valdemar.foz.fozquests.utils;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class MapUtil {
    public static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<>(
                (e1, e2) -> {
                    int res = e2.getValue().compareTo(e1.getValue());
                    return res != 0 ? res : 1; // Special fix to preserve items with equal values
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
