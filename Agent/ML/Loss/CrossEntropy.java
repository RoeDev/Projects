package Agent.ML.Loss;

import Agent.Matrix;
import Agent.ML.matrixIterable;
import Agent.ML.Exceptions.InvalidMatrixException;

/**
 * <h1>CrossEntropy</h1>
 * cross entropy is the loss that should be used when using softmax as an activation function
 * @author roeec
 *
 */
public class CrossEntropy extends Loss{
	
	/*
	 * 
	 * NOT SURE IF CORRECT ?
	 * 
	 */
	
	
	private Matrix inFeatures;
	
	public CrossEntropy(int moduleID) {
		this.moduleID = moduleID;
	}
	/**
	 * Cross entropy forward calc
	 * @param predicted - expected val
	 * @param actual - actual network val
	 * @return -1 * log(predicted)on base 10 * actual network val
	 */
	public double crossEntropyForward(double predicted, double actual) {
		return -1 * Math.log(predicted) * actual;
	}
	/**
	 * Cross entropy backward calc
	 * @param val - matrix cell value
	 * @return val - 1
	 */
	public double crossEntropyBackward(double val) {
		return val - 1;
	}
	/**
	 * Cross entropy forward
	 */
	@Override
	public Matrix forward(Matrix inFeatures) throws InvalidMatrixException, CloneNotSupportedException {
		
		if(desiredOutputMatrix == null)
			throw new InvalidMatrixException(moduleID, "Desired matrix is null in Cross Entropy loss");
		this.inFeatures = inFeatures;
		Matrix clone = inFeatures.clone();
		matrixIterable it = clone.iterator();
		matrixIterable desiredIt;
		for(int i = 0; i < inFeatures.getRows(); i++) {
			desiredIt = desiredOutputMatrix.iterator();
			for(int j = 0; j < inFeatures.getColumns() && it.hasNext(); j++)
				it.replace(crossEntropyForward(it.next(), desiredIt.next()));
		}
		
		//for preventing use of old desiredOutputMatrix
		return clone.avgMatrix();
	}
	
	/**
	 * cross entropy backward
	 */
	@Override
	public Matrix backward(Matrix gradient) throws InvalidMatrixException, CloneNotSupportedException {
		if(inFeatures == null)
			throw new InvalidMatrixException(moduleID, "inFeatures Is NULL!");
		
		matrixIterable it = inFeatures.iterator();
		while(it.hasNext())
			it.replace(crossEntropyBackward(it.next()));

		return inFeatures;
	}
	/**
	 * reset inFeatures
	 */
	@Override
	public void reset() {
		inFeatures = null;
	}
	@Override
	public CrossEntropy clone() throws CloneNotSupportedException {
		CrossEntropy clone = (CrossEntropy) super.clone();
		clone.inFeatures = null;
		return clone;
	}

}
