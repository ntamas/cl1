package uk.ac.rhul.cs.cl1.quality;

import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.cl1.NodeSet;

/**
 * Dummy quality function that returns NaN for everything.
 * 
 * @author ntamas
 */
public class DummyQualityFunction implements QualityFunction {
	public double calculate(NodeSet nodeSet) {
		return Double.NaN;
	}

	public double getAdditionAffinity(MutableNodeSet nodeSet, int index) {
		return Double.NaN;
	}

	public double getRemovalAffinity(MutableNodeSet nodeSet, int index) {
		return Double.NaN;
	}
}
