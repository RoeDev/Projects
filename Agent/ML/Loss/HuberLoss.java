package Agent.ML.Loss;

import Agent.Matrix;
import Agent.ML.matrixIterable;
import Agent.ML.Exceptions.InvalidMatrixException;

/**
 * <h1>Huber Loss</h1>
 * <p>Huber loss is a loss function that calculates 0.5 * MSE loss for |val| < beta else beta * a|val| - 0.5 * beta.
 * This is good because it doesn't exponentially decrease small numbers that way the network does it's final tweaks faster than MSE</p>
 * @author roeec
 *
 */
public class HuberLoss extends Loss{
	private double beta;
	private Matrix loss1;
	/**
	 * Constructor
	 * @param beta
	 */
	public HuberLoss(double beta) {
		this.beta = beta;
	}
	/**
	 * forward for huber loss
	 */
	@Override
	public Matrix forward(Matrix inFeatures) throws InvalidMatrixException, CloneNotSupportedException {
		if(inFeatures == null)
			throw new InvalidMatrixException(moduleID, "InFeatures Is NULL!");
		if(desiredOutputMatrix == null)
			throw new InvalidMatrixException(moduleID, "desiredOutputMatrix Is NULL! (Huber)");
		
		loss1 = inFeatures.subtractMatrix(desiredOutputMatrix);
		
		Matrix loss2 = loss1.clone();
		matrixIterable it = loss2.iterator();
		while(it.hasNext()) {
			it.replace(huberForwardCalc(it.next()));
		}
		lossMean = loss2.avgMatrix();
		
		//for preventing use of old desiredOutputMatrix
		desiredOutputMatrix = null;
		return lossMean;
	}
	/**
	 * Huber loss forward calc
	 * @param val - cell value
	 * @return - new cell value
	 */
	public double huberForwardCalc(double val) {
		if(Math.abs(val) < beta)
			return 0.5 * val * val;
		return beta * (Math.abs(val) - 0.5 * beta);
	}
	/**
	 * Huber loss backward calc
	 * @param val - cell value
	 * @return - new cell value
	 */
	public double huberBackwardCalc(double val) {
		if(Math.abs(val) < beta)
			return val;
		return beta;
	}
	/**
	 * Huber loss backward
	 */
	@Override
	public Matrix backward(Matrix gradient) throws InvalidMatrixException, CloneNotSupportedException {
		if(loss1 == null)
			throw new InvalidMatrixException(moduleID, "InFeatures Is NULL!");
		
		Matrix gradLossMean = new Matrix(loss1.getColumns(), loss1.getRows(), -1);

		//derivative of mean
		gradLossMean.setCells(1.0/loss1.getNumElements());

		//element wise multiplication between our X (loss1) and the derivative of mean
		gradLossMean = loss1.elementWiseMultiplication(gradLossMean, moduleID);
		
		//derivative of huber forward calculation
		matrixIterable it = gradLossMean.iterator();
		while(it.hasNext()) {
			it.replace(huberBackwardCalc(it.next()));
		}
		
		//for preventing use of old desiredOutputMatrix
		desiredOutputMatrix = null;
		return gradLossMean;
	}
	/**
	 * Resets gradients
	 */
	@Override
	public void reset() {
		loss1 = lossMean = desiredOutputMatrix = null;
	}
	@Override
	public HuberLoss clone() throws CloneNotSupportedException {
		HuberLoss clone = (HuberLoss) super.clone();
		clone.reset();
		return clone;
	}

}
