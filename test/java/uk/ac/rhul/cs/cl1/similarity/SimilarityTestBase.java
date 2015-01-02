package uk.ac.rhul.cs.cl1.similarity;

import uk.ac.rhul.cs.cl1.Intersectable;
import uk.ac.rhul.cs.cl1.Sized;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Superclass for similarity tests.
 * 
 * @author tamas
 */
public class SimilarityTestBase {
	class Set extends HashSet<Integer> implements Sized, Intersectable<Set> {
		public Set() {
			super();
		}
		
		public Set(Integer... nums) {
			super();
			this.addAll(Arrays.asList(nums));
		}
		
		public int getIntersectionSizeWith(Set other) {
			return getIntersectionWith(other).size();
		}

		public Set getIntersectionWith(Set other) {
			Set result = new Set();
			if (this.size() > other.size())
				return other.getIntersectionWith(this);
			result.addAll(this);
			result.retainAll(other);
			return result;
		}
	}
}
