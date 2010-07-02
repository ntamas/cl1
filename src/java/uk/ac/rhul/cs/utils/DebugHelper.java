package uk.ac.rhul.cs.utils;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Simple helper class for debugging web applications
 * 
 * @author tamas
 */
public class DebugHelper {
	public static void debug(String message) {
		try {
			FileWriter wr = new FileWriter("/tmp/debug.txt", true);
			wr.write(message+"\n");
			wr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
