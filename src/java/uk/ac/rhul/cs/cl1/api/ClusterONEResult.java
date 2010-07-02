package uk.ac.rhul.cs.cl1.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;
import uk.ac.rhul.cs.cl1.ValuedNodeSet;

/**
 * The results of a single run of the Cluster ONE algorithm on a single
 * graph.
 * 
 * Theoretically, this class would belong to the main package and not
 * the <tt>api</tt> subpackage. However, this class needs some annotations
 * from JAXB-2.0 to facilitate JSON marshalling in the API output, and we
 * don't want to include dependency on JAXB in the main package.
 *
 * A Cluster ONE result object contains the following elements:
 * 
 * - A list of {@link Cluster} objects. Each cluster is essentially a list
 *   of strings with some associated metadata.
 *  
 * @author tamas
 */
@XmlRootElement
public class ClusterONEResult implements Serializable {
	private List<Cluster> clusters = new ArrayList<Cluster>();
	private ClusterONEAlgorithmParameters parameters = null;
	
	public ClusterONEResult() {}
	
	public ClusterONEResult(List<Cluster> clusters) {
		this.clusters.addAll(clusters);
	}
	
	/**
	 * Returns the list of clusters as an array.
	 * 
	 * This is necessary because JAXB cannot deal with lists, but it can
	 * deal with arrays.
	 * 
	 * @return  the list of clusters as an array.
	 */
	@XmlElement(name="clusters", required=true)
	public Cluster[] getClusterArray() {
		Cluster[] dummy = {};
		return clusters.toArray(dummy);
	}
	
	/**
	 * Constructs a result object from a list of {@link ValuedNodeSet} objects.
	 * 
	 * This method essentially serves as a bridge between the output of
	 * {@link ClusterONE} (which is a {@link ValuedNodeSetList}) and the
	 * REST API package.
	 * 
	 * @param nodeSetList  a list of {@link ValuedNodeSet} objects that are to
	 *                     be converted to a {@link ClusterONEResult}.
	 * @return the newly constructed {@link ClusterONEResult} object.
	 */
	public static ClusterONEResult fromNodeSetList(List<ValuedNodeSet> nodeSetList) {
		ClusterONEResult result = new ClusterONEResult();
		
		for (ValuedNodeSet nodeSet: nodeSetList) {
			result.clusters.add(Cluster.fromNodeSet(nodeSet));
		}
		
		return result;
	}

	/**
	 * Returns the parameter settings of Cluster ONE that were used to
	 * generate this results.
	 * 
	 * @return the parameter settings or null if not specified
	 */
	@XmlElement(name="parameters", required=true)
	public ClusterONEAlgorithmParameters getParameters() {
		return parameters;
	}

	/**
	 * Sets the parameter settings of Cluster ONE that were used to
	 * generate this result.
	 *
	 * @param parameters the parameter settings.
	 */
	public void setParameters(ClusterONEAlgorithmParameters parameters) {
		this.parameters = parameters;
	}
}
