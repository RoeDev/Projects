package Agent.ML.Layers;

import Agent.Matrix;
import Agent.ML.Module;
import Agent.ML.matrixIterable;
import Agent.ML.Exceptions.InvalidMatrixException;

/**
 * <h1>SoftMax</h1>
 * SoftMax is an activation function.
 * @author roeec
 *
 */
public class SoftMax extends Module{
		
	public SoftMax() {
	}
	
	/**
	 * Softmax forward calculation
	 * @param sum - sum of row
	 * @param val - current cell value
	 * @return new cell value
	 */
	public double softMaxForward(double sum,double val) {
		return Math.pow(Math.E, val) / sum;
	}
	/**
	 * Softmax backward calculation
	 * @param sum - sum of row
	 * @param val - current cell value
	 * @return new cell value
	 */
	public double softMaxBackward(double sum, double val) {
		return (Math.pow(Math.E, val) / sum) * (1 - Math.pow(Math.E, val) / sum);
	}
	
	/**
	 * Softmax forward goes over the matrix, sums each row and then changes each cell in that row accordingly
	 */
	@Override
	public Matrix forward(Matrix inFeatures) throws InvalidMatrixException, CloneNotSupportedException {
		//iterator for InFeatures Matrix
		matrixIterable it = inFeatures.iterator();
		matrixIterable sumRows = inFeatures.iterator();
		
		//going over the rows
		for(int i = 0; i < inFeatures.getRows(); i++) {
			double sum = 0;
			double max = inFeatures.getCell(i,inFeatures.indexOfMaxElementInRow(i));
			for(int j = 0; j < inFeatures.getColumns() && sumRows.hasNext(); j++) {
				sum += Math.pow(Math.E, sumRows.next() - max);
			}
			
			for(int j = 0; j < inFeatures.getColumns() && it.hasNext(); j++)
				it.replace(softMaxForward(sum, it.next() - max));
		}
		
		//for preventing use of old desiredOutputMatrix
		return inFeatures;
	}
	/**
	 * Softmax backward goes over the matrix, sums each row and then changes each cell in that row accordingly
	 */
	@Override
	public Matrix backward(Matrix gradient) throws InvalidMatrixException, CloneNotSupportedException {
		//iterator for InFeatures Matrix
		matrixIterable it = gradient.iterator();
		matrixIterable sumRows = gradient.iterator();
		//going over the rows
		for(int i = 0; i < gradient.getRows(); i++) {
			double sum = 0;
			for(int j = 0; j < gradient.getColumns() && sumRows.hasNext(); j++)
				sum += Math.pow(Math.E, sumRows.next());
			
			for(int j = 0; j < gradient.getColumns() && it.hasNext(); j++)
				it.replace(softMaxBackward(sum, it.next()));
		}
		
		//for preventing use of old desiredOutputMatrix
		return gradient;
	}

	@Override
	public void reset() {
	}
	@Override
	public SoftMax clone() throws CloneNotSupportedException {
		return (SoftMax) super.clone();
	}

}
