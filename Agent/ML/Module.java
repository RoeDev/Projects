package Agent.ML;

import Agent.Matrix;
import Agent.ML.Exceptions.InvalidMatrixException;
/**
 * <h1>Module</h1>
 * <p>Module is the base class for all layers in the Neural network
 * It allows having the similar behaviors  across the Neural network's functions</p>
 * @author roeec
 *
 */

public abstract class Module implements Cloneable, java.io.Serializable{
	protected int moduleID;
	protected boolean enablePrint;
	
	// Forward is a function that receives a Matrix and runs a calculation on it and returns the calculated Matrix
	public abstract Matrix forward(Matrix inFeatures) throws InvalidMatrixException, CloneNotSupportedException;
	
	// Backward is a function that receives a Matrix and Calculates the derivative of the Calculation calculated in the forward() function
	// it returns the derived calculation Matrix
	public abstract Matrix backward(Matrix gradient) throws InvalidMatrixException, CloneNotSupportedException;
	// Resets the Module's stored Params to their default
	public void reset() {
	}
	// Resets all gradients that are stored in a Module
	public void zeroGrad() {
	}
	
	// This is a unit test function for the forward() function
	public Matrix unitTestForward(Matrix input, Matrix desiredOutput) throws InvalidMatrixException, CloneNotSupportedException {
		Matrix forwardAnswer = forward(input);
		forwardAnswer.printSideBySide(desiredOutput);
		return forwardAnswer;
	}
	// This is a unit test function for the backward() function
	public Matrix unitTestBackward(Matrix input, Matrix desiredOutput) throws InvalidMatrixException, CloneNotSupportedException {
		Matrix backwardAnswer = backward(input);
		backwardAnswer.printSideBySide(desiredOutput);
		return backwardAnswer;
	}
	
	// Sets the print mode
	// True = print
	// False = don't print
	public void setPrintMode(boolean enablePrint) {
		this.enablePrint = enablePrint;
	}
	
	// Returns the Module's params, Some modules need to return params that need to be sent to the optimizer
	public Matrix[][] getParams() {
		return null;
	}
	
	// Sets a Module's id 
	public void setModuleID(int moduleID) {
		this.moduleID = moduleID;
	}
	
	// Clones a module
	@Override
	public Module clone() throws CloneNotSupportedException {
		Module clone = (Module) super.clone();
		clone.zeroGrad();
		return clone;
	}
	
	// Some modules store gradients, those modules override this function
	// for more information look at Linear for an example
	public void clamp(double min, double max) {
	}
}
