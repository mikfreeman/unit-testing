package ie.tech.talk.exception;

/**
 * Used to demonstrate how to test for exceptions
 * 
 * @author Michael Freeman
 * 
 */
public class DiagnosticFailureException extends RuntimeException
{
	private static final long serialVersionUID = 5541769282912699546L;

	public DiagnosticFailureException(String error)
	{
		super(error);
	}

}
