package uk.ac.rhul.cs.cl1.ui.cmdline;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import uk.ac.rhul.cs.cl1.*;
import uk.ac.rhul.cs.cl1.io.ClusteringWriter;
import uk.ac.rhul.cs.cl1.io.CSVClusteringWriter;
import uk.ac.rhul.cs.cl1.io.ClusteringWriterFactory;
import uk.ac.rhul.cs.cl1.io.GraphReader;
import uk.ac.rhul.cs.cl1.io.GraphReaderFactory;
import uk.ac.rhul.cs.cl1.io.GraphReaderFactory.Format;
import uk.ac.rhul.cs.cl1.ui.ConsoleTaskMonitor;
import uk.ac.rhul.cs.graph.Graph;

/// The command line interface to ClusterONE
public class CommandLineApplication {
	/// Options object to describe the command line options accepted by ClusterONE
	protected Options options = null;
	
	/// Whether we are running in debug mode
	protected boolean debugMode = false;

	/// Whether we are running in profiling mode
	protected boolean profilingMode = false;

	/// Task monitor that shows the progress of the algorithm on the console
	protected TaskMonitor taskMonitor = new ConsoleTaskMonitor();

	/// Constructor of the command line entry point to ClusterONE
	public CommandLineApplication() {
		initOptions();
	}
	
	/// Parses the command line options and then executes the main program
	public int run(String[] args) {
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		ClusterONEAlgorithmParameters params = new ClusterONEAlgorithmParameters();
		String inputFormatSpec = null;
		String outputFormatSpec = null;
		GraphReaderFactory.Format inputFormat = null;
		ClusteringWriterFactory.Format outputFormat;
		ClusteringWriter outputWriter;

		params.setRejectSeedsWithOnlyUsedNodes(true);

		try {
			cmd = parser.parse(this.options, args);
			
			if (cmd.hasOption("version")) {
				showVersion();
				return 0;
			}
			
			if (cmd.hasOption("debug"))
				debugMode = true;
			if (cmd.hasOption("profile"))
				profilingMode = true;
			if (cmd.hasOption("fluff"))
				params.setFluffClusters(true);
			if (cmd.hasOption("keep-initial-seeds"))
				params.setKeepInitialSeeds(true);
			if (cmd.hasOption("haircut"))
				params.setHaircutThreshold(Double.parseDouble(cmd.getOptionValue("haircut")));
			if (cmd.hasOption("k-core"))
				params.setKCoreThreshold(Integer.parseInt(cmd.getOptionValue("k-core")));
			if (cmd.hasOption("input-format"))
				inputFormatSpec = cmd.getOptionValue("input-format");
			if (cmd.hasOption("max-overlap"))
				params.setOverlapThreshold(Double.parseDouble(cmd.getOptionValue("max-overlap")));
			if (cmd.hasOption("merge-method"))
				params.setMergingMethodName(cmd.getOptionValue("merge-method"));
			if (cmd.hasOption("min-density")) {
				String value = cmd.getOptionValue("min-density");
				if (value == null || value.equalsIgnoreCase("auto"))
					params.setMinDensity(null);
				else
					params.setMinDensity(Double.parseDouble(value));
			}
			if (cmd.hasOption("min-size"))
				params.setMinSize(Integer.parseInt(cmd.getOptionValue("min-size")));
			if (cmd.hasOption("no-fluff"))
				params.setFluffClusters(false);
			if (cmd.hasOption("no-keep-initial-seeds"))
				params.setKeepInitialSeeds(false);
			if (cmd.hasOption("no-merge"))
				params.setMergingMethodName("none");
			if (cmd.hasOption("output-format"))
				outputFormatSpec = cmd.getOptionValue("output-format");
			if (cmd.hasOption("penalty"))
				params.setNodePenalty(Double.parseDouble(cmd.getOptionValue("penalty")));
			if (cmd.hasOption("seed-method")) {
				// Handle legacy unused_nodes specification here because setSeedGenerator()
				// does not understand it any more.
				// TODO: show deprecation warning
				if ("unused_nodes".equals(cmd.getOptionValue("seed-method"))) {
					params.setSeedGenerator("nodes");
					params.setRejectSeedsWithOnlyUsedNodes(true);
				} else {
					params.setSeedGenerator(cmd.getOptionValue("seed-method"));
					params.setRejectSeedsWithOnlyUsedNodes(false);
				}
			}
			if (cmd.hasOption("similarity"))
				params.setSimilarityFunction(cmd.getOptionValue("similarity"));
			if (cmd.hasOption("num-threads")) {
				String numThreadsValue = cmd.getOptionValue("num-threads");
				if ("auto".equals(numThreadsValue)) {
					params.setNumThreads(0);
				} else {
					params.setNumThreads(Integer.parseInt(numThreadsValue));
				}
			}
		} catch (ParseException ex) {
			System.err.println("Failed to parse command line options. Reason: " + ex.getMessage());
			return 1;
		} catch (InstantiationException ex) {
			System.err.println("Failed to interpret string: "+cmd.getOptionValue("seed-method"));
			ex.printStackTrace();
			return 1;
		}
		
		// Check if we have an input file name or if we have the -h option
		if (cmd.getArgList().size() == 0 || cmd.hasOption('h')) {
			showUsage();
			return 0;
		}		
		
		// Check if we have more than one input file
		if (cmd.getArgList().size() > 1) {
			System.err.println("Only a single input file is supported");
			return 1;
		}
		
		// Process the options
		if (inputFormatSpec != null)
			try {
				inputFormat = GraphReaderFactory.Format.valueOf(inputFormatSpec.toUpperCase());
			} catch (IllegalArgumentException ex) {
				System.err.println("Unknown input file format: "+inputFormatSpec);
				return 1;
			}
		
		if (outputFormatSpec != null) {
			try {
				outputFormat = ClusteringWriterFactory.Format.valueOf(outputFormatSpec.toUpperCase());
			} catch (IllegalArgumentException ex) {
				System.err.println("Unknown output file format: "+outputFormatSpec);
				return 1;
			}
		} else {
			outputFormat = ClusteringWriterFactory.Format.PLAIN;
		}
		
		outputWriter = ClusteringWriterFactory.fromFormat(outputFormat);
		if (outputFormat == ClusteringWriterFactory.Format.CSV) {
			((CSVClusteringWriter)outputWriter).setQualityFunction(
					params.getQualityFunction()
			);
		}

		// Pause if profiling
		pauseDuringProfiling("Press Enter to start reading the input file...");

		// Read the input file
		Graph graph;
		long startTime = System.currentTimeMillis();
		try {
			graph = loadGraph(cmd.getArgs()[0], inputFormat);
		} catch (IOException ex) {
			System.err.println("IO error while reading input file: "+ex.getMessage());
			return 1;
		}
		System.err.println("Loaded graph with "+graph.getNodeCount()+" nodes and "+graph.getEdgeCount()+" edges");
		if (profilingMode) {
			System.err.println("Loading took " + (System.currentTimeMillis() - startTime) + " ms");
		}

		// Pause if profiling
		pauseDuringProfiling("Press Enter to start the algorithm...");

		// Start the algorithm
		ClusterONE algorithm = new ClusterONE(params);
		algorithm.setDebugMode(debugMode);
		algorithm.setTaskMonitor(taskMonitor);
		try {
			algorithm.runOnGraph(graph);
		} catch (ClusterONEException ex) {
			System.err.println("Error while executing the clustering algorithm: ");
			System.err.println(ex.getMessage());
			return 1;
		}
		
		System.err.println("Detected "+algorithm.getResults().size()+" complexes");
		
		try {
			outputWriter.writeClustering(algorithm.getResults(), System.out);
		} catch (IOException ex) {
			System.err.println("IO error while printing the results: ");
			System.err.println(ex.getMessage());
			return 1;
		}
		
		return 0;
	}
	
