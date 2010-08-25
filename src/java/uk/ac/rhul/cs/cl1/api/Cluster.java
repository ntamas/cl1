package uk.ac.rhul.cs.cl1.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;

import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.QualityFunction;

/**
 * A single cluster in the single run of the Cluster ONE algorithm.
 * 
 * Theoretically, this class would belong to the main package and not
 * the <tt>api</tt> subpackage. However, this class needs some annotations
 * from JAXB-2.0 to facilitate JSON marshalling in the API output, and we
 * don't want to include dependency on JAXB in the main package.
 *
 * This class is essentially a list of strings.
 * @author tamas
 */
public class Cluster implements Serializable {
	/** The list of nodes in this cluster */
	List<String> nodes = new ArrayList<String>();
	
	/** The density of the cluster */
	@XmlElement Double density = null;
	
	/** The internal weight of the cluster */
	@XmlElement Double inWeight = null;
	
	/** The boundary weight of the cluster */
	@XmlElement Double outWeight = null;
	
	/** The value of the goal function for this cluster */
	@XmlElement Double quality = null;
	
	public Cluster() {
	}
	
	/**
	 * Creates a cluster containing the given nodes
	 */
	public Cluster(List<String> nodes) {
		this();
		this.nodes.addAll(nodes);
	}
	
	/**
	 * Creates a cluster containing the given nodes
	 */
	public Cluster(String[] nodes) {
		this(Arrays.asList(nodes));
	}
	
	/**
	 * Returns the nodes in this cluster as an array.
	 * 
	 * This is required because JAXB cannot marshal lists.
	 */
	@XmlElement(name="members")
	public String[] getNodeArray() {
		String[] dummy = {};
		return nodes.toArray(dummy);
	}
	
	/**
	 * Constructs a cluster from a {@link NodeSet}.
	 * 
	 * This method essentially serves as a bridge between the output of
	 * {@link ClusterONE} (which is a list of {@link NodeSet}s) and the
	 * REST API package.
	 * 
	 * @param nodeSet      the {@link NodeSet} to be converted to a
	 *                     {@link Cluster}.
	 * @param qualityFunc  the quality function to be used for calculating
	 *                     the quality of the cluster.
	 *                     
	 * @return the newly constructed {@link Cluster} object.
	 */
	public static Cluster fromNodeSet(NodeSet nodeSet, QualityFunction qualityFunc) {
		Cluster cluster = new Cluster(nodeSet.getMemberNames());
		
		cluster.density = nodeSet.getDensity();
		cluster.inWeight = nodeSet.getTotalInternalEdgeWeight();
		cluster.outWeight = nodeSet.getTotalBoundaryEdgeWeight();
		
		if (qualityFunc != null)
			cluster.quality = qualityFunc.calculate(nodeSet);
		else
			cluster.quality = 0.0;
		
		return cluster;
	}
}
