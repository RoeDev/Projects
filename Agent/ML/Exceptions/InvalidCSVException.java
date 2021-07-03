package Agent.ML.Exceptions;

public class InvalidCSVException extends Exception{

	public InvalidCSVException(int moduleID, String errorMsg) {
		super("Matrix Error at module " + moduleID + ": " + errorMsg);
	}
	public InvalidCSVException(String moduleID, String errorMsg) {
		super("Matrix Error at module " + moduleID + ": " + errorMsg);
	}
}