	/// Initializes the Options object that describes the command line options accepted by ClusterONE
	@SuppressWarnings("static-access")
	protected void initOptions() {
		options = new Options();
		
		/* help option */
		options.addOption("h", "help", false, "shows this help message");
		
		/* version option */
		options.addOption("v", "version", false, "shows the version number");
		
		/* input format override option */
		options.addOption(OptionBuilder.withLongOpt("input-format")
				.withDescription("specifies the format of the input file (sif or edge_list)")
				.withType(String.class).hasArg().create("f"));
		
		/* output format override option */
		options.addOption(OptionBuilder.withLongOpt("output-format")
				.withDescription("specifies the format of the output file (plain, genepro or csv)")
				.withType(String.class).hasArg().create("F"));
		
		/* minimum size option */
		options.addOption(OptionBuilder.withLongOpt("min-size")
				     .withDescription("specifies the minimum size of clusters")
				     .withType(Integer.class).hasArg().create("s"));
		
		/* minimum density option */
		options.addOption(OptionBuilder.withLongOpt("min-density")
	                .withDescription("specifies the minimum density of clusters (default: auto)")
	                .withType(Float.class).hasArg().create("d"));
		
		/* maximum overlap option (advanced) */
		options.addOption(OptionBuilder.withLongOpt("max-overlap")
		             .withDescription("specifies the maximum allowed overlap between two clusters")
		             .withType(Float.class).hasArg().create());
		
		/* haircut threshold option (advanced) */
		options.addOption(OptionBuilder.withLongOpt("haircut")
	             .withDescription("specifies the haircut threshold for clusters")
	             .withType(Float.class).hasArg().create());
		
		/* penalty scores of nodes (advanced) */
		options.addOption(OptionBuilder.withLongOpt("penalty")
				.withDescription("set the node penalty value")
				.withType(Float.class).hasArg().create());
		
		/* k-core threshold (advanced) */
		options.addOption(OptionBuilder.withLongOpt("k-core")
		             .withDescription("specifies the minimum k-core index of clusters")
		             .withType(Integer.class).hasArg().create());
		
		/* fluffing option (advanced) */
		OptionGroup fluffGroup = new OptionGroup();
		fluffGroup.addOption(OptionBuilder.withLongOpt("fluff")
				  .withDescription("fluffs the clusters")
				  .withType(Boolean.class).create());
		fluffGroup.addOption(OptionBuilder.withLongOpt("no-fluff")
				  .withDescription("don't fluff the clusters (default)")
				  .withType(Boolean.class).create());
		options.addOptionGroup(fluffGroup);
		
		/* keep initial seeds option (advanced) */
		OptionGroup keepInitialSeedsGroup = new OptionGroup();
		keepInitialSeedsGroup.addOption(OptionBuilder.withLongOpt("keep-initial-seeds")
				  .withDescription("always keep the initial seed nodes in the cluster")
				  .withType(Boolean.class).create());
		keepInitialSeedsGroup.addOption(OptionBuilder.withLongOpt("no-keep-initial-seeds")
				  .withDescription("allow the initial seed nodes to leave the cluster if needed (default)")
				  .withType(Boolean.class).create());
		options.addOptionGroup(keepInitialSeedsGroup);
		
		/* merging method option (advanced) */
		options.addOption(OptionBuilder.withLongOpt("merge-method")
				 .withDescription("specifies the cluster merging method to use (single or multi)")
				 .withType(String.class).hasArg().create());
		
		/* seeding method option (advanced) */
		options.addOption(OptionBuilder.withLongOpt("seed-method")
				.withDescription("specifies the seed generation method to use")
				.withType(String.class).hasArg().create());
		
		/* similarity function option (advanced) */
		options.addOption(OptionBuilder.withLongOpt("similarity")
				 .withDescription("specifies the similarity function to use (match, simpson, jaccard or dice)")
				 .withType(String.class).hasArg().create());
		
		/* any other parameter (advanced) */
		/* options.addOption(OptionBuilder.withLongOpt("param")
				.withDescription("specifies the value of an advanced named parameter of the algorithm")
				.withArgName("name=value").hasArgs(2).withValueSeparator().create("p")); */
		
		/* debug mode option (advanced) */
		options.addOption(OptionBuilder.withLongOpt("debug")
				.withDescription("turns on the debug mode").withType(Boolean.class).create());
		
		/* profiling mode option (advanced) */
		options.addOption(OptionBuilder.withLongOpt("profile")
				.withDescription("turns on the profiling mode").withType(Boolean.class).create());

		/* number of threads (advanced) */
		options.addOption(OptionBuilder.withLongOpt("num-threads")
				.withDescription("specifies the number of threads to use during the growth process (default=auto)")
				.withType(String.class).hasArg().create());

		/* skip the merging phase (useful for debugging only) */
		options.addOption(OptionBuilder.withLongOpt("no-merge")
				.withDescription("don't merge highly overlapping clusters")
				.create());
	}

