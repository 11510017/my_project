package com.example.picart;

import java.util.Comparator;
import java.util.Map;

/**
 * Compare a value in a hash map so that the order of two hash map can be determined
 */
class MapComparator implements Comparator<Map<String, String>> {
    private final String key;
    private final String order;

    /**
     * Constructor, initialize comparator by given the key and order
     *
     * @param key   Specify which key to be compared
     * @param order Specify the order, either dsc (for descend) or asc (for ascend)
     */
    public MapComparator(String key, String order) {
        this.key = key;
        this.order = order;
    }

    /**
     * Hash map compare method
     * @param first One hash map to be compared
     * @param second Another hash map to be compared
     * @return Return value is determined by the given order
     */
    public int compare(Map<String, String> first,
                       Map<String, String> second) {
        // TODO: Null checking, both for maps and values
        String firstValue = first.get(key);
        String secondValue = second.get(key);
        if (this.order.equalsIgnoreCase("asc")) {
            return firstValue.compareTo(secondValue);
        } else {
            return secondValue.compareTo(firstValue);
        }

    }
}
