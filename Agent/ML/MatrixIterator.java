package Agent.ML;


public class MatrixIterator implements matrixIterable{
	private double mat[][];
	private int lastI;
	private int lastJ;
	private int i;
	private int j;
	
	/*
	 * Iterator for Matrix object
	 * will finish a row and move to the next
	 */
	public MatrixIterator(double mat[][]) {
		this.mat = mat;
	}
	
	// Returns false if iterator has passed the last item in the last row, else returns true
	@Override
	public boolean hasNext() {
		return !(i == (mat.length));
	}
	// Returns the next item in the matrix and increases it's count the next item in the matrix or to the last item in the matrix + 1
	@Override
	public Double next() {
		double next = mat[i][j];
		lastI = i;
		lastJ = j;
		j = (j+1) % mat[0].length;
		if(j==0)
			i++;
		return next;
	}
	// Replaces the last variable that was called using the next function
	@Override
	public void replace(double val) {
		mat[lastI][lastJ] = val;
	}	

}
