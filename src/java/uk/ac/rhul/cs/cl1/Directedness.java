package uk.ac.rhul.cs.cl1;

/**
 * Simple enum to describe different directedness modes for some graph operations.
 *
 * - ALL: means that the operation should ignore edge directions
 * - OUT: means that the operation should apply to outgoing edges only
 * - IN: means that the operation should apply to incoming edges only
 * 
 * @author tamas
 */
public enum Directedness {
	ALL, OUT, IN;
}
