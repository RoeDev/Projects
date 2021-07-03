package Agent.ML.Optimizer;

import java.util.List;

import Agent.Matrix;
import Agent.ML.Exceptions.InvalidMatrixException;

/**
 * <h1>Optimizer</h1>
 * <p>The optimizer is incharge of updating the weights and biases using their gradients</p>
 * @author roeec
 */
public abstract class Optimizer {
	protected double alpha;
	protected double decrement;
	
	public abstract void step() throws InvalidMatrixException, CloneNotSupportedException;
	protected abstract List<Matrix> unitTeststep(Matrix weightsGrad, Matrix weights, Matrix biasGrad, Matrix bias) throws InvalidMatrixException;	
	
	/**
	 * Setting the stored learning rate to alpha
	 * @param alpha - The new learning rate
	 */
	public void setLearningRate(double alpha) {
		this.alpha = alpha;
	}
	/**
	 * Unit Test any optimizer
	 * Optimizers that need more params override this function
	 * @param weightGrad - module weight gradients
	 * @param weights - module weights
	 * @param biasGrad - module bias gradients
	 * @param bias - module gradients
	 * @param desiredWeightOutput - desired module weights
	 * @param desiredBiasOutput - desired module biases
	 * @throws InvalidMatrixException
	 */
	public void unitTestOptimizer(Matrix weightGrad, Matrix weights, Matrix biasGrad, Matrix bias, Matrix desiredWeightOutput, Matrix desiredBiasOutput) throws InvalidMatrixException {
		List<Matrix> backwardAnswer = unitTeststep(weightGrad, weights, biasGrad, bias);
		System.out.println("opt weights:");
		backwardAnswer.get(0).printSideBySide(desiredWeightOutput);
		System.out.println("opt bias:");
		backwardAnswer.get(1).printSideBySide(desiredBiasOutput);
	}
}
