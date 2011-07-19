package uk.ac.rhul.cs.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Various utility functions related to arrays
 * 
 * @author tamas
 */
public class ArrayUtils {
	/**
	 * Returns the median of the elements in the given array.
	 * 
	 * @param   array      the array for which we need the median
	 * @return  the median or null if the array is empty
	 */
	public static Double getMedian(double[] array) {
		if (array.length == 0)
			return null;
		
		double[] copy = array.clone();
		int midpoint = copy.length / 2;
		Arrays.sort(copy);
		
		if (copy.length % 2 == 0)
			return (copy[midpoint-1] + copy[midpoint]) / 2;
		
		return copy[midpoint];
	}
	
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
	
	/**
	 * Returns the minimum element of an array
	 */
	public static double min(double[] array) {
		if (array.length == 0)
			throw new IllegalArgumentException("array must not be empty");
		
		double result = array[0];
		int i, n = array.length;
		
		for (i = 0; i < n; i++)
			if (array[i] < result)
				result = array[i];
		
		return result;
	}
	
	/**
	 * Returns the maximum element of an array
	 */
	public static double max(double[] array) {
		if (array.length == 0)
			throw new IllegalArgumentException("array must not be empty");
		
		double result = array[0];
		int i, n = array.length;
		
		for (i = 0; i < n; i++)
			if (array[i] > result)
				result = array[i];
		
		return result;
	}
}
