package Agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Agent.ML.MatrixIterator;
import Agent.ML.matrixIterable;
import Agent.ML.Exceptions.InvalidMatrixException;
import Agent.ML.Layers.SoftMax;
import defaultSettings.MLSettings;

/**
 * <h1>Matrix</h1>
 * <p> Matrix also known as a Tensor is the most basic entity in machine learning and allows of storing data and doing calculations needed with ease.</p>
 * @author roeec
 *
 */
public class Matrix implements Cloneable,Iterable<Double>,java.io.Serializable{
//	private static Random rand = new Random(MLSettings.seed);
	private static Random rand = new Random();
	private double values[][];
	private int rows;
	private int columns;
	private int moduleID;
	
	//accuracy after dot for round function
	private static int accuracy = 7;
	private static int mul = (int) Math.pow(10, accuracy);
	/*
	 * Will initialize matrix randomly
	 * axis 0 = x
	 * axis 1 = y
	 */
	public Matrix(List<Double> values, int axis) throws InvalidMatrixException {
		if(axis == 0) {
			this.columns = values.size();
			this.rows = 1;
		}
		else if(axis == 1) {
			this.columns = 1;
			this.rows = values.size();
		}
		else
			throw new InvalidMatrixException(moduleID, "Invalid axis while creating matrix from list!");
		this.values = new double[rows][columns];
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < columns; j++)
				this.values[i][j] = round(values.get(i + j));
	}
	/**
	 * Initializes a Matrix object with the double[][] matrix
	 * @param values
	 */
	public Matrix(double values[][]) {
		this.columns = values[0].length;
		this.rows = values.length;
		this.values = values;
		roundValues();
	}
	/**
	 * Initializes a Matrix object with the corresponding sizes and moduleID, all cells with have the value = val
	 * @param columns - columns in Matrix
	 * @param rows - rows in Matrix
	 * @param val - Value all cells in Matrix will have
	 * @param moduleID - Matrix ID
	 */
	public Matrix(int columns, int rows,double val, int moduleID) {
		this.columns = columns;
		this.rows = rows;
		this.moduleID = moduleID;
		
		values = new double[rows][columns];
		
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < columns; j++)
				values[i][j] = val;					//CHANGE
	}
	/**
	 * Initializes a Matrix object with the corresponding sizes and moduleID, all cells are set to a random number between 1 and -1
	 * @param columns - columns in Matrix
	 * @param rows - rows in Matrix
	 * @param moduleID - Matrix ID
	 */
	public Matrix(int columns, int rows,int moduleID) {
		this.columns = columns;
		this.rows = rows;
		this.moduleID = moduleID;
		
		values = new double[rows][columns];
		
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < columns; j++)
				values[i][j] = round(rand.nextDouble() * 2 - 1) / 10;
	}
	/**
	 * Initializes a Matrix object with the corresponding sizes and moduleID, all cells are set to a random number between Min and Max
	 * @param columns - columns in Matrix
	 * @param rows - rows in Matrix
	 * @param Min - min random value
	 * @param Max - max random value
	 * @param moduleID - Matrix ID
	 */
	public Matrix(int columns, int rows, double Min, double Max, int moduleID) {
		this.columns = columns;
		this.rows = rows;
		this.moduleID = moduleID;
		
		values = new double[rows][columns];
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < columns; j++)
				values[i][j] = round((rand.nextDouble() * (Max - Min)) + Min);
	}
	/**
	 * Initializes a Matrix object with the corresponding sizes and moduleID, rows from m that start at startRows index and end at row endRows will be copied
	 * into a matrix
	 * @param m - List<List<Double>> that will be copied from
	 * @param startRows - start rows index
	 * @param endRows - end rows index
	 * @throws InvalidMatrixException
	 */
	public Matrix(List<List<Double>> m, int startRows, int endRows) throws InvalidMatrixException {
		if(endRows - startRows <= 0)
			throw new InvalidMatrixException(moduleID, "Initializing matrix with invalid row amount: " + (endRows - startRows));
		this.columns = m.get(0).size();
		this.rows = endRows - startRows;
		this.values = new double[rows][columns];
		
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < columns; j++)
				values[i][j] = m.get(i).get(j);
	}
	/**
	 * finds the biggest number in a Matrix's row
	 * @param row - row index
	 * @return index of biggest num in row
	 */
	public int indexOfMaxElementInRow(int row) {
		int index = 0;
		double max = values[row][0];
		for(int i = 0; i < columns; i++) {
			if(values[row][i] > max) {
				max = values[row][i];
				index = i;
			}
		}
		return index;
	}
	/**
	 * Copying a Matrix's content without changing it's memory address
	 * @param m - matrix to be copied
	 */
	public void cpyMatrix(Matrix m) {
		values = m.getValues().clone();
		rows = m.getRows();
		columns = m.getColumns();
	}
	/**
	 * Add two Matrix, cell [i][j] in new matrix equals cell [i][j] in this matrix + cell [i][j] in Matrix m
	 * @param m
	 * @return new added matrix 
	 * @throws InvalidMatrixException
	 */
	public Matrix addMatrix(Matrix m) throws InvalidMatrixException {
		if(m == null)
			throw new InvalidMatrixException(moduleID, "Matrix received is NULL");
		if(getRows() != m.getRows() && getColumns() != m.getColumns())
			throw new InvalidMatrixException(moduleID, "Matrix Sizes don't match! (" + rows + ", " + columns + ")\t(" + m.getRows() + ", " + m.getColumns() + ")");
		double[][] ret = new double[getRows()][getColumns()];
		
		for(int i = 0; i < getRows(); i++)
			for(int j = 0; j < getColumns(); j++)
				ret[i][j] = values[i][j] + m.getCell(i, j);
		return new Matrix(ret);
	}
	/**
	 * mean of 2 Matrixs, cell [i][j] in new matrix equals (cell [i][j] in this matrix + cell [i][j] in Matrix m )/ 2
	 * @param m
	 * @return matrix Mean calculated
	 * @throws InvalidMatrixException
	 */
	public Matrix avgMatrixs(Matrix m) throws InvalidMatrixException {
		if(m == null)
			throw new InvalidMatrixException(moduleID, "columns Is NULL!");
		if(m.getMatrix().length != values.length || m.getMatrix()[0].length != values[0].length)
			throw new InvalidMatrixException(moduleID, "Matrix's Sizes Are Invalid! Input:(" + m.getMatrix().length + "," + m.getMatrix()[0].length + "), Weights(" + values.length + "," + values[0].length + ")");
		
		double ret[][] = new double[values.length][values[0].length];
		
		for(int i = 0; i < ret.length; i++)
			for(int j = 0; j < ret[0].length; j++)
				ret[i][j] = (values[i][j] + m.getCell(i, j)) / 2;
		
		return new Matrix(ret);
	}
	
	/**
	 * calculates the mean of all cells in a matrix, sum all cells divided by num of cells
	 * @return mean Matrix calculated
	 */
	public Matrix avgMatrix() {
		double ret[][] = new double[1][1];
		for(int i = 0; i < columns; i++) {
			for(int j = 0; j < rows; j++)
				ret[0][0] += values[j][i];
		}
		ret[0][0] /= getNumElements();
		return new Matrix(ret);
	}
	/**
	 * sums all columns in a matrix
	 * @return returns a 1xcolumns matrix that each cell is the matrix's sum for that column
	 */
	public Matrix sumCols() {
		double ret[][] = new double[1][columns];
		for(int i = 0; i < columns; i++) {
			for(int j = 0; j < rows; j++)
				ret[0][i] += values[j][i];
		}
		return new Matrix(ret);
	}
	/**
	 * the function multiply's every cell by it's self (power of 2)
	 * @return returns the matrix calculated
	 */
	public Matrix powerOfTwoMatrix() {
		double ret[][] = new double[values.length][values[0].length];
		for(int i = 0; i < values.length; i++)
			for(int j = 0; j < values[0].length; j++)
				ret[i][j] = values[i][j] * values[i][j];
		return new Matrix(ret);
	}
	/**
	 * the function multiply's every cell by it's self power Param times (power of power Param)
	 * @return returns the matrix calculated
	 */
	public Matrix powerOfMatrix(double power) {
		double ret[][] = new double[values.length][values[0].length];
		for(int i = 0; i < values.length; i++)
			for(int j = 0; j < values[0].length; j++)
				ret[i][j] = Math.pow(values[i][j], power);
		return new Matrix(ret);
	}
	/**
	 * add a matrix that's 1 x columns long to every row in the current matrix
	 * @param m
	 * @return returns the matrix calculated
	 * @throws InvalidMatrixException
	 */
	public Matrix addMatrixRow(Matrix m) throws InvalidMatrixException {
		if(m == null)
			throw new InvalidMatrixException(moduleID, "Matrix is null!(addMatrix())");
		if(m.getMatrix()[0].length != values[0].length)
			throw new InvalidMatrixException(moduleID, "Invalid sizes while adding Rows!(" + m.getMatrix().length + ", " + m.getMatrix()[0].length +")(" + values.length + ", " + values[0].length + ")");
	
		double ret[][] = new double[values.length][values[0].length];
		//System.out.println("\t\t\tSubtraction: (" + ret.length + "," + ret[0].length +")");
		for(int i = 0; i < ret.length; i++)
			for(int j = 0; j < ret[0].length; j++)
				ret[i][j] = values[i][j] + m.getCell(0, j);
		return new Matrix(ret);
	}
	/**
	 * subtracts matrix this matrix from m
	 * @param m
	 * @return returns the matrix calculated
	 * @throws InvalidMatrixException
	 */
	public Matrix subtractMatrix(Matrix m) throws InvalidMatrixException {
		if(m.getMatrix().length != values.length || m.getMatrix()[0].length != values[0].length)
			throw new InvalidMatrixException(-999, "Invalid sizes while subtracting!(" + m.getMatrix().length + ", " + m.getMatrix()[0].length +")(" + values.length + ", " + values[0].length + ")");
		double ret[][] = new double[values.length][values[0].length];
		//System.out.println("\t\t\tSubtraction: (" + ret.length + "," + ret[0].length +")");
		for(int i = 0; i < ret.length; i++)
			for(int j = 0; j < ret[0].length; j++)
				ret[i][j] = values[i][j] - m.getCell(i, j);
		return new Matrix(ret);
	}
	
	/**
	 * sets all cell's value to val
	 * @param val - value to set cells
	 */
	public void setCells(double val) {
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < columns; j++)
				values[i][j] = round(val);
	}
	
	/**
	 * Matrix multiplication between current matrix and matrix m
	 * @param m - matrix that will muyltiply current one
	 * @param moduleID - new matrix ID
	 * @return new matrix calculated from matrix multiplication between current matrix and matrix m
	 * @throws InvalidMatrixException
	 */
	public Matrix MultiMatrix(Matrix m, int moduleID) throws InvalidMatrixException {
		if(m == null)
			throw new InvalidMatrixException(moduleID, "multiplied matrix null!");
		if(values[0].length != m.getMatrix().length)
			throw new InvalidMatrixException(moduleID, "Matrix sizes are invalid! First Matrix: (" + values.length + "," + values[0].length + "), Second Matrix: (" + m.getRows() + "," + m.getColumns() + ")");
		
		double ret[][] = new double[values.length][m.getColumns()];

		for(int i = 0; i < getRows(); i++) {
			for(int j = 0; j < m.getColumns(); j++) {
				for(int k = 0; k < getColumns(); k++) {
					ret[i][j] += values[i][k] * m.getCell(k, j);
				}
			}
		}
		
		return new Matrix(ret);
	}
	
	/**
	 * Returns a list of indexes where the array arr has a item equal to 1
	 * @param arr - array to find 1s
	 * @return List<Integer> storing the indexs of 1s
	 */
	private List<Integer> findOne(int arr[]) {
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] == 1)
				list.add(i);
		}
		return list;
	}
	/**
	 * element wise multiplication between current matrix and matrix m.
	 * new matrix's cell [i][j] equals current matrix's cell [i][j] * matrix m's cell [i][j]
	 * @param m
	 * @param moduleID
	 * @return
	 * @throws InvalidMatrixException
	 */
	public Matrix elementWiseMultiplication(Matrix m, int moduleID) throws InvalidMatrixException {
		if(m == null)
			throw new InvalidMatrixException(moduleID, "columns Is NULL!");
		int arr[] = new int[4];
		
		arr[0] = getRows();
		arr[1] = getColumns();
		arr[2] = m.getRows();
		arr[3] = m.getColumns();
		List<Integer> ones = findOne(arr);
		if((arr[0] != arr[2] && ones.size() == 0) || (arr[1] != arr[3] && ones.size() == 0))
			throw new InvalidMatrixException(moduleID, "Matrix's Sizes Are Invalid! Input:(" + arr[2] + "," + arr[3] + "), Weights(" + arr[0] + "," + arr[1] + ")");
		
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] == 1)
				arr[i] = 0;
			else
				arr[i] = 1;
		}
		
		
		double ret[][] = new double[getRows()][getColumns()];
		if(arr[0] == 0 && arr[2] == 0) {
			if(arr[1] == 0 && arr[3] == 0) {
				ret[0][0] = round(values[0][0] * m.getMatrix()[0][0]);
			}
			else
				for(int j = 0, l=0; j < getColumns() && l < m.getColumns(); j+=arr[1], l+=arr[3])
					ret[0][j] = round(values[0][j] * m.getMatrix()[0][l]);
		}
		else
			for(int i = 0, k=0; i < getRows() && k < m.getRows(); i+=arr[0], k+= arr[2]) {
				if(arr[1] == 0 && arr[3] == 0) {
					ret[i][0] = round(values[i][0] * m.getMatrix()[k][0]);
				}
				else
					for(int j = 0, l=0; j < getColumns() && l < m.getColumns(); j+=arr[1], l+=arr[3])
						ret[i][j] = round(values[i][j] * m.getMatrix()[k][l]);
			}
		return new Matrix(ret);
	}
	/**
	 * add a value to a specific row in a matrix
	 * @param row - row to add to
	 * @param val - value to add to row 
	 */
	public void addRow(int row, double val) {
		for(int j = 0; j < values[0].length; j++)
			values[row][j] = round(values[row][j] + val);
	}
	/**
	 * multiply row by mulVal
	 * @param row - row to be multiplied
	 * @param mulVal - multiplied value
	 */
	public void multiplyRow(int row, double mulVal) {
			for(int j = 0; j < values[0].length; j++)
				values[row][j] = round(values[row][j] * mulVal);
	}
	/**
	 * Multiply all cells in matrix by mulVal
	 * @param mulVal - value thats multiplied by
	 */
	public void multiplyCells(double mulVal) {
		for(int i = 0; i < values.length; i++)
			for(int j = 0; j < values[0].length; j++)
				values[i][j] = round(values[i][j] * mulVal);
	}
	/**
	 * add a value to all cells
	 * @param mulVal - value that will be added to each cell
	 */
	public void addCells(double mulVal) {
		for(int i = 0; i < values.length; i++)
			for(int j = 0; j < values[0].length; j++)
				values[i][j] = round(values[i][j] + mulVal);
	}
	/*
	 * Transspose matrix, (flip by 90 degrees)
	 * cell [i][j] will be in [j][i] in transposed matrix
	 */
	public Matrix transpose() {
		double ret[][] = new double[values[0].length][values.length];
		for(int i = 0; i < ret.length; i++) {
			for(int j = 0; j < ret[0].length; j++) {
				ret[i][j] = values[j][i];
			}
		}
		return new Matrix(ret);
	}
	
	public double[][] getMatrix() {
		return values;
	}
	
	public void setCell(double val, int i, int j) {
		values[i][j] = val;
	}
	public double getCell(int i, int j) {
		return values[i][j];
	}
	public void setMatrix(double[][] matrix) {
		this.values = matrix;
	}
	@Override
	public String toString() {
		String str = "\n{";
		for(int i = 0; i < values.length; i++) {
			str += "[";
			for(int j = 0; j < values[0].length; j++)
				str += values[i][j] + ((j +1< values[0].length) ? ", " : ""); 
			str += "]" + ((i+1 < values.length) ? "\n" : "}");
		}
		return str;
	}
	@Override
	public Matrix clone() throws CloneNotSupportedException {
		Matrix clone = (Matrix) super.clone();
		double[][] valuesClone = new double[rows][columns];
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < columns; j++)
				valuesClone[i][j] = values[i][j];
		clone.setMatrix(valuesClone);
		return clone;
	}
	@Override
	public matrixIterable iterator() {
		return new MatrixIterator(values);
	}
	/**
	 * Round values
	 */
	public void roundValues() {
		for(int i = 0; i < values.length; i++)
			for(int j = 0; j < values[0].length; j++)
				values[i][j] = round(values[i][j]);
	}
	
	public void size() {
		System.out.println("Size: " + "[" + values.length + "," + values[0].length +"]");
	}
	/**
	 * print 2 matrixs side by side
	 * @param second
	 */
	public void printSideBySide(Matrix second) {
		String str = "{";
		for(int i = 0; i < values.length; i++) {
			str += "[";
			for(int j = 0; j < values[0].length; j++) {
				str += "(";
				str += values[i][j]; 
				str += " --> ";
				str += second.getCell(i, j);
				str += ")" + ((j +1< values[0].length) ? ", " : "");
			}
			str += "]" + ((i+1 < values.length) ? "\n" : "}");
		}
		System.out.println(str);
	}
	/**
	 * clamps all values between min and max
	 * @param min - min value possible in matrix
	 * @param max - max value possible in matrix
	 */
	public void clamp(double min, double max) {
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < columns; j++) {
				if(values[i][j] < min)
					values[i][j] = min;
				else if(values[i][j] > max)
					values[i][j] = max;
			}
	}
	public double round(double val) {
		return val;
//		return ((double) ((long)(val * mul))) / mul;
	}
	public void replace(double val) {
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	public int getColumns() {
		return columns;
	}
	public void setColumns(int columns) {
		this.columns = columns;
	}
	public int getNumElements() {
		return columns * rows;
	}
	public double[][] getValues() {
		return values;
	}
}
