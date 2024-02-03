package coProject;

//This exception is thrown when a method or operation does not return a value.
public class NoValue extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String toString() {
		return "/*can not compare unavailable value*/";
	}

	public String tosString() {
		return "/* can not operate while value is unavailable*/";
	}
}
