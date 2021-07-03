package Agent.ML.Exceptions;

public class InvalidParamsException extends Exception{

	public InvalidParamsException(String errorMsg) {
		super("Tuner Exception: " + errorMsg);
	}
}
