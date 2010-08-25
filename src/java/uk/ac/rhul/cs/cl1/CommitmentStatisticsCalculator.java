package uk.ac.rhul.cs.cl1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import procope.data.networks.NetworkReader;
import procope.data.networks.ProteinNetwork;
import procope.evaluation.complexquality.go.FunctionalSimilarities;
import procope.evaluation.complexquality.go.FunctionalSimilaritiesSchlicker;
import procope.evaluation.complexquality.go.GOAnnotationReader;
import procope.evaluation.complexquality.go.GOAnnotations;
import procope.evaluation.complexquality.go.GONetwork;
import procope.evaluation.complexquality.go.TermSimilarities;
import procope.evaluation.complexquality.go.TermSimilaritiesSchlicker;
import procope.evaluation.complexquality.go.FunctionalSimilaritiesSchlicker.FunctionalSimilarityMeasure;
import procope.evaluation.complexquality.go.TermSimilaritiesSchlicker.TermSimilarityMeasure;
import procope.tools.namemapping.ProteinManager;
import uk.ac.rhul.cs.graph.Graph;
import uk.ac.rhul.cs.stats.correlation.LinearCorrelation;
import uk.ac.rhul.cs.stats.datastructures.PairedData;

/**
 * Auxiliary class to calculate the commitment statistics for a set of complexes.
 * 
 * This class is not used directly in the application, it was only used to calculate
 * some results for the Cluster ONE paper. It also won't compile without the Procope
 * libraries.
 * 
 * @author tamas
 */
public class CommitmentStatisticsCalculator {
	/**
	 * The GO network that will be used by the calculator
	 */
	GONetwork net = null;
	
	/**
	 * The GO annotations
	 */
	GOAnnotations annotations = null;
	
	/**
	 * The calculated similarities between GO terms
	 */
	TermSimilarities sims = null;
	
	/**
	 * Functional similarity calculator between proteins
	 */
	FunctionalSimilarities funSims = null;
	
	/**
	 * Whether we have already added the name mappings for proteins
	 */
	boolean mappingsAdded = false;
	
	/**
	 * Initializes the calculator
	 */
	public CommitmentStatisticsCalculator() {}
	
	/**
	 * Prepares for the calculations by loading the GO tree
	 */
	protected void prepare() throws IOException {
		if (!mappingsAdded) {
			ProteinNetwork mappings = NetworkReader.readNetwork("/home/local/tamas/opt/procope/data/yeastmappings_080415.txt", true);
			ProteinManager.setCaseSensitivity(false);
			ProteinManager.addNameMappings(mappings, true);
		}
		
		if (net == null) {
			net = new GONetwork("/home/local/tamas/data/go/current/ontology/goslim_yeast.obo",
					GONetwork.Namespace.BIOLOGICAL_PROCESS,
					GONetwork.Relationships.IS_A);
		}
		
		if (annotations == null) {
			annotations = GOAnnotationReader.readAnnotations("/home/local/tamas/data/go/current/gene-associations/goslim_yeast_gene_association.sgd.txt");
		}
		
		if (sims == null) {
			sims = new TermSimilaritiesSchlicker(net, annotations, TermSimilarityMeasure.RELEVANCE, true);
		}
		
		if (funSims == null) {
			funSims = new FunctionalSimilaritiesSchlicker(net, annotations, sims, FunctionalSimilarityMeasure.COLROW_AVERAGE);
			/* alternative: COLROW_MAX */
		}
	}
	
	/**
	 * Runs the calculator on the given list of nodesets
	 */
	public double run(List<ValuedNodeSet> nodesets) throws IOException {
		double result = 0.0;
		int totalComplexSize = 0;
		
		prepare();
		
		TreeSet<String> rootTerms = new TreeSet<String>();
		rootTerms.add("GO:0008150"); // biological process
		rootTerms.add("GO:0005554"); // molecular function
		rootTerms.add("GO:0008372"); // cellular component
		
		for (NodeSet nodeset: nodesets) {
			int i, j, n = nodeset.size();
			Integer[] dummy = new Integer[1];
			Integer[] members = nodeset.getMembers().toArray(dummy);
			double[] commitments = new double[n];
			double[] similarities = new double[n];
			Graph g = nodeset.getGraph();
			
			// Check which proteins have functional annotations
			ArrayList<Integer> newMembers = new ArrayList<Integer>();
			for (int member: members) {
				int internalID = ProteinManager.getInternalID(g.getNodeName(member));
				Set<String> terms = this.annotations.getGOTerms(internalID);
				if (terms != null) {
					terms.removeAll(rootTerms);
					if (!terms.isEmpty())
						newMembers.add(member);
				}
			}
			
			// If there are less than three proteins in the set, we simply skip the complex
			members = newMembers.toArray(dummy);
			n = members.length;
			if (n < 3)
				continue;
			
			// Calculate the commitment of each node to the nodeset
			for (i = 0; i < n; i++) {
				commitments[i] = nodeset.getInternalWeight(members[i]);
			}
			
			// Calculate the average semantic similarity of each node to others in the nodeset
			for (i = 0; i < n; i++) {
				double sim = 0.0;
				String nodeName = g.getNodeName(members[i]);
				int k = 0;
				
				for (j = 0; j < n; j++) {
					if (i == j)
						continue;
					sim += this.funSims.getScore(nodeName, g.getNodeName(members[j]));
					k++;
				}
				similarities[i] = sim / k;
			}
			
			// Construct a PairedData
			PairedData data = new PairedData(commitments, similarities);
			double corrCoeff = LinearCorrelation.correlationCoeff(data);
			if (!Double.isNaN(corrCoeff)) {
				result += n * corrCoeff;
				totalComplexSize += n;
			}
		}
		
		return result / totalComplexSize;
	}
}
