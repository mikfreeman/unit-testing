package ie.tech.talk.exception;

/**
 * Used to demonstrate how to test for exceptions
 * 
 * @author Michael Freeman
 * 
 */
public class EngineException extends RuntimeException
{
	private static final long serialVersionUID = 2013753844896943939L;

	public EngineException(String error)
	{
		super(error);
	}
}
