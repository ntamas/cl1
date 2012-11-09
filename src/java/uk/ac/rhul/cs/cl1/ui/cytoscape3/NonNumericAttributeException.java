package uk.ac.rhul.cs.cl1.ui.cytoscape3;

/**
 * Exception thrown when a non-numeric attribute name was supplied where
 * a numeric attribute was expected.
 * 
 * @author ntamas
 */
public class NonNumericAttributeException extends Exception {
	public NonNumericAttributeException(String attributeName) {
		super(attributeName+" attribute must be numeric");
	}
}
