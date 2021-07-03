package Agent.ML;

import java.util.Iterator;

/*
 * Iterator interface for implementing custom function to iterator (replace func)
 */
public interface matrixIterable extends Iterator<Double>{
	public void replace(double val);
}
