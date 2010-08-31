package uk.ac.rhul.cs.utils;

/** 
 * Commonly used operations on objects.
 * 
 * This class tries to be API-compatible with the StringUtils class from
 * Apache Commons, but may contain extensions.
 * 
 * @author Tamas Nepusz <tamas@cs.rhul.ac.uk>
 */
public class ObjectUtils {
	/**
	 * Returns a default value if the object passed is <tt>null</tt>.
	 * 
	 * @param  obj           the object, may be <tt>null</tt>
	 * @param  defaultValue  the default value to return, may be <tt>null</tt>
	 * @return <tt>obj</tt> if it is not <tt>null</tt>, <tt>defaultValue</tt>
	 *         otherwise
	 */
	public static <T> T defaultIfNull(T obj, T defaultValue) {
		return (obj == null) ? defaultValue : obj;
	}
	
	/**
	 * Compares two objects for equality, where either one or both objects
	 * may be <tt>null</tt>.
	 * 
	 * @param  foo  an object or null
	 * @param  bar  an object or null
	 * @return true if both objects are null or if <tt>foo.equals(bar)</tt>
	 *              (assuming that foo is not <tt>null</tt>).
	 */
	public static boolean equals(Object foo, Object bar) {
		return (foo == null) ? (bar == null) : foo.equals(bar);
	}
	
	/**
	 * Gets the hash code of an object, returning zero when the object
	 * is <tt>null</tt>.
	 * 
	 * @param  obj  an object or null
	 * @return the hash code of <tt>obj</tt> or zero if <tt>obj</tt> is
	 *         <tt>null</tt>
	 */
	public static int hashCode(Object obj) {
		return (obj == null) ? 0 : obj.hashCode();
	}
	
	/**
	 * Gets the <tt>toString</tt> of an <tt>Object</tt> returning an empty
	 * string ("") if the input is <tt>null</tt>.
	 */
	public static String toString(Object obj) {
		return toString(obj, "");
	}
	
	/**
	 * Gets the <tt>toString</tt> of an <tt>Object</tt> returning a specified
	 * text if the input is <tt>null</tt>.
	 */
	public static String toString(Object obj, String nullStr) {
		return (obj == null) ? nullStr : obj.toString();
	}
}