	/// Shows the usage instructions
	public void showUsage() {
		HelpFormatter formatter = new HelpFormatter();
		showVersion();
		System.out.println("");
		formatter.printHelp("cl1", options, true);
	}
	
	/**
	 * Shows the version information
	 */
	public void showVersion() {
		System.out.println(ClusterONE.applicationName+" "+ClusterONE.version);
	}
	
	/**
	 * Loads a graph from an input file
	 * 
	 * @param filename  name of the file to be loaded
	 * @param format    the format of the file, null means autodetection based on extension
	 */
	public Graph loadGraph(String filename, Format format) throws IOException {
		GraphReader reader;
		InputStream stream;
		
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
			stream = System.in;
		else
			stream = new FileInputStream(filename);

		if (reader instanceof TaskMonitorSupport) {
			((TaskMonitorSupport)reader).setTaskMonitor(taskMonitor);
		}

		return reader.readGraph(new InputStreamReader(stream, "utf-8"));
	}

	/**
	 * Prints a message and waits for the Enter key to be pressed.
	 *
	 * @param message  the message to print
	 */
	public void pauseDuringProfiling(String message) {
		if (!profilingMode)
			return;

		System.gc();
		printHeapStatistics();
		System.err.println(message);
		try {
			System.in.read();
		} catch (IOException ex) {
			System.err.println("IOException while waiting for the Enter key.");
		}
	}

	/**
	 * Prints some basic statistics of the heap usage.
	 */
	public void printHeapStatistics() {
		long usedHeapSpace = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long maxHeapSpace = Runtime.getRuntime().maxMemory();

		System.err.println("Heap usage: " +
				usedHeapSpace + " bytes used out of " +
				maxHeapSpace + " bytes; " +
				Math.round(usedHeapSpace * 10000 / maxHeapSpace) / 100 + "% full"
		);
	}

	/**
	 * Starts the command line version of ClusterONE
	 * 
	 * @param args  the command line arguments
	 */
	public static void main(String[] args) {
		CommandLineApplication app = new CommandLineApplication();
		System.exit(app.run(args));
	}

}
