package Agent.ML.Layers;

import Agent.Matrix;
import Agent.ML.Kaiming;
import Agent.ML.Module;
import Agent.ML.Exceptions.InvalidMatrixException;

/**
 * <h1>Linear</h1>
 * <p>Linear is a layer in the neural network that is connected to all the neurons in the next layer.\n
 * Initialization of weights and biases is done using Kaiming initialization</p>
 * @author roeec
 *
 */
public class Linear extends Module implements java.io.Serializable{
	private int inFeatures;
	private int outFeatures;
	private Matrix bias;
	private Matrix weights;
	private Matrix weightsGrad;
	private Matrix biasGrad;
	private Matrix input;
	/**
	 * Initialization using Kaiming initialization
	 * @param inFeatures
	 * @param outFeatures
	 */
	public Linear(int inFeatures, int outFeatures) {
		//weights matrix needs to be transposed for proper matrix multiplication
		this.inFeatures = inFeatures;
		this.outFeatures = outFeatures;
		
		Kaiming kaimingRange = new Kaiming(inFeatures);
		
		System.out.println("kaiming abs range:" + kaimingRange.getKaimingMax());
		bias = new Matrix(outFeatures, 1,kaimingRange.getKaimingMin(), kaimingRange.getKaimingMax(), moduleID);
		weights = new Matrix(inFeatures, outFeatures,kaimingRange.getKaimingMin(), kaimingRange.getKaimingMax(), moduleID);
	}
	/**
	 * forward for Linear, it stores the input it receives, multiplies it by it's weights, sums the values for each neuron and add it's bias
	 */
	@Override
	public Matrix forward(Matrix input) throws InvalidMatrixException {
		if(input == null)
			throw new InvalidMatrixException(moduleID, "InFeatures is NULL!");
		if(weights == null)
			throw new InvalidMatrixException(moduleID, "Weights is NULL!");
		if(bias == null)
			throw new InvalidMatrixException(moduleID, "Bias is NULL!");
		
		this.input = input;
		
		return input.MultiMatrix(weights.transpose(), moduleID).addMatrixRow(bias);
	}	
	/**
	 * Backward for linear - calculating gradients
	 */
	@Override
	public Matrix backward(Matrix gradient) throws InvalidMatrixException {
		if(gradient == null)
			throw new InvalidMatrixException(moduleID, "Gradient is NULL!");
		if(weights == null)
			throw new InvalidMatrixException(moduleID, "Weights is NULL!");
		if(input == null)
			throw new InvalidMatrixException(moduleID, "Input is NULL!");
		
		// This is the gradient passed to the next module
		Matrix newGrad = gradient.MultiMatrix(weights, moduleID);
		
		// weights gradient calculated:
		// transpose input * gradient
		weightsGrad = input.transpose().MultiMatrix(gradient, moduleID);
		
		//bias's gradient =summing the columns of the gradient received
		biasGrad = gradient.sumCols();
		return newGrad;
	}
	
	/**
	 * returns an array of 4 Matrixes, in the following order:
	 * weights, bias, weightsGrad, biasGrad
	 */
	@Override
	public Matrix[][] getParams() {
		Matrix params[][] = {
				{weights,weightsGrad},
				{bias,biasGrad}
		};
		if(weightsGrad != null)
			params[0][1] = weightsGrad.transpose();
		return params;
	}
	/**
	 * Resets weights and biases
	 */
	@Override
	public void reset() {
		Kaiming kaimingRange = new Kaiming(inFeatures);
		bias = new Matrix(outFeatures, 1,kaimingRange.getKaimingMin(), kaimingRange.getKaimingMax(), moduleID);
		weights = new Matrix(inFeatures, outFeatures,kaimingRange.getKaimingMin(), kaimingRange.getKaimingMax(), moduleID);
		zeroGrad();
	}
	/**
	 * resets gradients
	 */
	@Override
	public void zeroGrad() {
		weightsGrad = biasGrad = input = null;
	}
	/**
	 * Unit test for backward function
	 */
	@Override
	public Matrix unitTestBackward(Matrix input, Matrix desiredOutput) throws InvalidMatrixException, CloneNotSupportedException {
		Matrix ret = super.unitTestBackward(input, desiredOutput);
		System.out.println("===================");
		System.out.println("grad weights: " + weightsGrad);
		System.out.println();
		System.out.println("bias grad: " + biasGrad);
		System.out.println("===================");
		return ret;
	}
	/**
	 * Cloning Module
	 */
	@Override
	public Linear clone() throws CloneNotSupportedException {
		Linear clone = (Linear) super.clone();
		clone.setBias(bias.clone());
		clone.setWeights(weights.clone());
		clone.zeroGrad();
		return clone;
	}
	/**
	 * Clamping gradients between min and max
	 */
	@Override
	public void clamp(double min, double max) {
		weightsGrad.clamp(min, max);
		biasGrad.clamp(min, max);
	}
	public Matrix getBias() {
		return bias;
	}

	public void setBias(Matrix bias) {
		this.bias = bias;
	}

	public Matrix getWeights() {
		return weights;
	}

	public void setWeights(Matrix weights) {
		this.weights = weights;
	}

	public Matrix getweightsGrad() {
		return weightsGrad;
	}

	public void setweightsGrad(Matrix weightsGrad) {
		this.weightsGrad = weightsGrad;
	}

	public Matrix getBiasGrad() {
		return biasGrad;
	}

	public void setBiasGrad(Matrix biasGrad) {
		this.biasGrad = biasGrad;
	}

	public Matrix getInput() {
		return input;
	}

	public void setInput(Matrix input) {
		this.input = input;
	}

	
	
}
