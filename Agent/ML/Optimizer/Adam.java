package Agent.ML.Optimizer;

import java.util.ArrayList;
import java.util.List;

import Agent.Matrix;
import Agent.ML.Module;
import Agent.ML.Exceptions.InvalidMatrixException;
import Agent.ML.Exceptions.InvalidModuleException;

/**
 * <h1>Adam</h1>
 * <p>Adam is an optimizer that optimizes parameters specifically to their layer, that way better optimization is possible</p>
 * @author roeec
 *
 */
public class Adam extends Optimizer{
	//beta1 - 
	//beta2 - 
	//eta   - Learning rate
	private double beta1 = 0.9,beta2 = 0.999, epsilon = 1e-8;
		
	private List<Module> modules;
	private List<Matrix> m_d;
	private List<Matrix> v_d;
	private int iteration;	
	
	/**
	 * Constructor
	 * @param modules - list of modules that need to be optimized
	 * @throws InvalidModuleException
	 */
	public Adam(List<Module> modules) throws InvalidModuleException {
		if(modules == null)
			throw new InvalidModuleException(-999,"Invalid modules into optimizer");
		this.modules = modules;
		this.iteration = 1;
		this.m_d = new ArrayList<Matrix>();
		this.v_d = new ArrayList<Matrix>();
		
		for(Module m : modules) {
			Matrix[][] currModuleParams = m.getParams();
			
			//modules that don't need a step, will return null when getParam() is called
			if(currModuleParams != null) {
				for(int j = 0; j < currModuleParams.length; j++) {
					m_d.add(new Matrix(currModuleParams[j][0].getColumns(), currModuleParams[j][0].getRows(), 0, -1));
					v_d.add(new Matrix(currModuleParams[j][0].getColumns(), currModuleParams[j][0].getRows(), 0, -1));
				}
			}
		}
		System.out.println("test");
	}
	
	/**
	 * Optimizer step, it will update the Parameters using the adam optimizer calculations
	 */
	@Override
	public void step() throws InvalidMatrixException, CloneNotSupportedException {
		int cnt = 0;
		for(Module m : modules) {
			Matrix[][] moduleParams = m.getParams();
			if(moduleParams != null) {
				for(int i = 0; i < moduleParams.length; i++) {
					paramCalc(moduleParams[i][0], moduleParams[i][1], m_d.get(cnt), v_d.get(cnt), iteration);
					cnt++;
				}
			}
		}
		iteration++;
	}
	/**
	 * Adam optimizer calculations per param
	 * @param param - may be a weight, bias or other
	 * @param dParam - param's derivative value
	 * @param m_d
	 * @param v_d 
	 * @param iteration - a stored variable that is needed for adam's calculation
	 * @throws InvalidMatrixException
	 * @throws CloneNotSupportedException
	 */
	private void paramCalc(Matrix param, Matrix dParam, Matrix m_d, Matrix v_d, int iteration) throws InvalidMatrixException, CloneNotSupportedException {
		//Momentum for beta 1
		//Calc(Same calc is done for m_db):
		//m_dw = beta1 * m_dw + (1 - beta1) * dw				
		//		 ^^^^^^^^^^^^	^^^^^^^^^^^^^^^^
		//			 m_dw			  clone
		//Calculation for weights
		Matrix p_d = dParam.clone();
		p_d.multiplyCells(1-beta1);
		
		
		m_d.multiplyCells(beta1);
		
		m_d = m_d.addMatrix(p_d);
		
		
		
		//Momentum for beta2
		//rms beta 2
		//Calc:
		//v_dw = beta2 * v_dw + (1 - beta2) * dw^2				
		//		 ^^^^^^^^^^^^	^^^^^^^^^^^^^^^^
		//			 m_dw			  clone
		//Calculation for weights
		p_d = dParam.powerOfTwoMatrix();
		p_d.multiplyCells(1-beta2);
		
		
		v_d.multiplyCells(beta2);
		v_d = v_d.addMatrix(p_d);
		
	
					
		//Bias correction
//		System.out.println("pow: " + (1/(1-Math.pow(beta1, iteration))));
		m_d.multiplyCells(1/(1-Math.pow(beta1, iteration)));
//		System.out.println("pow2: " + (1/(1-Math.pow(beta2, iteration))));
		v_d.multiplyCells(1/(1-Math.pow(beta2, iteration)));
		
		
		//updating weights and biases
		v_d = v_d.powerOfMatrix(0.5);
		v_d.addCells(epsilon);
		Matrix newD = m_d.elementWiseMultiplication(v_d.powerOfMatrix(-1), -1);
		newD.multiplyCells(alpha);
		param.cpyMatrix(param.subtractMatrix(newD));
	}
	/**
	 * Unit test Adam optimizer
	 */
	@Override
	protected List<Matrix> unitTeststep(Matrix weightsGrad, Matrix weights, Matrix biasGrad, Matrix bias)
			throws InvalidMatrixException {
		System.out.println("This unit has not been tested with unitest yet!");
		return null;
	}
}
