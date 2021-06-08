package feature.smartseed.example.empirical.constructor;
import java.util.*;
public class CollectionUtil {
    public static <T> Set<T> toSet(T ... elements) {
        HashSet<T> set = new HashSet<T>();
        if (elements != null)
	        for (T element : elements)
	            set.add(element);
        return set;
    }
    public static <K, V> Map<K, V> buildMap(K key, V value) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(key, value);
        return map;
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map buildMap(Object ... keyValuePairs) {
        Map map = new HashMap();
        if (keyValuePairs.length % 2 != 0)
            throw new IllegalArgumentException("Invalid numer of arguments. " +
                    "It must be even to represent key-value-pairs");
        for (int i = 0; i < keyValuePairs.length; i += 2)
            map.put(keyValuePairs[i], keyValuePairs[i + 1]);
        return map;
    }
}
