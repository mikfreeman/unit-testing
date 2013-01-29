package ie.tech.talk.exception;

public class TechTalkException extends RuntimeException
{
	private static final long serialVersionUID = -441976064336893149L;

	public TechTalkException(String error)
	{
		super(error);
	}

}
