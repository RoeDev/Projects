package Agent.ML.Layers;


import Agent.Matrix;
import Agent.ML.Module;
import Agent.ML.Exceptions.InvalidMatrixException;
/**
 * <h1>ActionSelect</h1>
 * <p>action select is a object used for selecting specific items from each row of a matrix and building a new matrix with those values.\n
 * This is used when training the neural network because you need to always use the next move(which is unknown at that time)</p>
 * @author roeec
 *
 */
public class ActionSelect extends Module {
	
	private int columns;
	private Matrix actions;
	public ActionSelect() {
	}
	/**
	 * Set Actions Matrix
	 * @param actions
	 */
	public void setActions(Matrix actions) {
		this.actions = actions;
	}
	/**
	 * Turns a matrix from M x N to M x 1 where each row in the new Matrix is the max value of that row in the inFeatures matrix
	 */
	@Override
	public Matrix forward(Matrix inFeatures) throws InvalidMatrixException, CloneNotSupportedException {
		
		this.columns = inFeatures.getColumns();
		
		Matrix expected_state_action_values = new Matrix(1, inFeatures.getRows(), -999);
		
		// if no actions matrix was set, it will make it's own choosing the highest values as the index for each row in the matrix
		if(actions == null) {
			actions = new Matrix(1, inFeatures.getRows(), -999);
			for(int i = 0; i < inFeatures.getRows(); i++) {
				int index = inFeatures.indexOfMaxElementInRow(i);
				actions.setCell(index, i, 0);
			}
		}
		 
		for(int i = 0; i < inFeatures.getRows(); i++)
			expected_state_action_values.setCell(inFeatures.getCell(i, (int)actions.getCell(i, 0)), i, 0);
		
		return expected_state_action_values;
	}
	
	/**
	 * Turns a matrix from M x 1 to M x N
	 * the items are put in their corresponding index in the new matrix using the actions matrix.
	 * all the other cells are set to 0.
	 */
	@Override
	public Matrix backward(Matrix gradient) throws InvalidMatrixException, CloneNotSupportedException {
		if(actions == null)
			throw new InvalidMatrixException(moduleID, "Actions matrix is null! (actionsSelect layer)");

		Matrix actionSelect = new Matrix(columns, actions.getRows(), -999);
		actionSelect.setCells(0);
		
		for(int i = 0; i < actions.getRows(); i++) {
			actionSelect.setCell(gradient.getCell(i, 0), i, (int) actions.getCell(i, 0));
		}
		
		actions = null;
		
		return actionSelect;
	}
	/**
	 * resets the action matrix
	 */
	@Override
	public void reset() {
		actions = null;
	}
		
}
