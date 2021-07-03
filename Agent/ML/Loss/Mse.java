package Agent.ML.Loss;

import Agent.Matrix;
import Agent.ML.Exceptions.InvalidMatrixException;
/**
 * <h1>MSE</h1>
 * <p>MSE (Mean Squared Error) is a loss function that increases exponentially the loss for a difference bigger than 1 and decreases exponentially
 * the loss for a difference smaller than 1</p>
 * @author roeec
 *
 */
public class Mse extends Loss{
	private Matrix loss1;
	public Mse(int moduleID) {
		this.moduleID = moduleID;
	}
	
	/**
	 * Forward function for MSE, calculates:
	 * mean((outFeaturesMatrix - desiredOutputMatrix) ^ 2)
	 */
	@Override
	public Matrix forward(Matrix inFeatures) throws InvalidMatrixException, CloneNotSupportedException {
		if(desiredOutputMatrix == null)
			throw new InvalidMatrixException(moduleID, "InFeatures Is NULL!");
		if(enablePrint)
			System.out.println("Desired Output: " + desiredOutputMatrix);
		
		//loss1 = outFeaturesMatrix - desiredOutputMatrix
		loss1 = inFeatures.subtractMatrix(desiredOutputMatrix);
		//loss2 = (outFeaturesMatrix - desiredOutputMatrix)^2
		Matrix loss2  = loss1.powerOfTwoMatrix();

		lossMean = loss2.avgMatrix();
		
		//for preventing use of old desiredOutputMatrix
		desiredOutputMatrix = null;
		return lossMean;
	}

	/**
	 * Backward function for MSE, calculates:
	 * 2 * (outFeaturesMatrix - desiredOutputMatrix) / numOfElements
	 */
	@Override
	public Matrix backward(Matrix gradient) throws InvalidMatrixException, CloneNotSupportedException {
		if(loss1 == null)
			throw new InvalidMatrixException(moduleID, "loss1 Is NULL!");
		//set's all the cells to 1/batchSize
		
		Matrix gradLossMean = new Matrix(loss1.getColumns(), loss1.getRows(), -1);
		
		//derivative of mean
		gradLossMean.setCells(1.0/loss1.getNumElements());
		
		// derivative of power of 2
		// (x^2)' = 2x
		gradLossMean.multiplyCells(2);
		gradLossMean = loss1.elementWiseMultiplication(gradLossMean, moduleID);
		//d(loss1) is 1 * therefore we don't multiply it
		
		loss1 = null;
		return gradLossMean;
	}
	/**
	 * Resets gradients
	 */
	@Override
	public void reset() {
		loss1 = lossMean = desiredOutputMatrix = null;
	}
}
