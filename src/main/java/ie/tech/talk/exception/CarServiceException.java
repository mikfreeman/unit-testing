package ie.tech.talk.exception;

public class CarServiceException extends RuntimeException
{
	private static final long serialVersionUID = -441976064336893149L;

	public CarServiceException(String error)
	{
		super(error);
	}

}
