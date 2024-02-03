package coProject;

//An exception that is thrown when there is no more memory available.
public class NoMoreMemoryException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toString() {
		return "/*Registers are full,variable will not be declared*/";
	}
}
