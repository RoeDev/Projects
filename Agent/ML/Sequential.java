package Agent.ML;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Agent.Matrix;
import Agent.ML.Exceptions.InvalidMatrixException;
import Agent.ML.Exceptions.InvalidModuleException;

/**
 * <h1>Sequential</h1>
 * <p>Container for neural network, it stores modules and allows doing actions on all the modules it stores with ease</p>
 * @author roeec
 *
 */
public class Sequential extends Module implements java.io.Serializable{
	private List<Module> modules;
	
	/**
	 * Constructor
	 */
	public Sequential() {
		createModulesList();
	}
	/**
	 * Creates modules list
	 */
	public void createModulesList() {
		modules = new ArrayList<Module>();
	}
	/**
	 * add a module to container
	 * @param m - module to add
	 * @throws InvalidModuleException
	 */
	public void addModule(Module m) throws InvalidModuleException {
		if(m == null)
			throw new InvalidModuleException(-1, "Module is null!");
		modules.add(m);
		m.setModuleID(modules.size());
	}
	
	/**
	 * Forward propagation through all modules stored and return the last module's output
	 */
	@Override
	public Matrix forward(Matrix inFeatures) throws InvalidMatrixException, CloneNotSupportedException {
		for(int i = 0; i < modules.size(); i++)
			inFeatures = modules.get(i).forward(inFeatures);
		
		if(enablePrint)
			System.out.println("Forward answer: " + inFeatures);
		return inFeatures;
	}
	/**
	 * backward propagation through all modules stored and retturns the first module's output(goes backward through the modules)
	 */
	@Override
	public Matrix backward(Matrix inFeatures) throws InvalidMatrixException, CloneNotSupportedException {
		
		for(int i = modules.size()-1; i >= 0; i--) {
			inFeatures = modules.get(i).backward(inFeatures);
		}
		return inFeatures;
	}
	/**
	 * Get modules that sequential stores
	 * @return
	 */
	public List<Module> getModules() {
		return modules;
	}
	/**
	 * Set print flag on or off
	 * @param enablePrint - true = print, else false
	 */
	public void setPrintFlag(boolean enablePrint) {
		this.enablePrint = enablePrint;
		for(int i = 0; i < modules.size(); i++)
			modules.get(i).setPrintMode(enablePrint);
	}
	/**
	 * calls the reset func on all modules
	 */
	@Override
	public void reset() {
		for(int i = 0; i < modules.size(); i++)
			modules.get(i).reset();
	}
	/**
	 * resets all module's gradients
	 */
	@Override
	public void zeroGrad() {
		for(int i = 0; i < modules.size(); i++)
			modules.get(i).zeroGrad();
	}
	/**
	 * Clone
	 */
	@Override
	public Sequential clone() throws CloneNotSupportedException{
		Sequential clone = (Sequential) super.clone();
		clone.resetModules();
		Iterator it = modules.iterator();
		try {
			while(it.hasNext())
				clone.addModule((Module)(((Module) it.next()).clone()));
		} catch (InvalidModuleException e) {
			e.printStackTrace();
		}
		return clone;
		
	}
	/**
	 * reset list of modules
	 */
	public void resetModules() {
		createModulesList();
	}
	/**
	 * clamp all module gradients
	 */
	@Override
	public void clamp(double min, double max) {
		for(Module m : modules)
			m.clamp(min, max);
	}
	
	
} 
