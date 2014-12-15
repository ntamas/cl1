package uk.ac.rhul.cs.utils;

import java.util.Iterator;

/**
 * Various utility functions related to iterators.
 *
 * @author ntamas
 */
public class IteratorUtils {
    /**
     * Exhausts an iterator.
     */
    public static void exhaust(Iterator<?> iterator) {
        while (iterator.hasNext()) {
            iterator.next();
        }
    }
}
