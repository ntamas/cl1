package uk.ac.rhul.cs.cl1;

/**
 * Abstract seed nodeset generator class.
 * 
 * A seed nodeset generator is an abstract algorithm that produces a set of candidate
 * seed nodesets from a graph. These candidate nodesets will be passed on to a
 * {@link ClusterGrowthProcess} during a {@link ClusterONEAlgorithm} to produce
 * the clusters.
 * 
 * Seed nodeset generators implement the {@link Iterable} interface, so the easiest
 * way to get the set of seed nodesets is to iterate over it in a for loop.
 *  
 * @author tamas
 */
public abstract class SeedGenerator extends GraphAlgorithm implements Iterable<MutableNodeSet> {
	/**
	 * Constructs a seed generator that is not associated to any graph yet.
	 */
	public SeedGenerator() {
		this(null);
	}

	/**
	 * Constructs a seed generator that is associated to a given graph.
	 * @param graph   the graph the seed generator will operate on
	 */
	public SeedGenerator(Graph graph) {
		super(graph);
	}
	
	/**
	 * Factory method that can construct seed generators from a simple string description.
	 * The following specifiers are recognised at the moment:
	 * <ul>
	 * <li><tt>nodes</tt> - generates a singleton seed for each node of the graph</li>
	 * <li><tt>edges</tt> - generates a seed containing the two endpoints for each edge of the graph</li>
	 */
	public static SeedGenerator fromString(String specification) {
		if (specification.equals("nodes"))
			return null;
		
		if (specification.equals("edges"))
			return null;
		
		return null;
	}
}
