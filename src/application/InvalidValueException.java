package application;

public class InvalidValueException extends RuntimeException {
	private static final long serialVersionUID = -6709530187599554313L;

	public InvalidValueException(String errorMessage) {
        super(errorMessage);
    }
}
