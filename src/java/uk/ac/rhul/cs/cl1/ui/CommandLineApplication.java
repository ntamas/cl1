package uk.ac.rhul.cs.cl1.ui;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import uk.ac.rhul.cs.cl1.ClusterONE;
import uk.ac.rhul.cs.cl1.ClusterONEAlgorithmParameters;
import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.io.GraphReader;
import uk.ac.rhul.cs.cl1.io.GraphReaderFactory;

/// The command line interface to Cluster ONE
public class CommandLineApplication {
	/// Options object to describe the command line options accepted by Cluster ONE
	protected Options options = null;
	
	/// Constructor of the command line entry point to Cluster ONE
	public CommandLineApplication() {
		initOptions();
	}
	
	/// Parses the command line options and then executes the main program
	public int run(String[] args) {
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		ClusterONEAlgorithmParameters params = new ClusterONEAlgorithmParameters();
		
		try {
			cmd = parser.parse(this.options, args);
			
			if (cmd.hasOption("min-size"))
				params.setMinSize(Integer.parseInt(cmd.getOptionValue("min-size")));
			if (cmd.hasOption("min-density"))
				params.setMinDensity(Double.parseDouble(cmd.getOptionValue("min-density")));
			if (cmd.hasOption("no-merge"))
				params.setMergingMethod("none");
			if (cmd.hasOption("haircut"))
				params.setHaircutThreshold(Double.parseDouble(cmd.getOptionValue("haircut")));
			if (cmd.hasOption("max-overlap"))
				params.setOverlapThreshold(Double.parseDouble(cmd.getOptionValue("max-overlap")));
			if (cmd.hasOption("seed-method"))
				params.setSeedGenerator(cmd.getOptionValue("seed-method").toString());
		} catch (ParseException ex) {
			System.err.println("Failed to parse command line options. Reason: " + ex.getMessage());
			return 1;
		} catch (InstantiationException ex) {
			System.err.println("Failed to construct seed method: "+cmd.getOptionValue("seed-method").toString());
			ex.printStackTrace();
			return 2;
		}
		
		// Check if we have an input file name or if we have the -h option
		if (cmd.getArgList().size() == 0 || cmd.hasOption('h')) {
			usage();
			return 0;
		}		
		
		// Check if we have more than one input file
		if (cmd.getArgList().size() > 1) {
			System.err.println("Only a single input file is supported");
			return 2;
		}
		
		// Process the options
		// Read the input file
		Graph graph = null;
		try {
			graph = loadGraph(cmd.getArgs()[0]);
		} catch (IOException ex) {
			System.err.println("IO error while reading input file: "+ex.getMessage());
			return 3;
		}
		System.err.println("Loaded graph with "+graph.getNodeCount()+" nodes and "+graph.getEdgeCount()+" edges");
		
		// Start the algorithm
		ClusterONE algorithm = new ClusterONE(params);
		algorithm.setTaskMonitor(new ConsoleTaskMonitor());
		algorithm.runOnGraph(graph);
		
		// Show the results
		System.err.println("Detected "+algorithm.getResults().size()+" complexes");
		for (NodeSet nodeSet: algorithm.getResults()) {
			System.out.println(nodeSet);
		}

		return 0;
	}
	
	/// Initializes the Options object that describes the command line options accepted by Cluster ONE
	@SuppressWarnings("static-access")
	protected void initOptions() {
		options = new Options();
		
		options.addOption("h", "help", false, "shows this help message");
		options.addOption(OptionBuilder.withLongOpt("min-size")
				 .withDescription("specifies the minimum size of clusters")
				 .withType(Integer.class).hasArg().create("s"));
		options.addOption(OptionBuilder.withLongOpt("min-density")
	             .withDescription("specifies the minimum density of clusters")
	             .withType(Float.class).hasArg().create("d"));
		options.addOption(OptionBuilder.withLongOpt("max-overlap")
		             .withDescription("specifies the maximum allowed overlap between two clusters")
		             .withType(Float.class).hasArg().create());
		options.addOption(OptionBuilder.withLongOpt("haircut")
	             .withDescription("specifies the haircut threshold for clusters")
	             .withType(Float.class).hasArg().create());
		options.addOption(OptionBuilder.withLongOpt("seed-method")
				 .withDescription("specifies the seed generation method to use")
				 .withType(String.class).hasArg().create("S"));
		options.addOption(OptionBuilder.withLongOpt("param")
				.withDescription("specifies the value of an advanced named parameter of the algorithm")
				.withArgName("name=value").hasArgs(2).withValueSeparator().create("p"));
		options.addOption(OptionBuilder.withLongOpt("no-merge")
				.withDescription("don't merge highly overlapping clusters")
				.create("n"));
	}

	/// Shows the usage instructions
	public void usage() {
		HelpFormatter formatter = new HelpFormatter();
		System.out.println(ClusterONE.applicationName+" "+ClusterONE.version);
		System.out.println("");
		formatter.printHelp("cl1", options, true);
	}
	
	/**
	 * Loads a graph from an input file
	 * 
	 * @param filename  name of the file to be loaded
	 */
	public Graph loadGraph(String filename) throws IOException {
		GraphReader reader = GraphReaderFactory.fromFilename(filename);
		return reader.readGraph(new FileInputStream(filename));
	}
	
	/**
	 * Starts the command line version of Cluster ONE
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CommandLineApplication app = new CommandLineApplication();
		System.exit(app.run(args));
	}

}
