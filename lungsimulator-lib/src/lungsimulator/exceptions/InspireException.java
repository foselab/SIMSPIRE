package lungsimulator.exceptions;

/**
 * Handles custom exception
 */
public class InspireException extends RuntimeException{
	/**
	 * Custom exception for error display
	 * @param errorMessage error message to display
	 */
	public InspireException(final String errorMessage) {
		super(errorMessage);
	}
}
