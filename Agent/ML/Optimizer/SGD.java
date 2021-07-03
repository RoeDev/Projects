package Agent.ML.Optimizer;

import java.util.ArrayList;
import java.util.List;

import Agent.Matrix;
import Agent.ML.Module;
import Agent.ML.Exceptions.InvalidMatrixException;
import Agent.ML.Exceptions.InvalidModuleException;

/**
 * <h1>SGD</h1>
 * <p>SGD (Stochastic gradient Descent) is an optimizer. It updates the weights and biases of the layers by the following calculation:\n
 * P = P(old) - alpha * P(gradient)\n
 * 
 * P - can represent a weight or bias
 * alpha - Learning Rate</p>
 * 
 * <p>The optimizer's job is to update the weights and biases according the their gradients</p>
 * @author roeec
 *
 */
public class SGD extends Optimizer {
	private List<Module> modules;
	
	/**
	 * Constructor
	 * @param modules - list of modules that need to be updated
	 * @throws InvalidModuleException
	 */
	public SGD(List<Module> modules) throws InvalidModuleException {
		if(modules == null)
			throw new InvalidModuleException(-999,"Invalid modules into optimizer");
		this.modules = modules;
	}
	
	/**
	 * <p>step updates all the modules stored using their gradients.\n
	 * it requests the modules that need to be updated and updates them using their according gradients and the learning rate</p>
	 */
//	@Override
//	public void step() throws InvalidMatrixException {
//		//Calculation per module:
//		// W = W - alpha * gradientOfWeights
//		for(int i = 0; i < modules.size(); i++) {
//			Matrix[][] currModuleParams = modules.get(i).getParams();
//			//modules that don't need a step, will return null when getParam() is called
//			if(currModuleParams != null) {
//				Matrix weights = currModuleParams[0][0];
//				Matrix weightsGrad = currModuleParams[0][1];
//				Matrix bias = currModuleParams[1][0];
//				Matrix biasGrad = currModuleParams[1][1];
//				
//				// W = W - alpha * gradientOfWeights
//				weightsGrad.multiplyCells(alpha);
//				//cpyMatrix is used so we change the content and not the current pointer to the Matrix
//				weights.cpyMatrix(weights.subtractMatrix(weightsGrad));
////				if(weights.getCell(0, 0) != weights.getCell(0, 0)) {
////					System.out.println("NAN");
////					System.out.println(weightsGrad);
////				}
//				// B = B - alpha * gradientOfWeights
//				biasGrad.multiplyCells(alpha);
//				//cpyMatrix is used so we change the content and not the current pointer to the Matrix
//				bias.cpyMatrix(bias.subtractMatrix(biasGrad));
//				
//			}
//		}
//	}
	@Override
	public void step() throws InvalidMatrixException {
		for(Module m : modules) {
			Matrix[][] moduleParams = m.getParams();
			if(moduleParams != null) {
				for(int i = 0; i < moduleParams.length; i++) {
					Matrix mat = moduleParams[i][0];
					Matrix matGrad = moduleParams[i][1];
					matGrad.multiplyCells(alpha);
					mat.cpyMatrix(mat.subtractMatrix(matGrad));
				}
			}
		}
		
	}
	/**
	 * Unit Test the SGD optimizer
	 */
	@Override
	protected List<Matrix> unitTeststep(Matrix weightsGrad, Matrix weights, Matrix biasGrad, Matrix bias) throws InvalidMatrixException {
		
		// W = W - alpha * gradientOfWeights
		weightsGrad.multiplyCells(alpha);
		//cpyMatrix is used so we change the content and not the current pointer to the Matrix
		weights.cpyMatrix(weights.subtractMatrix(weightsGrad));
		
		// B = B - alpha * gradientOfWeights
		biasGrad.multiplyCells(alpha);
		//cpyMatrix is used so we change the content and not the current pointer to the Matrix
		bias.cpyMatrix(bias.subtractMatrix(biasGrad));
		
		List<Matrix> ret = new ArrayList<Matrix>(2);
		ret.add(weights);
		ret.add(bias);
		return ret;
	}
	

}
