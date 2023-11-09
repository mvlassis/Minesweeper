package application;

public class InvalidDescriptionException extends RuntimeException{
	private static final long serialVersionUID = -5503570153001420162L;

	public InvalidDescriptionException(String errorMessage) {
        super(errorMessage);
    }
}
