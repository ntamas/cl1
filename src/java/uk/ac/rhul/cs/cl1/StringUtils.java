package uk.ac.rhul.cs.cl1;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Commonly used operations on strings.
 * 
 * This class tries to be API-compatible with the StringUtils class from
 * Apache Commons, but may contain extensions.
 * 
 * @author Tamas Nepusz <tamas@cs.rhul.ac.uk>
 */
public class StringUtils {
	/**
	 * Checks if the string is empty or null.
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
	/**
	 * Checks if the string is not empty and not null.
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
	/**
	 * Checks if the string contains only whitespace.
	 */
	public static boolean isWhitespace(String str) {
		return str == null || str.trim().length() == 0;
	}
	
	/**
	 * Reads the entire remaining contents of a stream into a string.
	 * 
	 * @param is       the InputStream being read
	 * @param charset  the encoding of the stream
	 */
	public static String readInputStream(InputStream is, String charset) throws IOException {
		final char[] buffer = new char[0x10000];
		StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(is, charset);
		int read;
		do {
			read = in.read(buffer, 0, buffer.length);
			if (read > 0)
				out.append(buffer, 0, read);
		} while (read >= 0);
		return out.toString();
	}
	
	/**
	 * Reads the entire remaining contents of a stream into a string, assuming UTF-8 encoding.
	 * 
	 * @param is       the InputStream being read
	 */
	public static String readInputStream(InputStream is) throws IOException {
		return readInputStream(is, "UTF-8");
	}
}
