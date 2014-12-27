package uk.ac.rhul.cs.cl1.seeding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import com.sosnoski.util.hashmap.StringIntHashMap;

import uk.ac.rhul.cs.cl1.MutableNodeSet;
import uk.ac.rhul.cs.graph.Graph;

/**
 * Seed generator where seeds will be generated from an input stream.
 * 
 * Each line in the stream must contain node names separated by spaces. Unknown
 * nodes will silently be ignored.
 * 
 * @see {@link FileBasedSeedGenerator} for reading seeds from a file
 * @author tamas
 */
public class StreamBasedSeedGenerator extends SeedGenerator {
	/**
	 * Reader that will be used to read the list of seeds.
	 */
	BufferedReader reader = null;
	
	/**
	 * Delimiters to be used when tokenizing the lines from the stream
	 */
	String delimiters = " \t\r\n";

	/**
	 * Constructs a new seed generator backed by the given stream.
	 * 
	 * @param  stream  the stream from which we will read the seeds. The stream
	 *                 is assumed to have a default encoding.
	 */
	public StreamBasedSeedGenerator(Graph graph, InputStream stream) {
		this(graph, new BufferedReader(new InputStreamReader(stream)));
	}
	
	/**
	 * Constructs a new seed generator backed by the given reader.
	 * 
	 * @param  reader  the reader that will be used to read the list of seeds.
	 */
	public StreamBasedSeedGenerator(Graph graph, BufferedReader reader) {
		super(graph);
		this.reader = reader;
	}
	
	/**
	 * Gets the delimiters used when splitting a line into node names
	 */
	public String getDelimiters() {
		return this.delimiters;
	}
	
	/**
	 * Internal iterator class that will be used when calling iterator()
	 */
	private class IteratorImpl extends SeedIterator {
		/** A mutable node set that contains no nodes */
		MutableNodeSet emptyNodeSet;

		/** The current nodeset that will be returned with the next call to next() */
		MutableNodeSet currentNodeSet = null;
		
		/** A map mapping node names to indices */
		StringIntHashMap namesToIndices = new StringIntHashMap();
		
		public IteratorImpl() {
			/* Populate the mapping from node names to node indices */
			int n = graph.getNodeCount();
			for (int i = 0; i < n; i++) {
				namesToIndices.add(graph.getNodeName(i), i);
			}

			/* Construct the empty node set */
			emptyNodeSet = new MutableNodeSet(graph);

			/* Process the first line */
			processLine();
		}

		private void processLine() {
			do {
				String line;
				currentNodeSet = emptyNodeSet.clone();

				try {
					line = reader.readLine();
				} catch (IOException ex) {
					ex.printStackTrace();
					currentNodeSet = null;
					return;
				}
				if (line == null) {
					currentNodeSet = null;
					return;
				}

				StringTokenizer st = new StringTokenizer(line, delimiters);
				
				/* Process current line */
				while (st.hasMoreTokens()) {
					String name = st.nextToken();
					int idx = namesToIndices.get(name);
					if (idx >= 0) {
						currentNodeSet.add(idx);
					}
					// TODO: error reporting here
				}
			} while (currentNodeSet.size() == 0);
		}
		
		public boolean hasNext() {
			return (currentNodeSet != null);
		}
		
		public Seed next() {
			Seed result = new Seed(currentNodeSet);
			processLine();
			return result;
		}
	}
	
	/**
	 * Returns an iterator that iterates over the seeds.
	 * 
	 * This method must be called only once as it is not possible to rewind
	 * the reader once the seeds have been generated.
	 */
	public SeedIterator iterator() {
		return new IteratorImpl();
	}
	
	/**
	 * Sets the delimiters used when splitting a line into node names
	 * 
	 * @param  delimiters  a string containing characters to be used as delimiters
	 */
	public void setDelimiters(String delimiters) {
		this.delimiters = delimiters;
	}
	
	/**
	 * Returns -1 as we cannot know in advance how many seeds there will be.
	 */
	public int size() {
		return -1;
	}
}
