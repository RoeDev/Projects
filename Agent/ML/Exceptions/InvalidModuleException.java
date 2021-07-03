package Agent.ML.Exceptions;

public class InvalidModuleException extends Exception{

	public InvalidModuleException(int moduleID, String errorMsg) {
		super("Module Error at module " + moduleID + ": " + errorMsg);
	}
	public InvalidModuleException(String moduleID, String errorMsg) {
		super("Module Error at module " + moduleID + ": " + errorMsg);
	}
}
