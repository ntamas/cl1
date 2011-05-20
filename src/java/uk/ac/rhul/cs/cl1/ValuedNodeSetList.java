package uk.ac.rhul.cs.cl1;

import java.util.ArrayList;

/**
 * A list of {@link ValuedNodeSet} objects, typically used as a result object in ClusterONE.
 * 
 * This object is practically an ArrayList of {@link ValuedNodeSet} objects with some
 * extra methods that allow ClusterONE to clean up the list by merging highly overlapping
 * nodesets.
 * 
 * @author ntamas
 */
public class ValuedNodeSetList extends ArrayList<ValuedNodeSet> {
}
