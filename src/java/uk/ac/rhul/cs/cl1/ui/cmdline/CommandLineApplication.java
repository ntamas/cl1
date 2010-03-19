package uk.ac.rhul.cs.cl1.ui.cmdline;

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
// import uk.ac.rhul.cs.cl1.CommitmentStatisticsCalculator;
import uk.ac.rhul.cs.cl1.Graph;
import uk.ac.rhul.cs.cl1.NodeSet;
import uk.ac.rhul.cs.cl1.io.GraphReader;
import uk.ac.rhul.cs.cl1.io.GraphReaderFactory;
import uk.ac.rhul.cs.cl1.io.GraphReaderFactory.Format;
import uk.ac.rhul.cs.cl1.ui.ConsoleTaskMonitor;

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
		String formatSpec = null;
		
		try {
			cmd = parser.parse(this.options, args);
			
			if (cmd.hasOption("input-format"))
				formatSpec = cmd.getOptionValue("input-format");
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
		GraphReaderFactory.Format format = null;
		
		if (formatSpec != null)
			try {
				format = GraphReaderFactory.Format.valueOf(formatSpec.toUpperCase());
			} catch (IllegalArgumentException ex) {
				System.err.println("Unknown input file format: "+formatSpec);
				return 4;
			}

		try {
			graph = loadGraph(cmd.getArgs()[0], format);
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
		/* if (cmd.hasOption("commitment-stats")) {
			CommitmentStatisticsCalculator calc = new CommitmentStatisticsCalculator();
			try {
				System.out.println(calc.run(algorithm.getResults()));
			} catch (IOException ex) {
				ex.printStackTrace();
				return 4;
			}
			return 0;
		} */
		
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
		
		/* help option */
		options.addOption("h", "help", false, "shows this help message");
		
		/* input format override option */
		options.addOption(OptionBuilder.withLongOpt("input-format")
				.withDescription("specifies the format of the input file (sif or edge_list)")
				.withType(String.class).hasArg().create("f"));
		
		/* minimum size option */
		options.addOption(OptionBuilder.withLongOpt("min-size")
				 .withDescription("specifies the minimum size of clusters")
				 .withType(Integer.class).hasArg().create("s"));
		
		/* minimum density option */
		options.addOption(OptionBuilder.withLongOpt("min-density")
	             .withDescription("specifies the minimum density of clusters")
	             .withType(Float.class).hasArg().create("d"));
		
		/* maximum overlap option (advanced) */
		options.addOption(OptionBuilder.withLongOpt("max-overlap")
		             .withDescription("specifies the maximum allowed overlap between two clusters")
		             .withType(Float.class).hasArg().create());
		
		/* haircut threshold option (advanced) */
		options.addOption(OptionBuilder.withLongOpt("haircut")
	             .withDescription("specifies the haircut threshold for clusters")
	             .withType(Float.class).hasArg().create());
		
		/* seeding method option (advanced) */
		options.addOption(OptionBuilder.withLongOpt("seed-method")
				 .withDescription("specifies the seed generation method to use")
				 .withType(String.class).hasArg().create("S"));
		
		/* any other parameter (advanced) */
		options.addOption(OptionBuilder.withLongOpt("param")
				.withDescription("specifies the value of an advanced named parameter of the algorithm")
				.withArgName("name=value").hasArgs(2).withValueSeparator().create("p"));
		
		/* skip the merging phase (useful for debugging only) */
		options.addOption(OptionBuilder.withLongOpt("no-merge")
				.withDescription("don't merge highly overlapping clusters")
				.create("n"));
		
		/* options.addOption(OptionBuilder.withLongOpt("commitment-stats")
				.withDescription("suppress regular output and calculate commitment statistics instead")
				.create()); */
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
	 * @param format    the format of the file, null means autodetection based on extension
	 */
	public Graph loadGraph(String filename, Format format) throws IOException {
		GraphReader reader;
		
		if (format == null) {
			if ("-".equals(filename)) {
				reader = GraphReaderFactory.fromFormat(Format.EDGE_LIST);
			} else {
				reader = GraphReaderFactory.fromFilename(filename);
			}
		} else {
			reader = GraphReaderFactory.fromFormat(format);
		}
		
		if ("-".equals(filename))
			return reader.readGraph(System.in);
		
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
