package Agent.ML.Exceptions;

public class InvalidMatrixException extends Exception{

	public InvalidMatrixException(int moduleID, String errorMsg) {
		super("Matrix Error at module " + moduleID + ": " + errorMsg);
	}
}
