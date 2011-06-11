package uk.ac.rhul.cs.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

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
	 * Checks if the string contains any character in the given set of characters.
	 */
	public static boolean containsAny(String str, char[] searchChars) {
		if (str == null || searchChars == null)
			return false;
		
		for (char ch: searchChars) {
			if (str.indexOf(ch) != -1)
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the string contains any character in the given set of characters.
	 */
	public static boolean containsAny(String str, String searchChars) {
		return containsAny(str, searchChars.toCharArray());
	}
	
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
		return str != null && str.trim().length() == 0;
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
	
	/**
	 * Joins the elements of the provided Iterator into a single String containing the provided
	 * elements
	 * <p>
	 * No delimiter is added before or after the list. A null separator is the same as an empty
	 * string.
	 * 
	 * @param   it         the iterator of values to join together
	 * @param   separator  separator between the strings, may be null
	 */
	public static String join(Iterator<?> it, String separator) {	
		if (!it.hasNext())
			return "";
		
		Object obj = it.next();
		StringBuilder sb = new StringBuilder();
		
		if (obj == null)
			sb.append("null");
		else
			sb.append(obj);
		
		if (separator == null || separator.isEmpty()) {
			while (it.hasNext()) {
				obj = it.next();
				if (obj == null)
					sb.append("null");
				else
					sb.append(obj);
			}
		} else {
			while (it.hasNext()) {
				obj = it.next();
				sb.append(separator);
				if (obj == null)
					sb.append("null");
				else
					sb.append(obj);
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Joins the elements of the provided Iterator into a single String containing the provided
	 * elements
	 * <p>
	 * No delimiter is added before or after the list.
	 * 
	 * @param   it         the iterator of values to join together
	 * @param   separator  the separator character to use
	 */
	public static String join(Iterator<?> it, char separator) {
		return StringUtils.join(it, separator + "");
	}
	
	/**
	 * Joins the elements of the provided Collection into a single String containing the provided
	 * elements
	 * <p>
	 * No delimiter is added before or after the list.
	 * 
	 * @param   coll       the collection of values to join together
	 * @param   separator  the separator string to use
	 */
	public static String join(Collection<?> coll, String separator) {
		return StringUtils.join(coll.iterator(), separator);
	}
	
	/**
	 * Joins the elements of the provided Collection into a single String containing the provided
	 * elements
	 * <p>
	 * No delimiter is added before or after the list.
	 * 
	 * @param   coll       the collection of values to join together
	 * @param   separator  the separator character to use
	 */
	public static String join(Collection<?> coll, char separator) {
		return StringUtils.join(coll, separator + "");
	}
	
	/**
	 * Joins the elements of the provided array into a single String containing the
	 * elements of the array
	 * <p>
	 * No delimiter is added before or after the list. A null separator is the same as an
	 * empty string.
	 * 
	 * @param  array       the array of values to join together
	 * @param  separator   separator between the strings, may be null
	 */
	public static String join(Object[] array, String separator) {
		return StringUtils.join(Arrays.asList(array).iterator(), separator);
	}
	
	/**
	 * Joins the elements of the provided array into a single String containing the
	 * elements of the array
	 * <p>
	 * No delimiter is added before or after the list.
	 * 
	 * @param  array       the array of values to join together
	 * @param  separator   the separator character to use
	 */
	public static String join(Object[] array, char separator) {
		return StringUtils.join(Arrays.asList(array).iterator(), separator);
	}
	
	/**
	 * Gets a substring from the specified String avoiding exceptions.
	 * <p>
	 * A negative start position can be used to start <tt>n</tt> characters from the end of
	 * the string.
	 * <p>
	 * A <tt>null</tt> string will return <tt>null</tt>. An empty string ("") will return "".
	 */
	public static String substring(String str, int start) {
		if (str == null)
			return null;
		
		if (start < 0) {
			start = str.length() + start;
			if (start < 0)
				return str;
		}
		
		if (start >= str.length())
			return "";
		
		return str.substring(start);
	}
	
	/**
	 * Gets a substring from the specified String avoiding exceptions.
	 * <p>
	 * A negative start position can be used to start <tt>n</tt> characters from the end of
	 * the string.
	 * <p>
	 * A <tt>null</tt> string will return <tt>null</tt>. An empty string ("") will return "".
	 */
	public static String substring(String str, int start, int end) {
		if (str == null)
			return null;
		
		if (start < 0)
			start = Math.max(str.length() + start, 0);
			
		if (start >= str.length())
			return "";
		
		if (end < 0)
			end = str.length() + end;
		
		if (end > str.length())
			end = str.length();
		
		if (end <= start)
			return "";
		
		return str.substring(start, end);
	}

	/**
	 * Returns the extension of the given file, converted to lowercase
	 * 
	 * @param  filename   the name of the file
	 * @return the extension, converted to lowercase
	 */
	public static String getFileExtension(String filename) {
		String extension = "";
		int i = filename.lastIndexOf('.');
		if (i > 0 && i < filename.length() - 1) {
			extension = filename.substring(i+1).toLowerCase();
		}
		return extension;
	}
	
	/**
	 * Returns the extension of the given file, converted to lowercase
	 * 
	 * @param  file          the file itself
	 * @return the extension, converted to lowercase
	 */
	public static String getFileExtension(File file) {
		return getFileExtension(file.getName());
	}
}
