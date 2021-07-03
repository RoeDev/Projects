package Agent.ML.Loss;

import Agent.Matrix;
import Agent.ML.Module;
import Agent.ML.Exceptions.InvalidMatrixException;
/**
 * <h1>Loss</h1>
 * <p>Loss is the base module for all loss modules</p>
 * @author roeec
 *
 */
public abstract class Loss extends Module{
	protected Matrix lossMean;
	protected Matrix desiredOutputMatrix;

	public abstract Matrix forward(Matrix inFeatures) throws InvalidMatrixException, CloneNotSupportedException;
	public abstract Matrix backward(Matrix inFeatures) throws InvalidMatrixException, CloneNotSupportedException;
	
	/**
	 * @return the current loss mean
	 */
	public Matrix getLossMean() {
		return lossMean;
	}
	/**
	 * sets the desired output for calculating loss
	 * @param desiredOutputMatrix - expected output
	 */
	public void setDesiredOutputMatrix(Matrix desiredOutputMatrix) {
		this.desiredOutputMatrix = desiredOutputMatrix;
	}
}
