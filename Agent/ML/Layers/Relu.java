package Agent.ML.Layers;

import Agent.Matrix;
import Agent.ML.Module;
import Agent.ML.matrixIterable;
import Agent.ML.Exceptions.InvalidMatrixException;

public class Relu extends Module implements java.io.Serializable{
	private Matrix input;
	
	/*
	 * Relu (rectified linear activation function) is an non-linear activation function
	 * 
	 * Activation functions are a model that allows the model to generalize or adapt the data it's given and differentiate between outputs
	 * 
	 * non-linear activation functions are the most commonly used activation functions
	 * 
	 * It's job is to make sure all the values that go through it are bigger or equal to 0
	 * this is done to solve the vanishing/exploding gradient problem
	 */
	
	// Constructor
	public Relu() {
	}
	
	@Override
	public Matrix forward(Matrix inFeatures) throws InvalidMatrixException, CloneNotSupportedException {
		if(inFeatures == null)
			throw new InvalidMatrixException(moduleID, "InFeatures Is NULL!");
		
		input = inFeatures.clone();
		input.setCells(1);
		
		matrixIterable inputIt = input.iterator();
		
		matrixIterable it = inFeatures.iterator();
		while(it.hasNext()) {
			inputIt.next();
			double nextVal = reluForwardPropogation(it.next());
			if(nextVal <= 0 )
				inputIt.replace(0);
			it.replace(nextVal);
		}
		return inFeatures;
	}
	
	private double reluForwardPropogation(double num) {
		if(num > 0)
			return num;
		return 0;
	}
	
	//This is the derivative of Relu
	private double reluBackPropogation(double num) {
		if(num > 0)
			return num;
		return 0;	//should be 1, return num so we don't need to multiply 1 by all the cells in the matrix
	}
	@Override
	public Matrix backward(Matrix gradient) throws InvalidMatrixException, CloneNotSupportedException {
		if(gradient == null)
			throw new InvalidMatrixException(moduleID, "InFeatures Is NULL!");
		
		Matrix clone = gradient.clone().elementWiseMultiplication(input, moduleID);
		
		return clone;
	}
	
	@Override
	public Relu clone() throws CloneNotSupportedException {
		return (Relu) super.clone();
	}
	
}
