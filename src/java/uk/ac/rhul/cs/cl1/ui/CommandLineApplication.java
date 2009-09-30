package uk.ac.rhul.cs.cl1.ui;

import java.io.IOException;

import giny.model.RootGraph;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import uk.ac.rhul.cs.cl1.ClusterONE;

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
		
		try {
			cmd = parser.parse(this.options, args);
		} catch (ParseException ex) {
			System.err.println("Failed to parse command line options. Reason: " + ex.getMessage());
			return 1;
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
		
		// Read the input file
		RootGraph graph = null;
		try {
			graph = loadGraph(cmd.getArgs()[0]);
		} catch (IOException ex) {
			System.err.println("IO error while reading input file: "+ex.getMessage());
			return 3;
		}
		System.out.println("Loaded graph with "+graph.getNodeCount()+" nodes and "+graph.getEdgeCount()+" edges");
		
		return 0;
	}
	
	/// Initializes the Options object that describes the command line options accepted by Cluster ONE
	protected void initOptions() {
		options = new Options();
		
		options.addOption("h", "help", false, "shows this help message");
		options.addOption("p", "param", true, "specifies the value of a named parameter of the algorithm");
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
	public RootGraph loadGraph(String filename) throws IOException {
		return null;
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
