package uk.ac.rhul.cs.cl1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.StringTokenizer;
import java.util.TreeSet;

import com.sosnoski.util.hashmap.StringIntHashMap;

/**
 * Seed generator where seeds will be generated according to the contents of
 * a file.
 * 
 * The file must contain one line for each seed to be used. Lines must contain
 * node names separated by spaces. If a line containing a single asterisk (*) is
 * found in the seed file, this means that after all the predefined seeds have
 * been processed, the remaining nodes will also be considered as singleton
 * seeds.
 * 
 * @author tamas
 *
 */
public class FileBasedSeedGenerator extends SeedGenerator {
	/** The name of the file that will be used */
	private String filename;
	
	/** The number of lines in the file */
	private int size;
	
	/** Whether to generate unused nodes as seeds after we finished processing the file */
	private boolean generateUnusedNodesAsSeeds = false;
	
	/**
	 * Constructs a seed generator backed by the given file
	 */
	public FileBasedSeedGenerator(Graph graph, String filename) throws FileNotFoundException, IOException {
		super(graph);
		
		this.filename = filename;
		
		/* Count the number of seeds */
		File f = new File(this.filename);
		LineNumberReader reader = new LineNumberReader(new FileReader(f));
		String nextLine = null;
		
		while ((nextLine = reader.readLine()) != null) {
			if (nextLine == null)
				break;
			if ("*".equals(nextLine))
				generateUnusedNodesAsSeeds = true;
		}
		
		size = reader.getLineNumber();
		
		reader.close();
	}
	
	/**
	 * Internal iterator class that will be used when calling iterator()
	 */
	private class IteratorImpl extends SeedIterator {
		/** Reader to read the file */
		BufferedReader reader = null;
		
		/** Line that was read the last time */
		String line = null;
		
		/** A map mapping node names to indices */
		StringIntHashMap namesToIndices = new StringIntHashMap();
		
		/** Nodes that have not been used so far */
		TreeSet<Integer> unusedNodes;
		
		/** Constructs the iterator */
		public IteratorImpl(String filename) {
			unusedNodes = new TreeSet<Integer>();
			
			File f = new File(filename);
			
			/* Populate the mapping from node names to node indices */
			int n = graph.getNodeCount();
			for (int i = 0; i < n; i++) {
				namesToIndices.add(graph.getNodeName(i), i);
				unusedNodes.add(i);
			}
			
			try {
				reader = new BufferedReader(new FileReader(f));
				line = reader.readLine();
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		public boolean hasNext() {
			if (line != null)
				return true;
			
			if (generateUnusedNodesAsSeeds && unusedNodes.size() > 0)
				return true;
			
			return false;
		}
		
		public MutableNodeSet next() {
			MutableNodeSet result = new MutableNodeSet(graph);
			
			if (line == null && generateUnusedNodesAsSeeds) {
				/* No more lines in file, return the unused nodes */
				Integer id = unusedNodes.pollFirst();
				result.add(id);
				return result;
			}
			
			StringTokenizer st = new StringTokenizer(line);
			
			/* Process current line */
			while (st.hasMoreTokens()) {
				String name = st.nextToken();
				int idx = namesToIndices.get(name);
				if (idx >= 0) {
					result.add(idx);
					unusedNodes.remove(idx);
				}
				// TODO: error reporting here
			}
			
			/* Read next line */
			try {
				line = reader.readLine();
			} catch (IOException ex) {
				ex.printStackTrace();
				line = null;
			}
			
			return result;
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		public void processFoundCluster(NodeSet cluster) {}
	}
	
	@Override
	public SeedIterator iterator() {
		return new IteratorImpl(filename);
	}

	@Override
	public int size() {
		if (generateUnusedNodesAsSeeds)
			return this.graph.getNodeCount();
		return size;
	}

}
