package uk.ac.rhul.cs.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Various utility functions related to arrays
 * 
 * @author tamas
 */
public class ArrayUtils {
	/**
	 * Gets the ranks of elements in the given array
	 * 
	 * @param   array      the array for which we need the ranks
	 * @param   tolerance  tolerance value within which two values are considered equal
	 */
	public static double[] getRanks(double[] array, double tolerance) {
		int i, j, n = array.length;
		ArrayList<Pair<Double, Integer>> valuesAndIndices = new ArrayList<Pair<Double, Integer>>(n);
		double[] ranks = new double[n];
		
		if (n == 0)
			return ranks;
		
		for (i = 0; i < n; i++) {
			valuesAndIndices.add(Pair.create(array[i], i));
		}
		Collections.sort(valuesAndIndices, new Comparator<Pair<Double, Integer>>() {
			public int compare(Pair<Double, Integer> foo, Pair<Double, Integer> bar) {
				return foo.getLeft().compareTo(bar.getLeft());
			}
		});
		
		int sumRanks = 0;
		int dupCount = 1;
		Pair<Double, Integer> prev = valuesAndIndices.get(0);
		
		for (i = 1; i < n; i++) {
			Pair<Double, Integer> curr = valuesAndIndices.get(i);
			if (Math.abs(curr.getLeft() - prev.getLeft()) < tolerance) {
				dupCount++;
				sumRanks += i;
			} else {
				double rank = (double)sumRanks / dupCount + 1;
				for (j = i - dupCount; j < i; j++)
					ranks[valuesAndIndices.get(j).getRight()] = rank;
				dupCount = 1;
				sumRanks = i;
			}
			prev = curr;
		}
		
		double rank = (double)sumRanks / dupCount + 1;
		for (j = n - dupCount; j < n; j++)
			ranks[valuesAndIndices.get(j).getRight()] = rank;
		
		return ranks;
	}
}
