package uk.ac.rhul.cs.cl1;

import procope.data.complexes.ComplexSet;
import procope.data.networks.ProteinNetwork;
import procope.methods.clustering.Clusterer;

/**
 * ProCope plugin version of Cluster ONE.
 * 
 * @author tamas
 */
public class ProcopePlugin implements Clusterer {
	@Override
	public ComplexSet cluster(ProteinNetwork net) {
		ComplexSet result = new ComplexSet();
		return result;
	}
}
